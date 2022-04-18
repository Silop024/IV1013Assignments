public class Collision
{
    public final int iterationsUntilCollision;
    public final String hitMessage;
    public final byte[] hitValue;

    public Collision(int iterations, String hitMessage, byte[] hitValue)
    {
        this.iterationsUntilCollision = iterations;
        this.hitMessage = hitMessage;
        this.hitValue = hitValue;
    }
}
