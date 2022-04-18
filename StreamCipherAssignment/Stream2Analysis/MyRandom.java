import java.util.Random;

public class MyRandom extends Random
{
    /*
    let m = large integer;
    choose two integers a & b and seed X0;

    0 < m, 0 < a < m, 0 <= b < m, 0 <= X0 < m
    sequence: xi == a*Xi-1 + b (mod m);

    x1 == a*X0 + b (mod m)
    x2 == a*X1 + b (mod m)
    .
    .
    .
     */

    private long Xi;

    // Numbers taken from https://en.wikipedia.org/wiki/Linear_congruential_generator
    private long A = 0x5DEECE66DL;
    private long M = 1L << 48;
    private long C = 11;

    public MyRandom()
    {
        this(0L);
    }

    public MyRandom(long seed)
    {
        setSeed(seed);
    }

    public MyRandom(long seed, long modulus, long multiplier, long increment)
    {
        setSeed(seed);
        this.M = modulus;
        this.A = multiplier;
        this.C = increment;
    }

    @Override
    public int next(int bits)
    {
        if (bits <= 0 || bits > 32) {
            StreamCipher.exitWithError("Can only request bits in the range [1-32]");
        }
        Xi = ((A * Xi) + C) & (M - 1);
        int pow = parsePower();
        return (int) (Xi >>> (pow - Math.min(pow, bits)));
    }

    @Override
    public int nextInt(int bound)
    {
        return next(32) % bound;
    }

    @Override
    public void setSeed(long newSeed)
    {
        this.Xi = newSeed;
    }

    private int parsePower()
    {
        int i = 0;
        while ((1L << i) < M) {
            i++;
        }
        return i;
    }
}
