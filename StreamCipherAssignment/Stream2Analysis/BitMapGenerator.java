import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class BitMapGenerator
{
    public int width;
    public int height;

    public BitMapGenerator(int width, int height)
    {
        this.height = height;
        this.width = width;
    }

    public void createBitMap(final Random rand, final String name)
    {
        BufferedImage bitMap = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int next = rand.nextInt(2);
                if (next == 1) {
                    bitMap.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    bitMap.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }
        File output = new File(name);
        try {
            ImageIO.write(bitMap, "jpg", output);
        } catch (IOException e) {
            throw new RuntimeException(e.getCause());
        }

    }
}
