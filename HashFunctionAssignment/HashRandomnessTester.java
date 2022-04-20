import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashRandomnessTester
{
    public static void main(String[] args) throws Exception
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        int nrOfSameBitsTotal = 0;
        for(int i = 0; i < 1000000; i++) {
            String m1 = StringGenerator.generateRandomString();
            String m2 = StringGenerator.generateRandomString();

            byte[] h1 = md.digest(m1.getBytes(StandardCharsets.UTF_8));
            byte[] h2 = md.digest(m2.getBytes(StandardCharsets.UTF_8));
            nrOfSameBitsTotal += BitComparer.compareBits(h1, h2);
        }
        float nrOfSameBitsAverage = nrOfSameBitsTotal / 1000000f;

        System.out.println(nrOfSameBitsAverage);
    }


}

