package hero.npc;

import hero.map.Map;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.map.message.DisappearNotify;
import hero.npc.ai.NPCFollowAI;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.BaseNpcFunction;
import hero.npc.message.NpcInteractiveResponse;
import hero.npc.message.NpcRefreshNotify;
import hero.npc.service.NotPlayerServiceImpl;
import hero.player.HeroPlayer;
import hero.share.Constant;
import hero.share.Direction;
import hero.share.EObjectType;
import hero.share.ME2GameObject;
import hero.share.MoveSpeed;
import hero.share.service.LogWriter;
import hero.share.service.ThreadPoolFactory;
import hero.ui.UI_NpcHandshake;

import java.util.ArrayList;

import javolution.util.FastList;
import org.apache.log4j.Logger;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ME2NPC.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-10 下午04:38:34
 * @描述 ：
 */

public class Npc extends ME2NotPlayer
{
    private static Logger log = Logger.getLogger(Npc.class);
    /**
     * 没有任何任务
     */
    public static final byte          NOT_MARK = 0;
    /**
     * 有可接的任务头顶标记
     */
    public static final byte          RECEIVE_TASK_MARK = 1;

    /**
     * 有可交的任务头顶标记
     */
    public static final byte          SUBMIT_TASK_MARK  = 2;
    /**
     * 未满足条件的任务
     */
    public static final byte          RECEIVE_TASK_NOT_MARK = 4;
    /**
     * 未满足交纳任务条件的已接任务
     */
    public static final byte          SUBMIT_TASK_NOT_MARK = 3;

    /**
     * 交互问候语
     */
    private String                    hello;

    /**
     * 呐喊内容
     */
    private String                    screamContent;

    /**
     * 称谓
     */
    private String                    title;
    /**
     * 功能类型
     */
    private byte                      functionType;

    /**
     * 功能列表
     */
    private FastList<BaseNpcFunction> functionList;

    /**
     * NPC图片类型（1：单帧，2：多帧, 3=可移动的NPC）
     */
    private byte                      imageType;

    /**
     * 可交互
     */
    private boolean                   canInteract       = true;

    /**
     * 跟随AI
     */
    private NPCFollowAI               followAi;
    
    /**
     * 动画id
     */
    private short animationID; 

    /**
     * 构造
     */
    public Npc()
    {
        objectType = EObjectType.NPC;
        functionList = new FastList<BaseNpcFunction>();
    }

    /**
     * 构造
     * 
     * @param _hello
     */
    public Npc(String _hello)
    {
        this();
        hello = _hello;
        setMoveSpeed(MoveSpeed.FASTER);
    }

    /**
     * 获取与NPC第一步交互时所说的话
     * 
     * @return
     */
    public String getHello ()
    {
        return hello;
    }

    /**
     * 设置呐喊内容
     * 
     * @return
     */
    public void setScreamContent (String _screamHello)
    {
        screamContent = _screamHello;
    }

    /**
     * 获取呐喊内容
     * 
     * @return
     */
    public String getScreamContent ()
    {
        return screamContent;
    }

    /**
     * 设置称谓
     * 
     * @param _title
     */
    public void setTitle (String _title)
    {
        title = _title;
    }

    /**
     * 设置NPC功能的类型(1=可选中,可交互类型; 2=不可选中,只可碰撞交互)
     * @param _functionType
     */
    public void setFunctionType (byte _functionType)
    {
    	functionType = _functionType;
    }
    /**
     * NPC功能的类型(1=可选中,可交互类型; 2=不可选中,只可碰撞交互)
     * @return
     */
    public byte getFunctionType ()
    {
    	return functionType;
    }
    /**
     * 设置称谓
     * 
     * @return
     */
    public String getTitle ()
    {
        if (null == title)
        {
            return "";
        }

        return title;
    }

    /**
     * 给NPC添加功能
     * 
     * @param _function
     */
    public void addFunction (BaseNpcFunction _function)
    {
        functionList.add(_function);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hero.npc.ME2NotPlayer#action()
     */
    public void active ()
    {
        super.active();
        setDirection(Direction.DOWN);
    }

    @Override
    public boolean canBeAttackBy (ME2GameObject _object)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * 聆听
     * 
     * @param _speaker
     * @param _content
     */
    public void listen (HeroPlayer _speaker, YOYOInputStream _content)
    {
        try
        {
            if (canInteract())
            {
                byte stepID = _content.readByte();
                log.debug("NPC listen stepID = " + stepID);
                if (0 == stepID)
                {
                    handshake(_speaker);
                }
                else
                {
                    int functionMark = _content.readInt();
                    log.debug("NPC functionMark = " + functionMark);
                    BaseNpcFunction function = getFunction(functionMark);

                    if (null != function)
                    {
                        log.debug("NPC optionIndex = " + parseFunctionOptionIndex(functionMark));
                        function.process(_speaker, stepID,
                                parseFunctionOptionIndex(functionMark),
                                _content);
                    }
                }
            }
            else
            {
                ResponseMessageQueue.getInstance().put(
                        _speaker.getMsgQueueIndex(),
                        new NpcInteractiveResponse(getID(), (short) 0,
                                (byte) 1, UI_NpcHandshake.getBytes(null)));
            }
        }
        catch (Exception e)
        {
            log.error("与NPC交互出错:", e);
        }
    }

    /**
     * 触发交互
     * 
     * @param _player
     */
    public void handshake (HeroPlayer _player)
    {
        ArrayList<NpcHandshakeOptionData> options = null;

        if (functionList.size() > 0)
        {
            options = new ArrayList<NpcHandshakeOptionData>();

            for (BaseNpcFunction function : functionList)
            {
                ArrayList<NpcHandshakeOptionData> functionOption = function
                        .getTopLayerOptionList(_player);

                if (null != functionOption)
                {
                    for (NpcHandshakeOptionData nhod : functionOption)
                    {
                        options.add(nhod);
                    }
                }
            }
        }

        ResponseMessageQueue.getInstance().put(
                _player.getMsgQueueIndex(),
                new NpcInteractiveResponse(getID(), (short) 0, (byte) 1,
                        UI_NpcHandshake.getBytes(options)));
    }

    /**
     * 解析功能类型
     * 
     * @param _functionMark 功能标记
     * @return
     */
    private int parseFunctionType (int _functionMark)
    {
        return _functionMark / BaseNpcFunction.FUNCTION_EXPEND_MODULUS;
    }

    /**
     * 解析功能类型的选项索引
     * 
     * @param _functionMark 功能标记
     * @return
     */
    private int parseFunctionOptionIndex (int _functionMark)
    {
        return _functionMark % BaseNpcFunction.FUNCTION_EXPEND_MODULUS;
    }

    /**
     * 根据功能标记获取具体功能
     * 
     * @param _functionMark
     * @return
     */
    public BaseNpcFunction getFunction (int _functionMark)
    {

        int functionType = parseFunctionType(_functionMark);

        for (BaseNpcFunction function : functionList)
        {
            if (function.getFunctionType().value() == functionType)
            {
                return function;
            }
        }

        return null;
    }

    /**
     * 设置图片类型
     * 
     * @param _imageType
     */
    public void setImageType (byte _imageType)
    {
        imageType = _imageType;
    }

    /**
     * 获取图片类型
     * 
     * @return
     */
    public byte getImageType ()
    {
        return imageType;
    }

    /**
     * 能否交互（当NPC进入某种不收干扰的状态后，将变为不可交互）
     * 
     * @return
     */
    public boolean canInteract ()
    {
        return canInteract;
    }

    @Override
    public void die (ME2GameObject _killer)
    {
        // TODO Auto-generated method stub

    }

    public void heartBeat ()
    {
        // TODO Auto-generated method stub
        if (isCalled())
        {
            if (System.currentTimeMillis() - getRefreshTime() >= getExistsTime())
            {
                destroy();
                where().getMonsterList().remove(this);

                MapSynchronousInfoBroadcast.getInstance()
                        .put(
                                where(),
                                new DisappearNotify(getObjectType().value(),
                                        getID()), false, 0);
            }
        }
    }

    /**
     * 开始跟随
     * 
     * @param _player
     */
    public void beginFollow (HeroPlayer _player)
    {
        followAi = new NPCFollowAI(this);
        followAi.startFollow(_player);

        canInteract = false;
    }

    /**
     * 停止跟随，复位
     */
    public void stopFollowTask ()
    {
        if (null != followAi)
        {
            followAi.stopFollow();
            ThreadPoolFactory.getInstance().removeAI(followAi);
            followAi = null;
            canInteract = true;
        }
    }

    /**
     * 进入某地图
     * 
     * @param map
     */
    public void gotoMap (Map _map)
    {
        if (where() != null)
        {
            where().getNpcList().remove(this);

            if (where() != _map)
            {
                MapSynchronousInfoBroadcast.getInstance()
                        .put(
                                where(),
                                new DisappearNotify(getObjectType().value(),
                                        getID()), false, 0);
            }
        }

        live(_map);

        if (_map != null)
        {
            where().getNpcList().add(this);

            MapSynchronousInfoBroadcast.getInstance().put(
                    Constant.CLIENT_OF_HIGH_SIDE, where(),
                    new NpcRefreshNotify(Constant.CLIENT_OF_HIGH_SIDE, this),
                    false, 0);
            //del by zhengl date: 2011-05-06; note: 客户端已经不再使用该值.统一使用:CLIENT_OF_HIGH_SIDE
            //减少MapSynchronousInfoBroadcast.infoList 的大小,较少for循环次数
//            MapSynchronousInfoBroadcast.getInstance().put(
//                    Constant.CLIENT_OF_MIDDLE_SIDE, where(),
//                    new NpcRefreshNotify(Constant.CLIENT_OF_MIDDLE_SIDE, this),
//                    false, 0);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see hero.npc.ME2NotPlayer#destroy()
     */
    @Override
    public void destroy ()
    {
        // TODO Auto-generated method stub
        invalid();
        stopFollowTask();
        NotPlayerServiceImpl.getInstance().removeNpc(this);
    }

    @Override
    public void happenFight ()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public byte getDefaultSpeed ()
    {
        // TODO Auto-generated method stub
        return MoveSpeed.FASTER;
    }

    /**
     * 获取npc的动画id
     * @return
     */
	public short getAnimationID ()
	{
		return animationID;
	}

	/**
	 * 设置npc的动画id
	 * @param animationID
	 */
	public void setAnimationID (short animationID)
	{
		this.animationID = animationID;
	}
    
    
}
