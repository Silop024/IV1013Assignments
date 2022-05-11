import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Hidenc
{
    private static final SecureRandom prng = new SecureRandom();

    public static void main(String[] args)
    {
        Map<String, String> map = Helper.parseArgs(args);

        if(map.isEmpty()) {
            Helper.exitWithError("No args given");
        }

        final SecretKeySpec key = Helper.createSecretKey(map.get("--key"));
        final Cipher cipher = Helper.createCipher(map.get("--ctr"), key, Cipher.ENCRYPT_MODE);
        final byte[] input = Helper.readBytesFromFile(map.get("--input"));
        final byte[] keyDigest = Helper.getDigest(key.getEncoded());
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

        Helper.initCiphers(cipher);

        boolean ctr = map.get("--ctr") != null;
        byte[] blob = createBlob(input, keyDigest, ctr);
        byte[] container = createContainer(blob, offset, size, template);
        Helper.writeToFile(container, map.get("--output"));
    }

    private static byte[] createContainer(byte[] blob, int offset, int size, byte[] template)
    {
        byte[] container;
        if (template != null) {
            container = template;
        } else {
            container = new byte[size];
            prng.nextBytes(container);
        }
        System.arraycopy(blob, 0, container, offset, blob.length);
        return container;
    }

    private static byte[] createBlob(byte[] data, byte[] keyDigest, boolean ctr)
    {
        // Create blob of encrypted data with H(k) at the beginning and H(k)/H(data) at the end.
        List<byte[]> blob = new ArrayList<>();
        blob.add(keyDigest);
        blob.addAll(Helper.splitIntoBlocks(data));
        blob.add(keyDigest);
        blob.add(Helper.getDigest(data));
        blob = ctr ? createCTR(blob) : createECB(blob);

        ByteBuffer buf = ByteBuffer.allocate(blob.size() * 16);
        blob.forEach(buf::put);
        return buf.array();
    }

    private static List<byte[]> createECB(List<byte[]> blob)
    {
        return blob.stream()
                .map(Helper.doFinal)
                .collect(Collectors.toList());
    }

    private static List<byte[]> createCTR(List<byte[]> blob)
    {
        return blob.stream()
                .map(Helper.update)
                .collect(Collectors.toList());
    }

    private static int getOffset(String offsetString, int blobSize)
    {
        int max = 2048 - blobSize;
        int rand = prng.nextInt(max);
        while (rand % 16 != 0) {
            rand = prng.nextInt(max);
        }
        return offsetString != null ? Integer.parseInt(offsetString) : rand;
    }

    private static int getSize(String sizeString)
    {
        return Integer.parseInt(sizeString);
    }

    private static byte[] getTemplate(String templateString)
    {
        return Helper.readBytesFromFile(templateString);
    }
}
