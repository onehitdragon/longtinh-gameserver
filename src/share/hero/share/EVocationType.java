package hero.share;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EVocationType.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-17 下午03:22:35
 * @描述 ：
 */

public enum EVocationType
{
	/**
	 * 战士
	 */
    PHYSICS(1), 
    
    /**
     * 游侠
     */
    RANGER(2), 
    /**
     * 法师
     */
    MAGIC(3), 

    /**
     * 牧师
     */
    PRIEST(4);

    int id;

    EVocationType(int _id)
    {
        id = _id;
    }

    public int getID ()
    {
        return id;
    }
}
