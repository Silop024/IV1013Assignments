import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class QueryHandler
{
    final Scanner input = new Scanner(System.in, StandardCharsets.UTF_8);

    public QueryHandler()
    {
        System.out.println("Valid options are given in [] after each query");
        System.out.println("If no or invalid input is given the default option is chosen");
    }

    public Endianness queryEndianness()
    {
        System.out.println("Big endian or little endian? [big, little] (default big)");
        String answer = input.nextLine().toLowerCase();

        if (answer.equals("little")) {
            return Endianness.Little;
        }
        return Endianness.Big;
    }

    public boolean queryVerbose()
    {
        System.out.println("Do you want verbose terminal output? [yes, no] (default no)");
        String answer = input.nextLine().toLowerCase();

        return answer.equals("yes");
    }

    public int queryMaxIterations()
    {
        System.out.println("How many hashes will be tried before giving up a brute force attempt? [decimal number] (default 2^32 - 1)");

        try {
            return Integer.parseInt(input.nextLine());
        } catch (Exception e) {
            return (1 << 31) - 1;
        }
    }

    public String queryMessage()
    {
        System.out.println("What message M do you want do use to generate the hash H(M)? [message string] (no default)");
        return input.nextLine();
    }

    public int queryNumberOfRuns()
    {
        System.out.println("How many times do you want the collision tester to try to break the hash? [decimal number] (default 1)");

        try {
            return Integer.parseInt(input.nextLine());
        } catch (Exception e) {
            return 1;
        }
    }
}
