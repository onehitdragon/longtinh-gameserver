package hero.pet.message;

import hero.pet.Pet;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponsePetList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-13 上午10:47:59
 * @描述 ：
 */

/**
 * 只用于显示已死亡宠物列表
 */
public class ResponsePetList extends AbsResponseMessage
{
	private static Logger log = Logger.getLogger(ResponsePetList.class);
    /**
     * 宠物列表
     */
    private List<Pet> petList;

    /**
     * 自动贩卖物品品质
     */
    private byte           autoSellTrait;

    /**
     * 构造
     * 
     * @param _petList
     */
    public ResponsePetList(List<Pet> _petList, byte _autoSellTrait)
    {
        petList = _petList;
        autoSellTrait = _autoSellTrait;
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
        if (null == petList)
        {
            yos.writeByte(0);

            return;
        }
        yos.writeByte(petList.size());

        for (Pet pet : petList)
        {
        	log.debug(pet.id + " " + pet.pk.getStage() +" "+pet.pk.getKind()+" "+pet.pk.getType()+"  " +
        			" " +pet.color+"  " + pet.iconID +"  " + pet.imageID +"  " + pet.name);
            yos.writeInt(pet.id);
            yos.writeShort(pet.pk.getStage());
            yos.writeShort(pet.pk.getKind());
            yos.writeShort(pet.pk.getType());
            yos.writeByte(pet.color);
            yos.writeShort(pet.iconID);
            yos.writeShort(pet.imageID);
        	//add by zhengl ; date: 2011-01-17 ; note: 添加动画
        	yos.writeShort(pet.animationID);
            yos.writeUTF(pet.name);
            yos.writeInt(pet.feeding);
        }

        if (petList.size() > 0)
        {
            yos.writeByte(autoSellTrait);
        }
    }
}
