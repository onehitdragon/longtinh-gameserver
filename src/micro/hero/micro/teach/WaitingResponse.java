package hero.micro.teach;

import hero.micro.teach.message.ClearConfirmDialog;
import hero.player.HeroPlayer;
import hero.share.message.Warning;

import java.util.TimerTask;

import yoyo.core.queue.ResponseMessageQueue;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 11-1-17
 * Time: 上午11:27
 * 等待计时器，如果对方在设定的时间内没有回复，则提示对方忙，
 * 并可以通知客户端清除对方手机上的确认框
 */
public class WaitingResponse extends TimerTask {
    private int waitingtime = 0;
    private HeroPlayer player;

    private int time;  //设置的等待的时间: 秒
    private boolean clear; // 是否清除确认框: true 清除 ，false 不清除

    public WaitingResponse(HeroPlayer _player, int _time, boolean _clear) {
        this.player = _player;
        this.time = _time;
        this.clear = _clear;
    }

    @Override
    public void run() {
        if(waitingtime == time){
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("对不起，对方忙！"));
            if(clear)
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new ClearConfirmDialog());
            waitingtime = 0;
            player.waitingTimerRunning = false;
            this.cancel();
        }else{
            waitingtime++;
        }
    }
}
