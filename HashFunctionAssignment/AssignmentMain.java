import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*
AssignmentMain is the main class for the assignment, specifically part 2.4 which checks for weak collision resistance.
 */
public class AssignmentMain
{
    public static void main(String[] args) throws Exception
    {
        QueryHandler queryHandler = new QueryHandler();

        boolean verbose = queryHandler.queryBoolean("Do you want verbose terminal output?");
        int maxIterations = queryHandler.queryInteger(
                "How many hashes will be tried before giving up a brute force attempt?", (1 << 31) - 1
        );
        String message = queryHandler.queryString(
                "What message M do you want do use to generate the hash H(M)?", "IV1013 security"
        );
        int runs = queryHandler.queryInteger(
                "How many times do you want the collision tester to try to break the hash? (rec. < 10)", 1
        );
        startTestPool(message, maxIterations, verbose, runs);
    }

    private static void startTestPool(String message, int maxIterations, boolean verbose, int runs) throws Exception
    {
        int threads = Runtime.getRuntime().availableProcessors();

        if (verbose)
            System.out.printf("Nr of cores available: %d%n", threads);


        ExecutorService pool = Executors.newFixedThreadPool(threads);
        Set<Future<Collision>> set = new HashSet<>();

        for (int i = 0; i < runs; i++) {
            Callable<Collision> callable = new RunnableCollisionTester(message, maxIterations, verbose, i);
            Future<Collision> future = pool.submit(callable);
            set.add(future);

            if (verbose)
                System.out.printf("Job %d queued%n", i);
        }
        long totalNrOfIterations = 0;
        for (Future<Collision> future : set) {
            Collision result = future.get();
            if (result != null)
                totalNrOfIterations += result.iterationsUntilCollision;
        }

        long averageNrOfIterations = totalNrOfIterations / runs;
        System.out.println(
                "On average, message " + message + " got hit with collision after " + averageNrOfIterations + " iterations."
        );
        pool.shutdown();
    }
}
