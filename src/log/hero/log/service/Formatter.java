/**
 * Copyright: Copyright (c) 2008 <br>
 * Company: Digifun <br>
 * Date: 2008-6-6
 */
package hero.log.service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Description:<br>
 * 
 * @author Johnny
 * @version 0.1
 */
public class Formatter
{
    private static String                 L_BRACKET     = "[", R_BRACKET = "]";

    private StringBuffer                  logBuffer     = new StringBuffer();

    private static final SimpleDateFormat dateFormatter = (SimpleDateFormat) SimpleDateFormat
                                                                .getDateTimeInstance();

    private int                           count         = 0;

    public Formatter()
    {
        dateFormatter.applyPattern("yyyy-MM-dd HH:mm:ss:SS");
    }

    /**
     * 清空数据
     */
    public Formatter reset ()
    {
        count = 0;
        logBuffer.delete(0, logBuffer.length());
        return this;
    }

    /**
     * 添加数据。内部将拼接成[_logUnit]的格式
     * 
     * @param _logUnit
     * @return
     */
    public Formatter append (String _logUnit)
    {
        count++;
        logBuffer.append(L_BRACKET).append(_logUnit).append(R_BRACKET);
        return this;
    }

    public Formatter append (int _logUnit)
    {
        return append(String.valueOf(_logUnit));
    }

    public Formatter append (float _logUnit)
    {
        return append(String.valueOf(_logUnit));
    }

    public Formatter append (double _logUnit)
    {
        return append(String.valueOf(_logUnit));
    }

    public Formatter append (short _logUnit)
    {
        return append(String.valueOf(_logUnit));
    }

    public Formatter append (Date _date)
    {
        return append(dateFormatter.format(_date));
    }

    /**
     * 默认使用info记录日志数据
     */
    public String flush ()
    {
        // if (count != getUnitCount())
        // {
        // log.info(this.getClass().getName() + " 数据不全。已生成的数据数量："
        // + count + "，需要数量：" + getUnitCount());
        // // logger.warn(this.getClass().getName() + " 数据不全。已生成的数据数量：" + count
        // // + "，需要数量：" + getUnitCount());
        // }
        // logger.info(logBuffer.toString());
        return logBuffer.toString();
    }

    /**
     * 已记录的日志数据数量
     * 
     * @return
     */
    public int getCount ()
    {
        return count;
    }
}
