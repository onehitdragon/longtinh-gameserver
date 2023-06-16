package hero.micro.store.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GridGoodsChangesNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-24 上午10:13:11
 * @描述 ：
 */

public class GridGoodsChangesNotify extends AbsResponseMessage
{
    /**
     * 是否自己商店
     */
    private boolean isSelf;

    /**
     * 在商店中的位置
     */
    private byte    gridIndex;

    /**
     * 变化的内容类型
     */
    private byte    changeTypeOfContent;

    /**
     * 变化的价格
     */
    private int     newPrice;

    /**
     * 构造
     * 
     * @param _changeTypeOfContent
     */
    public GridGoodsChangesNotify(boolean _isSelf, byte _gridIndex,
            byte _changeTypeOfContent)
    {
        isSelf = _isSelf;
        gridIndex = _gridIndex;
        changeTypeOfContent = _changeTypeOfContent;
    }

    /**
     * 构造
     * 
     * @param _changeTypeOfContent
     * @param _newPrice
     */
    public GridGoodsChangesNotify(boolean _isSelf, byte _gridIndex,
            byte _changeTypeOfContent, int _newPrice)
    {
        isSelf = _isSelf;
        gridIndex = _gridIndex;
        changeTypeOfContent = _changeTypeOfContent;
        newPrice = _newPrice;
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
        yos.writeByte(isSelf);
        yos.writeByte(gridIndex);
        yos.writeByte(changeTypeOfContent);

        if (CONTENT_OF_PRICE == changeTypeOfContent)
        {
            yos.writeInt(newPrice);
        }
    }

    /**
     * 价格变化
     */
    public static final byte CONTENT_OF_PRICE  = 1;

    /**
     * 移除商品
     */
    public static final byte CONTENT_OF_REMOVE = 2;
}
