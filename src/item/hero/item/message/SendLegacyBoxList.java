package hero.item.message;

import hero.item.legacy.MonsterLegacyBox;
import hero.item.legacy.PersonalPickerBox;
import hero.item.legacy.RaidPickerBox;

import java.io.IOException;
import java.util.ArrayList;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SendLegacyBoxList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-20 下午08:07:51
 * @描述 ：发送可见的怪物掉落箱子列表
 */

public class SendLegacyBoxList extends AbsResponseMessage
{
    /**
     * 怪物掉落箱子列表
     */
    private ArrayList<MonsterLegacyBox> legacyBoxList;

    /**
     * 玩家编号
     */
    private int                         playerUserID;

    /**
     * 构造
     * 
     * @param _legacyBoxList
     * @param _playerUserID
     */
    public SendLegacyBoxList(ArrayList<MonsterLegacyBox> _legacyBoxList,
            int _playerUserID)
    {
        legacyBoxList = _legacyBoxList;
        playerUserID = _playerUserID;
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
        yos.writeByte(legacyBoxList.size());

        for (MonsterLegacyBox legacyBox : legacyBoxList)
        {
            yos.writeInt(legacyBox.getID());
            yos.writeInt(legacyBox.getMonsterID());

            if (legacyBox instanceof PersonalPickerBox)
            {
                yos.writeByte(true);
            }
            else
            {
                yos.writeByte(((RaidPickerBox) legacyBox)
                        .getStateOfPicking(playerUserID));
            }

            yos.writeByte(legacyBox.getLocationX());
            yos.writeByte(legacyBox.getLocationY());
        }
    }
}
