package hero.item.message;

import hero.item.EquipmentInstance;
import hero.item.bag.BodyWear;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 RefreshEquipmentDurabilityPoint.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-24 下午04:43:39
 * @描述 ：刷新身上穿着装备耐久度
 */

public class RefreshEquipmentDurabilityPoint extends AbsResponseMessage
{
    /**
     * 身上装备
     */
    private BodyWear bodyWear;

    /**
     * 构造
     * 
     * @param _eiList
     */
    public RefreshEquipmentDurabilityPoint(BodyWear _bodyWear)
    {
        bodyWear = _bodyWear;
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
        // TODO Auto-generated method stub
        yos.writeByte(bodyWear.getFullGridNumber());

        if (bodyWear.getFullGridNumber() > 0)
        {
            EquipmentInstance ei;

            for (int i = 0; i < bodyWear.getSize(); i++)
            {
                ei = bodyWear.get(i);

                if (null != ei)
                {
                    yos.writeByte(i);
                    yos.writeShort(ei.getCurrentDurabilityPoint());
                }
            }
        }
    }
}
