package hero.ui.message;

import hero.item.EquipmentInstance;
import hero.item.PetEquipment;
import hero.item.Weapon;
import hero.item.Armor;
import hero.share.CharacterDefine;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseEuipmentPackageChange.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-11 下午12:56:14
 * @描述 ：
 */

public class ResponseEuipmentPackageChange extends AbsResponseMessage
{
    /**
     * 变化的UI类型
     */
    private byte              uiType;

    /**
     * 发生变化的位置
     */
    private int               gridIndex;

    /**
     * 格子内的物品
     */
    private EquipmentInstance equipmentIns;

    /**
     * 构造
     * 
     * @param _uiType 背包类型
     * @param _change 变化
     * @param _goods 物品
     */
    public ResponseEuipmentPackageChange(byte _uiType, int _gridIndex,
            EquipmentInstance _ei)
    {
        uiType = _uiType;
        gridIndex = _gridIndex;
        equipmentIns = _ei;
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
        yos.writeByte(uiType);

        if (null != equipmentIns)
        {
            yos.writeByte(1);
            yos.writeByte(gridIndex);
            yos.writeInt(equipmentIns.getInstanceID());
            yos.writeShort(equipmentIns.getArchetype().getIconID());
            //edit by zhengl; date: 2011-03-13; note: 物品名称下发
            StringBuffer name = new StringBuffer();
            name.append(equipmentIns.getArchetype().getName());
            int level = equipmentIns.getGeneralEnhance().getLevel();
            if(level > 0)
            {
            	name.append("+");
            	name.append(level);
            }
            int flash = equipmentIns.getGeneralEnhance().getFlash();
            if(flash > 0)
            {
            	name.append("(闪");
            	name.append(flash);
            	name.append(")");
            }
            yos.writeUTF(name.toString());// 物品名称

            if (equipmentIns.getArchetype() instanceof Weapon)
            {
                yos.writeByte(1);// 玩家武器
                yos.writeBytes(equipmentIns.getArchetype()
                        .getFixPropertyBytes());
                yos.writeByte(equipmentIns.isBind());
                yos.writeByte(equipmentIns.existSeal());
                yos.writeShort(equipmentIns.getCurrentDurabilityPoint());
                yos.writeInt(equipmentIns.getArchetype().getRetrievePrice());

                //edit by zhengl; date: 2011-03-13; note: 新强化信息下发
                yos.writeUTF(equipmentIns.getGeneralEnhance().getUpEndString());
				yos.writeShort(equipmentIns.getGeneralEnhance().getFlashView()[0]);
				yos.writeShort(equipmentIns.getGeneralEnhance().getFlashView()[1]);
                yos.writeByte(equipmentIns.getGeneralEnhance().detail.length);
                for (int j = 0; j < equipmentIns.getGeneralEnhance().detail.length; j++) {
        			if(equipmentIns.getGeneralEnhance().detail[j][0] == 1){
        				//0,0,1,2,3 = 0,1,2,3,4 = 未镶嵌,空孔,碎,正常,闪光
        				yos.writeByte(equipmentIns.getGeneralEnhance().detail[j][1] +1);
        			} else {
        				yos.writeByte(0);
        			}
        		}
                //end
            }
            else if(equipmentIns.getArchetype() instanceof Armor)
            {
                yos.writeByte(2);// 玩家防具
                yos.writeBytes(equipmentIns.getArchetype()
                        .getFixPropertyBytes());
                yos.writeByte(equipmentIns.isBind());
                yos.writeByte(equipmentIns.existSeal());
                yos.writeShort(equipmentIns.getCurrentDurabilityPoint());
                yos.writeInt(equipmentIns.getArchetype().getRetrievePrice());
                
                //edit by zhengl; date: 2011-03-13; note: 新强化信息下发
                yos.writeUTF(equipmentIns.getGeneralEnhance().getUpEndString());
				yos.writeShort(equipmentIns.getGeneralEnhance().getArmorFlashView()[0]);
				yos.writeShort(equipmentIns.getGeneralEnhance().getArmorFlashView()[1]);
                yos.writeByte(equipmentIns.getGeneralEnhance().detail.length);
                for (int j = 0; j < equipmentIns.getGeneralEnhance().detail.length; j++) {
        			if(equipmentIns.getGeneralEnhance().detail[j][0] == 1){
        				//0,0,1,2,3 = 0,1,2,3,4 = 未镶嵌,空孔,碎,正常,闪光
        				yos.writeByte(equipmentIns.getGeneralEnhance().detail[j][1] +1);
        			} else {
        				yos.writeByte(0);
        			}
        		}
                //end
            } else if (equipmentIns.getArchetype() instanceof PetEquipment){
            	yos.writeByte(3);// 宠物装备
                yos.writeBytes(equipmentIns.getArchetype()
                        .getFixPropertyBytes());
                yos.writeByte(equipmentIns.isBind());
                yos.writeByte(equipmentIns.existSeal());
                yos.writeShort(equipmentIns.getCurrentDurabilityPoint());
                yos.writeInt(equipmentIns.getArchetype().getRetrievePrice());
            }
        }
        else
        {
            yos.writeByte(0);
            yos.writeByte(gridIndex);
        }
    }
}
