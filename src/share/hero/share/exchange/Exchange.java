package hero.share.exchange;

/**
 * 交易对象类 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class Exchange
{
    public static final byte BEGIN      = 1;

    public static final byte ADD_MONEY      = 2;

    public static final byte LIST_INVENTORY_GOODS = 3;

    public static final byte ADD_GOODS  = 4;

    public static final byte CONFIM     = 5;

    public static final byte EXCHANGE_CANCEL     = 6;

    public static final byte EXCHANGE_FINISH     = 7;

    public static final byte EXCHANGE_BUSY       = 8;

    /**
     * 锁定交易
     */
    public static final byte EXCHANGE_LOCK  = 9;

    /**
     * 撤消全部物品
     */
    public static final byte REMOVE_GOODS = 10;

    /**
     * 撤消单个上架物品
     */
    public static final byte REMOVE_SINGLE_GOODS = 11;


    private int              exchangeID;

    private ExchangePlayer   player1;

    private ExchangePlayer   player2;

    public final static byte NORMAL              = 0;

    public final static byte READY               = 1;
    /**
     * 发起交易的玩家ID
     */
    private int requestExchangeUserID;

    protected Exchange(int _exchangeID, String _player1Nickname,
            String _player2Nickname)
    {
        exchangeID = _exchangeID;
        player1 = new ExchangePlayer(_player1Nickname);
        player2 = new ExchangePlayer(_player2Nickname);
    }

    public int getExchangeID ()
    {
        return exchangeID;
    }

    /**
     * 通过昵称得到此交易玩家对象
     * 
     * @param _nickname
     * @return
     */
    public ExchangePlayer getPlayerByNickname (String _nickname)
    {
        if (player1.nickname.equals(_nickname))
            return player1;
        else if (player2.nickname.equals(_nickname))
            return player2;

        return null;
    }

    /**
     * 得到交易对面的玩家
     * 
     * @param _nickname
     * @return
     */
    public ExchangePlayer getTargetByNickname (String _nickname)
    {
        if (player1.nickname.equals(_nickname))
            return player2;
        else if (player2.nickname.equals(_nickname))
            return player1;

        return null;
    }
    /**
     * 设置发起请求交易的玩家ID
     * @return
     */
	public int getRequestExchangeUserID ()
	{
		return requestExchangeUserID;
	}
	/**
	 * 获取发起请求交易的玩家ID
	 * @param requestExchangeUserID
	 */
	public void setRequestExchangeUserID (int requestExchangeUserID)
	{
		this.requestExchangeUserID = requestExchangeUserID;
	}
    
    
}
