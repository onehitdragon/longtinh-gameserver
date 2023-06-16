package yoyo.core.packet;

public class ContextData
{
    public byte   connectionKind;
    public int    sessionID;
    public short  clientVersion;
    public short  clientModel;
    public byte   gameID;
    public short  messageID;
    public int    serviceID;
    public int    messageType;
    public byte[] context;

    public ContextData(byte connectionKind, int sessionID, byte gameID,short messageID, byte[] context)
    {
        this.connectionKind = connectionKind;
        this.sessionID = sessionID;
        this.gameID = gameID;
        this.messageID = messageID;
        this.serviceID = (messageID >> 8) & 0xff;
        this.messageType =messageID & 0xff;
        this.context = context;
    }
    
    public long recvTime;
    public short key;
}
