package hero.chat.message;

import hero.chat.service.ChatServiceImpl;
import hero.share.DirtyStringDict;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2010
 * </p>
 * 
 * @文件 GmCommitResponse.java
 * @创建者 Luke 陈路
 * @修订者 ChenYaMeng
 * @版本 1.0
 * @时间 2010-06-11 14:22:21
 * @描述 ：报文（0xc01），各种聊天信息的发送，修订了GM专用频道
 */
public class ChatResponse extends AbsResponseMessage
{
    /**
     * 类型
     */
    byte   type;

    /**
     * 发送者名称
     */
    String name;

    /**
     * 目标名称
     */
    String dest;

    /**
     * 内容
     */
    String content;

    /**
     * SessionID方便与GM工具同步
     */
    int    sessionID;

    /**
     * GM的问题ID
     */
    int    questionID;
    /**
     * 是否显示在界面中央
     */
    boolean showMiddle = false;

    /**
     * @param _type 聊天形式
     * @param _name 发送者
     * @param _content 内容
     */
    public ChatResponse(byte _type, String _name, String _content)
    {
        type = _type;
        name = _name;
        content = _content;
    }
    
    /**
     * @param _type 聊天形式
     * @param _name 发送者
     * @param _content 内容
     * @param _showMiddle 是否在界面中央(出系统公告的地方)显示
     */
    public ChatResponse(byte _type, String _name, String _content,boolean _showMiddle)
    {
        type = _type;
        name = _name;
        content = _content;
        showMiddle = _showMiddle;
    }

    /**
     * @param _type 聊天形式
     * @param _name 发送者
     * @param _dest 接受者
     * @param _content 内容
     */
    public ChatResponse(byte _type, String _name, String _dest, String _content)
    {
        type = _type;
        name = _name;
        dest = _dest;
        content = _content;
    }

    /**
     * 构建下行GM的消息
     * 
     * @param _type 聊天形式
     * @param _name 发送者
     * @param _sessionID
     * @param _content 内容
     */
    public ChatResponse(byte _type, String _name, int _sessionID,
            int _questionID, String _content)
    {
        type = _type;
        name = _name;
        sessionID = _sessionID;
        questionID = _questionID;
        content = _content;
    }

    protected void write () throws IOException
    {
        // TODO Auto-generated method stub
    	//add by zhengl; date: 2011-02-27; note: 添加聊天关键字过滤
        if(type != ChatServiceImpl.TOP_SYSTEM_WORLD)
    	    content = DirtyStringDict.getInstance().clearDirtyChar(content);
        try
        {
            yos.writeByte(type);

            if (name == null)
                name = "";
            yos.writeUTF(name);

            if (type == ChatServiceImpl.PLAYER_SINGLE)
            {
                yos.writeUTF(dest);
            }

            yos.writeUTF(content);
            yos.writeByte(showMiddle);
            yos.flush();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public int getPriority ()
    {
        // TODO Auto-generated method stub
        return 0;
    }

}
