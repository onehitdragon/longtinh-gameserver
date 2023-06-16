package hero.npc.function.system;

import java.io.EOFException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;

import hero.item.Goods;
import hero.npc.Npc;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.BaseNpcFunction;
import hero.npc.function.ENpcFunctionType;
import hero.npc.function.FunctionIconSet;
import hero.npc.message.NpcInteractiveResponse;
import hero.npc.service.NotPlayerServiceImpl;
import hero.player.HeroPlayer;
import hero.share.message.Warning;
import hero.share.service.LogWriter;
import hero.share.service.Tip;
import hero.task.Award;
import hero.task.Task;
import hero.task.TaskInstance;
import hero.task.Award.AwardGoodsUnit;
import hero.task.service.TaskServiceImpl;
import hero.task.target.BaseTaskTarget;
import hero.task.target.ETastTargetType;
import hero.task.target.TaskTargetEscortNpc;
import hero.ui.UI_TaskContent;
import hero.ui.message.ReturnMainUI;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 TaskFunction.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-17 下午02:33:58
 * @描述 ：任务
 */

public class TaskPassageway extends BaseNpcFunction
{
	private static Logger log = Logger.getLogger(TaskPassageway.class);
    enum Step
    {
        TOP(1), SURE(2);

        byte tag;

        Step(int _tag)
        {
            tag = (byte) _tag;
        }
    }

    /**
     * 可接受的任务功能标记
     */
    public static final byte     RECEIVE_TASK_MARK = 1;

    /**
     * 可提交的任务功能标记
     */
    public static final byte     SUBMIT_TASK_MARK  = 2;

    /**
     * 任务相关的对话功能标记
     */
    public static final byte     TASK_TALK         = 3;

    /**
     * NPC模板编号
     */
    private String               npcModelID;


    public TaskPassageway(int npcID, String _npcModelID)
    {
        super(npcID);
        // TODO Auto-generated constructor stub
        npcModelID = _npcModelID;
    }

    @Override
    public ENpcFunctionType getFunctionType ()
    {
        // TODO Auto-generated method stub
        return ENpcFunctionType.TASK;
    }

    @Override
    public void initTopLayerOptionList ()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void process (HeroPlayer _player, byte _step, int selectIndex,
            YOYOInputStream _content) throws Exception
    {
        // TODO Auto-generated method stub
        int taskOperateMark = parseTaskOperateMark(selectIndex);
        int taskID = parseTaskID(selectIndex);
        log.debug("task process step="+_step+" -- selectIndex="+selectIndex+" -- taskOperateMark="+taskOperateMark+" -- taskID="+taskID);
        Task task = TaskServiceImpl.getInstance().getTask(taskID);
        
        if (null != task)
        {
        	log.debug("npcModelID : "+npcModelID+", task [: " + task.getName());
            if (Step.TOP.tag == _step)
            {
                if (RECEIVE_TASK_MARK == taskOperateMark)
                {
                    if (TaskServiceImpl.getInstance().getTaskList(
                            _player.getUserID()).size() >= TaskServiceImpl.MAX_TASK_NUMBER_AT_TIME)
                    {
                        ResponseMessageQueue
                                .getInstance()
                                .put(
                                        _player.getMsgQueueIndex(),
                                        new Warning(
                                                Tip.TIP_NPC_OF_TASK_NUMBER_FULL_AT_RECEIVE));

                        return;
                    }

                    if (null != task.getReceiveGoodsList()
                            && task.getReceiveGoodsList().size() > _player
                                    .getInventory().getTaskToolBag()
                                    .getEmptyGridNumber())
                    {
                        ResponseMessageQueue.getInstance().put(
                                _player.getMsgQueueIndex(),
                                new Warning(
                                        Tip.TIP_NPC_OF_TASK_TOOL_BAG_FULL_AT_RECEIVE));

                        return;
                    }
                    //点开任务之后所得到的任务描述信息下发
                    ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new NpcInteractiveResponse(
                                    		getHostNpcID(),
                                    		getFunctionType().value()* BaseNpcFunction.FUNCTION_EXPEND_MODULUS+ selectIndex, 
                                    		Step.SURE.tag, 
                                    		UI_TaskContent.getBytes(task, RECEIVE_TASK_MARK,_player.getLevel())));
                }
                else if (SUBMIT_TASK_MARK == taskOperateMark)
                {
                    Award award = task.getAward();

                    String tip = null;

                    if (null != award.getOptionalGoodsList())
                    {
                        Goods awardGoods = award.getOptionalGoodsList().get(0).goods;

                        switch (awardGoods.getGoodsType())
                        {
                            case EQUIPMENT:
                            {
                                if (_player.getInventory().getEquipmentBag()
                                        .getEmptyGridNumber() <= 0)
                                {
                                    tip = Tip.TIP_NPC_OF_EQUPMENT_BAG_FULL_AT_SUBMIT;
                                }

                                break;
                            }
                            case MEDICAMENT:
                            {
                                if (_player.getInventory().getMedicamentBag()
                                        .getEmptyGridNumber() <= 0)
                                {
                                    tip = Tip.TIP_NPC_OF_MEDICAMENT_BAG_FULL_AT_SUBMIT;
                                }

                                break;
                            }
                            case MATERIAL:
                            {
                                if (_player.getInventory().getMaterialBag()
                                        .getEmptyGridNumber() <= 0)
                                {
                                    tip = Tip.TIP_NPC_OF_MATERIAL_BAG_FULL_AT_SUBMIT;
                                }

                                break;
                            }
                            case TASK_TOOL:
                            {
                                if (_player.getInventory().getTaskToolBag()
                                        .getEmptyGridNumber() <= 0)
                                {
                                    tip = Tip.TIP_NPC_OF_TASK_TOOL_BAG_FULL_AT_SUBMIT;
                                }

                                break;
                            }
                            case SPECIAL_GOODS:
                            {
                                if (_player.getInventory().getSpecialGoodsBag()
                                        .getEmptyGridNumber() <= 0)
                                {
                                    tip = Tip.TIP_NPC_OF_SPECIAL_GOODS_BAG_FULL_AT_SUBMIT;
                                }

                                break;
                            }
                        }

                        if (null != tip)
                        {
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning(tip));

                            return;
                        }
                    }

                    if (null != award.getBoundGoodsList())
                    {
                        ArrayList<AwardGoodsUnit> awardGoodsList = award
                                .getBoundGoodsList();

                        int euipmentNumber = 0, materialNumber = 0, medicamentNumber = 0, taskToolNumber = 0, specialGoodsNumber = 0;

                        for (AwardGoodsUnit awardGoodsUnit : awardGoodsList)
                        {
                            switch (awardGoodsUnit.goods.getGoodsType())
                            {
                                case EQUIPMENT:
                                {
                                    euipmentNumber++;

                                    break;
                                }
                                case MEDICAMENT:
                                {
                                    medicamentNumber++;

                                    break;

                                }
                                case MATERIAL:
                                {
                                    materialNumber++;

                                    break;
                                }
                                case TASK_TOOL:
                                {
                                    taskToolNumber++;

                                    break;
                                }
                                case SPECIAL_GOODS:
                                {
                                    specialGoodsNumber++;

                                    break;
                                }
                            }
                        }

                        if (euipmentNumber > _player.getInventory()
                                .getEquipmentBag().getEmptyGridNumber())
                        {
                            tip = Tip.TIP_NPC_OF_EQUPMENT_BAG_FULL_AT_SUBMIT;
                        }
                        else if (medicamentNumber > _player.getInventory()
                                .getMedicamentBag().getEmptyGridNumber())
                        {
                            tip = Tip.TIP_NPC_OF_MATERIAL_BAG_FULL_AT_SUBMIT;
                        }
                        else if (materialNumber > _player.getInventory()
                                .getMaterialBag().getEmptyGridNumber())
                        {
                            tip = Tip.TIP_NPC_OF_MATERIAL_BAG_FULL_AT_SUBMIT;
                        }
                        else if (taskToolNumber > _player.getInventory()
                                .getTaskToolBag().getEmptyGridNumber())
                        {
                            tip = Tip.TIP_NPC_OF_TASK_TOOL_BAG_FULL_AT_SUBMIT;
                        }
                        else if (specialGoodsNumber > _player.getInventory()
                                .getSpecialGoodsBag().getEmptyGridNumber())
                        {
                            tip = Tip.TIP_NPC_OF_SPECIAL_GOODS_BAG_FULL_AT_SUBMIT;
                        }

                        if (null != tip)
                        {
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning(tip));

                            return;
                        }
                    }

                    ResponseMessageQueue
                            .getInstance()
                            .put(
                                    _player.getMsgQueueIndex(),
                                    new NpcInteractiveResponse(
                                            getHostNpcID(),
                                            getFunctionType().value()
                                                    * BaseNpcFunction.FUNCTION_EXPEND_MODULUS
                                                    + selectIndex,
                                            Step.SURE.tag, UI_TaskContent
                                                    .getBytes(task,
                                                            SUBMIT_TASK_MARK,_player.getLevel())));
                }
                else if (TASK_TALK == taskOperateMark)
                {
                    if (_player.getEscortTarget() != null)
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_NPC_OF_NOT_ESCORT_MORE));

                        return;
                    }

                    ArrayList<BaseTaskTarget> targetList = task.getTargetList();

                    for (BaseTaskTarget target : targetList)
                    {
                        if ( ETastTargetType.ESCORT_NPC == target.getType()
                                && ((TaskTargetEscortNpc) target).npcModelID.equals(npcModelID) )
                        {
                        	Npc npc = NotPlayerServiceImpl.getInstance().getNpc(npcModelID);
                        	byte tox = (byte)npc.getCellX();
                        	byte toy = (byte)npc.getCellY();
                        	byte x = (byte)_player.getCellX();
                        	byte y = (byte)_player.getCellY();
                        	boolean resv = checkArea(tox, toy, (byte)4, (byte)1, x, y);
                        	if(resv)
                        	{
                                TaskServiceImpl.getInstance().beginEscortNpcTask(
                                        _player,
                                        task,
                                        (TaskTargetEscortNpc) target,
                                        NotPlayerServiceImpl.getInstance().getNpc(
                                                getHostNpcID()));

                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new ReturnMainUI());
                        	}
                        	else 
                        	{
								ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
										new Warning(Tip.TIP_NPC_OF_DISTANCE_IS_LONG.replaceAll(
												"%fn", npc.getName())));
							}
                            break;
                        }
                    }
                }
            }
            else if (Step.SURE.tag == _step)
            {
                if (RECEIVE_TASK_MARK == taskOperateMark)
                {
                    TaskServiceImpl.getInstance().receiveTask(_player, taskID);
                }
                else if (SUBMIT_TASK_MARK == taskOperateMark)
                {
                    int selectAwardGoodsID;

                    try
                    {
                        selectAwardGoodsID = _content.readInt();
                    }
                    catch (EOFException eofe)
                    {
                        LogWriter.println("任务可选奖励物品编号缺失");

                        return;
                    }

                    TaskServiceImpl.getInstance().submitTask(_player, taskID,
                            selectAwardGoodsID);
                }
            }
        }
    }
    
    /**
     * 
     * @param _tox
     * @param _toy
     * @param _area =3
     * @param _size =1
     * @return
     */
    public boolean checkArea(byte _tox, byte _toy, byte _area, byte _size , byte bytTileX, byte bytTileY)
    {
		if(_area == 0)
			return true;
		byte s_x = bytTileX;
		byte s_y = bytTileY;
		byte e_x = _tox;
		byte e_y = _toy;
		if ((Math.pow(s_x - e_x, 2) + Math.pow(s_y - e_y, 2)) <= Math.pow(_area + _size / 2, 2))
		{
			return true;
		}
		return false;
	}


    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList (
            HeroPlayer _player)
    {
        // TODO Auto-generated method stub
        ArrayList<NpcHandshakeOptionData> handshakeOptionList = new ArrayList<NpcHandshakeOptionData>();

        NpcHandshakeOptionData optionData = null;

        ArrayList<TaskInstance> playerList = TaskServiceImpl.getInstance()
                .getTaskList(_player.getUserID());

        for (TaskInstance taskInstance : playerList)
        {
            if (taskInstance.isCompleted())
            {
                if (taskInstance.getArchetype().getSubmitNpcID().equals(
                        npcModelID))
                {
                    optionData = new NpcHandshakeOptionData();
//                    optionData.miniImageID = TASK_STATUS_ICON[SUBMIT_TASK_MARK - 1];
                    optionData.miniImageID = getMinMarkIconID2();
                    optionData.optionDesc = taskInstance.getArchetype()
                            .getName();
                    optionData.functionMark = getFunctionType().value()
                            * BaseNpcFunction.FUNCTION_EXPEND_MODULUS
                            + produceTaskMark(SUBMIT_TASK_MARK, taskInstance
                                    .getArchetype().getID());

                    handshakeOptionList.add(optionData);
                }
            }
            else
            {
                ArrayList<BaseTaskTarget> targetList = taskInstance
                        .getTargetList();

                for (BaseTaskTarget target : targetList)
                {
                    if (ETastTargetType.ESCORT_NPC == target.getType()
                            && !target.isCompleted())
                    {
                        if (((TaskTargetEscortNpc) target).npcModelID
                                .equals(npcModelID))
                        {
                            optionData = new NpcHandshakeOptionData();
                            optionData.miniImageID = getMinMarkIconID2();
                            optionData.optionDesc = "跟我走吧";
                            optionData.functionMark = getFunctionType().value()
                                    * BaseNpcFunction.FUNCTION_EXPEND_MODULUS
                                    + produceTaskMark(TASK_TALK, taskInstance
                                            .getArchetype().getID());

                            handshakeOptionList.add(optionData);

                            break;
                        }
                    }
                }
            }
        }

        ArrayList<Task> taskList = TaskServiceImpl.getInstance()
                .getReceiveableTaskList(npcModelID, _player);

        if (null != taskList)
        {
            for (Task task : taskList)
            {
                optionData = new NpcHandshakeOptionData();
//                optionData.miniImageID = TASK_STATUS_ICON[RECEIVE_TASK_MARK - 1];
                optionData.miniImageID = getMinMarkIconID();
                optionData.optionDesc = task.getName();
                optionData.functionMark = getFunctionType().value()
                        * BaseNpcFunction.FUNCTION_EXPEND_MODULUS
                        + produceTaskMark(RECEIVE_TASK_MARK, task.getID());

                handshakeOptionList.add(optionData);
            }
        }

        return handshakeOptionList;
    }

    /**
     * 解析任务操作类型
     * 
     * @param _taskMark 任务标记
     * @return
     */
    private int parseTaskOperateMark (int _taskMark)
    {
        return _taskMark / 10000;
    }

    /**
     * 解析任务编号
     * @param _taskMark 任务标记
     * @return
     */
    private int parseTaskID (int _taskMark)
    {
        return _taskMark % 10000;
    }

    /**
     * 组合任务标识
     * 
     * @param _distributeOrSubmitMark
     * @param _taskID
     * @return
     */
    private int produceTaskMark (byte _distributeOrSubmitMark, int _taskID)
    {
        return _distributeOrSubmitMark * 10000 + _taskID;
    }

}
