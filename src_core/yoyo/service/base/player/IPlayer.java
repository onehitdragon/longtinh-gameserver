package yoyo.service.base.player;

import yoyo.service.base.session.Session;

public interface IPlayer
{
    public void init ();
    public int getMsgQueueIndex ();
    public void setSession (Session sesion);
    public int getSessionID ();
    public void free ();
}
