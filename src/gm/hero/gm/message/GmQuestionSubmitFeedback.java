package hero.gm.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**<p>
 *  Copyright: DGFun CO., (c) 2008
 *  </p>
 *  @文件   GmQuestionSubmitFeedback.java
 *  @创建者  ChenYaMeng
 *  @版本    1.0
 *  @时间   2010-6-24 下午04:01:14
 *  @描述 ： 报文（0x5001），问题提交成功与否的回执，客户端表现在图标绘制
 **/

public class GmQuestionSubmitFeedback extends AbsResponseMessage
{

    public static final byte FAIL = 0;
    public static final byte OK = 1;
    
    private byte feedback;
    
    public GmQuestionSubmitFeedback(byte _feedback)
    {
        feedback = _feedback;
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
            yos.writeByte(feedback);
            yos.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


}


