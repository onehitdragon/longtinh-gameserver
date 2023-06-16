package hero.dcnbbs.service;

import yoyo.service.base.AbsServiceAdaptor;
import yoyo.service.base.session.Session;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

public class BbsServiceImpl extends AbsServiceAdaptor<BbsConfig> {
	
	private static BbsServiceImpl instance;
    /**
     * 获取单例
     * 
     * @return
     */
    public static BbsServiceImpl getInstance () {
        if (instance == null)
            instance = new BbsServiceImpl();
        return instance;
    } 
	
	@Override
	public void dbUpdate(int _userID) {
	}

	@Override
	public void createSession(Session _session) {
		HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(
				_session.userID);

		if (null != player) {
		}
	}

	@Override
	public void sessionFree(Session _session) {
	}

	public void clean(int _userID) {
	}

	@Override
	protected void start() {
	}

}
