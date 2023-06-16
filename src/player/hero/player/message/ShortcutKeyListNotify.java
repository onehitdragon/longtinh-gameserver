package hero.player.message;

import hero.item.Goods;
import hero.item.Medicament;
import hero.item.detail.EGoodsType;
import hero.item.service.GoodsServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.cd.CDUnit;

import java.io.IOException;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ShortcutKeyListNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-4-23 上午10:05:11
 * @描述 ：物品快捷键列表通知
 */

public class ShortcutKeyListNotify extends AbsResponseMessage
{
    private static Logger log = Logger.getLogger(ShortcutKeyListNotify.class);
    /**
     * 角色对象
     */
    private HeroPlayer player;

    /**
     * 构造
     * 
     * @param _player
     */
    public ShortcutKeyListNotify(HeroPlayer _player)
    {
        player = _player;
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
        int[][] shortKey = player.getShortcutKeyList();

        for (int j = 0; j < shortKey.length; j++)
        {
            yos.writeByte(shortKey[j][0]);

            if (shortKey[j][0] > 0)
            {
                switch (shortKey[j][0])
                {
                    case PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SKILL:
                    {
                        yos.writeInt(shortKey[j][1]);

                        break;
                    }
                    case PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_SYSTEM:
                    {
                        yos.writeInt(shortKey[j][1]);

                        break;
                    }
                    case PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_GOODS:
                    {
                        log.debug("shortKey["+j+"][1]="+shortKey[j][1]);
                        Goods goods = GoodsServiceImpl.getInstance()
                                .getGoodsByID(shortKey[j][1]);
                        log.debug("shortKey goods = " + goods);
                        yos.writeInt(goods.getID());
                        yos.writeUTF(goods.getName());
                        yos.writeShort(goods.getIconID());

                        if (goods.getGoodsType() == EGoodsType.MEDICAMENT)
                        {
                            Medicament medicament = (Medicament) goods;
                            CDUnit cd = player.userCDMap.get(medicament
                                    .getPublicCdVariable());
                            yos.writeInt(medicament.getMaxCdTime());// 最大冷却时间

                            if (medicament.getMaxCdTime() != 0)
                            {
                                if (cd == null)
                                {
                                    yos.writeInt(0);// 剩余冷却时间
                                }
                                else
                                {
                                    yos.writeInt(cd.getTimeBySec());// 剩余冷却时间
                                }
                                yos.writeShort(medicament
                                        .getPublicCdVariable());// 共享冷却变量
                            }
                        }
                        else
                        {
                            yos.writeInt(0);
                        }

                        break;
                    }
                }
            }
        } //for end
    }
}
