package hero.chat.service;

import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

import java.util.Timer;
import java.util.TimerTask;

import javolution.util.FastList;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 WorldHornService.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-10-29 下午04:22:39
 * @描述 ：
 */

public class WorldHornService extends TimerTask
{
    /**
     * 号角列表
     */
    private FastList<HornContent>   list;

    /**
     * 计时器
     */
    private Timer                   timer;

    /**
     * 单例
     */
    private static WorldHornService instance;

    /**
     * 私有构造
     */
    private WorldHornService()
    {
        list = new FastList<HornContent>();
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static WorldHornService getInstance ()
    {
        if (null == instance)
        {
            instance = new WorldHornService();
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
            timer.schedule(this, START_DELAY_TIME, EXCUTE_INTERVAL);
        }
    }

    /**
     * 向容器中放入世界聊天内容
     * 
     * @param _speakerName
     * @param _content
     */
    public void put (String _speakerName, String _content, int _type)
    {
        synchronized (list)
        {
            list.add(new HornContent(_speakerName, _content, _type));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.TimerTask#run()
     */
    public void run ()
    {
        synchronized (list)
        {
            HornContent horn;
            long now = System.currentTimeMillis();

            for (int i = 0; i < list.size();)
            {
                horn = list.get(i);

                if (horn.lastTimesSendTime == 0
                        || now - horn.lastTimesSendTime >= SEND_INTERVAL)
                {
                    HeroPlayer speaker = PlayerServiceImpl.getInstance().getPlayerByName(horn.speakerName);
                    
                    if (speaker != null && speaker.isEnable())
                    {
                    	if(horn.type == 1)
                    		ChatServiceImpl.getInstance().sendWorldPlayer(speaker,horn.content);
                    	else if(horn.type == 2)
                    		ChatServiceImpl.getInstance().sendWorldPlayerUseMassHorn(speaker, horn.content);

                        horn.whichTimes++;
                        horn.lastTimesSendTime = now;

                        if (horn.whichTimes == MAX_TIMES)
                        {
                            list.remove(i);
                            continue;
                        }
                    }
                    else
                    {
                        list.remove(i);
                        continue;
                    }
                }

                i++;
            }
        }
    }

    /**
     * @author DC 号角内容
     */
    public class HornContent
    {
        /**
         * 构造
         * 
         * @param _name
         * @param _content
         */
        public HornContent(String _speakerName, String _content, int _type)
        {
            speakerName = _speakerName;
            content = _content;
            type = _type;
        }

        /**
         * 上次发送时间
         */
        public long   lastTimesSendTime;

        /**
         * 第几次
         */
        public int    whichTimes;

        /**
         * 说话者名称
         */
        public String speakerName;

        /**
         * 说话内容
         */
        public String content;
        /**
         * 号角类型
         * 1:普通世界号角     2:集结号角
         */
        public int type;
    }

    /**
     * 启动延时
     */
    private static final long START_DELAY_TIME = 30 * 1000;

    /**
     * 执行间隔时间
     */
    private static final long EXCUTE_INTERVAL  = 1000;

    /**
     * 发送间隔时间
     */
    private static final long SEND_INTERVAL    = 5 * 1000;

    /**
     * 说话的最大次数
     */
    private static final byte MAX_TIMES        = 3;
}
