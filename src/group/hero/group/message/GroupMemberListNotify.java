package hero.group.message;

import hero.group.Group;
import hero.group.GroupMemberProxy;

import java.io.IOException;
import java.util.ArrayList;

import yoyo.core.packet.AbsResponseMessage;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GroupMemberListNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-20 下午06:03:32
 * @描述 ：当进入队伍时，成员列表通知
 */

public class GroupMemberListNotify extends AbsResponseMessage
{
    /**
     * 队伍对象
     */
    private Group group;

    /**
     * @param _group 队伍对象
     */
    public GroupMemberListNotify(Group _group)
    {
        group = _group;
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
        ArrayList<GroupMemberProxy> list = group.getMemberList();
        GroupMemberProxy member;

        yos.writeByte(list.size());

        for (int i = 0; i < list.size(); i++)
        {
            member = list.get(i);

            yos.writeByte(member.subGroupID);
            yos.writeInt(member.player.getUserID());
            yos.writeUTF(member.player.getName());
            yos.writeByte(member.player.getVocation().value());
            yos.writeByte(member.player.getSex().value());
            yos.writeShort(member.player.getLevel());
            yos.writeByte(member.memberRank.value());
            yos.writeByte(member.isOnline());
        }
    }
}
