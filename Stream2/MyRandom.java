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
    private static final long A = 0x5DEECE66DL;
    private static final long M = 0x1000000000000L;
    private static final int B = 11;

    private static final long outputMask = 0x0ffffffffL;

    public MyRandom()
    {
        setSeed(this.nextLong());
    }

    public MyRandom(long seed)
    {
        setSeed(seed);
    }

    @Override
    public int next(int bits)
    {
        if (bits == 0) {
            throw new IllegalArgumentException("Can not request 0 bits");
        }

        Xi = ((A * Xi) + B) % M;
        long temp = (Xi >>> 16) & outputMask; // Get bits 47..16 https://en.wikipedia.org/wiki/Linear_congruential_generator
        return (int) (temp >>> (32 - bits));
    }

    @Override
    public void setSeed(long seed)
    {
        if (seed >= M) {
            throw new IllegalArgumentException("Seed to large");
        }
        this.Xi = seed;
    }
}
