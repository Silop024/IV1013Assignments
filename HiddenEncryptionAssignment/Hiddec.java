import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Hiddec
{
    private static Predicate<byte[]> notEqualsKeyHash;

    public static void main(String[] args)
    {
        Map<String, String> map = Helper.parseArgs(args);

        final SecretKeySpec key = Helper.createSecretKey(map.get("--key"));
        final Cipher cipher = Helper.createCipher(map.get("--ctr"), key, Cipher.DECRYPT_MODE);
        final byte[] input = Helper.readBytesFromFile(map.get("--input"));
        final byte[] keyDigest = Helper.getDigest(key.getEncoded());

        Helper.initCiphers(cipher);

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
        byte[] Hprime = Helper.doFinal.apply(blocks.get(indexOfHprime));

        // Convert List<byte[]> to ByteBuffer and get the hash of the hidden data H(data).
        ByteBuffer buf = ByteBuffer.allocate(hiddenBlocks.size() * 16);
        hiddenBlocks.forEach(buf::put);
        byte[] Hdata = Helper.getDigest(buf.array());

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
                .map(Helper.doFinal)
                .dropWhile(notEqualsKeyHash)
                .skip(1)
                .takeWhile(notEqualsKeyHash)
                .collect(Collectors.toList());
    }

    private static List<byte[]> getCTR(final List<byte[]> blocks, final byte[][] Hk)
    {
        return blocks.stream()
                .peek(block -> Hk[0] = block)
                .dropWhile(block -> notEqualsKeyHash.test(Helper.doFinal.apply(block)))
                .map(Helper.update)
                .skip(1)
                .takeWhile(notEqualsKeyHash)
                .collect(Collectors.toList());
    }
}
