package yoyo.service.base.player;

import yoyo.service.base.IService;

public interface IPlayerService extends IService
{
    public IPlayer getPlayerByUserID (int userID);
    public IPlayer getPlayerBySessionID (int sessionID);
    public IPlayer getPlayerByName (String name);
    public byte[] listRole (int[] userIDList);
    public byte[] createRole (int accountID, short serverID, int userID,String[] roleInfo);
    public int deleteRole (int userID);
    IPlayerDAO getDAO ();
}
