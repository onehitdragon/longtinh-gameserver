package hero.item.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


public class PetClothesOrWeaponChangeNotify extends AbsResponseMessage
{
	
	/**
     * 宠物ID
     */
    private int   petID;

    /**
     * 装备类型（衣服、武器）
     */
    private byte  equipmentType;

    /**
     * 宠物装备不显示在宠物身上
     * 所以用新的图标编号,显示在已装备上的装备栏中
     */
    private short iconID;
    
    

	public PetClothesOrWeaponChangeNotify(int petID, byte equipmentType, short iconID)
	{
		this.petID = petID;
		this.equipmentType = equipmentType;
		this.iconID = iconID;
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
		yos.writeInt(petID);
        yos.writeByte(equipmentType);
        yos.writeShort(iconID);
		
	}

}
