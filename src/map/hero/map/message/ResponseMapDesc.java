package hero.map.message;


import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-3-23
 * Time: 下午4:17
 * 返回地图描述
 * 0x2a18
 */
public class ResponseMapDesc extends AbsResponseMessage{
    private String desc;
    private short mapID;

    public ResponseMapDesc(String desc,short mapID) {
        this.desc = desc;
        this.mapID = mapID;
    }

    @Override
    protected void write() throws IOException {
        yos.writeShort(mapID);
        yos.writeUTF(desc);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
