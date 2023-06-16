package hero.share;

public class Constant
{
    /**
     * 短整型最大值
     */
    public static final short SHORT_MAX_VALUE           = 32760;

    /**
     * 整型最大值
     */
    public static final int   INTEGER_MAX_VALUE         = 1000000000;

    /**
     * 低端客户端
     */
    public static final short CLIENT_OF_LOW_SIDE        = 1;

    /**
     * 中端客户端 
     * //del by zhengl date: 2011-05-06; note: 客户端已经不再使用该值.统一使用:CLIENT_OF_HIGH_SIDE
     * 减少MapSynchronousInfoBroadcast.infoList 的大小,较少for循环次数
     */
//    public static final short CLIENT_OF_MIDDLE_SIDE     = 2;

    /**
     * 高端客户端
     */
    public static final short CLIENT_OF_HIGH_SIDE       = 3;

    /**
     * 恢复生命值和魔法值的间隔时间（毫秒）
     */
    public static final int   INTERVAL_OF_RESUME_ENERGY = 3 * 1000;

    /**
     * PK中脱离战斗的判断时间（毫秒），指互相没有攻击（无视DOT伤害）
     */
    public static final int   DISENGAGE_FIGHT_TIME      = 15 * 1000;

    /***************************************************************************
     * NPC，地图、图片存储位置
     **************************************************************************/

    /**
     * 游戏中引用到的非代码文字的中文编码
     */
    public static String      CHINESE_ENCODING;

    // 心跳执行间隔时间 毫秒(Heartbeat)
    public static int         HEARTBEAT_INTERVAL;

    /***************************************************************************
     * 客户端聊天信息显示颜色
     **************************************************************************/

    // 系统消息字体颜色(ChatService)
    public static int         COLOR_SYSTEM;

    // 世界频道字体颜色 (ChatService)
    public static int         COLOR_WORLD;

    // 公会频道字体颜色(ChatService)
    public static int         COLOR_CONSORTIA;

    // 私聊字体颜色(ChatService)
    public static int         COLOR_PERSIONAL;

    // 队伍聊天字体颜色(ChatService)
    public static int         COLOR_TEAM;

    // 地图聊天字体颜色(ChatService)
    public static int         COLOR_MAP;

    /***************************************************************************
     * 初始角色的衣服图片编号
     **************************************************************************/

    /**
     * 缺省僵值时间（毫秒）
     */
    public static final short DEFAULT_IMMOBILITY_TIME   = 1;

    /**
     * 空手物理攻击距离
     */
    public static final short DEFAULT_ATTACK_DISTANCE   = 3;

    /**
     * 修正攻击距离
     */
    public static final short BALANCE_ATTACK_DISTANCE   = 2;

    /**
     * 地图格子尺寸（像素）
     */
    public static final short MAP_GRID_PIXELS_SIZE      = 16;

    /**
     * 服务器编号
     */
    public static int         SERVER_ID;

    // 物品类型编号分界（装备和药水物品）（ItemService）
    public static int         GOODS_ID_BOUNDARY;

    // TODO 客户端无请求决定的角色生命周期（毫秒）（SessionService）
    public static int         CHARACTER_LIFECYLE;

    // NPC跟随执行间隔时间（NpcService）
    public static int         FOLLOW_INTERVAL           = 1000;

    public static boolean isSkillUnit (int _skillUnitID)
    {
        return _skillUnitID > 10000 && _skillUnitID < 99999;
    }

    public static boolean isEffectUnit (int _effectUnitID)
    {
        return _effectUnitID > 100000 && _effectUnitID < 999999;
    }
}