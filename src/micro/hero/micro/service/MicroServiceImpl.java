package hero.micro.service;

import hero.micro.store.PersionalStore;
import hero.micro.store.StoreService;
import hero.micro.teach.MasterApprentice;
import hero.micro.teach.TeachService;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;

import yoyo.service.base.AbsServiceAdaptor;
import yoyo.service.base.session.Session;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MicroServiceImpl.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-19 上午10:12:19
 * @描述 ：
 */

public class MicroServiceImpl extends AbsServiceAdaptor<MicroConfig>
{
    private static Logger log = Logger.getLogger(MicroServiceImpl.class);
    /**
     * 单例
     */
    private static MicroServiceImpl instance;

    /**
     * 私有构造
     */
    private MicroServiceImpl()
    {
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static MicroServiceImpl getInstance ()
    {
        if (null == instance)
        {
            instance = new MicroServiceImpl();
        }

        return instance;
    }

    @Override
    public void createSession (Session _session)
    {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(
                _session.userID);

//        StoreService.login(_session.userID);
        StoreService.login(player);
        TeachService.login(player);
    }

    @Override
    public void sessionFree (Session _session)
    {
        TeachService.logout(_session.userID);
    }

    public void clean (int _userID)
    {
        StoreService.clear(_userID);
        TeachService.clear(_userID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.service.basic.ServiceAdaptor#startService()
     */
    public void start ()
    {

    }

    /**
     * 获取商店
     * 
     * @param _userID
     * @return
     */
    public PersionalStore getStore (int _userID)
    {
        return StoreService.get(_userID);
    }

    /**
     * 获取师徒关系
     * 
     * @param _userID
     * @return
     */
    public MasterApprentice getMasterApprentice (int _userID)
    {
        return TeachService.get(_userID);
    }
    
    /**
     * 获得师傅的名字
     * @param _userID
     * @return
     */
    public String getMasterName(HeroPlayer _user) {
    	String name = "";
        MasterApprentice m = null;
        if(_user.isEnable()){
            m = TeachService.get(_user.getUserID());
        }else{ //_user 不在线
            m = TeachService.getOffLineMasterApprentice(_user.getName());
//            log.debug("不在线 get master = " + m.masterName);
        }
        if(m != null && m.masterName != null && !m.masterName.equals(_user.getName())) {
            name = m.masterName;
        }
    	return name;
    }
    
    /**
     * 获得徒弟列表
     * @param _userID
     * @return
     */
    public String getApprenticeNameList(HeroPlayer _user) {
    	String name = "";
    	MasterApprentice m = null;
        if(_user.isEnable()){
            m = TeachService.get(_user.getUserID());
        }else{//_user 不在线
            m = TeachService.getOffLineMasterApprentice(_user.getName());
//            log.debug("不在线 get apprentice = " + m.apprenticeNumber);
        }
    	if(m != null && m.apprenticeList != null && m.apprenticeList.length > 0) {
    		for (int i = 0; i < m.apprenticeList.length; i++) {
    			if(m.apprenticeList[i] == null) {
    				continue;
    			}
    			name += m.apprenticeList[i].name + ", ";
				if(name.equals(_user.getName())) {
					//自己本身就只是该组的徒弟
					name = "";
					break;
				}
			}
    		if(name.length() > 2) {
    			name = name.substring(0, name.length()-2);
    		}
    	}
    	return name;
    }
}
