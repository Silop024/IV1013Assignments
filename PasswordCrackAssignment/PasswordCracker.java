import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class PasswordCracker
{
    private final List<String> dictionary;
    private final List<String> hashes;

    public PasswordCracker(List<String> dictionary, List<String> hashes)
    {
        this.dictionary = dictionary;
        this.hashes = new CopyOnWriteArrayList<>(hashes);
    }

    public void crack()
    {
        MangleChecker checker = new MangleChecker(this);

        int i = 0;
        while (!hashes.isEmpty()) {
            int finalI = i++;
            dictionary.parallelStream().forEach(word -> checker.checkMangles(word, finalI));
            removeFoundUserNames();
        }
    }

    public void checkAllVictims(String word)
    {
        CopyOnWriteArrayList<String> found = new CopyOnWriteArrayList<>();
        hashes.parallelStream().forEach(hash -> {
            if (hashCompare(word, hash)) {
                found.add(hash);
            }
        });
        if (!found.isEmpty()) {
            hashes.removeAll(found);
        }
    }

    private boolean hashCompare(String word, String hash)
    {
        if (hash.equals(JCrypt.crypt(hash.substring(0, 2), word))) {
            System.out.println(word);
            return true;
        }
        return false;
    }

    public List<String> getHashes()
    {
        return hashes;
    }

    public void removeFoundUserNames()
    {
        Set<String> dictSet = new HashSet<>(dictionary);
        for (String[] user : Main.userList) {
            List<String> fullName = List.of(user[2].split(" "));
            String account = user[0];
            String hash = user[1];

            if (dictSet.contains(account) && !getHashes().contains(hash)) {
                dictionary.remove(account);
                dictionary.removeAll(fullName);
            }
        }
    }
}
