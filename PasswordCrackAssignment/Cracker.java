import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class Cracker
{
    private final List<String> dictionary;
    private final List<String> hashes;

    public Cracker(List<String> dictionary, List<String> hashes)
    {
        this.dictionary = dictionary;
        this.hashes = new CopyOnWriteArrayList<>(hashes);
    }

    public void crack()
    {
        // Old ugly solution
        /*
        // 1 mangle
        dictionary.parallelStream().forEach(
                x -> Mangle.getAllMangles(x).forEach(this::checkAllVictims)
        );
        // 2 mangles
        dictionary.parallelStream().forEach(
                x -> Mangle.getAllMangles(x).forEach(
                        y -> Mangle.getAllMangles(y).forEach(this::checkAllVictims)
                )
        );
        // 3 mangles
        dictionary.parallelStream().forEach(
                x -> Mangle.getAllMangles(x).forEach(
                        y -> Mangle.getAllMangles(y).forEach(
                                z -> Mangle.getAllMangles(z).forEach(this::checkAllVictims)
                        )
                )
        );
        */
        // New recursive solution
        Mangler m = new Mangler(this);
        int i = 0;
        while (!hashes.isEmpty()) {
            int finalI = i++;
            dictionary.parallelStream().forEach(word -> m.start(word, finalI));
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
        if (hash.equals(jcrypt.crypt(hash.substring(0, 2), word))) {
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
        for (String[] user : PasswordCrack.userList) {
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
