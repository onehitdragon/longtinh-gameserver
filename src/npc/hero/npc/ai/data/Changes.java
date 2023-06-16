package hero.npc.ai.data;

import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.npc.Monster;
import hero.npc.message.MonsterChangesNotify;
import hero.npc.message.MonsterShoutNotify;
import hero.npc.service.NotPlayerServiceImpl;
import hero.share.EMagic;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ChangesData.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-12 上午11:03:49
 * @描述 ：自我变身
 */

public class Changes extends SpecialWisdom
{
    /**
     * 编号
     */
    public int           id;

    /**
     * 变身时的喊话
     */
    public String        shoutContent;

    /**
     * 力量
     */
    public int           strength;

    /**
     * 敏捷
     */
    public int           agility;

    /**
     * 智力
     */
    public int           inte;

    /**
     * 精神
     */
    public int           spirit;

    /**
     * 幸运
     */
    public int           lucky;

    /**
     * 物理防御力
     */
    public int           defense;

    /**
     * 最小物理攻击力
     */
    public int           minAttack;

    /**
     * 最大物理攻击力
     */
    public int           maxAttack;

    /**
     * 魔法伤害类型
     */
    public EMagic        magicType;

    /**
     * 最小魔法伤害值
     */
    public int           minDamageValue;

    /**
     * 最大魔法伤害值
     */
    public int           maxDamageValue;

    /**
     * 新生命值
     */
    public int           newHp;

    /**
     * 神圣抗性
     */
    public int           sanctityFastness;

    /**
     * 暗影抗性
     */
    public int           umbraFastness;

    /**
     * 火抗性
     */
    public int           fireFastness;

    /**
     * 水抗性
     */
    public int           waterFastness;

    /**
     * 土抗性
     */
    public int           soilFastness;

    /**
     * 结束类型
     */
    public byte          endType;

    /**
     * 变身状态持续的时间
     */
    public int           keepTime;

    /**
     * 变身状态下减少的血百分比
     */
    public float         reduceHpPercent;

    /**
     * 变身状态下使用的技能AI列表
     */
    public SkillAIData[] skillAIDataList;

    /**
     * 变身后使用的图片编号（为0时，不改变图片）
     */
    public short         imageID;

    @Override
    public byte getType ()
    {
        // TODO Auto-generated method stub
        return SpecialWisdom.CHANGES;
    }

    @Override
    public void think (Monster _dominator)
    {
        // TODO Auto-generated method stub
        NotPlayerServiceImpl.getInstance().processMonsterChanges(_dominator,
                this);

        _dominator.setChangesStatus(true);
        _dominator.getAI().currentChangesData = this;
        _dominator.getAI().traceDisappearTime = keepTime;
        _dominator.getAI().traceHpWhenChanges = _dominator.getHp();

        _dominator.getAI().currentSkillAIList = AIDataDict.getInstance()
                .buildSkillAIList(skillAIDataList, _dominator.getHPPercent());

        MapSynchronousInfoBroadcast.getInstance().put(_dominator.where(),
                new MonsterChangesNotify(_dominator), false, 0);
    }

    /**
     * 结束变身
     * 
     * @param _dominator AI支配者
     * @param _timeClockWhenChanges 变身时的时间
     * @param _traceHpPercentWhenChanges 变身时的生命剩余百分比
     * @return
     */
    public boolean endChanges (Monster _dominator, long _timeClockWhenChanges,
            int _traceHpWhenChanges)
    {
        if (END_CONDITION_TYPE_OF_TIME == endType)
        {
            if (System.currentTimeMillis() - _timeClockWhenChanges >= keepTime)
            {
                changesArchetype(_dominator, _traceHpWhenChanges);
            }
        }
        else
        {
            if (_traceHpWhenChanges / _dominator.getActualProperty().getHpMax()
                    - _dominator.getHPPercent() >= reduceHpPercent)
            {
                changesArchetype(_dominator, _traceHpWhenChanges);
            }
        }

        return false;
    }

    /**
     * 变身回原型
     * 
     * @param _dominator
     * @param _traceHpWhenChanges
     */
    private void changesArchetype (Monster _dominator, int _traceHpWhenChanges)
    {
        NotPlayerServiceImpl.getInstance().processMonsterChangesToArchetype(
                _dominator, _traceHpWhenChanges, this);
        _dominator.setChangesStatus(false);
        _dominator.getAI().currentSkillAIList = _dominator.getAI().skillAIList;
        MapSynchronousInfoBroadcast.getInstance().put(_dominator.where(),
                new MonsterChangesNotify(_dominator), false, 0);
    }

    /**
     * 结束变身状态条件为时间
     */
    public static final byte END_CONDITION_TYPE_OF_TIME = 1;

    /**
     * 结束变身状态条件为当前血量
     */
    public static final byte END_CONDITION_TYPE_OF_HP   = 2;
}
