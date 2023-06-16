package hero.effect;

import java.util.ArrayList;

import hero.skill.detail.AdditionalActionUnit;
import hero.skill.detail.ETargetRangeType;
import hero.skill.detail.ETargetType;
import hero.share.ME2GameObject;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 N_Effect.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-3-11 下午01:27:11
 * @描述 ：效果类（以图标形式表现在客户端，与时间相关，对基础属性、动态属性、行为等产生临时改变）
 */

public abstract class Effect implements Cloneable
{
    /**
     * 编号
     */
    public int                      ID;

    /**
     * 名称
     */
    public String                   name;

    /**
     * 在同类效果中的等级
     */
    public short                    level;

    /**
     * 特质等级
     */
    public byte                     featureLevel;
    
    /**
     * 特效图片
     */
    public short                    effectImageID;
    
    /**
     * 特效动画
     */
    public short                    effectAnimationID;
    /**
     * 层级关系
     */
    public short                    tierRelation;

    /**
     * 图标
     */
    public short                    iconID;

    /**
     * 累计叠加次数
     */
    public short                    totalTimes;

    /**
     * 当前累计叠加次数
     */
    public short                    currentCountTimes;

    /**
     * 剩余时间，秒（相对于非光环效果）
     */
    public short                    traceTime;

    /**
     * 持续时间，秒
     */
    public short                    keepTime;

    /**
     * 死亡后是否消失
     */
    public boolean                  isClearAfterDie;

    /**
     * 是否能在战斗状态中施放
     */
    public boolean                  canReleaseInFight;

    /**
     * 光环辐射范围
     */
    public AureoleRadiationRange    aureoleRadiationRange;

    /**
     * 替换编号（以免有些BUFF在不同职业和等级中发生重叠）
     */
    public int                      replaceID;

    /**
     * 品质
     */
    public EffectTrait              trait;

    /**
     * 特质
     */
    public EffectFeature            feature;

    /**
     * 持续时间类型
     */
    public EKeepTimeType            keepTimeType;

    /**
     * 技能描述
     */
    public String                   desc;

    /**
     * 产生时调用或触发时调用的技能或效果（效果或技能编号/几率分子/共可能调用的次数）
     */
    public AdditionalActionUnit[]   additionalOddsActionUnitList;

    /**
     * 施放者
     */
    public ME2GameObject            releaser;

    /**
     * 宿主类型（玩家、怪物）
     */
    public ME2GameObject            host;

    /**
     * 受光环影响的目标列表（玩家/怪物），当此效果为光环类型时，此属性有效，与hostID呼应
     */
    public ArrayList<ME2GameObject> aureoleRadiationTargetList;
    
    /**
     * 特效外观类型.
     */
    public byte                     viewType;
    /**
     * 特效外观图片
     */
    public short                    imageID;
    /**
     * 特效外观动画
     */
    public short                    animationID;

    /**
     * 构造
     * 
     * @param _id
     * @param _name
     */
    public Effect(int _id, String _name)
    {
        ID = _id;
        name = _name;
        canReleaseInFight = true;
        totalTimes = 1;
        currentCountTimes = 1;
        desc = "";
    }

    /**
     * 激活
     * 
     * @param _releaser
     * @param _host
     * @return 是否成功作用
     */
    public abstract boolean build (ME2GameObject _releaser, ME2GameObject _host);

    /**
     * 光环辐射
     * 
     * @param _releaser
     * @param _host
     */
    public void radiationTarget (ME2GameObject _releaser, ME2GameObject _target)
    {
        if (!aureoleRadiationTargetList.contains(_target))
        {
            aureoleRadiationTargetList.add(_target);
        }
    }

    /**
     * 有效性检查以及作用计算
     * 
     * @param _host
     * @return 是否继续有效
     */
    public abstract boolean heartbeat (ME2GameObject _host);

    /**
     * 销毁
     * 
     * @param _host
     */
    public abstract void destory (ME2GameObject _host);

    /**
     * 增加当前叠加的次数
     * 
     * @param _totalTimes
     */
    public boolean addCurrentCountTimes (ME2GameObject _host)
    {
        if (currentCountTimes < totalTimes)
        {
            currentCountTimes++;

            return true;
        }

        return false;
    }

    /**
     * 降低剩余时间
     * 
     * @param _time 减少的时间，秒
     * @return 剩余的时间
     */
    public short dropTraceTime (short _time)
    {
        traceTime -= _time;

        if (traceTime < 0)
        {
            traceTime = 0;
        }

        return traceTime;
    }

    /**
     * 重置剩余时间
     */
    public void resetTraceTime ()
    {
        traceTime = keepTime;
    }

    /**
     * 设置持续时间
     * 
     * @param _time
     */
    public void setKeepTime (short _time)
    {
        if (_time >= 0)
        {
            keepTime = _time;
            traceTime = keepTime;
        }
    }

    /**
     * 增加持续时间
     * 
     * @param _time
     */
    public void addKeepTime (short _time)
    {
        keepTime += _time;
        traceTime = keepTime;

        if (keepTime < 3)
        {
            keepTime = 3;
            traceTime = 3;
        }
    }

    /**
     * 添加光环作用目标编号
     * 
     * @param _objectID 目标编号
     * @return 是否添加成功
     */
    protected boolean addAureoleRadiationTarget (ME2GameObject _target)
    {
        if (!aureoleRadiationTargetList.contains(_target))
        {
            return aureoleRadiationTargetList.add(_target);
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    public Effect clone () throws CloneNotSupportedException
    {
        Effect effect = (Effect) super.clone();

        if (keepTimeType == EKeepTimeType.N_A)
        {
            effect.aureoleRadiationTargetList = new ArrayList<ME2GameObject>();
        }

        return effect;
    }

    /**
     * @author Administrator 效果品质
     */
    public static enum EffectTrait
    {
        /**
         * 增益性
         */
        BUFF((byte) 1),
        /**
         * 减益性
         */
        DEBUFFF((byte) 2);

        byte value;

        private EffectTrait(byte _value)
        {
            value = _value;
        }

        public byte value ()
        {
            return value;
        }
    }

    /**
     * @author Administrator 效果特质（争取驱散技能有效）
     */
    public static enum EffectFeature
    {
    	/**
    	 * 全部
    	 */
    	ALL("ALL"),
        /**
         * 伤痕
         */
        SCAR("伤痕"),
        /**
         * 毒液
         */
        VENOM("毒液"),
        /**
         * 火焰
         */
        FIRE("火焰"),
        /**
         * 法术
         */
        MAGIC("法术"),
        /**
         * 咒语
         */
        INCANTATION("咒语"),
        /**
         * 特殊
         */
        SPECIAL("特殊"),
        /**
         * 坐骑
         */
        MOUNT("坐骑"),
        /**
         * 无
         */
        NONE("");

        /**
         * 名称
         */
        private String name;

        /**
         * 构造
         * 
         * @param _name
         */
        private EffectFeature(String _name)
        {
            name = _name;
        }

        /**
         * 获取名称
         * 
         * @return
         */
        public String getName ()
        {
            return name;
        }

        /**
         * 根据效果名称获取效果类型
         * 
         * @param _desc 效果类型名称
         * @return
         */
        public static EffectFeature get (String _desc)
        {
            for (EffectFeature type : EffectFeature.values())
            {
                if (type.name.equals(_desc))
                {
                    return type;
                }
            }

            return null;
        }
    }

    public static enum EKeepTimeType
    {
        /**
         * 一直存在，称之为光环
         */
        N_A((byte) 1),
        /**
         * 有限时间
         */
        LIMITED((byte) 2);

        byte value;

        private EKeepTimeType(byte _value)
        {
            value = _value;
        }

        public byte value ()
        {
            return value;
        }
    }

    /**
     * @author Administrator 光环辐射目标范围
     */
    public static class AureoleRadiationRange
    {
        public AureoleRadiationRange(ETargetType _targetType)
        {
            targetType = _targetType;
        }

        /**
         * 作用的目标类型
         */
        public ETargetType      targetType;

        /**
         * 作用的目标范围类型
         */
        public ETargetRangeType targetRangeType;

        /**
         * 区域范围的宽度
         */
        public byte             rangeRadiu;
    }
}
