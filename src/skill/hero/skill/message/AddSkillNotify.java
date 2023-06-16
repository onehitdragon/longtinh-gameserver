package hero.skill.message;

import java.io.IOException;

import hero.player.HeroPlayer;
import hero.share.EVocation;
import hero.skill.ActiveSkill;
import hero.skill.Skill;
import hero.skill.unit.ActiveSkillUnit;
import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 AddSkillNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-29 上午09:08:38
 * @描述 ：
 */

public class AddSkillNotify extends AbsResponseMessage
{
     private static Logger log = Logger.getLogger(AddSkillNotify.class);
    /**
     * 增加的技能
     */
    private Skill skill;
    
    private EVocation vocation;

    /**
     * 构造
     * 
     * @param _skill
     */
    public AddSkillNotify(Skill _skill, HeroPlayer _player)
    {
        skill = _skill;
        vocation = _player.getVocation();
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
        for (int i = 0; i < skill.learnerVocation.length; i++) {
            if (skill.learnerVocation[i] == vocation) {
            	vocationType = vocation.value();
            	break;
            } else {
            	//是在无法匹配证明该技能为基础技能.
            	vocationType = vocation.getBasicVoction().value();
			}
		}
        yos.writeByte(skill.getType().value());
        yos.writeInt(skill.id);
        yos.writeUTF(skill.name);
        yos.writeByte(skill.level);
        yos.writeByte(skill.skillRank);//add by zhengl; date: 2011-03-22; note:添加技能阶层
        yos.writeShort(skill.iconID);
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
        	yos.writeInt(skill.learnFreight);//升级技能需要的金额
        } else {
        	yos.writeInt(0);
		}
        yos.writeByte(vocationType);

        if (skill instanceof ActiveSkill)
        {
            ActiveSkill activeSkill = (ActiveSkill) skill;
            ActiveSkillUnit skillUnit = (ActiveSkillUnit) activeSkill.skillUnit;

            yos.writeByte(skillUnit.activeSkillType.value());
            yos.writeShort(activeSkill.coolDownID);
            yos.writeInt(activeSkill.coolDownTime);
            yos.writeInt(activeSkill.reduceCoolDownTime);
            yos.writeShort(skillUnit.releaseTime * 1000);
            //edit:	zhengl
            //date:	2010-12-05
            //note:	技能的武器类型变为多种
            if(skill.needWeaponType == null) {
            	yos.writeByte(1);
            	yos.writeByte(0);
            } else {
            	byte size = (byte)skill.needWeaponType.size();
            	yos.writeByte(size);
				for (int i = 0; i < size; i++) {
					yos.writeShort(skill.needWeaponType.get(i).getID());
				}
			}
//            output.writeShort(activeSkill.needWeaponType == null ? 0
//                    : skill.needWeaponType.getID());
            //end
            yos.writeByte(skillUnit.isNeedTarget());
            yos.writeByte(skillUnit.targetType.value());
            yos.writeByte(skillUnit.targetDistance);
            yos.writeShort(skillUnit.consumeHp);
            yos.writeShort(skillUnit.consumeMp);
            yos.writeByte(skillUnit.consumeFp);
            yos.writeByte(skillUnit.consumeGp);
            //edit by zhengl ; date: 2010-12-06 ; note: 动画ID现在应该为short
            yos.writeShort(skillUnit.releaseImageID);
            log.info("0x1002 is skill releaseImageID:" + skillUnit.releaseImageID);
            yos.writeShort(skillUnit.releaseAnimationID);
            log.info("0x1002 is skill releaseAnimationID:" + skillUnit.releaseAnimationID);
            yos.writeByte(skillUnit.animationAction);
            yos.writeByte(skillUnit.tierRelation);
            //add by zhengl; date: 2011-02-15; note: 添加技能播放在目标什么高度位置
            yos.writeByte(skillUnit.releaseHeightRelation);
        }
    }
}
