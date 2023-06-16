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
 * 给其它人充值获取流水号
 * @author jiaodongjie
 * ox3b07
 */
public class GetTransIDForOther extends AbsClientProcess
{
    private static Logger log = Logger.getLogger(GetTransIDForOther.class);
	@Override
	public void read () throws Exception
	{
//		String fpcode = input.readUTF();
//        int price = input.readInt();  //计费信息里的价格(单位：分)
//        String nickname = input.readUTF();
//        String swcode = input.readUTF();
//        String mobileUserID = input.readUTF();
//        int publisher = input.readInt();

        String nickname = yis.readUTF();
		
		HeroPlayer player = PlayerServiceImpl.getInstance()
        							.getPlayerBySessionID(contextData.sessionID);
		
		if (player != null && player.isEnable())
        {
			
			HeroPlayer other = PlayerServiceImpl.getInstance().getOffLinePlayerInfoByName(nickname);
			if(other == null){
				ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                        new Warning("没找到\""+nickname+"\"玩家",Warning.UI_TOOLTIP_AND_EVENT_TIP,Warning.SUBFUNCTION_UI_CHARGEUP));
				return;
			}

            // 生成流水号
            String tranID = ChargeServiceImpl.getInstance().getTransIDGen();

/*
            FeePointInfo feePointInfo = ChargeServiceImpl.getInstance().getFpcodeByTypeAndPrice(FPType.CHARGE,price);
            String fpcode = feePointInfo.fpcode;
            log.debug("给别人充值获取计费伪码,fpcode="+fpcode);



            FeeIni feeIni = ChargeServiceImpl.getInstance().getFeeIni(player.getLoginInfo().accountID,player.getUserID(),
                                swcode,fpcode,tranID,player.getLoginInfo().loginMsisdn,mobileUserID,publisher,ChargeServiceImpl.CA);

            if(feeIni.status>0){
                OutMsgQ.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_GET_FEEINI_FAIL,Warning.UI_TOOLTIP_TIP));
                return;
            }*/
			

            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                    new ResponseTransID(tranID, other.getUserID(),other.getLoginInfo().accountID,
                    		(byte)GmServiceImpl.gameID,(short)GmServiceImpl.serverID));

        }
		
	}
	
}
