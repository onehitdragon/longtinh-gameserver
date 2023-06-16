package hero.micro.teach.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 M_AOperateConfirm.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-26 下午04:03:07
 * @描述 ：
 */

public class MAOperateConfirm extends AbsResponseMessage
{
    /**
     * 确认的请求类型
     */
    private byte   confirmRequestType;

    /**
     * 发起方userID
     */
    private int    initiatorUserID;

    /**
     * 发起方名字
     */
    private String initiatorName;

    /**
     * 构造
     * 
     * @param confirmRequestType
     * @param initiatorUserID
     */
    public MAOperateConfirm(byte _confirmRequestType, int _initiatorUserID,
            String _initiatorName)
    {
        confirmRequestType = _confirmRequestType;
        initiatorUserID = _initiatorUserID;
        initiatorName = _initiatorName;
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
        yos.writeByte(confirmRequestType);
        yos.writeInt(initiatorUserID);
        yos.writeUTF(initiatorName);
    }

}
