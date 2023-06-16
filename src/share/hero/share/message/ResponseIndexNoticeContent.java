package hero.share.message;


import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-4-20
 * Time: 下午5:56
 * 返回公告或活动内容 0x5d26
 */
public class ResponseIndexNoticeContent extends AbsResponseMessage{
    private String content;

    public ResponseIndexNoticeContent(String content) {
        this.content = content;
    }

    @Override
    protected void write() throws IOException {
        yos.writeUTF(content);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
