import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Hiddec
{
    public static Cipher cipher;

    public static void main(String[] args) throws Exception
    {
        Pattern pattern = Pattern.compile("--(\\w+)=");
        Consumer<String> argCheck = arg -> {
            if (!pattern.matcher(arg).find()) exitWithError("Incorrect input " + arg);
        };

        Map<String, String> map = Arrays.stream(args)
                .peek(argCheck)
                .map(arg -> arg.split("="))
                .collect(Collectors.toMap(x -> x[0], x -> x[1]));

        final byte[] data = DataParser.parseBytesFromFile(map.get("--input"));
        final SecretKeySpec key = createSecretKey(map.get("--key"));
        final Cipher cipher = createCipher(map.get("--ctr"), key);

        final MessageDigest md5 = MessageDigest.getInstance("MD5");
        final byte[] keyDigest = md5.digest(key.getEncoded());

        Predicate<byte[]> notEqualsKeyHash = block -> !Arrays.equals(block, keyDigest);

        List<byte[]> blocks = DataParser.splitIntoBlocks(data);

        final byte[][] idk = new byte[1][];

        List<byte[]> hiddenBlocks = blocks.stream()
                .peek(b -> idk[0] = b)
                .map(b -> decrypt(b, cipher))
                .dropWhile(notEqualsKeyHash)
                .skip(1)
                .takeWhile(notEqualsKeyHash)
                .collect(Collectors.toList());

        System.out.println(blocks.indexOf(idk[0]));
        Path outPath = Path.of(map.get("--output"));
        if (Files.exists(outPath)) {
            Files.delete(outPath);
        }
        for (byte[] bytes : hiddenBlocks) {
            Files.write(outPath, bytes, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        }

    }

    private static byte[] decrypt(byte[] bytes, Cipher c)
    {
        try {
            return c.doFinal(bytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException("Could not decrypt");
        }
    }

    private static SecretKeySpec createSecretKey(String key)
    {
        byte[] keyBytes = DataParser.parseHexFromFile(key);
        return new SecretKeySpec(keyBytes, "AES");
    }

    private static Cipher createCipher(String ctr, SecretKeySpec key)
    {
        Cipher cipher;
        try {
            if (ctr != null) {
                byte[] ctrBytes = DataParser.parseHexFromFile(ctr);
                IvParameterSpec spec = new IvParameterSpec(ctrBytes);

                cipher = Cipher.getInstance("AES/CTR/NoPadding");
                cipher.init(Cipher.DECRYPT_MODE, key, spec);
            } else {
                cipher = Cipher.getInstance("AES/ECB/NoPadding");
                cipher.init(Cipher.DECRYPT_MODE, key);
            }
            return cipher;
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                 InvalidKeyException e) {
            throw new RuntimeException("Failed to get cipher");
        }
    }

    public static void exitWithError(String message)
    {
        System.out.printf("Error: %s.%nTerminated", message);
        System.exit(1);
    }
}
