package hero.npc.message;

import hero.npc.Monster;
import hero.npc.dict.MonsterImageConfDict;
import hero.npc.dict.MonsterImageDict;
import hero.share.Constant;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MonsterChangesNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-24 下午07:21:33
 * @描述 ：
 */

public class MonsterChangesNotify extends AbsResponseMessage
{
    private Monster monster;

    /**
     * 构造
     * 
     * @param _monster
     */
    public MonsterChangesNotify(Monster _monster)
    {
        monster = _monster;
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
        yos.writeShort(monster.where().getID());

        if (!monster.where().fixedMonsterImageIDList.contains(monster
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
            //del by zhengl; date: 2011-02-18; note: 删除现在所有图片下发的地方
//            byte[] monsterImage = MonsterImageDict.getInstance()
//                    .getMonsterImageBytes(monster.getImageID());
//            output.writeShort(monsterImage.length);
//            output.writeBytes(monsterImage);
        }
        else
        {
            yos.writeByte(0);
        }

        yos.writeInt(monster.getID());
        yos.writeInt(monster.getHp());
        yos.writeInt(monster.getActualProperty().getHpMax());
        yos.writeInt(monster.getMp());
        yos.writeInt(monster.getActualProperty().getMpMax());
        yos.writeShort(monster.getImageID());
        yos.writeShort(monster.getAnimationID());
    }
}
