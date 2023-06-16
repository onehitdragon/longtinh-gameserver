package hero.item.special;

import hero.gather.dict.Refined;
import hero.gather.dict.RefinedDict;
import hero.gather.service.GatherServerImpl;
import hero.item.SpecialGoods;
import hero.log.service.LogServiceImpl;
import hero.manufacture.dict.ManufSkill;
import hero.manufacture.dict.ManufSkillDict;
import hero.manufacture.service.GetTypeOfSkillItem;
import hero.manufacture.service.ManufactureServerImpl;
import hero.skill.service.SkillServiceImpl;
import hero.player.HeroPlayer;
import hero.share.service.LogWriter;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SkillBook.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-1-24 下午12:06:58
 * @描述 ：
 */

public class SkillBook extends SpecialGoods
{
    /**
     * 使用后获得的技能编号
     */
    private int skillID;

    /**
     * 构造
     * 
     * @param _id
     * @param _stackNums
     */
    public SkillBook(int _id, short _stackNums)
    {
        super(_id, _stackNums);
        // TODO Auto-generated constructor stub
    }

    /**
     * 设置使用后获得的技能编号
     * 
     * @param _skill
     */
    public void setSkillID (int _skillID)
    {
        skillID = _skillID;
    }

    @Override
    public boolean beUse (HeroPlayer _player, Object _target, int _location)
    {
        // TODO Auto-generated method stub
        boolean res  = SkillServiceImpl.getInstance().comprehendSkill(_player, skillID);
        if(res){
            //如果使用成功，则记录使用日志
        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                                    _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getType().getDescription());
        }
        return res;
    }

    @Override
    public boolean disappearImmediatelyAfterUse ()
    {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public ESpecialGoodsType getType ()
    {
        // TODO Auto-generated method stub
        return ESpecialGoodsType.SKILL_BOOK;
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
        return true;
    }

}
