import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserAttacker extends Attacker<String>
{
    public UserAttacker(String[] victim)
    {
        super(victim[1]);

        String username = victim[0];
        this.wordList = new ArrayList<>(64);

        String fullName = victim[2];
        String[] split = fullName.split(" ");

        wordList.add(username);
        wordList.add(fullName);
        wordList.add(split[0]);
        wordList.add(split[1]);
        wordList.addAll(Arrays.asList(split));
    }

    public void crack()
    {
        for (String word : wordList) {
            if (isCracked(victim, word)) {
                System.out.println(word);
                return;
            }
        }
        tryMangles(3);
    }

    private void tryMangles(int tries)
    {
        for (int i = 0; i < tries; i++) {
            try {
                wordList = mangleWordList();

                for (String word : wordList) {
                    if (isCracked(victim, word)) {
                        System.out.println(word);
                        return;
                    }
                }
            } catch (OutOfMemoryError e) {
                return;
            }
        }
    }

    private List<String> mangleWordList()
    {
        List<String> mangleList = new ArrayList<>(wordList.size() * 16);

        for (String word : wordList) {
            String[] mangles = Mangle.getAllMangles(word);
            if (mangles != null) {
                mangleList.addAll(Arrays.asList(mangles));
            }
        }
        return mangleList.stream().distinct().collect(Collectors.toList());
    }
}
