import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HashMapTest
{
    public static void main(String[] args)
    {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

        try {
            // Get password file info
            Stream<String> passwdFileData = Files.lines(Paths.get(args[1]));
            List<String[]> userList = passwdFileData
                    .map(x -> x.split(":"))
                    .map(x -> new String[]{x[0], x[1], x[4]})
                    .collect(Collectors.toList());
            passwdFileData.close();

            //userList.forEach(x -> map.put(x[1], ""));

            Stream<String> dictFileData = Files.lines(Paths.get(args[0]));
            List<String> dict = dictFileData.collect(Collectors.toList());
            dictFileData.close();

            userList.forEach(user -> map.put(user[1], ""));

            dict.forEach(
                    word -> userList.forEach(
                            user -> {
                                String h1 = jcrypt.crypt(user[1].substring(0, 2), word);
                                if (map.containsKey(h1))
                                    map.put(h1, word);
                                List<String> mangles = Mangle.getAllMangles(word);

                                mangles.forEach(m -> {
                                    String h2 = jcrypt.crypt(user[1].substring(0, 2), m);
                                    if (map.containsKey(h2))
                                        map.put(h2, m);

                                });
                            }
                    )
            );
            map.values().forEach(password -> {
                if (password != null && !password.equals(""))
                    System.out.println(password);
            });
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
