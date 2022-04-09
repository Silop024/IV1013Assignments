import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.lang.System.exit;

public class StreamCipher
{
    private static final MyRandom rand = new MyRandom();

    public static void main(String[] args)
    {
        if (args.length != 3) {
            exitWithError("Usage: java StreamCipher <key> <infile> <outfile>");
        }

        try {
            FileInputStream plaintext = new FileInputStream(args[1]);
            FileOutputStream ciphertext = new FileOutputStream(args[2]);

            rand.setSeed(args[0].getBytes(StandardCharsets.UTF_8));

            for (int data = plaintext.read(); data != -1; data = plaintext.read()) {
                ciphertext.write(data ^ rand.next(8));
            }
            plaintext.close();
            ciphertext.close();

        } catch (FileNotFoundException e) {
            exitWithError("Error: Invalid file given. Try again and make sure in/outfile are valid");
        } catch (IOException e) {
            exitWithError("Error: IO operation failed due to system error. Try again and make sure in/outfile are valid");
        }
        exit(0);
    }

    private static void exitWithError(String errorMessage)
    {
        System.out.println(errorMessage);
        exit(1);
    }
}
