package hero.ui.message;

import hero.item.Goods;
import hero.item.SingleGoods;
import hero.item.SpecialGoods;
import hero.item.TaskTool;
import hero.item.detail.EGoodsType;
import hero.share.CharacterDefine;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 NotifySinglePackageAddGoods.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-4-14 下午02:57:41
 * @描述 ：
 */

public class NotifyAddGoods2SinglePackage extends AbsResponseMessage
{
    /**
     * 变化的UI类型
     */
    private byte        uiType;

    /**
     * 变化结果，0:index,1:nums
     */
    private short[]     change;

    /**
     * 格子内的物品
     */
    private SingleGoods goods;

    /**
     * 快捷键列表
     */
    private int[][]     shortcutKeyList;

    /**
     * 构造
     * 
     * @param _uiType 背包类型
     * @param _change 变化
     * @param _goods 物品
     */
    public NotifyAddGoods2SinglePackage(byte _uiType, short[] _change,
            SingleGoods _goods, int[][] _shortcutKeyList)
    {
        uiType = _uiType;
        change = _change;
        goods = _goods;
        shortcutKeyList = _shortcutKeyList;
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
        yos.writeByte(uiType);
        yos.writeByte(change[0]);// 物品在背包中的位置
        yos.writeInt(goods.getID());// 物品在背包中的标识（装备采用模型编号或者实例的编号）
        yos.writeShort(goods.getIconID());// 物品图标
        yos.writeUTF(goods.getName());// 物品名称
        yos.writeByte(goods.getTrait().value());// 物品品质
        yos.writeShort(change[1]);// 物品数量
        yos.writeByte(goods.getMaxStackNums());// 物品可叠加的最大数量

        if (goods.canBeSell())
        {
            yos.writeUTF(goods.getDescription()
                    + CharacterDefine.DESC_NEW_LINE_CHAR + "出售价格："
                    + goods.getRetrievePrice());
        }
        else
        {
            yos.writeUTF(goods.getDescription()
                    + CharacterDefine.DESC_NEW_LINE_CHAR + "不可出售");
        }

        yos.writeByte(goods.exchangeable());
        yos.writeByte(goods.useable());

        if (goods.useable())
        {
            for (int j = 0; j < shortcutKeyList.length; j++)
            {
                if (shortcutKeyList[j][1] == goods.getID())
                {
                    yos.writeByte(j);

                    break;
                }
            }

        }
        else
        {
            yos.writeByte(-1);
        }
    }
}
