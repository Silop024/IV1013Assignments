import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
MyMessageDigest handles the conversion from 256 to 24 bit digests.
 */
public class MyMessageDigest
{
    private final MessageDigest md;

    public MyMessageDigest()
    {
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public byte[] digest(byte[] input)
    {
        byte[] output = new byte[3];
        System.arraycopy(md.digest(input), 0, output, 0, 3);

        return output;
    }

    public byte[] digest(String input)
    {
        return digest(input.getBytes(StandardCharsets.UTF_8));
    }


    public static String toString(byte[] digest)
    {
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }
}
