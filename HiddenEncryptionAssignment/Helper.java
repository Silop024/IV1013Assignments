import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Helper
{
    public static final MessageDigest md5;

    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to initialize message digest");
        }
    }

    public static byte[] digest(byte[] data)
    {
        return md5.digest(data);
    }

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

    public static Map<String, String> parseArgs(String[] in)
    {
        Pattern pattern = Pattern.compile("--(\\w+)=");
        Consumer<String> argCheck = arg -> {
            if (!pattern.matcher(arg).find()) exitWithError("Incorrect input " + arg);
        };

        return Arrays.stream(in)
                .peek(argCheck)
                .map(arg -> arg.split("="))
                .collect(Collectors.toMap(x -> x[0], x -> x[1]));
    }

    public static List<byte[]> splitIntoBlocks(byte[] bytes)
    {
        int size = bytes.length;
        List<byte[]> blocks = new ArrayList<>(size / 16);

        for (int i = 0; i < size; i += 16) {
            blocks.add(Arrays.copyOfRange(bytes, i, i + 16));
        }
        return blocks;
    }

    public static SecretKeySpec createSecretKey(String key)
    {
        byte[] keyBytes = Helper.parseHexFromFile(key);
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static Cipher createCipher(String ctr, SecretKeySpec key, int mode)
    {
        Cipher cipher;
        try {
            if (ctr != null) {
                byte[] ctrBytes = Helper.parseHexFromFile(ctr);
                IvParameterSpec spec = new IvParameterSpec(ctrBytes);

                cipher = Cipher.getInstance("AES/CTR/NoPadding");
                cipher.init(mode, key, spec);
            } else {
                cipher = Cipher.getInstance("AES/ECB/NoPadding");
                cipher.init(mode, key);
            }
            return cipher;
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                 InvalidKeyException e) {
            throw new RuntimeException("Failed to get cipher");
        }
    }

    public static void writeToFile(byte[] out, String path)
    {
        try {
            Files.write(Path.of(path), out);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to file");
        }
    }

    public static void exitWithError(String message)
    {
        System.out.printf("Error: %s.%nTerminated", message);
        System.exit(1);
    }
}
