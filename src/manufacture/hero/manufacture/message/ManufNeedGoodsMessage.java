package hero.manufacture.message;

import hero.item.Goods;
import hero.item.dictionary.GoodsContents;
import hero.player.HeroPlayer;

import java.io.IOException;
import java.util.ArrayList;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 制造需要的物品 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class ManufNeedGoodsMessage extends AbsResponseMessage
{
    private int              manufID;

    private String           des;

    private ArrayList<Goods> goodsList;

    private ArrayList<Short> goodsNums;

    private HeroPlayer       player;

    public ManufNeedGoodsMessage(int _manufID, String _des, HeroPlayer _player)
    {
        manufID = _manufID;
        des = _des;
        player = _player;
        goodsList = new ArrayList<Goods>();
        goodsNums = new ArrayList<Short>();
    }

    public void addNeedGoods (int _goodsID, short goodsNum)
    {
        Goods g = GoodsContents.getGoods(_goodsID);
        goodsList.add(g);
        goodsNums.add(goodsNum);
    }

    @Override
    public int getPriority ()
    {
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        yos.writeInt(manufID);
        yos.writeUTF(des);
        yos.writeByte(goodsList.size());
        for (int i = 0; i < goodsList.size(); i++)
        {
            Goods goods = goodsList.get(i);
            yos.writeShort(goods.getIconID());
            yos.writeUTF(goods.getName());
            yos.writeShort(goodsNums.get(i));
            int num = player.getInventory().getMaterialBag().getGoodsNumber(
                    goods.getID());
            if (num >= goodsNums.get(i))
                yos.writeByte(goods.getTrait().value());
            else yos.writeByte(0);
            yos.writeShort(num);
        }
    }
}
