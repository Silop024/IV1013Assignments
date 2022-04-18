import java.util.Random;

public class StringGenerator
{
    private final static Random rand = new Random(System.currentTimeMillis());
    private final static int maxNrOfCharacters = 10;

    // https://www.baeldung.com/java-random-string
    public static String generateRandomString()
    {
        int leftLimit = 32; // character ' '
        int rightLimit = 122; // letter 'z'

        return rand.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(rand.nextInt(maxNrOfCharacters))
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
