package hero.map.broadcast;

import yoyo.core.packet.AbsResponseMessage;
import hero.map.Map;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SynchronousMessage.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-23 下午03:10:32
 * @描述 ：同步信息包装类
 */

public class SynchronousMessage
{
    /**
     * 客户端类型（高、中、低端）
     */
    public short         clientType;

    /**
     * 同步的地图
     */
    public Map           map;

    /**
     * 消息
     */
    public AbsResponseMessage msg;

    /**
     * 是否需要排除触发者
     */
    public boolean       needExcludeTrigger;

    /**
     * 不需要接受此消息的角色ID
     */
    public int           objectID;

    /**
     * 构造
     * 
     * @param _map 广播信息的地图
     * @param _msg 广播的消息
     * @param _needExcludeTrigger 是否排除消息的触发者（当消息是玩家产生时）
     * @param _objectID 需要排除的触发者角色编号
     */
    public SynchronousMessage(Map _map, AbsResponseMessage _msg,
            boolean _needExcludeTrigger, int _objectID)
    {
        map = _map;
        msg = _msg;
        needExcludeTrigger = _needExcludeTrigger;
        objectID = _objectID;
    }

    /**
     * 构造
     * 
     * @param _clientType 客户端类型（高、中、低端）
     * @param _map 广播信息的地图
     * @param _msg 广播的消息
     * @param _needExcludeTrigger 是否排除消息的触发者（当消息是玩家产生时）
     * @param _objectID 需要排除的触发者角色编号
     */
    public SynchronousMessage(short _clientType, Map _map, AbsResponseMessage _msg,
            boolean _needExcludeTrigger, int _objectID)
    {
        clientType = _clientType;
        map = _map;
        msg = _msg;
        needExcludeTrigger = _needExcludeTrigger;
        objectID = _objectID;
    }
}
