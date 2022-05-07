import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class InputHandler
{
    private final String[] args;
    private SecretKeySpec key;
    private Cipher cipher;
    private byte[] inData;
    private byte[] ctr;
    private String outPath;
    private String template;
    private int size = -1;
    private int offset = -1;

    public InputHandler(String[] args)
    {
        this.args = args;
        parseArgs();
    }

    private void parseArgs()
    {
        Pattern pattern = Pattern.compile("--\\w+=");

        for (String arg : args) {
            String[] splitArg = arg.split("=");

            if (splitArg.length != 2 || !pattern.matcher(arg).matches()) {
                Hiddec.exitWithError("Argument " + arg + " is invalid");
            }
            String flag = splitArg[0];
            String val = splitArg[1];

            switch (flag) {
                case "--key":
                    key = createSecretKey(val);
                    break;
                case "--input":
                    inData = getFileData(val);
                    break;
                case "--output":
                    outPath = val;
                    break;
                case "--ctr":
                    ctr = val.getBytes(StandardCharsets.UTF_8);
                    break;
                case "--offset":
                    offset = Integer.parseInt(val);
                    break;
                case "--template":
                    template = val;
                    break;
                case "--size":
                    size = Integer.parseInt(val);
            }
        }
        if (template != null && size > 0) {
            Hiddec.exitWithError("Only one of --template and --size can be specified");
        }
        cipher = createCipher();
    }

    private SecretKeySpec createSecretKey(String key)
    {
        try {
            byte[] digest = MessageDigest.getInstance("MD5").digest(key.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(digest, "AES");
        } catch (NoSuchAlgorithmException e) {
            Hiddec.exitWithError("Failed to get digest of " + key);
        }
        return null;
    }

    private byte[] getFileData(String path)
    {
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            Hiddec.exitWithError("Failed to read " + path);
        }
        return null;
    }

    private Cipher createCipher()
    {
        Cipher cipher = null;
        try {

            if (ctr != null) {
                cipher = Cipher.getInstance("AES/CTR/NoPadding");
                IvParameterSpec spec = new IvParameterSpec(ctr);
                cipher.init(Cipher.DECRYPT_MODE, key, spec);
            } else {
                cipher = Cipher.getInstance("AES/ECB/NoPadding");
                cipher.init(Cipher.DECRYPT_MODE, key);
            }
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                 InvalidKeyException e) {
            Hiddec.exitWithError("Failed to get cipher");
        }
        return cipher;
    }

    public byte[] getInData()
    {
        return inData;
    }

    public String getOutPath()
    {
        return outPath;
    }

    public SecretKeySpec getKey()
    {
        return key;
    }

    public int getOffset()
    {
        return offset;
    }

    public Cipher getCipher()
    {
        return cipher;
    }
}
