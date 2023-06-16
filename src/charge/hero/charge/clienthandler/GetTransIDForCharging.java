package hero.charge.clienthandler;

import hero.charge.FeeIni;
import hero.charge.message.ResponseTransID;
import hero.charge.service.ChargeServiceImpl;
import hero.gm.service.GmServiceImpl;
import hero.log.service.LogServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;
import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-4-13
 * Time: 下午9:03
 * 计费时获取流水号 0x3b14  测试用
 */
public class GetTransIDForCharging extends AbsClientProcess{
    private static Logger log = Logger.getLogger(GetTransIDForCharging.class);

    @Override
    public void read() throws Exception {
        String fpcode = yis.readUTF();    //现在测试用 所以 fpcode固定为 10019
//        String fpcode = "10019";
        String swcode = yis.readUTF();
        String mobileUserID = yis.readUTF();
        int publisher = yis.readInt();

        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        if (player != null && player.isEnable())
        {
            // 生成流水号
            String tranID = ChargeServiceImpl.getInstance().getTransIDGen();
            log.debug(player.getName()+",get tranID = " + tranID);

           /* FeeIni feeIni = ChargeServiceImpl.getInstance().getFeeIni(player.getLoginInfo().accountID,player.getUserID(),
                                swcode,fpcode,tranID,player.getLoginInfo().loginMsisdn,mobileUserID,publisher,ChargeServiceImpl.CK);

            if(feeIni.status>0){
                OutMsgQ.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_GET_FEEINI_FAIL,Warning.UI_TOOLTIP_TIP));
                return;
            }

            if(feeIni.feeType.equals(FeeIni.FEE_TYPE_SMS)){
                feeIni.sumPrice = feeIni.price; //todo 测试用
            }else if(feeIni.feeType.equals(FeeIni.FEE_TYPE_ERROR)){
                OutMsgQ.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_GET_FEEINI_FAIL,Warning.UI_TOOLTIP_TIP));
                return;
            }*/

            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                        new ResponseTransID(tranID, player.getUserID(),player.getLoginInfo().accountID,
                                (byte) GmServiceImpl.gameID,(short)GmServiceImpl.serverID));
        }

    }
}
