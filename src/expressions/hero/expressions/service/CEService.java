package hero.expressions.service;

import hero.share.EObjectLevel;
import org.apache.log4j.Logger;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 CEService.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-15 下午04:20:42
 * @描述 ：提供游戏中所有的计算公式（CalculateExpressions）
 */

public class CEService
{
     private static Logger log = Logger.getLogger(CEService.class);
    /**
     * 判断玩家是否在NPC的攻击范围内
     * 
     * @param _attackerLevelValue 攻击NPC等级类型值
     * @param _attackerLevel 攻击者等级
     * @param _attackerX 攻击者位置X坐标
     * @param _attackerY 攻击者位置Y坐标
     * @param _targetLevel 目标等级
     * @param _targetX 目标位置X坐标
     * @param _targetY 目标位置Y坐标
     * @公式描述：4+[怪物等级－玩家等级]*0.3+怪物类型标识值
     * @补充：-10<[怪物等级－玩家等级]<10
     * @return
     */
    public final static boolean inAttackRange (int _attackerLevelValue,
            int _attackerLevel, int _attackerX, int _attackerY,
            int _targetLevel, int _targetX, int _targetY)
    {
        int absLevelDifference = _attackerLevel - _targetLevel;

        if (absLevelDifference < -10)
        {
            absLevelDifference = -10;
        }
        else if (absLevelDifference > 10)
        {
            absLevelDifference = 10;
        }

        double attackableDistance = (4 + absLevelDifference * 0.04) + 1;
        
        if(attackableDistance >= 9.0)
        {
        	attackableDistance = 9.0;
        }

//        double actualDistance = Math.sqrt(Math.pow(_attackerX - _targetX, 2)
//                + Math.pow(_attackerY - _targetY, 2));

        boolean inDistance = attackableDistance*attackableDistance >= (_attackerX - _targetX)*(_attackerX - _targetX)+(_attackerY - _targetY)*(_attackerY - _targetY);


//        return attackableDistance >= actualDistance;
        return inDistance;
    }

    /**
     * 暂时没用
     * 
     * @param _levelBeforeUpgrade
     * @param _vocationPara
     * @return
     * @公式描述：result =（职业属性计算参数 + b）/（ 3 * b）（这里的除“ / ”需要带入余数）
     * @公式描述：当角色等级小于等于59级时， b = 3 — LV / 20 （其中LV/20为整除）；
     * @公式描述：当角色等级大于等于60级时， b = 1
     */
    public final static int cancel (int _levelBeforeUpgrade, int _vocationPara)
    {
        int result = 0;

        int levelPara = 3 - (int) (_levelBeforeUpgrade / 20);

        if (_levelBeforeUpgrade >= 60)
        {
            levelPara = 1;
        }

        result = (_vocationPara + levelPara) / (levelPara * 3);

        int residue = (_vocationPara + levelPara) % (levelPara * 3);
        int temp = (_levelBeforeUpgrade / 10 + 1) * 10 - residue;

        if (_levelBeforeUpgrade > temp)
        {
            result++;
        }

        return result;
    }

    /**
     * 计算玩家某等级的基本属性
     * 也可用于宠物 
     * @param _playerLevel 玩家等级
     * @param _vocationAttrPara 职业的属性计算参数
     * @return
     * @公式描述：a *（ LV + 145 ）*（ LV + 145 ）/（ LV + 2500 ）
     */
    public final static int playerBaseAttribute (int _playerLevel,
            float _vocationAttrPara)
    {
        return (int) (_vocationAttrPara * (_playerLevel + 145)
                * (_playerLevel + 145) / (_playerLevel + 2500));
    }

    /**
     * 计算耐力换算的生命值
     * 
     * @param _stamina 耐力
     * @param _level 对象的等级
     * @param _objectTypePara 对象的类型参数
     * @return
     * @公式描述：STA *（6 + 0.4 * LV ）  new-->STA *（6 + 0.4 * LV ）*atb
     * @公式描述：精英怪物NPC，ATB = 2；
     * @公式描述 英雄怪物NPC，ATB = 4；
     * @公式描述 王者怪物NPC，ATB = 16；
     * @公式描述 神灵怪物NPC，ATB = 64
     */
    public final static int hpByStamina (int _stamina, int _level, int _atb)
    {
    	//edit by zhengl; date: 2011-02-25; note: 计算玩家生命值添加系数关系.
        return (int) (_stamina * (6 + 0.4 * _level) * _atb);
    }

    /**
     * 计算智力换算的魔法值
     * 
     * @param _inte 智力
     * @param _level 对象的等级
     * @param _objectTypePara 对象的类型参数
     * @return
     * @公式描述：INT *（3 + 0.4 * LV ）* ATB
     * @公式描述：玩家角色、普通怪物NPC，ATB = 1
     * @公式描述：精英怪物NPC，ATB = 2；
     * @公式描述 英雄怪物NPC，ATB = 4；
     * @公式描述 王者怪物NPC，ATB = 16；
     * @公式描述 神灵怪物NPC，ATB = 64
     */
    public final static int mpByInte (int _inte, int _level, int _para)
    {
        return (int) (_inte * _para * (3 + 0.4 * _level) + .5);
    }

    /**
     * 由智力计算的基础法术伤害
     * 
     * @return
     * @公式描述：Migic_show = （INT / 2）
     * @param _inte 智力
     */
    public final static int magicHarmByInte (int _inte)
    {
        return _inte / 2;
    }

    /**
     * 由智力和附加的魔法类型伤害计算的法术伤害
     * 
     * @return
     * @公式描述：Migic_show = （INT / 2）+ ATT _mw
     * @param _inte 智力
     * @param _weaponMagicHarm 武器魔法伤害
     * @return
     */
    public final static int magicHarm (int _inte, int _weaponMagicHarm)
    {
        return _inte / 2 + _weaponMagicHarm;
    }

    /**
     * 基础物理命中率
     * 
     * @return 命中率百分比分子值
     * @公式描述： HIT_show = 75% + 15% * LUCK /（LUCK + 100）+ 1% * WP_per *400 / [ (
     *        LV_self + 20 ) * ( LV_self + 20 ) ]
     * @公式描述：LUCK 攻击方幸运参数
     * @公式描述：WP_per 装备、技能附带的命中参数之和
     * @公式描述：LV_self
     * @param _luck 幸运
     * @param _hitLevel 命中等级
     * @param _level 等级
     */
    public final static float basePhysicsHitOdds (int _luck, int _hitLevel,
            int _level)
    {
        float value = 0.85F + 0.05F * _luck / (_luck + 200) + 0.01F * _hitLevel
                * 400 / ((_level + 20) * (_level + 20));

        if (value > 1)
        {
            value = 1;
        }

        return oddsFormat(value);
    }
    
    /**
     * 幸运换算成命中等级
     * @param _agility
     * @param _luck
     * @return
     */
    public final static short hitLevel(int _luck)
    {
    	short physicsHitLevel = 0;
    	physicsHitLevel = (short)_luck;
    	return physicsHitLevel;
    }

    /**
     * 物理攻击命中率
     * 
     * @return 命中率百分比分子值
     * @公式描述： HIT_per = 75% + 15% * LUCK /（LUCK + 100）— 3 *（LV_en — LV_self）% +
     *        1% * WP_per *400 / [ ( LV_self + 20 ) * ( LV_self + 20 ) ]
     * @公式描述：LUCK 攻击方幸运参数
     * @公式描述：WP_per 装备、技能附带的命中参数之和
     * @公式描述：LV_self
     * @param _luck 攻击方幸运
     * @param _hitLevel 攻击方命中等级
     * @param _attackerLevel 攻击方等级
     * @param _targetLevel 被攻击方等级
     */
    public final static float attackPhysicsHitOdds (int _luck, int _hitLevel,
            int _attackerLevel, int _targetLevel)
    {
        int levelDifference = _targetLevel - _attackerLevel;
        if (levelDifference < 0)
        {
            levelDifference = 0;
        }

        float value = 0.85F + 0.05F * _luck / (_luck + 200) - 3F
                * levelDifference / 100 + 0.01F * _hitLevel * 400
                / ((_attackerLevel + 20) * (_attackerLevel + 20));

        if (value < 0.25)
        {
            value = 0.25F;
        }
        else if (value > 1)
        {
            value = 1;
        }

        return oddsFormat(value);
    }

    /**
     * 基础魔法命中率
     * 
     * @公式描述： HIT_show = 80% + 15% * LUCK /（LUCK + 100）+ 1% * WP_per *400 / [ (
     *        LV_self + 20 ) * ( LV_self + 20 ) ]
     * @公式描述：LUCK 攻击方幸运参数
     * @公式描述：WP_per 装备、技能附带的命中参数之和
     * @公式描述：LV_self
     * @param _luck 攻击方幸运
     * @param _hitLevel 角色魔法命中等级
     * @param _level 角色等级
     * @return 命中率百分比分子值
     */
    public final static float baseMagicHitOdds (int _luck, int _hitLevel,
            int _level)
    {
        float value = 0.9F + 0.05F * _luck / (_luck + 200) + 0.01F * _hitLevel
                * 400 / ((_level + 20) * (_level + 20));

        if (value > 1)
        {
            value = 1;
        }

        return oddsFormat(value);
    }

    /**
     * 魔法攻击命中率
     * 
     * @公式描述： MIC_per = 40% +（40 % — DEF_mic / 30 * LV_en）+ 15% * LUCK /（LUCK +
     *        100）— 3 *（LV_en — LV_self）% + 1% * WP_per * 400 / [ ( LV_self + 20 ) * (
     *        LV_self + 20 ) ]
     * @公式描述：LUCK 攻击方幸运参数
     * @公式描述：WP_per 装备、技能附带的命中参数之和
     * @公式描述：LV_self
     * @param _luck 攻击方幸运
     * @param _hitLevel 攻击方魔法命中等级
     * @param _magicFastness 被攻击方魔法抗性
     * @param _attackerLevel 攻击方等级
     * @param _targetLevel 被攻击方等级
     * @return 命中率百分比分子值
     */
    public final static float attackMagicHitOdds (int _luck, int _hitLevel,
            int _attackerLevel, int _targetLevel, int _targetMagicFastness)
    {
        int levelDifference = _targetLevel - _attackerLevel;

        if (levelDifference < 0)
        {
            levelDifference = 0;
        }

        float value = 0.45F
                + (0.45F - _targetMagicFastness / (30F * _targetLevel)) + 0.05F
                * _luck / (_luck + 200) - 3 * levelDifference * 0.01F + 0.01F
                * _hitLevel * 400
                / ((_attackerLevel + 20) * (_attackerLevel + 20));

        if (value < 0.25)
        {
            value = 0.25F;
        }
        else if (value > 1)
        {
            value = 1;
        }

        return oddsFormat(value);
    }

    /**
     * 基础物理爆击率
     * 
     * @return 爆击率百分比分子值
     * @公式描述：SPAT_show = 1% *（WP_per * 5 + AGI * 3）*（2 * LV_self + 20）/ [（
     *                 LV_self + 35 ）* （ LV_self + 35）]
     * @公式描述：(new)暴击率=1%*(暴击等级*4)*(（2 * LV_self + 40）/ [（ LV_self + 35 ）* （ LV_self + 35）]
     * @公式描述：WP_per ：物理暴击等级
     * @公式描述：AGI 角色敏捷值
     * @公式描述： LV_self 角色等级
     */
    public final static float basePhysicsDeathblowOdds (int _physicsDeathblowLevel, int _roleLevel)
    {
        float value = 0.01F * (_physicsDeathblowLevel * 4) * (2 * _roleLevel + 40)
                / ((_roleLevel + 35) * (_roleLevel + 35));

        if (value > 1)
        {
            value = 1;
        }

        return oddsFormat(value);
    }
    
    /**
     * 敏捷和幸运换算成暴击等级
     * @param _agility
     * @param _luck
     * @return
     */
    public final static short physicsDeathblowLevel(int _agility, int _luck)
    {
    	short deathblowLevel = 0;
    	deathblowLevel = (short)((_agility + _luck)/2);
    	return deathblowLevel;
    }

    /**
     * 物理攻击爆击率
     * 
     * @公式描述：SPAT =1% *（WP_per * 5 + AGI * 3）*（2 * LV_self + 40）/ [（ LV_self +
     *            35 ）* （ LV_self + 35 )] — 2 *（LV_en — LV_self）%
     * @公式描述：WP_per ：物理暴击等级
     * @公式描述：AGI 角色敏捷值
     * @公式描述： LV_self 角色等级
     * @param _agility 攻击方敏捷
     * @param _physicsDeathblowLevel 攻击方物理爆击等级
     * @param _attackerLevel 攻击方等级
     * @param _targetLevel 被攻击方等级
     * @return 爆击率百分比分子值
     */
    public final static float attackPhysicsDeathblowOdds (int _agility,
            int _physicsDeathblowLevel, int _attackerLevel, int _targetLevel)
    {
        int levelDifference = _targetLevel - _attackerLevel;
        if (levelDifference < 0)
        {
            levelDifference = 0;
        }

        float value = 0.01F * (_physicsDeathblowLevel * 4)
                * (2 * _attackerLevel + 40)
                / ((_attackerLevel + 35) * (_attackerLevel + 35)) - 2F
                * levelDifference * 0.01F;

        if (value < 0)
        {
            value = 0;
        }
        else if (value > 0.5F)
        {
            value = 0.5F;
        }

        return oddsFormat(value);
    }
    
    /**
     * 智力和幸运换算成魔法暴击等级
     * @param _agility
     * @param _luck
     * @return
     */
    public final static short magicDeathblowLevel(int _inte, int _luck)
    {
    	short deathblowLevel = 0;
    	deathblowLevel = (short)((_inte + _luck)/2);
    	return deathblowLevel;
    }

    /**
     * 基础魔法爆击率
     * 
     * @return 爆击率百分比分子值
     * @公式描述：SPAT_show = 1% *（WP_per * 5 + AGI * 3）*（2 * LV_self + 20）/ [（
     *                 LV_self + 35 ）* （ LV_self + 35）]
     * @公式描述：WP_per ：魔法暴击等级
     * @公式描述：AGI 角色幸运值
     * @公式描述： LV_self 角色等级
     */
    public final static float baseMagicDeathblowOdds (int _magicDeathblowLevel, int _roleLevel)
    {
        float value = 0.01F * (_magicDeathblowLevel * 4) * (2 * _roleLevel + 40)
                / ((_roleLevel + 35) * (_roleLevel + 35));

        if (value > 1)
        {
            value = 1;
        }

        return oddsFormat(value);
    }

    /**
     * 魔法攻击爆击率
     * 
     * @return 爆击率百分比分子值
     * @公式描述：SPAT_mic = 1% *（WP_per * 5 + LUCK * 3）*（2 * LV_self + 40）/ [（
     *                LV_self + 35 ）* （ LV_self + 35）] — 2*（LV_en — LV_self）%
     * @公式描述：WP_per ：魔法暴击等级
     * @公式描述：AGI 角色幸运值
     * @公式描述： LV_self 角色等级
     */
    public final static float attackMagicDeathblowOdds (int _magicDeathblowLevel, int _attackerLevel, int _targetLevel)
    {
        int levelDifference = _targetLevel - _attackerLevel;
        if (levelDifference < 0)
        {
            levelDifference = 0;
        }

        float value = 0.01F * (_magicDeathblowLevel * 4) * (2 * _attackerLevel + 40)
                / ((_attackerLevel + 35) * (_attackerLevel + 35) - 2 * levelDifference * 0.01F);

        if (value < 0)
        {
            value = 0;
        }
        else if (value > 0.5F)
        {
            value = 0.5F;
        }

        return oddsFormat(value);
    }

    /**
     * 基础物理闪避率
     * 
     * @公式描述：DODGE_show = 1% *（DD_per * 5 + AGI * 2 + LUCK ）*（2 * LV_self + 40）/ [（
     *                  LV_self + 35 ）* （ LV_self + 35）]
     * @公式描述： DD_per 闪避等级
     * @公式描述： LV_self 角色等级
     * @param __agility
     * @param _luck
     * @param _duckLevel
     * @param _roleLevel
     */
    public final static float basePhysicsDuckOdds (int _duckLevel, int _roleLevel)
    {
        float value = 0.01F * (_duckLevel * 4) * (2 * _roleLevel + 40)
                / ((_roleLevel + 35) * (_roleLevel + 35));

        if (value > 1)
        {
            value = 1;
        }

        return oddsFormat(value);
    }
    /**
     * 通过敏捷和幸运计算闪避等级
     * @param _agility 敏捷
     * @param _luck 幸运
     * @return
     */
    public final static short duckLevel (int _agility, int _luck)
    {
    	short result = (short)((_agility + _luck)/12);
    	return result;
    }

    /**
     * 物理攻击闪避率
     * 
     * @公式描述：DODGE =1% *（WP_per * 5 + AGI * 2 + LUCK ）*（2 * LV_self + 40）/ [（
     *             LV_self + 35 ）* （ LV_self + 35 ] — 2 *（LV_en — LV_self）%
     * @公式描述： DD_per 闪避等级
     * @公式描述：AGI 攻击方角色敏捷参数
     * @公式描述：LUCK 攻击方角色闪避参数
     * @公式描述： LV_self 角色等级
     * @param _attackerLevel 攻击方等级
     * @param _targetAgility 被攻击方敏捷
     * @param _targetLuck 被攻击方幸运
     * @param _targetDuckLevel 被攻击方闪避等级
     * @param _targetLevel 被攻击方等级
     * @return 物理攻击闪避率百分比分子值
     */
    public final static float attackPhysicsDuckOdds (int _attackerLevel,
            int _targetAgility, int _targetLucky, int _targetDuckLevel,
            int _targetLevel)
    {
        int levelDifference = _attackerLevel - _targetLevel;
        if (levelDifference < 0)
        {
            levelDifference = 0;
        }

        float value = 0.01F
                * (_targetDuckLevel * 5 + _targetAgility * 1 + _targetLucky)
                * (2 * _targetLevel + 40)
                / ((_targetLevel + 35) * (_targetLevel + 35)) - 2
                * levelDifference * 0.01F;

        if (value < 0)
        {
            value = 0;
        }
        else if (value > 0.5F)
        {
            value = 0.5F;
        }

        return oddsFormat(value);
    }

    /**
     * 最大物理攻击力
     * 
     * @param _strength 力量
     * @param _agility 敏捷
     * @param _vocationParaA 职业参数A
     * @param _vocationParaB 职业参数B
     * @param _vocationParaC 职业参数C
     * @param _weaponMaxAttack 武器最大攻击力
     * @param _weaponPara 武器附带参数
     * @param _attackImmobilityTime 攻击间隔时间
     * @param _attackPara objectTypePara
     * @return
     * @公式描述：ATT_max = [（STR * a + AGI * b）/ 3+（B_ATT_max + ATT_mw * c ）/ TIME ] *
     *               TIME * ATB
     * @公式描述：STR 力量
     * @公式描述：AGI 敏捷
     * @公式描述：B_ATT _max 怪物为自身攻击数据，玩家为持有武器的攻击数据
     * @公式描述：TIME 默认为3,怪物为自身数据，玩家为持有武器的攻击数据
     * @公式描述：ATT_mw 武器附带属性攻击参数
     */
    public final static int maxPhysicsAttack (int _strength, int _agility,
            float _vocationParaA, float _vocationParaB, float _vocationParaC,
            int _weaponMaxAttack, int _weaponPara, float _attackImmobilityTime,
            int _attackPara)
    {
    	int result = 0;
//    	System.out.println("_strength"+_strength);
//    	System.out.println("_agility"+_agility);
//    	System.out.println("_vocationParaA"+_vocationParaA);
//    	System.out.println("_vocationParaB"+_vocationParaB);
//    	System.out.println("_vocationParaC"+_vocationParaC);
//    	System.out.println("_weaponMaxAttack"+_weaponMaxAttack);
//    	System.out.println("_weaponPara"+_weaponPara);
//    	System.out.println("_attackImmobilityTime"+_attackImmobilityTime);
//    	System.out.println("_attackPara"+_attackPara);
    	result = (int) (((_strength * _vocationParaA + _agility * _vocationParaB) / 3 + (_weaponMaxAttack + _weaponPara
                * _vocationParaC)
                / _attackImmobilityTime) * _attackImmobilityTime * _attackPara);
        return result;
    }

    /**
     * 计算最小物理攻击力
     * 
     * @param _strength 力量
     * @param _agility 敏捷
     * @param _vocationParaA 职业参数A
     * @param _vocationParaB 职业参数B
     * @param _vocationParaC 职业参数C
     * @param _weaponMinAttack 武器最小攻击力
     * @param _weaponPara 武器附带参数
     * @param _attackImmobilityTime 攻击间隔时间
     * @param _para objectTypePara
     * @return
     * @公式描述：ATT_max = [（STR * a + AGI * b）/ 3+（B_ATT _max + ATT_mw * c ）/ TIME ] *
     *               TIME * ATB
     * @公式描述：STR 力量
     * @公式描述：AGI 敏捷
     * @公式描述：B_ATT _min 怪物为自身攻击数据，玩家为持有武器的攻击数据
     * @公式描述：TIME 默认为3,怪物为自身数据，玩家为持有武器的攻击数据
     * @公式描述：ATT_mw 武器附带属性攻击参数
     */
    public final static int minPhysicsAttack (int _strength, int _agility,
            float _vocationParaA, float _vocationParaB, float _vocationParaC,
            int _weaponMinAttack, int _weaponPara, float _attackImmobilityTime,
            int _attackPara)
    {
        return (int) (((_strength * _vocationParaA + _agility * _vocationParaB) / 3 + (_weaponMinAttack + _weaponPara
                * _vocationParaC)
                / _attackImmobilityTime)
                * _attackImmobilityTime * _attackPara);
    }

    // /**
    // * 敏捷换算的防御力
    // *
    // * @return
    // * @公式描述：DEF = AGI * a
    // * @公式描述：AGI 敏捷
    // * @公式描述：a 职业加权参数
    // * @param _agility 敏捷
    // * @param _vocationParaA 职业物理防御力参数A
    // * @return
    // */
    // public final static int defenseByAgility (int _agility, int
    // _vocationParaA)
    // {
    // return _agility * _vocationParaA;
    // }

    /**
     * 精神换算的物理防御力
     * 
     * @return
     * @公式描述：DEF =SPR * b
     * @公式描述：SPR 精神
     * @公式描述：b 职业加权参数
     * @param _spirit 精神
     * @param _vocationParaB 职业物理防御力参数B
     * @return
     */
    public final static int defenseBySpirit (int _spirit, int _vocationParaB)
    {
        return _spirit * _vocationParaB;
    }

    /**
     * 计算物理攻击伤害
     * 
     * @param _attackerLevel 攻击方等级
     * @param _attackerPhysicsAttack 攻击方物理攻击力
     * @param _targetLevel 目标等级
     * @param _targetDefense 目标物理防御力
     * @return
     */
    public static final int physicsHarm (int _attackerLevel,
            int _attackerPhysicsAttack, int _targetLevel, int _targetDefense)
    {
        int levelDifference = _attackerLevel - _targetLevel;
        int result = 0;

        if (levelDifference >= 0)
        {
            if (levelDifference > 10)
            {
                levelDifference = 10;
            }
        }
        else if (levelDifference < -10)
        {
            levelDifference = -10;
        }

        float para = 0f;

        if (levelDifference >= 0)
        {
            for (int i = 0; i < levelDifference; i++)
            {
                para += Math.sin((i + 1) * 3.1416 / 10);
            }
        }
        else
        {
            for (int i = 0; i > levelDifference; i--)
            {
                para += Math.sin((i - 1) * 3.1416 / 10);
            }
        }
        result = (int) (_attackerPhysicsAttack * (1 - _targetDefense 
        		/ (_targetDefense + 85f * _targetLevel + 400)) * (1 + 5f * para / 100f));
//        log.info(_attackerPhysicsAttack + ":运算前后伤害:" + result);
        return result;
    }

    /**
     * 计算技能产生的攻击力
     * @公式描述: (new) 人物即时总攻击力(含武器及装备提升)*技能伤害倍数(表格填写)+技能伤害附加值(表格填写) 
     * 
     * @param _sumAttack 人物即时总攻击力(含武器及装备提升)
//     * @param _weaponImmobilityTime 武器攻击间隔时间
     * @param _multiples 武器伤害的倍数
     * @param _valueAdded 武器伤害的倍数
     * @return
     */
    public static final int weaponPhysicsAttackBySkill (int _sumAttack, float _multiples, 
    		int _valueAdded)
    {
        return (int) (_sumAttack * _multiples + _valueAdded);
    }

    /**
     * 计算技能产生的魔法攻击
     * 
     * @return
     * @公式描述：(old)			ATT_mic = MIG_base + Migic_show *（Migic_time + 1）/ 3.5 
     *            (bak)			ATT_mic = MIG_base * LV_now/LV_skill + Migic_show *（Migic_time + 1）/ 3.5 
     *            (2011-02-13)	ATT_mic = MIG_base * (LV_now+LV_skill*2)/(LV_now+LV_skill )+ Migic_show *（Migic_time + 1）/ 3.5 
     * @param _attackerMagicHarm 攻击方基础魔法伤害值
     * @param _skillMagicHarm 使用的技能附带的魔法伤害值
     * @param _releaseTime 技能施放时间（秒）
     * @return
     */
    public final static int magicHarmBySkill (float _attackerMagicHarmValue,
            int _skillMagicHarmValue, float _releaseTime, int _pLevel, int _skLevel)
    {
        return (int) (_skillMagicHarmValue * (_pLevel + _skLevel*2)/(_pLevel + _skLevel) 
        		+ _attackerMagicHarmValue * (_releaseTime + 1) / 3.5F);
    }

    /**
     * 攻击魔法伤害计算
     * 
     * @return
     * @公式描述：HARM_mic = ATT_mic * [ 1 — 30 * DEF_mic /（ 30 * DEF_mic + 85 *
     *                LV_an + 400 ）]* [ 1 + 8 * X% ]
     * @param _attackerLevel 攻击方等级
     * @param _attakerMagicAttack 攻击方魔法攻击力
     * @param _targetLevel 目标等级
     * @param _targetMagicFastness 目标对应的魔法抗性
     * @return
     */
    public final static int attackMagicHarm (int _attackerLevel,
            int _attakerMagicAttack, int _targetLevel, int _targetMagicFastness)
    {
    	int result = 0;
        int levelDifference = _attackerLevel - _targetLevel;

        if (levelDifference >= 10)
        {
            levelDifference = 10;
        }
        else if (levelDifference < -10)
        {
            levelDifference = -10;
        }

        float para = 0f;

        if (levelDifference >= 0)
        {
            for (int i = 0; i < levelDifference; i++)
            {
                para += Math.sin((i + 1) * 3.1416 / 10);
            }
        }
        else
        {
            for (int i = 0; i > levelDifference; i--)
            {
                para += Math.sin((i - 1) * 3.1416 / 10);
            }
        }
        result = (int) (_attakerMagicAttack * (1 - 30 * _targetMagicFastness / (30 * _targetMagicFastness + 85f * _targetLevel + 400)) 
        			* (1 + 8f * para / 100f));
//        System.out.println("输入基础魔攻="+_attakerMagicAttack);
//        System.out.println("输入职业参数="+para);
//        System.out.println("抗性为:" + _targetMagicFastness + "制造伤害:" + result);
        return result;
    }

    /**
     * 恢复型技能效果计算
     * 
     * @公式描述：MIG_base + [ SPR / 2 + ATT _mw * 2 ] *（Migic_time + 1）/ 3.5
     * @公式描述：MIG_base ：技能提供的基础恢复值。（如果是hot型法术，这里为hot技能本身提供的恢复值总和）
     * @公式描述：SPR ：基础精神参数
     * @公式描述：ATT _mw ：武器附带神圣系法术伤害
     * @公式描述：Migic_time ：法术施法时间，瞬发时该值为0
     * @param _skillResumeValue 技能恢复的数值（HP或MP）
     * @param _spiritValue 施法方精神值
     * @param _inteValue 施法方智力值
     * @param _weaponSanctityMagicValue 施法方武器神圣伤害数值
     * @param _releaseTime 技能施法时间（秒）
     * @return
     */
    public final static int magicResume (int _skillResumeValue,
            int _spiritValue, int _inteValue, float _weaponSanctityMagicValue,
            float _releaseTime)
    {
        return (int) (_skillResumeValue + (_spiritValue / 2
                + _weaponSanctityMagicValue * 2 - _inteValue / 2)
                * (_releaseTime + 1) / 3.5F);
    }

    /**
     * 计算爆击伤害
     * 
     * @公式描述：HARM_apat = HARM_normal * 1.5 + HARM_normal * 0.5 * LUCK / 200
     * @param _originalHarmValue 未产生爆击时的伤害值
     * @param _attackerLucky 攻击方的幸运值
     * @return
     */
    public static final int calculateDeathblowHarm (int _originalHarmValue,
            int _attackerLucky)
    {
        return (int) (_originalHarmValue * 1.5 + _originalHarmValue * .5
                * _attackerLucky / 300);
    }

    /**
     * 升级到下一级需要的经验
     * 
     * @param _level
     * @return
     * @公式描述：EXP_LVUP_n = EXP_LVUP_(n - 1)×1.1 + 40 * n
     */
    public static final int expToNextLevel (int _currrentLevel,
            float _expOfCurrentLevel)
    {
        return (int) (_expOfCurrentLevel * 1.13F + 80 * (_currrentLevel - 1));
    }

    /**
     * 计算从N级升级到N+1级需要的经验
     * 
     * @param _level 当前等级
     * @return
     * @公式描述：EXP_LVUP_n = EXP_LVUP_(n - 1)×1.1 + 10 * n
     */
    public static final int totalUpgradeExp (int _level)
    {
        if (_level == 1)
        {
            return 200;
        }
        else
        {
            return (int) (totalUpgradeExp(_level - 1) * 1.13F + 80 * (_level - 1));
        }
    }

    /**
     * 计算怪物基础经验值
     * 
     * @公式描述： 	(old)EXP_base = [ 40 + 5 *（ LV — 1）] * ATB
     * 				(new)EXP_base = [ 3 + 6 *（ LV — 1）] * ATB
     * @param _monsterLevel 怪物等级
     * @param _typePara 怪物类型计算参数
     * @return
     */
    public static final int monsterBaseExperience (int _monsterLevel,
            EObjectLevel _monsterType)
    {
        return (3 + 6 * (_monsterLevel - 1)) * _monsterType.getBaseExperiencePara();
    }

    /**
     * <p>计算玩家杀死怪物后获得的经验值</p>
     * edit:	zhengl
     * date:	2011-01-21
     * note:	修改团队经验加成公式.
     * 
     * @param _groupMemberNumber 玩家数量（队伍或者团队中，1时为单人或者队伍中其他人不在同一个地图中）
     * @param _playerLevel 队伍或团队中最高成员等级，无队伍时则是杀死怪物的玩家等级
     * @param _monsterLevel 被杀死的怪物等级
     * @param _monsterBaseExp 被杀死的怪物的基础经验
     * @return
     */
    public static final int getExperienceFromMonster (int _groupMemberNumber,
            int _playerLevel, int _monsterLevel, int _monsterBaseExp)
    {
        if (_groupMemberNumber == 1)
        {
            if (_playerLevel < _monsterLevel)
            {
                if (_monsterLevel - _playerLevel > 50)
                {
                	//如果怪物等级超过玩家等级50级.杀死怪物获得经验1
                    return 1;
                }
                else
                {
                    return (int) (_monsterBaseExp * (1 - (_monsterLevel - _playerLevel) * 2 * 0.01F));
                }
            }
            else if (_playerLevel == _monsterLevel)
            {
                return (int) (_monsterBaseExp * (1 - (_monsterLevel - _playerLevel) * 2 * 0.01F));
            }
            else
            {
                if (_playerLevel - _monsterLevel <= 10)
                {
                    return (int) (_monsterBaseExp * (1 - (_playerLevel - _monsterLevel) * 8 * 0.01F));
                }
                else
                {
                	//如果玩家等级超过怪物等级10级.杀死怪物获得经验1
                    return 1;
                }
            }
        }
        else
        {
            if (_playerLevel < _monsterLevel)
            {
                if (_monsterLevel - _playerLevel > 50)
                {
                	//如果怪物等级超过玩家等级50级.杀死怪物获得经验1
                    return 1;
                }
                else
                {
//                    return (int) (_monsterBaseExp * _groupMemberNumber * 0.05F + _monsterBaseExp
//                            * (1 - (_monsterLevel - _playerLevel) * 2 * 0.01F)
//                            / _groupMemberNumber);
                	return (int) (_monsterBaseExp * _groupMemberNumber * 0.05F + _monsterBaseExp
                			* (1 - (_monsterLevel - _playerLevel) * 2 * 0.01F)
                			/ (_groupMemberNumber+(_groupMemberNumber*0.09)));
                }
            }
            else if (_playerLevel == _monsterLevel)
            {
//            	return (int) (_monsterBaseExp * _groupMemberNumber * 0.05F + _monsterBaseExp
//            			* (1 - (_monsterLevel - _playerLevel) * 2 * 0.01F)
//            			/ _groupMemberNumber);
            	return (int) (_monsterBaseExp * _groupMemberNumber * 0.05F + _monsterBaseExp
            			* (1 - (_monsterLevel - _playerLevel) * 2 * 0.01F)
            			/ (_groupMemberNumber+(_groupMemberNumber*0.09)));
            }
            else
            {
                if (_playerLevel - _monsterLevel <= 10)
                {
                    return (int) (_monsterBaseExp * _groupMemberNumber * 0.05F + _monsterBaseExp
                            * (1 - (_playerLevel - _monsterLevel) * 8 * 0.01F)
                            / _groupMemberNumber);
                }
                else
                {
                	//如果玩家等级超过怪物等级10级.杀死怪物获得经验1
                    return 1;
                }
            }
        }
    }

    /**
     * 计算每3秒恢复的生命值
     * 
     * @公式描述：CURE_rp = HP * SPR / [ 300 +（LV * LV）/ 4 ]
     * @param _playerLevel 玩家等级
     * @param _spirit 精神
     * @param _vocationStaminaPara 与职业相关的耐力计算参数
     * @return
     */
    public static final int hpResumeAuto (int _playerLevel, int _spirit,
            float _vocationStaminaPara)
    {
    	//edit by zhengl; date: 2011-03-13; note: 生命值恢复修改
    	int result = 0;
    	result = (int)(CEService.hpByStamina(
        		CEService.playerBaseAttribute(_playerLevel, _vocationStaminaPara), 
        		_playerLevel,
        		1/*edit by zhengl; date: 2011-02-24; note:待完善,暂时填1*/
        		) * 0.01);  // 按血量的的 1%来恢复
    	//add by zhengl; date: 2011-05-11; note: 至少返回1
    	if (result <= 0) {
    		result = 1;
		}
        return result;
    }

    /**
     * 非战斗状态下每3秒恢复的魔法值
     * 
     * @公式描述：CURE_rp = MP * SPR / [ 300 +（LV * LV）/ 2 ]
     * @param _playerLevel 玩家等级
     * @param _spirit 精神
     * @param _vocationStaminaPara 与职业相关的智力计算参数
     * @return
     */
    public static final int mpResumeAuto (int _playerLevel, int _spirit,
            float _vocationIntePara)
    {
    	//edit by zhengl; date: 2011-02-17; note: 修改代码结构便于调试与查看
    	int result = 0;
    	int mp = CEService.mpByInte(
    			CEService.playerBaseAttribute(_playerLevel, _vocationIntePara), 
    			_playerLevel,
                EObjectLevel.NORMAL.getMpCalPara() );
    	//edit by zhengl; date: 2011-02-24; note: 临时修改回魔速度
//    	result = (int) ( mp * _spirit / (300 + 2F * (_playerLevel * _playerLevel)) );
    	result = (int) (mp * 0.01);
    	//add by zhengl; date: 2011-05-11; note: 至少返回1
    	if (result <= 0) {
    		result = 1;
		}
    	return result;
    }

    /**
     * 战斗中每3秒恢复的魔法值
     * 
     * @公式描述：Magic_rb = Magic_rp / 2.5
     * @param _playerLevel 玩家等级
     * @param _spirit 精神
     * @param _vocationStaminaPara 与职业相关的智力计算参数
     * @return
     */
    public static final int mpResumeAutoInFighting (int _resumeMpNoneFight)
    {
        return (int) (_resumeMpNoneFight / 2.5F);
    }

    /**
     * 计算装备修理费用
     * 
     * @param _price 装备价格
     * @param _currentDurabilityPoint 当前耐久度
     * @param _maxDurabilityPoint 最大耐久度
     * @return
     */
    public static final float repairChargeOfEquipment (int _price,
            int _currentDurabilityPoint, int _maxDurabilityPoint)
    {
        return _price / 4F * (_maxDurabilityPoint - _currentDurabilityPoint)
                / _maxDurabilityPoint;
    }

    /**
     * 计算装备出售价格
     * 
     * @param _price 装备价格
     * @param _currentDurabilityPoint 当前耐久度
     * @param _maxDurabilityPoint 最大耐久度
     * @return
     */
    public static int sellPriceOfEquipment (int _price,
            int _currentDurabilityPoint, int _maxDurabilityPoint)
    {
    	//edit by zhengl; date: 2011-03-09; note: 3F更改为12F
    	//edit by zhengl; date: 2011-04-11; note: 12f-->17.5F
        float money = _price / 17.5F * _currentDurabilityPoint/ _maxDurabilityPoint + 0.5F;

        return money < 1 ? 1 : (int) money;
    }

    /**
     * 完成任务实际获得经验
     * 
     * @param _playerLevel 玩家等级
     * @param _taskNeedLevel 任务接受需要等级
     * @param _OriginalExperience 任务原始奖励经验
     * @return
     */
    public static final int taskExperience (short _playerLevel,
            short _taskLevel, int _OriginalExperience)
    {
        if (_playerLevel < _taskLevel)
        {
            return _OriginalExperience;
        }

        int exp = (int) (_OriginalExperience * (1 - 0.04F * (_playerLevel - _taskLevel)));

        return exp < 10 ? 10 : exp;
    }

    /**
     * 几率格式化（保留小数点后2位）
     * 
     * @param _value
     * @return 百分比分子值
     */
    public static float oddsFormat (float _value)
    {
        return Math.round((int) (_value * 10000)) / 100.0F;
    }

    public static void main (String[] args)
    {
    }
}
