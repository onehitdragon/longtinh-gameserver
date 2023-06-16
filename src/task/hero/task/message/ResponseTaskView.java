package hero.task.message;

import hero.expressions.service.CEService;
import hero.item.Goods;
import hero.share.service.LogWriter;
import hero.skill.Skill;
import hero.skill.service.SkillServiceImpl;
import hero.task.Task;
import hero.task.TaskInstance;
import hero.task.Award.AwardGoodsUnit;
import hero.task.clienthandler.OperateTaskList;
import hero.task.target.BaseTaskTarget;

import java.io.IOException;
import java.util.ArrayList;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseTaskList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-4-21 下午02:42:11
 * @描述 ：响应任务查看
 */

public class ResponseTaskView extends AbsResponseMessage
{
    /**
     * 任务
     */
    private TaskInstance taskIns;

    /**
     * 查看内容
     */
    private byte         viewContent;

    private short     playerLevel;

    /**
     * @param _existsTaskList
     */
    public ResponseTaskView(TaskInstance _taskIns, byte _viewContent,short playerLevel)
    {
        taskIns = _taskIns;
        viewContent = _viewContent;
        this.playerLevel = playerLevel;
    }

    @Override
    public int getPriority ()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        // TODO Auto-generated method stub
    	if (taskIns == null) 
    	{
    		LogWriter.error("error:查看任务的时候传入任务实例参数为NULL,执行被终止.", null);
			return;
		}
        Task task = taskIns.getArchetype();

        yos.writeByte(viewContent);
        yos.writeInt(task.getID());

        if (OperateTaskList.VIEW_DESC == viewContent)
        {
            yos.writeUTF(task.getViewDesc());
            yos.writeInt(task.getAward().money);
//            output.writeInt(task.getAward().experience);
            yos.writeInt(CEService.taskExperience(playerLevel,
                    task.getLevel(),task.getAward().experience));

            ArrayList<BaseTaskTarget> targetList = taskIns.getTargetList();

            yos.writeByte(targetList.size());

            for (BaseTaskTarget target : targetList)
            {
                yos.writeInt(target.getID());
                yos.writeByte(target.isCompleted());
                yos.writeUTF(target.getDescripiton());
                yos.writeByte(target.canTransmit());
            }
        }
        else if (OperateTaskList.VIEW_AWARD == viewContent)
        {
            ArrayList<AwardGoodsUnit> awardGoodList = task.getAward()
                    .getOptionalGoodsList();

            if (null != awardGoodList && awardGoodList.size() > 0)
            {
                yos.writeByte(awardGoodList.size());

                Goods goods = null;

                for (AwardGoodsUnit awardGoodsUnit : awardGoodList)
                {
                    goods = awardGoodsUnit.goods;
                    yos.writeInt(goods.getID());
                    yos.writeUTF(goods.getName());
                    yos.writeByte(goods.getTrait().value());
                    yos.writeShort(goods.getIconID());
                    yos.writeUTF(goods.getDescription());
                    yos.writeByte(awardGoodsUnit.number);
                }
            }
            else
            {
                yos.writeByte(0);
            }

            awardGoodList = task.getAward().getBoundGoodsList();

            if (null != awardGoodList && awardGoodList.size() > 0)
            {
                yos.writeByte(awardGoodList.size());

                Goods goods = null;

                for (AwardGoodsUnit awardGoodsUnit : awardGoodList)
                {
                    goods = awardGoodsUnit.goods;
                    yos.writeUTF(goods.getName());
                    yos.writeByte(goods.getTrait().value());
                    yos.writeShort(goods.getIconID());
                    yos.writeUTF(goods.getDescription());
                    yos.writeByte(awardGoodsUnit.number);
                }
            }
            else
            {
                yos.writeByte(0);
            }

            int skillID = task.getAward().skillID;

            yos.writeInt(skillID);

            if (skillID > 0)
            {
                Skill skill = SkillServiceImpl.getInstance().getSkillModel(
                        skillID);

                if (null != skill)
                {
                    yos.writeUTF(skill.name);
                    yos.writeShort(skill.iconID);
                    yos.writeUTF(skill.description);
                }
            }
        }
    }
}
