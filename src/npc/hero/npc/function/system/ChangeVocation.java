package hero.npc.function.system;

//import hero.gm.EResponseType;
//import hero.gm.ResponseToGmTool;
//import hero.gm.service.GmServiceImpl;
import hero.group.service.GroupServiceImpl;
import hero.item.TaskTool;
import hero.item.dictionary.ChangeVocationToolsDict;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.BaseNpcFunction;
import hero.npc.function.ENpcFunctionType;
import hero.player.HeroPlayer;
import hero.player.define.EClan;
import hero.player.message.VocationChangeNotify;
import hero.player.service.PlayerServiceImpl;
import hero.share.EVocation;
import hero.share.EVocationType;
import hero.share.message.Warning;
import hero.skill.Skill;
import hero.skill.dict.SkillDict;
import hero.skill.message.LearnedSkillListNotify;
import hero.skill.service.SkillServiceImpl;
import hero.task.service.TaskServiceImpl;
import hero.ui.UI_Confirm;
import hero.ui.message.CloseUIMessage;

import java.util.ArrayList;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ChangeVocation.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-13 下午04:31:25
 * @描述 ：转职功能，只要玩家背包中拥有相应的转职道具就可以转变为新的职业
 */

public class ChangeVocation extends BaseNpcFunction
{
    /**
     * 顶层操作菜单列表
     */
    private String[]             mainMenuList;

    /**
     * 无道具提示
     */
    private String               noneToolTip;

    /**
     * 确认提示内容
     */
    private String               confirmTip;

    /**
     * 职业
     */
    private EVocation          vocationThatChange;
    
    private EVocation[]		   changes;

    /**
     * 需要的道具
     */
    private TaskTool             tool;

    /**
     * 构造
     * 
     * @param _npcID
     * @param _vocation
     */
    public ChangeVocation(int _npcID, EVocation _vocation, EClan _clan)
    {
        super(_npcID);
        //"转职为" 
        vocationThatChange = _vocation;
        changes = vocationThatChange.getSubVoction(_clan);
        tool = ChangeVocationToolsDict.getInstance().getToolByVocation(vocationThatChange);
        noneToolTip = "缺少转职道具‘" + tool.getName() + "’";
        confirmTip = "确定转职吗"; // + changes[0].getDesc() + "’";
        mainMenuList = new String[changes.length];
        for (int i = 0; i < mainMenuList.length; i++) 
        {
        	mainMenuList[i] = "转职为" + changes[i].getDesc();
		}
        initTopLayerOptionListSecond();
    }

    @Override
    public ENpcFunctionType getFunctionType ()
    {
        // TODO Auto-generated method stub
        return ENpcFunctionType.CHANGE_VOCATION;
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList (
            HeroPlayer _player)
    {
        // TODO Auto-generated method stub
    	//开3转也在这里改东西
        if (_player.getVocation() == vocationThatChange)
        {
            return optionList;
        }
        else
        {
            return null;
        }
    }

    @Override
    public void initTopLayerOptionList ()
    {
        // TODO Auto-generated method stub

    }

    /**
     * 再次初始化顶层操作菜单
     */
    private void initTopLayerOptionListSecond ()
    {
        for (int i = 0; i < mainMenuList.length; i++)
        {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
//            data.miniImageID = mainMenuMarkImageIDList[i];
            data.miniImageID = getMinMarkIconID();
            data.optionDesc = mainMenuList[i];
            data.functionMark = getFunctionType().value()
                    * BaseNpcFunction.FUNCTION_EXPEND_MODULUS + i;

            optionList.add(data);
        }

        optionList.get(0).followOptionData = new ArrayList<byte[]>(1);
        optionList.get(0).followOptionData.add(UI_Confirm.getBytes(confirmTip));
    }

    @Override
    public void process (HeroPlayer _player, byte _step, int selectIndex,
            YOYOInputStream _content) throws Exception
    {
        // TODO Auto-generated method stub
        if (_player.getInventory().getTaskToolBag()
                .getGoodsNumber(tool.getID()) < 1)
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(noneToolTip));
        }
        else
        {
            if (GoodsServiceImpl.getInstance().deleteOne(_player,
                    _player.getInventory().getTaskToolBag(), tool.getID(),
                    CauseLog.CHANGEVOCATION))
            {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new CloseUIMessage());
                _player.setVocation(changes[selectIndex]);
                PlayerServiceImpl.getInstance().dbUpdate(_player);

                PlayerServiceImpl.getInstance()
                        .reCalculateRoleProperty(_player);
                PlayerServiceImpl.getInstance().refreshRoleProperty(_player);
                
                //add by zhengl ; date: 2010-11-16 ; note: 转职之后新技能列表获得
                SkillServiceImpl.getInstance().changeVocationProcess(_player);
                //end
                VocationChangeNotify msg = new VocationChangeNotify(
                		_player.getID(), changes[selectIndex].value(), 
                		_player.getActualProperty().getHpMax(), 
                		_player.getActualProperty().getMpMax());

                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);

                MapSynchronousInfoBroadcast.getInstance().put(_player.where(),
                        msg, true, _player.getID());

                TaskServiceImpl.getInstance().notifyMapNpcTaskMark(_player,
                        _player.where());
                GroupServiceImpl.getInstance().refreshMemberVocation(_player);

                /**
                 * 推送转职信息，插入队列
                 */
//                ResponseToGmTool rtgt = new ResponseToGmTool(
//                        EResponseType.SEND_ROLE_LVL_UPDATE, 0);
//                rtgt.setRoleLvlUpdate(_player.getName(), _player.getLevel(),
//                        _player.getVocation().getDesc());
//                GmServiceImpl.addGmToolMsg(rtgt);
                // GameMasterServiceImpl.getInstance().sendRoleLvlUpdate(
                // _player.getName(), _player.getLevel(),
                // _player.getVocation().getDesc());
            }
        }
    }
}
