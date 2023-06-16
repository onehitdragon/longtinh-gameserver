package hero.micro.teach.message;

import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 AddMasterApprenticeNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-10 下午04:23:00
 * @描述 ：师徒变化通知
 */

public class AddMasterApprenticeNotify extends AbsResponseMessage
{
    /**
     * 增加的师徒关系类型
     */
    private byte relationType;

    /**
     * 变化目标的编号
     */
    private int  userID;

    /**
     * 构造
     * 
     * @param _changeType 变化类型
     * @param _relationType 目标关系类型（师傅、徒弟）
     * @param _userID 目标的编号
     */
    public AddMasterApprenticeNotify(byte _relationType,
            int _userID)
    {
        relationType = _relationType;
        userID = _userID;
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
        HeroPlayer target = PlayerServiceImpl.getInstance().getPlayerByUserID(
                userID);

        if (null != target)
        {
            yos.writeByte(relationType);
            yos.writeInt(userID);
            yos.writeUTF(target.getName());
            yos.writeByte(target.getVocation().value());
            yos.writeShort(target.getLevel());
            yos.writeByte(target.getSex().value());
        }
    }
}
