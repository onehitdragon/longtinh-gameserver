package hero.share.service;

import java.text.NumberFormat;
import java.util.Locale;

import org.apache.log4j.Logger;

import yoyo.tools.YOYOOutputStream;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class DataConvertor
{
     private static Logger log = Logger.getLogger(DataConvertor.class);
    public DataConvertor()
    {
    }

    public static int byte2Int (byte _input)
    {
        return Integer.parseInt(String.valueOf(_input));
    }

    public static byte int2Byte (int _input)
    {
        return Byte.parseByte(String.valueOf(_input));
    }

    public static void int2Chars (int _input, char _output[])
    {
        _output[0] = (char) (_input >>> 16);
        _output[1] = (char) (_input & 0xffff);
    }

    public static short byte2Short (byte _input)
    {
        return Short.parseShort(String.valueOf(_input));
    }

    public static byte short2Byte (short _input)
    {
        return Byte.parseByte(String.valueOf(_input));
    }

    public static char byte2Char (byte _input)
    {
        return (char) Short.parseShort(String.valueOf(_input));
    }

    public static byte char2Byte (char _input)
    {
        return Byte.parseByte(String.valueOf(_input));
    }

    public static int chars2Int (char _input[])
    {
        return chars2Int(_input, 0);
    }

    public static int chars2Int (char _input[], int _offset)
    {
        int high = _input[_offset];
        int low = _input[_offset + 1];
        return high << 16 | low & 0xffff;
    }

    public static short bytes2Short (byte _input[])
    {
        byte high = _input[0];
        byte low = _input[1];
        return (short) (high << 8 | low & 0xff);
    }

    public static int bytes2Int (byte[] bRefArr, int _start, int _length)
    {
        int iOutcome = 0;
        byte bLoop;
        int temp = 0;
        for (int i = _start; i < (_start + _length); i++)
        {
            bLoop = bRefArr[i];
            iOutcome += (bLoop & 0xFF) << (8 * (3 - temp));
            temp++;
        }
        return iOutcome;
    }

    public static short bytes2Short (byte[] bRefArr, int _startIndex)
    {
        short sOutcome = 0;

        sOutcome += (bRefArr[_startIndex] & 0xFF) << 8;
        sOutcome += bRefArr[_startIndex + 1] & 0xFF;

        return sOutcome;
    }

    public static void main (String[] args)
    {
        try
        {
            YOYOOutputStream a = new YOYOOutputStream();
            a.writeShort((short) 120);
            a.flush();
            byte[] b = a.getBytes();

            short c = bytes2Short(b, 0);

            log.info("c:" + c);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static char bytes2Char (byte _input[])
    {
        byte high = _input[0];
        byte low = _input[1];
        return (char) (high << 8 | low & 0xff);
    }

    public static int bytes2Int (byte _input[])
    {
        byte byte1 = _input[0];
        byte byte2 = _input[1];
        byte byte3 = _input[2];
        byte byte4 = _input[3];
        return (char) ((byte4 << 32) + (byte3 << 16) + (byte2 << 8) + byte1);
    }

    public static byte[] int2bytes (int _value)
    {
        byte[] value = new byte[4];
        value[0] = (byte) (_value >>> 24);
        value[1] = (byte) (_value << 8 >>> 24);
        value[2] = (byte) (_value << 16 >>> 24);
        value[3] = (byte) (_value & 0xff);

        return value;
    }

    public static void bytes2Shorts (short _arrayLength, byte _input[],
            short _output[])
    {
        short arrayLength = (short) (_arrayLength / 2);
        for (short i = 0; i < arrayLength; i++)
        {
            byte high = _input[i * 2];
            byte low = _input[i * 2 + 1];
            if (low < 0)
                _output[i] = (short) (low + 256);
            else
                _output[i] = low;
            if (high < 0)
                _output[i] = (short) ((high + 256 << 8) + _output[i]);
            else
                _output[i] = (short) ((high << 8) + _output[i]);
        }

    }

    public static void shorts2Bytes (short _arrayLength, short _input[],
            byte _output[])
    {
        for (short i = 0; i < _arrayLength; i++)
        {
            _output[i * 2] = (byte) (_input[i] >>> 8);
            _output[i * 2 + 1] = (byte) (_input[i] & 0xff);
        }

    }

    public static void short2Bytes (short _input, byte _output[])
    {
        _output[0] = (byte) (_input >>> 8);
        _output[1] = (byte) (_input & 0xff);
    }

    public static void char2Bytes (char _input, byte _output[])
    {
        _output[0] = (byte) (_input >>> 8);
        _output[1] = (byte) (_input & 0xff);
    }

    public static void bytes2Chars (short _arrayLength, byte _input[],
            char _output[])
    {
        short arrayLength = (short) (_arrayLength / 2);
        for (short i = 0; i < arrayLength; i++)
        {
            char high = _input[i * 2] < 0 ? (char) (_input[i * 2] + 256)
                    : (char) _input[i * 2];
            char low = _input[i * 2 + 1] < 0 ? (char) (_input[i * 2 + 1] + 256)
                    : (char) _input[i * 2 + 1];
            high <<= '\b';
            _output[i] = (char) (high + low);
        }

    }

    public static void bytes2Chars (short _arrayLength, byte _input[],
            char _output[], short _off)
    {
        short arrayLength = (short) (_arrayLength / 2);
        for (short i = 0; i < arrayLength; i++)
        {
            char high = _input[i * 2] < 0 ? (char) (_input[i * 2] + 256)
                    : (char) _input[i * 2];
            char low = _input[i * 2 + 1] < 0 ? (char) (_input[i * 2 + 1] + 256)
                    : (char) _input[i * 2 + 1];
            high <<= '\b';
            _output[i + _off] = (char) (high + low);
        }

    }

    public static String bytes2String (byte _input[], int len)
    {
        int n = (len + 1) / 2;
        if (n == 0)
            return "";
        char chs[] = new char[n];
        for (int i = 0; i < n; i++)
            chs[i] = (char) (_input[2 * i] << 8 | _input[2 * i + 1] & 0xff);

        return new String(chs);
    }

    public static String bytes2String (byte _input[])
    {
        int n = (_input.length + 1) / 2;
        if (n == 0)
            return "";
        char chs[] = new char[n];
        for (int i = 0; i < n; i++)
            chs[i] = (char) (_input[2 * i] << 8 | _input[2 * i + 1] & 0xff);

        return new String(chs);
    }

    public static void bytes2Chars (short _arrayLength, short _off,
            byte _input[], char _output[])
    {
        _arrayLength += _off;
        short j = 0;
        for (short i = _off; i < _arrayLength; i += 2)
        {
            char high = _input[i] < 0 ? (char) (_input[i] + 256)
                    : (char) _input[i];
            char low = _input[i + 1] < 0 ? (char) (_input[i + 1] + 256)
                    : (char) _input[i + 1];
            high <<= '\b';
            _output[j] = (char) (high + low);
            j++;
        }

    }

    public static void chars2Bytes (short _arrayLength, char _input[],
            byte _output[])
    {
        for (short i = 0; i < _arrayLength; i++)
        {
            char high = (char) (_input[i] >>> 8);
            char low = (char) (_input[i] & 0xff);
            _output[i * 2] = high >= '\200' ? (byte) (high - 256) : (byte) high;
            _output[i * 2 + 1] = low >= '\200' ? (byte) (low - 256)
                    : (byte) low;
        }

    }

    public static void string2Shorts (String _input, short _output[], int _off)
    {
        char chars[] = _input.toCharArray();
        int length = chars.length;
        for (short i = 0; i < length; i++)
            _output[_off + i] = (short) chars[i];

    }

    public static short intel2Net (short _input)
    {
        byte high = (byte) (_input >>> 8);
        byte low = (byte) (_input & 0xff);
        return (short) ((low << 8) + high);
    }

    public static char intel2Net (char _input)
    {
        byte high = (byte) (_input >>> 8);
        byte low = (byte) (_input & 0xff);
        return (char) ((low << 8) + high);
    }

    public static int intel2Net (int _input)
    {
        byte byte1 = (byte) (_input >>> 32);
        byte byte2 = (byte) (_input >>> 16 & 0xff);
        byte byte3 = (byte) (_input >>> 8 & 0xff);
        byte byte4 = (byte) (_input & 0xff);
        return (char) ((byte4 << 32) + (byte3 << 16) + (byte2 << 8) + byte1);
    }

    public static void intel2Net (short _arrayLength, char _input[],
            char _output[])
    {
        for (short i = 0; i < _arrayLength; i++)
        {
            byte high = (byte) (_input[i] >>> 8);
            byte low = (byte) (_input[i] & 0xff);
            _output[i] = (char) ((low << 8) + high);
        }

    }

    public static void intel2Net (short _arrayLength, short _input[],
            short _output[])
    {
        for (short i = 0; i < _arrayLength; i++)
        {
            byte high = (byte) (_input[i] >>> 8);
            byte low = (byte) (_input[i] & 0xff);
            _output[i] = (short) ((low << 8) + high);
        }

    }

    public static void net2Intel (short _arrayLength, char _input[],
            char _output[])
    {
        for (short i = 0; i < _arrayLength; i++)
        {
            byte high = (byte) (_input[i] >>> 8);
            byte low = (byte) (_input[i] & 0xff);
            _output[i] = (char) ((low << 8) + high);
        }

    }

    /**
     * 百分比数值字符转换为float数值类型
     * 
     * @param _percent
     * @return
     */
    public static float percentString2Float (String _percent)
    {
        float value = 0.0F;

        try
        {
            value = NumberFormat.getPercentInstance(Locale.US).parse(_percent)
                    .floatValue();
        }
        catch (Exception e)
        {

        }

        return value;
    }

    /**
     * 百分比分子数值字符转换为float数值类型
     * 
     * @param _percent
     * @return
     */
    public static float percentElementsString2Float (String _percent)
    {
        return Float.parseFloat(_percent) / 100;
    }
}
