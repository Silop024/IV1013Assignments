import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Hiddec
{
    static Predicate<byte[]> notEqualsKeyHash;

    static Function<byte[], byte[]> decrypt;
    static Function<byte[], byte[]> update;

    public static void main(String[] args)
    {
        Map<String, String> map = Helper.parseArgs(args);

        final SecretKeySpec key = Helper.createSecretKey(map.get("--key"));
        final Cipher cipher = Helper.createCipher(map.get("--ctr"), key, Cipher.DECRYPT_MODE);
        final byte[] input = Helper.parseBytesFromFile(map.get("--input"));
        final byte[] keyDigest = Helper.digest(key.getEncoded());

        update = cipher::update;
        decrypt = block -> {
            try {
                return cipher.doFinal(block);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException("Failed to decrypt block");
            }
        };
        notEqualsKeyHash = block -> !Arrays.equals(block, keyDigest);

        boolean ctr = map.get("--ctr") != null;

        byte[] hiddenData = getHiddenData(input, ctr);

        Helper.writeToFile(hiddenData, map.get("--output"));
    }

    private static byte[] getHiddenData(final byte[] data, boolean ctr)
    {
        // Split data into blocks aligned with AES-128.
        List<byte[]> blocks = Helper.splitIntoBlocks(data);

        // Stores last found H(k).
        final byte[][] Hk = new byte[1][];

        // Get hidden blocks, either in ctr or ecb mode
        List<byte[]> hiddenBlocks = ctr ? getCTR(blocks, Hk) : getECB(blocks, Hk);

        // Get H'.
        int indexOfHprime = blocks.lastIndexOf(Hk[0]) + 1;
        byte[] Hprime = decrypt.apply(blocks.get(indexOfHprime));

        // Convert List<byte[]> to ByteBuffer and get the hash of the hidden data H(data).
        ByteBuffer buf = ByteBuffer.allocate(hiddenBlocks.size() * 16);
        hiddenBlocks.forEach(buf::put);
        byte[] Hdata = Helper.digest(buf.array());

        // If H(data) != H'(k)', unsuccessful.
        if (!Arrays.equals(Hdata, Hprime)) {
            Helper.exitWithError("Failed to verify data, H' != H(data)");
        }
        return buf.array();
    }

    private static List<byte[]> getECB(final List<byte[]> blocks, final byte[][] Hk)
    {
        return blocks.stream()
                .peek(block -> Hk[0] = block)
                .map(decrypt)
                .dropWhile(notEqualsKeyHash)
                .skip(1)
                .takeWhile(notEqualsKeyHash)
                .collect(Collectors.toList());
    }
    
    private static List<byte[]> getCTR(final List<byte[]> blocks, final byte[][] Hk)
    {
        return blocks.stream()
                .peek(block -> Hk[0] = block)
                .dropWhile(block -> notEqualsKeyHash.test(decrypt.apply(block)))
                .map(update)
                .skip(1)
                .takeWhile(notEqualsKeyHash)
                .collect(Collectors.toList());
    }
}
