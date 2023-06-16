package hero.effect.detail;

import java.util.Random;

import hero.effect.Effect;
import hero.effect.service.EffectServiceImpl;
import hero.expressions.service.CEService;
import hero.skill.detail.EHarmType;
import hero.share.EMagic;
import hero.share.ME2GameObject;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 DynamicEffect.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-3-11 下午01:33:12
 * @描述 ：动态效果，在效果存在后的某个时间点产生实际作用，主要恢复和减少生命值、魔法值
 */

public class DynamicEffect extends Effect
{
    /**
     * 每次执行造成的伤害值
     */
    public int            hpHarmValue;

    /**
     * 每次执行恢复的生命值
     */
    public int            hpResumeValue;

    /**
     * 每次执行造成的伤害值
     */
    public int            mpHarmValue;

    /**
     * 每次执行恢复的生命值
     */
    public int            mpResumeValue;

    /**
     * 产生效果时是否是暴击
     */
    public boolean        isDeathblow;

    /**
     * 作用时间类型（效果产生时，间隔性，效果结束时）
     */
    public ActionTimeType actionTimeType;

    /**
     * 造成的伤害类型
     */
    public EHarmType      harmType;

    /**
     * 伤害魔法类型
     */
    public EMagic         harmMagicType;

    /**
     * 减少的生命值总量
     */
    public int            hpHarmTotal;

    /**
     * 减少的魔法值
     */
    public int            mpHarmTotal;

    /**
     * 恢复生命值
     */
    public int            hpResumeTotal;

    /**
     * 恢复魔法值
     */
    public int            mpResumeTotal;

    /**
     * 构造
     * 
     * @param _id
     * @param _name
     */
    public DynamicEffect(int _id, String _name)
    {
        super(_id, _name);
    }

    public static enum ActionTimeType
    {
        BEGIN("开始时"), INTERVAL("三秒"), END("结束时");

        String desc;

        ActionTimeType(String _desc)
        {
            desc = _desc;
        }

        public static ActionTimeType get (String _desc)
        {
            for (ActionTimeType actionTimeType : ActionTimeType.values())
            {
                if (actionTimeType.desc.equals(_desc))
                {
                    return actionTimeType;
                }
            }

            return null;
        }
    }

    @Override
    public boolean build (ME2GameObject _releaser, ME2GameObject _host)
    {
        // TODO Auto-generated method stub
        releaser = _releaser;
        host = _host;

        if (RANDOM.nextInt(100) <= releaser.getActualProperty()
                .getMagicDeathblowOdds())
        {
            isDeathblow = true;
        }

        if (hpHarmTotal != 0)
        {
            if (harmType == EHarmType.PHYSICS)
            {
                hpHarmValue = hpHarmTotal;

                if (keepTimeType == EKeepTimeType.LIMITED)
                {
                    hpHarmValue = CEService.physicsHarm(
                    		_releaser.getLevel(),
                            _releaser.getActualProperty().getActualPhysicsAttack() + hpHarmTotal, 
                            _host.getLevel(), 
                            _host.getActualProperty().getDefense());

                    if (isDeathblow)
                    {
                        hpHarmValue = CEService.calculateDeathblowHarm(
                                hpHarmValue, releaser.getActualProperty()
                                        .getLucky());
                    }

                    if (actionTimeType == ActionTimeType.INTERVAL)
                        hpHarmValue = hpHarmValue / (keepTime / 3);
                }
            }
            else
            {
                hpHarmValue = hpHarmTotal;

                if (keepTimeType == EKeepTimeType.LIMITED)
                {
                    hpHarmValue = CEService.magicHarmBySkill(_releaser
                            .getActualProperty().getBaseMagicHarmList()
                            .getEMagicHarmValue(harmMagicType), hpHarmTotal, 0, _host.getLevel(), level);

                    if (isDeathblow)
                    {
                        hpHarmValue = CEService.calculateDeathblowHarm(
                                hpHarmValue, releaser.getActualProperty()
                                        .getLucky());
                    }
                    //add by zhengl; date: 2011-04-23; note: 添加dot伤害加成 .
//        			System.out.println("运算前伤害" + harmValue);
                    hpHarmValue += hpHarmValue * _releaser.getActualProperty().getAdditionalMagicHarmScale(
        					this.harmMagicType);
//        			System.out.println("施法者法术加成之后伤害:" + harmValue);
                    hpHarmValue += _releaser.getActualProperty().getAdditionalMagicHarm(
        					this.harmMagicType);
//        			System.out.println("施法者法术附加之后伤害:" + harmValue);
                    hpHarmValue += hpHarmValue * _host.getActualProperty().getAdditionalMagicHarmScaleBeAttack(
        					this.harmMagicType);
//        			System.out.println("被攻击者法术加成之后伤害:" + harmValue);
                    hpHarmValue += _host.getActualProperty().getAdditionalMagicHarmBeAttack(
        					this.harmMagicType);
//        			System.out.println("被攻击者法术附加之后伤害:" + harmValue);
                    if (actionTimeType == ActionTimeType.INTERVAL)
                        hpHarmValue = hpHarmValue / (keepTime / 3);
                }
            }
        }

        if (mpHarmTotal != 0)
        {
            mpHarmValue = mpHarmTotal;

            if (keepTimeType == EKeepTimeType.LIMITED
                    && actionTimeType == ActionTimeType.INTERVAL)
            {
                mpHarmValue = mpHarmTotal / (keepTime / 3);
            }
        }

        if (hpResumeTotal != 0)
        {
            hpResumeValue = hpResumeTotal;

            if (keepTimeType == EKeepTimeType.LIMITED)
            {
                hpResumeValue = CEService.magicResume(hpResumeTotal, _releaser
                        .getActualProperty().getSpirit(), _releaser
                        .getActualProperty().getInte(), releaser
                        .getActualProperty().getBaseMagicHarmList()
                        .getEMagicHarmValue(EMagic.SANCTITY), 0);

                if (isDeathblow)
                {
                    hpResumeValue = CEService.calculateDeathblowHarm(
                            hpResumeValue, releaser.getActualProperty()
                                    .getLucky());
                }

                if (actionTimeType == ActionTimeType.INTERVAL)
                    hpResumeValue = hpResumeValue / (keepTime / 3);
            }
        }

        if (mpResumeTotal != 0)
        {
            mpResumeValue = mpResumeTotal;

            if (keepTimeType == EKeepTimeType.LIMITED
                    && actionTimeType == ActionTimeType.INTERVAL)
                mpResumeValue = mpResumeTotal / (keepTime / 3);
        }

        return true;
    }

    @Override
    public boolean heartbeat (ME2GameObject _host)
    {
        // TODO Auto-generated method stub
        if (keepTimeType == EKeepTimeType.N_A)
        {
            if (_host == releaser)
            { // 扫描受影响的范围目标
                EffectServiceImpl.getInstance().scanAureoleRadiationTarget(
                        releaser, this);
            }
            else
            {
                // 判断是否应该结束，与释放者的距离，是否在队伍中，释放者对象是否有效等
                if (aureoleRadiationTargetList.contains(_host))
                {
                    return EffectServiceImpl.getInstance().checkAureoleValidity(_host, this);
                }
                else
                {
                    EffectServiceImpl.getInstance().removeEffect(_host, this, false, true);

                    return false;
                }
            }
        }

        if (actionTimeType == ActionTimeType.INTERVAL)
        {
            EffectServiceImpl.getInstance().executeDynamicEffect(this, _host);
        }

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

        if (actionTimeType == ActionTimeType.END)
        {
            EffectServiceImpl.getInstance().executeDynamicEffect(this, _host);
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
        if (keepTimeType == EKeepTimeType.N_A)
        {
            super.radiationTarget(_releaser, _host);
        }
    }

    /**
     * 随机数生成器
     */
    private static final Random RANDOM = new Random();
}
