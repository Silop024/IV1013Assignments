import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import static java.lang.System.exit;

public class StreamCipher
{
    public static final MyRandom rand = new MyRandom();

    public static void main(String[] args) throws IOException
    {
        if (args.length != 3) {
            throw new IllegalArgumentException("Usage: java StreamCipher <key> <infile> <outfile>");
        }

        FileInputStream plaintext = new FileInputStream(args[1]);
        FileOutputStream ciphertext = new FileOutputStream(args[2]);

        rand.setSeed(Long.parseLong(args[0]));

        for (int data = plaintext.read(); data != -1; data = plaintext.read()) {
            ciphertext.write(data ^ rand.nextInt(256));
        }

        plaintext.close();
        ciphertext.close();

        exit(0);
    }
}
