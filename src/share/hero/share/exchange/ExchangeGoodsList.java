package hero.share.exchange;

import hero.item.EquipmentInstance;
import hero.item.Goods;
import hero.item.Weapon;
import hero.item.bag.EquipmentContainer;
import hero.item.bag.PetContainer;
import hero.item.bag.SingleGoodsBag;
import hero.item.detail.EGoodsTrait;
import hero.item.service.GoodsServiceImpl;
import hero.pet.Pet;
import hero.share.CharacterDefine;

import java.io.IOException;

import yoyo.tools.YOYOOutputStream;


/**
 * Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class ExchangeGoodsList
{
    private static int showNum (ExchangePlayer _eplayer, int _gridIndex, int num, int goodsID)
    {
        for (int i = 0; i < _eplayer.gridIndex.length; i++)
        {
//log.info.println("special goods _eplayer.gridIndex[i]="+i+",_eplayer.goodsID="+_eplayer.goodsID[i]+",num="+_eplayer.goodsNum[i]);
            if (_eplayer.gridIndex[i] == _gridIndex && _eplayer.goodsID[i] == goodsID)
            {
                num -= _eplayer.goodsNum[i];
            }
        }
        return num;
    }

    public static byte[] getData (SingleGoodsBag _singleGoodsPackage,
            ExchangePlayer _eplayer)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();
        YOYOOutputStream output1 = new YOYOOutputStream();

        try
        {
            int[][] singleGoodsDataList = _singleGoodsPackage.getAllItem();

            int num = 0;
            for (int i = 0; i < singleGoodsDataList.length; i++)
            {
                int[] goodsData = singleGoodsDataList[i];

                if (goodsData[0] != 0)
                {
                    Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(
                            goodsData[0]);

                    if (null != goods)
                    {
                        int n = showNum(_eplayer, i, goodsData[1],goodsData[0]);
//log.info.println("new show num goodsid = " + goodsData[0]+",currNum="+goodsData[1]+", after num="+n);
                        if (n > 0)
                        {
                            num++;
                            output.writeByte(i);// 物品在背包中的位置
                            output.writeInt(goods.getID());// 物品在背包中的标识（装备采用模型编号或者实例的编号）
                            output.writeShort(goods.getIconID());// 物品图标
                            output.writeUTF(goods.getName());// 物品名称
                            output.writeByte(goods.getTrait().value());// 物品品质
                            output.writeShort(n);// 物品数量
                            output.writeUTF(goods.getDescription()
                                    + CharacterDefine.DESC_NEW_LINE_CHAR
                                    + "价格：" + goods.getRetrievePrice());// 物品描述

                            output.writeByte(goods.exchangeable() ? 1 : 0);
                        }
                    }
                }
            }

            output.flush();

            output1.writeByte(singleGoodsDataList.length);
            output1.writeByte(num);
            output1.writeBytes(output.getBytes());
            output1.flush();

            return output1.getBytes();
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

    /**
     * 宠物列表
     * @param PetContainer
     * @param _eplayer
     * @return
     */
    public static byte[] getData (PetContainer petPackage,
            ExchangePlayer _eplayer)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();
        YOYOOutputStream output1 = new YOYOOutputStream();

        try
        {
            Pet[] petlist = petPackage.getPetList();
    		output.writeByte(petlist.length); //格子数
    		output.writeByte(petPackage.getFullGridNumber());//有宠物的格子数,客户端以这个值取宠物数据

    		Pet pet;
    		for(int i=0; i<petlist.length; i++){

    			pet = petlist[i];
    			if(null != pet){
    				output.writeByte(i);//格子位置
    				output.writeInt(pet.id);
    	            output.writeShort(pet.iconID);
    	            output.writeUTF(pet.name);
    	            output.writeByte(pet.trait.value());
    	            output.writeShort(1);
    	            output.writeUTF(pet.name);
    	            output.writeInt(pet.feeding);
                    output.writeByte(pet.bind == 1 ? 1 : 0);
    			}
    		}

            output.flush();

            output1.writeByte(petlist.length);
            output1.writeByte(1);
            output1.writeBytes(output.getBytes());
            output1.flush();

            return output1.getBytes();
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

    private static boolean isShow (ExchangePlayer _eplayer, int index, int goodsID)
    {
        for (int i = 0; i < _eplayer.gridIndex.length; i++)
        {
//log.info.println("is show = " + goodsID);
//log.info.println("_eplayer.gridIndex["+i+"] == "+index+" && _eplayer.goodsID="+_eplayer.goodsID[i]);
            if (_eplayer.gridIndex[i] == index && _eplayer.goodsID[i] == goodsID)
            {
                return true;
            }
        }
        return false;
    }

    public static byte[] getData (EquipmentContainer _equipmentPackage,
            ExchangePlayer _eplayer)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();
        YOYOOutputStream output1 = new YOYOOutputStream();

        try
        {
            EquipmentInstance[] equipmentDataList = _equipmentPackage
                    .getEquipmentList();

            int num = 0;

            for (int i = 0; i < equipmentDataList.length; i++)
            {
                EquipmentInstance instance = equipmentDataList[i];

                if (null != instance && !isShow(_eplayer,i, instance.getInstanceID()))
                {
                    num++;
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
                        output.writeBytes(instance.getArchetype()
                                .getFixPropertyBytes());
                        output.writeByte(instance.isBind());
                        output.writeByte(instance.existSeal());
                        output.writeShort(instance.getCurrentDurabilityPoint());
                        output.writeInt(instance.getArchetype()
                                .getRetrievePrice());

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
                        output.writeInt(instance.getArchetype()
                                .getRetrievePrice());
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

            output1.writeByte(equipmentDataList.length);
            output1.writeByte(num);
            output1.writeBytes(output.getBytes());
            output1.flush();
            return output1.getBytes();
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
