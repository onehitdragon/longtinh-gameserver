package hero.guild.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GuildInviteNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-11 下午16:40:33
 * @描述 ：公会邀请通知
 */

public class GuildInviteNotify extends AbsResponseMessage
{
    /**
     * 邀请者编号
     */
    private int    invitorUserID;

    /**
     * 邀请者名称
     */
    private String invitorName;

    /**
     * 公会编号
     */
    private int    guildID;

    /**
     * 工会名称
     */
    private String guildName;

    /**
     * 构造
     * 
     * @param _invitorUserID
     * @param _invitorName
     * @param _guildID
     * @param _guildName
     */
    public GuildInviteNotify(int _invitorUserID, String _invitorName,
            int _guildID, String _guildName)
    {
        invitorUserID = _invitorUserID;
        invitorName = _invitorName;
        guildID = _guildID;
        guildName = _guildName;
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
        yos.writeInt(invitorUserID);
        yos.writeUTF(invitorName);
        yos.writeInt(guildID);
        yos.writeUTF(guildName);
    }

}
