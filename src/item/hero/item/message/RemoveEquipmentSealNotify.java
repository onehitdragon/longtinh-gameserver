package hero.item.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 RemoveEquipmentSealNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-10-27 下午01:55:47
 * @描述 ：解除装备封印通知
 */

public class RemoveEquipmentSealNotify extends AbsResponseMessage
{
    /**
     * 装备包位置
     */
    private int bagGridIndex;

    /**
     * 装备实例编号
     */
    private int equipmentInstanceID;

    /**
     * 构造
     * 
     * @param _bagGridIndex
     * @param _equipmentInstanceID
     */
    public RemoveEquipmentSealNotify(int _bagGridIndex, int _equipmentInstanceID)
    {
        bagGridIndex = _bagGridIndex;
        equipmentInstanceID = _equipmentInstanceID;
    }

    @Override
    public int getPriority ()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        // TODO Auto-generated method stub
        yos.writeByte(bagGridIndex);
        yos.writeInt(equipmentInstanceID);
    }

}
