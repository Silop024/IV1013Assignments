import java.util.Random;

public class MyRandom extends Random
{
    private final byte[] S = new byte[256];

    private static int a = 0;
    private static int b = 0;

    private static final int Ox100 = 256;
    private static final int OxFF = Ox100 - 1;

    private void reset(final byte[] key)
    {
        a = 0;
        b = 0;
        scheduleKeys(key);
    }

    private void scheduleKeys(final byte[] key)
    {
        for (int i = 0; i < Ox100; i++) {
            S[i] = (byte) i;
        }
        int j = 0;
        for (int i = 0; i < Ox100; i++) {
            j = ((j + S[i] + key[i % key.length]) & OxFF); // If key length < message => wraps around and uses key again
            swapValues(i, j);
        }
    }

    public MyRandom()
    {
        byte[] key = new byte[]{1, 2, 3, 4, 5};
        reset(key);
    }

    public MyRandom(final byte[] key)
    {
        reset(key);
    }

    @Override
    public int next(int bits)
    {
        a = (a + 1) & OxFF;
        b = (b + S[a]) & OxFF;

        swapValues(a, b);

        return S[(S[a] + S[b]) & OxFF] & ((1 << bits) - 1);
    }

    @Override
    public void setSeed(long seed) {}

    private void swapValues(int i, int j)
    {
        S[i] = (byte) (S[i] ^ S[j]);
        S[j] = (byte) (S[i] ^ S[j]);
        S[i] = (byte) (S[i] ^ S[j]);
    }
}
