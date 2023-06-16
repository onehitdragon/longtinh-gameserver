package hero.share.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import yoyo.service.tools.database.DBServiceImpl;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-10-15 上午11:35:40
 * 
 * <pre>
 *      Description:
 * </pre>
 */

public class IDManager
{
    /**
     * 所有编号的初始值
     */
    private static final int MIN_ID  = 1000;

    /**
     * 编号是否初始化的标记
     */
    private static boolean   INITED  = false;

    /**
     * 游戏对象当前JVM临时编号（玩家、怪物、NPC）
     */
    private static int       OBJECT_ID;

    /**
     * 客户端与服务器通讯的会话编号
     */
    private static int       SESSION_ID;

    /**
     * 怪物掉落箱子编号
     */
    private static int       MONSTER_LEGACY_BOX_ID;

    /**
     * 装备实例的永久编号
     */
    private static int       EQUIPMENT_INS_ID;

    /**
     * 组队ID
     */
    private static int       GROUP_ID;

    /**
     * 最大正整数
     */
    private static final int MAX_INT = 0x7FFFFFFF;

    /**
     * 初始化所有编号
     */
    public static void init ()
    {
        if (!INITED)
        {
            OBJECT_ID = MIN_ID;
            SESSION_ID = MIN_ID;
            EQUIPMENT_INS_ID = MIN_ID;
            MONSTER_LEGACY_BOX_ID = MIN_ID;
            GROUP_ID = 0;
            initEuipmentInsID();

            INITED = true;
        }
    }

    /**
     * 创建新的装备实例永久编号
     * 
     * @return 新的装备实例永久编号
     */
    public synchronized static int buildEquipmentInsID ()
    {
        if (!INITED)
        {
            init();
        }

        return EQUIPMENT_INS_ID++;
    }

    /**
     * 创建新的游戏对象临时编号
     * 
     * @return 新的对象临时编号
     */
    public static final int buildObjectID ()
    {
        if (!INITED)
        {
            init();
        }

        return ++OBJECT_ID;
    }

    /**
     * 创建怪物掉落的箱子编号
     * 
     * @return
     */
    public static final int buildMonsterLegacyBoxID ()
    {
        if (!INITED)
        {
            init();
        }

        return ++MONSTER_LEGACY_BOX_ID;
    }

    /**
     * 创建新的会话编号
     * 
     * @return 新的会话编号
     */
    public synchronized static int buildSessionID ()
    {
        if (!INITED)
        {
            init();
        }

        if (SESSION_ID >= Integer.MAX_VALUE)
            SESSION_ID = MIN_ID;

        return ++SESSION_ID;
    }

    /**
     * 初始化装备实例编号
     */
    private static void initEuipmentInsID ()
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn
                    .prepareStatement("select max(instance_id) as max_id from equipment_instance");
            rs = pstm.executeQuery();

            if (rs.next())
            {
                int maxEuipmentInsID = rs.getInt("max_id");

                if (maxEuipmentInsID > 0)
                {
                    EQUIPMENT_INS_ID = ++maxEuipmentInsID;
                }
                else
                {
                    EQUIPMENT_INS_ID = MIN_ID;
                }
            }
            else
            {
                EQUIPMENT_INS_ID = MIN_ID;
            }
        }
        catch (Exception e)
        {
            LogWriter.error(null, e);
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                }
                if (pstm != null)
                {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null)
                {
                    conn.close();
                    conn = null;
                }
            }
            catch (SQLException e)
            {
            }
        }
    }

    /**
     * 得到队伍ID 1～7FFFFFFFH
     * 
     * @return
     */
    public synchronized static int buildGroupID ()
    {
        if (GROUP_ID + 1 > MAX_INT)
        {
            GROUP_ID = 0;
        }
        return ++GROUP_ID;
    }
}
