import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PasswordCrack
{
    public static final List<String[]> userList = new ArrayList<>(20);

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

        dictionary.addAll(0, parseNames());
        List<String> hashes = parseHashes();

        Cracker cracker = new Cracker(dictionary, hashes);
        cracker.crack();
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
        return PasswordCrack.userList.stream().map(x -> x[1]).collect(Collectors.toList());
    }

    private static List<String> parseNames()
    {
        List<String> userNames = new ArrayList<>(20 * 4);
        for (String[] strings : PasswordCrack.userList) {
            List<String> names = Arrays.stream(strings[2].split(" "))
                    .filter(y -> !y.contains("."))
                    .collect(Collectors.toList());

            userNames.addAll(names);
            userNames.add(strings[0]);
        }
        return userNames;
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
