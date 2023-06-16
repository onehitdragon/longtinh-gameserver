package hero.player.message;


import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 11-1-26
 * Time: 下午4:37
 * 回应镶嵌天书是否成功
 * 0x419
 */
public class ResponseInlayHeavenBook extends AbsResponseMessage {
    private int bookID;
    private byte position;   //镶嵌的位置
    private boolean success;  // 是否成功
    private boolean allSame; //3本天书是否一样

    public ResponseInlayHeavenBook(int bookID, byte position, boolean success,boolean allSame) {
        this.bookID = bookID;
        this.position = position;
        this.success = success;
        this.allSame = allSame;
    }

    @Override
    protected void write() throws IOException {
        yos.writeInt(bookID);
        yos.writeByte(position);
        yos.writeByte(success);
        yos.writeByte(allSame);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
