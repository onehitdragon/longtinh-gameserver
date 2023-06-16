package hero.share.clienthandler;

import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.letter.LetterService;
import hero.share.message.Warning;
import hero.share.service.Tip;

import java.io.IOException;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 OperateLetter.java
 * @创建者 LuLin
 * @版本 1.0
 * @时间 2010-06-18 下午14:35:18
 * @描述 ：操作信件
 */

public class OperateLetter extends AbsClientProcess
{
    /**
     * 读
     */
    private static final byte READ   = 0;

    /**
     * 保存
     */
    private static final byte SAVE   = 1;

    /**
     * 删除
     */
    private static final byte DELELE = 2;

    /*
     * (non-Javadoc)
     * 
     * @see me2.core.handler.ClientHandler#read()
     */
    public void read () throws Exception
    {
        HeroPlayer player = (HeroPlayer) PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        try
        {
            byte type = yis.readByte();
            int letterID = yis.readInt();

            switch (type)
            {
                case READ:
                {
                    LetterService.getInstance().settingToRead(player.getUserID(),
                            letterID);

                    break;
                }
                case SAVE:
                {
                    LetterService.getInstance().settingToSaved(player.getUserID(),
                            letterID);
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_SHARE_MAIL_OF_SAVED, Warning.UI_STRING_TIP));

                    break;
                }
                case DELELE:
                {
                    LetterService.getInstance().removeLetter(player.getUserID(),
                            letterID);
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_SHARE_MAIL_OF_DELETE, Warning.UI_STRING_TIP));

                    break;
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
