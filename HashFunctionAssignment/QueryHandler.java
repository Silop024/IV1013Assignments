import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/*
QueryHandler is a class which handles queries which require user input.
 */
public class QueryHandler
{
    private final Scanner input;

    public QueryHandler()
    {
        input = new Scanner(System.in, StandardCharsets.UTF_8);

        System.out.println("Valid options are given in [] after each query");
        System.out.println("If no input or an invalid input is given the default option is chosen");
    }

    public boolean queryBoolean(String message)
    {
        System.out.printf("%s [yes, no] (default = no)%n", message);

        String answer = input.nextLine().toLowerCase();
        return answer.equals("yes");
    }

    public int queryInteger(String message, int defaultVal)
    {
        System.out.printf("%s [decimal number] (default = %d)%n", message, defaultVal);

        try {
            return Integer.parseInt(input.nextLine());
        } catch (Exception e) {
            return defaultVal;
        }
    }

    public String queryString(String message, String defaultVal)
    {
        System.out.printf("%s [message string] (default = %s)%n", message, defaultVal);


        String answer = input.nextLine();
        if (answer.equals(""))
            return defaultVal;

        return answer;
    }
}
