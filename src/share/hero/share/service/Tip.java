package hero.share.service;

public class Tip {
	
	//public use
    public static final String TIP_PUBLIC_BUSY                 = "玩家正忙";
    
    public static final String TIP_PUBLIC_OF_NOT_HAVE_ITEM_TO_CHARGE = "没有%fn,是否去商城购买";
    
    public static final String TIP_PUBLIC_OF_FAIL_CONTINU = "操作失败,需要劳烦您再试.";
	
    
    //chat
    /**
     * 被私聊目标屏蔽提示
     */
    public final static String TIP_CHAT_OF_BLACK               = "你被人家拉黑了";
    
    /**
     * 私聊目标不在线提示
     */
    public final static String TIP_CHAT_OF_TARGET_OFFLINE      = "这家伙好像不在啊";
    
    public final static String TIP_CHAT_OF_USE_END  = "突破喊话限制,使用了1个%fn";
    /**
     * 提示世界聊天等待
     */
    public final static String TIP_CHAT_OF_WORLD_CHANNEL_WAIT  = "说得这么快一定很累，休息3分钟再发言吧";
    /**
     * 提示种族聊天等待
     */
    public final static String TIP_CHAT_OF_CLAN_CHANNEL_WAIT   = "说得这么快一定很累，休息30秒再发言吧";
    
    public final static String TIP_CHAT_OF_NOT_HAVE_GUILD      = "你还没有加入任何帮派";
    
    public final static String TIP_CHAT_OF_SHUT_UP = "你已经被禁言！";
    
    public final static String TIP_CHAT_OF_NOT_TARGET = "无效的目标或玩家已下线";
    
    public final static String TIP_CHAT_OF_NOT_TEAM = "队伍不存在";
    
    
    
    public final static String TIP_ITEM_REPAIR = "花费了%fmoney金";
    

    public final static String TIP_ITEM_COMM_BAG_IS_FULL  = "%fn背包没地儿了呀";
    /**
     * 装备背包已满
     */
    public final static String TIP_ITEM_EQUIPMENT_BAG_IS_FULL  = "装备背包没地儿了呀";
    /**
     * 药品背包已满
     */
    public final static String TIP_ITEM_MEDICAMENT_BAG_IS_FULL = "药品背包没地儿了呀";
    /**
     * 材料背包已满
     */
    public final static String TIP_ITEM_MATERIAL_BAG_IS_FULL   = "材料背包没地儿了呀";
    /**
     * 任务道具背包已满
     */
    public final static String TIP_ITEM_TASK_TOOL_BAG_IS_FULL     = "任务背包没地儿了呀";
    
    /**
     * 特殊物品背包已满
     */
    public final static String TIP_ITEM_SPECIAL_GOODS_BAG_IS_FULL = "宝物背包没地儿了呀";
    
    public final static String TIP_ITEM_PET_CONTAIN_IS_FULL       = "每个玩家最多只能拥有 ";
    
    /**
     * 提交的兑换物品编号错误
     */
    public final static String TIP_NPC_NONE_GOODS                 = "这个，嗯，你懂的";
    /**
     * 在网络延时下的重复兑换导致材料不足的提示
     */
    public final static String TIP_NPC_NOT_ENOUGH_MATERIAL            = "材料不足";
    /**
     * 选择要去的地图提示
     */
    public final static String TIP_NPC_OF_SELECT_DIST_MAP  = "嘿，想去哪里请选择吧";
    /**
     * 等级不够的提示信息
     */
    public final static String TIP_NPC_OF_NEED_LEVEL       = "需要等级 ";
    /**
     * 金钱不够的提示信息
     */
    public final static String TIP_NPC_OF_NOT_ENOUGH_MONEY = "您的金钱不够";
    /**
     * 已死亡不能传送提示信息
     */
    public final static String TIP_NPC_OF_DIE              = "成了鬼魂就不要传来传去吓人了";
    
    
    //skill
	/**
	 * 没有技能可被遗忘时的提示
	 */
    public final static String TIP_SKILL_OF_NONE_SKILL_BE_FORGET = "没有可以遗忘的技能";
	/**
	 * 更新数据库时失败的提示
	 */
    public static final String TIP_SKILL_OF_DB_OPERATE = "嗯，这个，你懂的";
	/**
	 * 生命值不够的提示
	 */
    public static final String TIP_SKILL_OF_NOT_ENOUGH_HP = "生命不足";
	/**
	 * 气值不够的提示
	 */
    public static final String TIP_SKILL_OF_NOT_ENOUGH_GP = "气值不足";
	/**
	 * 力值不够的提示
	 */
    public static final String TIP_SKILL_OF_NOT_ENOUGH_FP = "力值不足";
	/**
	 * 法力值不够的提示
	 */
    public static final String TIP_SKILL_OF_NOT_ENOUGH_MP = "魔法不足";
	/**
	 * 需要自身不在存在某个效果的提示
	 */
    public static final String TIP_SKILL_OF_NEED_TARGET_NONE_EFFECT = "需要目标无：";
	/**
	 * 需要自身存在某个效果的提示
	 */
    public static final String TIP_SKILL_OF_NEED_TARGET_EFFECT = "需要目标效果：";
	/**
	 * 需要自身不在存在某个效果的提示
	 */
    public static final String TIP_SKILL_OF_NEED_SELF_NONE_EFFECT = "需要自身无：";
	/**
	 * 需要自身存在某个效果的提示
	 */
    public static final String TIP_SKILL_OF_NEED_SELF_EFFECT = "需要自身效果：";
	/**
	 * 距离过远提示
	 */
    public static final String TIP_SKILL_OF_FARAWAY = "您这离得也太远了";
	/**
	 * 当某个条件不满足时无法施放技能的提示
	 */
    public static final String TIP_SKILL_OF_CANNOT_RELEASE = "未满足技能施放条件";
    
    public static final String TIP_SKILL_OF_NOT_HAVE_RINSE_SKILL = "没有%fn,是否去商城购买";
    
    public static final String TIP_SKILL_OF_NOT_HAVE_SKILL = "您还未学习技能,不需要洗点";
	/**
	 * 施放技能时需要武器的提示
	 */
    public static final String TIP_SKILL_OF_NEED_WEAPON = "您手中的武器无法使用该技能";
	/**
	 * 战斗中无法使用的技能的提示
	 */
    public static final String TIP_SKILL_OF_UNUSABLE_IN_FIGHTING = "打架的时候可不能用这个";
	/**
	 * 施放技能时技能未冷却的提示
	 */
    public static final String TIP_SKILL_OF_SKILL_COOLDOWNING = "技能冷却中";
	/**
	 * 施放技能时无效目标的提示
	 */
    public static final String TIP_SKILL_OF_INVALIDATE_TARGET = "不要对这个目标有非分之想";
	/**
	 * 学习技能时，等级不够的提示
	 */
    public static final String TIP_SKILL_OF_NOT_ENOUGH_LEVEL = "您的等级不够";
	/**
	 * 学习技能时，技能点不够的提示
	 */
    public static final String TIP_SKILL_OF_NOT_ENOUGH_POINT = "您的技能点数不够";
	/**
	 * 学习技能时，金钱不够的提示
	 */
    public static final String TIP_SKILL_OF_NOT_ENOUGH_MONEY = "您的金钱不够";
    
    public static final String TIP_SKILL_OF_HIGHEST = "技能已达最高等级";
    
    
    
    
    //task
    public final static String TIP_TASK_PUSH_BAG_FULL = "非常遗憾，您%fn背包内已满，不能接收此次推荐的极品装备";
    
    public final static String TIP_TASK_PUSH_FEE_FAIL = "操作失败";
    
    public final static String TIP_TASK_PUSH_FAIL = "对不起，由于您的网络问题（或是您主动放弃了发送短信），您的发送请求不成功，请稍后再试。 已发送成功的短信我们会为您自动充值相应点数到您的账号。";
    /**
     * 目标描述前缀
     */
    public final static String TIP_TASK_DESC_PREFIX = "护送";
    
    public final static String TIP_TASK_POINT_FEE_FAIL = "获得信息失败,请使用CMWAP进行游戏.状态:";
    /**
     * 在
     */
    public final static String TIP_TASK_AT          = "在";
    /**
     * 分钟
     */
    public final static String TIP_TASK_MINUITE     = "分钟内完成";
    /**
     * 目标描述前缀
     */
    public final static String TIP_TASK_DESC_PREFIX_EXPLORE   = "探索";
    /**
     * 连接字符
     */
    public final static String TIP_TASK_CONNECTOR     = "的";
    /**
     * 目的地方位描述
     */
    public final static String[] TIP_TASK_LOCATION_LIST = {"西北", "西南", "东北", "东南" };
    /**
     * 空格
     */
    public final static String SPACE     = "　";
    /**
     * 数量分隔符
     */
    public final static String SEPATATOR = "/";
    /**
     * 目标描述前缀
     */
    public final static String DESC_PREFIX = "杀死";
    
    //ui
    public final static String PRICE_HEADER = "价格：";
    
    //gather
    /**
     * 技能等级的称号
     */
    public final static String[] GATHER_LEVEL_TITLE          = {
            "初学", "熟练", "精通", "大师", "至尊"                 };
    public static final String TIP_GATHER_GET_BASIC_SKILL              = "您学会了初级采集技能";
    
    public static final String TIP_GATHER_FORGET_SUCCESS              = "成功遗忘采集技能";
    
    public static final String TIP_GATHER_SKILL                            = "采集技能";
    
    public static final String TIP_GATHER_LVL_UP                      = "您升级到";
    
    public static final String TIP_GATHER_ALREADY_HIGH_LVL       = "您已经是顶级了";
    
    public static final String TIP_GATHER_ALREADY_LEARN                = "您已经习得了此技能";
    
    public static final String TIP_GATHER_LEARN                       = "您习得了";
    
    public static final String TIP_GATHER_SKILL_POINT_NOT_ENOUGH   = "您的技能点不够，需要";
    
    public static final String TIP_GATHER_MONEY_NOT_ENOUGH             = "金钱不够，需要";
    
    public static final String TIP_GATHER_NOT_STUDY_GATHER           = "您还没有学习过采集技能";
    
    public static final String TIP_GATHER_GORGET_SKILL                  = "您确定要遗忘吗？";
    
    //duel,决斗
    /**
     * 对方正忙
     */
    public static final String TIP_DUEL_OF_OTHER_SIDE_IS_BUSY           = "正忙";
    
    /**
     * 拒绝邀请
     */
    public static final String TIP_DUEL_OF_OTHER_SIDE_REFUSE            = "不屑于与你决斗";
    /**
     * 已处于决斗状态
     */
    public static final String TIP_DUEL_OF_NOT_MORE                     = "您已处于决斗状态";
    /**
     * 无效的邀请，当玩家不存在、离线、死亡等
     */
    public static final String TIP_DUEL_OF_INVALIDATE_TARGET            = "无效的决斗目标";
    /**
     * 自己离开决斗场地的提示
     */
    public static final String TIP_DUEL_OF_MYSELF_LEFT_SCENE            = "您逃出了决斗场地，决斗结束";
    /**
     * 邀请决斗的双方不在同张地图上
     */
    public static final String TIP_DUEL_OF_TWO_SIDE_LOCATION_INVALIDATE = "您与目标不在一起";
    /**
     * 被对方屏蔽的提示
     */
    public static final String TIP_DUEL_OF_BE_BLACK                     = "你被人家拉黑了";
    /**
     * 对方离开决斗场地的提示
     */
    public static final String TIP_DUEL_OF_OTHER_SIDE_LEFT_SCENE        = "对方逃出了决斗场地，决斗结束";
    /**
     * 战胜广播
     */
    public static final String TIP_DUEL_OF_WIN        = "在决斗中战胜了";
    /**
     * 拒绝提示
     */
    public static final String TIP_DUEL_OF_REFUSE     = "不屑于与你决斗";
    
    
    
    //dungeon,副本
    public static final String TIP_DUNGEON_OF_MEMBER_IN_DIFFICULT_PATTERN = "已有队员在困难难度中";
    
    public static final String TIP_DUNGEON_OF_HISTORY_HAPPEND             = "进度已产生";
    
    public static final String TIP_DUNGEON_OF_MEMBER_FULL                 = "副本中人数已满";
    
    public static final String TIP_DUNGEON_OF_OTHER_GROUP_USEING_HISTORY  = "另一个队伍在您的进度中";
    
    public static final String TIP_DUNGEON_OF_DIFFERENT_HISTORY           = "与队长进度不同";
    
    public static final String TIP_DUNGEON_OF_MEMBER_IN_EASY_PATTERN      = "已有队员在简单难度中";
    
    public static final String TIP_DUNGEON_OF_NOT_IN_TEAM                 = "在队伍中才可以进入";
    
    public static final String TIP_DUNGEON_OF_LEADER_NOT_IN = "队长进入后才能进入！";
    
    public static final String TIP_DUNGEON_OF_TEAM_INFO_FAIL = "获取队伍信息失败！";
    
    public static final String TIP_DUNGEON_OF_LEVEL_LACK = "你的等级不够";
    
    public static final String TIP_DUNGEON_OF_IN_BOSS = "首领战中无法进入";
    
    public static final String TIP_DUNGEON_OF_TEAM_WRONG = "队伍信息错误，不能进入！";
    
    public static final String TIP_DUNGEON_OF_MAP_DATA_WRONG = "副本数据错误，不能进入！";
    
    public static final String TIP_DUNGEON_OF_MAP_FULL = "礼堂人数已满";
    
    public static final String TIP_DUNGEON_OF_WEDDING_MAP_WRONG = "婚礼副本数据错误，不能进入！";
    
    /**
     * 传送提示
     */
    public static final String TIP_DUNGEON_TRANSMIT        = "秒后将被移出副本";
    
    
    //effect,持续性特效
	/**
	 * 免疫提示
	 */
    public static final String TIP_EFFECT_OF_IMMUNITY = "免疫";
	/**
	 * 低等级效果提示
	 */
    public static final String TIP_EFFECT_OF_LOWER_LEVEL = "存在更高等级的效果";
	/**
	 * 无效的目标提示
	 */
    public static final String TIP_EFFECT_OF_VALIDATE_TARGET = "无效的目标";
    
    
    //fight,攻击
    /**
     * 攻击距离过远提示
     */
    public static final String TIP_FIGHT_OF_ATTACK_RANGE_ENOUGH = "走近一点再出手吧";
    
    //group,队伍
    /**
     * 提示-队伍已满
     */
    public static final String TIP_GROUP_OF_GROUP_FULL          = "对不起，您的队伍人满为患了";
    /**
     * 提示-没有权限邀请队员
     * 只有队长或助手可以邀请队员
     */
    public static final String TIP_GROUP_OF_NOT_GRANT    = "您没有邀请权限";
    /**
     * 提示-被屏蔽
     */
    public static final String TIP_GROUP_OF_BE_BLACK           = "您被人家拉黑了";
    /**
     * 提示-拒绝邀请
     */
    public static final String TIP_GROUP_OF_REFUSE_INVITE      = "婉拒了您的邀请";
    /**
     * 提示-邀请者已离线
     */
    public static final String TIP_GROUP_OF_INVITOR_OFFLINE        = "邀请者已下线";
    /**
     * 提示-无法邀请死亡玩家
     */
    public static final String TIP_GROUP_OF_TARGET_IS_DEAD         = "无法邀请变成鬼魂的玩家";
    /**
     * 提示-自己已有队伍
     */
    public static final String TIP_GROUP_OF_MYSELF_IN_GROUP        = "您已有队伍";
    /**
     * 提示-对方已有队伍
     */
    public static final String TIP_GROUP_OF_TARGET_IN_GROUP        = "已有队伍";
    /**
     * 提示-目标已离线
     */
    public static final String TIP_GROUP_OF_TARGET_OFFLINE         = "玩家不在线";
    /**
     * 提示-不同的氏族
     */
    public static final String TIP_GROUP_OF_DIFFERENT_CLAN         = "不同的种族";
    
    public static final String TIP_GROUP_OF_HER_LEAVE = "无法转让给离线队员";
    
    public static final String TIP_GROUP_OF_AIDE_LIST_FULL = "最多只能有2个助手！";
    
    public static final String TIP_GROUP_OF_TEAM_FULL_NOT_POSITION = "该小队已满员，不能改变位置!";
    
    public static final String TIP_GROUP_OF_IN_THIS_TEAM = "已经在这个小队!";

    public static final String TIP_GROUP_NOT_MYSELF = "不能自己组自己";

    public static final String TIP_NOT_MYSELF = "不能是自己";
    
    
    //guild,帮会
    /**
     * 没有帮会的提示
     */
    public static final String TIP_GUILD_OF_NONE_GUILD           = "您没有帮派";
    /**
     * 邀请者已离线的提示
     */
    public static final String TIP_GUILD_OF_INVITOR_OFFLINE      = "邀请者已离线";
    /**
     * 拒绝加入帮会的提示
     */
    public static final String TIP_GUILD_OF_REFUCE_JOIN_GUILD    = "不屑于加入您的帮派";
    /**
     * 氏族不同提示
     */
    public static final String TIP_GUILD_OF_DEFFERENT_CLAN       = "该玩家与您种族不同";
    /**
     * 已有帮会提示
     */
    public static final String TIP_GUILD_OF_IN_GUILD             = "该玩家已有帮派";
    /**
     * 已有帮会提示
     */
    public static final String TIP_GUILD_OF_IN_GUILD_TO_YOU             = "您已有帮派";

    public static final String TIP_GUILD_OF_NOT_HAVE_BUILD_ITEM  = "您没有%fn,无法创建帮派";
    
    public static final String TIP_GUILD_OF_POSITION_SIZE_MAX    = "职务已达到上限";
    /**
     * 被屏蔽提示
     */
    public static final String TIP_GUILD_OF_IN_BLACK             = "您被人家拉黑了";
    /**
     * 不在线的提示
     */
    public static final String TIP_GUILD_OF_TARGET_OFFLINE       = "该玩家不在线";
    /**
     * 没有权限的提示
     */
    public static final String TIP_GUILD_OF_NONE_RANK            = "您的权限太低了";
    /**
     * 已达最大等级
     */
    public static final String TIP_GUILD_OF_LEVEL_MAX            = "帮派已达最大等级,无法再进行提升";
    
    /**
     * 缺钱升级
     */
    public static final String TIP_GUILD_OF_NOT_MONEY            = "您的金钱不够,无法进行提升";
    /**
     * 您的帮派升级成功
     */
    public static final String TIP_GUILD_OF_UP_END               = "您的帮派升级成功";
    /**
     * 你
     */
    public static final String TIP_GUILD_OF_YOU                      = "您";
    /**
     * 创建帮会时金钱不够的提示
     */
    public static final String TIP_GUILD_OF_MONEY_LESS               = "金钱不够";
    /**
     * 创建帮会时等级不够的提示
     */
    public static final String TIP_GUILD_OF_LEVEL_LESS               = "级才可以创建帮派";
    /**
     * 帮会名称已存在的提示
     */
    public static final String TIP_GUILD_OF_EXISTS_GUILD_NAME        = "不能山寨别人的帮派名称";
    /**
     * 无效的帮会名字提示
     */
    public static final String TIP_GUILD_OF_INVALIDATE_GUILD_NAME    = "不和谐的帮派名称";
    /**
     * 新成员加入帮会时的提示
     */
    public static final String TIP_GUILD_OF_NEW_MEMBER               = "加入了帮派";
    
    public static final String TIP_GUILD_OF_MAX_SIZE                 = "帮派人数已达上限";
    /**
     * 离开帮会时的提示
     */
    public static final String TIP_GUILD_OF_LEFT                     = "离开了帮派";
    /**
     * 会长离开帮会时的提示
     */
    public static final String TIP_GUILD_OF_PRESIDENT_NOT_ALLOW_LEFT = "转让帮主后方可隐退";
    /**
     * 被改变会员等级后自己收到的提示
     */
    public static final String TIP_GUILD_OF_YOU_BE                   = "您成为了";
    /**
     * 被改变会员等级后的提示
     */
    public static final String TIP_GUILD_OF_BE                       = "成为了";
    /**
     * 被开除出帮会的提示
     */
    public static final String TIP_GUILD_OF_NOT_BE_REMOVE            = "被帮派扫地出门";
    /**
     * 不是帮会成员的提示
     */
    public static final String TIP_GUILD_OF_NOT_GUILD_MEMBER         = "对方不是混你这里的";
    
    //item,物品
    public final static String TIP_ITEM_TONIC_END  = "你已经用完了一个%fname";
    
    public final static String TIP_ITEM_TONIC_STOP = "停止使用%fname";
    
    public final static String TIP_ITEM_IN_FIGHTING  = "战斗中无法使用.";
    
    public final static String TIP_ITEM_START_ACTIVATE  = "开始使用%fname";
    
    public final static String TIP_ITEM_HP_IS_FULL  = "生命值已满";
    
    public final static String TIP_ITEM_MP_IS_FULL  = "魔法值已满";

    public static final String TIP_UPGRADE_BAG_FEE      = "你的%fn包裹是第%fx次扩容，需要%fy点数，是否扩容？";
    
    public final static String TIP_ITEM_PET_CARD_END  = "你已经用完了一张%fname";
    
    public final static String TIP_ITEM_PET_CARD_IN_USE  = "你已经在使用相同效果的%fname了";
    
    public final static String TIP_ITEM_NOT_UES = "无法使用";
    
    
	/**
	 * 没有复活石
	 */
    public static final String TIP_ITEM_OF_NOT_HAVE_REVIVE = "没有复活石,确认进入商城购买吗";
    /**
     * 包裹已是最大容量
     */
    public static final String TIP_BAG_IS_MAX      = "包裹已是最大容量";
    /**
     * 没有封印提示内容
     */
    public static final String TIP_ITEM_NONE_SEAL      = "该装备没有封印";
    /**
     * 背包中没有封印祝福提示内容
     */
    public static final String TIP_ITEM_NONE_SEAL_PRAY = "缺少合适的装备祝福之光";
	/**
	 * 等级不匹配提示内容
	 */
    public static final String TIP_ITEM_OF_NOT_CONFORM = "宝石与装备的等级不符";
	/**
	 * 目标装备已满级
	 */
    public static final String TIP_ITEM_OF_TOP_LEVEL = "装备镶嵌已满";
	/**
	 * 该位置还未打孔,无法镶嵌
	 */
    public static final String TIP_ITEM_OF_HAVE_HOLE = "该位置还未打孔,无法镶嵌";
	/**
	 * 打孔数已满
	 */
    public static final String TIP_ITEM_OF_MAX_POSITION = "完美的12个孔，已经全打好了";
	/**
	 * 打孔数已满
	 */
    public static final String TIP_ITEM_OF_MONEY_LACK = "打孔失败,金钱不足";
    
    public static final String TIP_ITEM_OF_MONEY_LACK_ENHANCE = "镶嵌失败,金钱不足";
    
    public static final String TIP_ITEM_OF_MONEY_LACK_WRECK = "剥离失败,金钱不足";
    
    
	/**
	 * 没有可供镶嵌的插槽
	 */
    public static final String TIP_ITEM_OF_NOT_POSITION = "没有可供镶嵌的孔位";
    
	/**
	 * 该位置上已经有宝石了
	 */
    public static final String TIP_ITEM_OF_HAVE_OCCUPIED = "该位置上已经有宝石了";
	/**
	 * 最后3个孔必须用女娲石
	 */
    public static final String TIP_ITEM_OF_NOT_ULTIMATE = "最后3个孔必须用女娲石";

    public static final String TIP_ITEM_OF_NOT_HAVE_ULTIMATE = "珍贵的女娲石只能用于最后3个孔的镶嵌";
    
    public static final String TIP_ITEM_OF_CHIP_COMPLETE = "宝石将增加装备微量的属性加成";
    
    public static final String TIP_ITEM_OF_SUCCESS_COMPLETE = "宝石将增加装备少量的属性加成";
    
    public static final String TIP_ITEM_OF_FLASH_COMPLETE = "宝石将增加装备较多的属性加成\n连续闪光有额外加成\n后三孔的镶嵌加成均有额外提高";
    
    public static final String TIP_ITEM_OF_WRECK_COMPLETE = "成功剥离了宝石";
    
    public static final String TIP_ITEM_OF_NOT_WRECK = "闪光的宝石不能被剥离";
    
    public static final String TIP_ITEM_OF_NOT_HAVE_JEWEL = "该位置没有宝石";
    
    public static final String TIP_ITEM_OF_WRONG = "您的微操太快了，请重试";
    
    public static final String TIP_ITEM_OF_WRONG_TYPE = "请不要使用奇怪的东西";
    
    public static final String TIP_ITEM_OF_NOT_HAVE_NEW_JEWEL = "没有强化宝石";
    
    public static final String TIP_ITEM_OF_NO_COMPLETE_WRECK = "该装备没有可供剥离的宝石";
    
    public static final String TIP_ITEM_OF_PWEDOEATE_COMPLETE = "恭喜您打孔成功";
    
    public static final String TIP_ITEM_OF_BIG_LOSE = "很遗憾，打孔失败,您的装备已经损毁消失了";
    //--------强化公告--------
    public static final String TIP_ITEM_NOTICE_BY_ELEVEN_PERFORATE_END = "恭喜[%p]打造出一件11孔的[%e]！幸运之神已经开始眷顾[%p]了！";
    
    public static final String TIP_ITEM_NOTICE_BY_TWELVE_PERFORATE_END = "恭喜[%p]打造出一件12孔的[%e]！雷神已经开始注意[%p]了！";
    
    public static final String TIP_ITEM_NOTICE_BY_ONE_FLASH_END = "恭喜[%p]打造出了[%e+%s（闪1）]！[%p]已经是初露锋芒了！";
    
    public static final String TIP_ITEM_NOTICE_BY_TWO_FLASH_END = "恭喜[%p]打造出了[%e+%s（闪2）]！[%p]已经是略有小成了！";
    
    public static final String TIP_ITEM_NOTICE_BY_THREE_FLASH_END = "恭喜[%p]打造出了[%e+%s（闪3）]！[%p]已经称霸一方了！";
    
    public static final String TIP_ITEM_NOTICE_BY_FOUR_FLASH_END = "恭喜[%p]打造出了[%e+%s（闪4）]！[%p]已经是威名远播了！";
    
    public static final String TIP_ITEM_NOTICE_BY_FIVE_FLASH_END = "恭喜[%p]打造出了[%e+%s（闪5）]！[%p]已经是名动江湖了！";
    
    public static final String TIP_ITEM_NOTICE_BY_SIX_FLASH_END = "恭喜[%p]打造出了[%e+%s（闪6）]！[%p]已经名扬天下了！";
    
    public static final String TIP_ITEM_NOTICE_BY_SEVEN_FLASH_END = "恭喜[%p]打造出了[%e+%s（闪7）]！[%p]已经是一代宗师了！";
    
    public static final String TIP_ITEM_NOTICE_BY_EIGHT_FLASH_END = "恭喜[%p]打造出了[%e+%s（闪8）]！[%p]已经是笑傲江湖了！";
    
    public static final String TIP_ITEM_NOTICE_BY_NINE_FLASH_END = "恭喜[%p]打造出了[%e+%s（闪9）]！[%p]已经无人能敌了！";
    
    public static final String TIP_ITEM_NOTICE_BY_TEN_FLASH_END = "恭喜[%p]打造出了[%e+%s（闪10）]！[%p]已经独孤求败了！";
    
    public static final String TIP_ITEM_NOTICE_BY_ELEVEN_FLASH_END = "恭喜[%p]打造出了[%e+%s（闪11）]！[%p]已经天下第一了！";
    
    public static final String TIP_ITEM_NOTICE_BY_TWELVE_FLASH_END = "恭喜[%p]打造出了[%e+12（闪12）]！[%p]已经天外飞仙了！";
    //--------强化公告--------
    
    
    /**
     * 耐久度未损失提示
     */
    public static final String TIP_ITEM_OF_NOT_BAD = "装备丝毫无损";
    /**
     * 没有足够的离线时间可消耗的提示
     */
    public static final String TIP_ITEM_OF_NOT_ENOGH_TIME = "上次离线时间低于2小时，无法使用";
    /**
     * 没有离线时间可消耗的提示
     */
    public static final String TIP_ITEM_OF_MAX_LEVEL      = "已是最高等级,无法使用";
    /**
     * 获得经验书时间提示
     */
    public static final String TIP_ITEM_OF_GET_TIIME = "获得%fx分钟双倍经验时间";
    /**
     * 使用经验书时间提示
     */
    public static final String TIP_ITEM_OF_GET_TIME = "使用了%fx";
    
    public static final String TIP_ITEM_OF_EXPERIENCE_FULL = "剩余的时间还有%t秒,无法再次使用%n";

    /**
     * 获得宠物的提示
     */
    public static final String TIP_ITEM_OF_GET_PET = "获得";
    /**
     * 已在灵魂记录地图上提示
     */
    public static final String TIP_ITEM_OF_EXIST       = "已经在灵魂记录地图";
    /**
     * 战斗中无法使用的提示
     */
    public static final String TIP_ITEM_OF_IN_FIGHTING = "战斗中无法使用";
    /**
     * 无法记录灵魂提示
     */
    public static final String TIP_ITEM_OF_CAN_NOT_BIND = "副本地图无法记录灵魂";
    /**
     * 无法在同一张地图上记录灵魂提示
     */
    public static final String TIP_ITEM_OF_SAME_MAP     = "无法重复记录灵魂";
    /**
     * 缺少世界号角提示内容
     */
    public static final String TIP_ITEM_OF_NONE_HORN = "缺少小喇叭";
    /**
     * 无效的目标提示
     */
    public static final String TIP_ITEM_OF_INVALID_TARGET        = "无效的目标";
    /**
     * 目标状态无效
     */
    public static final String TIP_ITEM_OF_INVALID_TARGET_STATUS = "目标状态无效";
    /**
     * 错误的地图提示
     */
    public static final String TIP_ITEM_OF_INVALID_MAP           = "该地图不可使用";
    /**
     * 错误的使用位置提示
     */
    public static final String TIP_ITEM_OF_INVALID_POINT         = "无效的位置";
    /**
     * 剩余冷却时间提示
     */
    public static final String TIP_ITEM_OF_USE_WAITING           = "秒后才可以使用";
    
    //manuf,生产
    public static final String TIP_MANUF_SUCCESS          = "制作成功";
    
    public static final String TIP_MANUF_FAIL             = "制作失败";
    
    public static final String TIP_MANUF_EXPECTED         = "意外的成功";
    
    public static final String TIP_MANUF_GOODS_NOT_ENOUGH = "材料不足";

    public static final String TIP_MANUF_BOX_NOT_ENOUGH   = "没有足够的背包空间";
    
    
    //map
    /**
     * 提示信息－不存在地图
     */
    public static final String TIP_MAP_OF_NONE_EXISTS      = "不存在的地图，ID:";
    /**
     * 提示信息－相同地图前缀
     */
    public static final String TIP_MAP_OF_SAME_HEADER      = "当前地图和目标地图ID相同,当前地图：";
    /**
     * 提示信息－相同地图后缀
     */
    public static final String TIP_MAP_OF_SAME_ENDER       = ",目标地图：";
    /**
     * 提示信息－无法获取出生点信息前缀
     */
    public static final String TIP_MAP_OF_NONE_BORN_HEADER = "不能获取目标地图出生点信息，当前地图: ";
    /**
     * 提示信息－无法获取出生点信息后缀
     */
    public static final String TIP_MAP_OF_NONE_BORN_ENDER  = ",目标地图: ";

    public static final String TIP_NO_WORLD_MAPS = "Bản đồ hiện tại không thể sử dụng chức năng này";
    
    public static final String TIP_MAP_OF_DUNGEON_FAIL = "获取副本地图失败";
    
    
    
    //micro,微功能模块
    /**
     * 没有队伍的提示
     */
    public static final String TIP_MICRO_OF_NONE_TEAM        = "需要在队伍中";
    /**
     * 队伍成员数量超出的提示
     */
    public static final String TIP_MICRO_OF_MORE_MEMBER      = "队员数量超过选择的队伍类型";
    /**
     * 未分胜负提示
     */
    public static final String  TIP_MICRO_OF_END              = "比赛结束，未分胜负";
    /**
     * 胜利提示
     */
    public static final String  TIP_MICRO_OF_WIN              = "您赢得了比赛";
    /**
     * 输提示
     */
    public static final String  TIP_MICRO_OF_LOSE             = "您输掉了比赛";
    /**
     * 等级过低的提示
     */
    public static final String TIP_MICRO_OF_LESS_LEVEL       = "20级才能进行竞技";
    /**
     * 队伍中有成员离线的提示
     */
    public static final String TIP_MICRO_OF_MEMBER_OFFLINE   = "队伍有成员离线";
    /**
     * 加入等待队列的提示
     */
    public static final String TIP_MICRO_OF_JOIN_QUEUE       = "加入了竞技等待队列";
    /**
     * 队伍成员等级区间不一致的提示
     */
    public static final String TIP_MICRO_OF_MEMBER_DIFFICULT = "队伍成员等级区间不一致";
    /**
     * 价格错误提示
     */
    public static final String TIP_MICRO_OF_PRICE_ERROR = "这是什么诡异的价格";
    /**
     * 开店时没有商品的提示
     */
    public static final String TIP_MICRO_OF_NONE_GOODS             = "没上货？想要空手套白狼吗？";
    /**
     * 购买时找不到商店
     */
    public static final String TIP_MICRO_OF_STORE_EMPTY            = "商店不存在";
    /**
     * 购买时找不到物品
     */
    public static final String TIP_MICRO_OF_GOODS_EMPTY            = "商品不存在";
    /**
     * 购买时金钱不够提示
     */
    public static final String TIP_MICRO_OF_MONEY_NOTENOUGH        = "金钱不够";
    /**
     * 商品售空，关店提示
     */
    public static final String TIP_MICRO_OF_CLOSE_THAT_GOODS_EMPTY = "卖完了,马上打烊喽";
    /**
     * 拒绝成为自己徒弟的提示
     */
    public static final String TIP_MICRO_OF_REFUSE_BE_APPR   = "不屑做您徒弟";
    /**
     * 拒绝成为自己师傅的提示
     */
    public static final String TIP_MICRO_OF_REFUSE_BE_MASTER = "拒绝收您为徒";
    /**
     * 师徒等级差距不足10级的提示
     */
    public static final String   TIP_MICRO_OF_LEVEL_DIFFERENCE_NOTENOUGH = "相差10级才可以成为师徒";
    /**
     * 种族不同的提示
     */
    public static final String   TIP_MICRO_OF_CLAN_DIFFERENT             = "同种族才可以成为师徒";
    /**
     * 收纳徒弟时对方已有师傅的提示
     */
    public static final String   TIP_MICRO_OF_EXIST_MASTER               = "对方已有师傅";
    /**
     * 拜师时自己已有师傅的提示
     */
    public static final String   TIP_MICRO_OF_MASTER_ONLY                = "您已有师傅";
    /**
     * 徒弟数量到达上限的提示
     */
    public static final String   TIP_MICRO_OF_APPRENTICE_FULL            = "徒弟数量已满";
    /**
     * 对方徒弟数量到达上限的提示
     */
    public static final String   TIP_MICRO_OF_OTHER_APPRENTICE_FULL      = "对方徒弟已满";
    /**
     * 成为自己徒弟的提示
     */
    public static final String   TIP_MICRO_OF_BE_APPR                    = "成为您的徒弟";
    /**
     * 成为自己师傅的提示
     */
    public static final String   TIP_MICRO_OF_BE_MASTER                  = "已成为您的师傅";
    /**
     * 授予知识结果提示－－等级不足10级
     */
    public static final String   TIP_MICRO_OF_THAT_LEVEL_NOTENOUGH       = "徒弟未达到10级";
    /**
     * 材料包已满提示(授予徒弟知识时师傅会获得系统奖励)
     */
    public static final String   TIP_MICRO_OF_BAG_IF_FULL                = "材料背包没地儿了呀";
    /**
     * 授予知识时不在同张地图上的提示
     */
    public static final String   TIP_MICRO_OF_THAT_NOT_TOGETHER          = "您们相距太远";
    /**
     * 授予知识结果提示－－当前等级区间已经授予过知识了
     */
    public static final String   TIP_MICRO_OF_THAT_HAS_TEACHED           = "当前等级段已授予过知识了";
    /**
     * 徒弟被授予知识时的提示标题
     */
    public static final String   TIP_MICRO_OF_BE_TEACHED                 = "师傅授予您知识";
    /**
     * 师傅授予徒弟知识时的提示标题
     */
    public static final String   TIP_MICRO_OF_TEACHED                    = "授予徒弟知识";
    /**
     * 知识授予失败提示
     */
    public static final String   TIP_MICRO_OF_TEACH_FAILER               = "知识授予失败";
    /**
     * 徒弟或师傅解除师徒关系时的提示
     */
    public static final String   TIP_MICRO_OF_LEFT_MASTER                = "已与您解除师徒关系";
    /**
     * 师傅解散所有徒弟时的提示
     */
    public static final String TIP_MICRO_OF_DISMISS_ALL                    = "您已遣散所有徒弟";
    /**
     * 主动解除师徒关系时的提示前缀
     */
    public static final String   TIP_MICRO_OF_REDUCE_APPRENTICE_HEADER   = "您已与";
    /**
     * 主动解除师徒关系时的提示后缀
     */
    public static final String   TIP_MICRO_OF_REDUCE_APPRENTICE_ENDER    = "解除师徒关系";
    /**
     * 系统邮件标题
     */
    public static final String   TIP_MICRO_OF_SYSTEM                     = "系统";
    /**
     * 不到脱离时间不能拜师提示
     */
    public static final String  TIP_MICRO_LAST_LEFT_MASTER_DISTANCE = "距离您上次解除师徒关系未满 3 天,不能拜师";
    /**
     * 出师时收到的提示
     */
    public static final String   TIP_MICRO_OF_APRENTICE_FINISH_STUDY     = "已经圆满出师";
    
    
    
    //npc
    public static final String TIP_NPC_NOT_HAVE_ITEM      = "该商人没有要卖的物品";
    
    public static final String TIP_NPC_ITEM_NOT_TRADE     = "该物品无法出售";
    
    public static final String TIP_NPC_FAIL_OPERATE       = "操作失败,请重试";
    
    public static final String TIP_NPC_EVIDENVE_BY_USE    = "操作失败,请重试";
    
    public static final String TIP_NPC_EVIDENVE_GET_END   = "恭喜您得到：%fa感谢您的支持，祝您游戏愉快";
    
    public static final String TIP_NPC_GET_NEW_MAIL       = "您有一封新的邮件，快去邮箱查收吧！";
    
    public static final String TIP_NPC_MAIL_AUCTION_TITLE = "拍卖获得:";
    
    public static final String TIP_NPC_BUY                  = "请选择竞拍物品的种类";
    
    public static final String TIP_NPC_QUESTION_NOT_BY_TIME = "答题时间未到";
    
    public static final String TIP_NPC_QUESTION_JOIN_IT     = "已参与了该活动,请等待下次机会";
    
    public static final String TIP_NPC_SALE                     = "请选择拍卖物品的种类";
    
    public static final String TIP_NPC_SEARCH                 = "请选择查询物品的种类";
    
    public static final String TIP_NPC_SALE_NUM                = "请输入拍卖数量";
    
    public static final String TIP_NPC_SALE_MONEY             = "请输入拍卖价格";
    
    public static final String TIP_NPC_SEL_GOODS_NAME               = "查询物品名：";
    
    public static final String  TIP_NPC_MONEY_ERROR                  = "您的金钱不够支付拍卖费用";
    
    public static final String TIP_NPC_MONEY_NOT_ENOUGH             = "您的金钱不够";
    
    public static final String TIP_NPC_DURABILITY_ERROR             = "耐久度必须为最大耐久度才可以拍卖";
    
    public static final String TIP_NPC_NUM_ERROR                    = "您好像没有这么多吧";
    
    public static final String TIP_NPC_AUCTION_OVER                 = "该物品已经给人竞拍了";
    
    public static final String TIP_NPC_AUCTION_SUCCESS              = "拍卖成功";
    
    public static final String TIP_NPC_BUY_SUCCESS                  = "竞拍成功";
    
    public static final String TIP_NPC_NEW_MAIL                     = "您有一封新的邮件，快去邮箱查收吧！";
    
    public static final String TIP_NPC_NOT_PREC_PAGE                = "没有上一页了";
    
    public static final String TIP_NPC_NOT_NEXT_PAGE                = "没有下一页了";
    
    public static final String TIP_NPC_NO_EXCHANGEABLE              = "这东西太珍贵了，不可拍卖";
    
    public static final String TIP_NPC_MONEY_NOTIFY                 = "获得金币通知";

    public static final String TIP_NPC_LETTER_NOTIFY                = "您在拍卖行拍卖的{0}成功售出，获得了{1}金币";
    
    public static final String TIP_NPC_DROP_GUILD               = "您确认解散帮派吗？";
    
    public static final String TIP_NPC_GUILD_NAME               = "帮派名称";
    
    public static final String TIP_NPC_LEADER_NAME              = "帮主名称";

    public static final String TIP_SZF_CHARGE_BACK              = "神州付充值结果通知";
    
    /**
     * 顶层操作菜单列表
     */
    public static final String[] FUNCTION_MAIN_MENU_LIST        = {"建立帮派", "解散帮派"};
    /**
     * 顶层操作菜单列表
     */
//    public static final String[] FUNCTION_MAIN_MENU_LIST            = {
//            "建立帮派", "解散帮派", "转让帮派"                            };
    
    public static final String FUNCTION_INPUT                   = "请输入对方可爱的昵称";
    /**
     * 登记恋人名称
     */
    public static final String[] FUNCTION_LOVE_MAIN_MENU_LIST            = {"我希望天长地久的人" };
    
    public static final String FUNCTION_LOVE_FORGET_SKILL              = "您确认要遗忘吗？";
    
    public static final String FUNCTION_LOVE_MONEY_NOT_ENOUGH             = "金钱不够，需要";
    
    public static final String TIP_NPC_NOT_STUDY_MANUF               = "您还没有学习过制造技能";
    
    public static final String TIP_NPC_ALREADY_LEARN             = "您已经学会了此技能";
    
    public static final String[] TIP_NPC_SKILL_POINT_NOT_ENOUGH         = {"您的熟练度不够，需要","点熟练度"};
    
    public static final String TIP_NPC_FORGET_SUCCESS         = "成功遗忘了制造技能";
    
    public static final String[] FUNCTION_LOVE_MENU_LIST      = {"我要结婚","我要离婚","婚姻说明"};

    public static final String[] FUNCTION_LOVE_DIVORCE_MENU_LIST = {"协议离婚","强制离婚"};

    public static final String TIP_SEND_MESSAGE_NOTIFY = "消息已发出，请等待对方回应！";

    public static final String TIP_MARRY_DESC = "婚姻提示:你好,当你的等级到达20级；爱情值到达3000后,你可以携带结婚戒指和你的恋人组队去找月老登记结婚.";
    
    public static final String FUNCTION_LOVE_DIVORCE_TYPE = "请选择离婚类型";
    
    public static final String FUNCTION_LOVE_INPUT     = "请输入对方昵称";
    
    public static final String FUNCTION_MAIL_CHOOSE     = "请选择邮寄物品的种类";
    
    public static final String FUNCTION_MAIL_NUM              = "请输入邮寄数量";
    
    public static final String FUNCTION_MAIL_NICKNAME           = "请输入收信玩家的昵称";
    
    public static final String FUNCTION_MAIL_MONEY               = "请输入邮寄的金币数";
    /**
     * 出售操作菜单列表
     */
    public static final String[] FUNCTION_MAIL_SALE_OPERTION_LIST           = {"邮　　寄" };
    
    public static final String TIP_NPC_MAIL_NOT_EXITS               = "指定的邮件不存在！";
    
    public static final String TIP_NPC_MAIL_DEL_FINISH              = "删除邮件成功";
    
    public static final String TIP_NPC_MAIL_NOT_ATTACHMENT          = "没有附件！";
    
    public static final String TIP_NPC_MAIL_SEND_SUCCESS                 = "发送邮件成功！";
    
    public static final String TIP_NPC_MAIL_GET_NEW                = "您有一封新的邮件，快去邮箱查收吧";
    
    public static final String TIP_NPC_MAIL_MONEY_NOT_ENOUGH             = "金钱不够";
    
    public static final String TIP_NPC_MAIL_POINT_AMOUNT_NOT_ENOUGH      = "游戏点数不够";
    
    public static final String TIP_NPC_MAIL_PLAYER                       = "玩家‘";
    
    public static final String TIP_NPC_MAIL_PLAYER_NOT_EXITS     = "’不存在，无法发送邮件！";
    
    public static final String TIP_NPC_MAIL_BOX_FULL                     = "’对方邮箱已满，无法接收邮件！";
    
    public static final String TIP_NPC_MAIL_NO_EXCHANGEABLE              = "此物品为不可交易物品，不能邮寄";
    
    public static final String FUNCTION_MAIL_DEFAULT_NEW_TITLE       = "新邮件";
    
    
    /**
     * 顶层操作菜单列表
     */
    public static final String[] FUNCTION_NPC_REPAIR_MAIN            = {"修理装备" };
    
    public static final String TIP_NPC_LVL_UP_SUCCESS               = "仓库扩容成功";
    
    public static final String TIP_NPC_ALREADY_HIGH_LVL             = "扩容已达最高级不能再扩容了";
    
    public static final String TIP_NPC_STORAGE_NUM_ERROR            = "存入数量大于已有数量";
    
    public static final String TIP_NPC_STORAGE_FULL                         = "仓库已满";
    
    public static final String TIP_NPC_STORAGE_SUCCESS                      = "成功存入仓库";
    
    public static final String TIP_NPC_STORAGE_NOT_EXITS                    = "指定的物品未找到";
    
    public static final String FUNCTION_NPC_STORAGE_LVL_UP_NEED                  = "升级需要";
    
    public static final String FUNCTION_NPC_STORAGE_CONFIM_LVL_UP                = "金币，确定升级吗？";
    
    public static final String TIP_NPC_OF_DISTANCE_IS_LONG = "您离 %fn 太远了,走近一些吧.";
    /**
     * 装备包满提示
     */
    public static final String TIP_NPC_OF_EQUPMENT_BAG_FULL_AT_SUBMIT      = "装备背包已满,无法提交";
    /**
     * 材料包满提示
     */
    public static final String TIP_NPC_OF_MATERIAL_BAG_FULL_AT_SUBMIT      = "材料背包已满,无法提交";
    
    /**
     * 药品包满提示
     */
    public static final String TIP_NPC_OF_MEDICAMENT_BAG_FULL_AT_SUBMIT    = "药品背包已满,无法提交";
    /**
     * 任务道具包满提示
     */
    public static final String TIP_NPC_OF_TASK_TOOL_BAG_FULL_AT_SUBMIT     = "任务背包已满,无法提交";
    /**
     * 特殊物品包满提示
     */
    public static final String TIP_NPC_OF_SPECIAL_GOODS_BAG_FULL_AT_SUBMIT = "宝物背包已满,无法提交";
    
    /**
     * 接任务时任务数量已满提示
     */
    public static final String TIP_NPC_OF_TASK_NUMBER_FULL_AT_RECEIVE      = "任务数量已满,无法接受任务";
    /**
     * 接任务时任务道具包满提示
     */
    public static final String TIP_NPC_OF_TASK_TOOL_BAG_FULL_AT_RECEIVE    = "任务背包已满,无法接受任务";
    /**
     * 不能同时进行多个护送任务的提示
     */
    public static final String TIP_NPC_OF_NOT_ESCORT_MORE          = "护送任务要一个一个做";
    
    
    
    //player, 玩家模块
    public static final String TIP_PLAYER_ILLEGAL_MOVE = "我了个去,请勿使用外挂";
    
    public static final String TIP_PLAYER_CELEBRATE_GIFT = "恭喜您获得系统发放的%fn,下一个礼包将在%ft分钟后下发,届时请确保您的宝物背包有1个位置接收它";
    
    public static final String TIP_PLAYER_GIFT_TIME = "感谢您对仙境OL的支持,系统将在您在线%ft分钟之后给您赠送%fn";
    
    //player, 玩家模块
    public static final String TIP_PLAYER_ILLEGAL_MOVE_1 = "为了您的账号安全,请勿使用其他第三方软件来进行游戏";
    
    
    //share,公共模块
    
    /**
     * 已保存提示
     */
    public static final String TIP_SHARE_MAIL_OF_SAVED  = "邮件已保存";
    
    /**
     * 信件删除提示
     */
    public static final String TIP_SHARE_MAIL_OF_DELETE = "邮件已删除";
    
    public static final String TIP_SHARE_MAIL_BY_UES = "尊敬的*name*您的随身邮还有*day*天使用时间。";
    
    public static final String TIP_SHARE_MAIL_FEE_TIP = "尊敬的*name*您的随身邮目前只能发送和接收文字邮件。点击确定前往商城付费开通邮寄钱物功能，点击返回继续收发邮件。";
    
    /**
     * 对方邮箱已满提示
     */
    public static final String TIP_SHARE_MAIL_BOX_FULL               = "玩家邮箱已满，邮件发送失败";
    
    public static final String TIP_SHARE_DOWNLOAD_NOT_FILE           = "没有该资源";
    /**
     * 发送成功提示
     */
    public static final String TIP_SHARE_MAIL_SEND_SUCCESSFULLY      = "邮件发送成功";
    /**
     * 信件接收人不存在的提示
     */
    public static final String TIP_SHARE_MAIL_TARGET_INVALIDATE_FAIL = "玩家不存在，发送失败";
    
	/**
     * 找不到玩家的提示前缀
     */
    public static final String TIP_SOCIAL_0F_NULL_TARGET = "玩家不在线";

    
    
    //RequestExchang 
    
    public static final String        REQUEST_EXCHANGE_NUM_TIP               = "请输入数量";

    public static final String        REQUEST_EXCHANGE_PLAYER_NOT_EXITS      = "玩家不存在，交易请求失败";

    public static final String        REQUEST_EXCHANGE_PLAYER_BUSY           = "玩家正忙，交易请求失败";

    public static final String        REQUEST_EXCHANGE_NO_EXCHANGEABLE       = "此物品为不可交易物品";

    public static final String        REQUEST_EXCHANGE_EQUIPEMNT_FULL        = "装备背包已满，交易失败";

    public static final String        REQUEST_EXCHANGE_MONEY_ENOUGH          = "金钱不够";

    public static final String        REQUEST_EXCHANGE_MONEY_FULL            = "金钱达到上限";

    public static final String        REQUEST_EXCHANGE_OTHER_MONEY_ENOUGH    = "对方金钱不够";

    public static final String        REQUEST_EXCHANGE_OTHER_MONEY_FULL      = "对方的钱包已经装不下了";

    public static final String        REQUEST_EXCHANGE_OTHER_EQUIPMENT_FULL  = "对方装备背包已满，交易失败";

    public static final String        REQUEST_EXCHANGE_MATERIAL_FULL         = "材料背包已满，交易失败";

    public static final String        REQUEST_EXCHANGE_OTHER_MATERIAL_FULL   = "对方材料背包已满，交易失败";

    public static final String        REQUEST_EXCHANGE_MEDICAMENT_FULL       = "药品背包已满，交易失败";

    public static final String        REQUEST_EXCHANGE_OTHER_MEDICAMENT_FULL = "对方药品背包已满，交易失败";

    public static final String        REQUEST_EXCHANGE_SPECIAL_FULL          = "宝物背包已满，交易失败";

    public static final String        REQUEST_EXCHANGE_OTHER_SPECIAL_FULL    = "对方宝物背包已满，交易失败";

    public static final String     REQUEST_EXCHANGE_PET_FULL = "宠物背包已满，交易失败";

    public static final String     REQUEST_EXCHANGE_OTHER_PET_FULL = "对方宠物背包已满，交易失败";

    public static final String     REQUEST_EXCHANGE_PET_GOODS_FULL = "宠物物品背包已满，交易失败";

    public static final String     REQUEST_EXCHANGE_OTHER_PET_GOODS_FULL = "对方宠物物品背包已满，交易失败";

    public static final String     REQUEST_EXCHANGE_PET_EQUIPMENT_FULL = "宠物装备背包已满，交易失败";

    public static final String     REQUEST_EXCHANGE_OTHER_PET_EQUIPMENT_FULL = "对方宠物装备背包已满，交易失败";



    public static final String TIP_SOCIAL_SERVICE_ADD_SUCCESS               = "添加成功";

    public static final String TIP_SOCIAL_SERVICE_DELETE_SUCCESS            = "删除成功";

    public static final String TIP_SOCIAL_SERVICE_DELETE_FAIL               = "删除失败";

    /**
     * 仇人上线提示
     */
    public static final String TIP_SOCIAL_SERVICE_OF_LOGIN              = "仇人上线了";

    /**
     * 仇人下线提示
     */
    public static final String TIP_SOCIAL_SERVICE_OF_LOGOUT             = "仇人下线了：";

    /**
     * 好友列表已满提示
     */
    public static final String TIP_SOCIAL_SERVICE_OF_FIREND_FULL        = "好友列表已满";

    /**
     * 屏蔽名单已满提示
     */
    public static final String TIP_SOCIAL_SERVICE_OF_BLACK_FULL         = "屏蔽名单已满";

    /**
     * 
     */
    public static final String TIP_SOCIAL_SERVICE_OF_ENEMY_FULL         = "通缉名单已满";

    /**
     * 已在屏蔽名单中的提示
     */
    public static final String TIP_SOCIAL_SERVICE_OF_EXISTS_BLACK_LIST  = "已在屏蔽名单中";

    /**
     * 已在亲友列表中的提示
     */
    public static final String TIP_SOCIAL_SERVICE_OF_EXISTS_FRIEND_LIST = "已在好友名单中";

    /**
     * 已在仇人名单中的提示
     */
    public static final String TIP_SOCIAL_SERVICE_OF_EXISTS_ENEMY_LIST  = "已在仇人名单中";

    /**
     * 别的氏族加为好友时的提示
     */
    public static final String TIP_SOCIAL_SERVICE_OF_DIFFERENT_CLAN     = "不同的种族";

    /**
     * 相同氏族加为仇人时候的提示
     */
    public static final String TIP_SOCIAL_SERVICE_OF_SAME_CLAN          = "相同种族不能添加仇人";
    
    
    
    //task,任务模块
    public static final String TIP_TASK_OF_NONE = "无可接任务";
    
    public static final String TIP_TASK_NOT_HAVE = "没有任务传送道具";
    
    public static final String TIP_TASK_USE = "您已经消耗了1个";
    
    //task-service
    public static final String TIP_TASK_SERVICE_OF_COMPETED                    = "您已完成该任务";

    public static final String TIP_TASK_SERVICE_OF_HAS                         = "您已有该任务";

    public static final String TIP_TASK_SERVICE_OF_CAN_NOT_TRANSMIT            = "不可传送";
    
    public static final String TIP_TASK_SERVICE_OF_CAN_NOT_TRANSMIT_BY_TYPE    = "这种任务您还是亲自去一趟比较好";

    public static final String TIP_TASK_SERVICE_OF_CAN_NOT_TRANSMIT_TO_DUNGEON = "无法传送到副本中";

    public static final String TIP_TASK_SERVICE_OF_HERE_ALREADY                = "您已在这里了";
    
    
    //ui,游戏部分ui文字在服务器的部分
    /**
     * 游戏币 单位
     */
    public static final String FUNCTION_UI_DESC_OF_MONEY      = "金";
    /**
     * 游戏点数描述
     */
    public static final String FUNCTION_UI_DESC_OF_GAME_POINT = "点";
    
    
    //charge
    public static final String TIP_CHARGE_NOT_POINT = "游戏点数不足，请到商城充值，可享更多优惠";

    public static final String TIP_NUMBER_ERROR = "系统警告:数量值错误，系统已记录此行为！！！";
    
    public static final String TIP_CHARGE_PET_BAG_FULL = "宠物装备包已满";
    
    public static final String TIP_CHARGE_PET_LIST_FULL = "宠物列表已满";
    
    public static final String TIP_CHARGE_PET_ITEM_FULL = "宠物物品包已满";
    
    public static final String TIP_CHARGE_SPECIAL_FULL = "特殊物品包已满";
    
    public static final String TIP_CHARGE_MATERIAL_FULL = "材料包已满";
    
    public static final String TIP_CHARGE_MEDICAMENT_FULL = "药水包已满";
    
    public static final String TIP_CHARGE_EQUIPMENT_FULL = "装备包已满";
    
    public static final String TIP_CHARGE_EXP_BOOK_TIMEOVER = "双倍经验时间已结束";
    
    public static final String TIP_CHARGE_TASK_TOOL_FULL = "任务道具包已满";
    
    public static final String TIP_CHARGE_FAIL_FEE = "计费错误，商品无法购买，请稍后再试";
    
    public static final String TIP_CHARGE_POINT_GAIN = "获得游戏点数：";

    public static final String TIP_PRESENT_POINT = "获得额外游戏点数：";
    
    public static final String TIP_CHARGE_POINT_CONSUME = "消耗游戏点数：";

    public static final String TIP_BUY_TOOL = "购买道具";
    
    public static final String TIP_LEVEL_NOT_ENOUGH = "您的等级不够";

    public static final String TIP_BAG_EXPAN_ERROR = "扩展背包失败";

    public static final String TIP_EXCHANGE_DISTANCE = "对方距离太远不能交易";

    public static final String TIP_TRANSPORT_NPC_NEED_GOODS = "传送到NPC需要传送符，你要购买一个吗？";

    public static final String TIP_TRANSPORT_MAP_NEED_GOODS = "传送到该地图需要传送符，你要购买一个吗？";

    public static final String TIP_TRANSPORT_NPC_USE_GOODS = "传送到NPC需要消费一个传送符，你确定吗？";

    public static final String TIP_TRANSPORT_MAP_USE_GOODS = "传送到该地图需要消费一个传送符，你确定吗？";

    public static final String TIP_NOT_NPC = "没有找到这个NPC:";

    public static final String TIP_USE_TRANSPORT_GOODS_FAIL = "使用传送符失败，不能传送";

    public static final String TIP_NOT_HEAVEN_BOOK = "您的背包中没有多余的天书，" +
                                        "镶嵌天书可以为角色提供额外的技能加成，您可以去商城购买天书。";

    public static final String TIP_OVERRIDE_HEAVEN_BOOK = "确定覆盖已经镶嵌的天书?";
    
   
    //计费模块
    public static final String QUERY_POINT_ERROR_TIP = "查询出错了，请稍后再试";

    public static final String TIP_CHARGE_WAITTING = "充值处理中，请耐心等待...";

    public static final String TIP_CHARGE_FAIL = "充值失败，请稍后再试...";

    public static final String TIP_GET_FEEINI_FAIL = "获取计费配置信息失败，请稍后再试！";


    //
    public static final String TIP_FUN_NOT_OPEN = "此功能暂未开放，敬请期待！";

    public static final String TIP_MAP_NOT_STORE = "您所在的地图不能摆摊!";

    public static final String TIP_EXCHANGEING_NOT_STORE = "交易状态中不能摆摊!";

    public static final String TIP_EXCHANGEING_NOT_SWITCHMAP = "摆摊状态中不能跳转地图";

    public static final String TIP_EXCHANGEING_NOT_USE_FUN = "摆摊状态中不能使用此功能";

}
