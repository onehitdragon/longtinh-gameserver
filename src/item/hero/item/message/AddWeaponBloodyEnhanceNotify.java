package hero.item.message;

import hero.item.EquipmentInstance;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 AddWeaponBloodyEnhanceNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-10-21 下午01:48:25
 * @描述 ：血腥强化发生变化
 */

public class AddWeaponBloodyEnhanceNotify extends AbsResponseMessage
{
    /**
     * 变化的武器实例
     */
    private EquipmentInstance weapon;

    /**
     * 构造
     * 
     * @param _weapon
     */
    public AddWeaponBloodyEnhanceNotify(EquipmentInstance _weapon)
    {
        weapon = _weapon;
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
        yos.writeInt(weapon.getInstanceID());
        yos.writeInt(weapon.getWeaponBloodyEnhance().getPveNumber());
        yos.writeInt(weapon.getWeaponBloodyEnhance().getPveUpgradeNumber());
        yos.writeByte(weapon.getWeaponBloodyEnhance().getPveLevel());

        if (null != weapon.getWeaponBloodyEnhance().getPveBuff())
        {
            yos.writeUTF(weapon.getWeaponBloodyEnhance().getPveBuff().desc);
        }

        yos.writeInt(weapon.getWeaponBloodyEnhance().getPvpNumber());
        yos.writeInt(weapon.getWeaponBloodyEnhance().getPvpUpgradeNumber());
        yos.writeByte(weapon.getWeaponBloodyEnhance().getPvpLevel());

        if (null != weapon.getWeaponBloodyEnhance().getPvpBuff())
        {
            yos.writeUTF(weapon.getWeaponBloodyEnhance().getPvpBuff().desc);
        }
    }
}
