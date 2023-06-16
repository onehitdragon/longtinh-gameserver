package hero.item.special;

import hero.gather.dict.Refined;
import hero.gather.dict.RefinedDict;
import hero.gather.service.GatherServerImpl;
import hero.item.SpecialGoods;
import hero.log.service.LogServiceImpl;
import hero.manufacture.ManufactureType;
import hero.manufacture.dict.ManufSkill;
import hero.manufacture.dict.ManufSkillDict;
import hero.manufacture.service.GetTypeOfSkillItem;
import hero.manufacture.service.ManufactureServerImpl;
import hero.player.HeroPlayer;
import hero.share.service.LogWriter;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 AssistantSkillBook.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-8-4 上午09:12:23
 * @描述 ：图纸，辅助技能具体条目来源之一
 */

public class Drawings extends SpecialGoods
{
    /**
     * 需要的制造技能类型
     */
    private ManufactureType needManufactureType;

    /**
     * 使用后获得制造技能条目编号
     */
    private int             getSkillItemID;

    /**
     * 构造
     * 
     * @param _id
     * @param _stackNums
     */
    public Drawings(int _id, short _stackNums)
    {
        super(_id, _stackNums);
    }

    @Override
    public ESpecialGoodsType getType ()
    {
        // TODO Auto-generated method stub
        return ESpecialGoodsType.DRAWINGS;
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
     * 设置使用需要的制造技能类型
     */
    public void setNeedManufactureType (String _manufactureTypeDesc)
    {
        needManufactureType = ManufactureType.get(_manufactureTypeDesc);
    }

    /**
     * 获取需要制造技能
     * 
     * @return
     */
    public ManufactureType getNeedManufactureType ()
    {
        return needManufactureType;
    }

    /**
     * 设置使用后获得的技能条目编号
     * 
     * @param _manufactureItemID
     */
    public void setManufactureItemID (int _manufactureItemID)
    {
        getSkillItemID = _manufactureItemID;
    }

    @Override
    public boolean disappearImmediatelyAfterUse ()
    {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean beUse (HeroPlayer _player, Object _target, int _location)
    {
        // TODO Auto-generated method stub
        boolean res = false;
        if (getSkillItemID >= 40001)
        {
            Refined skillItem = RefinedDict.getInstance().getRefinedByID(
                    getSkillItemID);

            if (null != skillItem)
            {
                res = GatherServerImpl.getInstance().addRefinedItem(_player,
                        skillItem, GetTypeOfSkillItem.LEARN);
            }
            else
            {
                LogWriter.println("不存在的技能条目：" + getSkillItemID);
            }
        }
        else
        {
            ManufSkill skillItem = ManufSkillDict.getInstance()
                    .getManufSkillByID(getSkillItemID);

            if (null != skillItem)
            {
                res = ManufactureServerImpl.getInstance().addManufSkillItem(
                        _player, skillItem, GetTypeOfSkillItem.LEARN);
            }
            else
            {
                LogWriter.println("不存在的技能条目：" + getSkillItemID);
            }

        }
        if(res){
            LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getType().getDescription());
        }

        return false;
    }
}
