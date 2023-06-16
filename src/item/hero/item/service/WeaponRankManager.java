package hero.item.service;

import hero.item.Weapon;
import hero.item.dictionary.WeaponDict;
import hero.share.service.LogWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import yoyo.service.YOYOSystem;
import yoyo.service.tools.database.DBServiceImpl;

import javolution.util.FastList;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MapModelData.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-10-20 下午03:44:39
 * @描述 ：武器排名管理器
 */

public class WeaponRankManager
{
    /**
     * 排名列表
     */
    private FastList<WeaponRankUnit> rankList;

    /**
     * 排名刷新计时器
     */
    private Timer                    rankTimer;

    /**
     * 单例
     */
    private static WeaponRankManager instance;

    /**
     * 私有构造
     */
    private WeaponRankManager()
    {
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static WeaponRankManager getInstance ()
    {
        if (instance == null)
        {
            instance = new WeaponRankManager();
            instance.init();
        }

        return instance;
    }

    /**
     * 初始化
     */
    private void init ()
    {
        rankList = new FastList<WeaponRankUnit>();

        FileInputStream fis = null;

        try
        {
            GregorianCalendar fixedRefreshTime = new GregorianCalendar();
            fixedRefreshTime.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            fixedRefreshTime.set(Calendar.HOUR_OF_DAY, 3);
            fixedRefreshTime.set(Calendar.MINUTE, 0);
            fixedRefreshTime.set(Calendar.SECOND, 0);

            File configFile = new File(CONFIG_FILE);

            if (!configFile.exists())
            {
                configFile.createNewFile();
                reRank();

                return;
            }

            fis = new FileInputStream(configFile);
            Properties property = new Properties();
            property.load(fis);
            String lastRefreshDate = property.getProperty("Refresh_Time");

            if (null != lastRefreshDate)
            {
                Date refreshDate = DATE_FORMATTER.parse(lastRefreshDate);
                property.clear();

                GregorianCalendar lastRefreshTime = new GregorianCalendar();
                lastRefreshTime.setTime(refreshDate);

                if (fixedRefreshTime.before(lastRefreshTime))
                {
                    loadRankFromDB();
                }
                else
                {
                    reRank();
                }
            }
            else
            {
                reRank();
            }

            rankTimer = new Timer();
            fixedRefreshTime.add(Calendar.DAY_OF_MONTH, 7);
            rankTimer.schedule(new WeaponRerankTask(), fixedRefreshTime
                    .getTimeInMillis()
                    - new GregorianCalendar().getTimeInMillis(), 7 * 24 * 60
                    * 60 * 1000);
        }
        catch (Exception e)
        {
            LogWriter.error(this, e);
        }
        finally
        {
            if (fis != null)
            {
                try
                {
                    fis.close();
                    fis = null;
                }
                catch (Exception ioe)
                {

                }
            }
        }
    }

    /**
     * 加载排行榜
     */
    private void loadRankFromDB ()
    {
        Connection conn = null;
        PreparedStatement stam = null;
        ResultSet rs = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            stam = conn.prepareStatement(SELECT_RANK_SQL);
            stam.setInt(1, MAX_RANK_NUMBER);
            rs = stam.executeQuery();

            Weapon weapon;

            while (rs.next())
            {
                int equipmentID = rs.getInt("equipment_id");
                boolean existSeal = rs.getBoolean("exist_seal");
                String ownerName = rs.getString("owner_name");
                String genericEnhanceDesc = rs
                        .getString("generic_enhance_desc");
                String bloodyEnhanceDesc = rs.getString("bloody_enhance_desc");

                weapon = WeaponDict.getInstance().getWeapon(equipmentID);

                if (null != weapon)
                {
                    rankList.add(new WeaponRankUnit(weapon, ownerName,
                            genericEnhanceDesc, bloodyEnhanceDesc, existSeal));
                }
            }
        }
        catch (Exception e)
        {
            LogWriter.error(this, e);
        }
        finally
        {
            try
            {
                if (null != rs)
                {
                    rs.close();
                    rs = null;
                }
                if (null != stam)
                {
                    stam.close();
                    stam = null;
                }
                if (null != conn)
                {
                    conn.close();
                    conn = null;
                }
            }
            catch (Exception e)
            {
            }
        }
    }

    /**
     * 重新排名（1、将数据表所有记录改为过期 2、从装备实例表中重新加载数据并排名 3、将新的排名数据插入到数据库 4、更改配置文件中的更新时间）
     */
    private void reRank ()
    {
        Connection conn = null;
        PreparedStatement stam = null;
        ResultSet rs = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            stam = conn.prepareStatement(UPDATE_OVERDUE_RANK_SQL);
            stam.executeUpdate();

            rankList.clear();
            Weapon weapon;

            rs = stam.executeQuery(SELECT_WEAPON_SQL);

            while (rs.next())
            {
                int equipmentID = rs.getInt("equipment_id");
                byte existSeal = rs.getByte("be_sealed");
                String genericEnhanceDesc = rs
                        .getString("generic_enhance_desc");
                String bloodyEnhanceDesc = rs.getString("bloody_enhance_desc");
                String ownerName = rs.getString("nickname");

                weapon = WeaponDict.getInstance().getWeapon(equipmentID);

                if (weapon != null)
                {
                    sort(new WeaponRankUnit(weapon, ownerName,
                            genericEnhanceDesc, bloodyEnhanceDesc,
                            existSeal == 1 ? true : false));
                }
            }

            if (rankList.size() > 0)
            {
                stam.close();
                stam = null;
                conn.setAutoCommit(false);
                stam = conn.prepareStatement(INSERT_RANK_SQL);

                for (WeaponRankUnit rank : rankList)
                {
                    stam.setInt(1, rank.weapon.getID());
                    stam.setString(2, rank.ownerName);
                    stam.setString(3, rank.genericEnhanceDesc);
                    stam.setString(4, rank.bloodyEnhanceDesc);
                    stam.setBoolean(5, rank.existSeal);

                    stam.addBatch();
                }

                stam.executeBatch();
                conn.setAutoCommit(true);
            }

            FileOutputStream fw = new FileOutputStream(CONFIG_FILE);
            Properties property = new Properties();
            property.setProperty("Refresh_Time", DATE_FORMATTER
                    .format(new Date()));
            property.store(fw, null);
            property.clear();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (stam != null)
                    stam.close();
                if (conn != null)
                    conn.close();
            }
            catch (Exception e)
            {
            }
        }

    }

    /**
     * 排序插入
     * 
     * @param _rank
     */
    private void sort (WeaponRankUnit _rank)
    {
        if (_rank.bloodyEnhance.getPveLevel() >= 1
                || _rank.bloodyEnhance.getPvpLevel() >= 1)
        {
            for (int i = 0; i < rankList.size(); i++)
            {
                if (rankList.get(i).score < _rank.score)
                {
                    rankList.add(i, _rank);

                    return;
                }
            }

            rankList.add(_rank);
        }
    }

    /**
     * 得到兵器谱排名列表
     * 
     * @return
     */
    public FastList<WeaponRankUnit> getRankList ()
    {
        return rankList;
    }

    /**
     * @author DC 武器重排名任务
     */
    class WeaponRerankTask extends TimerTask
    {
        @Override
        public void run ()
        {
            Calendar now = Calendar.getInstance();

            if (now.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
                    && now.get(Calendar.HOUR_OF_DAY) == 3)
            {
                reRank();
            }
        }
    }

    /**
     * 配置文件
     */
    private final static String     CONFIG_FILE             = YOYOSystem.HOME
                                                                    + File.separator
                                                                    + "res"
                                                                    + File.separator
                                                                    + "config"
                                                                    + File.separator
                                                                    + "WeaponRankTime.txt";

    /**
     * 插入武器排名脚本
     */
    private static final String     INSERT_RANK_SQL         = "INSERT INTO weapon_rank(equipment_id,owner_name,"
                                                                    + "generic_enhance_desc,bloody_enhance_desc,exist_seal)"
                                                                    + " VALUES (?,?,?,?,?)";

    /**
     * 加载武器排名脚本
     */
    private static final String     SELECT_RANK_SQL         = "SELECT equipment_id,exist_seal,owner_name,"
                                                                    + "generic_enhance_desc,bloody_enhance_desc"
                                                                    + " FROM weapon_rank WHERE overdue = 0"
                                                                    + " ORDER BY id ASC LIMIT ?";

    /**
     * 更新过期标记
     */
    private static final String     UPDATE_OVERDUE_RANK_SQL = "UPDATE weapon_rank SET overdue = 1;";

    /**
     * 查询有血腥强化属性的武器实例
     */
    private static final String     SELECT_WEAPON_SQL       = "SELECT ei.equipment_id,ei.be_sealed,"
                                                                    + "ei.generic_enhance_desc,"
                                                                    + "ei.bloody_enhance_desc,p.nickname FROM "
                                                                    + "equipment_instance ei,player p WHERE "
                                                                    + "ei.bloody_enhance_desc NOT LIKE '' AND "
                                                                    + "p.user_id = ei.owner_user_id";

    /**
     * 排名显示的最大数量
     */
    private static final int        MAX_RANK_NUMBER         = 50;

    /**
     * 时间格式化器
     */
    private static SimpleDateFormat DATE_FORMATTER;

    static
    {
        DATE_FORMATTER = (SimpleDateFormat) SimpleDateFormat
                .getDateTimeInstance();
        DATE_FORMATTER.applyPattern("yy-MM-dd HH:mm:ss");
    }
}
