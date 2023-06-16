package hero.item.message;


import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 2010-11-21
 * Time: 17:46:37
 * 询问玩家是否要用点数购买一个剥离宝石
 * 0xd31
 */
public class AskBuyJewel extends AbsResponseMessage {

    private int currPoint;//玩家当前的点数
    public AskBuyJewel(int _currPoint){
        currPoint = _currPoint;
    }
    @Override
    protected void write() throws IOException {
        yos.writeInt(currPoint);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
