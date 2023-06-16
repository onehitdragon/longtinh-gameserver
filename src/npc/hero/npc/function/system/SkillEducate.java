package hero.npc.function.system;

import java.util.ArrayList;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;

import hero.skill.Skill;
import hero.skill.dict.SkillDict;
import hero.skill.service.SkillServiceImpl;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.ENpcFunctionType;
import hero.npc.function.BaseNpcFunction;
import hero.npc.message.NpcInteractiveResponse;
import hero.player.HeroPlayer;
import hero.share.ESystemFeature;
import hero.share.EVocation;
import hero.share.message.Warning;
import hero.ui.UI_Confirm;
import hero.ui.UI_SkillList;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SkillEducate.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-17 下午02:33:06
 * @描述 ：技能训练
 */

public class SkillEducate extends BaseNpcFunction
{
    /**
     * 顶层操作菜单列表
     */
    private static final String[] mainMenuList            = {"学习技能", "遗忘技能" };

    /**
     * 顶层操作菜单图标列表
     */
    private static final short[]  mainMenuMarkImageIDList = {1006, 1007 };

    /**
     * 学习技能菜单列表
     */
    private static final String[] learnMenuList           = {"学　　习", "查　　看" };

    /**
     * 遗忘技能确认框提示内容
     */
    private static final String   forgetSkillTip          = "你确认要遗忘所有的技能吗？";

    /**
     * 训练职业
     */
    private EVocation             educateVocation;

    /**
     * 技能列表
     */
    private ArrayList<Skill>      skillList;

    enum Step
    {
        TOP(1), LEARN_SKILL(2);

        byte tag;

        Step(int _tag)
        {
            tag = (byte) _tag;
        }
    }

    /**
     * 构造
     * 
     * @param _vocation
     */
    public SkillEducate(int _hostNpcID, EVocation _vocation,
            ESystemFeature _feature)
    {
        super(_hostNpcID);
        educateVocation = _vocation;
        skillList = SkillDict.getInstance().getSkillList(educateVocation,
                _feature);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hero.npc.function.NpcFunction#process(hero.player.HeroPlayer, byte,
     *      byte, me2.util.ME2InputStream)
     */
    @Override
    public void process (HeroPlayer _player, byte _step, int selectIndex,
            YOYOInputStream _content) throws Exception
    {
        // TODO Auto-generated method stub
        if (Step.TOP.tag == _step)
        {
            if (0 == selectIndex)
            {
                ArrayList<Skill> learnableSkillList = SkillServiceImpl
                        .getInstance()
                        .getLearnableSkillList(skillList, _player);
                
                if (null != learnableSkillList)
                {
                    ResponseMessageQueue.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new NpcInteractiveResponse(getHostNpcID(), 
                            		optionList.get(selectIndex).functionMark, 
                            		Step.LEARN_SKILL.tag, 
                                    UI_SkillList.getBytes(learnMenuList, learnableSkillList)));
                }
                else
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(TIP_OF_NONE_SKILL));
                }
            }
            else if (1 == selectIndex)
            {
            	//del by zhengl ; date: 2010-11-09 ; note: 为保险起见,删除老的技能洗点方式.
//                SkillServiceImpl.getInstance().forgetSkill(_player);
            }
        }
        else if (Step.LEARN_SKILL.tag == _step)
        {
            byte optionIndex = _content.readByte();

            // 学习某技能
            if (0 == optionIndex)
            {
            	//del by zhengl ; date: 2010-11-09 ; note: 为保险起见,删除老的技能学习方式
//                SkillServiceImpl.getInstance().learnSkill(_player,
//                        _content.readInt());
            }
        }
    }

    @Override
    public ENpcFunctionType getFunctionType ()
    {
        // TODO Auto-generated method stub
        return ENpcFunctionType.SKILL_EDUCATE;
    }

    @Override
    public void initTopLayerOptionList ()
    {
        // TODO Auto-generated method stub
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

        optionList.get(1).followOptionData = new ArrayList<byte[]>(1);
        optionList.get(1).followOptionData.add(UI_Confirm
                .getBytes(forgetSkillTip));
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList (
            HeroPlayer _player)
    {
        // TODO Auto-generated method stub
        if (_player.getVocation() == educateVocation)
        {
            return optionList;
        }
        else
        {
            return null;
        }
    }

    /**
     * 没有技能学时的提示
     */
    private static final String TIP_OF_NONE_SKILL = "没有技能可学";
}
