package hero.task.message;

import hero.player.HeroPlayer;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-4-18
 * Time: 下午2:24
 * 玩家循环任务接收次数通知  0xf13
 */
public class NotifyPlayerReciveRepeateTaskTimes extends AbsResponseMessage{

    private HeroPlayer player;

    public NotifyPlayerReciveRepeateTaskTimes(HeroPlayer player) {
        this.player = player;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        yos.writeInt(player.receivedRepeateTaskTimes);
        yos.writeInt(player.getCanReceiveRepeateTaskTimes());
    }
}
