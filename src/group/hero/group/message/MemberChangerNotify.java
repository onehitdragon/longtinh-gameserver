package hero.group.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MemberChangerNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-20 下午05:34:44
 * @描述 ：队伍成员信息变化通知
 */

public class MemberChangerNotify extends AbsResponseMessage
{
    /**
     * 变化类型
     */
    private byte changerType;

    /**
     * 成员编号
     */
    private int  memberUserID;

    /**
     * 变化的数据
     */
    private int  value;

    /**
     * 构造
     * 
     * @param _changerType
     * @param _memberUserID
     * @param _value
     */
    public MemberChangerNotify(byte _changerType, int _memberUserID, int _value)
    {
        changerType = _changerType;
        memberUserID = _memberUserID;
        value = _value;
    }

    /**
     * 构造
     * 
     * @param _changerType
     * @param _memberUserID
     * @param _onlineStatus
     */
    public MemberChangerNotify(byte _changerType, int _memberUserID,
            boolean _onlineStatus)
    {
        changerType = _changerType;
        memberUserID = _memberUserID;
        value = _onlineStatus ? 1 : 0;
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
        yos.writeByte(changerType);
        yos.writeInt(memberUserID);
        yos.writeShort(value);
    }

    /**
     * 通知类型-所在小队变化
     */
    public static final byte CHANGER_OF_SUB_GROUP     = 1;

    /**
     * 通知类型-权限
     */
    public static final byte CHANGER_OF_RANK          = 2;

    /**
     * 通知类型-在线状态
     */
    public static final byte CHANGER_OF_ONLINE_STATUS = 3;

    /**
     * 通知类型-等级
     */
    public static final byte CHANGER_OF_LEVEL         = 4;

    /**
     * 通知类型-职业
     */
    public static final byte CHANGER_OF_VOCATION      = 5;
}
