import java.util.ArrayList;
import java.util.Arrays;

public class CollisionTester
{
    private final Endianness endianness;
    private final boolean verbose;
    private final int maxIterations;

    public CollisionTester(Endianness endianness, int maxIterations, boolean verbose)
    {
        this.endianness = endianness;
        this.verbose = verbose;
        this.maxIterations = maxIterations;
    }

    public Collision testForCollision(String message)
    {
        MyMessageDigest md = new MyMessageDigest("SHA-256");
        byte[] digest = md.generateDigest(message, endianness);

        String randomMessage = StringGenerator.generateRandomString();
        byte[] randomHash = md.generateDigest(randomMessage, endianness);

        for (int nrOfIterations = 1; nrOfIterations < maxIterations; nrOfIterations++) {
            if (Arrays.equals(randomHash, digest)) {
                if (verbose) {
                    System.out.printf(
                            "Collision found: H(%s) = H(%s) = %s after %d iterations%n",
                            randomMessage, message, MyMessageDigest.digestToString(randomHash), nrOfIterations
                    );
                }
                return new Collision(nrOfIterations, randomMessage, randomHash);
            }
            randomMessage = StringGenerator.generateRandomString();
            randomHash = md.generateDigest(randomMessage, endianness);
        }
        return null;
    }

    public Collision[] testForCollision(String message, int nrOfTests)
    {
        ArrayList<Collision> tests = new ArrayList<>();

        for (int i = 0; i < nrOfTests; i++) {
            Collision test = testForCollision(message);
            if (test != null) {
                tests.add(test);
            }
        }
        return tests.toArray(Collision[]::new);
    }
}

