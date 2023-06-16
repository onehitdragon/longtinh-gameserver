package hero.effect.message;

import hero.effect.Effect;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 RefreshEffectNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-3-18 上午09:30:09
 * @描述 ：效果刷新通知，包括施放者、剩余时间、当前叠加次数
 */

public class RefreshEffectNotify extends AbsResponseMessage
{
    /**
     * 效果
     */
    private Effect effect;

    /**
     * 原有效果的释放者编号
     */
    private int    oldReleaserID;

    /**
     * 构造
     * 
     * @param _effect
     */
    public RefreshEffectNotify(Effect _effect, int _oldReleaserID)
    {
        effect = _effect;
        oldReleaserID = _oldReleaserID;
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
        yos.writeByte(effect.host.getObjectType().value());
        yos.writeInt(effect.host.getID());
        yos.writeInt(effect.ID);
        yos.writeInt(oldReleaserID);
        yos.writeInt(effect.releaser.getID());
        yos.writeByte(effect.trait.value());
        yos.writeShort(effect.currentCountTimes);
        yos.writeByte(effect.keepTimeType.value());
        yos.writeShort(effect.traceTime);
        yos.writeShort(effect.iconID);
        yos.writeUTF(effect.name);
        //add by zhengl; date: 2011-03-22; note: 宠物卡坐骑的外观
        yos.writeByte(effect.viewType); //添加BUFF外观类型
        yos.writeShort(effect.imageID); //添加BUFF外观图片
        yos.writeShort(effect.animationID); //添加BUFF外观动画
    }
}
