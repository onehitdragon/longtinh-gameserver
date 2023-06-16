package hero.share.service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 DateFormatter.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-18 上午11:47:46
 * @描述 ：日期格式化工具类
 */

public class DateFormatter
{
    /**
     * 格式化器
     */
    private static SimpleDateFormat dateFormatter = (SimpleDateFormat) SimpleDateFormat
                                                          .getDateTimeInstance();

    /**
     * 私有构造
     */
    private DateFormatter()
    {
    }

    /**
     * 当前时间格式化
     * 
     * @return
     */
    public static String currentTime ()
    {
        return dateFormatter.format(new Date());
    }
    
    /**
     * 获得指定格式的日期字符串
     * @param _format
     * @param _date
     * @return
     */
    public static String getStringTime (String _format, Date _date)
    {
    	String date = "";
    	SimpleDateFormat formatter = new SimpleDateFormat (_format);
    	if(_date != null) 
    	{
    		date = formatter.format(_date.getTime());    		
    	}
    	return date;
    }

    /**
     * 格式化
     * 
     * @return
     */
    public static String format (Date _date)
    {
        return dateFormatter.format(_date);
    }
}
