package hero.task;

import java.util.ArrayList;
import java.util.HashMap;

import hero.item.Goods;
import hero.player.define.EClan;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Award.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-9 下午01:32:24
 * @描述 ：
 */

public class Award
{
    /**
     * 金钱
     */
    public int                        money;

    /**
     * 经验
     */
    public int                        experience;

    /**
     * 奖励的技能编号
     */
    public int                        skillID;

    /**
     * 奖励的增益效果编号
     */
    public int                        effectID;
    
    /**
     * 奖励的传送地图编号
     */
    public short                      mapID;
    
    /**
     * 奖励的传送地图x坐标
     */
    public short                      mapX;
    
    /**
     * 奖励的传送地图y坐标
     */
    public short                      mapY;

    /**
     * 可选物品奖励列表
     */
    private ArrayList<AwardGoodsUnit> optionalGoodList;

    /**
     * 必得物品奖励列表
     */
    private ArrayList<AwardGoodsUnit> boundGoodList;

    /**
     * 添加可选奖励物品
     * 
     * @param _goods
     * @param _number
     */
    public void addOptionalGoods (Goods _goods, int _number)
    {
        if (null == optionalGoodList)
        {
            optionalGoodList = new ArrayList<AwardGoodsUnit>();
        }

        optionalGoodList.add(new AwardGoodsUnit(_goods, (byte) _number));
    }

    /**
     * 获取可选奖励物品列表
     * 
     * @return
     */
    public ArrayList<AwardGoodsUnit> getOptionalGoodsList ()
    {
        return optionalGoodList;
    }
    

    /**
     * 输入的可选物品ID是否合法,如果非法则返回最后1个物品ID
     * <p>
     * add by zhengl
     * date:	2011-05-09
     * note:	添加该方法用于获得物品的时候安全性验证,防止客户端恶意篡改物品ID
     * @param _goodsID
     * @return
     */
    public int selectGoodsVerify (int _goodsID)
    {
    	int result = 0;
    	if (optionalGoodList != null) 
    	{
        	for (int i = 0; i < optionalGoodList.size(); i++) 
        	{
    			int existGoodsID = optionalGoodList.get(i).goods.getID();
    			if (existGoodsID == _goodsID) 
    			{
    				result = existGoodsID;
    				break;
    			}
    			else 
    			{
    				result = existGoodsID;
    			}
    		}
		}
    	return result;
    }

    /**
     * 获取可选奖励物品数量
     * 
     * @param _goods
     * @return
     */
    public byte getOptionalGoodsNumber (Goods _goods)
    {
        if (null != optionalGoodList)
        {
            for (AwardGoodsUnit awardGoods : optionalGoodList)
            {
                if (awardGoods.goods == _goods)
                {
                    return awardGoods.number;
                }
            }
        }

        return 0;
    }

    /**
     * 添加奖励物品
     * 
     * @param _goods
     * @param _number
     */
    public void addBoundGoods (Goods _goods, int _number)
    {
        if (null == boundGoodList)
        {
            boundGoodList = new ArrayList<AwardGoodsUnit>();
        }

        boundGoodList.add(new AwardGoodsUnit(_goods, (byte) _number));
    }

    /**
     * 获取奖励物品列表
     * 
     * @return
     */
    public ArrayList<AwardGoodsUnit> getBoundGoodsList ()
    {
        return boundGoodList;
    }

    /**
     * @author DC 奖励物品单元
     */
    public static class AwardGoodsUnit
    {
        public AwardGoodsUnit(Goods _goods, byte _number)
        {
            goods = _goods;
            number = _number;
        }

        public Goods goods;

        public byte  number;
    }
}
