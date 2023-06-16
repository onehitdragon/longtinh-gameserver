package hero.share.clienthandler;

import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.letter.Letter;
import hero.share.letter.LetterService;
import hero.share.message.MailStatusChanges;
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
 * @文件 PostLetter.java
 * @创建者 LuLin
 * @版本 1.0
 * @时间 2010-06-18 下午14:35:18
 * @描述 ：发送信件
 */

public class PostLetter extends AbsClientProcess
{
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
            String receiverName = yis.readUTF();
            String title = yis.readUTF();
            String content = yis.readUTF();

            HeroPlayer receiver = PlayerServiceImpl.getInstance()
                    .getPlayerByName(receiverName);
            int userID = 0;

            if (receiver == null)
            {
                userID = PlayerServiceImpl.getInstance().getUserIDByNameFromDB(
                        receiverName);
            }
            else
            {
                userID = receiver.getUserID();
            }

            if (userID > 0)
            {
                if (LetterService.getInstance().getLetterNumber(userID) >= LetterService.MAX_SIZE)
                {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_SHARE_MAIL_BOX_FULL, Warning.UI_STRING_TIP));

                    return;
                }
                else
                {
                    Letter letter = new Letter(LetterService.getInstance()
                            .getUseableLetterID(), title, player.getName(), userID,
                            receiverName, content);
                    LetterService.getInstance().addNewLetter(letter);

                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_SHARE_MAIL_SEND_SUCCESSFULLY, Warning.UI_STRING_TIP));

                    if (null != receiver && receiver.isEnable())
                    {
                        ResponseMessageQueue
                                .getInstance()
                                .put(
                                        receiver.getMsgQueueIndex(),
                                        new MailStatusChanges(
                                                MailStatusChanges.TYPE_OF_LETTER,
                                                true));
                    }
                }
            }
            else
            {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                        new Warning(Tip.TIP_SHARE_MAIL_TARGET_INVALIDATE_FAIL, Warning.UI_STRING_TIP));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
