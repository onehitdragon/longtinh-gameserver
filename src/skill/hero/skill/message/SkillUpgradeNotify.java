package hero.skill.message;

import hero.player.HeroPlayer;
import hero.share.EVocation;
import hero.skill.ActiveSkill;
import hero.skill.Skill;
import hero.skill.unit.ActiveSkillUnit;

import java.io.IOException;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SkillUpgradeNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-29 上午09:40:50
 * @描述 ：技能升级通知
 */

public class SkillUpgradeNotify extends AbsResponseMessage
{
     private static Logger log = Logger.getLogger(SkillUpgradeNotify.class);
    /**
     * 增加的技能
     */
    private Skill skill;
    
    private EVocation vocation;
    
    private short surplusSkillPoint;

    /**
     * 构造
     * 
     * @param _skill
     */
    public SkillUpgradeNotify(Skill _skill, HeroPlayer _player)
    {
        skill = _skill;
        vocation = _player.getVocation();
        surplusSkillPoint = _player.surplusSkillPoint;
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
    	byte vocationType = 0;
    	boolean isLoad = false;
		for (int i = 0; i < skill.learnerVocation.length; i++) {
			//1,技能要求的职业与玩家职业匹配
			//2,技能要求的职业与玩家的基础职业匹配
			//3,技能非法:既不与玩家职业匹配也不与玩家基础职业匹配
            if (skill.learnerVocation[i] == vocation) {
            	vocationType = skill.learnerVocation[i].value();
            	log.info(vocation.getDesc() + " 成功加载" + skill.learnerVocation[i].getDesc()
                        + "职业的[" + skill.name + "]");
            	isLoad = true;
            } else if(skill.learnerVocation[i] == vocation.getBasicVoction()) {
            	vocationType = skill.learnerVocation[i].value();
            	log.info(vocation.getDesc() + " 成功加载" + skill.learnerVocation[i].getDesc()
                        + "职业的[" + skill.name + "]");
            	isLoad = true;
			} else {
				if(i == skill.learnerVocation.length -1 && !isLoad) {
					//多次匹配依然无法加载这个技能将警告.
                	log.info("warn:" + vocation.getDesc() + "加载[" + skill.name + "]失败");
				}
			}
		}
        yos.writeByte(skill.getType().value());
        yos.writeUTF(skill.name);
        yos.writeInt(skill.id);
        yos.writeByte(skill.level);
        yos.writeByte(skill.skillRank);//add by zhengl; date: 2011-03-22; note:添加技能阶层
        yos.writeUTF(skill.description);
        if(skill.next != null) {
        	yos.writeByte(1);
        	yos.writeUTF(skill.next.description);
        } else {
        	yos.writeByte(0);
		}
        yos.writeShort(skill.skillPoints);
        //add by zhengl; date: 2011-02-20; note: 下级技能学习费用
        if(skill.next != null) {
        	yos.writeInt(skill.learnFreight);//学习技能需要的金额
        } else {
        	yos.writeInt(0);
		}
        yos.writeByte(vocationType);
        //edit:	zhengl
        //date:	2010-11-17
        //note:	通知玩家的剩余技能变化.
        //提示:仅为了方便编码而把该属性下发放置于此.
        yos.writeShort(surplusSkillPoint);
        //end

        if (skill instanceof ActiveSkill)
        {
            ActiveSkill activeSkill = (ActiveSkill) skill;
            ActiveSkillUnit skillUnit = (ActiveSkillUnit) activeSkill.skillUnit;
            yos.writeInt(activeSkill.coolDownTime);
            yos.writeShort(skillUnit.releaseTime * 1000);
            yos.writeByte(skillUnit.targetType.value());
            yos.writeByte(skillUnit.targetDistance);
            yos.writeShort(skillUnit.consumeHp);
            log.info("使用技能需要消耗的生命值:" + skillUnit.consumeHp);
            yos.writeShort(skillUnit.consumeMp);
            yos.writeByte(skillUnit.consumeFp);
            yos.writeByte(skillUnit.consumeGp);
            //add by zhengl; date: 2011-02-20; note: 添加学习技能时候的必发项
            yos.writeShort(skillUnit.releaseImageID);
            yos.writeShort(skillUnit.releaseAnimationID);
            yos.writeByte(skillUnit.animationAction);
            yos.writeByte(skillUnit.tierRelation);
            yos.writeByte(skillUnit.releaseHeightRelation);
            //end
            //add by zhengl; date: 2011-02-24; note: 施法技能特效是否按方向播放
            yos.writeByte(skillUnit.isDirection);
        }
    }
}
