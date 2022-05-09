import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

public class Hidenc
{
    static SecureRandom prng;

    public static void main(String[] args)
    {
        Map<String, String> map = Helper.parseArgs(args);

        final SecretKeySpec key = Helper.createSecretKey(map.get("--key"));
        final Cipher cipher = Helper.createCipher(map.get("--ctr"), key, Cipher.ENCRYPT_MODE);
        final byte[] input = Helper.parseBytesFromFile(map.get("--input"));
        final byte[] keyDigest = Helper.digest(key.getEncoded());
        

        boolean ctr = map.get("--ctr") != null;

        byte[] blob = createBlob(input, ctr);
    }

    private static int getOffset(String offsetString, int blobSize)
    {
        int max = 2000 - blobSize;
        int rand = prng.nextInt(max);
        while (rand % 16 != 0) {
            rand = prng.nextInt(max);
        }
        return offsetString != null ? Integer.parseInt(offsetString) : rand;
    }

    private static byte[] createBlob(final byte[] data, boolean ctr)
    {


        return null;
    }

    private static List<byte[]> createECB()
    {
        return null;
    }

    private static List<byte[]> createCTR()
    {
        return null;
    }
}
