package hero.player.define;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2005
 * </p>
 * 
 * @file Clan.java
 * @author Ding Chu
 * @version 1.0
 * @date 2008-10-9 下午04:06:50
 * 
 * <pre>
 *      Description:
 * </pre>
 */
public enum EClan
{
    /**
     * 龙之传人(原龙山)
     */
    LONG_SHAN((short) 1, "龙之传人")
    {
    },
    /**
     * 恶魔之子(原河姆渡)
     */
    HE_MU_DU((short) 2, "恶魔之子")
    {
    },

    /**
     * 无
     */
    NONE((short) 0, "ALL")
    {
    };

    /**
     * 获取阵营
     * 
     * @param _id 阵营编号
     * @return
     */
    public static EClan getClan (int _id)
    {
        for (EClan c : EClan.values())
        {
            if (c.getID() == _id)
            {
                return c;
            }
        }

        return NONE;
    }
    
    /**
     * 获得枚举数组
     * @return
     */
    public static EClan[] getValues() {
    	EClan[] clans = {EClan.HE_MU_DU, EClan.LONG_SHAN};
    	return clans;
    }

    /**
     * 获取阵营
     * 
     * @param _name 阵营名称
     * @return
     */
    public static EClan getClanByDesc (String _name)
    {
        if (null == _name)
        {
            return NONE;
        }

        for (EClan c : EClan.values())
        {
            if (c.getDesc().equalsIgnoreCase(_name))
            {
                return c;
            }
        }

        return NONE;
    }

    /**
     * 阵营编号
     */
    private short  id;

    /**
     * 阵营描述
     */
    private String desc;

    /**
     * 构造
     * 
     * @param _id
     */
    EClan(short _id, String _desc)
    {
        id = _id;
        desc = _desc;
    }

    /**
     * 获取阵营编号
     * 
     * @return
     */
    public short getID ()
    {
        return id;
    }

    /**
     * 获取阵营描述
     * 
     * @return
     */
    public String getDesc ()
    {
        return desc;
    }
}
