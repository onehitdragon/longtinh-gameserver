package hero.social.message;

import hero.social.SocialObjectProxy;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 AddSocialMemberNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-9 下午18:11:08
 * @描述 ：社交关系变化通知（包括好友、仇人、屏蔽三种关系）
 */

public class AddSocialMemberNotify extends AbsResponseMessage
{
    /**
     * 增加的个人社交关系
     */
    private SocialObjectProxy socialObjectProxy;

    /**
     * 构造
     * 
     * @param _changeType
     * @param _socialObjectProxy
     */
    public AddSocialMemberNotify(SocialObjectProxy _socialObjectProxy)
    {
        socialObjectProxy = _socialObjectProxy;
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
        yos.writeByte(socialObjectProxy.socialRelationType.value());
        yos.writeInt(socialObjectProxy.userID);
        yos.writeUTF(socialObjectProxy.name);
        yos.writeByte(socialObjectProxy.vocation.value());
        yos.writeShort(socialObjectProxy.level);
        yos.writeByte(socialObjectProxy.sex.value());
    }
}
