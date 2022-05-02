import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DictionaryAttacker extends Attacker<List<String[]>>
{
    private static final List<String[]> crackedUsers = Collections.synchronizedList(new ArrayList<>());

    public DictionaryAttacker(List<String[]> victim, List<String> wordList)
    {
        super(victim, wordList);
    }

    public void crack()
    {
        for (String word : wordList) {
            checkAllVictims(word);
            String[] mangles = Mangle.getAllMangles(word);

            if (mangles == null) continue;

            for (String mangle : mangles) {
                checkAllVictims(mangle);
            }
        }
    }

    private void checkAllVictims(String word)
    {
        for (String[] user : victim) {
            if (crackedUsers.contains(user)) continue;

            String hash = user[1];

            if (isCracked(hash, word)) {
                crackedUsers.add(user);
                System.out.println(word);
            }
        }
    }

    public static List<String[]> getCrackedUsers()
    {
        return List.copyOf(crackedUsers);
    }
}
