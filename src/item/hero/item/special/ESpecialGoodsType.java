package hero.item.special;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ESpecialGoodsType.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-8-6 下午01:45:02
 * @描述 ：
 */

public enum ESpecialGoodsType
{
    GOURD("葫芦"), RATTAN("藤条"), CRYSTAL("水晶"), DRAWINGS("图纸"), SEAL_PRAY("封印祝福"), 
    		WORLD_HORN("世界号角"),MASS_HORN("集结号角"), EXPERIENCE_BOOK("在线经验书"), 
            EXP_BOOK_OFFLINE("离线经验书"), HUNT_EXP_BOOK("狩猎经验书"), SOUL_MARK("灵魂印记"), 
            SOUL_CHANNEL("灵魂符文"), EQUIPMENT_REPAIR("装备修补剂"), PET_ARCHETYPE("宠物原型"), 
            SHOP_LICENCE("个人商店许可证"), SKILL_BOOK("技能书"),PET_FEED("宠物饲料"),
            PET_REVIVE("宠物复活卷轴"),PET_DICARD("宠物能力槽洗点道具"),
            PET_SKILL_BOOK("宠物技能书"),MARRY_RING("结婚戒指"),DIVORCE("离婚证明"),HEAVEN_BOOK("天书"),
            TASK_TRANSPORT("传送水晶"),BIG_TONIC("大补丸"),RINSE_SKILL("洗点符"),REVIVE_STONE("复活石"),
            GUILD_BUILD("工会成立卡"),PET_FOREVER("永久坐骑卡"),PET_PER("按次坐骑卡"),PET_TIME("计时坐骑卡"),
            FLOWER("鲜花"), CHOCOLATE("巧克力"),SPOUSE_TRANSPORT("夫妻传送符"),BAG_EXPAN("背包扩展"), 
            GIFT_BAG("礼包"),HOOK_EXP("离线挂机经验"),REPEATE_TASK_EXPAN("循环任务扩展");


    /**
     * 描述
     */
    private String description;

    /**
     * 构造
     * 
     * @param _desc
     */
    ESpecialGoodsType(String _desc)
    {
        description = _desc;
    }

    /**
     * 获取描述
     * 
     * @return
     */
    public String getDescription ()
    {
        return description;
    }

    /**
     * 根据类型描述获取类型枚举
     * 
     * @param _desc 类型描述
     * @return
     */
    public static ESpecialGoodsType get (String _desc)
    {
        for (ESpecialGoodsType type : ESpecialGoodsType.values())
        {
            if (type.getDescription().equals(_desc))
            {
                return type;
            }
        }

        return null;
    }
}
