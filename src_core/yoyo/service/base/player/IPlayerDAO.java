package yoyo.service.base.player;

public interface IPlayerDAO
{
    public IPlayer load (int key);
    public byte[] listRole (int[] userIDs);
    public byte[] createRole (int accountID, short serverID, int userID,String[] paras);
    public int deleteRole (int userId);
    public void updateDB (IPlayer player);
}
