package hero.charge.message;


import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-3-30
 * Time: 下午3:18
 * 0x3b14 返回查询结果，以 #HH 换行
 */
public class ResponseQueryResult extends AbsResponseMessage{
    private String result;

    public ResponseQueryResult(String result) {
        this.result = result;
    }

    @Override
    protected void write() throws IOException {
        yos.writeUTF(result);

    }

    @Override
    public int getPriority() {
        return 0;
    }
}
