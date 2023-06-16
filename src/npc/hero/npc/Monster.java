package hero.npc;

import hero.dungeon.service.DungeonServiceImpl;
import hero.effect.Effect;
import hero.effect.service.EffectServiceImpl;
import hero.fight.broadcast.SpecialViewStatusBroadcast;
import hero.fight.service.FightConfig;
import hero.fight.service.SpecialStatusDefine;
import hero.gather.service.GatherServerImpl;
import hero.item.legacy.TaskGoodsLegacyInfo;
import hero.item.legacy.WorldLegacyDict;
import hero.map.Decorater;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.npc.ai.MonsterAI;
import hero.npc.detail.AttackChain;
import hero.npc.detail.EMonsterLevel;
import hero.npc.dict.MonsterDataDict.MonsterData;
import hero.npc.message.MonsterDisengageFightNotify;
import hero.npc.message.MonsterRefreshNotify;
import hero.npc.service.NotPlayerServiceImpl;
import hero.player.HeroPlayer;
import hero.player.define.EClan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Future;

import javolution.util.FastList;

import hero.share.Constant;
import hero.share.EObjectLevel;
import hero.share.EVocation;
import hero.share.ME2GameObject;
import hero.share.EObjectType;
import hero.share.MoveSpeed;
import hero.share.service.ThreadPoolFactory;
import hero.task.service.TaskServiceImpl;
import org.apache.log4j.Logger;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Monster.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-02 下午04:01:59
 * @描述 ：
 */

public class Monster extends ME2NotPlayer
{
    private static Logger   log = Logger.getLogger(Monster.class);
    private static Random                               random     = new Random();

    /**
     * 是否是主动攻击类型
     */
    private boolean                                     isActiveAttackType;

    /**
     * 重生时间(秒)
     */
    private int                                         retime;

    /**
     * 死亡时间(毫秒)
     */
    private long                                        dieTime;

    /**
     * 进入战斗的时间
     */
    private long                                        enterFightTime;

    /**
     * 是否处于变身状态
     */
    private boolean                                     isChangesStatus;

    /**
     * 逃跑中
     */
    private boolean                                     runingAway;

    /**
     * 怪物的基础经验值
     */
    private int                                         basicExp;

    /**
     * 是否在副本中
     */
    private boolean                                     isInDungeon;

    /**
     * 当前意图
     */
    private ENotPlayerIntention                         intention;

    /**
     * 攻击AI
     */
    private int                                         attackAiID;

    /**
     * NPC对象等级
     */
    private EMonsterLevel                               monsterLevel;

    /**
     * 挑衅者
     */
    private HeroPlayer                                  provocateur;

    /**
     * AI
     */
    private MonsterAI                                   ai;

    /**
     * 仇恨关联判定
     */
    private AttackChain                                 attackChain;

    /**
     * 第一个攻击怪物的人，同时也就是物品拾取者
     */
    private HeroPlayer                                  attackerAtFirst;

    /**
     * 仇恨目标
     */
    private FastList<HatredTarget>                      hatredTargetList;

    /**
     * 仇恨值排序比较工具
     */
    private static final HatredComparator<HatredTarget> comparator = new HatredComparator<HatredTarget>();

    /**
     * 灵魂ID列表
     */
    private Vector<Integer>                             soulIDList;

    /**
     * 吸收灵魂的玩家ID
     */
    private int                                         takeSoulUserID;

    /**
     * 最后一次攻击时间（用于计算下一次普通攻击时间）
     */
    private long                                        lastAttackTime;

    /**
     * 最后一次与战斗状态相关的时间（攻击和被攻击，用于战斗状态的判断）
     */
    private long                                        lastFightTime;

    /**
     * AI任务
     */
    private Future<MonsterAI>                           aiTast;

    /**
     * 单次掉落物品种类最多数量
     */
    private byte                                        legacyTypeMostNums;

    /**
     * 单次掉落物品最少种类数量
     */
    private byte                                        legacyTypeSmallestNums;

    /**
     * 掉落物品总清单
     */
    private ArrayList<float[]>                          legacyList;

    /**
     * 构造
     */
    public Monster(MonsterData _data)
    {
        super();

        objectType = EObjectType.MONSTER;

        setModelID(_data.modelID);
        setName(_data.name);
        setImageID(Short.parseShort(_data.imageID));
        setLevel(Short.parseShort(_data.level));
        setClan(EClan.getClanByDesc(_data.clan));

        if (null != _data.vocation)
        {
            setVocation(EVocation.getVocationByDesc(_data.vocation));
        }
        else
        {
        	log.info("怪物职业为NULL,请检查怪物配置文件.");
            setVocation(EVocation.LI_SHI);
        }

        setObjectLevel(EObjectLevel.getNPCLevel(_data.type));
        setMonsterLevel(EMonsterLevel.getMonsterLevel(_data.normalOrBoss));
        setActualAttackImmobilityTime(Constant.DEFAULT_IMMOBILITY_TIME);
        setMoveSpeed(MoveSpeed.GENERIC);

        hatredTargetList = new FastList<HatredTarget>();
        setIntention(ENotPlayerIntention.AI_INTENTION_ACTIVE);
        effectList = new ArrayList<Effect>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see hero.npc.ME2NPC#action() 激活实例怪物
     */
    public void active ()
    {
        super.active();
        setDirection((byte) (random.nextInt(4) + 1));
        startAITask();
    }

    public void active (byte[][] _movePath)
    {
        log.debug("monster active ..");
        active();
        ai.setMovePath(_movePath);
    }

    /**
     * 启动AI
     */
    @SuppressWarnings("unchecked")
    public void startAITask ()
    {
        log.debug("monster startAITask ..");
        if (aiTast == null)
        {
            ai = new MonsterAI(this);
            aiTast = (Future<MonsterAI>) ThreadPoolFactory
                    .getInstance()
                    .excuteAI(
                            ai,
                            RANDOM.nextInt(1500) + 1500,
                            NotPlayerServiceImpl.getInstance().getConfig().MONSTER_AI_INTERVAL);
        }
    }

    /**
     * 设置意图
     * 
     * @param _intention 意图
     */
    public void setIntention (ENotPlayerIntention _intention)
    {
        intention = _intention;
    }

    /**
     * 获取当前意图
     * 
     * @return 当前意图
     */
    public ENotPlayerIntention getIntention ()
    {
        return intention;
    }

    /**
     * 改变意图
     * 
     * @param _intention 意图
     * @param _gameObject 意图可能涉及到的玩家对象
     */
    public void changeInvention (ENotPlayerIntention _intention,
            HeroPlayer _player)
    {
        switch (_intention)
        {
            case AI_INTENTION_ACTIVE:
            {
                setIntention(ENotPlayerIntention.AI_INTENTION_ACTIVE);
                setMoveSpeed(MoveSpeed.GENERIC);

                break;
            }
            case AI_INTENTION_ATTACK:
            {
                enterFight();
                setIntention(ENotPlayerIntention.AI_INTENTION_ATTACK);

                if (EMonsterLevel.NORMAL == monsterLevel)
                    changeMoveSpeed(MoveSpeed.NORMAL_MONSTER_ATTACK_ADD);
                else
                    changeMoveSpeed(MoveSpeed.BOSS_MONSTER_ATTACK_ADD);

                ai.normalAttack();

                break;
            }
        }
    }

    /**
     * 刷新最后一次攻击时间
     */
    public void refreshLastAttackTime ()
    {
        lastAttackTime = System.currentTimeMillis();
    }

    /**
     * 设置第一次攻击时间，修正攻击动作和行走的同步问题
     */
    public void setFirstAttackTime ()
    {
        lastAttackTime = System.currentTimeMillis() - 2000;
    }

    /**
     * 获取最后一次攻击时间
     * 
     * @return
     */
    public long getLastAttackTime ()
    {
        return lastAttackTime;
    }

    /**
     * 停止AI
     */
    public void stopAITast ()
    {
        if (aiTast != null)
        {
            aiTast.cancel(false);
            aiTast = null;
            ThreadPoolFactory.getInstance().removeAI(ai);
            ai = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see hero.npc.ME2NotPlayer#destroy()
     */
    public void destroy ()
    {
        if (!isDead)
        {
            invalid();
            clearEffect();
        }

        stopAITast();
        NotPlayerServiceImpl.getInstance().removeMonster(getID());
    }

    /**
     * 设置重生时间
     * 
     * @param _retime
     */
    public void setRetime (int _retime)
    {
        retime = _retime;
    }

    /**
     * 获取重生时间
     * 
     * @return
     */
    public int getRetime ()
    {
        return retime;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hero.share.ME2GameObject#enterFight()
     */
    public void enterFight ()
    {
        if (!inFighting)
        {
            super.enterFight();
            enterFightTime = System.currentTimeMillis();
            lastFightTime = System.currentTimeMillis();
        }
    }

    /**
     * 获取攻击目标
     * 
     * @return
     */
    public HeroPlayer getAttackTarget ()
    {
        if (null != provocateur && provocateur.where() == where()
                && provocateur.isEnable() && !provocateur.isDead())
        {
            return provocateur;
        }
        else
        {
            synchronized (hatredTargetList)
            {
                Collections.sort(hatredTargetList, comparator);

                for (HatredTarget target : hatredTargetList)
                {
                    if (null != target.player
                            && target.player.where() == where()
                            && target.player.isEnable()
                            && !target.player.isDead())
                    {
                        return target.player;
                    }
                }
            }
        }

        return null;
    }

    /**
     * 获取进入战斗的时间点
     * 
     * @return
     */
    public long getTimeOfEnterFight ()
    {
        return enterFightTime;
    }

    /**
     * 设置死亡时间
     * 
     * @param _dieTime
     */
    public void setDieTime (long _dieTime)
    {
        dieTime = _dieTime;
    }

    /**
     * 获取死亡时间
     * 
     * @return
     */
    public long getDieTime ()
    {
        return dieTime;
    }

    /**
     * 设置变身状态
     * 
     * @param _isOrNo
     */
    public void setChangesStatus (boolean _isOrNo)
    {
        isChangesStatus = _isOrNo;
    }

    /**
     * 是否处于变身状态
     * 
     * @return
     */
    public boolean isChangesStatus ()
    {
        return isChangesStatus;
    }

    /**
     * 开始逃跑
     * 
     * @param _distance 距离
     */
    public void beginRunAway (int _distance)
    {
        runingAway = true;
    }

    /**
     * 停止逃跑
     */
    public void stopRunAway ()
    {
        runingAway = false;
    }

    /**
     * 是否正在逃跑中
     * 
     * @return
     */
    public boolean isRunningAway ()
    {
        return runingAway;
    }

    public MonsterAI getAI ()
    {
        return ai;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hero.share.ME2GameObject#revive(hero.share.ME2GameObject) 重生
     */
    public void revive (ME2GameObject _savior)
    {
        setHp(getActualProperty().getHpMax());
        setMp(getActualProperty().getMpMax());
        setCellX(getOrgX());
        setCellY(getOrgY());

        super.revive(_savior);
        super.active();
        setDirection((byte) (random.nextInt(4) + 1));
        setMoveSpeed(MoveSpeed.GENERIC);

        where().getMonsterList().add(this);

        MapSynchronousInfoBroadcast.getInstance().put(
                Constant.CLIENT_OF_HIGH_SIDE, where(),
                new MonsterRefreshNotify(Constant.CLIENT_OF_HIGH_SIDE, this),
                false, 0);
        //del by zhengl date: 2011-05-06; note: 客户端已经不再使用该值.统一使用:CLIENT_OF_HIGH_SIDE
        //减少MapSynchronousInfoBroadcast.infoList 的大小,较少for循环次数
//        MapSynchronousInfoBroadcast.getInstance().put(
//                Constant.CLIENT_OF_MIDDLE_SIDE, where(),
//                new MonsterRefreshNotify(Constant.CLIENT_OF_MIDDLE_SIDE, this),
//                false, 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hero.share.ME2GameObject#die()
     */
    public void die (ME2GameObject _killer)
    {
        super.die(_killer);
        invalid();

        dieTime = System.currentTimeMillis();
        ai.clearDynamicData();
        where().getMonsterList().remove(this);

        if (!GatherServerImpl.getInstance().processSoulWhenMonsterDied(this))
        {
            SpecialViewStatusBroadcast.send(this, SpecialStatusDefine.DIE);

            if (isInDungeon)
            {
                DungeonServiceImpl.getInstance().processMonsterDie(this,
                        where().getDungeon());
            }

            if (null != attackerAtFirst)
            {
                ArrayList<TaskGoodsLegacyInfo> legacyTaskGoodsInfoList = TaskServiceImpl
                        .getInstance().processTaskAboutMonster(this,
                                attackerAtFirst);

                try
                {
                    NotPlayerServiceImpl.getInstance().processMonsterLegacy(
                            this, getActualLegacyList(),
                            legacyTaskGoodsInfoList);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        clearFightInfo();

        if (isCalled)
        {
            destroy();
        }
    }

    /**
     * 设置掉落物品信息
     * 
     * @param _dropItemList
     */
    public void setLegacyListInfo (byte _legacyTypeMostNums,
            byte _legacyTypeSmallestNums)
    {
        legacyTypeMostNums = _legacyTypeMostNums;
        legacyTypeSmallestNums = _legacyTypeSmallestNums;
    }

    /**
     * 添加掉落物品
     * 
     * @param _itemID 物品编号
     * @param _odds 掉落几率
     * @param _num 掉落数量
     */
    public void addLegacy (int _itemID, float _odds, int _num)
    {
        if (null == legacyList)
        {
            legacyList = new ArrayList<float[]>();
        }
        log.debug("monster add legacy itemID = " + _itemID+",odds="+_odds+",num="+_num);
        legacyList.add(new float[]{_itemID, _odds, _num });
    }

    /**
     * 获取实际物品掉落清单
     * 
     * @return 物品编号和数量
     */
    public ArrayList<int[]> getActualLegacyList ()
    {
        try
        {
        	long monsterTime = System.currentTimeMillis();
        	log.info("[" + this.getName() + "]开始计算掉落时间," 
        			+ String.valueOf(monsterTime) 
        			+ ";objectid=" + String.valueOf(getID()));
        	ArrayList<int[]> itemList = new ArrayList<int[]>();

            ArrayList<Integer> worldLegacyGoodsID = WorldLegacyDict.getInstance()
                    .getLegacyGoodID(getLevel());
            log.info("怪物死亡，世界掉落 数量： ="+worldLegacyGoodsID.size());
            if (worldLegacyGoodsID.size() > 0)
            {
                for (Integer goodsID : worldLegacyGoodsID){
                    itemList.add(new int[]{goodsID, 1 });
                }
            }
            monsterTime = System.currentTimeMillis();
        	log.info("[" + this.getName() + "]完成第1个循环," 
        			+ String.valueOf(monsterTime) 
        			+ ";objectid=" + String.valueOf(getID()));

            if (null == legacyList)
            {
                if (0 != itemList.size())
                {
                    log.info("[" + this.getName() + "]完成所有循环return," 
                			+ String.valueOf(monsterTime) 
                			+ ";objectid=" + String.valueOf(getID()));
                    return itemList;
                }
                log.info("[" + this.getName() + "]完成所有循环return," 
            			+ String.valueOf(monsterTime) 
            			+ ";objectid=" + String.valueOf(getID()));
                return null;
            }

            int legacyTypeNums = random.nextInt(legacyTypeMostNums + 1);

            while (legacyTypeNums < legacyTypeSmallestNums)
            {
                legacyTypeNums = random.nextInt(legacyTypeMostNums + 1);
            }
        	log.info("[" + this.getName() + "]完成第2个循环," 
        			+ String.valueOf(monsterTime) 
        			+ ";objectid=" + String.valueOf(getID()));

            if (0 == legacyTypeNums)
            {
                log.info("[" + this.getName() + "]完成所有循环return," 
            			+ String.valueOf(monsterTime) 
            			+ ";objectid=" + String.valueOf(getID()));
                return null;
            }
            while (legacyTypeNums > 0)
            {
                int randomIndex = random.nextInt(legacyList.size());

                boolean exist = false;

                for (int[] dropItem : itemList)
                {
                    if (dropItem[0] == legacyList.get(randomIndex)[0])
                    {
                        exist = true;

                        break;
                    }
                }

                if (!exist)
                {
                    float temp = random.nextFloat();

                    if (temp <= legacyList.get(randomIndex)[1])
                    {
                        int dropNums = random.nextInt((int) (legacyList
                                .get(randomIndex)[2])) + 1;
                        itemList
                                .add(new int[]{
                                        (int) legacyList.get(randomIndex)[0],
                                        dropNums });
                    }

                    legacyTypeNums--;
                }
            }
        	log.info("[" + this.getName() + "]完成第3个嵌套循环准备退出方法," 
        			+ String.valueOf(monsterTime) 
        			+ ";objectid=" + String.valueOf(getID()));

            /*ArrayList<Integer> worldLegacyGoodsID = WorldLegacyDict.getInstance()
                    .getLegacyGoodID(getLevel());
            log.debug("怪物死亡，世界掉落 数量： ="+worldLegacyGoodsID.size());
            if (worldLegacyGoodsID.size() > 0)
            {

                for (Integer goodsID : worldLegacyGoodsID){
                    log.debug("怪物死亡，世界掉落 goodsid = " + goodsID);
                    itemList.add(new int[]{goodsID, 1 });
                }
            }*/

            if (0 != itemList.size())
            {
                return itemList;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public int getBaseExp ()
    {
        return basicExp;
    }

    public void setBaseExp (int _experience)
    {
        basicExp = _experience;
    }

    /**
     * 得到怪物灵魂ID
     * 
     * @return
     */
    public int getSoulID ()
    {
        if (null != soulIDList)
        {
            return soulIDList.get(RANDOM.nextInt(soulIDList.size()));
        }

        return 0;
    }

    /**
     * 设置怪物灵魂ID列表
     * 
     * @param _soulID
     */
    public void setSoulIDList (Vector<Integer> _soulIDList)
    {
        soulIDList = _soulIDList;
    }

    /**
     * 得到灵魂吸收者玩家ID
     * 
     * @return
     */
    public int getTakeSoulUserID ()
    {
        return takeSoulUserID;
    }

    /**
     * 设置灵魂吸收者玩家ID
     * 
     * @param _takeSoulUserID
     */
    public void setTakeSoulUserID (int _takeSoulUserID)
    {
        takeSoulUserID = _takeSoulUserID;
    }

    /**
     * @param _attackChain
     */
    public void setAttackChain (int _range, int _factor)
    {
        if (_range <= 0 || _factor <= 0)
        {
            return;
        }

        AttackChain chain = new AttackChain();

        chain.range = _range;
        chain.factor = _factor;

        attackChain = chain;
    }

    /**
     * @return
     */
    public AttackChain getAttackChain ()
    {
        return this.attackChain;
    }

    /**
     * 清除战斗数据
     */
    public void clearFightInfo ()
    {
        super.disengageFight();
        enterFightTime = 0;
        lastAttackTime = 0;
        lastFightTime = 0;
        takeSoulUserID = 0;
        attackerAtFirst = null;

        clearEffect();

        synchronized (hatredTargetList)
        {
            for (HatredTarget target : hatredTargetList)
            {
                if (null != target)
                {
                    target.player.removeHatredSource(this);
                }
            }

            hatredTargetList.clear();
        }

        changeInvention(ENotPlayerIntention.AI_INTENTION_ACTIVE, null);
    }

    /*
     * 只被AI调用 (non-Javadoc)
     * 
     * @see hero.share.ME2GameObject#disengageFight()
     */
    public synchronized void disengageFight ()
    {
        if (enabled)
        {
            clearFightInfo();
            setCellX(getOrgX());
            setCellY(getOrgY());
            setHp(getActualProperty().getHpMax());
            setMp(getActualProperty().getMpMax());

            MapSynchronousInfoBroadcast.getInstance().put(
                    where(),
                    new MonsterDisengageFightNotify(getID(), getCellX(),
                            getCellY()), false, 0);
        }
    }

    /**
     * 设置攻击AI编号
     * 
     * @param _aiID
     */
    public void setAttackAiID (int _attackAiID)
    {
        attackAiID = _attackAiID;
    }

    /**
     * 获取攻击AI编号
     * 
     * @return
     */
    public int getFightAIID ()
    {
        return attackAiID;
    }

    /**
     * 设置攻击类型
     * 
     * @param _attackType
     */
    public void setActiveAttackType (boolean _attackType)
    {
        isActiveAttackType = _attackType;
    }

    /**
     * 是否是主动攻击类型
     * 
     * @return
     */
    public boolean isActiveAttackType ()
    {
        return isActiveAttackType;
    }

    /**
     * 被嘲讽
     * 
     * @param _player 施放技能者
     */
    public void beProvoke (HeroPlayer _player)
    {
        if (null != _player && provocateur != _player)
        {
            provocateur = _player;

            HatredTarget firstHatredTarget = hatredTargetList.get(0);

            synchronized (hatredTargetList)
            {
                if (null != firstHatredTarget
                        && firstHatredTarget.player != _player)
                {
                    for (int i = 0; i < hatredTargetList.size(); i++)
                    {
                        HatredTarget target = hatredTargetList.get(i);

                        if (target.player == _player)
                        {
                            target.hatredValue = firstHatredTarget.hatredValue;

                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * 结束嘲讽
     */
    public void endProvoke ()
    {
        provocateur = null;
    }

    /**
     * 获取挑衅者
     * 
     * @return
     */
    public HeroPlayer getProvocateur ()
    {
        return provocateur;
    }

    @Override
    public boolean canBeAttackBy (ME2GameObject _object)
    {
        // TODO Auto-generated method stub
        if (_object.getClan() == getClan())
        {
            return false;
        }

        return true;
    }

    /**
     * 获取怪物类别等级
     * 
     * @return
     */
    public EMonsterLevel getMonsterLevel ()
    {
        return monsterLevel;
    }

    /**
     * 设置怪物类别等级
     * 
     * @param _monsterLevel
     */
    public void setMonsterLevel (EMonsterLevel _monsterLevel)
    {
        monsterLevel = _monsterLevel;
    }

    /**
     * 是否在副本中
     * 
     * @return
     */
    public boolean isInDungeon ()
    {
        return isInDungeon;
    }

    /**
     * 设置为在副本中的怪
     */
    public void setInDungeon ()
    {
        isInDungeon = true;
    }

    /**
     * 增加仇恨值
     * 
     * @param _player
     * @param _value
     */
    public void addTargetHatredValue (HeroPlayer _player, int _value)
    {
        synchronized (hatredTargetList)
        {
            for (int i = 0; i < hatredTargetList.size(); i++)
            {
                HatredTarget target = hatredTargetList.get(i);

                if (target.player == _player)
                {
                    target.hatredValue += _player.getHatredModulus() * _value;

                    if (target.hatredValue < 0)
                    {
                        target.hatredValue = 0;
                    }

                    return;
                }
            }

            hatredTargetList.add(new HatredTarget(_player, (int) (_player
                    .getHatredModulus() * _value)));
        }

        _player.enterFight();
        _player.addHatredSource(this);

        if (!inFighting)
        {
            changeInvention(ENotPlayerIntention.AI_INTENTION_ATTACK, _player);
        }
    }

    /**
     * 添加仇恨目标
     * 
     * @param _player
     * @param _value
     */
    public void addHattedTarget (HeroPlayer _player)
    {
        synchronized (hatredTargetList)
        {
            hatredTargetList.add(new HatredTarget(_player, 0));
        }

        _player.addHatredSource(this);
    }

    /**
     * 改变指定仇恨位置玩家的的仇恨值
     * 
     * @param _player
     * @param _value
     */
    public void changeTargetHattedPercent (int _hatredSequence, int _value)
    {
        HatredTarget target = hatredTargetList.get(_hatredSequence);

        if (null != target)
        {
            target.hatredValue += _value;

            if (target.hatredValue < 0)
            {
                target.hatredValue = 0;
            }
        }
    }

    /**
     * 受到伤害
     * 
     * @param _player
     * @param _value
     */
    public void beHarmed (HeroPlayer _player, int _value)
    {
        if (_value < 0 || !enabled)
        {
            return;
        }

        synchronized (hatredTargetList)
        {
            for (int i = 0; i < hatredTargetList.size(); i++)
            {
                HatredTarget target = hatredTargetList.get(i);

                if (target.player == _player)
                {
                    target.hatredValue += _player.getHatredModulus() * _value;

                    return;
                }
            }

            hatredTargetList.add(new HatredTarget(_player, (int) (_player
                    .getHatredModulus() * _value)));
            _player.addHatredSource(this);
        }

        _player.enterFight();

        if (!inFighting)
        {
            changeInvention(ENotPlayerIntention.AI_INTENTION_ATTACK, _player);
        }
    }


    /**
     * 目标受到有效治疗
     * 
     * @param _releaser 治疗者
     * @param _value 恢复的有效生命值
     */
    public void targetBeResumed (HeroPlayer _releaser, int _value)
    {
        if (_releaser == null || _value <= 0)
        {
            return;
        }
       
        synchronized (hatredTargetList)
        {
            for (int i = 0; i < hatredTargetList.size(); i++)
            {
                HatredTarget target = hatredTargetList.get(i);

                if (target.player == _releaser)
                {
                    target.hatredValue += _releaser.getHatredModulus() * _value
                            / 2;
                  
                    return;
                }
            }

            hatredTargetList.add(new HatredTarget(_releaser, (int) (_releaser
                    .getHatredModulus()
                    * _value / 2)));
            _releaser.addHatredSource(this);
        }

        _releaser.enterFight();
    }

    /**
     * 改变已有仇恨值的百分比
     * 
     * @param _player
     * @param _value
     */
    public void changeHattedPercent (HeroPlayer _player, float _percent)
    {
        synchronized (hatredTargetList)
        {
            for (HatredTarget target : hatredTargetList)
            {
                if (target.player == _player)
                {
                    target.hatredValue += target.hatredValue * _percent;

                    if (target.hatredValue < 0)
                    {
                        target.hatredValue = 0;
                    }

                    return;
                }
            }
        }
    }

    /**
     * 改变指定仇恨位置玩家的的仇恨值的百分比
     * 
     * @param _index
     * @param _value
     */
    public void changeHattedPercent (int _hatredSequence, float _percent)
    {
        HatredTarget target = hatredTargetList.get(_hatredSequence);

        if (null != target)
        {
            target.hatredValue += target.hatredValue * _percent;

            if (target.hatredValue < 0)
            {
                target.hatredValue = 0;
            }
        }
    }

    /**
     * 获取仇恨对象数量
     * 
     * @return
     */
    public int getHatredTargetNums ()
    {
        return hatredTargetList.size();
    }

    /**
     * 获取第一个造成伤害的人
     * 
     * @return
     */
    public HeroPlayer getAttackerAtFirst ()
    {
        return attackerAtFirst;
    }

    /**
     * 设置第一个造成伤害的人
     * 
     * @return
     */
    public void setAttackerAtFirst (HeroPlayer _player)
    {
        if (null == attackerAtFirst)
        {
            attackerAtFirst = _player;
        }
    }

    /**
     * 是否在有效战斗状态时间中
     * 
     * @return
     */
    public boolean inValidateFightTime ()
    {
        return 0 < lastFightTime
                && System.currentTimeMillis() - lastFightTime < FightConfig.DISENGAGE_FIGHT_TIME;
    }

    /**
     * 发生战斗（攻击玩家或被玩家攻击）
     */
    public void happenFight ()
    {
        lastFightTime = System.currentTimeMillis();
    }

    /**
     * 获取仇恨列表中指定位置的玩家
     * 
     * @param _hatredSequence 从1开始
     * @return
     */
    public HeroPlayer getHatredTargetByHatredSequence (int _hatredSequence)
    {
        if (hatredTargetList.size() >= _hatredSequence)
        {
            HeroPlayer player = hatredTargetList.get(_hatredSequence - 1).player;
//            for (int i = 0; i < hatredTargetList.size(); i++) {
//            	HeroPlayer p = hatredTargetList.get(i).player;
//            	if(p != null){
//            		log.info("仇恨列表:"+p.getName());
//            	} else {
//					log.info("第"+i+"个仇恨列表为null");
//				}
//			}

            if (!player.isDead())
            {
                return player;
            }
        }

        return null;
    }

    /**
     * 清除效果列表
     */
    private void clearEffect ()
    {
        synchronized (effectList)
        {
            if (effectList.size() > 0)
            {
                Effect effect;

                for (int i = 0; i < effectList.size(); i++)
                {
                    try
                    {
                        effect = effectList.get(i);
                        effect.destory(this);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();

                        break;
                    }
                }

                effectList.clear();
            }
        }
    }

    /**
     * 获取仇恨列表中的玩家
     * 
     * @return
     */
    public ArrayList<HeroPlayer> getHatredTargetList ()
    {
        if (hatredTargetList.size() > 0)
        {
            synchronized (hatredTargetList)
            {
                ArrayList<HeroPlayer> targetList = new ArrayList<HeroPlayer>();

                for (HatredTarget target : hatredTargetList)
                {
                    targetList.add(target.player);
                }

                return targetList;
            }
        }

        return null;
    }

    /**
     * 复制仇恨列表，仇恨值为0
     * 
     * @param _hatredTargetList
     */
    public void copyHatredTargetList (ArrayList<HeroPlayer> _hatredTargetList)
    {
        synchronized (hatredTargetList)
        {
            hatredTargetList.clear();

            for (HeroPlayer player : _hatredTargetList)
            {
                hatredTargetList.add(new HatredTarget(player, 0));
            }
        }
    }

    /**
     * 获取仇恨最高玩家
     * 
     * @return
     */
    public HeroPlayer getTopHatredTarget ()
    {
        return hatredTargetList.get(0).player;
    }

    /**
     * 仇恨目标远去
     * 
     * @param _player
     * @return
     */
    public void hatredTargetAway (HeroPlayer _player)
    {
        synchronized (hatredTargetList)
        {
            for (int i = 0; i < hatredTargetList.size(); i++)
            {
                HatredTarget target = hatredTargetList.get(i);

                if (target.player == _player)
                {
                    hatredTargetList.remove(i);
                    _player.removeHatredSource(this);

                    if (attackerAtFirst == _player)
                    {
                        attackerAtFirst = null;
                    }

                    return;
                }
            }
        }
    }

    /**
     * 清除仇恨值（成员下线、死亡）
     * 
     * @param _player
     * @return
     */
    public void clearHatred (HeroPlayer _player)
    {
        synchronized (hatredTargetList)
        {
            for (int i = 0; i < hatredTargetList.size(); i++)
            {
                if (hatredTargetList.get(i).player == _player)
                {
                    hatredTargetList.remove(i);

                    return;
                }
            }
        }
    }

    /**
     * 仇恨目标
     * 
     * @author DC
     */
    public class HatredTarget
    {
        /**
         * 玩家
         */
        private HeroPlayer player;

        /**
         * 仇恨值
         */
        private int        hatredValue;

        /**
         * 构造
         * 
         * @param _player
         * @param _hatredValue
         */
        public HatredTarget(HeroPlayer _player, int _hatredValue)
        {
            player = _player;
            hatredValue = _hatredValue;
        }
    }

    public static class HatredComparator<T> implements Comparator<T>
    {
        /*
         * (non-Javadoc)
         * 
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare (Object _o1, Object _o2)
        {
            return ((HatredTarget) _o2).hatredValue
                    - ((HatredTarget) _o1).hatredValue;
        }
    }

    /**
     * 随机数生成器
     */
    private static final Random RANDOM = new Random();

    @Override
    public byte getDefaultSpeed ()
    {
        // TODO Auto-generated method stub
        return MoveSpeed.GENERIC;
    }
}
