package yoyo.tools;
import java.util.Random;

public class YOYORandom
{
    private static Random rnd = new Random();
    
    public static void reset ()
    {
    	long cur = System.currentTimeMillis();
        rnd.setSeed(cur);
    }
    
    public static Random newRandom ()
    {
        return new Random();
    }
    
    public static boolean probility (double probility)
    {
        int probInt = (int) (probility * 100);
        probInt = Math.abs(probInt);
        int random = nextInt(100);
        if (random < probInt)
        {
            return true;
        }
        return false;
    }

    public static int getRndInt ()
    {
        return rnd.nextInt();
    }

    public static boolean nextBoolean ()
    {
        return rnd.nextBoolean();
    }
    
    public static int nextInt (int range)
    {
        if (range <= 0)
        {
            return 0;
        }
        return rnd.nextInt(range);
    }

    public static void nextBytes (byte[] bytes)
    {
        rnd.nextBytes(bytes);
    }
    
    public static int nextInt (int min, int max)
    {
        if (min < 0)
        {
            min = 0;
        }
        int offset = max - min;
        return Math.abs(rnd.nextInt()) % (Math.abs(offset) + 1) + min;
    }

    public static long nextLong ()
    {
        return rnd.nextLong();
    }

    public static float nextFloat ()
    {
        return rnd.nextFloat();
    }

    public static Double nextDouble ()
    {
        return rnd.nextDouble();
    }
}
