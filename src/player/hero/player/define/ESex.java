package hero.player.define;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2005
 * </p>
 * 
 * @file ESex.java
 * @author Ding Chu
 * @version 1.0
 * @date 2008-9-24 上午10:26:13
 * 
 * <pre>
 *      Description:性别
 * </pre>
 */
public enum ESex
{
    /**
     * 男性
     */
    Male((byte) 1, "男"),
    /**
     * 女性
     */
    Female((byte) 2, "女");

    public static ESex getSex (int _value)
    {
        for (ESex sex : ESex.values())
        {
            if (sex.value == _value)
            {
                return sex;
            }

        }

        return null;
    }

    private byte   value;

    private String desc;

    ESex(byte _value, String _desc)
    {
        value = _value;
        desc = _desc;
    }

    public byte value ()
    {
        return value;
    }

    public String getDesc ()
    {
        return desc;
    }
}
