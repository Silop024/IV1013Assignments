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
            Stream<String> passwdStream = Files.lines(Paths.get(args[1]));
            List<String[]> passwdList = passwdStream
                    .map(x -> x.split(":"))
                    .map(x -> new String[]{x[0], x[1], x[4]})
                    .collect(Collectors.toList());
            passwdStream.close();

            if (passwdList.isEmpty()) {
                exitWithError("Password list empty, no passwords to crack.", "Exiting...");
            }

            // Get dictionary
            Stream<String> dictStream = Files.lines(Paths.get(args[0]));
            List<String> dict = dictStream.collect(Collectors.toList());
            dictStream.close();

            addCommons(dict);

            int threads = Runtime.getRuntime().availableProcessors();

            startDictionaryAttack(threads, passwdList, dict);

            // No need to check password of users who have been found
            passwdList.removeAll(DictionaryAttacker.getCrackedUsers());

            startUserAttack(threads, passwdList);


        } catch (IOException e) {
            exitWithError("IO Error", "Usage: java PasswordCrack <dictionary> <password>");
        }
    }

    private static void startDictionaryAttack(int threads, List<String[]> passwdList, List<String> dict)
    {
        ExecutorService dictPool = Executors.newFixedThreadPool(threads);
        int size = dict.size();
        int subSize = size / threads;
        for (int i = 0; i < threads; i++) {
            int max = Math.min(size, (i + 1) * subSize);
            int min = i * subSize;

            DictionaryAttacker attacker = new DictionaryAttacker(passwdList, dict.subList(min, max));
            dictPool.submit(attacker);
        }
        closePool(dictPool);
    }

    private static void startUserAttack(int threads, List<String[]> passwdList)
    {
        ExecutorService userPool = Executors.newFixedThreadPool(threads);
        for (String[] user : passwdList) {
            UserAttacker attacker = new UserAttacker(user);
            userPool.submit(attacker);
        }
        closePool(userPool);
    }

    private static void addCommons(final List<String> dict)
    {
        dict.add("1234");
        dict.add("12345");
        dict.add("123456");
        dict.add("1234567");
        dict.add("12345678");
        dict.add("123456789");
        dict.add("1234567890");
        dict.add("qwerty");
        dict.add("abc123");
        dict.add("111111");
        dict.add("1qaz2wsx");
        dict.add("letmein");
        dict.add("qwertyuiop");
        dict.add("starwars");
        dict.add("login");
        dict.add("passw0rd");
    }

    private static void closePool(final ExecutorService pool)
    {
        try {
            pool.shutdown();
            pool.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            exitWithError("System error. Thread prematurely interrupted");
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
