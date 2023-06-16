package hero.player.clienthandler;

import yoyo.core.process.AbsClientProcess;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-5-11
 * Time: 下午2:39
 * 0x420 设置玩家是否开启各个聊天频道
 */
public class RequestOpenOrCloseWorldChat extends AbsClientProcess{
    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);

        if(player != null){
            byte worldFlag = yis.readByte();// 是否开启世界聊天  0:关闭  1:开启
            byte clanFlag = yis.readByte(); // 是否开启种族聊天  0:关闭  1:开启
            byte mapFlag = yis.readByte();  // 是否开启区域聊天  0:关闭  1:开启
            byte singleFlag = yis.readByte(); //是否开启私聊     0:关闭  1:开启

            player.openWorldChat = worldFlag == 1;
            player.openClanChat = clanFlag == 1;
            player.openMapChat = mapFlag == 1;
            player.openSingleChat = singleFlag == 1;
        }
    }
}
