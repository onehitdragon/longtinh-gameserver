package hero.pet.message;

import hero.pet.clienthandler.OperatePet;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 PetChangeNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-12 上午10:38:16
 * @描述 ：
 */

public class PetChangeNotify extends AbsResponseMessage
{
    /**
     * 主人ObjectID
     */
    private int   ownerID;

    /**
     * 宠物变化类型
     */
    private byte  petChangeType;

    /**
     * 宠物图片编号
     */
    private short petImageID;
    
    /**
     * 宠物类型
     * 0:蛋或幼年   1:草食  2:肉食
     */
    private byte type;
   

    /**
     * 构造
     * 
     * @param _ownerID
     * @param _petID
     */
    public PetChangeNotify(int _ownerID, byte _petChangeType, short _petImageID, short _type)
    {
        ownerID = _ownerID;
        petChangeType = _petChangeType;
        petImageID = _petImageID;
        type = (byte)_type;
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
        yos.writeInt(ownerID);
        yos.writeByte(petChangeType);
       

        if (OperatePet.SHOW == petChangeType)
        {
            yos.writeShort(petImageID);
            yos.writeByte(type);
        }
        
        
    }

}
