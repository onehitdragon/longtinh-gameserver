package hero.share.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Random;

public class OtherCommTool
{
    private static Random random;

    private static byte   dateBuf[]    = null;

    protected static byte bdays[][]    = {
            {80, 97, 100 }, {83, 117, 110 }, {77, 111, 110 }, {84, 117, 101 },
            {87, 101, 100 }, {84, 104, 117 }, {70, 114, 105 }, {83, 97, 116 } };

    protected static byte bmonthes[][] = {
            {74, 97, 110 }, {70, 101, 98 }, {77, 97, 114 }, {65, 112, 114 },
            {77, 97, 121 }, {74, 117, 110 }, {74, 117, 108 }, {65, 117, 103 },
            {83, 101, 112 }, {79, 99, 116 }, {78, 111, 118 }, {68, 101, 99 } };

    private OtherCommTool()
    {
    }

    public static void clear (char _input[])
    {
        short length = (short) _input.length;
        for (short i = 0; i < length; i++)
            _input[i] = '\0';

    }

    public static String getCurrentTime ()
    {
        Calendar now = Calendar.getInstance();
        StringBuffer time = new StringBuffer("");
        time.append(now.get(1));
        time.append("-");
        time.append(now.get(2) + 1);
        time.append("-");
        time.append(now.get(5));
        time.append(" ");
        time.append(now.get(11));
        time.append(":");
        time.append(now.get(12));
        return time.toString();
    }

    public static boolean moveFile (String _soureFile, String _destFile)
    {
        File soureFile = new File(_soureFile);
        File destFile = new File(_destFile);
        return moveFile(soureFile, destFile);
    }

    public static boolean moveFile (File _soureFile, File _destFile)
    {
        boolean moveSuccess = false;
        if (_soureFile != null && _destFile != null && _soureFile.exists())
            try
            {
                (new File(_destFile.getParent())).mkdirs();
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(_destFile));
                BufferedInputStream in = new BufferedInputStream(
                        new FileInputStream(_soureFile));
                for (int currentByte = in.read(); currentByte != -1; currentByte = in
                        .read())
                    out.write(currentByte);

                in.close();
                out.close();
                moveSuccess = true;
                _soureFile.delete();
            }
            catch (Exception e)
            {
                moveSuccess = false;
            }
        return moveSuccess;
    }

    public static final int getRandomInt (int _range)
    {
        return random.nextInt(_range);
    }

    public static boolean containBlackSpace (char _input[], int _length)
    {
        for (int i = 0; i < _length; i++)
            if (_input[i] <= ' ')
                return true;

        return false;
    }

    public static boolean containBlackSpace (String input, int _length)
    {
        char _input[] = input.toCharArray();
        for (int i = 0; i < _length; i++)
            if (_input[i] <= ' ')
                return true;

        return false;
    }

    public static boolean isValideChar (char _input[], int _length)
    {
        for (int i = 0; i < _length; i++)
            if (_input[i] < '!' || _input[i] > '~')
                return false;

        return true;
    }

    public static boolean isValideChar (String _input, int _length)
    {
        char input[] = _input.toCharArray();
        for (int i = 0; i < _length; i++)
            if (input[i] < '!' || input[i] > '~')
                return false;

        return true;
    }

    public static byte[] getHTTPDate ()
    {
        Calendar cal = Calendar.getInstance();
        java.util.Date now = cal.getTime();
        int dayofweek = cal.get(7);
        int j = 0;
        for (int i = 0; i < 3; i++)
            dateBuf[j++] = bdays[dayofweek][i];

        dateBuf[j++] = 44;
        dateBuf[j++] = 32;
        int day = cal.get(5);
        if (day < 10)
        {
            dateBuf[j++] = 48;
            dateBuf[j++] = (byte) (48 + day);
        }
        else
        {
            dateBuf[j++] = (byte) (48 + day / 10);
            dateBuf[j++] = (byte) (48 + day % 10);
        }
        dateBuf[j++] = 32;
        int month = cal.get(2);
        for (int i = 0; i < 3; i++)
            dateBuf[j++] = bmonthes[month][i];

        dateBuf[j++] = 32;
        int year = cal.get(1);
        dateBuf[j + 3] = (byte) (48 + year % 10);
        year /= 10;
        dateBuf[j + 2] = (byte) (48 + year % 10);
        year /= 10;
        dateBuf[j + 1] = (byte) (48 + year % 10);
        year /= 10;
        dateBuf[j] = (byte) (48 + year);
        j += 4;
        dateBuf[j++] = 32;
        int hour = cal.get(11);
        if (hour < 10)
        {
            dateBuf[j++] = 48;
            dateBuf[j++] = (byte) (48 + hour);
        }
        else
        {
            dateBuf[j++] = (byte) (48 + hour / 10);
            dateBuf[j++] = (byte) (48 + hour % 10);
        }
        dateBuf[j++] = 58;
        int minute = cal.get(12);
        if (minute < 10)
        {
            dateBuf[j++] = 48;
            dateBuf[j++] = (byte) (48 + minute);
        }
        else
        {
            dateBuf[j++] = (byte) (48 + minute / 10);
            dateBuf[j++] = (byte) (48 + minute % 10);
        }
        dateBuf[j++] = 58;
        int second = cal.get(13);
        if (second < 10)
        {
            dateBuf[j++] = 48;
            dateBuf[j++] = (byte) (48 + second);
        }
        else
        {
            dateBuf[j++] = (byte) (48 + second / 10);
            dateBuf[j++] = (byte) (48 + second % 10);
        }
        dateBuf[j++] = 32;
        dateBuf[j++] = 71;
        dateBuf[j++] = 77;
        dateBuf[j++] = 84;
        return dateBuf;
    }

    static
    {
        random = new Random();
        random.setSeed(Calendar.getInstance().getTimeInMillis());
        dateBuf = new byte[29];
    }
}
