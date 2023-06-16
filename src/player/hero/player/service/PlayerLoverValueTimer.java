package hero.player.service;

import hero.lover.service.LoverServiceImpl;
import hero.player.HeroPlayer;

import java.util.TimerTask;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 11-1-23
 * Time: 上午11:15
 * 恋人或夫妻两人双方同时在线时，每过一分钟获得到 10 点爱情度。
 * 第一次运行时，确保双方都在线
 */
public class PlayerLoverValueTimer extends TimerTask {
    private String playerName;
    private String otherName;
    public PlayerLoverValueTimer(String playerName, String otherName){
        this.playerName = playerName;
        this.otherName = otherName;
    }

    @Override
    public void run() {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByName(playerName);
        HeroPlayer other = PlayerServiceImpl.getInstance().getPlayerByName(otherName);
        if(player != null && other != null
                && player.isEnable() && (!player.isDead())
                    && other.isEnable() && (!other.isDead())){
            if(!player.addLoverValue(LoverServiceImpl.ADD_LOVER_VALUE_FOR_PER_MINUTE)){
                this.cancel();
                PlayerServiceImpl.getInstance().removeLoverValueTimer(player);
            }
            if(!other.addLoverValue(LoverServiceImpl.ADD_LOVER_VALUE_FOR_PER_MINUTE)){
                this.cancel();
                PlayerServiceImpl.getInstance().removeLoverValueTimer(other);
            }

        }else{
            this.cancel();

        }
    }
}
