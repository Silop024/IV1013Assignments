import java.awt.image.BufferedImage;

public class Steganographer
{
    int hello;

    public static void main(String[] args)
    {
        if (args.length != 3) {
            throw new RuntimeException("Usage: java Steganographer <data> <input image> <output image>");
        }
        byte[] data = Helper.readBytesFromFile(args[0]);

        BufferedImage inputImage = Helper.readImageFromFile(args[1]);
    }
}

