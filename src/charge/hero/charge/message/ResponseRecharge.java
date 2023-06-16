package hero.charge.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**<p>
 *  Copyright: DGFun CO., (c) 2008
 *  </p>
 *  @文件   ResponseRecharge.java
 *  @创建者  ChenYaMeng
 *  @版本    1.0
 *  @时间   2010-7-2 上午09:08:01
 *  @描述 ：点数不足，充值计费
 **/

public class ResponseRecharge extends AbsResponseMessage
{

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

    }

}


