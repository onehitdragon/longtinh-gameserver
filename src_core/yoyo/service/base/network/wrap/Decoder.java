package yoyo.service.base.network.wrap;

public class Decoder
{
	static byte[] hex = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
    
	public static byte[] decode (byte[] input)
    {
        int len = input.length;
        int m = len % 2;
        len = len / 2 + m;

        byte[] result = new byte[len];
        for (int i = 0, j = 0; i < input.length; i += 2, j++)
        {
            int hight = input[i];
            int low = 0;
            if (i + 1 != input.length)
            {
                low = input[i + 1];
            }

            int real = getIndexOf((byte) hight) * 16 + getIndexOf((byte) low);
            result[j] = (byte) real;
        }
        return result;
    }

    private static int getIndexOf (byte val)
    {
        for (int i = 0; i < hex.length; i++)
        {
            if (hex[i] == val)
            {
                return i;
            }
        }
        return -1;
    }
}
