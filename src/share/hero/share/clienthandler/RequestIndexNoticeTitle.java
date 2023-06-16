package hero.share.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.ResponseIndexNoticeList;
import hero.share.service.ShareServiceImpl;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-4-20
 * Time: 下午5:51
 * 公告/活动界面  0x5d23
 */
public class RequestIndexNoticeTitle extends AbsClientProcess{
    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);

        byte type = yis.readByte(); //0：所有    1：公告   2:活动

        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new ResponseIndexNoticeList(ShareServiceImpl.getInstance().getInoticeList(type)));
    }
}
