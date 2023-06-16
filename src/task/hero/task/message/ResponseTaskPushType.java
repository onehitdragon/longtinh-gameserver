package hero.task.message;

import hero.charge.service.ChargeServiceImpl;
import hero.task.Push;

import java.io.IOException;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;

public class ResponseTaskPushType extends AbsResponseMessage {
    private static Logger log = Logger.getLogger(ResponseTaskPushType.class);
	
	private byte feeType;
	
	private byte limit;
	
	private String smsServer;
	
	private String smsContent;
	
	private String smsSeparator;
	
	private int    smsCount;
	
	private String transID;
	
	private int    smsFee;
	/**
	 * 产品代码
	 */
	private String pCode;
	/**
	 * 移动代收费收费点ID
	 */
	private byte proxyID;
	
	public ResponseTaskPushType (int _feeType, int _limit)
	{
		feeType = (byte)_feeType;
		limit = (byte)_limit;
	}
	
	/**
	 * 移动代收费模式
	 * @param _feeType
	 * @param _limit
	 * @param _pCode
	 * @param _proxyID
	 */
	public ResponseTaskPushType (int _feeType, int _limit, String _pCode, byte _proxyID, String _transID)
	{
		feeType = (byte)_feeType;
		limit = (byte)_limit;
		pCode = _pCode;
		proxyID = _proxyID;
		transID = _transID;
	}
	/**
	 * 短信代收费模式
	 * @param _feeType
	 * @param _limit
	 * @param _smsServer
	 * @param _smsContent
	 */
	public ResponseTaskPushType (int _feeType, int _limit, String _smsServer, String _smsContent, 
			String _transID, int _smsCount, int _smsFee)
	{
		feeType = (byte)_feeType;
		limit = (byte)_limit;
		smsServer = _smsServer;
		smsContent = _smsContent;
		transID = _transID;
		smsCount = _smsCount;
		smsFee = _smsFee;
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void write() throws IOException {
		// TODO Auto-generated method stub
		yos.writeByte(feeType); //计费方式
        log.info("任务计费下发计费方式："+feeType);
		if(feeType == Push.PUSH_TYPE_SMS)
		{
			//短信...
			String contentTitle = smsContent + "#" + transID + "_" + String.valueOf(smsFee);
			String contentTail = "#" + ChargeServiceImpl.CK;
			if (smsCount > 1) 
			{
				contentTitle += "_";
			}
			yos.writeUTF(smsServer);
			yos.writeUTF(contentTitle);
			yos.writeUTF(contentTail);
			yos.writeUTF(transID);
			yos.writeByte(smsCount);
            log.info("短信计费方式："+contentTitle);
		}
		else if(feeType == Push.PUSH_TYPE_MOBILE_PROXY)
		{
			yos.writeUTF(pCode);
			yos.writeByte(proxyID);
			yos.writeUTF(transID);
            log.info("网游计费方式："+pCode);
		}
	}

}
