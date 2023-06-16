package hero.item.message;

import hero.item.bag.EquipmentBag;
import hero.item.service.GoodsServiceImpl;
import hero.player.HeroPlayer;
import hero.ui.data.EquipmentPackageData;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseEquipmentBag.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-10 下午03:38:08
 * @描述 ：响应装备背包
 */

public class ResponseEquipmentBag extends AbsResponseMessage
{
    /**
     * 装备背包栏
     */
    private EquipmentBag equipmentPackage;
    
    /**
     * 玩家
     */
    private HeroPlayer player;

    /**
     * 构造;
     * 响应装备背包
     * 
     * @param _equipmentPackage
     */
    public ResponseEquipmentBag(EquipmentBag _equipmentPackage,HeroPlayer player)
    {
        equipmentPackage = _equipmentPackage;
        this.player = player;
    }

    @Override
    public int getPriority ()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        yos.writeBytes(EquipmentPackageData.getData(equipmentPackage, 
        		GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name));
    }

}
