package hero.item.message;

import hero.item.bag.BodyWear;
import hero.item.service.GoodsServiceImpl;
import hero.player.HeroPlayer;
import hero.ui.data.EquipmentPackageData;

import java.io.IOException;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SendBodyWearList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-10 下午03:38:08
 * @描述 ：身上穿着装备列表
 */

public class SendBodyWearList extends AbsResponseMessage
{
	private static Logger log = Logger.getLogger(SendBodyWearList.class);
    /**
     * 着装栏
     */
    private BodyWear bodyWear;
    
    /**
     * 玩家
     */
    private HeroPlayer player;

    /**
     * 构造
     * 
     * @param _bodyWear
     */
    public SendBodyWearList(BodyWear _bodyWear, HeroPlayer player)
    {
        bodyWear = _bodyWear;
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
        yos.writeBytes(EquipmentPackageData.getData(bodyWear, 
        		GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name));
        log.info("output size = " + String.valueOf(yos.size()) 
        		+ "player id = " + String.valueOf(player.getUserID()));
    }
}
