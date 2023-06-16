package hero.share.message;

import hero.share.Inotice;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import yoyo.core.packet.AbsResponseMessage;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-4-20
 * Time: 下午5:55
 * 返回公告或活动标题   0x5d25
 */
public class ResponseIndexNoticeList extends AbsResponseMessage{

    private List<Inotice> inoticeList;

    public ResponseIndexNoticeList(List<Inotice> inoticeList) {
        this.inoticeList = inoticeList;
    }

    @Override
    protected void write() throws IOException {
        yos.writeInt(inoticeList.size());
        String title;
        for(Inotice inotice : inoticeList){
            yos.writeInt(inotice.id);
            title = inotice.title;
            /*if(inotice.isTop()){
                title += "[置顶]";
            }*/
            yos.writeUTF(title);
            yos.writeInt(inotice.color);
        }

    }

    @Override
    public int getPriority() {
        return 0;
    }
}
