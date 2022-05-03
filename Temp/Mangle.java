import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Mangle
{
    private static final char[] alphabet = "01234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final Locale locale = Locale.ROOT;

    public static String[] getAllMangles(String origin)
    {
        if (origin.length() < 2) {
            return null;
        }

        final Set<String> mangles = new HashSet<>(16);

        mangles.add(toggle(origin));
        mangles.add(capitalize(origin, true));
        mangles.add(capitalize(origin, false));
        mangles.add(trim(origin, true));
        mangles.add(trim(origin, false));
        mangles.add(duplicate(origin));
        mangles.add(uppercase(origin, true));
        mangles.add(uppercase(origin, false));
        mangles.add(rollercoaster(origin, true));
        mangles.add(rollercoaster(origin, false));

        String reverse = reverse(origin);
        if(!reverse.equals(origin))
        {
            mangles.add(reverse);
            mangles.add(reflect(origin, true));
            mangles.add(reflect(origin, false));
        }

        for (char c : alphabet) {
            if(origin.length() < 8) {
                mangles.add(append(origin, c));
            }
            mangles.add(prepend(origin, c));
        }

        mangles.remove(origin);

        return mangles.toArray(String[]::new);
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