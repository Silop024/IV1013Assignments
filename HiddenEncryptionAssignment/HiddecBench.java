/*  This program takes an encrypted file and a decryption key and searches the given file for the hashed key, when the hashed
    key is found the program starts to decrypt data until the second hash is found. The extracted data is verified with the hash of the
    data located in the input file. If the data is verfied the data is printed to an output file. The program has support for CTR-mode.

    Usage under UNIX:
        javac Hiddec.java
        java Hiddec --key=KEY --input=INPUT --output=OUTPUT --ctr=CTR

    @author Emil Stahl
    Date: May 18, 2020
*/

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HiddecBench
{

    static Cipher cipher;
    static boolean isCTR = false;
    static byte[] globalCTR;

    static byte[] hash(byte[] key) throws NoSuchAlgorithmException
    {

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(key);
        byte[] digest = md.digest();
        return digest;
    }

    static byte[] stringToHexByteArray(String string)
    {
        int len = string.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(string.charAt(i), 16) << 4) + Character.digit(string.charAt(i + 1), 16));
        }
        return data;
    }

    static byte[] readFile(String input)
    {

        byte[] byteArray = null;
        try {
            byteArray = Files.readAllBytes(Paths.get(input));
        } catch (IOException e) {
            System.out.println("\nAn error occured while reading from file " + input + "\n" + e);
            System.exit(1);
        }
        return byteArray;
    }

    static void writeToFile(byte[] data, String output)
    {
        try {
            Files.write(Paths.get(output), data);
        } catch (IOException e) {
            System.out.println("\nAn error occured while writing to file " + output + "\n" + e);
            System.exit(1);
        }
    }

    static byte[] extractData(byte[] key, byte[] input, byte[] hash) throws Exception
    {
        byte[] data;
        init(key);
        for (int i = 0; i < input.length; i += 16) {
            data = decrypt(Arrays.copyOfRange(input, i, input.length));
            if (match(data, hash, 0)) {
                return verify(hash, data);
            }
        }
        throw new Exception("No data found");
    }

    static byte[] decrypt(byte[] blob) throws BadPaddingException, IllegalBlockSizeException
    {
        return cipher.doFinal(blob);
    }

    static byte[] verify(byte[] hash, byte[] data) throws Exception
    {

        int hashLength = hash.length, start, end, offset;
        byte[] extractedData, hashedData;

        for (offset = hashLength; offset < data.length; offset++) {

            if (match(data, hash, offset)) {

                extractedData = Arrays.copyOfRange(data, hashLength, offset);
                start = offset += hashLength;
                end = start + hashLength;
                hashedData = Arrays.copyOfRange(data, start, end);

                if (Arrays.equals(hash(extractedData), hashedData))
                    return extractedData;

                else
                    System.out.println("Extracted data do not match verification data");
                System.exit(1);
            }
        }
        throw new Exception("No data found");
    }

    static boolean match(byte[] data, byte[] hash, int offset)
    {
        byte[] header = Arrays.copyOfRange(data, offset, offset + hash.length);
        return Arrays.equals(hash, header);
    }

    static void init(byte[] key) throws Exception
    {
        if (isCTR) {
            cipher = Cipher.getInstance("AES/CTR/NoPadding");
            IvParameterSpec ivSpec = new IvParameterSpec(globalCTR);
            SecretKeySpec sKey = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.DECRYPT_MODE, sKey, ivSpec);
        } else {
            cipher = Cipher.getInstance("AES/ECB/NoPadding");
            SecretKeySpec sKey = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.DECRYPT_MODE, sKey);
        }
    }

    static Map<String, String> getArgs(String args[])
    {

        Map<String, String> argsList = new HashMap<String, String>();

        for (String arg : args) {
            String[] argument = arg.split("=");
            switch (argument[0]) {
                case "--key":
                    argsList.put("key", argument[1]);
                    break;

                case "--ctr":
                    argsList.put("ctr", argument[1]);
                    break;

                case "--input":
                    argsList.put("input", argument[1]);
                    break;

                case "--output":
                    argsList.put("output", argument[1]);
                    break;
            }
        }
        if (argsList.containsKey("ctr")) {
            isCTR = true;
            globalCTR = stringToHexByteArray(argsList.get("ctr"));
        }
        return argsList;
    }

    public static void main(String[] args) throws Exception
    {

        if (args.length < 3) {
            System.out.println("Usage: --key=KEY --ctr=CTR --input=INPUT --output=OUTPUT");
            System.exit(1);
        }

        byte[] data, byteKey;
        String key, input, output;
        Map<String, String> argsList = getArgs(args);

        key = argsList.get("key");
        input = argsList.get("input");
        output = argsList.get("output");
        byteKey = stringToHexByteArray(key);

        data = extractData(byteKey, readFile(input), hash(byteKey));
        writeToFile(data, output);
    }
}
