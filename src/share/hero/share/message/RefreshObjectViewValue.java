package hero.share.message;

import hero.player.HeroPlayer;
import hero.share.EObjectType;
import hero.share.ME2GameObject;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 RefreshMonsterValue.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-3-18 下午09:17:30
 * @描述 ：刷新玩家、怪物可见数值生命值、魔法值、力气值
 */

public class RefreshObjectViewValue extends AbsResponseMessage
{
    /**
     * 对象类型
     */
    private EObjectType objectType;

    /**
     * 对象编号
     */
    private int         objectID;

    /**
     * 当前生命值
     */
    private int         hp;

    /**
     * 最大生命值
     */
    private int         hpMax;

    /**
     * 当前魔法值
     */
    private int         mp;

    /**
     * 最大魔法值
     */
    private int         mpMax;
    
    private short       surplusSkillPoint;

    /**
     * 构造
     * 
     * @param _object
     */
    public RefreshObjectViewValue(ME2GameObject _object)
    {
        objectType = _object.getObjectType();
        objectID = _object.getID();
        hp = _object.getHp();
        hpMax = _object.getActualProperty().getHpMax();
        mp = _object.getMp();
        mpMax = _object.getActualProperty().getMpMax();
        surplusSkillPoint = -1;
        if(_object instanceof HeroPlayer) 
        {
        	surplusSkillPoint = ((HeroPlayer)_object).surplusSkillPoint;
        }

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
        yos.writeByte(objectType.value());
        yos.writeInt(objectID);
        yos.writeInt(hp);
        yos.writeInt(hpMax);
        yos.writeInt(mp);
        yos.writeInt(mpMax);
        //add by zhengl ; date: 2011-03-23 ; note: 技能点下发
//        output.writeShort(surplusSkillPoint);
    }
}
