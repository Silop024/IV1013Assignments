import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Hiddec
{
    static InputHandler input;

    public static void main(String[] args) throws IllegalBlockSizeException, BadPaddingException
    {
        input = new InputHandler(args);

        List<byte[]> blocks = new ArrayList<>();

        byte[] inData = input.getInData();
        int dataLength = inData.length;

        for (int i = 0; i < dataLength / 128 - 128; i++) {
            blocks.add(Arrays.copyOfRange(inData, i * 128, (i + 1) * 128));
        }

        int startIndex = -1;
        int endIndex = -1;

        for (byte[] block : blocks) {
            byte[] decryptData = input.getCipher().doFinal(block);
            byte[] key = input.getKey().getEncoded();

            if (Arrays.equals(decryptData, key)) {
                if (startIndex == -1) {
                    startIndex = (blocks.indexOf(block) + 1) * 128;
                } else {
                    endIndex = (blocks.indexOf(block) - 1) * 128;
                    break;
                }
            }
        }
        byte[] data = input.getCipher().doFinal(Arrays.copyOfRange(inData, startIndex, endIndex));
        writeToFile(data);
    }

    private static void writeToFile(byte[] data)
    {
        try {
            Files.write(Path.of(input.getOutPath()), data);
        } catch (IOException e) {
            exitWithError("Could not write data to file");
        }
    }

    public static void exitWithError(String message)
    {
        System.out.printf("Error: %s.%nTerminated", message);
        System.exit(1);
    }
}
