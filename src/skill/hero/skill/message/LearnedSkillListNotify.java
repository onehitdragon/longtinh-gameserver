package hero.skill.message;

import hero.item.service.GoodsServiceImpl;
import hero.item.special.HeavenBook;
import hero.share.EVocation;
import hero.skill.ActiveSkill;
import hero.skill.PassiveSkill;
import hero.skill.unit.ActiveSkillUnit;
import hero.player.HeroPlayer;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;
import yoyo.service.tools.log.SystemLogManager;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 LearnedSkillListNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-22 下午02:53:59
 * @描述 ：已学技能列表，包括主动技能和被动技能2个列表
 * 0x1002
 */

public class LearnedSkillListNotify extends AbsResponseMessage
{
     private static Logger log = Logger.getLogger(LearnedSkillListNotify.class);
    /**
     * 主动技能列表
     */
    ArrayList<ActiveSkill>  activeSkillList;

    /**
     * 被动技能列表
     */
    ArrayList<PassiveSkill> passiveSkillList;
    
    private EVocation vocation;

    /**
     * 3个天书位置里的天书ID
     * 0:没有天书
     */
    private int[] heaven_book_ids;
    /**
     * 构造.
     * 已学技能列表
     */
    public LearnedSkillListNotify(HeroPlayer _player)
    {
        activeSkillList = _player.activeSkillList;
        passiveSkillList = _player.passiveSkillList;
        vocation = _player.getVocation();
        this.heaven_book_ids = _player.heaven_book_ids;
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
        yos.writeShort(activeSkillList.size());

        byte vocationType = 0;
        ActiveSkillUnit skillUnit;
//		|| vocation.baseIs(skill.learnerVocation[i]))
//      && !skill.getFromSkillBook)
        
        for (ActiveSkill skill : activeSkillList)
        {
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
            skillUnit = (ActiveSkillUnit) skill.skillUnit;
            yos.writeInt(skill.id);
            yos.writeUTF(skill.name);
            yos.writeByte(skill.level);
            yos.writeByte(skill.skillRank);//add by zhengl; date: 2011-03-22; note:添加技能阶层
            yos.writeShort(skill.iconID);
            yos.writeUTF(skill.description);
//            log.info("当前技能描述:"+skill.description);
            if(skill.next != null) {
            	yos.writeByte(1);
            	yos.writeUTF(skill.next.description);
//            	log.info("下一级技能描述:"+skill.next.description);
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
            yos.writeByte(skillUnit.activeSkillType.value());
            yos.writeShort(skill.coolDownID);
            yos.writeInt(skill.coolDownTime);
            yos.writeInt(skill.reduceCoolDownTime);
            yos.writeShort(skillUnit.releaseTime * 1000);
            if (skillUnit.releaseTime > 0) {
            	System.out.println("狂暴前速度列表:"+skillUnit.releaseTime);
			}
            //edit:	zhengl
            //date:	2010-12-05
            //note:	技能的武器类型变为多种
            if(skill.needWeaponType == null) {
            	yos.writeByte(1);
            	//edit by zhengl; date: 2011-02-10; note:同步把byte变为short
//            	output.writeByte(0);
            	yos.writeShort(0);
            	//end
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
//            log.info("0x1002 is skill releaseImageID:" + skillUnit.releaseImageID);
            yos.writeShort(skillUnit.releaseAnimationID);
//            log.info("0x1002 is skill releaseAnimationID:" + skillUnit.releaseAnimationID);
            yos.writeByte(skillUnit.animationAction);
            yos.writeByte(skillUnit.tierRelation);
            //add by zhengl; date: 2011-02-15; note: 添加施法技能播放在目标什么高度位置
            yos.writeByte(skillUnit.releaseHeightRelation);
//            log.info("skillUnit.animationAction=" + skillUnit.animationAction);
//            log.info("下发技能高度关系:=" + skillUnit.releaseHeightRelation);
            //add by zhengl; date: 2011-02-24; note: 施法技能特效是否按方向播放
            yos.writeByte(skillUnit.isDirection);
        }

        yos.writeShort(passiveSkillList.size());

        for (PassiveSkill skill : passiveSkillList)
        {
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
	                	log.info("warn:" + vocation.getDesc() + "加载[" + skill.name + "]失败");
					}
				}
    		}
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
        }

        yos.writeByte(heaven_book_ids.length);//天书槽数量
        for (int heaven_book_id : heaven_book_ids) {
            yos.writeInt(heaven_book_id);
            if (heaven_book_id > 0) {
                HeavenBook book = (HeavenBook) GoodsServiceImpl.getInstance().getGoodsByID(heaven_book_id);
                yos.writeShort(book.getIconID());
                yos.writeByte(book.getTrait().value());
                yos.writeShort(book.getSkillPoint());
                yos.writeUTF(book.getName());
                yos.writeUTF(book.getDescription());
            }
        }
    }
}
