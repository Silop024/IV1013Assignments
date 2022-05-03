import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PasswordCrack
{
    public static void main(String[] args)
    {
        if (args.length != 2) {
            exitWithError("Incorrect input error", "Usage: java PasswordCrack <dictionary> <password>");
        }
        try {
            // Get password file info
            Stream<String> passwdFileData = Files.lines(Paths.get(args[1]));
            List<String[]> userList = passwdFileData
                    .map(x -> x.split(":"))
                    .map(x -> new String[]{x[0], x[1], x[4]})
                    .collect(Collectors.toList());
            passwdFileData.close();

            if (userList.isEmpty()) {
                exitWithError("Password list empty, no passwords to crack.", "Exiting...");
            }
            //Cracker.userList.addAll(userList);

            // Get dictionary
            Stream<String> dictFileData = Files.lines(Paths.get(args[0]));
            List<String> dict = dictFileData.collect(Collectors.toList());
            dictFileData.close();

            startAttack(dict, userList);

        } catch (IOException e) {
            exitWithError("IO Error", "Usage: java PasswordCrack <dictionary> <password>");
        } catch (InterruptedException e) {
            exitWithError("Thread termination error");
        }
    }

    private static void startAttack(List<String> dict, List<String[]> users) throws InterruptedException
    {
        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        int size = dict.size();
        int subSize = size / threads;
        for (int i = 0; i < threads; i++) {
            int max = Math.min(size, (i + 1) * subSize);
            int min = i * subSize;

            Cracker c = new Cracker(dict.subList(min, max));
            c.userList.addAll(users);
            pool.submit(c);
        }
        pool.shutdown();
        pool.awaitTermination(3, TimeUnit.MINUTES);
    }

    private static void exitWithError(String errorMsg)
    {
        System.out.println(errorMsg);
        System.exit(1);
    }

    private static void exitWithError(String errorMsg1, String errorMsg2)
    {
        System.out.println(errorMsg1);
        exitWithError(errorMsg2);
    }
}
