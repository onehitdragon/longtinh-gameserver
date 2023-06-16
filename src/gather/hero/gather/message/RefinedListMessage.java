package hero.gather.message;

import hero.gather.Gather;
import hero.gather.RefinedCategory;
import hero.gather.dict.Refined;
import hero.gather.dict.RefinedDict;
import hero.gather.service.GatherServerImpl;
import hero.item.Goods;
import hero.item.dictionary.GoodsContents;

import java.io.IOException;
import java.util.ArrayList;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 炼制列表消息 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class RefinedListMessage extends AbsResponseMessage
{
    private String title;

    private Gather gather;

    public RefinedListMessage(String _title, Gather _gather)
    {
        title = _title;
        gather = _gather;
    }

    @Override
    public int getPriority ()
    {
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        yos.writeUTF(title);
        yos.writeInt(gather.getPoint());
        yos.writeInt(GatherServerImpl.POINT_LIMIT[gather.getLvl() - 1]);
        yos.writeByte(gather.getLvl());

        String[] _str = RefinedCategory.getCategorys();
        yos.writeByte(_str.length);
        for (String _s : _str)
        {
            yos.writeUTF(_s);
        }

        ArrayList<Integer> gatherSkillIDList = gather.getRefinedList();

        yos.writeShort(gatherSkillIDList.size());

        for (int _refinedID : gatherSkillIDList)
        {
            Refined _refined = RefinedDict.getInstance().getRefinedByID(
                    _refinedID);
            yos.writeInt(_refined.id);
            int goodsID = _refined.getGoodsID[1];
            Goods goods = GoodsContents.getGoods(goodsID);
            yos.writeShort(goods.getIconID());
            yos.writeUTF(goods.getName());
            yos.writeByte(goods.getTrait().value());
            yos.writeByte(_refined.category);
        }
    }

}
