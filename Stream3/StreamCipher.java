import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.lang.System.exit;

public class StreamCipher
{
    public static void main(String[] args)
    {
        if (args.length != 3) {
            exitWithError("Usage: java StreamCipher <key> <infile> <outfile>");
        }

        try {
            FileInputStream infile = new FileInputStream(args[1]);
            FileOutputStream outfile = new FileOutputStream(args[2]);

            // Key debug
            byte[] key = args[0].getBytes(StandardCharsets.UTF_8);
            System.out.println("-----Key data-----");
            debugOutputCharacters(key);
            debugOutputHex(key);

            final MyRandom rand = new MyRandom(key);

            System.out.println("-----Stream data-----");
            for (int data = infile.read(); data != -1; data = infile.read()) {
                int stream = rand.next(8);
                int cipher = (data ^ stream);
                outfile.write(cipher);

                System.out.printf("[K=%02x,C=%02x]", stream, cipher);
            }
            System.out.println();

            infile.close();
            outfile.close();

	        // Terminal output of the contents of the outfile.
            System.out.println("-----Outfile data-----");
            FileInputStream outputReader = new FileInputStream(args[2]);
            byte[] outputBytes = outputReader.readAllBytes();
            outputReader.close();
            debugOutputHex(outputBytes);
            debugOutputCharacters(outputBytes);

        } catch (FileNotFoundException e) {
            exitWithError("Error: Invalid file given. Try again and make sure in/outfile are valid");
        } catch (IOException e) {
            exitWithError("Error: IO operation failed due to system error. Try again and make sure in/outfile are valid");
        }
        System.out.println("-----Exit without error-----");
        exit(0);
    }

    private static void exitWithError(String errorMessage)
    {
        System.out.println(errorMessage);
        exit(1);
    }

    private static void debugOutputCharacters(byte[] output)
    {
        System.out.println("Characters:");
        System.out.println(new String(output));
    }

    private static void debugOutputHex(byte[] output)
    {
        System.out.println("Hexadecimal values:");

        for(byte b : output)
        {
            System.out.printf("%02x ", b);
        }
        System.out.println();
    }
}
