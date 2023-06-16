package hero.charge.message;

import java.io.IOException;

import hero.charge.FeeIni;
import hero.charge.service.ChargeConfig;
import hero.charge.service.ChargeServiceImpl;
import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;

/**<p>
 *  Copyright: DGFun CO., (c) 2008
 *  </p>
 *  @文件   ResponseTransID.java
 *  @创建者  ChenYaMeng
 *  @版本    1.0
 *  @时间   2010-6-28 上午11:08:36
 *  @描述 ： 返回流水号   下行 0x3b06
 **/

public class ResponseTransID extends AbsResponseMessage
{
    private static Logger log = Logger.getLogger(ResponseTransID.class);
    /**
     * 流水号
     */
    private String tranID;
    /**
     * 玩家userID,如果是给其他人充值的，则是其他人的userID
     */
    private int userID; 
    /**
     * 玩家accountID,如果是给其他人充值的，则是其他人的accountID
     */
    private int accountID;
    private byte gameID;
    private short serverID;

//    private FeeIni feeIni;
    
    public ResponseTransID(String _tranID, int _userID,int _accountID,byte _gameID,short _serverID)
    {
        tranID = _tranID;
        userID = _userID;
        accountID = _accountID;
        gameID = _gameID;
        serverID = _serverID;
//        feeIni = _feeIni;
    }

    @Override
    public int getPriority ()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        // TODO Auto-generated method stub        
        yos.writeUTF(tranID);
        yos.writeInt(userID);
        yos.writeInt(accountID);
        yos.writeByte(gameID);
        yos.writeShort(serverID);
//        output.writeUTF(ChargeServiceImpl.getInstance().getConfig().fee_ini_url);
        /*log.debug("下发计费配置信息, feetype="+feeIni.feeType+",feecode="+feeIni.feeCode+",feeurlID="+feeIni.feeUrlID+",price="+feeIni.price);
        output.writeUTF(feeIni.feeType);
        output.writeUTF(feeIni.feeCode);
        output.writeUTF(feeIni.feeUrlID);
        output.writeInt(feeIni.price);*/
        /*if(feeIni.feeType.equals(FeeIni.FEE_TYPE_SMS)){
            int smsnum = feeIni.sumPrice/feeIni.price;
            if(smsnum < 1) smsnum = 1;
            output.writeInt(smsnum);//需要发短信的条数
        }*/
    }

}


