package hero.item.special;

import hero.item.SpecialGoods;
import hero.log.service.LogServiceImpl;
import hero.player.HeroPlayer;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 PersionalStoreLicence.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-19 上午09:31:49
 * @描述 ：
 */

public class PersionalStoreLicence extends SpecialGoods
{

    /**
     * 构造
     * 
     * @param _id
     * @param _stackNums
     */
    public PersionalStoreLicence(int _id, short _stackNums)
    {
        super(_id, _stackNums);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean beUse (HeroPlayer _player, Object _target, int _location)
    {
        // TODO Auto-generated method stub
        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getType().getDescription());
        return true;
    }

    @Override
    public boolean disappearImmediatelyAfterUse ()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ESpecialGoodsType getType ()
    {
        // TODO Auto-generated method stub
        return ESpecialGoodsType.SHOP_LICENCE;
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
