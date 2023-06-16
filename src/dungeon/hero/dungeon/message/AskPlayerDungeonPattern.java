package hero.dungeon.message;


import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 2010-12-3
 * Time: 14:16:59
 * 0x4203
 */
public class AskPlayerDungeonPattern extends AbsResponseMessage {
    private short mapID;
    private short targetX;
    private short targetY;

    public AskPlayerDungeonPattern( short mapID, short targetX, short targetY) {
        this.mapID = mapID;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    @Override
    protected void write() throws IOException {
        yos.writeShort(mapID);
        yos.writeShort(targetX);
        yos.writeShort(targetY);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
