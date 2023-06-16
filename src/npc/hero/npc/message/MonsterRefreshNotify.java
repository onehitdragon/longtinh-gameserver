package hero.npc.message;

import hero.npc.Monster;
import hero.npc.dict.MonsterImageConfDict;
import hero.npc.dict.MonsterImageDict;
import hero.share.Constant;
import hero.share.EVocationType;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MonsterRefreshNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-23 下午05:28:24
 * @描述 ：
 */

public class MonsterRefreshNotify extends AbsResponseMessage
{
    /**
     * 客户端类型（高、中、低端）
     */
    private short   clientType;

    /**
     * 
     */
    private Monster monster;

    /**
     * @param monster
     * @param receiver
     */
    public MonsterRefreshNotify(short _clientType, Monster _monster)
    {
        clientType = _clientType;
        monster = _monster;
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
        yos.writeShort(monster.where().getID());
        yos.writeInt(monster.getID());
        yos.writeUTF(monster.getName());
        yos.writeShort(monster.getLevel());
        yos.writeByte(monster.getClan().getID());
        yos.writeByte(monster.isActiveAttackType());
        yos.writeByte(monster.getVocation().value());
        yos.writeByte(monster.getMonsterLevel().value());
        yos.writeByte(monster.getObjectLevel().value());
        yos.writeByte(monster.getTakeSoulUserID() > 0 ? 1 : 0);
        yos.writeInt(monster.getHp());
        yos.writeInt(monster.getActualProperty().getHpMax());
        yos.writeInt(monster.getMp());
        yos.writeInt(monster.getActualProperty().getMpMax());

        yos.writeByte(monster.getCellX());
        yos.writeByte(monster.getCellY());
        yos.writeByte(monster.getDirection());

        if (null == monster.where().fixedMonsterImageIDList
                || !monster.where().fixedMonsterImageIDList.contains(monster
                        .getImageID()))
        {
            yos.writeByte(1);
            yos.writeShort(monster.getImageID());

            MonsterImageConfDict.Config config = MonsterImageConfDict
                    .get(monster.getImageID());

            yos.writeByte(config.grid);
            yos.writeShort(config.monsterHeight);
            //add by zhengl; date: 2011-02-18; note: 添加怪物阴影大小及选中框大小
            yos.writeByte(config.shadowSize);//暂不启用
            //del by zhengl; date: 2011-02-18; note: 删除现在不用的数据
//            output.writeByte(config.centerExcursionX);
//            output.writeByte(config.headExcursionX);
//            output.writeByte(config.headExcursionY);
//            output.writeByte(config.shadowType);
//            output.writeByte(config.shadowX);
//            output.writeShort(config.shadowY);

            byte[] monsterImage = MonsterImageDict.getInstance()
                    .getMonsterImageBytes(monster.getImageID());

            if (Constant.CLIENT_OF_HIGH_SIDE != clientType)
            {
                yos.writeShort(monsterImage.length);
                yos.writeBytes(monsterImage);
            }
        }
        else
        {
            yos.writeByte(0);
        }

        yos.writeShort(monster.getImageID());
        yos.writeShort(monster.getAnimationID());
    }
}
