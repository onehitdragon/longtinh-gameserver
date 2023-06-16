package hero.ui.data;

import hero.expressions.service.CEService;
import hero.item.Armor;
import hero.item.EquipmentInstance;
import hero.item.PetArmor;
import hero.item.PetWeapon;
import hero.item.Weapon;
import hero.item.Armor.ArmorType;
import hero.item.bag.EquipmentBag;
import hero.item.bag.EquipmentContainer;
import hero.item.detail.EBodyPartOfEquipment;
import hero.pet.Pet;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import yoyo.tools.YOYOOutputStream;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EquipmentPackage.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-5 上午09:55:47
 * @描述 ：
 */

public class EquipmentPackageData
{
	private static Logger log = Logger.getLogger(EquipmentPackageData.class);
	
    public static byte[] getData (EquipmentContainer _equipmentPackage, String _tabName)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(GridDataTypeDefine.PACKAGE_EQUIPMENT_LIST);
            EquipmentInstance[] equipmentDataList = _equipmentPackage
                    .getEquipmentList();

            output.writeUTF(_tabName);//加页签名 
            output.writeByte(equipmentDataList.length);//格子数
            log.debug("@@@ player equip bag fullnumber = " + _equipmentPackage.getFullGridNumber());

            output.writeByte(_equipmentPackage.getFullGridNumber());

            for (int i = 0; i < equipmentDataList.length; i++)
            {
                EquipmentInstance instance = equipmentDataList[i];
                int money = 0;
                if (null != instance)
                {
                	money = CEService.sellPriceOfEquipment(instance
                            .getArchetype().getSellPrice(), instance
                            .getCurrentDurabilityPoint(), instance
                            .getArchetype().getMaxDurabilityPoint());
                    output.writeByte(i);// 物品在背包中的位置
                    output.writeInt(instance.getInstanceID());// 物品在背包中的标识（装备采用模型编号或者实例的编号）
                    output.writeShort(instance.getArchetype().getIconID());// 物品图标(新版游戏改为动画ID但实际还是使用这个变量)
                    //暂时不用加这个
//                    if(instance.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.BOSOM 
//                    		|| instance.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.HEAD) {
//                    	//如果是头,胸部等展示部位那么则考虑是否区分种族来展示
//                    	output.writeByte( ((Armor)instance.getArchetype()).getDistinguish() );
//                    }
                    //edit by zhengl; date: 2011-03-13; note: 物品名称下发
                    StringBuffer name = new StringBuffer();
                    name.append(instance.getArchetype().getName());
                    int level = instance.getGeneralEnhance().getLevel();
                    if(level > 0)
                    {
                    	name.append("+");
                    	name.append(level);
                    }
                    int flash = instance.getGeneralEnhance().getFlash();
                    if(flash > 0)
                    {
                    	name.append("(闪");
                    	name.append(flash);
                    	name.append(")");
                    }
                    output.writeUTF(name.toString());// 物品名称
                    //end
                    log.debug("player body equip = " + instance.getArchetype().getName());
                    if (instance.getArchetype() instanceof Weapon)
                    {
                        output.writeByte(1);// 武器
                        output.writeBytes(instance.getArchetype().getFixPropertyBytes());
                        output.writeByte(instance.isBind());
                        output.writeByte(instance.existSeal());
                        output.writeShort(instance.getCurrentDurabilityPoint());
                        output.writeInt(money);
                        //edit by zhengl; date: 2011-03-08; note: 新强化信息下发
                        output.writeUTF(instance.getGeneralEnhance().getUpEndString());
						output.writeShort(instance.getGeneralEnhance().getFlashView()[0]);
						output.writeShort(instance.getGeneralEnhance().getFlashView()[1]);
                        output.writeByte(instance.getGeneralEnhance().detail.length);
                        for (int j = 0; j < instance.getGeneralEnhance().detail.length; j++) {
                			if(instance.getGeneralEnhance().detail[j][0] == 1){
                				//0,0,1,2,3 = 0,1,2,3,4 = 未镶嵌,空孔,碎,正常,闪光
                				output.writeByte(instance.getGeneralEnhance().detail[j][1] +1);
                			} else {
                				output.writeByte(0);
                			}
                		}
                        //end
                    }
                    else if(instance.getArchetype() instanceof Armor)
                    {
                        output.writeByte(2);// 防具
                        output.writeBytes(instance.getArchetype().getFixPropertyBytes());
                        output.writeByte(instance.isBind());
                        output.writeByte(instance.existSeal());
                        output.writeShort(instance.getCurrentDurabilityPoint());
                        output.writeInt(money);
                        //add by zhengl; date: 2011-03-08; note: 新强化信息下发
                        output.writeUTF(instance.getGeneralEnhance().getUpEndString());
                        if(instance.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.BOSOM) {
							output.writeShort(instance.getGeneralEnhance().getArmorFlashView()[0]);
							output.writeShort(instance.getGeneralEnhance().getArmorFlashView()[1]);
						} else {
							output.writeShort(-1);
							output.writeShort(-1);
						}
                        output.writeByte(instance.getGeneralEnhance().detail.length);
                        for (int j = 0; j < instance.getGeneralEnhance().detail.length; j++) {
                			if(instance.getGeneralEnhance().detail[j][0] == 1){
                				//0,0,1,2,3 = 0,1,2,3,4 = 未镶嵌,空孔,碎,正常,闪光
                				output.writeByte(instance.getGeneralEnhance().detail[j][1] +1);
                			} else {
                				output.writeByte(0);
                			}
                		}
                        //end
                    } else if(instance.getArchetype() instanceof PetWeapon){	
                    	log.debug("## EquipmentPackageData 宠物爪部");
                    	output.writeByte(3); //宠物武器(爪部)
                    	int size = instance.getArchetype().getFixPropertyBytes().length;
                    	log.debug("pet weapon property size = " + size );
                    	output.writeInt(size);
                    	output.writeBytes(instance.getArchetype().getFixPropertyBytes());
                    	output.writeByte(instance.isBind());
                    	output.writeInt(money);
                    	log.debug("## EquipmentPackageData 宠物爪部  end ");
                    }else if (instance.getArchetype() instanceof PetArmor){
                    	log.debug("## EquipmentPackageData 宠物防具");
                    	output.writeByte(4); //宠物防具
                    	int size = instance.getArchetype().getFixPropertyBytes().length;
                    	log.debug("pet armor property size = " + size );
                    	output.writeInt(size);
                    	output.writeBytes(instance.getArchetype().getFixPropertyBytes());
                    	output.writeByte(instance.isBind());
                    	output.writeInt(money);
                    	log.debug("## EquipmentPackageData 宠物防具  end");
                    }
                }
            }
            
            output.flush();

            return output.getBytes();
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return null;
        }
        finally
        {
            try
            {
                output.close();
            }
            catch (IOException e)
            {
            }
            output = null;
        }
    }

    public static byte[] getStorageData (EquipmentContainer _equipmentPackage)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(GridDataTypeDefine.PACKAGE_EQUIPMENT_LIST);
            EquipmentInstance[] equipmentDataList = _equipmentPackage
                    .getEquipmentList();

            output.writeByte(equipmentDataList.length);
            output.writeByte(_equipmentPackage.getFullGridNumber());

            for (int i = 0; i < equipmentDataList.length; i++)
            {
                EquipmentInstance instance = equipmentDataList[i];
                int money = 0;
                if (null != instance)
                {
                	money = CEService.sellPriceOfEquipment(instance
                            .getArchetype().getSellPrice(), instance
                            .getCurrentDurabilityPoint(), instance
                            .getArchetype().getMaxDurabilityPoint());
                    output.writeByte(i);// 物品在背包中的位置
                    output.writeInt(instance.getInstanceID());// 物品在背包中的标识（装备采用模型编号或者实例的编号）
                    output.writeShort(instance.getArchetype().getIconID());// 物品图标
                    output.writeUTF(instance.getArchetype().getName());// 物品名称

                    if (instance.getArchetype() instanceof Weapon)
                    {
                        output.writeByte(1);// 武器
                        output.writeBytes(instance.getArchetype()
                                .getFixPropertyBytes());
                        output.writeByte(instance.isBind());
                        output.writeByte(instance.existSeal());
                        output.writeShort(instance.getCurrentDurabilityPoint());
                        output.writeInt(money);

                        //edit by zhengl; date: 2011-03-13; note: 新强化信息下发
                        output.writeUTF(instance.getGeneralEnhance().getUpEndString());
						output.writeShort(instance.getGeneralEnhance().getFlashView()[0]);
						output.writeShort(instance.getGeneralEnhance().getFlashView()[1]);
                        output.writeByte(instance.getGeneralEnhance().detail.length);
                        for (int j = 0; j < instance.getGeneralEnhance().detail.length; j++) {
                			if(instance.getGeneralEnhance().detail[j][0] == 1){
                				//0,0,1,2,3 = 0,1,2,3,4 = 未镶嵌,空孔,碎,正常,闪光
                				output.writeByte(instance.getGeneralEnhance().detail[j][1] +1);
                			} else {
                				output.writeByte(0);
                			}
                		}
                        //end
                    }
                    else
                    {
                        output.writeByte(2);// 防具
                        output.writeBytes(instance.getArchetype()
                                .getFixPropertyBytes());
                        output.writeByte(instance.isBind());
                        output.writeByte(instance.existSeal());
                        output.writeShort(instance.getCurrentDurabilityPoint());
                        output.writeInt(money);
                        
                        //edit by zhengl; date: 2011-03-13; note: 新强化信息下发
                        output.writeUTF(instance.getGeneralEnhance().getUpEndString());
						output.writeShort(instance.getGeneralEnhance().getArmorFlashView()[0]);
						output.writeShort(instance.getGeneralEnhance().getArmorFlashView()[1]);
                        output.writeByte(instance.getGeneralEnhance().detail.length);
                        for (int j = 0; j < instance.getGeneralEnhance().detail.length; j++) {
                			if(instance.getGeneralEnhance().detail[j][0] == 1){
                				//0,0,1,2,3 = 0,1,2,3,4 = 未镶嵌,空孔,碎,正常,闪光
                				output.writeByte(instance.getGeneralEnhance().detail[j][1] +1);
                			} else {
                				output.writeByte(0);
                			}
                		}
                        //end
                    }
                }
            }

            output.flush();

            return output.getBytes();
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return null;
        }
        finally
        {
            try
            {
                output.close();
            }
            catch (IOException e)
            {
            }
            output = null;
        }
    }

    
}
