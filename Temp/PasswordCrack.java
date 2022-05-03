import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PasswordCrack
{
    public final static List<String> foundHashes = Collections.synchronizedList(new ArrayList<>(20));


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

            userList.forEach(x -> {
                dict.add(0, x[0]);
                dict.addAll(0, List.of(x[2].split(" ")));
            });

            startAttack(dict, userList);

        } catch (IOException e) {
            exitWithError("IO Error", "Usage: java PasswordCrack <dictionary> <password>");
        } catch (InterruptedException e) {
            exitWithError("Thread termination error");
        }
    }

    private static void startAttack(List<String> dict, List<String[]> users) throws InterruptedException
    {
        Cracker cracker = new Cracker(dict, users);
        dict.forEach(cracker::checkAllVictims);
        users = users.stream().filter(x -> !foundHashes.contains(x[1])).collect(Collectors.toList());

        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        int size = dict.size();
        int subSize = size / threads;
        for (int i = 0; i < threads; i++) {
            int max = Math.min(size, (i + 1) * subSize);
            int min = i * subSize;

            pool.submit(new Cracker(dict.subList(min, max), users));
        }
        pool.shutdown();
        if (!pool.awaitTermination(1, TimeUnit.DAYS)) {
            System.out.println("Exited before thread termination");
        }
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
