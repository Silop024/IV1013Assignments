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

        int totalNrOfIterations = 0;
        for (Collision result : results) {
            totalNrOfIterations += result.iterationsUntilCollision;
        }
        int averageNrOfIterations = totalNrOfIterations / runs;

        System.out.println("On average, message " + message + " got hit with collision after " + averageNrOfIterations + " iterations.");
    }
}
