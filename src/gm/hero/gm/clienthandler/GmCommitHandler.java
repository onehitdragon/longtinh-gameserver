package hero.gm.clienthandler;

import hero.gm.EResponseType;
import hero.gm.ResponseToGmTool;
import hero.gm.service.GmServiceImpl;

import java.io.IOException;

import yoyo.core.process.AbsClientProcess;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GmCommitHandler.java
 * @创建者 ChenYaMeng
 * @版本 1.0
 * @时间 2010-6-24 下午03:56:00
 * @描述 ：报文（0x5003），玩家对GM的评价
 */

public class GmCommitHandler extends AbsClientProcess
{
    @Override
    public void read () throws Exception
    {
        try
        {
            int questionID = yis.readInt();
            byte appraise = yis.readByte();

            /**
             * 推送GM评价信息，插入队列
             */
            ResponseToGmTool rtgt = new ResponseToGmTool(
                    EResponseType.SEND_QUESTION_APPRAISE, 0);
            rtgt.setQuestionAppraise(questionID, appraise);
            GmServiceImpl.addGmToolMsg(rtgt);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
