package hero.player.clienthandler;

import java.util.List;

import hero.micro.store.PersionalStore;
import hero.micro.store.StoreService;
import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.service.base.session.SessionServiceImpl;

import hero.pet.Pet;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.service.ShareServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ExitGame.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-4-2 下午09:51:11
 * @描述 ：
 */

public class ExitGame extends AbsClientProcess {
	private static Logger log = Logger.getLogger(ExitGame.class);

	public void read() throws Exception {
		// TODO Auto-generated method stub
		HeroPlayer player = (HeroPlayer) PlayerServiceImpl.getInstance().getPlayerBySessionID(
				contextData.sessionID);

		if (null != player) {
			log.debug("玩家正常下线... player where = " + player.where().getID());
			player.getLoginInfo().logoutCause = "正常下线";

			List<Pet> petlist = PetServiceImpl.getInstance().getPetList(player.getUserID());
			if (petlist != null && petlist.size() > 0) {
				for (Pet pet : petlist) {
					PetServiceImpl.getInstance().updatePet(player.getUserID(), pet);

				}
			}

			// PlayerServiceImpl.getInstance().dbUpdate(player);

			PersionalStore store = StoreService.get(player.getUserID());
			if (null != store && (store.opened || player.isSelling())) {
				log.debug("退出游戏，摆摊状态 = " + store.opened + ", player storestatus = "
						+ player.isSelling());
				StoreService.takeOffAll(player);
				StoreService.clear(player.getUserID()); // 退出游戏时，清除玩家之前的开店状态
			}

			PlayerServiceImpl.getInstance().getPlayerList().remove(player);
			SessionServiceImpl.getInstance().fireSessionFree(player.getSessionID());
            //add by zhengl; date: 2011-02-15; note:
            PlayerServiceImpl.getInstance().getSessionPlayerList().remove(player.getSessionID());
            PlayerServiceImpl.getInstance().getUserIDPlayerList().remove(player.getUserID());
            
            ShareServiceImpl.getInstance().removePlayerFromRequestExchangeList(player.getUserID());
            //end
			log.info(player.getName() + ":正常退出。");

            //释放对象资源
            player.free();
            player = null;
		}
	}
}
