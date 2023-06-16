package hero.charge.service;

import hero.charge.ChargeInfo;
import hero.expressions.service.CEService;
import hero.item.special.HuntExperienceBook;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.LogWriter;
import hero.share.service.Tip;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import yoyo.core.queue.ResponseMessageQueue;


import javolution.util.FastList;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ExperienceBookService.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-5 下午05:29:14
 * @描述 ：经验书服务，包括在线经验书计算任务和离线经验书服务接口
 */

public class ExperienceBookService
{
    /**
     * 计费相关信息列表
     */
    private FastList<ChargeInfo>         timeChargeInfoList;

    /**
     * 计时器
     */
    private Timer                        timer;

    /**
     * 单例
     */
    private static ExperienceBookService instance;

    /**
     * 私有构造
     */
    private ExperienceBookService()
    {
        timeChargeInfoList = new FastList<ChargeInfo>();
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static ExperienceBookService getInstance ()
    {
        if (null == instance)
        {
            instance = new ExperienceBookService();
        }

        return instance;
    }

    /**
     * 启动
     */
    public void start ()
    {
        if (null == timer)
        {
            timer = new Timer();
            timer.schedule(new ExpBookOnlineCalTask(), START_RELAY,
                    CALCUL_INTERVAL);
        }
    }

    /**
     * 放入计费相关信息
     * 
     * @param _chargeInfo
     */
    public void put (ChargeInfo _chargeInfo)
    {
        synchronized (timeChargeInfoList)
        {
            for (ChargeInfo chargeInfo : timeChargeInfoList)
            {
                if (chargeInfo.userID == _chargeInfo.userID)
                {
                    chargeInfo.expBookTimeTotal = _chargeInfo.expBookTimeTotal;

                    return;
                }
            }

            if (_chargeInfo.huntBookTimeTotal != 0)
            {
                timeChargeInfoList.add(_chargeInfo);
            }
        }
    }

    /**
     * 增加在线经验书时间
     * 
     * @param _player
     * @param _time
     */
    public void addExpBookTime (HeroPlayer _player, long _time)
    {
        _player.getChargeInfo().expBookTimeTotal += _time;

        ChargeDAO.updateExpBookTimeInfo(_player.getChargeInfo());

        synchronized (timeChargeInfoList)
        {
            for (ChargeInfo chargeInfo : timeChargeInfoList)
            {
                if (chargeInfo.userID == _player.getUserID())
                {
                    return;
                }
            }

            timeChargeInfoList.add(_player.getChargeInfo());
        }
    }

    /**
     * 增加狩猎经验书时间
     * 
     * @param _player
     * @param _time
     */
    public void addHuntExpBookTime (HeroPlayer _player, long _time)
    {
        if (_player.getChargeInfo().huntBookTimeTotal == 0)
        {
            _player.changeExperienceModulus(HuntExperienceBook.EXP_MODULUS);
        }

        _player.getChargeInfo().huntBookTimeTotal += _time;

        ChargeDAO.updateHuntExpBookTimeInfo(_player.getChargeInfo());

        synchronized (timeChargeInfoList)
        {
            for (ChargeInfo chargeInfo : timeChargeInfoList)
            {
                if (chargeInfo.userID == _player.getUserID())
                {
                    return;
                }
            }

            timeChargeInfoList.add(_player.getChargeInfo());
        }
    }

    /**
     * 异常计费相关信息
     * 
     * @param _userID
     */
    public ChargeInfo remove (int _userID)
    {
        synchronized (timeChargeInfoList)
        {
            for (int i = 0; i < timeChargeInfoList.size(); i++)
            {
                if (timeChargeInfoList.get(i).userID == _userID)
                {
                    return timeChargeInfoList.remove(i);
                }
            }
        }

        return null;
    }

    /**
     * @author DC
     */
    class ExpBookOnlineCalTask extends TimerTask
    {
        /*
         * (non-Javadoc)
         * 
         * @see java.util.TimerTask#run()
         */
        public void run ()
        {
        	try 
        	{
                synchronized (timeChargeInfoList)
                {
                    HeroPlayer player;
                    ChargeInfo chargeInfo;
                    boolean huntExpBookOverdue = false;

                    for (int i = 0; i < timeChargeInfoList.size();)
                    {
                    	//add by zhengl; date: 2011-05-19; note: 每次重置为false以免影响到其他玩家
                    	huntExpBookOverdue = false;
                        chargeInfo = timeChargeInfoList.get(i);

                        player = PlayerServiceImpl.getInstance().getPlayerByUserID(chargeInfo.userID);

                        if (player == null || !player.isEnable())
                        {
                            timeChargeInfoList.remove(i);
                        }
                        else
                        {
                            if (chargeInfo.huntBookTimeTotal > 0)
                            {
                                chargeInfo.huntBookTimeTotal -= CALCUL_INTERVAL;

                                if (chargeInfo.huntBookTimeTotal <= 0)
                                {
                                    chargeInfo.huntBookTimeTotal = 0;
                                    player.changeExperienceModulus(-HuntExperienceBook.EXP_MODULUS);
                                    huntExpBookOverdue = true;
                                    
                                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
                                    		new Warning(Tip.TIP_CHARGE_EXP_BOOK_TIMEOVER, 
                                    				Warning.UI_TOOLTIP_TIP));
                                }
                            }

                            if (huntExpBookOverdue)
                            {
                                timeChargeInfoList.remove(i);
                                ChargeDAO.clearExpBookInfo(chargeInfo.userID);
                            }
                            else
                            {
                                i++;
                                ChargeDAO.updateExpBookInfo(chargeInfo);
                            }
                        }
                    }
                }
			} 
        	catch (Exception e) {
				LogWriter.error("经验书线程异常", e);
			}
        }
    }

    /**
     * 启动延时
     */
    private static final long START_RELAY     = 7 * 1000;

    /**
     * 每次添加经验时间（分钟）
     */
    private static final byte CALCUL_TIME     = 2;

    /**
     * 计算间隔
     */
    private static final long CALCUL_INTERVAL = 59000;
}
