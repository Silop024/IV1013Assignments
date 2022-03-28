import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.lang.System.exit;

public class StreamCipher
{
    private static final MyRandom rand = new MyRandom();

    public static void main(String[] args) throws IOException
    {
        if (args.length != 3) {
            throw new IllegalArgumentException("Usage: java StreamCipher <key> <infile> <outfile>");
        }

        FileInputStream plaintext = new FileInputStream(args[1]);
        FileOutputStream ciphertext = new FileOutputStream(args[2]);

        rand.setSeed(args[0].getBytes(StandardCharsets.UTF_8));

        for (int data = plaintext.read(); data != -1; data = plaintext.read()) {
            ciphertext.write(data ^ rand.next(8));
        }

        plaintext.close();
        ciphertext.close();

        exit(0);
    }

    private static void debugPrint(int stream, int cipher)
    {
        System.out.printf("stream = %02X, cipher = %02X %n", stream, cipher);
    }
}
