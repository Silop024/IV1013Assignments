/*
Collision is the data class which holds useful information from a collision of hash values.
 */
public class Collision
{
    public final int iterationsUntilCollision;
    public final String hitMessage;
    public final byte[] hitHashValue;

    public Collision(int iterations, String hitMessage, byte[] hitValue)
    {
        this.iterationsUntilCollision = iterations;
        this.hitMessage = hitMessage;
        this.hitHashValue = hitValue;
    }
}
