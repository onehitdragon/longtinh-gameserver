package hero.gm.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GmReplyResponse.java
 * @创建者 ChenYaMeng
 * @版本 1.0
 * @时间 2010-6-24 下午04:22:04
 * @描述 ：报文（0x5002），GM回复玩家消息
 */

public class GmReplyResponse extends AbsResponseMessage
{
    /**
     * gm名字
     */
    private String gmName;

    /**
     * sid
     */
    private int    sessionID;

    /**
     * 问题ID
     */
    private int    questionID;

    /**
     * 聊天内容
     */
    private String content;

    public GmReplyResponse(String _gmName, int _sid, int _questionID,
            String _content)
    {
        gmName = _gmName;
        sessionID = _sid;
        questionID = _questionID;
        content = _content;
    }

    @Override
    public int getPriority ()
    {
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        try
        {
            yos.writeUTF(gmName);
            yos.writeInt(sessionID);
            yos.writeInt(questionID);
            yos.writeUTF(content);
            yos.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
