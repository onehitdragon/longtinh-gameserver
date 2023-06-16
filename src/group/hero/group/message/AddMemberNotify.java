package hero.group.message;

import hero.group.GroupMemberProxy;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 AddMemberNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-20 下午06:08:33
 * @描述 ：增加成员通知
 */

public class AddMemberNotify extends AbsResponseMessage
{
    /**
     * 增加的成员
     */
    private GroupMemberProxy member;

    /**
     * 构造
     * 
     * @param _member
     * @param _indexInGroup
     */
    public AddMemberNotify(GroupMemberProxy _member)
    {
        member = _member;
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
        yos.writeByte(member.subGroupID);
        yos.writeInt(member.player.getUserID());
        yos.writeUTF(member.player.getName());
        yos.writeByte(member.player.getVocation().value());
        yos.writeByte(member.player.getSex().value());
        yos.writeShort(member.player.getLevel());
        yos.writeByte(member.memberRank.value());
    }
}
