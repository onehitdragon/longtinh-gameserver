package hero.item.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.item.EquipmentInstance;
import hero.item.message.ResponseSingleHoleEnhanceProperty;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 2010-11-21
 * Time: 14:57:29
 * 请求查看装备的强化细节
 * 单个孔的强化加成属性值
 * 上行报文 0xd29
 */
public class SingleHoleEnhanceProperty  extends AbsClientProcess{
    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);
        int equipID = yis.readInt();

        EquipmentInstance ei = player.getInventory().getEquipmentBag().getEquipmentByInstanceID(equipID);
        if(null == ei){
        	ei = player.getBodyWear().getEquipmentByInstanceID(equipID);
        }

        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new ResponseSingleHoleEnhanceProperty(ei));
    }
}
