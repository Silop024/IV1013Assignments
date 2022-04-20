public class Main
{
    public static void main(String[] args)
    {
        QueryHandler queryHandler = new QueryHandler();

        Endianness endianness = queryHandler.queryEndianness();
        boolean verbose = queryHandler.queryVerbose();
        int maxIterations = queryHandler.queryMaxIterations();

        CollisionTester tester = new CollisionTester(endianness, maxIterations, verbose);

        String message = queryHandler.queryMessage();
        int runs = queryHandler.queryNumberOfRuns();

        Collision[] results = tester.testForCollision(message, runs);
        if(results.length < 1)
            System.out.println("No collisions");

        int totalNrOfIterations = 0;
        for (Collision result : results) {
            totalNrOfIterations += result.iterationsUntilCollision;
        }
        int averageNrOfIterations = totalNrOfIterations / runs;

        System.out.println("On average, message " + message + " got hit with collision after " + averageNrOfIterations + " iterations.");

        String p = "hi";
        boolean res = tester.testForCollision(results[0].hitMessage + p, message + p);
        System.out.printf("H(%s) = H(%s)? %b",
                results[0].hitMessage + p, message + p, res
        );
    }
}
