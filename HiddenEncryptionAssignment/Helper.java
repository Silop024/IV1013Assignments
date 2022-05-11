import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
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
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Helper
{
    public static Function<byte[], byte[]> doFinal;
    public static Function<byte[], byte[]> update;

    public static void initCiphers(Cipher cipher) {
        update = cipher::update;
        doFinal = block -> {
            try {
                return cipher.doFinal(block);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                throw new NoStackTraceRuntimeException("Failed to process block " + Arrays.toString(block));
            }
        };
    }

    public static Map<String, String> parseArgs(String[] in)
    {
        Pattern pattern = Pattern.compile("--(\\w+)=");
        Consumer<String> argCheck = arg -> {
            if (!pattern.matcher(arg).find()) throw new NoStackTraceRuntimeException("Incorrect arg " + arg);
        };

        Map<String, String> map =  Arrays.stream(in)
                .peek(argCheck)
                .map(arg -> arg.split("="))
                .collect(Collectors.toMap(x -> x[0], x -> x[1]));

        if(map.isEmpty()) {
            throw new NoStackTraceRuntimeException("No args given");
        }
        return map;
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

    public static byte[] getDigest(byte[] data)
    {
        try {
            return MessageDigest.getInstance("MD5").digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new NoStackTraceRuntimeException("Failed to get digest");
        }
    }

    public static SecretKeySpec createSecretKey(String key)
    {
        byte[] keyBytes = stringToHex(key);
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static void writeToFile(byte[] out, String path)
    {
        try {
            Files.write(Path.of(path), out);
        } catch (IOException e) {
            throw new NoStackTraceRuntimeException("Failed to write to file " + path);
        }
    }

    public static byte[] readBytesFromFile(String path)
    {
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            throw new NoStackTraceRuntimeException("Failed to parse file " + path);
        }
    }

    public static Cipher createCipher(String ctr, SecretKeySpec key, int mode)
    {
        try {
            Cipher cipher;
            if (ctr != null) {
                byte[] ctrBytes = stringToHex(ctr);
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
            throw new NoStackTraceRuntimeException("Failed to get cipher");
        }
    }

    public static byte[] stringToHex(String str) {
        int len = str.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4)
                    + Character.digit(str.charAt(i + 1), 16));
        }
        return data;
    }

    public static void exitWithError(String message)
    {
        throw new NoStackTraceRuntimeException(message);
    }

    public static class NoStackTraceRuntimeException extends RuntimeException {

        public NoStackTraceRuntimeException(String s)
        {
            System.out.println(s);
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }
}