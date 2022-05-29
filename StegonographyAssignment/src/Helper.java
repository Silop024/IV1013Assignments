import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Helper
{
    public static byte[] readBytesFromFile(String path)
    {
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file " + path);
        }
    }

    public static void writeToFile(byte[] out, String path)
    {
        try {
            Files.write(Path.of(path), out);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to file " + path);
        }
    }

    public static BufferedImage readImageFromFile(String path)
    {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image " + path);
        }
    }
}
