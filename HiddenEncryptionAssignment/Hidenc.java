import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

public class Hidenc
{
    private static final SecureRandom prng = new SecureRandom();

    public static void main(String[] args)
    {
        Map<String, String> map = Helper.parseArgs(args);

        final SecretKeySpec key = Helper.createSecretKey(map.get("--key"));
        final Cipher cipher = Helper.createCipher(map.get("--ctr"), key, Cipher.ENCRYPT_MODE);
        final byte[] input = Helper.readBytesFromFile(map.get("--input"));
        final byte[] keyDigest = Helper.getDigest(key.getEncoded());

        int size = 2048;
        byte[] template = null;
        if (map.containsKey("--size")) {
            size = getSize(map.get("--size"));
        } else if (map.containsKey("--template")) {
            template = getTemplate(map.get("--template"));
            size = template.length;
        }

        Helper.initCiphers(cipher);
        
        byte[] blob = createBlob(input, keyDigest);

        final int offset = getOffset(map.get("--offset"), size - blob.length);

        if (offset + blob.length > size) Helper.exitWithError("Can not put a blob past the end of the container");

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

    private static byte[] createBlob(byte[] data, byte[] keyDigest)
    {
        // Create blob of encrypted data with H(k) at the beginning and H(k)+H(data) at the end.
        ByteBuffer blobBuf = ByteBuffer.allocate(data.length + 48);
        blobBuf.put(keyDigest);
        blobBuf.put(data);
        blobBuf.put(keyDigest);
        blobBuf.put(Helper.getDigest(data));
        return Helper.doFinal.apply(blobBuf.array());
    }

    /** getOffset will parse the offsetString as an integer if not null
     *  or return a pseudo-random integer.
     * @param offsetString The input string given as argument --offset=
     * @param maxOffset The maximum value of the offset
     * @return The given offset or a pseudo-random number less than maxOffset and divisible by 16.
     */
    private static int getOffset(String offsetString, int maxOffset)
    {
        if(offsetString != null) {
            return Integer.parseInt(offsetString);
        } else {
            return prng.ints().dropWhile(num -> num % 16 != 0 || num > maxOffset).findAny().orElse(0);
        }

        /*int rand = prng.nextInt(maxOffset);
        while (rand % 16 != 0) {
            rand = prng.nextInt(maxOffset);
        }
        return rand;*/
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
