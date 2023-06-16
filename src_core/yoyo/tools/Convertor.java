package yoyo.tools;

public class Convertor
{
    public static void int2Bytes (int input, byte[] output, int offset)
    {
        if (output == null)
        {
            throw new NullPointerException("output null");
        }
        if (offset + 4 > output.length)
        {
            throw new IndexOutOfBoundsException("overflow");
        }
        output[offset] = (byte) ((input & 0xff000000) >> 24);
        output[offset + 1] = (byte) ((input & 0xff0000) >> 16);
        output[offset + 2] = (byte) ((input & 0xff00) >> 8);
        output[offset + 3] = (byte) (input & 0xff);
    }
    
    public static byte[] int2Bytes (int input)
    {
        byte[] bytes = new byte[4];
        int2Bytes(input, bytes, 0);
        return bytes;
    }

    public static int bytes2Int (byte[] input, int offset)
    {
        if (input == null)
        {
            throw new NullPointerException("output null");
        }
        if (offset + 4 > input.length)
        {
            throw new IndexOutOfBoundsException("overflow");
        }
        return ((input[offset] & 0xff) << 24)
                | ((input[offset + 1] & 0xff) << 16)
                | ((input[offset + 2] & 0xff) << 8)
                | (input[offset + 3] & 0xff);
    }

    public static byte[] short2Bytes (short input)
    {
        byte[] bytes = new byte[2];
        short2Bytes(input, bytes, 0);
        return bytes;
    }

    public static void short2Bytes (short input, byte[] output, int offset)
    {
        if (output == null)
        {
            throw new NullPointerException("output null");
        }
        if (offset + 2 > output.length)
        {
            throw new IndexOutOfBoundsException("overflow");
        }
        output[offset] = (byte) ((input & 0xff00) >> 8);
        output[offset + 1] = (byte) (input & 0xff);
    }

    public static short bytes2Short (byte[] input, int offset)
    {
        if (input == null)
        {
            throw new NullPointerException("output null");
        }
        if (offset + 2 > input.length)
        {
            throw new IndexOutOfBoundsException("overflow");
        }
        return (short) (((input[offset] & 0xff) << 8) | (input[offset + 1] & 0xff));
    }

    public static String bytes2String (byte[] input, int length)
    {
        int n = (length + 1) / 2;
        if (n == 0)
        {
            return "";
        }
        char chs[] = new char[n];
        for (int i = 0; i < n; i++)
        {
            chs[i] = (char) (input[2 * i] << 8 | input[2 * i + 1] & 0xff);
        }
        return new String(chs);
    }

}
