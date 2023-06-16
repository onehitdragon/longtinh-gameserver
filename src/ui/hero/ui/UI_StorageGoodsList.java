package hero.ui;

import hero.item.Equipment;
import hero.item.Goods;
import hero.item.service.GoodsServiceImpl;
import hero.npc.function.system.storage.Warehouse;
import hero.npc.function.system.storage.WarehouseGoods;
import hero.share.CharacterDefine;
import hero.ui.data.GridDataTypeDefine;

import java.io.IOException;
import java.util.ArrayList;

import yoyo.tools.YOYOOutputStream;


public class UI_StorageGoodsList
{
    public static byte[] getBytes (String[] _menuList, Warehouse _warehouse)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());
            output.writeBytes(getData(_warehouse));
            output.writeByte(_menuList.length);

            for (int i = 0; i < _menuList.length; i++)
            {
                String menu = _menuList[i];
                output.writeUTF(menu);
                output.writeByte(0);
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

    private static byte[] getData (Warehouse _warehouse)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(GridDataTypeDefine.STORAGE_GOODS_LIST);
            ArrayList<WarehouseGoods> goodsList = _warehouse.getGoodsList();

            output.writeByte(goodsList.size());
            output.writeByte(_warehouse.getGoodsNum());

            for (int i = 0; i < goodsList.size(); i++)
            {
                WarehouseGoods wgoods = goodsList.get(i);

                if (wgoods != null)
                {
                    Goods goods = null;

                    if (wgoods.goodsType == 0)
                    {
                        goods = wgoods.instance.getArchetype();
                    }
                    else
                    {
                        goods = GoodsServiceImpl.getInstance().getGoodsByID(
                                wgoods.goodsID);
                    }

                    if (null != goods)
                    {
                        output.writeByte(i);// 物品在背包中的位置
                        output.writeInt(wgoods.goodsID);// 物品在背包中的标识（装备采用模型编号或者实例的编号）
                        output.writeShort(goods.getIconID());// 物品图标
                        output.writeShort(goods.getNeedLevel());
                        //edit by zhengl; date: 2011-04-10; note: 物品名称下发
                        if (goods instanceof Equipment) {
                            StringBuffer name = new StringBuffer();
                            name.append(wgoods.instance.getArchetype().getName());
                            int level = wgoods.instance.getGeneralEnhance().getLevel();
                            if(level > 0)
                            {
                            	name.append("+");
                            	name.append(level);
                            }
                            int flash = wgoods.instance.getGeneralEnhance().getFlash();
                            if(flash > 0)
                            {
                            	name.append("(闪");
                            	name.append(flash);
                            	name.append(")");
                            }
                            output.writeUTF(name.toString());// 物品名称
						} else {
							output.writeUTF(goods.getName());
						}


                        if (goods instanceof Equipment)
                        {
                            output.writeByte(TYPE_OF_EQUIPMENT);
                            output.writeBytes(((Equipment) goods).getFixPropertyBytes());
//                            //edit by zhengl; date: 2011-04-10; note: 新强化信息下发
//                            output.writeUTF(instance.getGeneralEnhance().getUpEndString());
//    						output.writeShort(instance.getGeneralEnhance().getFlashView()[0]);
//    						output.writeShort(instance.getGeneralEnhance().getFlashView()[1]);
//                            output.writeByte(instance.getGeneralEnhance().detail.length);
//                            for (int j = 0; j < instance.getGeneralEnhance().detail.length; j++) {
//                    			if(instance.getGeneralEnhance().detail[j][0] == 1){
//                    				//0,0,1,2,3 = 0,1,2,3,4 = 未镶嵌,空孔,碎,正常,闪光
//                    				output.writeByte(instance.getGeneralEnhance().detail[j][1] +1);
//                    			} else {
//                    				output.writeByte(0);
//                    			}
//                    		}
//                            //end
                            output.writeByte(wgoods.instance.isBind());
                            output.writeByte(wgoods.instance.existSeal());
                            output.writeShort(((Equipment) goods).getMaxDurabilityPoint());
                            output.writeShort(wgoods.goodsNum);// 物品数量，限量出售的物品数量发送负数-
                        }
                        else
                        {
                            output.writeByte(TYPE_OF_SINGLE_GOODS);
                            output.writeByte(goods.getTrait().value());
                            output.writeUTF(goods.getDescription());// 物品描述
                            output.writeShort(wgoods.goodsNum);// 物品数量，限量出售的物品数量发送负数-
                        }
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

    /**
     * 获取UI类型
     * 
     * @return
     */
    public static EUIType getType ()
    {
        return EUIType.GOODS_OPERATE_LIST;
    }

//    private static final String DURA_HEADER          = "耐久度　：";
//
//    private static final String DURA_MIDDLE          = "/";

    /**
     * 非装备物品
     */
    public static final byte    TYPE_OF_SINGLE_GOODS = 1;

    /**
     * 装备
     */
    public static final byte    TYPE_OF_EQUIPMENT    = 2;
}
