package hero.log.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTime
{

    private static Locale     currentLocale = new Locale("zh", "CN");

    public static final int   DEFAULT       = DateFormat.DEFAULT;

    public static final int   SHORT         = DateFormat.SHORT;

    public static final int   MEDIUM        = DateFormat.MEDIUM;

    public static final int   LONG          = DateFormat.LONG;

    public static final int   FULL          = DateFormat.FULL;

    private static String     result;

    private static DateFormat formatter;

    public DateTime()
    {
    }

    public DateTime(Locale currentLocale)
    {
        DateTime.currentLocale = currentLocale;
    }

    /*
     * ----------- 显示日期 ------------- 按五个字段依次显示实例 2006-8-21 06-8-21 2006-8-21
     * 2006年8月21日 2006年8月21日 星期一 --------------------------------
     */
    public static String showDateStyles (int style)
    {
        formatter = DateFormat.getDateInstance(style, currentLocale);
        result = formatter.format(new Date());
        return result;
    }

    /*
     * ----------- 显示时间 ------------- 按五个字段依次显示实例 20:16:55 下午8:16 20:16:55
     * 下午08时16分55秒 下午08时16分55秒 CST --------------------------------
     */
    public static String showtimeStyles (int style)
    {
        formatter = DateFormat.getTimeInstance(style, currentLocale);
        result = formatter.format(new Date());
        return result;
    }

    /*
     * -------- 显示时间和日期 ---------- 按五个字段依次显示实例 2006-8-21 20:16:55 06-8-21 下午8:16
     * 2006-8-21 20:16:55 2006年8月21日 下午08时16分55秒 2006年8月21日 星期一 下午08时16分55秒 CST
     * --------------------------------
     */
    public static String showBothStyles (int style)
    {
        formatter = DateFormat.getDateTimeInstance(style, style, currentLocale);
        result = formatter.format(new Date());
        return result;
    }

    public static String getCurrentTime ()
    {
        formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        result = formatter.format(new Date());
        return result;
    }

    public static String getCurrentTime2 ()
    {
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        result = formatter.format(new Date());
        return result;
    }

    public static String getTime (long time)
    {
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        result = formatter.format(c.getTime());
        return result;
    }

    public static void main (String[] args)
    {
    }
}
