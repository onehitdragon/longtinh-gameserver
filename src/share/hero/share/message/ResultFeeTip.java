package hero.share.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**<p>
 *  Copyright: DGFun CO., (c) 2008
 *  </p>
 *  @文件   ResultFeeTip.java
 *  @创建者  ChenYaMeng
 *  @版本    1.0
 *  @时间   2010-7-1 下午04:02:21
 *  @描述 ：充值反馈提示
 **/

public class ResultFeeTip extends AbsResponseMessage
{
    private String tip;
    
    public ResultFeeTip(String _tip)
    {
        tip = _tip;
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
        yos.writeUTF(tip);
    }

}


