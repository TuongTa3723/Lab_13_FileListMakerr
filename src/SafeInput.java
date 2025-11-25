import java.util.Scanner;
import java.util.regex.Pattern;

public class SafeInput {

    /** A: getNonZeroLenString */
    public static String getNonZeroLenString(Scanner pipe, String prompt) {
        String retString = "";
        do {
            System.out.print("\n" + prompt + ": ");
            retString = pipe.nextLine().trim();
        } while (retString.length() == 0);
        return retString;
    }

    /** B: getInt (unconstrained) */
    public static int getInt(Scanner pipe, String prompt) {
        int result = 0;
        boolean done = false;
        String trash;
        do {
            System.out.print(prompt + ": ");
            if (pipe.hasNextInt()) {
                result = pipe.nextInt();
                pipe.nextLine(); // clear newline
                done = true;
            } else {
                trash = pipe.nextLine(); // read invalid token(s)
                System.out.println("Error: \"" + trash + "\" is not an integer. Try again.");
            }
        } while (!done);
        return result;
    }

    /** C: getDouble (unconstrained) */
    public static double getDouble(Scanner pipe, String prompt) {
        double result = 0.0;
        boolean done = false;
        String trash;
        do {
            System.out.print(prompt + ": ");
            if (pipe.hasNextDouble()) {
                result = pipe.nextDouble();
                pipe.nextLine(); // clear newline
                done = true;
            } else {
                trash = pipe.nextLine();
                System.out.println("Error: \"" + trash + "\" is not a number. Try again.");
            }
        } while (!done);
        return result;
    }

    /** D: getRangedInt [low..high], inclusive */
    public static int getRangedInt(Scanner pipe, String prompt, int low, int high) {
        int result = 0;
        boolean done = false;
        String trash;
        do {
            System.out.print(prompt + " [" + low + " - " + high + "]: ");
            if (pipe.hasNextInt()) {
                result = pipe.nextInt();
                pipe.nextLine(); // clear newline
                if (result >= low && result <= high) {
                    done = true;
                } else {
                    System.out.println("Error: input out of range. Try again.");
                }
            } else {
                trash = pipe.nextLine();
                System.out.println("Error: \"" + trash + "\" is not an integer. Try again.");
            }
        } while (!done);
        return result;
    }

    /** E: getRangedDouble [low..high], inclusive */
    public static double getRangedDouble(Scanner pipe, String prompt, double low, double high) {
        double result = 0.0;
        boolean done = false;
        String trash;
        do {
            System.out.print(prompt + " [" + low + " - " + high + "]: ");
            if (pipe.hasNextDouble()) {
                result = pipe.nextDouble();
                pipe.nextLine(); // clear newline
                if (result >= low && result <= high) {
                    done = true;
                } else {
                    System.out.println("Error: input out of range. Try again.");
                }
            } else {
                trash = pipe.nextLine();
                System.out.println("Error: \"" + trash + "\" is not a number. Try again.");
            }
        } while (!done);
        return result;
    }

    /** F: getYNConfirm → true for Yes, false for No (accepts Y/N y/n) */
    public static boolean getYNConfirm(Scanner pipe, String prompt) {
        String resp;
        while (true) {
            System.out.print(prompt + " [Y/N]: ");
            resp = pipe.nextLine().trim();
            if (resp.equalsIgnoreCase("Y")) return true;
            if (resp.equalsIgnoreCase("N")) return false;
            System.out.println("Error: please enter Y or N.");
        }
    }

    /** G: getRegExString (must match given regex) */
    public static String getRegExString(Scanner pipe, String prompt, String regEx) {
        String input;
        Pattern pat = Pattern.compile(regEx);
        while (true) {
            System.out.print(prompt + " (pattern " + regEx + "): ");
            input = pipe.nextLine();
            if (pat.matcher(input).matches()) {
                return input;
            }
            System.out.println("Error: input does not match the required pattern. Try again.");
        }
    }

    /** H (PrettyHeader helper): prints a 60-char wide header with centered msg and *** at both ends of line 2 */
    public static void prettyHeader(String msg) {
        final int WIDTH = 60;

        // line 1: 60 *
        for (int i = 0; i < WIDTH; i++) System.out.print("*");
        System.out.println();

        // line 2: *** + centered msg + ***
        String inner = " " + msg.trim() + " ";
        int innerWidth = WIDTH - 6; // because we place 3 * on left and right
        if (inner.length() > innerWidth) {
            inner = inner.substring(0, innerWidth); // truncate if too long
        }
        int padTotal = innerWidth - inner.length();
        int padLeft = padTotal / 2;
        int padRight = padTotal - padLeft;

        System.out.print("***");
        for (int i = 0; i < padLeft; i++) System.out.print(" ");
        System.out.print(inner);
        for (int i = 0; i < padRight; i++) System.out.print(" ");
        System.out.println("***");

        // line 3: 60 *
        for (int i = 0; i < WIDTH; i++) System.out.print("*");
        System.out.println();
    }
}