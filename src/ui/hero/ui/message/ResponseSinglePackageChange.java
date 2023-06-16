package hero.ui.message;

import hero.item.Goods;
import hero.item.SingleGoods;
import hero.item.SpecialGoods;
import hero.item.detail.EGoodsType;
import hero.share.CharacterDefine;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseSingleackageChange.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-11 下午12:56:14
 * @描述 ：
 */

public class ResponseSinglePackageChange extends AbsResponseMessage
{
    /**
     * 变化的UI类型
     */
    private byte    uiType;

    /**
     * 变化结果，0:index,1:nums
     */
    private short[] change;

    /**
     * 构造
     * 
     * @param _uiType 背包类型
     * @param _change 变化
     * @param _goods 物品
     */
    public ResponseSinglePackageChange(byte _uiType, short[] _change)
    {
        uiType = _uiType;
        change = _change;
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
        yos.writeByte(uiType);
        yos.writeByte(change[0]);
        yos.writeShort(change[1]);
    }
}
