package hero.share.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.letter.LetterService;
import hero.share.message.ResponseLetterList;

public class RequestLetterList extends AbsClientProcess
{
    public void read () throws Exception
    {
        HeroPlayer player = (HeroPlayer) PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);
        ResponseMessageQueue.getInstance().put(
                player.getMsgQueueIndex(),
                new ResponseLetterList(LetterService.getInstance().getLetterList(
                        player.getUserID())));
    }
}
