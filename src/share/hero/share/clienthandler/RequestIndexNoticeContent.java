package hero.share.clienthandler;

import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.Inotice;
import hero.share.message.ResponseIndexNoticeContent;
import hero.share.service.ShareServiceImpl;

import java.util.List;
import java.util.Map;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-4-20
 * Time: 下午5:53
 * 请求公告或活动的内容   0x5d24
 */
public class RequestIndexNoticeContent extends AbsClientProcess{
    @Override
    public void read() throws Exception {

        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);

        int id = yis.readInt(); //公告或活动的ID
        List<Inotice> inoticeList = ShareServiceImpl.getInstance().getInoticeList(0);
        String content = "无内容";
        if(inoticeList != null && inoticeList.size()>0){
            for (Inotice inotice : inoticeList){
                if(inotice.id == id){
                    content = inotice.content;
                    break;
                }
            }
        }

        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new ResponseIndexNoticeContent(content));
    }
}
