import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Hidenc
{
    static SecureRandom prng = new SecureRandom();

    static Function<byte[], byte[]> encrypt;
    static Function<byte[], byte[]> update;

    public static void main(String[] args)
    {
        Map<String, String> map = Helper.parseArgs(args);

        final SecretKeySpec key = Helper.createSecretKey(map.get("--key"));
        final Cipher cipher = Helper.createCipher(map.get("--ctr"), key, Cipher.ENCRYPT_MODE);
        final byte[] input = Helper.parseBytesFromFile(map.get("--input"));
        final byte[] keyDigest = Helper.digest(key.getEncoded());
        final int offset = getOffset(map.get("--offset"), input.length + 48);
        int size;
        byte[] template = null;

        if (map.containsKey("--size")) {
            size = getSize(map.get("--size"));
        } else if (map.containsKey("--template")) {
            template = getTemplate(map.get("--template"));
            size = template.length;
        } else {
            size = 2048;
        }

        update = cipher::update;
        encrypt = block -> {
            try {
                return cipher.doFinal(block);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException("Failed to decrypt block");
            }
        };

        boolean ctr = map.get("--ctr") != null;
        List<byte[]> blob = createBlob(input, keyDigest, ctr);
        byte[] container = createContainer(blob, offset, size, template);
        Helper.writeToFile(container, map.get("--output"));
    }

    private static byte[] createContainer(List<byte[]> blob, int offset, int size, byte[] template)
    {
        List<byte[]> container = new ArrayList<>(128);
        if (template != null) {
            container.addAll(Helper.splitIntoBlocks(template));
        } else {
            byte[] pad = new byte[size];
            prng.nextBytes(pad);
            List<byte[]> padBlocks = Helper.splitIntoBlocks(pad);
            container.addAll(padBlocks);
        }
        offset = offset / 16;
        int j = 0;
        for (int i = offset; i < offset + blob.size(); i++) {
            container.set(i, blob.get(j++));
        }
        ByteBuffer idk = ByteBuffer.allocate(size);
        container.forEach(idk::put);

        return idk.array();
    }

    private static int getOffset(String offsetString, int blobSize)
    {
        int max = 2048 - blobSize;
        int rand = prng.nextInt(max);
        while (rand % 16 != 0) {
            rand = prng.nextInt(max);
        }
        return offsetString != null ? Integer.parseInt(Helper.parseStringFromFile(offsetString)) : rand;
    }

    private static int getSize(String sizeString)
    {
        return Integer.parseInt(sizeString);
    }

    private static byte[] getTemplate(String templateString)
    {
        return Helper.parseBytesFromFile(templateString);
    }

    private static List<byte[]> createBlob(final byte[] data, final byte[] keyDigest, boolean ctr)
    {
        // Create blob of encrypted data with H(k) at the beginning and H(k)/H(data) at the end.
        List<byte[]> blob = new ArrayList<>();
        blob.add(keyDigest);
        blob.addAll(Helper.splitIntoBlocks(data));
        blob.add(keyDigest);
        blob.add(Helper.digest(data));
        return ctr ? createCTR(blob) : createECB(blob);
    }

    private static List<byte[]> createECB(List<byte[]> blob)
    {
        return blob.stream()
                .map(encrypt)
                .collect(Collectors.toList());
    }

    private static List<byte[]> createCTR(List<byte[]> blob)
    {
        return blob.stream()
                .map(update)
                .collect(Collectors.toList());
    }
}
