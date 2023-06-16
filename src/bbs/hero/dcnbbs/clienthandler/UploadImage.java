package hero.dcnbbs.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.dcnbbs.service.DCNService;
import hero.dcnbbs.service.Result;
import hero.dcnbbs.service.ZipUtil;
import hero.player.HeroPlayer;
import hero.player.service.PlayerDAO;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;

import java.util.Base64;

public class UploadImage extends AbsClientProcess {

	@Override
	public void read() throws Exception {
		HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);
		String imgtype = yis.readUTF();
		int bytesize = yis.readInt();
		byte [] bytes = new byte[bytesize];
		yis.readFully(bytes, 0, bytesize);
		if(player.getLoginInfo().username == null) {
			PlayerDAO.loadPlayerAccountInfo(player);
		}
		String mid = "";
		String djtk = (player.getDcndjtk() != null && player.getDcndjtk().length() > 0) ? player.getDcndjtk() : "";
		if(("".equals(djtk) || "".equals(mid)) && player.getLoginInfo().username.indexOf(DCNService.CHANNEL_ID + "#") > 0) {
			Result result = DCNService.login(player.getLoginInfo().username.replaceFirst(DCNService.CHANNEL_ID + "#", ""), "", player.getLoginInfo().password);
			mid = result.getReList();
			djtk = result.getDjtk();
			player.setDcndjtk(djtk);
		} else {
			Result result = DCNService.sys(player.getLoginInfo().accountID + "", player.getName(), player.getLoginInfo().password);
			if(result.isResult()) {
				mid = result.getReList();
				djtk = result.getDjtk();
				player.setDcndjtk(djtk);
			}
		}
		
		if("".equals(djtk) || "".equals(mid)) {
			ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("操作失败，你无权限上传图片！"));
			return;
		}
		if(imgtype != null && imgtype.length() > 0 && bytesize > 0) {
			byte [] b = ZipUtil.decompress(bytes);
//			File fileOut=new File("d://" + player.getUserID() + System.currentTimeMillis() + "." + imgtype);
//		    FileOutputStream out=new FileOutputStream(fileOut);
//		    out.write(b,0,b.length);
//		    out.close();
//			OutMsgQ.getInstance().put(player.getMsgQueueIndex(),new Warning("截图上传成功！"));
//			return;
			Result result = DCNService.uploadImg(mid, djtk, "", player.getUserID() + System.currentTimeMillis() + "", Base64.getDecoder().decode(b).toString(), "", imgtype);
			if(result.isResult()) {
				Result result2 = DCNService.newTopic("", djtk, "", "我的游戏截图", "我的游戏截图", "", player.getName(), result.getReList(), "我的游戏截图");
				if(result2.isResult()) {
					ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("上传图片成功已为你生成图片展示贴！"));
				} else {
					ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("上传截图成功，生成展示贴失败，请联系GM处理！"));
				}
				return;
			} else {
				ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("上传截图失败，请重试！"));
				player.setDcndjtk("");
				return;
			}
		} else {
			ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("操作失败，服务器接收数据错误！"));
			return;
		}
	}
}
