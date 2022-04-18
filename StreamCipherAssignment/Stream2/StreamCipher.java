import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.lang.System.exit;

public class StreamCipher
{
    public static final MyRandom rand = new MyRandom();

    public static void main(String[] args)
    {
        if (args.length != 3) {
            exitWithError("Error: Invalid input. Usage: java StreamCipher <key> <infile> <outfile>");
        }

        try {
            FileInputStream infile = new FileInputStream(args[1]);
            FileOutputStream outfile = new FileOutputStream(args[2]);

            rand.setSeed(Long.parseLong(args[0]));

            for (int data = infile.read(); data != -1; data = infile.read()) {
                outfile.write(data ^ rand.nextInt(256));
            }
            infile.close();
            outfile.close();

	    // Terminal output of the contents of the outfile.
            byte[] outputBytes = new FileInputStream(args[2]).readAllBytes();
            debugOutputCharacters(outputBytes);
            debugOutputValues(outputBytes);

        } catch (FileNotFoundException e) {
            exitWithError("Error: Invalid file given. Try again and make sure in/outfile are valid");
        } catch (NumberFormatException e) {
            exitWithError("Error: Invalid key. Valid characters in key [0-9] and can not be too large");
        } catch (IOException e) {
            exitWithError("Error: IO operation failed due to system error. Try again and make sure in/outfile are valid");
        }
        exit(0);
    }

    public static void exitWithError(String errorMessage)
    {
        System.out.println(errorMessage);
        exit(1);
    }

    private static void debugOutputCharacters(byte[] output)
    {
        System.out.println(new String(output));
    }

    private static void debugOutputValues(byte[] output)
    {
        for(byte b : output)
        {
            System.out.printf("%02x ", b);
        }
    }
}
