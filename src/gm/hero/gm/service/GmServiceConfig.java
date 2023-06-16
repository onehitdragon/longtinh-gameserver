package hero.gm.service;

import org.dom4j.Element;

import yoyo.service.base.AbsConfig;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GMServiceConfig.java
 * @创建者 ChenYaMeng
 * @版本 1.0
 * @时间 2010-6-1 15:18:44
 * @描述 ：GM服务接口配置
 */
public class GmServiceConfig extends AbsConfig
{
    /**
     * 监听端口
     */
    private int port;
    
    /**
     * GM账号数据库URL
     */
    private String accountDBurl;
    
    /**
     * GM账号数据库名
     */
    private String accountDBname;
    
    /**
     * GM账号数据库用户名
     */
    private String accountDBusername;
    
    /**
     * GM账号数据库密码
     */
    private String accountDBpassword;    
    
    /**
     * 问题提交数据库URL
     */
    private String commitDBurl;
    
    /**
     * 问题提交数据库名
     */
    private String commitDBname;
    
    /**
     * 问题提交数据库用户名
     */
    private String commitDBusername;
    
    /**
     * 问题提交数据库密码
     */
    private String commitDBpassword;
    /**
     * 本服务器分区ID
     */
    private int serverID;
    /**
     * 向GM添加聊天内容的URL
     */
    private String addChatContentURL;
    /**
     * 游戏ID
     */
    private int gameID;

    @Override
    public void init (Element _root) throws Exception
    {
        try
        {
            Element eRoot = _root.element("config");
            
            port = Integer.parseInt(eRoot.elementTextTrim("port"));
            
            serverID = Integer.parseInt(eRoot.elementTextTrim("serverID"));
            gameID = Integer.parseInt(eRoot.elementTextTrim("gameID"));
            addChatContentURL = eRoot.elementTextTrim("add_chat_content_to_gm_url");
    
            accountDBurl = eRoot.elementTextTrim("gm_db_url");
            accountDBname = eRoot.elementTextTrim("gm_db_name");
            accountDBusername = eRoot.elementTextTrim("gm_name");
            accountDBpassword = eRoot.elementTextTrim("gm_password");
    
//            commitDBurl = eRoot.elementTextTrim("commit_db_url");
//            commitDBname = eRoot.elementTextTrim("commit_db_name");
//            commitDBusername = eRoot.elementTextTrim("commit_name");
//            commitDBpassword = eRoot.elementTextTrim("commit_password");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 获取监听端口
     * @return
     */
    public int getPort()
    {
        return port;
    }
    
    /**
     * 获取GM账号数据库URL
     * @return
     */
    public String getAccountDBurl()
    {
        return accountDBurl;
    }
    
    /**
     * 获取GM账号数据库名
     * @return
     */
    public String getAccountDBname()
    {
        return accountDBname;
    }
    
    /**
     * 获取GM账号数据库用户名
     * @return
     */
    public String getAccountDBusername()
    {
        return accountDBusername;
    }
    
    /**
     * 获取GM账号数据库密码
     * @return
     */
    public String getAccountDBpassword()
    {
        return accountDBpassword;
    }
    
    /**
     * 获取问题提交数据库URL
     * @return
     */
    public String getCommitDBurl()
    {
        return commitDBurl;
    }
    
    /**
     * 获取问题提交数据库名
     * @return
     */
    public String getCommitDBname()
    {
        return commitDBname;
    }
    
    /**
     * 获取问题提交数据库用户名
     * @return
     */
    public String getCommitDBusername()
    {
        return commitDBusername;
    }
    
    /**
     * 获取问题提交数据库密码
     * @return
     */
    public String getCommitDBpassword()
    {
        return commitDBpassword;
    }

	public int getServerID ()
	{
		return serverID;
	}

	public void setServerID (int serverID)
	{
		this.serverID = serverID;
	}

	public String getAddChatContentURL ()
	{
		return addChatContentURL;
	}

	public void setAddChatContentURL (String addChatContentURL)
	{
		this.addChatContentURL = addChatContentURL;
	}
	
	public int getGameID(){
		return gameID;
	}
    
}
