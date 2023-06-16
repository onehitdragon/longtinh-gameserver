package hero.manufacture;

/**
 * 制造类型 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 * 因为现在规定采集和制造合起来最多只能学习两种
 * 所以把采集和提纯也放到这里处理 --- 2010-12-22 jiaodongjie
 */
public enum ManufactureType
{
    /**
     * 药草匠
     */
    DISPENSER((byte) 4, "药草匠"),
    /**
     * 鉴宝匠
     */
    JEWELER((byte) 3, "鉴宝匠"),
    /**
     * 工艺匠
     */
    CRAFTSMAN((byte) 2, "工艺匠"),
    /**
     * 铁匠
     */
    BLACKSMITH((byte) 1, "铁匠"),

    /**
     * 采集师
     */
    GRATHER((byte)6,"采集师"),

    /**
     * 提纯师
     */
    PURIFY((byte)5,"提纯师");

    private byte   id;

    private String name;

    ManufactureType(byte _id, String _name)
    {
        id = _id;
        name = _name;
    }

    public byte getID ()
    {
        return id;
    }

    public String getName ()
    {
        return name;
    }

    /**
     * 根据类型编号获取制造技能类型
     * 
     * @param _value
     * @return
     */
    public static ManufactureType get (byte _value)
    {
        for (ManufactureType type : ManufactureType.values())
        {
            if (type.id == _value)
            {
                return type;
            }
        }

        return null;
    }

    /**
     * 根据类型描述获取制造技能类型
     * 
     * @param _typeDesc
     * @return
     */
    public static ManufactureType get (String _typeDesc)
    {
        for (ManufactureType type : ManufactureType.values())
        {
            if (type.name.equals(_typeDesc))
            {
                return type;
            }
        }

        return null;
    }
}
