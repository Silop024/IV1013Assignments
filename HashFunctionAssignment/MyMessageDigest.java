import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MyMessageDigest
{
    private final MessageDigest messageDigest;

    public MyMessageDigest(String algorithm)
    {
        try {
            messageDigest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create message digest, no such algorithm");
        }
    }

    public byte[] getBigEndianDigest(String message, int length)
    {
        return getTruncatedDigest(getDigest(message), 0, length);
    }

    public byte[] getLittleEndianDigest(String message, int length)
    {
        byte[] digest = getDigest(message);

        return getTruncatedDigest(digest, digest.length - length, length);
    }

    private byte[] getDigest(String message)
    {
        messageDigest.update(message.getBytes(StandardCharsets.UTF_8));

        return messageDigest.digest();
    }

    private byte[] getTruncatedDigest(byte[] digest, int srcPos, int length)
    {
        byte[] truncatedDigest = new byte[length];

        System.arraycopy(digest, srcPos, truncatedDigest, 0, length);

        return truncatedDigest;
    }

    public byte[] generateDigest(String message, Endianness endianness)
    {
        switch (endianness) {
            case Big:
                return getBigEndianDigest(message, 3);
            case Little:
                return getLittleEndianDigest(message, 3);
            default:
                return null;
        }
    }

    public static String digestToString(byte[] digest)
    {
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }
}
