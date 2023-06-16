package hero.charge.clienthandler;

import hero.charge.FPType;
import hero.charge.FeeIni;
import hero.charge.FeePointInfo;
import hero.charge.FeeType;
import hero.charge.service.ChargeDAO;
import hero.charge.service.ChargeServiceImpl;
import hero.gm.service.GmServiceImpl;
import hero.log.service.LogServiceImpl;
import hero.log.service.ServiceType;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;
import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

/**
 * 神州付充值报文
 * @author jiaodongjie
 * 0x3b10
 *
 * 卡号11035020237380575 密码：02 1483 1200 1610 6086 ---30元
 */

public class ChargeUp extends AbsClientProcess
{
    private static Logger log = Logger.getLogger(ChargeUp.class);

	@Override
	public void read () throws Exception
	{
        log.debug("神州付充值....");

		HeroPlayer player = PlayerServiceImpl.getInstance()
        						.getPlayerBySessionID(contextData.sessionID);

        byte chargeid = yis.readByte(); //充值信息id
        String transID = yis.readUTF();
        String mid = yis.readUTF();
        int userID = yis.readInt(); //要充值的USERID(有可能是其他人的)
		int accountID = yis.readInt(); //要充值的账号ID(有可能是其他人的)

        int publisher = yis.readInt(); //渠道号
        String ip = yis.readUTF(); //用户IP地址用户网关IP

        byte rechargetype = player.getLoginInfo().accountID==accountID?(byte)1:(byte)2;//1：给自己充值    2：给别人充值

        String othername = player.getName();
        if(rechargetype == 2){
            HeroPlayer other = PlayerServiceImpl.getInstance().getOffLinePlayerInfo(userID);
            if(other != null){
                othername = other.getName();
            }
            if(other == null){
				ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                        new Warning("没找到玩家",Warning.UI_TOOLTIP_AND_EVENT_TIP,Warning.SUBFUNCTION_UI_CHARGEUP));
				return;
			}
        }

        FeePointInfo feePointInfo = ChargeServiceImpl.getInstance().getFpcodeByTypeAndPrice(chargeid);

        String msisdn = player.getLoginInfo().loginMsisdn;
        String bindmsisdn = player.getLoginInfo().boundMsisdn;


        String cardnum = yis.readUTF();//卡号
        String cardpass = yis.readUTF();//卡密码
        int cardsum = feePointInfo.price; //面值

        byte cardtypecombine = feePointInfo.typeID;//充值卡类型 0:移动；1:联通 ；2:电信

        FeeType feeType = ChargeServiceImpl.getInstance().getFeeTypeById(feePointInfo.typeID);
        if(feeType != null){
            cardtypecombine = (byte)feeType.cardType.getId();
        }

        if(ip == null || ip.trim().length()==0){
            ip = getIp();
        }

        //订单号#状态码#状态信息（响应码200时会异步同步充值结果）
        String result = ChargeServiceImpl.getInstance().chargeUpSZF(transID, accountID, userID, accountID, cardnum, cardpass, cardsum, cardtypecombine,
                                                                            msisdn,feePointInfo.price, ServiceType.CHARGEUP,publisher,ip,bindmsisdn);
        if(result != null && result.trim().length()>0){
            log.debug("charge up szf result =" + result);
            if(result.indexOf("#")>0){
                String[] res = result.split("#");
                String orderid = res[0];
                String status_code = res[1];
                String status_desc = "已提交，请等待结果！";
                if(res.length == 3){ //当乐神州付返回时 res结果长度=2
                    status_desc = res[2];
                }
                log.info("chargeup: transID="+transID+",res:["+orderid+"]["+status_code+"]["+status_desc+"]");
                int syncres = status_code.equals("200")?0:1;
                ChargeDAO.insertChargeUpSZF(player.getLoginInfo().accountID,player.getUserID(),ChargeServiceImpl.PAYTYPE_SZF,rechargetype,
                        accountID,userID,transID,status_code,feePointInfo.price,orderid,syncres,feePointInfo.fpcode);

                //保存到xj_account数据库以便于回调使用
                ChargeDAO.insertChargeUpSZFAccount(GmServiceImpl.gameID,GmServiceImpl.serverID,accountID,userID,transID,orderid);

                if(status_code.equals("200")){
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_CHARGE_WAITTING));
                }else {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning(status_desc,Warning.UI_TOOLTIP_TIP));
                }

                // 充值发起日志记录
                LogServiceImpl.getInstance().chargeGenLog(
                    player.getLoginInfo().accountID,
                    player.getLoginInfo().username, player.getUserID(),
                    player.getName(), player.getLoginInfo().loginMsisdn,
                    transID, feePointInfo.fpcode,rechargetype,othername,accountID,syncres+"","神州付",res[1],status_desc);

            }else{
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_CHARGE_FAIL,Warning.UI_TOOLTIP_TIP));
            }
        }else {
            // 充值发起日志记录
                LogServiceImpl.getInstance().chargeGenLog(
                    player.getLoginInfo().accountID,
                    player.getLoginInfo().username, player.getUserID(),
                    player.getName(), player.getLoginInfo().loginMsisdn,
                    transID, feePointInfo.fpcode, rechargetype,othername,accountID,"1","神州付","失败","结果为空");
        }


        /* 充值暂时不用网游 ，如果用，则加一个新报文单独处理
        else if(feeIni.feeType == FeeIni.FEE_TYPE_NG){
            log.debug("网游充值....");

            int count = (price/10)/feeIni.price; //发送请求的次数
            if(count < 1){
                count = 1;
            }
            log.debug("recharge up count="+count);

            String[] transIDS = new String[count];
            if(count>1){
                for (int i=0; i<count; i++){
                    transIDS[i] = transIDS+"_"+(i+1);
                }
            }else {
                transIDS[0] = transID;
            }
            log.debug("网游请求次数：count="+count);
//            OutMsgQ.getInstance().put(player.getMsgQueueIndex(),new Warning("网游充值暂未开通，请使用其它方式充值",Warning.UI_TOOLTIP_TIP));
			for(int i=0; i<count; i++){
                //0#状态码（成功）；失败 1#状态码 （自定义状态码004:计费参数不全）
                String ngres = ChargeServiceImpl.getInstance().chargeUpNg(feeIni.feeUrlID, transIDS[i], accountID, accountID, userID, msisdn,ServiceType.CHARGEUP,publisher,ChargeServiceImpl.CA);
                if(ngres != null && ngres.trim().length()>0){
                    String[] ngreses = ngres.split("#");
                    ChargeDAO.insertChargeUpNG(player.getLoginInfo().accountID,player.getUserID(),ChargeServiceImpl.PAYTYPE_NG,(byte)0,accountID,userID,transIDS[i],ngreses[1],price,Integer.parseInt(ngreses[0]),fpcode);

                     // 发起充值日志记录
                    LogServiceImpl.getInstance().chargeGenLog(
                            player.getLoginInfo().accountID,
                            player.getLoginInfo().username, player.getUserID(),
                            player.getName(), player.getLoginInfo().loginMsisdn,
                            transIDS[i], fpcode, rechargetype,othername,accountID,ngreses[0],"网游",ngreses[1],"");
                }else {
                    // 发起充值日志记录
                    LogServiceImpl.getInstance().chargeGenLog(
                            player.getLoginInfo().accountID,
                            player.getLoginInfo().username, player.getUserID(),
                            player.getName(), player.getLoginInfo().loginMsisdn,
                            transIDS[i], fpcode, rechargetype,othername,accountID,"1","网游","失败","");
                }
            }
            // 处理额外赠送点数

        }*/
	}


	public static void main(String[] args){
//		String result = ChargeServiceImpl.getInstance().chargeUpSZF("1-1-20110428180305378", 44, 1069, 44, "111101182817716", "1106178930402227143",20,(byte)1, "13718345631",10,ServiceType.CHARGEUP,0,"192.168.0.59","13718345631");
//        String result = ChargeServiceImpl.getInstance().addPoint(null,"1-1-20110314180305334","1111111111",111942,1394,10000,(byte)0,1,"","111111");
//        String result = ChargeServiceImpl.getInstance().reducePoint(null,"305334",111942,1394,10000,340004,"1",1);
        String result = ChargeServiceImpl.getInstance().queryBalancePoint(50057);
//         String result = ChargeServiceImpl.getInstance().queryChargeUpDetail(112437,20016,2,null,null);
//        String result = ChargeServiceImpl.getInstance().queryConsumeDetail(111942,1394,"2011-03-17 00:00:00","2011-03-18 00:00:00");
        System.out.println(result);

        /*StringBuffer sf = new StringBuffer();
        for(int i=0; i<5; i++){
            sf.append("aaaa").append(",");
        }
        sf.deleteCharAt(sf.length()-1);
        System.out.println(sf);*/
	}
}