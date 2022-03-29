import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import static java.lang.System.exit;

public class StreamCipher
{
    public static final Random rand = new Random();

    public static void main(String[] args) throws IOException
    {
        if (args.length != 3) {
			System.out.println("Invalid input");
            System.out.println("Usage: java StreamCipher <key> <infile> <outfile>");
			exit(1);
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
