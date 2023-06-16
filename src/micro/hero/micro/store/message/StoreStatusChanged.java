package hero.micro.store.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 StoreStatusChanged.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-23 下午04:49:51
 * @描述 ：
 */

public class StoreStatusChanged extends AbsResponseMessage
{
    /**
     * 商店主人object编号
     */
    private int     storeOwnerID;

    /**
     * 现在的状态（true:营业 false:关闭）
     */
    private boolean statusAtNow;

    /**
     * 商店名称
     */
    private String  storeName;

    /**
     * 构造－开始营业
     * 
     * @param _storeOwnerID 商店主人object编号
     * @param _statusAtNow 现在的状态（true:营业 false:关闭）
     * @param _storeName 商店名称
     */
    public StoreStatusChanged(int _storeOwnerID, boolean _statusAtNow,
            String _storeName)
    {
        storeOwnerID = _storeOwnerID;
        statusAtNow = _statusAtNow;
        storeName = _storeName;
    }

    /**
     * 构造
     * 
     * @param _storeOwnerID 商店主人object编号
     * @param _statusAtNow 现在的状态（true:营业 false:关闭）
     */
    public StoreStatusChanged(int _storeOwnerID, boolean _statusAtNow)
    {
        storeOwnerID = _storeOwnerID;
        statusAtNow = _statusAtNow;
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
        yos.writeInt(storeOwnerID);
        yos.writeByte(statusAtNow);

        if (statusAtNow)
        {
            yos.writeUTF(storeName);
        }
    }
}
