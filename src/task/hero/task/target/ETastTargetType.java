package hero.task.target;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ETastTargetType.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-9 下午02:03:23
 * @描述 ：
 */

public enum ETastTargetType
{
    /**
     * 物品
     */
    GOODS("物品"),
    /**
     * 探路
     */
    FOUND_A_PATH("探路"),
    /**
     * 护送NPC
     */
    ESCORT_NPC("护送"),
    /**
     * 交互
     */
    OPEN_GEAR("机关"),
    /**
     * 杀怪
     */
    KILL_MONSTER("杀怪");

    /**
     * 类型描述
     */
    private String typeDesc;

    ETastTargetType(String _typeDesc)
    {
        typeDesc = _typeDesc;
    }

    /**
     * 根据描述获取任务目标类型枚举
     * 
     * @param _desc
     * @return
     */
    public static ETastTargetType getTastTargetTypeByDesc (String _desc)
    {
        if (null == _desc)
        {
            return null;
        }

        for (ETastTargetType type : ETastTargetType.values())
        {
            if (type.typeDesc.equals(_desc))
            {
                return type;
            }
        }

        return null;
    }
}
