package hero.item;

import static hero.item.detail.EGoodsType.MEDICAMENT;

import java.util.Random;

import yoyo.core.queue.ResponseMessageQueue;

import hero.log.service.LogServiceImpl;

import hero.effect.service.EffectServiceImpl;
import hero.fight.service.FightServiceImpl;
import hero.item.detail.AdditionEffect;
import hero.item.detail.EGoodsType;
import hero.skill.detail.ETouchType;
import hero.skill.service.SkillServiceImpl;
import hero.player.HeroPlayer;
import hero.player.message.HotKeySumByMedicament;
import hero.share.ME2GameObject;

/**
 * 药品类
 * 
 * @author Luke chen
 * @date 2009-2-16
 */
public class Medicament extends SingleGoods
{
    /**
     * 战斗中是否可以使用
     */
    private boolean         canUseInFight;

    /**
     * 死亡后是否消失
     */
    private boolean         isDisappearAfterDead;

    /**
     * 公共使用冷却变量
     */
    private int             publicCdVariable;

    /**
     * 最大冷却时间
     */
    private int             maxCdTime;

    /**
     * 剩余冷却时间
     */
    private int             traceCdTime;

    /**
     * 恢复的生命值
     */
    private int             resumeHp;

    /**
     * 恢复的魔法值
     */
    private int             resumeMp;

    /**
     * 恢复的力槽值
     */
    private int             resumeForceQuantity;

    /**
     * 恢复的气槽值
     */
    private int             resumeGasQuantity;

    /**
     * 使用时的动画动画编号
     */
    private int             releaseAnimation;
    
    /**
     * 使用时候的图片编号
     */
    private int             releaseImage;

    /**
     * 附加效果
     */
    public AdditionEffect[] additionEffectList;

    /**
     * @param _stackNumss
     */
    public Medicament(short _stackNumss)
    {
        super(_stackNumss);
    }

    @Override
    public EGoodsType getGoodsType ()
    {
        // TODO Auto-generated method stub
        return MEDICAMENT;
    }

    @Override
    public void initDescription ()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isIOGoods ()
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * 设置恢复的生命值
     * 
     * @param _resumeHp
     */
    public void setResumeHp (int _resumeHp)
    {
        resumeHp = _resumeHp;
    }

    /**
     * 获取恢复的生命值
     * 
     * @return
     */
    public int getResumeHp ()
    {
        return resumeHp;
    }

    /**
     * 设置恢复的魔法值
     * 
     * @param _resumeMp
     */
    public void setResumeMp (int _resumeMp)
    {
        resumeMp = _resumeMp;
    }

    /**
     * 获取恢复的魔法值
     * 
     * @return
     */
    public int getResumeMp ()
    {
        return resumeMp;
    }

    /**
     * 设置恢复的力槽值
     * 
     * @param _resumeForceQuantity
     */
    public void setResumeForceQuantity (int _resumeForceQuantity)
    {
        resumeForceQuantity = _resumeForceQuantity;
    }

    /**
     * 获取恢复的力槽值
     * 
     * @return
     */
    public int getResumeForceQuantity ()
    {
        return resumeForceQuantity;
    }

    /**
     * 设置恢复的气槽值
     * 
     * @param resumeGasQuantity
     */
    public void setResumeGasQuantity (int _resumeGasQuantity)
    {
        resumeGasQuantity = _resumeGasQuantity;
    }

    /**
     * 获取恢复的气槽值
     * 
     * @return
     */
    public int getResumeGasQuantity ()
    {
        return resumeGasQuantity;
    }

    /**
     * 战斗中能否使用
     * 
     * @return
     */
    public boolean canUseInFight ()
    {
        return canUseInFight;
    }

    /**
     * 设置是否在战斗中使用
     * 
     * @param _canUse
     */
    public void setCanUseInFight (boolean _canUse)
    {
        canUseInFight = _canUse;
    }

    /**
     * 角色死亡后时候产生的持续效果是否消失
     * 
     * @return
     */
    public boolean isDisappearAfterDead ()
    {
        return isDisappearAfterDead;
    }

    /**
     * 设置死亡后产生的持续效果是否消失
     * 
     * @param _isDisappear
     */
    public void setIsDisappearAfterDead (boolean _isDisappear)
    {
        isDisappearAfterDead = _isDisappear;
    }

    /**
     * 设置与其他药水的公共冷却变量
     * 
     * @return
     */
    public int getPublicCdVariable ()
    {
        return publicCdVariable;
    }

    /**
     * 获取与其他药水的公共冷却变量
     * 
     * @param _publicCdVariable
     */
    public void setPublicCdVariable (int _publicCdVariable)
    {
        publicCdVariable = _publicCdVariable;
    }

    /**
     * 获取最大冷却时间
     * 
     * @return
     */
    public int getMaxCdTime ()
    {
        return maxCdTime;
    }

    /**
     * 设置最大冷却时间
     * 
     * @param _maxCdTime
     */
    public void setMaxCdTime (int _maxCdTime)
    {
        maxCdTime = _maxCdTime;
    }

    /**
     * 获取喝下时的动画图片编号
     * 
     * @return
     */
    public int getReleaseImage ()
    {
        return releaseImage;
    }

    /**
     * 设置喝下时的动画图片编号
     * 
     * @param _releaseAnimation
     */
    public void setReleaseImage (int _releaseImage)
    {
    	releaseImage = _releaseImage;
    }
    
    /**
     * 获取喝下时的动画动画编号
     * 
     * @return
     */
    public int getReleaseAnimation ()
    {
        return releaseAnimation;
    }

    /**
     * 设置喝下时的动画动画编号
     * 
     * @param _releaseAnimation
     */
    public void setReleaseAnimation (int _releaseAnimation)
    {
        releaseAnimation = _releaseAnimation;
    }

    /**
     * 获取剩余冷却时间
     * 
     * @return
     */
    public int getTraceCdTime ()
    {
        return traceCdTime;
    }

    @Override
    public byte getSingleGoodsType ()
    {
        // TODO Auto-generated method stub
        return SingleGoods.TYPE_MEDICAMENT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hero.item.IItemCanBeUsed#beUse(hero.player.HeroPlayer,
     *      hero.share.ME2GameObject)
     */
    public boolean beUse (HeroPlayer _player, Object _target)
    {
        // TODO Auto-generated method stub

        if (_player.isDead())
            return false;
        //edit by zhengl; date: 2011-02-12; note:添加图片,0表示使用药品的时候没有作用动画也没有施法动作
        SkillServiceImpl.getInstance().sendSingleSkillAnimation(_player, null,
                releaseAnimation, releaseImage, 0, 0, (byte)-1, (byte)1, (byte)2, (byte)2, (byte)0);

        if (resumeHp > 0)
        {
            FightServiceImpl.getInstance().processAddHp(_player, _player,
                    resumeHp, true, false);
        }

        if (resumeMp > 0)
        {
            _player.addMp(resumeMp);
            FightServiceImpl.getInstance().processSingleTargetMpChange(_player, true);
        }

        if (resumeForceQuantity > 0)
        {
            _player.consumeForceQuantity(-resumeForceQuantity);
            FightServiceImpl.getInstance().processPersionalForceQuantityChange(
                    _player);
        }
        else if (resumeGasQuantity > 0)
        {
            _player.consumeGasQuantity(-resumeGasQuantity);
            FightServiceImpl.getInstance().processPersionalForceQuantityChange(
                    _player);

        }

        if (null != additionEffectList)
        {
            for (AdditionEffect additionEffect : additionEffectList)
            {
                if (RANDOM.nextFloat() <= additionEffect.activeOdds)
                {
                    EffectServiceImpl.getInstance().appendSkillEffect(_player,
                            _player, additionEffect.effectUnitID);
                }
            }
        }

        //如果使用成功，则记录使用日志
        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                                    _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getGoodsType().getDescription());
        return true;
    }

    private static final Random RANDOM = new Random();
}
