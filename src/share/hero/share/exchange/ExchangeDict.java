package hero.share.exchange;

import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.ResponseExchange;
import hero.share.service.LogWriter;
import hero.share.service.ShareServiceImpl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

import yoyo.core.queue.ResponseMessageQueue;


/**
 * 交易集合类 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class ExchangeDict
{
    private static ExchangeDict        instance;

    private static int                 MAX_EXCHANGE_ID = Integer.MIN_VALUE;

    private HashMap<Integer, Exchange> exchangeList;

    private ReentrantLock              lock            = new ReentrantLock();

    private ExchangeDict()
    {
        exchangeList = new HashMap<Integer, Exchange>();
    }

    public static ExchangeDict getInstance ()
    {
        if (instance == null)
            instance = new ExchangeDict();
        return instance;
    }

    /**
     * 根据指定的交易ID得到交易对象
     * 
     * @param _exchangeID
     * @return
     */
    public Exchange getExchangeByID (int _exchangeID)
    {
        try
        {
            lock.lock();
            return exchangeList.get(_exchangeID);
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * 根据指定的交易ID移除交易对象
     * 
     * @param _exchangeID
     */
    public void removeExchangeByID (int _exchangeID)
    {
        try
        {
            lock.lock();
            exchangeList.remove(_exchangeID);
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * 添加交易
     * 
     * @param _player1
     * @param _player2
     * @return
     */
    public int addExchange (HeroPlayer _player1, HeroPlayer _player2)
    {
        try
        {
            lock.lock();
            Exchange exchange = new Exchange(getExchangeID(), _player1
                    .getName(), _player2.getName());
            exchange.setRequestExchangeUserID(_player1.getUserID());
            exchangeList.put(exchange.getExchangeID(), exchange);
            return exchange.getExchangeID();
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * 得到一个可用的交易ID
     * 
     * @return
     */
    private int getExchangeID ()
    {
        try
        {
            lock.lock();
            if (MAX_EXCHANGE_ID == Integer.MAX_VALUE)
                MAX_EXCHANGE_ID = Integer.MIN_VALUE;
            return MAX_EXCHANGE_ID++;
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * 玩家同意交易后，开始交易
     * @param player
     * @param other
     */
    public void startExchange(HeroPlayer player, HeroPlayer other){
        int exchangeID = addExchange(player, other);

        ResponseMessageQueue.getInstance().put(
                player.getMsgQueueIndex(),
                new ResponseExchange(exchangeID, other
                        .getName()));
        ResponseMessageQueue.getInstance().put(
                other.getMsgQueueIndex(),
                new ResponseExchange(exchangeID, player
                        .getName()));

        player.swapBegin();
        other.swapBegin();
    }

    /**
     * 有玩家掉线
     * 
     * @param _nickname
     */
    public void playerOutline (String _nickname)
    {
        try
        {
            lock.lock();
            Iterator<Exchange> iter = exchangeList.values().iterator();

            while (iter.hasNext())
            {
                Exchange exchange = iter.next();
                ExchangePlayer me = exchange.getPlayerByNickname(_nickname);

                if (me != null)
                {
                    HeroPlayer other = PlayerServiceImpl
                            .getInstance()
                            .getPlayerByName(
                                    exchange.getTargetByNickname(_nickname).nickname);
                    exchangeCancel(exchange.getExchangeID(), other, null);

                    return;
                }
            }
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * 交易取消
     * 
     * @param _exchangeID
     * @param _player1
     * @param _player2
     */
    public void exchangeCancel (int _exchangeID, HeroPlayer _player1,
            HeroPlayer _player2)
    {
        if (_player1 != null && _player1.isEnable())
        {
            _player1.swapOver();

            ResponseMessageQueue.getInstance().put(_player1.getMsgQueueIndex(),
                    new ResponseExchange(Exchange.EXCHANGE_CANCEL));
        }
        if (_player2 != null && _player2.isEnable())
        {
            _player2.swapOver();

            ResponseMessageQueue.getInstance().put(_player2.getMsgQueueIndex(),
                    new ResponseExchange(Exchange.EXCHANGE_CANCEL));
        }
        Exchange exchange = getExchangeByID(_exchangeID);
        if (exchange != null) 
        {
        	ShareServiceImpl.getInstance().removePlayerFromRequestExchangeList(
        			exchange.getRequestExchangeUserID());
        	ExchangeDict.getInstance().removeExchangeByID(_exchangeID);
		}
        else 
        {
            LogWriter.error("error:通过exchangeID=" + String.valueOf(_exchangeID)+"获得交易对象为null",
            		null);
		}

    }
}
