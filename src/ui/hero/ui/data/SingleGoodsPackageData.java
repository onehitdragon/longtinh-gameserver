package hero.ui.data;

import hero.item.Goods;
import hero.item.SingleGoods;
import hero.item.TaskTool;
import hero.item.bag.SingleGoodsBag;
import hero.item.detail.EGoodsType;
import hero.item.service.GoodsServiceImpl;
import hero.item.special.BigTonicBall;
import hero.item.special.PetPerCard;
import hero.item.SpecialGoods;
import hero.player.service.PlayerServiceImpl;
import hero.share.CharacterDefine;

import java.io.IOException;

import org.apache.log4j.Logger;

import yoyo.tools.YOYOOutputStream;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SinglePackageData.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-5 上午10:46:32
 * @描述 ：
 */

public class SingleGoodsPackageData
{
    private static Logger log = Logger.getLogger(SingleGoodsPackageData.class);
    public static byte[] getData (SingleGoodsBag _singleGoodsPackage,
            boolean _viewOperate, int[][] _shortcutKeyList, String _tabName)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(GridDataTypeDefine.PACKAGE_SINGLE_GOODS_LIST);
            int[][] singleGoodsDataList = _singleGoodsPackage.getAllItem();

            log.debug("singleGoodsDataList size = " + singleGoodsDataList.length);

            output.writeUTF(_tabName); //页签名字.(很多地方用不到,仅为NPC交易界面而添加)
            output.writeByte(singleGoodsDataList.length);
            output.writeByte(_singleGoodsPackage.getFullGridNumber());

            for (int i = 0; i < singleGoodsDataList.length; i++)
            {
                int[] goodsData = singleGoodsDataList[i];

                if (goodsData[0] != 0)
                {
                    SingleGoods goods = (SingleGoods) GoodsServiceImpl
                            .getInstance().getGoodsByID(goodsData[0]);
                    //add by zhengl; date: 2011-03-15; note: 每个特定的丸子都需要单独从hash获得
                    if (goods instanceof BigTonicBall) {
                    	goods = _singleGoodsPackage.tonicList.get(i);
					}
                    //add by zhengl; date: 2011-03-20; note: 每个特定的宠物卡都需要单独从hash获得
                    if (goods instanceof PetPerCard) {
                    	goods = _singleGoodsPackage.petPerCardList.get(i);
					}
                    if (null != goods)
                    {
                        log.debug("goodsname = " + goods.getName()+",goodsid="+goods.getID());
                        output.writeByte(i);// 物品在背包中的位置
                        output.writeInt(goods.getID());// 物品在背包中的标识（装备采用模型编号或者实例的编号）
                        output.writeShort(goods.getIconID());// 物品图标
                        output.writeUTF(goods.getName());// 物品名称
                        output.writeByte(goods.getTrait().value());// 物品品质
                        output.writeShort(goodsData[1]);// 物品数量
                        output.writeByte(goodsData[1]);// 物品可操作的最大数量
                        output.writeInt(goods.getRetrievePrice());// 出售价格
                        //add by zhengl; date: 2011-04-25; note: 物品的使用等级.
                        log.debug("goods.trait="+goods.getTrait().value()+",num="+goodsData[1]+",price="+goods.getRetrievePrice());
                        int level = 1;
                        if (goods.getNeedLevel() > 1) 
                        {
                        	level = goods.getNeedLevel();
						}
                        output.writeShort(level);
                        log.debug("goods level="+level);
                        if (goods.getGoodsType() == EGoodsType.TASK_TOOL)
                        {
                            output.writeUTF(goods.getDescription());// 物品描述

                            output.writeByte(goods.exchangeable() ? 1 : 0);
                        }
                        else if (goods.getGoodsType() == EGoodsType.SPECIAL_GOODS 
                        		|| goods.getGoodsType() == EGoodsType.PET_GOODS)
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

//                            log.debug("!!!!特殊物品描述:" + goods.getDescription());

                            output.writeByte(goods.exchangeable() ? 1 : 0);
                        }
                        else
                        {
                            output.writeUTF(goods.getDescription()
                                    + CharacterDefine.DESC_NEW_LINE_CHAR
                                    + "出售价格：" + goods.getRetrievePrice());// 物品描述
                            output.writeByte(goods.exchangeable() ? 1 : 0);
                        }
                        if (goods.useable() || goods.getGoodsType() == EGoodsType.MEDICAMENT)
                        {
                        	output.writeByte(1);//可使用
						} 
                        else 
                        {
							output.writeByte(0);//不可使用
						}

                        int shortcutKey = -1;

                        if ((goods.getGoodsType() == EGoodsType.MEDICAMENT
                                || goods.getGoodsType() == EGoodsType.TASK_TOOL || goods
                                .getGoodsType() == EGoodsType.SPECIAL_GOODS)
                                && _viewOperate && null != _shortcutKeyList)
                        {
                            for (int j = 0; j < _shortcutKeyList.length; j++)
                            {
                                if (_shortcutKeyList[j][0] == PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_GOODS
                                        && _shortcutKeyList[j][1] == goods
                                                .getID())
                                {
                                    shortcutKey = j;

                                    break;
                                }
                            }
                        }

                        output.writeByte(shortcutKey);
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

    public static byte[] getStorageData (SingleGoodsBag _singleGoodsPackage,
            boolean _viewOperate, int[][] _shortcutKeyList)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(GridDataTypeDefine.PACKAGE_SINGLE_GOODS_LIST);
            int[][] singleGoodsDataList = _singleGoodsPackage.getAllItem();

            output.writeByte(singleGoodsDataList.length);
            output.writeByte(_singleGoodsPackage.getFullGridNumber());
            for (int i = 0; i < singleGoodsDataList.length; i++)
            {
                int[] goodsData = singleGoodsDataList[i];

                if (goodsData[0] != 0)
                {
                    Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(
                            goodsData[0]);

                    if (null != goods)
                    {
                        output.writeByte(i);// 物品在背包中的位置
                        output.writeInt(goods.getID());// 物品在背包中的标识（装备采用模型编号或者实例的编号）
                        output.writeShort(goods.getIconID());// 物品图标
                        output.writeUTF(goods.getName());// 物品名称
                        output.writeByte(goods.getTrait().value());// 物品品质
                        output.writeShort(goodsData[1]);// 物品数量
                        output.writeByte(goodsData[1]);// 物品可操作的最大数量

                        byte useable = 0;

                        if (goods.getGoodsType() == EGoodsType.TASK_TOOL)
                        {
                            output.writeUTF(goods.getDescription());// 物品描述
                            output.writeByte(1);
                            if (((TaskTool) goods).useable())
                            {
                                useable = 1;
                            }
                        }
                        else if (goods.getGoodsType() == EGoodsType.SPECIAL_GOODS)
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
                            output.writeByte(1);

                            if (((SpecialGoods) goods).useable())
                            {
                                useable = 1;
                            }
                        }
                        else
                        {
                            output.writeUTF(goods.getDescription()
                                    + CharacterDefine.DESC_NEW_LINE_CHAR
                                    + "出售价格：" + goods.getRetrievePrice());// 物品描述
                            output.writeByte(1);
                        }

                        output.writeByte(useable);

                        int shortcutKey = -1;

                        if ((goods.getGoodsType() == EGoodsType.MEDICAMENT || goods
                                .getGoodsType() == EGoodsType.TASK_TOOL)
                                && _viewOperate)
                        {
                            for (int j = 0; j < _shortcutKeyList.length; j++)
                            {
                                if (_shortcutKeyList[j][1] == goods.getID())
                                {
                                    shortcutKey = j;

                                    break;
                                }
                            }
                        }

                        output.writeByte(shortcutKey);
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
