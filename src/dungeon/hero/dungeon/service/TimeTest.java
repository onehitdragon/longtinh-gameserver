package hero.dungeon.service;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 TimeTest.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-8-5 下午03:32:44
 * @描述 ：
 */

public class TimeTest
{
    public static void main (String[] args)
    {
        Calendar calendar = new GregorianCalendar(2008, 5, 5, 16, 8, 0);

        Calendar calendar2 = new GregorianCalendar(2008, 5, 1, 13, 13, 0);

        DateFormat df = DateFormat.getDateTimeInstance();
        String backupFileName = df.format(calendar.getTime());
        String backupFileName1 = df.format(calendar2.getTime());

        int days = (int) ((calendar.getTimeInMillis() - calendar2
                .getTimeInMillis()) / (24 * 60 * 60 * 1000));
        int hour = (int) ((calendar.getTimeInMillis() - calendar2
                .getTimeInMillis()) % (24 * 60 * 60 * 1000))
                / (60 * 60 * 1000);
        int minute = (int) ((calendar.getTimeInMillis() - calendar2
                .getTimeInMillis()) % (24 * 60 * 60 * 1000))
                % (60 * 60 * 1000) / (60 * 1000);

        System.out.println("距离重置" + days + "天" + hour + "小时" + minute + "分");

        // if (DungeonHistoryManager.getInstance()
        // .getRaidDungeonRefreshTime().get(Calendar.YEAR) != now
        // .get(Calendar.YEAR))
        // {
        // days = now.getActualMaximum(java.util.Calendar.DAY_OF_YEAR)
        // - now.get(Calendar.DAY_OF_YEAR)
        // + DungeonHistoryManager.getInstance()
        // .getRaidDungeonRefreshTime().get(
        // Calendar.DAY_OF_YEAR);
        // }
        // else
        // {
        // days = DungeonHistoryManager.getInstance()
        // .getRaidDungeonRefreshTime().get(
        // Calendar.DAY_OF_YEAR)
        // - now.get(Calendar.DAY_OF_YEAR);
        // }
    }
}
