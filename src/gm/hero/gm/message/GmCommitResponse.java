package hero.gm.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GmCommitResponse.java
 * @创建者 ChenYaMeng
 * @版本 1.0
 * @时间 2010-6-24 下午03:59:26
 * @描述 ：报文（0x5003），提示客户端GM已经处理完该问题，客户端表现在问题图标不绘制， 
 *        传回来的questionID用于玩家评价GM时的交互
 */

public class GmCommitResponse extends AbsResponseMessage
{

    private int questionID = -1;

    public GmCommitResponse(int _questionID)
    {
        questionID = _questionID;
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
            yos.writeInt(questionID);
            yos.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
