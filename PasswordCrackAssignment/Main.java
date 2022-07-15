import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main
{
    public static final List<String[]> userList = new ArrayList<>(20);

    // https://en.wikipedia.org/wiki/List_of_the_most_common_passwords Top 20 according to Nordpass
    // Removed passwords from the wiki list that would be achieved with 1 or 2 mangles of another.
    private static final String[] commonPasswords = {
            "123456",
            "123456789",
            "querty",
            "password",
            "111111",
            "123",
            "qwerty123",
            "000000",
            "1q2w3e",
            "aa12345678",
            "abc123",
            "qwertyuiop",
            "password123"
    };

    public static void main(String[] args)
    {
        if (args.length != 2) {
            exitWithError("Incorrect input error", "Usage: java PasswordCrack <dictionary> <password>");
        }
        userList.addAll(getUserList(args[1]));

        if (userList.isEmpty()) {
            exitWithError("Password list empty, no passwords to crack.", "Exiting...");
        }
        List<String> dictionary = getDictionary(args[0]);
        dictionary = getEnhancedDictionary(dictionary);

        List<String> hashes = parseHashes();

        new PasswordCracker(dictionary, hashes).crack();
    }

    private static List<String> getEnhancedDictionary(List<String> dictionary)
    {
        dictionary.addAll(0, List.of(commonPasswords));
        dictionary.addAll(0, parseNames());
        return dictionary.stream().distinct().collect(Collectors.toList());
    }

    private static List<String[]> getUserList(String path)
    {
        try {
            Stream<String> passwdFileData = Files.lines(Paths.get(path));
            List<String[]> userList = passwdFileData
                    .map(x -> x.split(":"))
                    .map(x -> new String[]{x[0], x[1], x[4]})
                    .collect(Collectors.toList());
            passwdFileData.close();
            return userList;
        } catch (IOException e) {
            exitWithError("Failed to read " + path, "Usage: java PasswordCrack <dictionary> <password>");
        }
        return Collections.emptyList();
    }

    private static List<String> getDictionary(String path)
    {
        try {
            Stream<String> dictFileData = Files.lines(Paths.get(path));
            List<String> dict = dictFileData.collect(Collectors.toList());
            dictFileData.close();
            return dict;
        } catch (IOException e) {
            exitWithError("Failed to read " + path, "Usage: java PasswordCrack <dictionary> <password>");
        }
        return Collections.emptyList();
    }

    private static List<String> parseHashes()
    {
        return Main.userList.stream().map(x -> x[1]).collect(Collectors.toList());
    }

    private static List<String> parseNames()
    {
        List<String> userNames = new ArrayList<>(20 * 4);
        for (String[] strings : Main.userList) {
            List<String> names = Arrays.stream(strings[2].split(" "))
                    .filter(y -> !y.contains("."))
                    .collect(Collectors.toList());

            userNames.addAll(names);
            userNames.add(strings[0]);
        }
        return userNames;
    }

    private static void exitWithError(String errorMsg1, String errorMsg2)
    {
        System.out.println(errorMsg1);
        System.out.println(errorMsg2);
        System.exit(1);
    }
}
