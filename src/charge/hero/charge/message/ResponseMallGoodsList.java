package hero.charge.message;

import hero.charge.MallGoods;
import hero.charge.service.ChargeServiceImpl;
import hero.item.Goods;
import hero.item.dictionary.GoodsContents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseMallGoodsList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-1-24 下午09:27:58
 * @描述 ：
 */

public class ResponseMallGoodsList extends AbsResponseMessage
{
     private static Logger log = Logger.getLogger(ResponseMallGoodsList.class);
    /**
     * 物品类型
     */
    private byte                 goodsType;

    /**
     * 总列表
     */
    private Hashtable<Byte, ArrayList<MallGoods>> goodsTable;
    
    private int nullListSize;
    
    /**
     * 物品列表
     */
    private ArrayList<MallGoods> goodsList;
    
    private short clientVersion;

    /**
     * 构造
     */
    public ResponseMallGoodsList(Hashtable<Byte, ArrayList<MallGoods>> _goodsTable, 
    		short _clientVersion)
    {
        goodsTable = _goodsTable;
        clientVersion = _clientVersion;
//        nullListSize
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
        if(clientVersion == ChargeServiceImpl.getInstance().getConfig().now_version) {
        	yos.writeByte(0);
        	return;
        } else {
        	yos.writeByte(1);
		}
        yos.writeShort(ChargeServiceImpl.getInstance().getConfig().now_version);
        //----------
        String[][] bagUpgradeData = ChargeServiceImpl.getInstance().getConfig().bag_upgrade_data;
        yos.writeByte(bagUpgradeData.length);
        for (int i = 0; i < bagUpgradeData.length; i++) {
            yos.writeByte(Byte.parseByte(bagUpgradeData[i][1]));
            yos.writeUTF(bagUpgradeData[i][0]);
		}
        //----------
        yos.writeByte(goodsTable.size()); //列表数量
        yos.writeUTF(ChargeServiceImpl.getInstance().getConfig().notice_string);

        if (goodsTable.size() > 0)
        {
            Iterator<Entry<Byte, ArrayList<MallGoods>>> iterator = goodsTable.entrySet().iterator();
            while(iterator.hasNext()) {
                Entry<Byte, ArrayList<MallGoods>>  entry = iterator.next();
                goodsType = entry.getKey();
                goodsList = entry.getValue();
                MallGoods goods;
                //单个列表的名称
                yos.writeUTF(ChargeServiceImpl.getInstance().getConfig().getTypeDesc(
                		goodsType));
                yos.writeShort(goodsList.size()); //单个列表下物品数量
                log.info("goodsList.size()-->" + goodsList.size());
                int needLevel = 1;
                for (int i = 0; i < goodsList.size(); i++)
                {
                	goods = goodsList.get(i);
                	
                	Goods item = GoodsContents.getGoods(goods.goodsList[0][0]);
                	needLevel = item.getNeedLevel();
                	yos.writeInt(goods.id);
                	yos.writeUTF(goods.name);
                	yos.writeByte(goods.trait.value());
                	yos.writeShort(goods.icon);
                	yos.writeUTF(goods.desc);
                	yos.writeInt(goods.price);
                	yos.writeShort(needLevel);
                	yos.writeByte(goods.buyNumberPerTime);
                	
                	if (1 == goods.goodsList.length)
                	{
                		yos.writeShort(goods.goodsList[0][1]);
                	}
                	else
                	{
                		yos.writeShort(1);
                	}
                }
            }//while end
            
        }
    }
}
