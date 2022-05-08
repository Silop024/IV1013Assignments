import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataParser
{
    public static String parseStringFromFile(String path)
    {
        try {
            return Files.readString(Path.of(path)).trim();
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse file " + path);
        }
    }

    public static byte[] parseBytesFromFile(String path)
    {
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse file " + path);
        }
    }

    public static byte[] parseHexFromFile(String path)
    {
        String s = parseStringFromFile(path);
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static List<byte[]> splitIntoBlocks(byte[] bytes)
    {
        int size = bytes.length;
        List<byte[]> blocks = new ArrayList<>(size / 16);

        for (int i = 0; i < size - 16; i += 16) {
            blocks.add(Arrays.copyOfRange(bytes, i, i + 16));
        }
        return blocks;
    }
}
