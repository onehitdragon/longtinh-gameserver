package hero.map.broadcast;

import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.queue.ResponseMessageQueue;
import hero.map.Map;
import hero.player.HeroPlayer;
import hero.share.Constant;
import hero.share.service.ME2ObjectList;
import javolution.util.FastList;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MapSynchronousInfoBroadcast.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-23 下午03:09:36
 * @描述 ：地图同步信息广播
 */

public class MapSynchronousInfoBroadcast implements Runnable
{
    /**
     * 需要同步的信息列表
     */
    private FastList<SynchronousMessage>       infoList = new FastList<SynchronousMessage>();

    /**
     * 单例
     */
    private static MapSynchronousInfoBroadcast instance;

    /**
     * 获取单例
     * 
     * @return
     */
    public static MapSynchronousInfoBroadcast getInstance ()
    {
        if (null == instance)
        {
            instance = new MapSynchronousInfoBroadcast();
        }

        return instance;
    }

    /**
     * 放入到队列里
     * 
     * @param _map 广播的地图
     * @param _msg 广播的消息
     * @param _needExcludeTrigger 是否需要过滤消息的触发者
     * @param _playerObjectID 角色对象编号
     */
    public void put (Map _map, AbsResponseMessage _msg, boolean _needExcludeTrigger,
            int _playerObjectID)
    {
        if (_map.getPlayerList().size() > 0)
        {
            synchronized (infoList)
            {
                infoList.add(new SynchronousMessage(_map, _msg,
                        _needExcludeTrigger, _playerObjectID));
            }
        }
    }

    /**
     * 放入到队列里
     * 
     * @param _clientType
     * @param _map 广播的地图
     * @param _msg 广播的消息
     * @param _needExcludeTrigger 是否需要过滤消息的触发者
     * @param _playerObjectID 角色对象编号
     */
    public void put (short _clientType, Map _map, AbsResponseMessage _msg,
            boolean _needExcludeTrigger, int _playerObjectID)
    {
        if (_map.getPlayerList().size() > 0)
        {
            synchronized (infoList)
            {
                infoList.add(new SynchronousMessage(_clientType, _map, _msg,
                        _needExcludeTrigger, _playerObjectID));
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run ()
    {
        // TODO Auto-generated method stub
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {

        }
        while (true)
        {
            try
            {
                broadcast();
            }
            catch (Exception e)
            {

            }
            try
            {
                Thread.sleep(20);
            }
            catch (InterruptedException e)
            {

            }
        }
    }

    /**
     * 广播
     */
    private void broadcast ()
    {
        try
        {
            synchronized (infoList)
            {
                for (SynchronousMessage info : infoList)
                {
                    ME2ObjectList mapPlayerList = info.map.getPlayerList();

                    if (mapPlayerList.size() > 0)
                    {
                        HeroPlayer player = null;

                        for (int i = 0; i < mapPlayerList.size(); i++)
                        {
                            player = (HeroPlayer) mapPlayerList.get(i);

                            if (player.isEnable())
                            {
                                if (info.needExcludeTrigger
                                        && player.getID() == info.objectID)
                                {
                                    continue;
                                }
                                else
                                {
                                    if (0 == info.clientType)
                                    {
                                        ResponseMessageQueue.getInstance().put(
                                                player.getMsgQueueIndex(),
                                                info.msg);
                                    }
                                    else if (Constant.CLIENT_OF_HIGH_SIDE == info.clientType)
                                    {
                                        if (player.getLoginInfo().clientType == Constant.CLIENT_OF_HIGH_SIDE)
                                        {
                                            ResponseMessageQueue.getInstance().put(
                                                    player.getMsgQueueIndex(),
                                                    info.msg);
                                        }
                                    }
                                    else
                                    {
                                        if (player.getLoginInfo().clientType != Constant.CLIENT_OF_HIGH_SIDE)
                                        {
                                            ResponseMessageQueue.getInstance().put(
                                                    player.getMsgQueueIndex(),
                                                    info.msg);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                infoList.clear();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
