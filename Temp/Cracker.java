import java.util.List;

public class Cracker implements Runnable
{
    private final List<String> wordList;
    private final List<String[]> userList;

    public Cracker(List<String> wordList, List<String[]> userList)
    {
        this.wordList = wordList;
        this.userList = userList;
    }

    @Override
    public void run()
    {
        crack();
    }

    private void crack()
    {
        System.out.println("Trying 1 mangle");
        wordList.forEach(
                x -> Mangle.getAllMangles(x).forEach(this::checkAllVictims)
        );
        System.out.println("Trying 2 mangles");
        wordList.forEach(
                x -> Mangle.getAllMangles(x).forEach(
                        y -> Mangle.getAllMangles(y).forEach(this::checkAllVictims)
                )
        );
        System.out.println("Trying 3 mangles");
        wordList.forEach(
                x -> Mangle.getAllMangles(x).forEach(
                        y -> Mangle.getAllMangles(y).forEach(
                                z -> Mangle.getAllMangles(z).forEach(this::checkAllVictims)
                        )
                )
        );
    }

    public void checkAllVictims(String word)
    {
        userList.removeIf(x -> hashCompare(word, x[1]));
    }

    private boolean hashCompare(String word, String hash)
    {
        if (hash.equals(jcrypt.crypt(hash.substring(0, 2), word))) {
            synchronized (PasswordCrack.foundHashes) {
                if (!PasswordCrack.foundHashes.contains(hash)) {
                    System.out.println(word);
                    PasswordCrack.foundHashes.add(hash);
                }
            }
            return true;
        }
        return false;
    }
}