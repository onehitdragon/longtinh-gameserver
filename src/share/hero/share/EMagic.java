package hero.share;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-10-14 上午10:45:45
 * 
 * <pre>
 *      Description:魔法
 * </pre>
 */

public enum EMagic
{
//    /**
//     * 金 (以前的神圣)
//     */
//    SANCTITY(1, "金"),
//
//    /**
//     * 土 (以前的暗影)
//     */
//    UMBRA(2, "土"),
//    /**
//     * 水 (以前的冰)
//     */
//    WATER(3, "水"),
//    /**
//     * 火
//     */
//    FIRE(4, "火"),
//    /**
//     * 木(老版本的'土'系)
//     * 
//     */
//    SOIL(5, "木");
	
    /**
     * 所有魔法伤害
     */
    ALL(0, "全部"),
	
    /**
     * 神圣
     */
    SANCTITY(1, "神圣"),

    /**
     * 暗影
     */
    UMBRA(2, "暗影"),
    /**
     * 冰
     */
    WATER(3, "冰"),
    /**
     * 火
     */
    FIRE(4, "火"),
    /**
     * 自然(老版本的'土'系)
     * 
     */
    SOIL(5, "自然");
    /**
     * 魔法编号
     */
    private int    ID;

    /**
     * 魔法名称
     */
    private String name;

    /**
     * 构造
     * 
     * @param _id
     * @param _name
     */
    private EMagic(int _id, String _name)
    {
        ID = _id;
        name = _name;
    }

    /**
     * 获取魔法编号
     * 
     * @return
     */
    public int getID ()
    {
        return ID;
    }

    /**
     * 获取魔法名称
     * 
     * @return
     */
    public String getName ()
    {
        return name;
    }

    /**
     * 根据魔法编号，获取魔法
     * 
     * @param _magicID
     * @return
     */
    public static EMagic getMagic (int _magicID)
    {
        switch (_magicID)
        {
        	case 0:
        		return ALL;
            case 1:
                return SANCTITY;
            case 2:
                return UMBRA;
            case 3:
                return WATER;
            case 4:
                return FIRE;
            case 5:
                return SOIL;
            default:
                return null;
        }
    }

    /**
     * 根据魔法名称，获取魔法
     * 
     * @param _magicName
     * @return
     */
    public static EMagic getMagic (String _magicName)
    {
        for (EMagic magic : EMagic.values())
        {
            if (magic.getName().equals(_magicName))
            {
                return magic;
            }
        }

        return null;
    }
}
