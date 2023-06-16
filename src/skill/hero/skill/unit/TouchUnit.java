package hero.skill.unit;

import java.util.Random;

import hero.effect.service.EffectServiceImpl;
import hero.expressions.service.CEService;
import hero.fight.service.FightServiceImpl;
import hero.player.HeroPlayer;
import hero.skill.detail.AdditionalActionUnit;
import hero.skill.detail.EAOERangeBaseLine;
import hero.skill.detail.EAOERangeType;
import hero.skill.detail.EHarmType;
import hero.skill.detail.EMathCaluOperator;
import hero.skill.detail.ETargetRangeType;
import hero.skill.detail.ETargetType;
import hero.skill.detail.ETouchType;
import hero.skill.dict.SkillUnitDict;
import hero.skill.service.SkillServiceImpl;
import hero.share.Constant;
import hero.share.EMagic;
import hero.share.ME2GameObject;
import org.apache.log4j.Logger;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 TouchUnit.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-13 下午05:56:42
 * @描述 ：触发性被动技能，被触发后会产生类似主动技能的效果
 */

public class TouchUnit extends PassiveSkillUnit
{
    private static Logger log = Logger.getLogger(TouchUnit.class);
    /**
     * @param _id
     * @param _name
     */
    public TouchUnit(int _id)
    {
        super(_id);
        // TODO Auto-generated constructor stub
    }

    /**
     * 触发方式
     */
    public ETouchType             touchType;

    /**
     * 目标类型
     */
    public ETargetType            targetType;

    /**
     * 目标范围类型
     */
    public ETargetRangeType       targetRangeType;

    /**
     * 目标范围数量
     */
    public short                  rangeTargetNumber;

    /**
     * 范围性法术范围基准
     */
    public EAOERangeBaseLine      rangeLine;

    /**
     * 范围性法术范围模式（中心模式、前方矩形模式）
     */
    public EAOERangeType          rangeMode;

    /**
     * 范围法术范围宽度
     */
    public byte                   rangeX;

    /**
     * 范围性法术范围高度
     */
    public byte                   rangeY;

    /**
     * 是否分摊技能结果
     */
    public boolean                isAverageResult;

    /**
     * 物理伤害类型
     */
    public EHarmType              physicsHarmType;

    /**
     * 直接物理伤害
     */
    public int                    physicsHarmValue;

    /**
     * 武器伤害倍数
     */
    public float                  weaponHarmMult;

    /**
     * 法术伤害属性
     */
    public EMagic                 magicHarmType;

    /**
     * 法术伤害生命值值
     */
    public int                    magicHarmHpValue;

    /**
     * 法术伤害魔法值
     */
    public int                    magicHarmMpValue;

    /**
     * 恢复生命值
     */
    public int                    resumeHp;

    /**
     * 恢复魔法值
     */
    public int                    resumeMp;

    /**
     * 产生的仇恨值
     */
    public int                    hateValue;

    /**
     * 附带的技能或效果单元信息列表（这部分数据不可以被强化）
     */
    public AdditionalActionUnit[] additionalOddsActionUnitList;

    /**
     * 作用动画编号
     */
    public short                  activeAnimationID;
    
    /**
     * 作用图片编号
     */
    public short                  activeImageID;
    
    /**
     * 承受技能播放高度关系
     * 1=脚下播放 (力量光环,闪电召唤等)
     * 2=中心位置播放 (火球术等)
     * 3=头顶播放 (小天使光圈等)
     */
    public byte                   heightRelation;

    /*
     * (non-Javadoc)
     * 
     * @see hero.n_skill.unit.PassiveSkillUnit#getPassiveSkillType()
     */
    public PassiveSkillType getPassiveSkillType ()
    {
        return PassiveSkillType.TOUCH;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    public PassiveSkillUnit clone () throws CloneNotSupportedException
    {
        return (PassiveSkillUnit) super.clone();
    }

    @Override
    public void touch (ME2GameObject _toucher, ME2GameObject _other,
            ETouchType _touchType, boolean _isSkillTouch)
    {
        //edit by zhengl; date: 2011-03-07; note: 触发类型变更为合理值.
        if (touchType.canTouch(touchType, _touchType))
        {
            if (targetType == ETargetType.FRIEND)
            {
                if (null == _other || !_other.isEnable() || _other.isDead())
                    return;

                if (_toucher.getClan() == _other.getClan())
                {
                    if (resumeHp != 0)
                    {
                        FightServiceImpl.getInstance().processHpChange(
                                _toucher, _other, resumeHp, false, null);
                    }

                    if (resumeMp != 0)
                    {
                        _other.addMp(resumeMp);

                        FightServiceImpl.getInstance()
                                .processSingleTargetMpChange(_other, true);
                    }
                }
            }
            else if (targetType == ETargetType.MYSELF)
            {
                if (resumeHp != 0)
                {
                    FightServiceImpl.getInstance().processHpChange(_toucher,
                            _toucher, resumeHp, false, null);
                }

                if (resumeMp != 0)
                {
                    _toucher.addMp(resumeMp);

                    FightServiceImpl.getInstance().processSingleTargetMpChange(
                            _toucher, true);
                }
            }
            else if (targetType == ETargetType.ENEMY)
            {
                if (null == _other || !_other.isEnable() || _other.isDead())
                    return;

                if (resumeHp != 0)
                {
                    FightServiceImpl.getInstance().processHpChange(_toucher,
                            _other, -resumeHp, false, magicHarmType);
                }

                if (resumeMp != 0)
                {
                    _other.addMp(-resumeMp);

                    FightServiceImpl.getInstance().processSingleTargetMpChange(
                            _other, false);
                }
            }
            log.info("将要触发:" + additionalSEID);
            if (additionalSEID > 0)
            {
                if (Constant.isSkillUnit(additionalSEID))
                {
                    SkillServiceImpl.getInstance().additionalSkillUnitActive(
                            (HeroPlayer) _toucher,
                            _other,
                            (ActiveSkillUnit) SkillUnitDict.getInstance()
                                    .getSkillUnitRef(additionalSEID), 1, 1);
                }
                else
                {
                    EffectServiceImpl.getInstance().appendSkillEffect(_toucher,
                            _other, additionalSEID);
                }
            }
            if(magicHarmHpValue > 0) {
            	log.info("触发在敌人身上magicHarmHpValue:" + magicHarmHpValue);
                if (null == _other || !_other.isEnable() || _other.isDead())
                    return;
    			int harmValue = CEService.attackMagicHarm(_toucher.getLevel(), 
    					magicHarmHpValue, 
    					_other.getLevel(), 
    					_other.getActualProperty().getMagicFastnessList()
    						.getEMagicFastnessValue(magicHarmType));
                if (harmValue != 0)
                {
                    FightServiceImpl.getInstance().processHpChange(_toucher,
                            _other, -harmValue, false, magicHarmType);
                }
                //edit by zhengl; date: 2011-02-12; note: 添加图片
                SkillServiceImpl.getInstance().sendSingleSkillAnimation(_toucher, 
                		_other, 0, 0, activeAnimationID, activeImageID, (byte)-1, (byte)1, 
                		(byte)-1, heightRelation, (byte)0);
            }
            log.info("将要触发的数量:" + additionalOddsActionUnitList.length);
            if (null != additionalOddsActionUnitList)
            {
            	//法术到底施加到什么人身上会根据该触发法术的目标类型来重新分配.
            	//比如.对自己有益的触发法术在攻击敌人的时候触发.
            	//那么这个法术就不能释放到敌人身上,而应该放到自己身上
            	
            	//新的参与者
            	ME2GameObject newToucher = null;
            	//新的目标
            	ME2GameObject newTarget = null;
            	if (targetType == ETargetType.MYSELF)
            	{
            		newToucher = _other;
            		newTarget = _toucher;
            	}
            	else
            	{
            		newToucher = _toucher;
            		newTarget = _other;
				}
                for (AdditionalActionUnit additionalActionUnit : additionalOddsActionUnitList)
                {
                	log.info("将要触发:"
                            + additionalActionUnit.skillOrEffectUnitID + ";odd is :"
                            + additionalActionUnit.activeOdds);
                    if (Constant.isSkillUnit(additionalActionUnit.skillOrEffectUnitID))
                    {
                        SkillServiceImpl
                                .getInstance()
                                .additionalSkillUnitActive(
                                        (HeroPlayer) newToucher,
                                        newTarget,
                                        (ActiveSkillUnit) SkillUnitDict
                                                .getInstance()
                                                .getSkillUnitRef(
                                                        additionalActionUnit.skillOrEffectUnitID),
                                        additionalActionUnit.activeTimes,
                                        additionalActionUnit.activeOdds);
                    }
                    else
                    {
                    	//edit by zhengl; date: 2011-03-06; note: 添加触发几率
                    	int n = RANDOM.nextInt(100);
                    	if(RANDOM.nextInt(100) 
								<= CEService.oddsFormat(additionalActionUnit.activeOdds))
                    	{
                    		EffectServiceImpl.getInstance().appendSkillEffect(
                    				newToucher, newTarget,
                    				additionalActionUnit.skillOrEffectUnitID);
                    	}
                    }
                }//end for
            }
        }
    }
    
	/**
	 * 改变的值
	 * 
	 * @param _baseValue
	 *            基础值
	 * @param _caluModulus
	 *            计算系数
	 * @param _operator
	 *            运算符号
	 * @return
	 */
	private float valueChanged(float _baseValue, float _caluModulus, EMathCaluOperator _operator) {
		if (_caluModulus > 0) {
			switch (_operator) {
			case ADD: {
				return _caluModulus;
			}
			case DEC: {
				return -_caluModulus;
			}
			case MUL: {
				if (_caluModulus > 1)
					return _baseValue * (_caluModulus - 1);
				else if (_caluModulus < 1)
					return -(_baseValue * (1 - _caluModulus));
			}
			case DIV: {
				return -(_baseValue * ((_caluModulus - 1) / _caluModulus));
			}
			}
		}

		return 0;
	}

    /**
     * 随机数生成器
     */
    private static final Random RANDOM = new Random();
}
