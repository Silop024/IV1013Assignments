import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cracker implements Runnable
{
    protected final List<String> wordList;
    //protected static final List<String[]> userList = Collections.synchronizedList(new ArrayList<>(20));
    public final List<String[]> userList = new ArrayList<>(20);
    //public static final CopyOnWriteArrayList<String[]> userList = new CopyOnWriteArrayList<>();

    public Cracker(List<String> wordList)
    {
        this.wordList = List.copyOf(wordList);
    }

    @Override
    public final void run()
    {
        crack();
    }

    protected void crack()
    {
        wordList.forEach(this::checkAllVictims);

        wordList.forEach(
                x -> {
                    String[] mangles = Mangle.getAllMangles(x);
                    if (mangles != null)
                        Arrays.asList(mangles).forEach(this::checkAllVictims);
                }
        );
    }

    private void checkAllVictims(String word)
    {
        userList.removeIf(x -> hashCompare(word, x[1]));
    }

    private boolean hashCompare(String word, String hash)
    {
        if (hash.equals(jcrypt.crypt(hash.substring(0, 2), word))) {
            System.out.println(word);
            return true;
        }
        return false;
    }
}