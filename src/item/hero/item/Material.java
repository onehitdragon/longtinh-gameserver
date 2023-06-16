package hero.item;

import hero.item.detail.EGoodsType;
import hero.log.service.LogServiceImpl;
import hero.player.HeroPlayer;
import hero.share.ME2GameObject;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Material.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-17 下午03:05:00
 * @描述 ：材料，物品的子类
 */

public class Material extends SingleGoods
{

    /**
     * 构造
     * 
     * @param nums
     */
    public Material(short nums)
    {
        super(nums);
        // TODO Auto-generated constructor stub

        setNeedLevel(1);
    }

    @Override
    public byte getSingleGoodsType ()
    {
        // TODO Auto-generated method stub
        return SingleGoods.TYPE_MATERIAL;
    }

    @Override
    public EGoodsType getGoodsType ()
    {
        // TODO Auto-generated method stub
        return EGoodsType.MATERIAL;
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

    @Override
    public boolean beUse (HeroPlayer _player, Object _target)
    {
        // TODO Auto-generated method stub
        boolean res = true;
        if(res){
        //如果使用成功，则记录使用日志
        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                                    _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getGoodsType().getDescription());
        }
        return res;
    }
}
