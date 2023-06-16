package yoyo.core.packet;

public class ResponseData
{
    private int sessionId;

    private byte[] context;
    
    private boolean isError;

    public ResponseData(byte[] context)
    {
        this.context = context;
        isError = false;
    }

    public boolean isErrorMessage()
    {
    	return isError;
    }

    public void setErrorMessage()
    {
    	isError = true;
    }

    public byte[] getContext ()
    {
        return context;
    }

    public int getSize ()
    {
        if (context == null)
        {
            return 0;
        }
        return context.length + 5;
    }

    public int getSessionId ()
    {
        return sessionId;
    }

    public void setSessionID (int sessionID)
    {
        this.sessionId = sessionID;
    }

    private long recvTime;
    private short key;

	public long getRecvTime() 
	{
		return recvTime;
	}

	public void setRecvTime(long time) 
	{
		this.recvTime = time;
	}

	public short getKey() 
	{
		return key;
	}

	public void setKey(short key) 
	{
		this.key = key;
	}     
}
