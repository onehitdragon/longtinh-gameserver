package hero.ui;

import hero.expressions.service.CEService;
import hero.item.EqGoods;
import hero.item.Equipment;
import hero.item.Goods;
import hero.skill.Skill;
import hero.skill.service.SkillServiceImpl;
import hero.npc.function.system.TaskPassageway;
import hero.task.Task;
import hero.task.Award.AwardGoodsUnit;

import java.io.IOException;
import java.util.ArrayList;

import yoyo.tools.YOYOOutputStream;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 UI_TaskContent.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-4-17 上午11:18:12
 * @描述 ：
 */

public class UI_TaskContent
{
    /**
     * 获取UI类型
     * 
     * @return
     */
    public static EUIType getType ()
    {
        return EUIType.TASK_CONTENT;
    }

    /**
     * 获取内容字节数组
     * 
     * @param _task 任务
     * @return
     */
    public static byte[] getBytes (Task _task, byte _receiveOfSubmit,short playerLevel)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());
            output.writeByte(_receiveOfSubmit);
            output.writeShort(_task.getLevel());
            output.writeUTF(_task.getName());

            if (TaskPassageway.RECEIVE_TASK_MARK == _receiveOfSubmit)
            {
                output.writeUTF(_task.getReceiveDesc());
            }
            else
            {
                output.writeUTF(_task.getSubmitDesc());
            }

            output.writeInt(_task.getAward().money);
//            output.writeInt(_task.getAward().experience);
            output.writeInt(CEService.taskExperience(playerLevel,
                    _task.getLevel(), _task.getAward().experience));

            ArrayList<AwardGoodsUnit> awardGoodList = _task.getAward()
                    .getOptionalGoodsList();

            if (null != awardGoodList)
            {
                output.writeByte(awardGoodList.size());

                Goods goods = null;

                for (AwardGoodsUnit awardGoodsUnit : awardGoodList)
                {
                    goods = awardGoodsUnit.goods;
                    if (goods instanceof Equipment)
                    {
                    	output.writeByte(0);
                        EqGoods e = (EqGoods)goods;
                        output.writeInt(goods.getID());//装备采用模型编号或者实例的编号）
                        output.writeShort(e.getIconID());// 物品图标
                        output.writeUTF(e.getName());// 物品名称
                        output.writeBytes(e.getFixPropertyBytes());
                        output.writeByte(0);
                        output.writeByte(0);
                        output.writeShort(e.getMaxDurabilityPoint());
                        output.writeByte(1);// 物品可操作的最大数量
                        output.writeInt(e.getSellPrice());// 出售价格
                        /*add by zhengl; date: 2011-04-26; note: 添加等级给客户端显示用*/
                        output.writeShort(e.getNeedLevel());
                    }
                    else 
                    {
						output.writeByte(1);
                    	output.writeInt(goods.getID());
                    	output.writeUTF(goods.getName());
                    	output.writeByte(goods.getTrait().value());
                    	output.writeShort(goods.getIconID());
                    	output.writeUTF(goods.getDescription());
                    	output.writeByte(awardGoodsUnit.number);
                    	output.writeInt(goods.getSellPrice());// 出售价格
                    	/*add by zhengl; date: 2011-04-26; note: 添加等级给客户端显示用*/
                    	output.writeShort(goods.getNeedLevel());
					}

                }
            }
            else
            {
                output.writeByte(0);
            }

            awardGoodList = _task.getAward().getBoundGoodsList();

            if (null != awardGoodList)
            {
                output.writeByte(awardGoodList.size());

                Goods goods = null;

                for (AwardGoodsUnit awardGoodsUnit : awardGoodList)
                {
                    goods = awardGoodsUnit.goods;
                    if (goods instanceof Equipment)
                    {
                    	output.writeByte(0);//代表装备
                        EqGoods e = (EqGoods)goods;
                        output.writeShort(e.getIconID());// 物品图标
                        output.writeUTF(e.getName());// 物品名称
                        output.writeBytes(e.getFixPropertyBytes());
                        output.writeByte(0);
                        output.writeByte(0);
                        output.writeShort(e.getMaxDurabilityPoint());
                        output.writeByte(1);// 物品可操作的最大数量
                        output.writeInt(e.getSellPrice());// 出售价格
                        /*add by zhengl; date: 2011-04-26; note: 添加等级给客户端显示用*/
                        output.writeShort(e.getNeedLevel());
                    }
                    else {
                    	output.writeByte(1);//代表
                    	output.writeUTF(goods.getName());
                    	output.writeByte(goods.getTrait().value());
                    	output.writeShort(goods.getIconID());
                    	output.writeUTF(goods.getDescription());
                    	output.writeByte(awardGoodsUnit.number);
                    	output.writeInt(goods.getSellPrice());// 出售价格
                    	/*add by zhengl; date: 2011-04-26; note: 添加等级给客户端显示用*/
                        output.writeShort(goods.getNeedLevel());
					}

                }
            }
            else
            {
                output.writeByte(0);
            }

            int skillID = _task.getAward().skillID;

            output.writeInt(skillID);

            if (skillID > 0)
            {
                Skill skill = SkillServiceImpl.getInstance().getSkillModel(
                        skillID);

                if (null != skill)
                {
                    output.writeUTF(skill.name);
                    output.writeShort(skill.iconID);
                    output.writeUTF(skill.description);
                    output.writeShort(skill.learnerLevel);
                }
            }

            output.flush();

            return output.getBytes();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            try
            {
                output.close();
            }
            catch (IOException e)
            {
            }
            output = null;
        }
    }
}
