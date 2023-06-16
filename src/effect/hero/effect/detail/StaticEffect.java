package hero.effect.detail;

import java.util.ArrayList;

import hero.effect.Effect;
import hero.effect.service.EffectServiceImpl;
import hero.skill.detail.EHarmType;
import hero.skill.detail.EMathCaluOperator;
import hero.skill.detail.ESpecialStatus;
import hero.skill.detail.ETargetType;
import hero.share.EMagic;
import hero.share.ME2GameObject;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 StaticEffect.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-3-11 下午01:31:53
 * @描述 ：静态效果，主要对玩家的各种属性进行影响，并可能携带某种特殊效果
 */

public class StaticEffect extends Effect
{
    /**
     * 剩余的伤害吸收值(具体数值、或伤害的比例)
     */
    public int                traceReduceHarmValue;

    /**
     * 运算符
     */
    public EMathCaluOperator  caluOperator;

    /**
     * 特殊状态
     */
    public ESpecialStatus     specialStatus;

    /**
     * 特殊状态的等级
     */
    public byte               specialStatusLevel;

    /**
     * 力量
     */
    public float              strength;

    /**
     * 敏捷
     */
    public float              agility;

    /**
     * 耐力
     */
    public float              stamina;

    /**
     * 智力
     */
    public float              inte;

    /**
     * 精神
     */
    public float              spirit;

    /**
     * 幸运
     */
    public float              lucky;

    /**
     * 物理防御
     */
    public float              defense;

    /**
     * 命中等级
     */
    public float              hitLevel;

    /**
     * 物理闪避等级
     */
    public float              physicsDuckLevel;

    /**
     * 物理暴击等级
     */
    public float              physicsDeathblowLevel;

    /**
     * 魔法暴击等级
     */
    public float              magicDeathblowLevel;

    /**
     * 生命值（上限）
     */
    public float              maxHp;

    /**
     * 魔法值（上限）
     */
    public float              maxMp;

    /**
     * 仇恨值
     */
    public float              hate;

    /**
     * 魔法抗性类型
     */
    public EMagic             magicFastnessType;

    /**
     * 魔法抗性改变数值
     */
    public float              magicFastnessValue;

    /**
     * 物理攻击伤害
     */
    public float              physicsAttackHarmValue;

    /**
     * 受的物理攻击伤害
     */
    public float              bePhysicsHarmValue;

    /**
     * 魔法伤害类型
     */
    public EMagic             magicHarmType;

    /**
     * 魔法伤害改变数值
     */
    public float              magicHarmValue;

    /**
     * 承受的魔法伤害类型
     */
    public EMagic             magicHarmTypeBeAttack;

    /**
     * 承受的魔法伤害改变数值
     */
    public float              magicHarmValueBeAttack;

    /**
     * 普通物理攻击间隔时间
     */
    public float              physicsAttackInterval;

    /**
     * 所有技能施放受影响的时间
     */
    public float              allSkillReleaseTime;

    /**
     * 影响施放时间的特殊技能编号列表，可以是多个
     */
    public ArrayList<Integer> specialSkillReleaseTimeIDList;

    /**
     * 特殊技能施放受影响的时间
     */
    public float              specialSkillReleaseTime;

    /**
     * 吸收的伤害类型
     */
    public EHarmType          reduceHarmType;

    /**
     * 是否是免疫伤害
     */
    public boolean            isReduceAllHarm;

    /**
     * 减少的伤害值
     */
    public int                reduceHarm;

    /**
     * 抵抗属性类型
     */
    public ESpecialStatus     resistSpecialStatus;

    /**
     * 抵抗属性几率
     */
    public float              resistSpecialStatusOdds;

    /**
     * 构造
     * 
     * @param _id
     * @param _name
     */
    public StaticEffect(int _id, String _name)
    {
        super(_id, _name);
    }

    /**
     * 设置特殊状态效果等级
     * 
     * @param _specialStatusLevel
     */
    public void setSpecialStatusLevel (byte _specialStatusLevel)
    {
        specialStatusLevel = _specialStatusLevel;
    }

    /**
     * 获取特殊状态效果等级
     * 
     * @return
     */
    public byte getSpecialStatusLevel ()
    {
        return specialStatusLevel;
    }

    /**
     * @author Administrator 改变的内容
     */
    public static class ChangeContent
    {

    }

    /**
     * 激活
     * 
     * @param _host
     */
    @Override
    public boolean build (ME2GameObject _releaser, ME2GameObject _host)
    {
        boolean result = false;
        releaser = _releaser;
        host = _host;
        
        if (keepTimeType == EKeepTimeType.N_A)
        {
            if (aureoleRadiationRange.targetType == ETargetType.ENEMY)
            {
                return true;
            }
        }
        result = EffectServiceImpl.getInstance().changePropertyValue(_host, this, true) 
        	|| EffectServiceImpl.getInstance().appendSpecialStatus(_releaser, _host, this);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hero.effect.N_Effect#heartbeat(hero.share.ME2GameObject)
     */
    @Override
    public boolean heartbeat (ME2GameObject _host)
    {
        // TODO Auto-generated method stub
        if (keepTimeType == EKeepTimeType.N_A)
        {
            if (_host == releaser)
            { // 扫描受影响的内范围目标
                EffectServiceImpl.getInstance().scanAureoleRadiationTarget(
                        releaser, this);
            }
            else
            {
                // 判断是否应该结束，与释放者的距离，是否在队伍中，释放者对象是否有效等
                if (aureoleRadiationTargetList.contains(_host))
                {
                    return EffectServiceImpl.getInstance()
                            .checkAureoleValidity(_host, this);
                }
            }
        }
        else
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

    /**
     * 销毁
     * 
     * @param _host
     */
    @Override
    public void destory (ME2GameObject _host)
    {
        // TODO Auto-generated method stub
        if (keepTimeType == EKeepTimeType.N_A)
        {
            if (_host == releaser)
            {
                aureoleRadiationTargetList.clear();

                if (aureoleRadiationRange.targetType == ETargetType.ENEMY)
                {
                    return;
                }
            }
            else
            {
                aureoleRadiationTargetList.remove(_host);
            }
        }
        else 
        {
			
		}
        //zhengl 标记
        EffectServiceImpl.getInstance().changePropertyValue(_host, this, false);
        EffectServiceImpl.getInstance().clearSpecialStatus(_host, this);
    }

    @Override
    public Effect clone () throws CloneNotSupportedException
    {
        // TODO Auto-generated method stub
        return super.clone();
    }

    /*
     * (non-Javadoc)
     * 
     * @see hero.effect.N_Effect#addCurrentCountTimes(hero.share.ME2GameObject)
     *      增加当前叠加的次数 @param _totalTimes
     */
    public boolean addCurrentCountTimes (ME2GameObject _host)
    {
        if (super.addCurrentCountTimes(_host))
        {
            EffectServiceImpl.getInstance().changePropertyValue(_host, this,
                    true);

            return true;
        }

        return false;
    }

    @Override
    public void radiationTarget (ME2GameObject _releaser, ME2GameObject _host)
    {
        // TODO Auto-generated method stub
        if (keepTimeType == EKeepTimeType.N_A)
        {
            super.radiationTarget(_releaser, _host);

            EffectServiceImpl.getInstance().changePropertyValue(_host, this,
                    true);
            EffectServiceImpl.getInstance().appendSpecialStatus(_releaser,
                    _host, this);
        }
    }
}
