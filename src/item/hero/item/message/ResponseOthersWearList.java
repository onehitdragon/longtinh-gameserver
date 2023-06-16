package hero.item.message;

import hero.item.bag.BodyWear;
import hero.item.service.GoodsServiceImpl;
import hero.ui.data.EquipmentPackageData;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseOthersWearList.java
 * @创建者 Lu Lin
 * @版本 1.0
 * @时间 2009-3-12 下午04:13:23
 * @描述 ：响应其他玩家着装栏
 */

public class ResponseOthersWearList extends AbsResponseMessage
{
    /**
     * 其他玩家着装栏
     */
    private BodyWear othersBodyBag;

    /**
     * 构造
     * 
     * @param _bodyPackage
     */
    public ResponseOthersWearList(BodyWear _bodyPackage)
    {
        othersBodyBag = _bodyPackage;
    }

    @Override
    public int getPriority ()
    {
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        yos.writeBytes(EquipmentPackageData.getData(othersBodyBag, 
        		GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name));
    }

}
