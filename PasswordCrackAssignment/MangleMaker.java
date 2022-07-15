import java.util.*;

public class MangleMaker
{
    private static final char[] alphabet = "01234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final Locale locale = Locale.ROOT;

    public static List<String> getAllMangles(String origin)
    {
        int length = origin.length();
        if (length < 2) {
            return Collections.emptyList();
        }
        Set<String> mangles = new HashSet<>(64);

        mangles.add(toggle(origin));
        mangles.add(capitalize(origin, true));
        mangles.add(capitalize(origin, false));
        mangles.add(uppercase(origin, true));
        mangles.add(uppercase(origin, false));
        mangles.add(rollercoaster(origin, true));
        mangles.add(rollercoaster(origin, false));

        for (char c : alphabet) {
            if (length < 8) {
                mangles.add(append(origin, c));
            }
            mangles.add(prepend(origin, c));
        }
        if (length <= 8) {
            mangles.add(trim(origin, false));
        }
        if (length < 8) {
            mangles.add(duplicate(origin));
        }

        mangles.add(trim(origin, true));

        String reverse = reverse(origin);
        if (!reverse.equals(origin)) {
            mangles.add(reverse);
            if (length < 8) {
                mangles.add(reflect(origin, false));
                mangles.add(reflect(origin, true));
            }
        }
        mangles.remove(origin);

        return new ArrayList<>(mangles);
    }

    public static String toggle(String origin)
    {
        char[] chars = origin.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            chars[i] = Character.isUpperCase(c) ? Character.toLowerCase(c) : Character.toUpperCase(c);
            //chars[i] = (char) (chars[i] ^ 32);
        }
        return new String(chars);
    }

    public static String capitalize(String origin, boolean yes)
    {
        if (yes) {
            return Character.toUpperCase(origin.charAt(0)) + origin.substring(1);
        }
        return Character.toLowerCase(origin.charAt(0)) + origin.substring(1);
    }

    public static String reverse(String origin)
    {
        return new StringBuilder(origin).reverse().toString();
    }

    public static String reflect(String origin, boolean first)
    {
        if (first) {
            return reverse(origin) + origin;
        }
        return origin + reverse(origin);
    }

    public static String trim(String origin, boolean first)
    {
        if (first) {
            return origin.substring(1);
        }
        return origin.substring(0, origin.length() - 1);
    }

    public static String duplicate(String origin)
    {
        return origin + origin;
    }

    public static String append(String origin, char c)
    {
        return origin + c;
    }

    public static String prepend(String origin, char c)
    {
        return c + origin;
    }

    public static String uppercase(String origin, boolean yes)
    {
        if (yes) {
            return origin.toUpperCase(locale);
        }
        return origin.toLowerCase(locale);
    }

    public static String rollercoaster(String origin, boolean first)
    {
        char[] chars = origin.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            boolean up = first == (i % 2 == 0);
            chars[i] = up ? Character.toUpperCase(chars[i]) : Character.toLowerCase(chars[i]);
        }
        return new String(chars);
    }
}