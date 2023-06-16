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
 * @文件 RemoveEffectNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-3-18 下午04:59:53
 * @描述 ：移除效果通知
 */

public class RemoveEffectNotify extends AbsResponseMessage
{
    /**
     * 移除的效果
     */
    private Effect        effect;

    /**
     * 目标
     */
    private ME2GameObject target;

    /**
     * 构造
     * 
     * @param _effect
     */
    public RemoveEffectNotify(ME2GameObject _target, Effect _effect)
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
        yos.writeInt(effect.releaser.getID());
        yos.writeInt(effect.ID);
        yos.writeByte(effect.trait.value());
        yos.writeShort(effect.iconID);
        //add by zhengl; date: 2011-03-22; note: 宠物卡坐骑的外观
        yos.writeByte(effect.viewType); //添加BUFF外观类型
        yos.writeShort(-1); //添加BUFF外观图片
        yos.writeShort(-1); //添加BUFF外观动画
    }

}
