import java.util.List;
import java.util.function.BiConsumer;

public class Mangler implements BiConsumer<List<String>, Integer>
{
    private final Cracker c;

    public Mangler(final Cracker c)
    {
        this.c = c;
    }

    public void start(String s, int i)
    {
        if (i == 0) {
            c.checkAllVictims(s);
        } else {
            accept(Mangle.getAllMangles(s), i - 1);
        }
    }

    @Override
    public void accept(List<String> strings, Integer integer)
    {
        if (integer < 1) {
            strings.forEach(c::checkAllVictims);
        } else {
            strings.forEach(x -> this.accept(Mangle.getAllMangles(x), integer - 1));
        }
    }

    @Override
    public BiConsumer<List<String>, Integer> andThen(BiConsumer<? super List<String>, ? super Integer> after)
    {
        return BiConsumer.super.andThen(after);
    }
}