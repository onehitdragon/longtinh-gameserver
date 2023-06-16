package hero.player.message;

import hero.item.special.HeavenBook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import yoyo.core.packet.AbsResponseMessage;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 11-1-27
 * Time: 下午4:13
 * 返回玩家所有天书列表
 * 0x421
 */
public class ResponseHeavenBookList extends AbsResponseMessage{

    private List<HeavenBook> heavenBookList = new ArrayList<HeavenBook>();

    public ResponseHeavenBookList(List<HeavenBook> heavenBookList) {
        this.heavenBookList = heavenBookList;
    }

    @Override
    protected void write() throws IOException {
        yos.writeByte(heavenBookList.size());
        for(HeavenBook book : heavenBookList){
            yos.writeInt(book.getID());
            yos.writeShort(book.getIconID());
            yos.writeShort(book.getSkillPoint());
            yos.writeByte(book.getTrait().value());
            yos.writeUTF(book.getName());
            yos.writeUTF(book.getDescription());
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
