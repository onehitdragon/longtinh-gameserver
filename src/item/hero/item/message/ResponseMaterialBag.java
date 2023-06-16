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
 * @文件 ResponseMaterialBag.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-12 下午04:13:23
 * @描述 ：响应材料背包
 */

public class ResponseMaterialBag extends AbsResponseMessage
{
    /**
     * 材料背包
     */
    private SingleGoodsBag goodsPackage;

    /**
     * 构造
     * 
     * @param _goodsPackage
     */
    public ResponseMaterialBag(SingleGoodsBag _goodsPackage)
    {
        goodsPackage = _goodsPackage;
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
        yos.writeBytes(SingleGoodsPackageData.getData(goodsPackage, true, null, 
        		GoodsServiceImpl.getInstance().getConfig().material_bag_tab_name));
    }
}
