package hero.item.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SendBagSize.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-8-3 上午10:28:42
 * @描述 ：发送背包大小（装备、药水、材料、特殊物品，宠物装备背包，宠物物品背包，宠物背包）
 */

public class SendBagSize extends AbsResponseMessage
{
    /**
     * 装备背包大小
     */
    private int equipmentBagSize;

    /**
     * 药水背包大小
     */
    private int medicamentBagSize;

    /**
     * 材料背包大小
     */
    private int materialBagSize;

    /**
     * 特殊物品背包大小
     */
    private int specialGoodsBagSize;
    /**
     * 宠物装备背包大小
     */
    private int petEquipmentBagSize;

    /**
     * 宠物物品背包大小
     */
    private int petGoodsBagSize;
    /**
     * 宠物背包大小
     */
    private int petBagSize;

    public SendBagSize(int _equipmentBagSize, int _medicamentBagSize,
            int _materialBagSize, int _specialGoodsBagSize, int _petEquipmentBagSize,int _petGoodsBagSize,int _petBagSize)
    {
        equipmentBagSize = _equipmentBagSize;
        medicamentBagSize = _medicamentBagSize;
        materialBagSize = _materialBagSize;
        specialGoodsBagSize = _specialGoodsBagSize;
        petEquipmentBagSize = _petEquipmentBagSize;
        petGoodsBagSize = _petGoodsBagSize;
        petBagSize = _petBagSize;
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
        yos.writeByte(equipmentBagSize);
        yos.writeByte(medicamentBagSize);
        yos.writeByte(materialBagSize);
        yos.writeByte(specialGoodsBagSize);
        yos.writeByte(petEquipmentBagSize);
        yos.writeByte(petGoodsBagSize);
        yos.writeByte(petBagSize);
    }

}
