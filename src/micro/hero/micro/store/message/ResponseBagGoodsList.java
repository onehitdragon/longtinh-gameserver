package hero.micro.store.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseBagGoodsList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-20 下午03:43:45
 * @描述 ：
 */

public class ResponseBagGoodsList extends AbsResponseMessage
{
    /**
     * 背包物品数据列表
     */
    private byte[] goodsDataList;

    /**
     * 构造
     * 
     * @param _goodsDataList
     */
    public ResponseBagGoodsList(byte[] _goodsDataList)
    {
        goodsDataList = _goodsDataList;
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
        yos.writeBytes(goodsDataList);
    }

}
