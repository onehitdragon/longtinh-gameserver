package hero.npc.function;

import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.dict.NpcFunIconDict;
import hero.player.HeroPlayer;

import java.util.ArrayList;

import yoyo.tools.YOYOInputStream;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 NpcFunction.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-17 下午02:53:49
 * @描述 ：NPC携带功能
 */

public abstract class BaseNpcFunction
{
    /**
     * 功能标识扩展系数
     */
    public static final int                     FUNCTION_EXPEND_MODULUS = 100000;

    /**
     * 宿主NPC编号
     */
    private int                                 hostNpcID;

    /**
     * 顶层操作项数据
     */
    protected ArrayList<NpcHandshakeOptionData> optionList;

    /**
     * 构造
     */
    public BaseNpcFunction(int _hostNpcID)
    {
        hostNpcID = _hostNpcID;
        optionList = new ArrayList<NpcHandshakeOptionData>();
        initTopLayerOptionList();
    }

    /**
     * 获取宿主NPC编号
     * 
     * @return
     */
    public int getHostNpcID ()
    {
        return hostNpcID;
    }

    /**
     * 获取功能图标ID
     * 
     * @return
     */
    public short getMinMarkIconID(){
        return NpcFunIconDict.getInstance().getNpcFunIcon(getFunctionType().value())[0];
    }
    /**
     * 获取第二个功能图标ID
     * 用于类似任务NPC的提交任务图标
     * @return
     */
    public short getMinMarkIconID2(){
    	return NpcFunIconDict.getInstance().getNpcFunIcon(getFunctionType().value())[1];
    }

    /**
     * 处理操作请求
     * 
     * @param _player 玩家
     * @param _step 步骤标记
     * @param _topSelectIndex 顶层选项索引
     * @param _content 请求携带的数据流
     */
    public abstract void process (HeroPlayer _player, byte _step,
            int _topSelectIndex, YOYOInputStream _content) throws Exception;

    /**
     * 获取功能类型
     * 
     * @return
     */
    public abstract ENpcFunctionType getFunctionType ();

    /**
     * 初始化顶层操作项数据
     */
    public abstract void initTopLayerOptionList ();

    /**
     * 获取顶层选项
     * 
     * @param _player
     * @return
     */
    public abstract ArrayList<NpcHandshakeOptionData> getTopLayerOptionList (
            HeroPlayer _player);
}
