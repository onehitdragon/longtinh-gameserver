package hero.map.message;

import hero.npc.Npc;
import hero.share.service.ME2ObjectList;
import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-3-23
 * Time: 下午4:28
 *  返回地图上NPC简单信息
 * 0x2a19
 */
public class ResponseMapNpcList extends AbsResponseMessage{
    private static Logger log = Logger.getLogger(ResponseMapNpcList.class);
    private short mapID;
    private ME2ObjectList npcList;

    public ResponseMapNpcList(ME2ObjectList npcList,short mapID) {
        this.npcList = npcList;
        this.mapID = mapID;
    }

    @Override
    protected void write() throws IOException {
        log.debug("mapid="+mapID+",npclist size = " + npcList.size());
        yos.writeShort(mapID);
        yos.writeShort(npcList.size());
        Npc npc;
        for (int i = 0; i < npcList.size(); i++)
        {
            npc = (Npc) npcList.get(i);
            yos.writeInt(npc.getID());
            yos.writeUTF(npc.getTitle());
            yos.writeUTF(npc.getName());
            yos.writeByte(npc.getClan().getID());
            yos.writeShort(npc.getCellX());
            yos.writeShort(npc.getCellY());
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
