package hero.ui.data;

import hero.item.EquipmentInstance;
import hero.item.SingleGoods;
import hero.item.SpecialGoods;
import hero.item.Weapon;
import hero.item.bag.EquipmentContainer;
import hero.item.bag.SingleGoodsBag;
import hero.item.detail.EGoodsType;
import hero.item.service.GoodsServiceImpl;
import hero.share.CharacterDefine;

import java.io.IOException;

import yoyo.tools.YOYOOutputStream;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ActiveGoodsBagData.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-20 上午11:28:46
 * @描述 ：
 */

public class ActiveGoodsBagData
{
    public static byte[] getData (EquipmentContainer _equipmentPackage)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(EGoodsType.EQUIPMENT.value());
            EquipmentInstance[] equipmentDataList = _equipmentPackage
                    .getEquipmentList();

            output.writeByte(equipmentDataList.length);
            output.writeByte(_equipmentPackage.getFullGridNumber());

            for (int i = 0; i < equipmentDataList.length; i++)
            {
                EquipmentInstance instance = equipmentDataList[i];

                if (null != instance)
                {
                    output.writeByte(i);// 物品在背包中的位置
                    output.writeInt(instance.getInstanceID());// 物品在背包中的标识（装备采用模型编号或者实例的编号）
                    output.writeShort(instance.getArchetype().getIconID());// 物品图标
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

                    if (instance.getArchetype() instanceof Weapon)
                    {
                        output.writeByte(1);// 武器
//                        output.writeUTF(instance.getGeneralEnhance().getUpString());
                        output.writeBytes(instance.getArchetype()
                                .getFixPropertyBytes());//获得固定属性字段 (力量等属性. 详见hero.item.dictionary.setFixPropertyBytes())
                        output.writeByte(instance.isBind());//是否绑定
                        output.writeByte(instance.existSeal());//将要废弃的字段
                        output.writeShort(instance.getCurrentDurabilityPoint());//耐久
                        output.writeInt(instance.getArchetype()
                                .getRetrievePrice());//回收价格

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
                    else
                    {
                        output.writeByte(2);// 防具
                        output.writeBytes(instance.getArchetype()
                                .getFixPropertyBytes());
                        output.writeByte(instance.isBind());
                        output.writeByte(instance.existSeal());
                        output.writeShort(instance.getCurrentDurabilityPoint());
                        output.writeInt(instance.getArchetype()
                                .getRetrievePrice());
                        
                        //edit by zhengl; date: 2011-03-08; note: 新强化信息下发
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

    public static byte[] getData (SingleGoodsBag _singleGoodsPackage,
            EGoodsType _goodsType)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(_goodsType.value());
            int[][] singleGoodsDataList = _singleGoodsPackage.getAllItem();

            output.writeByte(singleGoodsDataList.length);
            output.writeByte(_singleGoodsPackage.getFullGridNumber());
            for (int i = 0; i < singleGoodsDataList.length; i++)
            {
                int[] goodsData = singleGoodsDataList[i];

                if (goodsData[0] != 0)
                {
                    SingleGoods goods = (SingleGoods) GoodsServiceImpl
                            .getInstance().getGoodsByID(goodsData[0]);

                    if (null != goods)
                    {
                        output.writeByte(i);// 物品在背包中的位置
                        output.writeInt(goods.getID());// 物品在背包中的标识（装备采用模型编号或者实例的编号）
                        output.writeShort(goods.getIconID());// 物品图标
                        output.writeUTF(goods.getName());// 物品名称
                        output.writeByte(goods.getTrait().value());// 物品品质
                        output.writeShort(goodsData[1]);// 物品数量
                        output.writeByte(goodsData[1]);// 物品可操作的最大数量
                        output.writeInt(goods.getSellPrice());
                        //add by zhengl; date: 2011-04-27; note: 物品的使用等级.
                        int level = 1;
                        if (goods.getNeedLevel() > 1) 
                        {
                        	level = goods.getNeedLevel();
						}
                        output.writeShort(level);

                        if (goods.getGoodsType() == EGoodsType.SPECIAL_GOODS)
                        {
                            if (((SpecialGoods) goods).canBeSell())
                            {
                                output.writeUTF(goods.getDescription()
                                        + CharacterDefine.DESC_NEW_LINE_CHAR
                                        + "出售价格：" + goods.getRetrievePrice());
                            }
                            else
                            {
                                output.writeUTF(goods.getDescription()
                                        + CharacterDefine.DESC_NEW_LINE_CHAR
                                        + "不可出售");
                            }
                        }
                        else
                        {
                            output.writeUTF(goods.getDescription()
                                    + CharacterDefine.DESC_NEW_LINE_CHAR
                                    + "出售价格：" + goods.getRetrievePrice());// 物品描述
                        }

                        output.writeByte(goods.exchangeable() ? 1 : 0);
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
