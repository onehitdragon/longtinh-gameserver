package hero.micro.store.message;

import hero.item.SpecialGoods;
import hero.item.Weapon;
import hero.item.detail.EGoodsType;
import hero.micro.store.PersionalStore;
import hero.micro.store.PersionalStore.GoodsForSale;
import hero.share.CharacterDefine;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 OtherStoreGoodsList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-24 下午05:34:43
 * @描述 ：
 */

public class OtherStoreGoodsList extends AbsResponseMessage
{
    /**
     * 商店
     */
    private PersionalStore store;

    /**
     * 构造
     * 
     * @param _store
     */
    public OtherStoreGoodsList(PersionalStore _store)
    {
        store = _store;
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
        yos.writeByte(store.goodsNumber);
        yos.writeUTF(store.name);

        GoodsForSale[] goodsList = store.goodsList;
        GoodsForSale goods;

        for (int i = 0; i < PersionalStore.MAX_SIZE; i++)
        {
            goods = goodsList[i];

            if (null != goods)
            {
                yos.writeByte(goods.goodsType);
                yos.writeByte(i);

                if (goods.goodsType == EGoodsType.EQUIPMENT.value())
                {
                    yos.writeInt(goods.equipment.getInstanceID());// 物品在背包中的标识（装备采用模型编号或者实例的编号）
                    yos.writeShort(goods.equipment.getArchetype()
                            .getIconID());// 物品图标
                    //edit by zhengl; date: 2011-03-13; note: 物品名称下发
                    StringBuffer name = new StringBuffer();
                    name.append(goods.equipment.getArchetype().getName());
                    int level = goods.equipment.getGeneralEnhance().getLevel();
                    if(level > 0)
                    {
                    	name.append("+");
                    	name.append(level);
                    }
                    int flash = goods.equipment.getGeneralEnhance().getFlash();
                    if(flash > 0)
                    {
                    	name.append("(闪");
                    	name.append(flash);
                    	name.append(")");
                    }
                    yos.writeUTF(name.toString());// 物品名称

                    if (goods.equipment.getArchetype() instanceof Weapon)
                    {
                        yos.writeByte(1);// 武器
                        yos.writeBytes(goods.equipment.getArchetype()
                                .getFixPropertyBytes());
                        yos.writeByte(goods.equipment.isBind());
                        yos.writeByte(goods.equipment.existSeal());
                        yos.writeShort(goods.equipment
                                .getCurrentDurabilityPoint());
                        yos.writeInt(goods.equipment.getArchetype()
                                .getRetrievePrice());
                        //edit by zhengl; date: 2011-03-13; note: 新强化信息下发
                        yos.writeUTF(goods.equipment.getGeneralEnhance().getUpEndString());
						yos.writeShort(goods.equipment.getGeneralEnhance().getFlashView()[0]);
						yos.writeShort(goods.equipment.getGeneralEnhance().getFlashView()[1]);
                        yos.writeByte(goods.equipment.getGeneralEnhance().detail.length);
                        for (int j = 0; j < goods.equipment.getGeneralEnhance().detail.length; j++) {
                			if(goods.equipment.getGeneralEnhance().detail[j][0] == 1){
                				//0,0,1,2,3 = 0,1,2,3,4 = 未镶嵌,空孔,碎,正常,闪光
                				yos.writeByte(goods.equipment.getGeneralEnhance().detail[j][1] +1);
                			} else {
                				yos.writeByte(0);
                			}
                		}
                        //end
                    }
                    else
                    {
                        yos.writeByte(2);// 防具
                        yos.writeBytes(goods.equipment.getArchetype()
                                .getFixPropertyBytes());
                        yos.writeByte(goods.equipment.isBind());
                        yos.writeByte(goods.equipment.existSeal());
                        yos.writeShort(goods.equipment
                                .getCurrentDurabilityPoint());
                        yos.writeInt(goods.equipment.getArchetype()
                                .getRetrievePrice());
                        //edit by zhengl; date: 2011-03-13; note: 新强化信息下发
                        yos.writeUTF(goods.equipment.getGeneralEnhance().getUpEndString());
						yos.writeShort(goods.equipment.getGeneralEnhance().getArmorFlashView()[0]);
						yos.writeShort(goods.equipment.getGeneralEnhance().getArmorFlashView()[1]);
                        yos.writeByte(goods.equipment.getGeneralEnhance().detail.length);
                        for (int j = 0; j < goods.equipment.getGeneralEnhance().detail.length; j++) {
                			if(goods.equipment.getGeneralEnhance().detail[j][0] == 1){
                				//0,0,1,2,3 = 0,1,2,3,4 = 未镶嵌,空孔,碎,正常,闪光
                				yos.writeByte(goods.equipment.getGeneralEnhance().detail[j][1] +1);
                			} else {
                				yos.writeByte(0);
                			}
                		}
                        //end
                    }
                }
                else
                {
                    yos.writeInt(goods.singleGoods.getID());// 物品在背包中的标识（装备采用模型编号或者实例的编号）
                    yos.writeShort(goods.singleGoods.getIconID());// 物品图标
                    yos.writeUTF(goods.singleGoods.getName());// 物品名称
                    yos.writeByte(goods.singleGoods.getTrait().value());// 物品品质
                    yos.writeShort(goods.number);// 物品数量
                    yos.writeByte(goods.number);// 物品可操作的最大数量

                    yos.writeInt(goods.singleGoods.getRetrievePrice());
                    yos.writeShort(goods.singleGoods.getNeedLevel());

                    if (goods.singleGoods.getGoodsType() == EGoodsType.SPECIAL_GOODS)
                    {
                        if (((SpecialGoods) goods.singleGoods).canBeSell())
                        {
                            yos.writeUTF(goods.singleGoods.getDescription()
                                    + CharacterDefine.DESC_NEW_LINE_CHAR
                                    + "出售价格："
                                    + goods.singleGoods.getRetrievePrice());
                        }
                        else
                        {
                            yos.writeUTF(goods.singleGoods.getDescription()
                                    + CharacterDefine.DESC_NEW_LINE_CHAR
                                    + "不可出售");
                        }
                    }
                    else
                    {
                        yos.writeUTF(goods.singleGoods.getDescription()
                                + CharacterDefine.DESC_NEW_LINE_CHAR + "出售价格："
                                + goods.singleGoods.getRetrievePrice());// 物品描述
                    }

                    yos.writeByte(goods.singleGoods.exchangeable() ? 1 : 0);
                }

                yos.writeInt(goods.salePrice);
            }
        }
    }
}
