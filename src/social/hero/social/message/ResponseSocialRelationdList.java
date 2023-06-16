package hero.social.message;

import hero.social.SocialObjectProxy;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseSocialRelationdList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-9 下午18:46:45
 * @描述 ：响应查看社交关系列表（包括友好、仇人、屏蔽三种关系）
 */

public class ResponseSocialRelationdList extends AbsResponseMessage
{
    private static Logger log = Logger.getLogger(ResponseSocialRelationdList.class);
    /**
     * 社交关系类型
     */
    private byte                 socialRelationType;

    /**
     * 社交关系列表
     */
    ArrayList<SocialObjectProxy> list;

    /**
     * 构造
     * 
     * @param _list
     */
    public ResponseSocialRelationdList(byte _socialRelationType,
            ArrayList<SocialObjectProxy> _list)
    {
        socialRelationType = _socialRelationType;
        list = _list;
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
        log.debug("下发社交列表，类型="+socialRelationType);
        // TODO Auto-generated method stub
        yos.writeByte(socialRelationType);

        if (null != list && list.size() > 0)
        {
            log.debug("social list size = " + list.size());
            yos.writeShort(list.size());

            for (SocialObjectProxy socialObjectProxy : list)
            {
                log.debug("userid="+socialObjectProxy.userID+",name="+socialObjectProxy.name+",isOnline="+socialObjectProxy.isOnline);
                yos.writeInt(socialObjectProxy.userID);
                yos.writeUTF(socialObjectProxy.name);
                yos.writeByte(socialObjectProxy.isOnline);


                if (socialObjectProxy.isOnline)
                {
                    log.debug("user isonline...");
                    yos.writeByte(socialObjectProxy.vocation.value());
                    yos.writeShort(socialObjectProxy.level);
                    yos.writeByte(socialObjectProxy.sex.value());
                    log.debug("vocation="+socialObjectProxy.vocation.value()+",level="+socialObjectProxy.level+",sex="+socialObjectProxy.sex.value());
                }else{
                    yos.writeByte(0);
                    yos.writeShort(0);
                    log.debug("user offline...");
                }
            }
        }
        else
        {
            yos.writeShort(0);
        }
        log.debug("下发社交列表 end ...");
    }

}
