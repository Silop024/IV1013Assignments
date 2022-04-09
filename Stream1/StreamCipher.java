import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import static java.lang.System.exit;

public class StreamCipher
{
    public static final Random rand = new Random();

    public static void main(String[] args)
    {
        if (args.length != 3) {
            exitWithError("Error: Invalid input. Usage: java StreamCipher <key> <infile> <outfile>");
        }

        try {
            FileInputStream plaintext = new FileInputStream(args[1]);
            FileOutputStream ciphertext = new FileOutputStream(args[2]);

            rand.setSeed(Long.parseLong(args[0]));

            for (int data = plaintext.read(); data != -1; data = plaintext.read()) {
                ciphertext.write(data ^ rand.nextInt(256));
            }
            plaintext.close();
            ciphertext.close();

        } catch(FileNotFoundException e) {
            exitWithError("Error: Invalid file given. Try again and make sure in/outfile are valid");
        } catch(NumberFormatException e) {
            exitWithError("Error: Invalid key. Valid characters in key [0-9]");
        } catch(IOException e) {
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
