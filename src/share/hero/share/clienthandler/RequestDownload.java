package hero.share.clienthandler;

import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.Constant;
import hero.share.message.ResponseDownload;
import hero.share.message.Warning;
import hero.share.service.AllPictureDataDict;
import hero.share.service.ShareServiceImpl;
import hero.share.service.Tip;

public class RequestDownload extends AbsClientProcess {
	
	private static Logger log = Logger.getLogger(RequestDownload.class);
	
	private final static byte FILE_TYPE_PNG = 0;
	
	private final static byte FILE_TYPE_ANU = 1;
	
	private final static byte FILE_TYPE_MAP = 2;

	@Override
	public void read() throws Exception {
		// TODO Auto-generated method stub
        HeroPlayer player = (HeroPlayer) PlayerServiceImpl.getInstance()
        	.getPlayerBySessionID(contextData.sessionID);
        
        String path = "";
		String url = yis.readUTF(); //当前URL
//		String fileName = input.readUTF(); /*edit by zhengl; date: 2011-04-17; note: 文件并与URL合并了*/
//		byte fileType = input.readByte(); /*edit by zhengl; date: 2011-04-17; note: 不要类型了*/
		byte clientType = yis.readByte(); //当前客户端设置的中高端等级
		
		String mainPath = "";
		if(Constant.CLIENT_OF_HIGH_SIDE == clientType) {
			mainPath = ShareServiceImpl.getInstance().getConfig().getHighPath();
		} 
		//del by zhengl; date: 2011-05-06; note: 暂时不启用下面2个版本的下载逻辑
//		else if (Constant.CLIENT_OF_MIDDLE_SIDE == clientType) {
//			mainPath = ShareServiceImpl.getInstance().getConfig().getMiddlePath();
//		} else if (Constant.CLIENT_OF_LOW_SIDE == clientType) {
//			mainPath = ShareServiceImpl.getInstance().getConfig().getLowPath();
//		}
		path = mainPath + url;
//		switch (fileType) {
//			case FILE_TYPE_PNG:
//				path = path + fileName + ".png";
//				break;
//			case FILE_TYPE_ANU:
//				path = path + fileName + ".anu";
//				break;
//			case FILE_TYPE_MAP:
//				path = path + fileName + ".map";
//				break;
//	
//			default:
//				break;
//		}
		
		byte[] bytes = AllPictureDataDict.getInstance().getFileBytes(path);
		log.info("发送前的fileURL--->" + url);
		if (bytes != null) {
			log.info("发送前的file.length--->" + bytes.length);
	        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
	        		new ResponseDownload(url, bytes));
		} else {
//			OutMsgQ.getInstance().put(player.getMsgQueueIndex(), 
//					new Warning(Tip.TIP_SHARE_DOWNLOAD_NOT_FILE));
			log.warn("没有找到需要下载的资源:" + url);
		}

	}

}
