package hero.share;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ObjectType.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-31 下午12:31:08
 * @描述 ：游戏对象类型
 */

public enum EObjectType
{
    NPC(1), PLAYER(2), MONSTER(3), PET(4);

    private byte type;

    /**
     * 构造
     * 
     * @param _type
     */
    EObjectType(int _type)
    {
        type = (byte) _type;
    }

    /**
     * 获取类型标识数值
     * 
     * @return
     */
    public byte value ()
    {
        return type;
    }
}
