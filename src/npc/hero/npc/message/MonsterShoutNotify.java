package hero.npc.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MonsterShoutNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-19 下午04:28:54
 * @描述 ：
 */

public class MonsterShoutNotify extends AbsResponseMessage
{
    /**
     * 怪物编号
     */
    private int    id;

    /**
     * 喊话内容
     */
    private String shoutContent;

    /**
     * 构造
     * 
     * @param _shoutContent
     */
    public MonsterShoutNotify(int _id, String _shoutContent)
    {
        id = _id;
        shoutContent = _shoutContent;
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
        yos.writeInt(id);
        yos.writeUTF(shoutContent);
    }
}
