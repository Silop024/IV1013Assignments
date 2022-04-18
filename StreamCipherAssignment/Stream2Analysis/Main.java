import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class Main
{
    static int width = 300;
    static int height = 300;
    static long seed = 12345;
    static long modulus = 0x1000000000000L;
    static long multiplier = 0x5DEECE66DL;
    static long increment = 11L;
    static boolean quit = false;

    public static void main(String[] args)
    {
        System.out.println("This program will generate a bitmap from a LCG of desired parameters");
        System.out.println("seed = (a * seed) + c mod  m");
        System.out.printf(
                "Defaults: seed = %d, width/height = %d/%d, modulus = %d, multiplier = %d, increment = %d%n",
                seed, width, height, modulus, multiplier, increment
        );
        while (!quit) {
            System.out.println("[input option]");
            System.out.printf(
                    "Set seed [s]%nSet width [w]%nSet height [h]%nSet modulus [m]%nSet multiplier " +
                            "[a]%nSet increment [c]%nGenerate [g]%nFrom java [j]%nSee params [p]%nQuit [q]%n"
            );
            handleInput();
        }

        //BitMapGenerator generator = new BitMapGenerator(seed, width, height);
    }

    private static void handleInput()
    {
        Scanner in = new Scanner(System.in, StandardCharsets.UTF_8);
        String option = in.nextLine().toLowerCase(Locale.ROOT);

        long answer;
        switch (option) {
            case "s":
                System.out.println("Enter desired seed (decimal)");
                answer = Long.parseLong(in.nextLine());

                if (answer >= 0 && answer < modulus) seed = answer;
                else System.out.println("Invalid seed, 0 <= seed < modulus");

                break;
            case "w":
                System.out.println("Enter desired width");
                width = Integer.parseInt(in.nextLine());
                break;
            case "h":
                System.out.println("Enter desired height");
                height = Integer.parseInt(in.nextLine());
                break;
            case "m":
                System.out.println("Enter desired modulus m");
                answer = Long.parseLong(in.nextLine());

                if (answer > 0) modulus = answer;
                else System.out.println("Invalid modulus, 0 < modulus");
                break;
            case "a":
                System.out.println("Enter desired multiplier a");
                answer = Long.parseLong(in.nextLine());

                if (answer > 0 && answer < modulus) multiplier = answer;
                else System.out.println("Invalid multiplier, 0 < multiplier < modulus");
                break;
            case "c":
                System.out.println("Enter desired increment c");
                answer = Long.parseLong(in.nextLine());

                if (answer >= 0 && increment < modulus) increment = answer;
                else System.out.println("Invalid increment, 0 <= increment < modulus");
                break;
            case "g":
                if (checkCorrectParameters()) {
                    System.out.println("Generating bitmap...");
                    BitMapGenerator generator = new BitMapGenerator(width, height);
                    MyRandom random = new MyRandom(seed, modulus, multiplier, increment);
                    generator.createBitMap(random, "GeneratedBitMap.jpg");
                } else {
                    System.out.println("Could not generate bitmap");
                }
                break;
            case "j":
                BitMapGenerator javaGenerator = new BitMapGenerator(width, height);
                javaGenerator.createBitMap(new Random(seed), "JavaGeneratedBitMap.jpg");
                break;
            case "p":
                System.out.printf(
                        "seed = %d, width/height = %d/%d, modulus = %d, multiplier = %d, increment = %d%n",
                        seed, width, height, modulus, multiplier, increment
                );
                break;
            case "q":
                quit = true;
                break;
            default:
                System.out.println("Invalid option");
        }
    }

    private static boolean checkCorrectParameters()
    {
        if (modulus <= 0) {
            System.out.println("Invalid modulus, 0 < modulus");
            return false;
        }

        if (multiplier <= 0 || multiplier >= modulus) {
            System.out.println("Invalid multiplier, 0 < multiplier < modulus");
            return false;
        }

        if (increment < 0 || increment >= modulus) {
            System.out.println("Invalid increment, 0 <= increment < modulus");
            return false;
        }

        if (seed <= 0 || seed > modulus) {
            System.out.println("Invalid seed, 0 <= seed < modulus");
            return false;
        }
        return true;
    }
}
