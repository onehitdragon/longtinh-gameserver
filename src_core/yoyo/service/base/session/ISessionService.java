package yoyo.service.base.session;

import yoyo.service.base.IService;

public interface ISessionService extends IService
{
    public int createSession (int userID, int accountID);
    public void initSession (Session session);
    public int getIndexByUserID (int userID);
    public int getIndexBySessionID (int sessionID);
    public void freeSession (Session _session);
    public void freeSessionByAccountID (int accountID);
}
