package hero.ui;

import hero.item.service.WeaponRankUnit;
import hero.item.service.WeaponRankManager;
import hero.share.service.LogWriter;

import java.io.IOException;

import yoyo.tools.YOYOOutputStream;

import javolution.util.FastList;


public class UI_WeaponRecord
{
    public static byte[] getBytes ()
    {
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());

            FastList<WeaponRankUnit> list = WeaponRankManager.getInstance()
                    .getRankList();

            output.writeByte(list.size());

            int i = 1;

            for (WeaponRankUnit rank : list)
            {
                output.writeByte(i);// 排名
                output.writeUTF(rank.ownerName);// 显示名称
                output.writeShort(rank.weapon.getIconID());// 武器图标
                output.writeUTF(rank.weapon.getName());// 武器名称
                output.writeBytes(rank.weapon.getFixPropertyBytes());
                output.writeByte(0);
                output.writeByte(rank.existSeal);
                output.writeShort(rank.weapon.getMaxDurabilityPoint());
                output.writeInt(rank.weapon.getRetrievePrice());
                output.writeInt(rank.bloodyEnhance.getPveNumber());
                output.writeInt(rank.bloodyEnhance.getPveUpgradeNumber());
                output.writeByte(rank.bloodyEnhance.getPveLevel());

                if (0 < rank.bloodyEnhance.getPveLevel())
                {
                    output.writeUTF(rank.bloodyEnhance.getPveBuff().desc);
                }

                output.writeInt(rank.bloodyEnhance.getPvpNumber());
                output.writeInt(rank.bloodyEnhance.getPvpUpgradeNumber());
                output.writeByte(rank.bloodyEnhance.getPvpLevel());

                if (0 < rank.bloodyEnhance.getPvpLevel())
                {
                    output.writeUTF(rank.bloodyEnhance.getPvpBuff().desc);
                }

                output.writeBytes(rank.genericEnhance);

                i++;
            }

            output.flush();

            return output.getBytes();
        }
        catch (Exception e)
        {
            LogWriter.error(null, e);
        }
        finally
        {
            try
            {
                output.close();
                output = null;
            }
            catch (IOException e)
            {
            }
        }

        return null;
    }

    /**
     * 获取UI类型
     * 
     * @return
     */
    public static EUIType getType ()
    {
        return EUIType.WEAPON_RECORD_LIST;
    }
}
