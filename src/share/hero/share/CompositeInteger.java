package hero.share;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 CompositeInteger.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-4-9 下午03:18:54
 * @描述 ：自定义整形数值型对象
 */

public class CompositeInteger
{
    private int value;

    public CompositeInteger()
    {
        value = 0;
    }

    public CompositeInteger(int _value)
    {
        value = -value;
    }

    /**
     * 自增
     */
    public int increasing ()
    {
        return ++value;
    }

    /**
     * 递减
     */
    public int decreasing ()
    {
        return --value;
    }

    public int value ()
    {
        return value;
    }

    /**
     * 加
     * 
     * @param _value
     * @return
     */
    public int add (float _value)
    {
        value += _value;

        return value;
    }

    /**
     * 减
     * 
     * @param _value
     * @return
     */
    public int reduce (float _value)
    {
        value -= _value;

        return value;
    }

    /**
     * 乘积
     * 
     * @param _value
     * @return
     */
    public int product (float _value)
    {
        value *= _value;

        return value;
    }

    /**
     * 除以
     * 
     * @param _value
     * @return
     */
    public int divide (float _value)
    {
        value *= _value;

        return value;
    }
}
