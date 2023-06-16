package hero.item.message;

import hero.item.bag.SingleGoodsBag;
import hero.item.service.GoodsServiceImpl;
import hero.ui.data.SingleGoodsPackageData;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseMedicamentBag.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-12 下午04:13:23
 * @描述 ：响应药水背包
 */

public class ResponseMedicamentBag extends AbsResponseMessage
{
    /**
     * 药水背包
     */
    private SingleGoodsBag goodsPackage;

    /**
     * 物品快捷键
     */
    private int[][]        shortcutKeyList;

    /**
     * 构造
     * 
     * @param _goodsPackage
     * @param _shortcutKeyList
     */
    public ResponseMedicamentBag(SingleGoodsBag _goodsPackage,
            int[][] _shortcutKeyList)
    {
        goodsPackage = _goodsPackage;
        shortcutKeyList = _shortcutKeyList;
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
        yos.writeBytes(SingleGoodsPackageData.getData(goodsPackage, true, shortcutKeyList, 
        		GoodsServiceImpl.getInstance().getConfig().medicament_bag_tab_name));
    }
}
