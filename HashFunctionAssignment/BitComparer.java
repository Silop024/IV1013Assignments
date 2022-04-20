import java.nio.charset.StandardCharsets;

public class BitComparer
{
    private final static String md5Normal = "3fb624533a033a4a7ee1ca6b292dd11e";
    private final static String md5FlippedBit = "eca927d8d53ee84a9452a740c7e68ac7";
    private final static byte[] md5NormalBytes = md5Normal.getBytes(StandardCharsets.UTF_8);
    private final static byte[] md5FlippedBytes = md5FlippedBit.getBytes(StandardCharsets.UTF_8);

    private final static String sha256Normal = "bea5a6860140ba45fff81855204cff76f61445b4b44d1bbd0498d2ebaa8dbab0";
    private final static String sha256FlippedBit = "1f64f489433b18a9d6aab32c85e4a1847efe6c1b7126377af15f260d95e2cc2c";
    private final static byte[] sha256NormalBytes = sha256Normal.getBytes(StandardCharsets.UTF_8);
    private final static byte[] sha256FlippedBytes = sha256FlippedBit.getBytes(StandardCharsets.UTF_8);

    public static void main(String[] args)
    {
        if(args.length != 2 && args.length != 0) {
            System.out.println("Invalid input");
            System.exit(1);
        }
        int bits;
        if(args.length == 2) {
            bits = compareBits(
                    args[0].getBytes(StandardCharsets.UTF_8),
                    args[1].getBytes(StandardCharsets.UTF_8)
            );
        } else {
            bits = compareBits(
                    sha256NormalBytes,
                    sha256FlippedBytes
            );
        }
        if(bits == -1) {
            System.out.println("Invalid input");
            System.exit(1);
        }
        System.out.println("Nr of equal bits = " + bits);
    }

    public static int compareBits(byte[] a1, byte[] a2)
    {
        int length = a1.length;
        if(length != a2.length) {
            return -1;
        }
        byte[] equal = new byte[length];
        for(int i = 0; i < length; i++) {
            equal[i] = (byte) ((a1[i] & a2[i]) & 0xff);
        }
        int nrSameBits = 0;
        for(int i = 0; i < length; i++) {
            for(int j = 0; j < 8; j++) {
                if((equal[i] & (1 << j)) != 0) nrSameBits++;
            }
        }
        return nrSameBits;
    }
}
