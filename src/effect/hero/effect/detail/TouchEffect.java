package hero.effect.detail;

import hero.effect.Effect;
import hero.effect.service.EffectServiceImpl;
import hero.fight.service.FightServiceImpl;
import hero.player.HeroPlayer;
import hero.skill.detail.AdditionalActionUnit;
import hero.skill.detail.EHarmType;
import hero.skill.detail.ETargetType;
import hero.skill.detail.ETouchType;
import hero.skill.dict.SkillUnitDict;
import hero.skill.service.SkillServiceImpl;
import hero.skill.unit.ActiveSkillUnit;
import hero.share.Constant;
import hero.share.EMagic;
import hero.share.ME2GameObject;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 TouchEffect.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-3-11 下午01:34:39
 * @描述 ：触发型效果，作用在目标身上，因为后期的某个动作或被动作而产生实际作用，恢复或减少生命值、魔法值等
 */

public class TouchEffect extends Effect
{
    /**
     * 触发方式
     */
    public ETouchType  touchType;

    /**
     * 触发几率
     */
    public float       touchOdds;

    /**
     * 目标类型
     */
    public ETargetType targetType;

    /**
     * 造成的伤害类型
     */
    public EHarmType   harmType;

    /**
     * 伤害魔法类型
     */
    public EMagic      harmMagicType;

    /**
     * 减少的生命值
     */
    public int         hpHarmValue;

    /**
     * 减少的魔法值
     */
    public int         mpHarmValue;

    /**
     * 恢复生命值
     */
    public int         hpResumeValue;

    /**
     * 恢复魔法值
     */
    public int         mpResumeValue;

    /**
     * 构造
     * 
     * @param _id
     * @param _name
     */
    public TouchEffect(int _id, String _name, ETouchType _touchType,
            float _touchOdds)
    {
        super(_id, _name);
        touchType = _touchType;
        touchOdds = _touchOdds;
    }

    @Override
    public boolean build (ME2GameObject _releaser, ME2GameObject _host)
    {
        // TODO Auto-generated method stub
        releaser = _releaser;
        host = _host;

        return true;
    }

    @Override
    public boolean heartbeat (ME2GameObject _host)
    {
        // TODO Auto-generated method stub
        if (keepTimeType == EKeepTimeType.LIMITED)
        {
            traceTime -= 3;

            if (traceTime <= 0)
            {
                EffectServiceImpl.getInstance().removeEffect(_host, this, true, true);

                return false;
            }
        }

        return true;
    }

    @Override
    public void destory (ME2GameObject _host)
    {
        // TODO Auto-generated method stub
        if (keepTimeType == EKeepTimeType.N_A)
        {
            if (_host == releaser)
            {
                aureoleRadiationTargetList.clear();
            }
            else
            {
                aureoleRadiationTargetList.remove(_host);
            }
        }
    }

    @Override
    public Effect clone () throws CloneNotSupportedException
    {
        // TODO Auto-generated method stub
        return super.clone();
    }

    @Override
    public void radiationTarget (ME2GameObject _releaser, ME2GameObject _host)
    {
        // TODO Auto-generated method stub

    }

    /**
     * 被触发
     * 
     * @param _host 效果宿主
     * @param _other 触发者
     * @param _touchType 触发方式
     * @param _isSkillTouch 触发方式
     */
    public void touch (HeroPlayer _host, ME2GameObject _other,
            ETouchType _touchType, boolean _isSkillTouch, EMagic _accMagic)
    {
        if (touchType.canTouch(_touchType, _isSkillTouch))
        {
            if (targetType == ETargetType.FRIEND)
            {
                if (null == _other || !_other.isEnable() || _other.isDead())
                    return;

                if (_host.getClan() == _other.getClan())
                {
                    if (hpResumeValue != 0)
                    {
                        FightServiceImpl.getInstance().processHpChange(_host,
                                _other, hpResumeValue, false, null);
                    }

                    if (mpResumeValue != 0)
                    {
                        _other.addMp(mpResumeValue);

                        FightServiceImpl.getInstance()
                                .processSingleTargetMpChange(_other, true);
                    }
                }
            }
            else if (targetType == ETargetType.MYSELF)
            {
                if (hpResumeValue != 0)
                {
                    FightServiceImpl.getInstance().processHpChange(_host,
                            _host, hpResumeValue, false, null);
                }

                if (mpResumeValue != 0)
                {
                    _host.addMp(mpResumeValue);

                    FightServiceImpl.getInstance().processSingleTargetMpChange(
                            _host, true);
                }
            }
            else if (targetType == ETargetType.ENEMY)
            {
                if (null == _other || !_other.isEnable() || _other.isDead())
                    return;

                if (hpHarmValue != 0)
                {
                    FightServiceImpl.getInstance().processHpChange(_host,
                            _other, -hpHarmValue, false, _accMagic);
                }

                if (mpHarmValue != 0)
                {
                    _other.addMp(-mpHarmValue);

                    FightServiceImpl.getInstance().processSingleTargetMpChange(
                            _other, false);
                }
            }

            if (null != additionalOddsActionUnitList)
            {
                for (AdditionalActionUnit additionalActionUnit : additionalOddsActionUnitList)
                {
                    if (Constant
                            .isSkillUnit(additionalActionUnit.skillOrEffectUnitID))
                    {
                        SkillServiceImpl
                                .getInstance()
                                .additionalSkillUnitActive(
                                        _host,
                                        _other,
                                        (ActiveSkillUnit) SkillUnitDict
                                                .getInstance()
                                                .getSkillUnitRef(
                                                        additionalActionUnit.skillOrEffectUnitID),
                                        additionalActionUnit.activeTimes,
                                        additionalActionUnit.activeOdds);
                    }
                    else
                    {
                        EffectServiceImpl.getInstance().appendSkillEffect(
                                _host, _other,
                                additionalActionUnit.skillOrEffectUnitID);
                    }
                }
            }
        }
    }
}
