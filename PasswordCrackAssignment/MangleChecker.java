import java.util.List;

public class MangleChecker
{
    private final PasswordCracker cracker;

    public MangleChecker(final PasswordCracker cracker)
    {
        this.cracker = cracker;
    }

    public void checkMangles(String origin, int iterationsLeft)
    {
        if (iterationsLeft < 1) {
            cracker.checkAllVictims(origin);
        } else {
            checkMangles(MangleMaker.getAllMangles(origin), iterationsLeft - 1);
        }
    }

    public void checkMangles(List<String> mangles, int iterationsLeft)
    {
        if (iterationsLeft < 1) {
            for (String mangle : mangles) cracker.checkAllVictims(mangle);
        } else {
            for (String mangle : mangles) checkMangles(MangleMaker.getAllMangles(mangle), iterationsLeft - 1);
        }
    }
}