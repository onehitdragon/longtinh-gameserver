package hero.charge.clienthandler;

import hero.charge.FPType;
import hero.charge.FeeIni;
import hero.charge.FeePointInfo;
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
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GetTransID.java
 * @创建者 ChenYaMeng
 * @版本 1.0
 * @时间 2010-6-28 上午10:44:17
 * @描述 ：0x3b06 客户端请求流水号，用于充值
 */

public class GetTransID extends AbsClientProcess
{
    private static Logger log = Logger.getLogger(GetTransID.class);
    @Override
    public void read () throws Exception
    {
//    	String fpcode = input.readUTF();
//        int price = input.readInt();  //计费信息里的价格(单位：分)
//        String swcode = input.readUTF();
//        String mobileUserID = input.readUTF();
//        int publisher = input.readInt();

        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        if (player != null && player.isEnable())
        {

            // 生成流水号
            String tranID = ChargeServiceImpl.getInstance().getTransIDGen();
            log.debug(player.getName()+",get tranID = " + tranID);

            /*FeePointInfo feePointInfo = ChargeServiceImpl.getInstance().getFpcodeByTypeAndPrice(FPType.CHARGE,price);
            String fpcode = feePointInfo.fpcode;
            log.debug("充值获取计费伪码,fpcode="+fpcode);


            FeeIni feeIni = ChargeServiceImpl.getInstance().getFeeIni(player.getLoginInfo().accountID,player.getUserID(),
                                swcode,fpcode,tranID,player.getLoginInfo().loginMsisdn,mobileUserID,publisher,ChargeServiceImpl.CA);

            if(feeIni.status>0){
                OutMsgQ.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_GET_FEEINI_FAIL,Warning.UI_TOOLTIP_TIP));
                return;
            }*/

            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                    new ResponseTransID(tranID, player.getUserID(),player.getLoginInfo().accountID,
                    		(byte) GmServiceImpl.gameID,(short)GmServiceImpl.serverID));
        }
    }
}
