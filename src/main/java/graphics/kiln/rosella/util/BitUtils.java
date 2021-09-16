package graphics.kiln.rosella.util;

public class BitUtils {

    public static int countBits(int number) {
        int count = 0;
        while(number != 0) {
            number &= (number - 1);
            count++;
        }
        return count;
    }

    public static int countBits(long number) {
        int count = 0;
        while(number != 0) {
            number &= (number - 1);
            count++;
        }
        return count;
    }

    public static boolean isPowerOf2(int number) {
        return ((number - 1) & number) == 0;
    }

    public static boolean isPowerOf2(long number) {
        return ((number - 1) & number) == 0;
    }

    public static int getLogBase2(int number) {
        int result = 0;
        while(number > 0) {
            number = number >>> 1;
            result++;
        }
        return result;
    }
}
