package hero.ui;

import hero.item.Armor;
import hero.item.Equipment;
import hero.item.Goods;
import hero.item.Weapon;
import hero.item.dictionary.GoodsContents;
import hero.npc.function.system.auction.AuctionGoods;
import hero.npc.function.system.auction.AuctionType;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import yoyo.tools.YOYOOutputStream;

public class UI_AuctionGoodsList
{
    private static Logger log = Logger.getLogger(UI_AuctionGoodsList.class);
    /**
     * 获取UI绘制数据字节数组
     * 
     * @return
     */
    public static byte[] getBytes (short _pageNum,
            ArrayList<AuctionGoods> _auctionGoodsList, String[] _menuList)
    {
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());
            output.writeShort(_pageNum);
            output.writeByte(_auctionGoodsList.size());

            Goods goods;

            for (AuctionGoods auctionGoods : _auctionGoodsList)
            {
                output.writeInt(auctionGoods.getAuctionID());// 拍卖ID

                if (auctionGoods.getAuctionType() == AuctionType.MATERIAL
                        || auctionGoods.getAuctionType() == AuctionType.MEDICAMENT
                        || auctionGoods.getAuctionType() == AuctionType.SPECIAL)
                {
                    goods = GoodsContents.getGoods(auctionGoods.getGoodsID());

                    if (goods == null)
                    {
                        log.info("药水为空：" + auctionGoods.getGoodsID());
                    }

                    output.writeShort(goods.getIconID());// 图标
                    output.writeUTF(goods.getName());// 物品名称
                    output.writeShort(auctionGoods.getNum());// 装备的数量为1
                    output.writeInt(auctionGoods.getPrice());// 拍卖价格
                    output.writeByte(3);// 非装备
                    output.writeUTF(goods.getDescription());// 物品介绍
                    output.writeByte(goods.getTrait().value());// 物品品质
                    output.writeShort(goods.getNeedLevel());// 需要等级
                }
                else
                {
                    Equipment e = (Equipment)auctionGoods.getInstance().getArchetype();
                    output.writeShort(e.getIconID());// 图标
                    //edit by zhengl; date: 2011-03-13; note: 物品名称下发
                    StringBuffer name = new StringBuffer();
                    name.append(e.getName());
                    int level = auctionGoods.getInstance().getGeneralEnhance().getLevel();
                    if(level > 0)
                    {
                    	name.append("+");
                    	name.append(level);
                    }
                    int flash = auctionGoods.getInstance().getGeneralEnhance().getFlash();
                    if(flash > 0)
                    {
                    	name.append("(闪");
                    	name.append(flash);
                    	name.append(")");
                    }
                    output.writeUTF(name.toString());// 物品名称
                    output.writeShort(1);// 装备的数量为1
                    output.writeInt(auctionGoods.getPrice());// 拍卖价格

                    if (e instanceof Weapon)
                    {
                        output.writeByte(1);// 武器
                        output.writeBytes(e.getFixPropertyBytes());
                        output.writeByte(0);
                        output
                                .writeByte(auctionGoods.getInstance()
                                        .existSeal());
                        output.writeShort(auctionGoods.getInstance()
                                .getCurrentDurabilityPoint());
                        output.writeInt(e.getRetrievePrice());
                        //edit by zhengl; date: 2011-03-13; note: 新强化信息下发
                        output.writeUTF(
                        		auctionGoods.getInstance().getGeneralEnhance().getUpEndString());
						output.writeShort(auctionGoods.getInstance().getGeneralEnhance().getFlashView()[0]);
						output.writeShort(auctionGoods.getInstance().getGeneralEnhance().getFlashView()[1]);
                        output.writeByte(auctionGoods.getInstance().getGeneralEnhance().detail.length);
                        for (int j = 0; j < auctionGoods.getInstance().getGeneralEnhance().detail.length; j++) {
                			if(auctionGoods.getInstance().getGeneralEnhance().detail[j][0] == 1){
                				//0,0,1,2,3 = 0,1,2,3,4 = 未镶嵌,空孔,碎,正常,闪光
                				output.writeByte(auctionGoods.getInstance().getGeneralEnhance().detail[j][1] +1);
                			} else {
                				output.writeByte(0);
                			}
                		}
                        //end
                    }
                    else if (e instanceof Armor)
                    {
                        output.writeByte(2);// 防具
                        output.writeBytes(e.getFixPropertyBytes());
                        output.writeByte(0);
                        output.writeByte(e.existSeal());
                        output.writeShort(auctionGoods.getInstance()
                                .getCurrentDurabilityPoint());
                        output.writeInt(e.getRetrievePrice());
                        
                        //edit by zhengl; date: 2011-03-13; note: 新强化信息下发
                        output.writeUTF(
                        		auctionGoods.getInstance().getGeneralEnhance().getUpEndString());
						output.writeShort(
								auctionGoods.getInstance().getGeneralEnhance().getArmorFlashView()[0]);
						output.writeShort(
								auctionGoods.getInstance().getGeneralEnhance().getArmorFlashView()[1]);
                        output.writeByte(
                        		auctionGoods.getInstance().getGeneralEnhance().detail.length);
                        for (int j = 0; j < auctionGoods.getInstance().getGeneralEnhance().detail.length; j++) {
                			if(auctionGoods.getInstance().getGeneralEnhance().detail[j][0] == 1){
                				//0,0,1,2,3 = 0,1,2,3,4 = 未镶嵌,空孔,碎,正常,闪光
                				output.writeByte(
                						auctionGoods.getInstance().getGeneralEnhance().detail[j][1] +1);
                			} else {
                				output.writeByte(0);
                			}
                		}
                        //end
                    }
                }
            }

            output.writeByte(_menuList.length);

            for (String menu : _menuList)
            {
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

    /**
     * 获取UI类型
     * 
     * @return
     */
    public static EUIType getType ()
    {
        return EUIType.AUCTION_GOODS_LIST;
    }
}
