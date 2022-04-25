import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.concurrent.Callable;

/*
RunnableCollisionTester handles searching for collisions between hash values
and enables it being done using several cores.
 */
public class RunnableCollisionTester implements Callable<Collision>
{
    private final boolean verbose;
    private final String message;
    private final int maxIterations;
    private int id;

    private final SecureRandom rand = new SecureRandom();

    public RunnableCollisionTester(String message, int maxIterations, boolean verbose)
    {
        this.maxIterations = maxIterations;
        this.message = message;
        this.verbose = verbose;
    }

    public RunnableCollisionTester(String message, int maxIterations, boolean verbose, int id)
    {
        this.maxIterations = maxIterations;
        this.message = message;
        this.verbose = verbose;
        this.id = id;
    }

    public Collision testForCollision()
    {
        MyMessageDigest md = new MyMessageDigest();
        byte[] digest = md.digest(message);

        byte[] randomBytes = getRandomBytes();
        byte[] randomHash = md.digest(randomBytes);

        for (int nrOfIterations = 1; nrOfIterations < maxIterations; nrOfIterations++) {
            if (Arrays.equals(randomHash, digest)) {
                if (verbose) {
                    System.out.printf(
                            "ID %d: Collision found: H(%s) = H(%s) after %d iterations%n",
                            id, new String(randomBytes, StandardCharsets.UTF_8), message, nrOfIterations
                    );
                }
                return new Collision(nrOfIterations, new String(randomBytes, StandardCharsets.UTF_8), randomHash);
            }
            randomBytes = getRandomBytes();
            randomHash = md.digest(randomBytes);
        }
        if (verbose)
            System.out.println("ID " + id + ": No collision found");

        return null;
    }

    public byte[] getRandomBytes()
    {
        int length = rand.nextInt(10);
        byte[] bytes = new byte[length];
        rand.nextBytes(bytes);
        return bytes;
    }

    public Collision call()
    {
        return testForCollision();
    }
}
