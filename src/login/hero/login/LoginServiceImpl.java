package hero.login;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import hero.login.rmi.IYOYOLoginRMI;
import hero.login.rmi.YOYOLoginRMIImpl;
import hero.player.service.PlayerServiceImpl;
import hero.share.service.LogWriter;
import org.apache.log4j.Logger;

import yoyo.service.base.AbsServiceAdaptor;
import yoyo.service.base.session.SessionServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 LoginServiceImpl.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-10-17 下午05:47:58
 * @描述 ：登陆服务，主要提供在进入游戏前的服务入口
 */

public class LoginServiceImpl extends AbsServiceAdaptor<LoginConfig>
{
    private static Logger log = Logger.getLogger(LoginServiceImpl.class);
    /**
     * 单例
     */
    private static LoginServiceImpl instance;
    private IYOYOLoginRMI yoyoLoginRMI;

    /**
     * 私有构造
     */
    private LoginServiceImpl()
    {
        config = new LoginConfig();
        try {
            yoyoLoginRMI = new YOYOLoginRMIImpl();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static LoginServiceImpl getInstance ()
    {
        if (instance == null)
        {
            instance = new LoginServiceImpl();
        }
        return instance;
    }

    @Override
    protected void start ()
    {
        try
        {
            IYOYOLoginRMI yoyoLoginRMIStub = (IYOYOLoginRMI)UnicastRemoteObject.exportObject
            (
                yoyoLoginRMI,
                0
            );
            Registry registry = LocateRegistry.createRegistry(config.rmi_port);
            registry.rebind(config.rmi_object, yoyoLoginRMIStub);
            LogWriter.println("vinh - RMI register ok");
        }
        catch (Exception e)
        {
        	e.printStackTrace();
            LogWriter.error(this, e);
        }
    }

    /**
     * 创建角色
     * 
     * @param _accountID
     * @param _serverID
     * @param _userID
     * @param _paras
     * @return
     */
    public byte[] createRole (int _accountID, short _serverID, int _userID,
            String[] _paras)
    {
        return PlayerServiceImpl.getInstance().createRole(_accountID,
                _serverID, _userID, _paras);
    }
    
    /**
     * 获得默认信息
     * @return
     */
    public byte[] listDefaultRole () {
        return PlayerServiceImpl.getInstance().listDefaultRole();
    }

    /**
     * 获取角色列表
     * 
     * @param _userIDArray 角色编号数组
     * @return 角色列表的视图字节数组
     */
    public byte[] listRole (int[] _userIDArray)
    {
        return PlayerServiceImpl.getInstance().listRole(_userIDArray);
    }

    /**
     * 删除角色
     * 
     * @param _userID 角色编号
     * @return 是否成功的标记（1：成功 0：失败）
     */
    public int deleteRole (int _userID)
    {
        return PlayerServiceImpl.getInstance().deleteRole(_userID);
    }

    /**
     * 将帐号下的角色状态置为离线状态
     * 
     * @param _accountID 帐号编号
     * @return 是否成功
     */
    public boolean resetPlayersStatus (int _accountID)
    {
    	System.out.println("开始强行把账号ID=" + _accountID + "上的在线角色踢下线");
        SessionServiceImpl.getInstance().freeSessionByAccountID(_accountID);

        return true;
    }

    /**
     * 玩家上线
     * 
     * @param _userID 角色编号
     * @param _accountID 帐号编号
     * @return 返回新生成的sessionID
     */
    public int login (int _userID, int _accountID)
    {
    	int session = 0;
    	try {
    		log.info("玩家登陆:" + _userID);
    		log.info("_accountID:" + _accountID);
    		session = SessionServiceImpl.getInstance().createSession(_userID, _accountID);
		} catch (Exception e) {
			log.info("出现严重问题,创建sessionid失败");
			e.printStackTrace();
		}
    	log.info("返回sessionid:" + session);
        return session;
    }

    /**
     * 获取在线人数
     * 
     * @return
     */
    public int getOnlinePlayerNumber ()
    {
        return PlayerServiceImpl.getInstance().getPlayerNumber();
    }
}
