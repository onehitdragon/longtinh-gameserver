package hero.npc.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

import hero.npc.Npc;
import hero.npc.dict.NpcImageConfDict;
import hero.npc.dict.NpcImageDict;
import hero.npc.dict.NpcImageConfDict.Config;
import hero.share.Constant;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 NpcRefreshNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-23 下午05:28:24
 * @描述 ：
 */

public class NpcRefreshNotify extends AbsResponseMessage
{
    /**
     * 客户端类型（高、中、低端）
     */
    private short clientType;

    /**
     * 
     */
    private Npc   npc;

    /**
     * @param emerger
     * @param receiver
     */
    public NpcRefreshNotify(short _clientType, Npc _npc)
    {
        clientType = _clientType;
        npc = _npc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.core.data.ResultMessage#getPriority()
     */
    @Override
    public int getPriority ()
    {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.core.data.ResultMessage#write()
     */
    @Override
    protected void write () throws IOException
    {
        yos.writeShort(npc.where().getID());
        yos.writeInt(npc.getID());
        yos.writeUTF(npc.getTitle());
        yos.writeUTF(npc.getName());
        yos.writeByte(npc.getClan().getID());
        yos.writeUTF(npc.getHello());

        if (null != npc.getScreamContent())
        {
            yos.writeByte(1);
            yos.writeUTF(npc.getScreamContent());
        }
        else
        {
            yos.writeByte(0);
        }

        yos.writeByte(npc.canInteract());
        yos.writeByte(npc.getCellX());
        yos.writeByte(npc.getCellY());

        if (null == npc.where().fixedNpcImageIDList
                || !npc.where().fixedNpcImageIDList.contains(npc.getImageID()))
        {
            yos.writeByte(1);

            byte[] imageBytes = NpcImageDict.getInstance().getImageBytes(
                    npc.getImageID());

            yos.writeShort(npc.getImageID());
            yos.writeShort(npc.getAnimationID());
            Config npcConfig = NpcImageConfDict.get(npc.getImageID());
//            output.writeByte(npcConfig.headExcursionX);
//            output.writeByte(npcConfig.headExcursionY);
//            output.writeByte(npcConfig.shadowType);
//            output.writeByte(npcConfig.shadowX);
//            output.writeShort(npcConfig.shadowY);
            //add by zhengl ; date: 2010-12-20 ; note: 添加 NPC 动画文件的属性
            yos.writeByte(npcConfig.npcGrid);
            yos.writeShort(npcConfig.npcHeight);
            //edit by zhengl; date: 2011-02-18; note: 下发NPC阴影大小以及选中框大小
            yos.writeByte(npcConfig.shadowSize);//暂不启用

            if (Constant.CLIENT_OF_HIGH_SIDE != clientType)
            {
                yos.writeShort(imageBytes.length);
                yos.writeBytes(imageBytes);
            }
        }
        else
        {
            yos.writeByte(0);
        }

        yos.writeByte(npc.getImageType());
        yos.writeShort(npc.getImageID());
        yos.writeShort(npc.getAnimationID());
    }
}
