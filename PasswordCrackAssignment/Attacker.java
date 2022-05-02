import java.util.List;

public abstract class Attacker<T> implements Runnable
{
    protected List<String> wordList;
    protected T victim;

    public Attacker(T victim)
    {
        this.victim = victim;
    }

    public Attacker(T victim, List<String> wordList)
    {
        this.victim = victim;
        this.wordList = List.copyOf(wordList);
    }

    public void run()
    {
        crack();
    }

    protected boolean isCracked(String realHash, String pass)
    {
        String salt = realHash.substring(0, 2);
        return realHash.equals(jcrypt.crypt(salt, pass));
    }
    
    protected abstract void crack();
}
