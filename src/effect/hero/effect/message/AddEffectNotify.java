package hero.effect.message;

import hero.effect.Effect;
import hero.share.ME2GameObject;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 AddEffectNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-3-18 下午05:10:55
 * @描述 ：
 */

public class AddEffectNotify extends AbsResponseMessage
{
    /**
     * 添加的效果
     */
    private Effect        effect;

    /**
     * 作用目标
     */
    private ME2GameObject target;

    /**
     * @param _target
     * @param _effect
     */
    public AddEffectNotify(ME2GameObject _target, Effect _effect)
    {
        target = _target;
        effect = _effect;
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
        yos.writeByte(target.getObjectType().value());
        yos.writeInt(target.getID());
        yos.writeInt(effect.ID);
        yos.writeInt(effect.releaser.getID());
        yos.writeByte(effect.trait.value());
        yos.writeByte(effect.keepTimeType.value());
        yos.writeShort(effect.traceTime);
        yos.writeShort(effect.iconID);
        yos.writeUTF(effect.name);
        yos.writeUTF(effect.desc);
        //add by zhengl; date: 2011-03-22; note: 宠物卡坐骑的外观
        yos.writeByte(effect.viewType); //添加BUFF外观类型
        yos.writeShort(effect.imageID); //添加BUFF外观图片
        yos.writeShort(effect.animationID); //添加BUFF外观动画
    }
}
