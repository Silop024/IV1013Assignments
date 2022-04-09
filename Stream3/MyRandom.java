import java.util.Random;

public class MyRandom extends Random
{
    private final byte[] S = new byte[256];

    private int a = 0;
    private int b = 0;

    private void reset(final byte[] key)
    {
        a = 0;
        b = 0;
        scheduleKeys(key);
    }

    private void scheduleKeys(final byte[] key)
    {
        for (int i = 0; i < 256; i++) {
            S[i] = (byte) i;
        }
        int j = 0;
        for (int i = 0; i < 256; i++) {
            j = Math.floorMod(j + S[i] + key[i % key.length], 256); // If key length < message => wraps around and uses key again
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
        a = Math.floorMod((a + 1), 256);
        b = Math.floorMod((b + S[a]), 256);

        swapValues(a, b);

        return S[Math.floorMod(S[a] + S[b], 256)] & ((1 << bits) - 1);
    }

    public void setSeed(byte[] key)
    {
        reset(key);
    }

    private void swapValues(int i, int j)
    {
        /*byte temp = S[i];
        S[i] = S[j];
        S[j] = temp;*/

        S[i] = (byte) (S[i] ^ S[j]);
        S[j] = (byte) (S[i] ^ S[j]);
        S[i] = (byte) (S[i] ^ S[j]);
    }
}
