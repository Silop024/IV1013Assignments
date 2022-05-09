import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Objects;

public class Hidenc
{

    public static void main(String[] args) throws IOException
    {
        Map<String, String> map = Helper.parseArgs(args);

        final SecretKeySpec key = Helper.createSecretKey(map.get("--key"));
        final Cipher cipher = Helper.createCipher(map.get("--ctr"), key, Cipher.ENCRYPT_MODE);
        final byte[] input = Helper.parseBytesFromFile(map.get("--input"));
        final byte[] keyDigest = Helper.digest(key.getEncoded());


        // Get template or size
        String templateString = map.get("--template");
        String sizeString = map.get("--size");
        int size;
        byte[] template;
        if (Objects.equals(templateString, sizeString)) { // Both null
            Helper.exitWithError("Only one of --template and --size can be specified.");
        }
        if (sizeString != null) {
            size = Integer.parseInt(sizeString);
        } else if (templateString != null) {
            template = Helper.parseBytesFromFile(templateString);
            size = template.length;
        } else {
            size = 2048;
        }
        final int finalSize = size;

        // Set offset
        String offsetString = map.get("--offset");
        SecureRandom random = new SecureRandom();
        int randomInt = random
                .ints(100, 0, finalSize / 2)
                .filter(i -> i % 16 == 0)
                .findAny()
                .orElse(0);
        final int offset = offsetString != null ? Integer.parseInt(Helper.parseStringFromFile(offsetString)) : randomInt;

    }
}
