package hero.ui;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 UI_Type.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-11 下午04:51:16
 * @描述 ：
 */

public enum EUIType
{
    /**
     * NPC第一次交互
     */
    NPC_HANDSHAKE((byte) 1),
    /**
     * 操作选择
     */
    SELECT((byte) 2),

    /**
     * 确认框
     */
    CONFIRM((byte) 3),

    /**
     * 输入数字
     */
    INPUT_STRING((byte) 4),

    /**
     * 输入文字
     */
    INPUT_DIGITAL((byte) 5),

    /**
     * 物品列表
     */
    GOODS_OPERATE_LIST((byte) 6),

    /**
     * 整屏内容框
     */
    TIP((byte) 7),

    /**
     * 技能列表
     */
    SKILL_LIST((byte) 8),

    /**
     * 某格子的物品数量发生变化
     */
    GRID_CHANGED((byte) 9),
    /**
     * 拍卖行物品列表
     */
    AUCTION_GOODS_LIST((byte) 10),

    /**
     * 邮箱物品列表
     */
    MAIL_GOODS_LIST((byte) 11),
    /**
     * 拍卖行物品列表
     */
    TASK_CONTENT((byte) 12),

    /**
     * 队伍查看玩家
     */
    GROUP_VIEW((byte) 13),

    /**
     * 交换物品列表
     */
    EXCHANGE_ITEM_LIST((byte) 14),

    /**
     * 交换物品需要材料列表
     */
    EXCHANGE_MATERIAL_LIST((byte) 15),

    /**
     * 兵器谱排行
     */
    WEAPON_RECORD_LIST((byte) 16),

    /**
     * 公会列表
     */
    GUILD_LIST((byte) 17),

    /**
     * 辅助技能列表
     */
    ASSIST_SKILL_LIST((byte) 18),

    /**
     * 带提示的
     */
    SELECT_WITH_TIP((byte) 19),
    
    /**
     * 问答功能
     */
    ANSWER_QUESTION((byte) 20),
    
    /**
     * 凭证领取功能
     */
    EVIDENVE_RECEIVE((byte) 21),
    
    TIP_UI((byte) 22);

    private byte id;

    EUIType(byte _id)
    {
        id = _id;
    }

    public byte getID ()
    {
        return id;
    }
}
