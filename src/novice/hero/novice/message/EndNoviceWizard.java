package hero.novice.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EndNoviceWizard.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-9-27 下午02:18:19
 * @描述 ：
 */

public class EndNoviceWizard extends AbsResponseMessage
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
