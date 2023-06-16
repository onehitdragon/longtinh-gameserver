package hero.share;

import java.util.Random;

import hero.expressions.service.CEService;
import hero.item.EquipmentInstance;
import hero.item.Weapon;
import hero.npc.Monster;
import hero.player.HeroPlayer;
import hero.share.service.MagicDamage;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ObjectProperty.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-23 下午01:46:36
 * @描述 ：
 */

public class ObjectProperty
{
    private static Random     random = new Random();

    /**
     * 宿主
     */
    private ME2GameObject     host;

    /**
     * 最大生命值、最大魔法值
     */
    private int               maxHp, maxMp;

    /**
     * 物理防御
     */
    private int               defense;

    /**
     * 力量
     */
    private int               strength;

    /**
     * 敏捷
     */
    private int               agility;

    /**
     * 耐力
     */
    private int               stamina;

    /**
     * 智力
     */
    private int               inte;

    /**
     * 精神
     */
    private int               spirit;

    /**
     * 幸运
     */
    private int               lucky;

    /**
     * 物理爆击等级
     */
    private short             physicsDeathblowLevel;

    /**
     * 魔法爆击等级
     */
    private short             magicDeathblowLevel;

    /**
     * 命中等级
     */
    private short             hitLevel;

    /**
     * 物理闪避等级
     */
    private short             physicsDuckLevel;

    /**
     * 物理致命一击几率
     */
    private float             physicsDeathblowOdds;

    /**
     * 魔法致命一击几率
     */
    private float             magicDeathblowOdds;

    /**
     * 物理命中几率
     */
    private float             physicsHitOdds;

    /**
     * 魔法命中几率
     */
    private float             magicHitOdds;

    /**
     * 物理闪避等级
     */
    private float             physicsDuckOdds;

    /**
     * 最小物理攻击力
     */
    private int               minPhysicsAttack;

    /**
     * 最大物理攻击力
     */
    private int               maxPhysicsAttack;

    /**
     * 基础属性魔法伤害列表
     */
    private MagicHarmList     baseMagicHarmList;

    /**
     * 魔法抗性列表
     */
    private MagicFastnessList magicFastnessList;

    /**
     * 附加物理攻击伤害值
     */
    private int               additionalPhysicsAttackHarmValue;

    /**
     * 附加物理攻击伤害比例
     */
    private int               additionalPhysicsAttackHarmScale;

    /**
     * 附加的受物理攻击伤害值
     */
    private int               additionalHarmValueBePhysicsAttack;

    /**
     * 附加的受物理攻击伤害比例
     */
    private int               additionalHarmScaleBePhysicsAttack;

    /**
     * 附带的魔法伤害值列表
     */
    private MagicHarmList     additionalMagicHarmList;

    /**
     * 附带的魔法伤害比例列表
     */
    private MagicHarmList     additionalMagicHarmScaleList;

    /**
     * 附带的受魔法伤害值列表
     */
    private MagicHarmList     additionalMagicHarmBeAttackList;

    /**
     * 附带的受魔法伤害比例列表
     */
    private MagicHarmList     additionalMagicHarmScaleBeAttackList;

    /**
     * 构造
     * 
     * @param _host
     */
    public ObjectProperty(ME2GameObject _host)
    {
        host = _host;
        baseMagicHarmList = new MagicHarmList();
        magicFastnessList = new MagicFastnessList();
        additionalMagicHarmList = new MagicHarmList();
        additionalMagicHarmScaleList = new MagicHarmList();
        additionalMagicHarmBeAttackList = new MagicHarmList();
        additionalMagicHarmScaleBeAttackList = new MagicHarmList();
    }

    /**
     * 设置最大生命值
     * 
     * @param _hpMax
     */
    public void setHpMax (int _hpMax)
    {
        maxHp = _hpMax;
    }

    /**
     * 增加HP上限
     * 
     * @param _hpMax
     */
    public void addHpMax (int _hpMax)
    {
        maxHp += _hpMax;
    }

    /**
     * 获取最大生命值
     * 
     * @return
     */
    public int getHpMax ()
    {
        if (maxHp < 0)
        {
            return 0;
        }

        return maxHp;
    }

    /**
     * 设置最大魔法值
     * 
     * @param _mpMax
     */
    public void setMpMax (int _mpMax)
    {
        maxMp = _mpMax;
    }

    /**
     * 增加魔法值上限
     * 
     * @param _mpMax
     */
    public void addMpMax (int _mpMax)
    {
        maxMp += _mpMax;
    }

    /**
     * 获取最大魔法值
     * 
     * @return
     */
    public int getMpMax ()
    {
        if (maxMp < 0)
        {
            return 0;
        }

        return maxMp;
    }

    /**
     * 设置物理爆击等级
     * 
     * @param _physicsDeathblowLevel 物理爆击等级
     * @param _roleLevel 角色等级
     */
    public void setPhysicsDeathblowLevel (short _physicsDeathblowLevel)
    {
        physicsDeathblowLevel = _physicsDeathblowLevel;

        physicsDeathblowOdds = CEService.basePhysicsDeathblowOdds(
        		getPhysicsDeathblowLevel(), host.getLevel());
    }

    /**
     * 增加物理爆击等级
     * 
     * @param _physicsDeathblowLevel 物理爆击等级
     * @param _roleLevel 角色等级
     */
    public void addPhysicsDeathblowLevel (short _physicsDeathblowLevel)
    {
        physicsDeathblowLevel += _physicsDeathblowLevel;

        physicsDeathblowOdds = CEService.basePhysicsDeathblowOdds(
                getPhysicsDeathblowLevel(), host.getLevel());
    }

    /**
     * 获取物理爆击等级
     * 
     * @return
     */
    public short getPhysicsDeathblowLevel ()
    {
        if (physicsDeathblowLevel < 0)
        {
            return 0;
        }

        return physicsDeathblowLevel;
    }

    /**
     * 获取物理爆击几率
     * 
     * @return
     */
    public float getPhysicsDeathblowOdds ()
    {
        if (physicsDeathblowOdds < 0)
        {
            return 0;
        }

        return physicsDeathblowOdds;
    }

//    /**
//     * 设置物理爆击几率 **不再提供直接设置
//     * 
//     * @param _physicsDeathblowOdds
//     */
//    public void setPhysicsDeathblowOdds (float _physicsDeathblowOdds)
//    {
//        physicsDeathblowOdds = _physicsDeathblowOdds;
//    }

//    /**
//     * 增加物理爆击几率 **不再提供直接设置
//     * 
//     * @param _physicsDeathblowOdds
//     */
//    public void addPhysicsDeathblowOdds (float _physicsDeathblowOdds)
//    {
//        physicsDeathblowOdds += _physicsDeathblowOdds;
//    }

    /**
     * 设置魔法爆击等级
     * 
     * @param _magicDeathblowLevel 变化魔法爆击等级
     * @param _roleLevel 角色等级
     */
    public void setMagicDeathblowLevel (short _magicDeathblowLevel)
    {
        magicDeathblowLevel = _magicDeathblowLevel;

        magicDeathblowOdds = CEService.baseMagicDeathblowOdds(getMagicDeathblowLevel(), host.getLevel());
    }

    /**
     * 增加魔法爆击等级
     * 
     * @param _magicDeathblowLevel 变化魔法爆击等级
     * @param _roleLevel 角色等级
     */
    public void addMagicDeathblowLevel (short _magicDeathblowLevel)
    {
        magicDeathblowLevel += _magicDeathblowLevel;

        magicDeathblowOdds = CEService.baseMagicDeathblowOdds(getMagicDeathblowLevel(), host.getLevel());
    }

    /**
     * 获取魔法爆击等级
     * 
     * @return
     */
    public short getMagicDeathblowLevel ()
    {
        if (magicDeathblowLevel < 0)
        {
            return 0;
        }

        return magicDeathblowLevel;
    }

    /**
     * 获取魔法爆击几率
     * 
     * @return
     */
    public float getMagicDeathblowOdds ()
    {
        if (magicDeathblowOdds < 0)
        {
            return 0;
        }

        return magicDeathblowOdds;
    }

    /**
     * 设置魔法爆击几率
     * 
     * @param _magicDeathblowOdds
     */
    public void setMagicDeathblowOdds (float _magicDeathblowOdds)
    {
        magicDeathblowOdds = _magicDeathblowOdds;
    }

    /**
     * 增加魔法爆击几率
     * 
     * @param _magicDeathblowOdds
     */
    public void addMagicDeathblowOdds (float _magicDeathblowOdds)
    {
        magicDeathblowOdds += _magicDeathblowOdds;
    }

    /**
     * 设置命中等级
     * 
     * @param _hitLevel 命中等级
     * @param _roleLevel 角色等级
     */
    public void setHitLevel (short _hitLevel)
    {
        hitLevel = _hitLevel;

        physicsHitOdds = CEService.basePhysicsHitOdds(getLucky(),
                getHitLevel(), host.getLevel());
        magicHitOdds = CEService.baseMagicHitOdds(getLucky(), getHitLevel(),
                host.getLevel());
    }

    /**
     * 增加命中等级
     * 
     * @param _hitLevel 增加的命中等级
     * @param _roleLevel 角色等级
     */
    public void addHitLevel (short _hitLevel)
    {
        hitLevel += _hitLevel;

        physicsHitOdds = CEService.basePhysicsHitOdds(getLucky(),
                getHitLevel(), host.getLevel());
        magicHitOdds = CEService.baseMagicHitOdds(getLucky(), getHitLevel(),
                host.getLevel());
    }

    /**
     * 获取命中等级
     * 
     * @return
     */
    public short getHitLevel ()
    {
        if (hitLevel < 0)
        {
            return 0;
        }

        return hitLevel;
    }

    /**
     * 获取物理命中几率
     * 
     * @return
     */
    public float getPhysicsHitOdds ()
    {
        if (physicsHitOdds < 0)
        {
            return 0;
        }

        return physicsHitOdds;
    }

    /**
     * 设置物理命中几率
     * 
     * @param _physicsHitOdds
     */
    public void setPhysicsHitOdds (float _physicsHitOdds)
    {
        physicsHitOdds = _physicsHitOdds;
    }

    /**
     * 获取魔法命中几率
     * 
     * @return
     */
    public float getMagicHitOdds ()
    {
        if (magicHitOdds < 0)
        {
            return 0;
        }

        return magicHitOdds;
    }

    /**
     * 设置魔法命中几率
     * 
     * @param _physicsHitOdds
     */
    public void setMagicHitOdds (float _magicHitOdds)
    {
        magicHitOdds = _magicHitOdds;
    }

    /**
     * 设置物理闪避等级
     * 
     * @param _physicsDuckLevel 物理闪避等级
     * @param _roleLevel 角色等级
     */
    public void setPhysicsDuckLevel (short _physicsDuckLevel)
    {
        physicsDuckLevel = _physicsDuckLevel;

        physicsDuckOdds = CEService.basePhysicsDuckOdds(getPhysicsDuckLevel(), host.getLevel());
    }

    /**
     * 增加物理闪避等级
     * 
     * @param _physicsDuckLevel 物理闪避等级
     * @param _roleLevel 角色等级
     */
    public void addPhysicsDuckLevel (short _physicsDuckLevel)
    {
        physicsDuckLevel += _physicsDuckLevel;

        physicsDuckOdds = CEService.basePhysicsDuckOdds(getPhysicsDuckLevel(), host.getLevel());
    }

    /**
     * 获取物理闪避等级
     * 
     * @return
     */
    public short getPhysicsDuckLevel ()
    {
        if (physicsDuckLevel < 0)
        {
            return 0;
        }

        return physicsDuckLevel;
    }

    /**
     * 获取物理闪避几率
     * 
     * @return
     */
    public float getPhysicsDuckOdds ()
    {
        if (physicsDuckOdds < 0)
        {
            return 0;
        }

        return physicsDuckOdds;
    }

    /**
     * 设置物理闪避几率
     * 
     * @param _physicsHitOdds
     */
    public void setPhysicsDuckOdds (float _physicsDuckOdds)
    {
        physicsDuckOdds = _physicsDuckOdds;
    }

    /**
     * 设置护甲值
     * 
     * @param _defense
     */
    public void setDefense (int _defense)
    {
        defense = _defense;
    }

    /**
     * 增加护甲值
     * 
     * @param _defense
     */
    public void addDefense (int _defense)
    {
        defense += _defense;
    }

    /**
     * 获取护甲值
     * 
     * @return
     */
    public int getDefense ()
    {
        if (defense < 0)
        {
            return 0;
        }

        return defense;
    }

    /**
     * 设置力量
     * 
     * @param _strength
     */
    public void setStrength (int _strength)
    {
        strength = _strength;
    }

    /**
     * 增加力量
     * 
     * @param _strength 力量值
     */
    public void addStrength (int _strength)
    {
        strength += _strength;

        EVocation vocation = host.getVocation();

        int weaponMinPhysicsAttack = 0;
        int weaponMaxPhysicsAttack = 0;
        int weaponMinMagicHarm = 0;
        int weaponMaxMagicHarm = 0;
        float weaponImmobilityTime = Constant.DEFAULT_IMMOBILITY_TIME;

        if (EObjectType.PLAYER == host.getObjectType())
        {
            EquipmentInstance ei = ((HeroPlayer) host).getBodyWear()
                    .getWeapon();

            if (null != ei)
            {
                weaponMinPhysicsAttack = ei.getWeaponMinPhysicsAttack();
                weaponMaxPhysicsAttack = ei.getWeaponMaxPhysicsAttack();
                weaponImmobilityTime = ((Weapon) ei.getArchetype())
                        .getImmobilityTime();

                if (null != ei.getMagicDamage())
                {
                    weaponMaxMagicHarm = ei.getMagicDamage().maxDamageValue;
                    weaponMinMagicHarm = ei.getMagicDamage().minDamageValue;
                }
            }
        }
        else
        {
            weaponImmobilityTime = ((Monster) host)
                    .getActualAttackImmobilityTime() / 1000F;
            weaponMinPhysicsAttack = ((Monster) host).getBaseProperty()
                    .getMinPhysicsAttack();
            weaponMaxPhysicsAttack = ((Monster) host).getBaseProperty()
                    .getMaxPhysicsAttack();
        }

        maxPhysicsAttack = CEService.maxPhysicsAttack(getStrength(),
                getAgility(), vocation.getPhysicsAttackParaA(), vocation
                        .getPhysicsAttackParaB(), vocation
                        .getPhysicsAttackParaC(), weaponMaxPhysicsAttack,
                weaponMaxMagicHarm, weaponImmobilityTime, host.getObjectLevel()
                        .getPhysicsAttckCalPara());

        minPhysicsAttack = CEService.minPhysicsAttack(getStrength(),
                getAgility(), vocation.getPhysicsAttackParaA(), vocation
                        .getPhysicsAttackParaB(), vocation
                        .getPhysicsAttackParaC(), weaponMinPhysicsAttack,
                weaponMinMagicHarm, weaponImmobilityTime, host.getObjectLevel()
                        .getPhysicsAttckCalPara());
    }

    /**
     * 获取力量
     * 
     * @return
     */
    public int getStrength ()
    {
        if (strength < 0)
        {
            return 0;
        }

        return strength;
    }

    /**
     * 设置精神
     * 
     * @param _spirit
     */
    public void setSpirit (int _spirit)
    {
        spirit = _spirit;
    }

    /**
     * 增加精神
     * 
     * @param _spirit
     */
    public void addSpirit (int _spirit)
    {
    	//把老的防御加成先删掉
        defense -= CEService.defenseBySpirit(getSpirit(), host.getVocation()
                .getPhysicsDefenceSpiritPara());

        spirit += _spirit;
        //把新的防御加成加上.
        defense += CEService.defenseBySpirit(getSpirit(), host.getVocation()
                .getPhysicsDefenceSpiritPara());

        if (EObjectType.PLAYER == host.getObjectType())
        {
            ((HeroPlayer) host).resetHpResumeValue(CEService.hpResumeAuto(host
                    .getLevel(), getSpirit(), host.getVocation()
                    .getStaminaCalPara()));
            ((HeroPlayer) host).resetMpResumeValue(CEService.mpResumeAuto(host
                    .getLevel(), getSpirit(), host.getVocation()
                    .getInteCalcPara()));
        }
    }

    /**
     * 获取精神
     * 
     * @return
     */
    public int getSpirit ()
    {
        if (spirit < 0)
        {
            return 0;
        }

        return spirit;
    }

    /**
     * 设置智力
     * 
     * @param _inte
     */
    public void setInte (int _inte)
    {
        inte = _inte;
    }

    /**
     * 增加智力
     * 
     * @param _inte
     */
    public void addInte (int _inte)
    {
    	//edit by zhengl; date: 2011-04-21 note: 暴击,命中,闪避 各种等级的修改点
    	magicDeathblowLevel -= CEService.magicDeathblowLevel(inte, lucky);
    	inte += _inte;
        //再附加新的加成
    	magicDeathblowLevel += CEService.magicDeathblowLevel(inte, lucky);

        maxMp = CEService.mpByInte(inte, host.getLevel(), EObjectLevel.NORMAL.getMpCalPara());

        int baseMagicHarmByInte = CEService.magicHarmByInte(getInte());

        //add by zhengl; date: 2011-03-04; note: 对于魔法伤害的计算,都使用了下面注释里的规则
        /**
         * 1,先计算玩家智力属性附加的魔法伤害
         * 2,再计算玩家手持武器制造的魔法伤害
         * 3,手持武器除特定武器外,现在均制造ALL类型魔法伤害.
         */
//        baseMagicHarmList.reset(EMagic.SANCTITY, baseMagicHarmByInte);
//        baseMagicHarmList.reset(EMagic.UMBRA, baseMagicHarmByInte);
//        baseMagicHarmList.reset(EMagic.FIRE, baseMagicHarmByInte);
//        baseMagicHarmList.reset(EMagic.WATER, baseMagicHarmByInte);
//        baseMagicHarmList.reset(EMagic.SOIL, baseMagicHarmByInte);
        baseMagicHarmList.resetByInte(EMagic.SANCTITY, baseMagicHarmByInte);
        baseMagicHarmList.resetByInte(EMagic.UMBRA, baseMagicHarmByInte);
        baseMagicHarmList.resetByInte(EMagic.FIRE, baseMagicHarmByInte);
        baseMagicHarmList.resetByInte(EMagic.WATER, baseMagicHarmByInte);
        baseMagicHarmList.resetByInte(EMagic.SOIL, baseMagicHarmByInte);

        if (EObjectType.PLAYER == host.getObjectType())
        {
            EquipmentInstance ei = ((HeroPlayer) host).getBodyWear().getWeapon();

            if (null != ei)
            {
                MagicDamage weaponMagicDamage = ei.getMagicDamage();

                if (null != weaponMagicDamage)
                {
                	//edit by zhengl; date: 2011-04-27; note: 武器伤害已经设置为ALL类型魔法伤害.
                    int magicHarmValue = 
                    	(weaponMagicDamage.minDamageValue + weaponMagicDamage.maxDamageValue) / 2;
                    baseMagicHarmList.reset(weaponMagicDamage.magic, magicHarmValue);
                }
            }
        }
    }

    /**
     * 获取智力
     * 
     * @return
     */
    public int getInte ()
    {
        if (inte < 0)
        {
            return 0;
        }

        return inte;
    }

    /**
     * 设置耐力
     * 
     * @param _stamina
     */
    public void setStamina (int _stamina)
    {
        stamina = _stamina;
    }

    /**
     * 增加耐力
     * 
     * @param _stamina
     */
    public void addStamina (int _stamina)
    {
        stamina += _stamina;
        //edit by zhengl; date: 2011-02-24; note: 添加系数进HP计算公式
        maxHp = CEService.hpByStamina(getStamina(), host.getLevel(), host.getObjectLevel().getHpCalPara());
    }

    /**
     * 获取耐力
     * 
     * @return
     */
    public int getStamina ()
    {
        if (stamina < 0)
        {
            return 0;
        }

        return stamina;
    }

    /**
     * 设置敏捷
     * 
     * @param _agility
     */
    public void setAgility (int _agility)
    {
        agility = _agility;
    }

    /**
     * 增加敏捷
     * 
     * @param _agility
     */
    public void addAgility (int _agility)
    {
    	//edit by zhengl; date: 2011-04-21 note: 暴击,命中,闪避 各种等级的修改点
    	//先去掉老的加成
    	physicsDeathblowLevel -= CEService.physicsDeathblowLevel(agility, lucky);
    	physicsDuckLevel -= CEService.duckLevel(agility, lucky);
        agility += _agility;
        //再附加新的加成
        physicsDeathblowLevel += CEService.physicsDeathblowLevel(agility, lucky);
        physicsDuckLevel += CEService.duckLevel(agility, lucky);

        EVocation vocation = host.getVocation();

        int weaponMinPhysicsAttack = 0;
        int weaponMaxPhysicsAttack = 0;
        int weaponMinMagicHarm = 0;
        int weaponMaxMagicHarm = 0;
        float weaponImmobilityTime = Constant.DEFAULT_IMMOBILITY_TIME;

        if (EObjectType.PLAYER == host.getObjectType())
        {
            EquipmentInstance ei = ((HeroPlayer) host).getBodyWear()
                    .getWeapon();

            if (null != ei)
            {
                weaponMinPhysicsAttack = ei.getWeaponMinPhysicsAttack();
                weaponMaxPhysicsAttack = ei.getWeaponMaxPhysicsAttack();
                weaponImmobilityTime = ((Weapon) ei.getArchetype())
                        .getImmobilityTime();

                if (null != ei.getMagicDamage())
                {
                    weaponMaxMagicHarm = ei.getMagicDamage().maxDamageValue;
                    weaponMinMagicHarm = ei.getMagicDamage().minDamageValue;
                }
            }
        }
        else
        {
            weaponImmobilityTime = ((Monster) host)
                    .getActualAttackImmobilityTime() / 1000F;
            weaponMinPhysicsAttack = ((Monster) host).getBaseProperty()
                    .getMinPhysicsAttack();
            weaponMaxPhysicsAttack = ((Monster) host).getBaseProperty()
                    .getMaxPhysicsAttack();
        }

        maxPhysicsAttack = CEService.maxPhysicsAttack(getStrength(),
                getAgility(), vocation.getPhysicsAttackParaA(), vocation
                        .getPhysicsAttackParaB(), vocation
                        .getPhysicsAttackParaC(), weaponMaxPhysicsAttack,
                weaponMaxMagicHarm, weaponImmobilityTime, host.getObjectLevel()
                        .getPhysicsAttckCalPara());
        //edit by zhengl; date: 2011-03-06; note: 修改此处获得最小攻击的BUG
        minPhysicsAttack = CEService.minPhysicsAttack(
        		getStrength(),
                getAgility(), 
                vocation.getPhysicsAttackParaA(), 
                vocation.getPhysicsAttackParaB(), 
                vocation.getPhysicsAttackParaC(), 
                weaponMinPhysicsAttack,
                weaponMinMagicHarm, 
                weaponImmobilityTime, 
                host.getObjectLevel().getPhysicsAttckCalPara());

        physicsDuckOdds = CEService.basePhysicsDuckOdds(getPhysicsDuckLevel(), host.getLevel());
        physicsDeathblowOdds = CEService.basePhysicsDeathblowOdds(
                getPhysicsDeathblowLevel(), host.getLevel());
    }

    /**
     * 获取敏捷
     * 
     * @return
     */
    public int getAgility ()
    {
        if (agility < 0)
        {
            return 0;
        }

        return agility;
    }

    /**
     * 获取幸运值
     * 
     * @return
     */
    public int getLucky ()
    {
        if (lucky < 0)
        {
            return 0;
        }

        return lucky;
    }

    /**
     * 设置幸运值
     * 
     * @param _lucky
     */
    public void setLucky (int _lucky)
    {
        lucky = _lucky;
    }

    /**
     * 增加幸运值
     * 
     * @param _lucky
     */
    public void addLucky (int _lucky)
    {
    	//edit by zhengl; date: 2011-04-21 note: 暴击,命中,闪避 各种等级的修改点
    	//先去掉老的加成
    	physicsDeathblowLevel -= CEService.physicsDeathblowLevel(agility, lucky);
    	magicDeathblowLevel -= CEService.magicDeathblowLevel(inte, lucky);
    	hitLevel -= CEService.hitLevel(lucky);
    	physicsDuckLevel -= CEService.duckLevel(agility, lucky);
    	lucky += _lucky;
        //再附加新的加成
        physicsDeathblowLevel += CEService.physicsDeathblowLevel(agility, lucky);
    	magicDeathblowLevel += CEService.magicDeathblowLevel(inte, lucky);
    	hitLevel += CEService.hitLevel(lucky);
    	physicsDuckLevel += CEService.duckLevel(agility, lucky);

        physicsDuckOdds = CEService.basePhysicsDuckOdds(getPhysicsDuckLevel(), host.getLevel());
        physicsHitOdds = CEService.basePhysicsHitOdds(getLucky(),
                getHitLevel(), host.getLevel());
        magicHitOdds = CEService.baseMagicHitOdds(getLucky(), getHitLevel(),
                host.getLevel());
        magicDeathblowOdds = CEService.baseMagicDeathblowOdds(getMagicDeathblowLevel(), host.getLevel());
        physicsDuckOdds = CEService.basePhysicsDuckOdds(getPhysicsDuckLevel(), host.getLevel());
    }

    /**
     * 魔法抗性列表
     * 
     * @return
     */
    public MagicFastnessList getMagicFastnessList ()
    {
        return magicFastnessList;
    }

    /**
     * 获取基础属性魔法伤害列表
     * 
     * @return
     */
    public MagicHarmList getBaseMagicHarmList ()
    {
        return baseMagicHarmList;
    }

    /**
     * 获取实际攻击力，在不少技能或计算公式中会使用此值
     * <p>
     * 取物理攻击上下限内的一个随机值
     * 
     * @return
     */
    public int getActualPhysicsAttack ()
    {
        return minPhysicsAttack + random.nextInt(maxPhysicsAttack - minPhysicsAttack + 1);
    }

    /**
     * 设置最大物理攻击力
     * 
     * @param _maxPhysicsAttack
     */
    public void setMaxPhysicsAttack (int _maxPhysicsAttack)
    {
        maxPhysicsAttack = _maxPhysicsAttack;
    }

    /**
     * 获取最大物理攻击力
     * 
     * @return
     */
    public int getMaxPhysicsAttack ()
    {
        if (maxPhysicsAttack < 0)
        {
            return 0;
        }

        return maxPhysicsAttack;
    }

    /**
     * 设置最小物理攻击力
     * 
     * @param _maxPhysicsAttack
     */
    public void setMinPhysicsAttack (int _minPhysicsAttack)
    {
        minPhysicsAttack = _minPhysicsAttack;
    }

    /**
     * 获取最小物理攻击力
     * 
     * @return
     */
    public int getMinPhysicsAttack ()
    {
        if (minPhysicsAttack < 0)
        {
            return 0;
        }

        return minPhysicsAttack;
    }

    /**
     * 获取附带的物理攻击伤害值
     */
    public int getAdditionalPhysicsAttackHarmValue ()
    {
        return additionalPhysicsAttackHarmValue;
    }

    /**
     * 增加附带的物理攻击伤害值
     * 
     * @param _value 伤害值（负数为降低）
     * @return
     */
    public int addAdditionalPhysicsAttackHarmValue (int _value)
    {
        return additionalPhysicsAttackHarmValue += _value;
    }

    /**
     * 获取附带的物理攻击伤害比例
     */
    public float getAdditionalPhysicsAttackHarmScale ()
    {
        return additionalPhysicsAttackHarmScale;
    }

    /**
     * 增加附带的物理攻击伤害比例
     * 
     * @param _scale 比例值（负数为降低）
     * @return 变化后的结果
     */
    public float addAdditionalPhysicsAttackHarmScale (float _scale)
    {
        return additionalPhysicsAttackHarmScale += _scale;
    }

    /**
     * 获取附带的受物理攻击伤害值
     */
    public int getAdditionalHarmValueBePhysicsAttack ()
    {
        return additionalHarmValueBePhysicsAttack;
    }

    /**
     * 增加附带的受物理攻击伤害值
     * 
     * @param _value 伤害值（负数为降低）
     * @return
     */
    public int addAdditionalHarmValueBePhysicsAttack (int _value)
    {
        return additionalHarmValueBePhysicsAttack += _value;
    }

    /**
     * 获取附带的受物理攻击伤害比例
     */
    public float getAdditionalHarmScaleBePhysicsAttack ()
    {
        return additionalHarmScaleBePhysicsAttack;
    }

    /**
     * 增加附带的受物理攻击伤害比例
     * 
     * @param _scale 比例值（负数为降低）
     * @return 变化后的结果
     */
    public float addAdditionalHarmScaleBePhysicsAttack (float _scale)
    {
        return additionalHarmScaleBePhysicsAttack += _scale;
    }

    /**
     * 获取附加魔法伤害值
     * 
     * @param _magic
     * @return
     */
    public float getAdditionalMagicHarm (EMagic _magic)
    {
        return additionalMagicHarmList.getEMagicHarmValue(_magic);
    }

    /**
     * 增加附加魔法伤害值
     * 
     * @param _magic
     * @param _value
     * @return
     */
    public float addAdditionalMagicHarm (EMagic _magic, float _value)
    {
        return additionalMagicHarmList.add(_magic, _value);
    }

    /**
     * 增加附加魔法伤害值
     * 
     * @param _magicHarmList
     */
    public void addAdditionalMagicHarm (MagicHarmList _magicHarmList)
    {
        additionalMagicHarmList.add(_magicHarmList);
    }

    /**
     * 获取附加魔法伤害比例
     * 
     * @param _magic
     * @return
     */
    public float getAdditionalMagicHarmScale (EMagic _magic)
    {
        return additionalMagicHarmScaleList.getEMagicHarmValue(_magic);
    }

    /**
     * 增加附加魔法伤害比例
     * 
     * @param _magic
     * @return
     */
    public float addAdditionalMagicHarmScale (EMagic _magic, float _scale)
    {
        return additionalMagicHarmScaleList.add(_magic, _scale);
    }

    /**
     * 增加附加魔法伤害比例
     * 
     * @param _magicHarmList
     */
    public void addAdditionalMagicHarmScale (MagicHarmList _magicHarmList)
    {
        additionalMagicHarmScaleList.add(_magicHarmList);
    }

    /**
     * 获取附加受魔法伤害值
     * 
     * @param _magic
     * @return
     */
    public float getAdditionalMagicHarmBeAttack (EMagic _magic)
    {
        return additionalMagicHarmBeAttackList.getEMagicHarmValue(_magic);
    }

    /**
     * 增加附加的受魔法伤害值
     * 
     * @param _magic
     * @param _value
     * @return
     */
    public float addAdditionalMagicHarmBeAttack (EMagic _magic, float _value)
    {
        return additionalMagicHarmBeAttackList.add(_magic, _value);
    }

    /**
     * 增加附加受魔法伤害值
     * 
     * @param _magicHarmList
     */
    public void addAdditionalMagicHarmBeAttack (MagicHarmList _magicHarmList)
    {
        additionalMagicHarmBeAttackList.add(_magicHarmList);
    }

    /**
     * 获取附加受魔法伤害比例
     * 
     * @param _magic
     * @return
     */
    public float getAdditionalMagicHarmScaleBeAttack (EMagic _magic)
    {
        return additionalMagicHarmScaleBeAttackList.getEMagicHarmValue(_magic);
    }

    /**
     * 增加附加的受魔法伤害值比例
     * 
     * @param _magic
     * @param _value
     * @return
     */
    public float addAdditionalMagicHarmScaleBeAttack (EMagic _magic,
            float _value)
    {
        return additionalMagicHarmScaleBeAttackList.add(_magic, _value);
    }

    /**
     * 增加附加受魔法伤害值比例
     * 
     * @param _magicHarmList
     */
    public void addAdditionalMagicHarmScaleBeAttack (
            MagicHarmList _magicHarmList)
    {
        additionalMagicHarmScaleBeAttackList.add(_magicHarmList);
    }

    /**
     * 清除非基本属性（附加的）
     */
    public void clearNoneBaseProperty ()
    {
        additionalPhysicsAttackHarmValue = 0;
        additionalPhysicsAttackHarmScale = 0;
        additionalHarmValueBePhysicsAttack = 0;
        additionalHarmScaleBePhysicsAttack = 0;
        additionalMagicHarmList.clear();
        additionalMagicHarmScaleList.clear();
        additionalMagicHarmBeAttackList.clear();
        additionalMagicHarmScaleBeAttackList.clear();
    }
}
