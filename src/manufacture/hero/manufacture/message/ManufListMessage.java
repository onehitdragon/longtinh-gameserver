package hero.manufacture.message;

import hero.item.Goods;
import hero.item.dictionary.GoodsContents;
import hero.manufacture.CategoryManager;
import hero.manufacture.Manufacture;
import hero.manufacture.dict.ManufSkill;
import hero.manufacture.dict.ManufSkillDict;
import hero.manufacture.service.ManufactureServerImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import yoyo.core.packet.AbsResponseMessage;


public class ManufListMessage extends AbsResponseMessage
{
    private String      title; //大类技能等级名称(初学、专家、精通)

    private List<Manufacture> manufactureList;

    public ManufListMessage(String _title, List<Manufacture> _manufactureList)
    {
        title = _title;
        manufactureList = _manufactureList;
    }

    @Override
    public int getPriority ()
    {
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        yos.writeByte(manufactureList.size());
        for(Manufacture manufacture : manufactureList){
            yos.writeByte(manufacture.getManufactureType().getID());//制造技能大类的类型ID，可以用来区分是哪种制造技能
            yos.writeUTF(manufacture.getManufactureType().getName());
            yos.writeUTF(title);
            yos.writeInt(manufacture.getPoint());
//            output.writeInt(ManufactureServerImpl.POINT_LIMIT[manufacture
//                            .getLvl() - 1]);
//            output.writeByte(manufacture.getLvl());

            /*String[] _str = CategoryManager
                    .getCategoryStrByManufactureType(manufacture
                            .getManufactureType());
            output.writeByte(_str.length);
            for (String _s : _str)
            {
                output.writeUTF(_s);
            }*/

            ArrayList<Integer> manufIDList = manufacture.getManufIDList();

            yos.writeShort(manufIDList.size());

            for (int _manufID : manufIDList)
            {
                ManufSkill manuf = ManufSkillDict.getInstance().getManufSkillByID(
                        _manufID);
                yos.writeInt(manuf.id);
                int goodsID = manuf.getGoodsID[1];
                Goods goods = GoodsContents.getGoods(goodsID);
                yos.writeShort(goods.getIconID());
                yos.writeUTF(goods.getName());
                yos.writeByte(goods.getTrait().value());
                yos.writeByte(manuf.category);
            }
        }
    }
}
