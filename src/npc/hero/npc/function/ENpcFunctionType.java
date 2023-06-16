package hero.npc.function;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ENpcFunctionType.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-17 下午05:42:12
 * @描述 ：
 */

public enum ENpcFunctionType
{
    /**
     * 任务
     */
    TASK(1),

    /**
     * 买卖
     */
    TRADE(2),

    /**
     * 装备修理
     */
    REPAIR(3),

    /**
     * 物品交换
     */
    EXCHANGE(4),

    /**
     * 地图传送
     */
    TRANSMIT(5),

    /**
     * 拍卖
     */
    AUCTION(6),

    /**
     * 仓库
     */
    STORAGE(7),

    /**
     * 邮箱
     */
    POST_BOX(8),

    /**
     * 技能训练
     */
    SKILL_EDUCATE(9),

    /**
     * 公会管理
     */
    GUILD_MANAGE(10),

    /**
     * 兵器谱
     */
    WEAPON_RECORD(11),

    /**
     * 灵魂采集训练
     */
    GATHER_NPC(12),

    /**
     * 物品制造训练
     */
    MANUF_NPC(13),

    /**
     * 婚姻系统－大榕树
     */
    LOVER_TREE(14),

    /**
     * 婚姻系统－幸福使者
     */
    MARRY_NPC(15),

    /**
     * 婚姻系统－婚礼主持
     */
    WEDDING(16),

    /**
     * 副本传送
     */
    DUNGEON_TRANSMIT(17),

    /**
     * 转职
     */
    CHANGE_VOCATION(18),
    
    /**
     * 答题功能
     */
    ANSWER_QUESTION(19),  /*add by zhengl; date: 2011-03-30; note: 添加答题功能*/
    
    /**
     * 凭输入验证码领取奖励功能
     */
    EVIDENVE_GET_GIFT(20); /*add by zhengl; date: 2011-04-07; note: 凭输入验证码领取奖励功能*/

    /**
     * 类型值
     */
    private int value;

    ENpcFunctionType(int _value)
    {
        value = _value;
    }

    /**
     * 获取类型值
     * 
     * @return
     */
    public int value ()
    {
        return value;
    }

    /**
     * 根据类型值获取功能类型
     * 
     * @param _value
     * @return
     */
    public static ENpcFunctionType getType (int _value)
    {
        for (ENpcFunctionType type : ENpcFunctionType.values())
        {
            if (type.value() == _value)
            {
                return type;
            }
        }

        return null;
    }
}
