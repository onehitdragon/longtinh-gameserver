package hero.login.rmi;

import hero.chat.service.ChatServiceImpl;
import hero.gm.service.GmServiceImpl;
import hero.item.Goods;
import hero.item.dictionary.GoodsContents;
import hero.login.LoginServiceImpl;
import hero.map.MapModelData;
import hero.map.service.MapModelDataDict;
import hero.player.HeroPlayer;
import hero.player.LoginInfo;
import hero.player.service.PlayerServiceImpl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import javolution.util.FastList;


public class YOYOLoginRMIImpl implements
        IYOYOLoginRMI
{
    public YOYOLoginRMIImpl() throws RemoteException
    {
        super();
    }

    public byte[] listRole (int[] _userIDs) throws RemoteException
    {
        return LoginServiceImpl.getInstance().listRole(_userIDs);
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.server.login.rmi.LSIRMI#createRole(int, short,
     *      java.lang.String[])
     */
    public byte[] createRole (int accountID, short serverID, int userID,
            String[] paras) throws RemoteException
    {
        byte[] result = null;

        try
        {
            result = LoginServiceImpl.getInstance().createRole(accountID,
                    serverID, userID, paras);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.server.login.rmi.LSIRMI#deleteRole(int)
     */
    public int deleteRole (int userId) throws RemoteException
    {
        return LoginServiceImpl.getInstance().deleteRole(userId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.server.login.rmi.LSIRMI#resetPlayersStatus(int[])
     */
    public boolean resetPlayersStatus (int _accountID) throws RemoteException
    {
        return LoginServiceImpl.getInstance().resetPlayersStatus(_accountID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.server.login.rmi.LSIRMI#checkStatusOfRun()
     */
    public boolean checkStatusOfRun () throws RemoteException
    {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.sv.login.rmi.ME2ILoginRMI#createObjectID(int)
     */
    public int createSessionID (int _userID, int _accountID)
            throws RemoteException
    {
        // TODO Auto-generated method stub
        return LoginServiceImpl.getInstance().login(_userID, _accountID);
    }

    @Override
    public String getPlayerInfoByUserID(int _userID) throws RemoteException {
    	int online = 1;
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_userID);
        if(player == null){
        	online = 0;
            player = PlayerServiceImpl.getInstance().getOffLinePlayerInfo(_userID);
        }
        if(null == player){
            return "-1";
        }
        StringBuffer sb = new StringBuffer();
        String phone = "";
        String publisher = "";
        LoginInfo lInfo = player.getLoginInfo();
        if(lInfo != null) {
        	phone = lInfo.loginMsisdn + "";
        	publisher = lInfo.publisher + "";
        }
        sb.append(player.getUserID()).append(",")
            .append(player.getName()).append(",")
            .append(player.getLoginInfo().accountID).append(",")
            .append(player.getSex().getDesc()).append(",")
            .append(player.getVocation().getDesc()).append(",")
            .append(player.getClan().getDesc()).append(",")
            .append(player.getLevel()).append(",")
            .append(player.getExp()).append(",")
            .append(player.getMoney()).append(",")
            .append(online).append(",")
            .append(player.loginTime).append(",")
            .append(player.lastLogoutTime).append(",")
            .append(player.where() != null ? player.where().getName() : "").append(",")
            .append(phone).append(",")
            .append(publisher);
        return sb.toString();
    }

    @Override
    public String getPlayerInfoByNickname(String nickname) throws RemoteException {
    	int online = 1;
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByName(nickname);
        if(null == player){
        	online = 0;
            player = PlayerServiceImpl.getInstance().getOffLinePlayerInfoByName(nickname);
        }
        if(null == player){
            return "-1";
        }
        StringBuffer sb = new StringBuffer();
        String phone = "";
        String publisher = "";
        LoginInfo lInfo = player.getLoginInfo();
        if(lInfo != null) {
        	phone = lInfo.loginMsisdn + "";
        	publisher = lInfo.publisher + "";
        }
        sb.append(player.getUserID()).append(",")
            .append(player.getName()).append(",")
            .append(player.getLoginInfo().accountID).append(",")
            .append(player.getSex().getDesc()).append(",")
            .append(player.getVocation().getDesc()).append(",")
            .append(player.getClan().getDesc()).append(",")
            .append(player.getLevel()).append(",")
            .append(player.getExp()).append(",")
            .append(player.getMoney()).append(",")
            .append(online).append(",")
            .append(player.loginTime).append(",")
            .append(player.lastLogoutTime).append(",")
            .append(player.where() != null ? player.where().getName() : "").append(",")
            .append(phone).append(",")
            .append(publisher);
        return sb.toString();
    }

    /*
    @Override
    public boolean setPlayerUserIDBlack(int _userID, int keepTime, String startTime, String endTime, String memo) throws RemoteException {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_userID);
        if(null == player){
            player = PlayerServiceImpl.getInstance().getOffLinePlayerInfo(_userID);
        }
        if(null == player){
            return false;
        }
        return PlayerServiceImpl.getInstance().setPlayerUserIDBlack(_userID,player.getName(),keepTime,startTime,endTime,memo);
    }

    @Override
    public boolean setPlayerAccountIDBlack(int _accountID, int keepTime, String startTime, String endTime, String memo) throws RemoteException {
        String username = PlayerDAO.getUsernameByAccountID(_accountID);
        return PlayerServiceImpl.getInstance().setPlayerAccountIDBlack(_accountID,username,keepTime,startTime,endTime,memo);
    }

    @Override
    public boolean setPlayerChatBlack(int _userID, int keepTime, String startTime, String endTime, String memo) throws RemoteException {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_userID);
        if(null == player){
            player = PlayerServiceImpl.getInstance().getOffLinePlayerInfo(_userID);
        }
        if(null == player){
            return false;
        }
        return PlayerServiceImpl.getInstance().setPlayerChatBlack(_userID,player.getName(),keepTime,startTime,endTime,memo);
    }

    @Override
    public boolean deletePlayerUserIDBlack(int _userID) throws RemoteException {
        return PlayerServiceImpl.getInstance().deletePlayerUserIDBlack(_userID);
    }

    @Override
    public boolean deletePlayerAccountIDBlack(int _accountID) throws RemoteException {
        return PlayerServiceImpl.getInstance().deletePlayerAccountIDBlack(_accountID);
    }

    @Override
    public boolean deletePlayerChatBlack(int _userID) throws RemoteException {
        return PlayerServiceImpl.getInstance().deletePlayerChatBlack(_userID);
    }

    @Override
    public boolean setPlayerUserIDBlack(String nickname, int keepTime, String startTime, String endTime, String memo) throws RemoteException {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByName(nickname);
        if(null == player){
            player = PlayerServiceImpl.getInstance().getOffLinePlayerInfoByName(nickname);
        }
        if(null == player){
            return false;
        }
        return PlayerServiceImpl.getInstance().setPlayerUserIDBlack(player.getUserID(),nickname,keepTime,startTime,endTime,memo);
    }

    @Override
    public boolean setPlayerAccountIDBlack(String username, int keepTime, String startTime, String endTime, String memo) throws RemoteException {
        int _accountID = GmDAO.getAccountIDByUserName(username);
        return PlayerServiceImpl.getInstance().setPlayerAccountIDBlack(_accountID,username,keepTime,startTime,endTime,memo);
    }

    @Override
    public boolean setPlayerChatBlack(String nickname, int keepTime, String startTime, String endTime, String memo) throws RemoteException {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByName(nickname);
        if(null == player){
            player = PlayerServiceImpl.getInstance().getOffLinePlayerInfoByName(nickname);
        }
        if(null == player){
            return false;
        }
        return PlayerServiceImpl.getInstance().setPlayerChatBlack(player.getUserID(),player.getName(),keepTime,startTime,endTime,memo);
    }

    @Override
    public boolean deletePlayerUserIDBlack(String nickname) throws RemoteException {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByName(nickname);
        if(null == player){
            player = PlayerServiceImpl.getInstance().getOffLinePlayerInfoByName(nickname);
        }
        if(null == player){
            return false;
        }
        return PlayerServiceImpl.getInstance().deletePlayerUserIDBlack(player.getUserID());
    }

    @Override
    public boolean deletePlayerAccountIDBlack(String username) throws RemoteException {
        int _accountID = GmDAO.getAccountIDByUserName(username);
        return PlayerServiceImpl.getInstance().deletePlayerAccountIDBlack(_accountID);
    }

    @Override
    public boolean deletePlayerChatBlack(String nickname) throws RemoteException {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByName(nickname);
        if(null == player){
            player = PlayerServiceImpl.getInstance().getOffLinePlayerInfoByName(nickname);
        }
        if(null == player){
            return false;
        }
        return PlayerServiceImpl.getInstance().deletePlayerChatBlack(player.getUserID());
    }*/

    /*
    * (non-Javadoc)
    *
    * @see hero.login.rmi.ME2ILoginRMI#getOnlineNumber()
    */
    public int getOnlinePlayerNumber () throws RemoteException
    {
        return LoginServiceImpl.getInstance().getOnlinePlayerNumber();
    }


	public byte[] listDefaultRole() throws RemoteException {
		// TODO Auto-generated method stub
		return LoginServiceImpl.getInstance().listDefaultRole();
	}

	@Override
	public String getGameMapList () throws RemoteException
	{
		ArrayList<MapModelData> mapList = MapModelDataDict.getInstance().getMapModelDataList();
		StringBuffer sb = new StringBuffer();
		for(MapModelData map : mapList){
			sb.append(map.id).append("-").append(map.name).append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}

	@Override
	public void sendNoticeGM (String gmName,String content) throws RemoteException
	{
		ChatServiceImpl.getInstance().sendNoticeGM(gmName, content);
	}
	
	@Override
	public void GMReplyLetter (int gmLetterID) throws RemoteException
	{
		GmServiceImpl.GMReplyLetter(gmLetterID);
		
	}

	@Override
	public int blink (short mapID, int userID) throws RemoteException
	{
		return GmServiceImpl.gmBlinkPlayer(mapID, userID);
	}

    @Override
    public int szfFeeCallBack(int userID,String transID, byte result,String orderID,int point) throws RemoteException {
        return GmServiceImpl.szfFeeCallBack(userID,transID,result,orderID,point);
    }

    @Override
    public int addGoodsForPlayer(int userID, int goodsID, int number) throws RemoteException {
        return PlayerServiceImpl.getInstance().GMAddGoodsForPlayer(userID,goodsID,number);
    }

    @Override
    public int addPointForPlayer(int userID, int point) throws RemoteException {
        return PlayerServiceImpl.getInstance().GMAddPointForPlayer(userID,point);
    }

    @Override
    public int modifyPlayerInfo(int userID, int money, int loverValue, int level,int skillPoint) throws RemoteException {
        return PlayerServiceImpl.getInstance().GMModifyPlayerInfo(userID,money,loverValue,level,skillPoint);
    }

    @Override
    public String getGoodsName(int goodsID) throws RemoteException {
        Goods goods = GoodsContents.getGoods(goodsID);
        if(goods != null){
            return goods.getName();
        }
        return "0";
    }

    @Override
    public void smsCallBack(String transID, String result) throws RemoteException {
        GmServiceImpl.smsCallBack(transID,result);
    }

    @Override
	public void resetPlayers () throws RemoteException {
		FastList<HeroPlayer> playerList = PlayerServiceImpl.getInstance().getPlayerList();
		for(int i = playerList.size() - 1; i >= 0; i--) {
			HeroPlayer hPlayer = playerList.get(i);
			LoginServiceImpl.getInstance().resetPlayersStatus(hPlayer.getLoginInfo().accountID);
		}
	}
}
