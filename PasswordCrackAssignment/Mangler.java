import java.util.List;
import java.util.function.BiConsumer;

public class Mangler implements BiConsumer<List<String>, Integer>
{
    private final Cracker cracker;

    public Mangler(final Cracker cracker)
    {
        this.cracker = cracker;
    }

    public void start(String s, int i)
    {
        if (i == 0) {
            cracker.checkAllVictims(s);
        } else {
            accept(Mangle.getAllMangles(s), i - 1);
        }
    }

    @Override
    public void accept(List<String> strings, Integer integer)
    {
        if (integer < 1) {
            strings.forEach(cracker::checkAllVictims);
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