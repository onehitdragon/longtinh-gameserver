package hero.guild.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GuildChangeNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-11 下午16:40:33
 * @描述 ：公会变化通知（加入公会，离开公会）
 */

public class GuildChangeNotify extends AbsResponseMessage
{
    /**
     * 玩家objectID
     */
    private int    changerObjectID;

    /**
     * 变化
     */
    private byte   changeReason;

    /**
     * 名称
     */
    private String guildName;

    /**
     * 构造
     * 
     * @param _objectID
     * @param _guildName
     */
    public GuildChangeNotify(int _objectID, String _guildName)
    {
        changerObjectID = _objectID;
        changeReason = CHANGER_OF_JOIN;
        guildName = _guildName;
    }

    /**
     * 构造
     * 
     * @param _objectID
     */
    public GuildChangeNotify(int _objectID)
    {
        changerObjectID = _objectID;
        changeReason = CHANGER_OF_LEFT;
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
        yos.writeInt(changerObjectID);
        yos.writeByte(changeReason);

        if (CHANGER_OF_JOIN == changeReason)
        {
            yos.writeUTF(guildName);
        }
    }

    /**
     * 变化-加入公会
     */
    private static final byte CHANGER_OF_JOIN = 1;

    /**
     * 变化-离开公会
     */
    private static final byte CHANGER_OF_LEFT = 2;
}
