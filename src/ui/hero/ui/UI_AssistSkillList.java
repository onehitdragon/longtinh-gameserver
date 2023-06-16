package hero.ui;

import hero.gather.RefinedCategory;
import hero.gather.dict.Refined;
import hero.item.Goods;
import hero.item.dictionary.GoodsContents;
import hero.manufacture.CategoryManager;
import hero.manufacture.Manufacture;
import hero.manufacture.dict.ManufSkill;

import java.io.IOException;
import java.util.ArrayList;

import yoyo.tools.YOYOOutputStream;


public class UI_AssistSkillList
{
    public static byte[] getRefinedBytes (String[] _menuList,
            ArrayList<Refined> _list)
    {
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());

            String[] _str = RefinedCategory.getCategorys();
            output.writeByte(_str.length);
            for (String _s : _str)
            {
                output.writeUTF(_s);
            }

            output.writeShort(_list.size());

            for (Refined _refined : _list)
            {
                output.writeInt(_refined.id);
                output.writeShort(_refined.icon);
                output.writeUTF(_refined.name);
                int goodsID = _refined.getGoodsID[1];
                Goods goods = GoodsContents.getGoods(goodsID);
                output.writeUTF(goods.getName());
                output.writeShort(goods.getIconID());
                output.writeByte(goods.getTrait().value());
                output.writeByte(_refined.category);
                output.writeInt(_refined.money);
            }

            output.writeByte(_menuList.length);

            for (String menu : _menuList)
            {
                output.writeUTF(menu);
                output.writeByte(0);
            }

            output.flush();

            return output.getBytes();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            try
            {
                output.close();
            }
            catch (IOException e)
            {
            }
            output = null;
        }
    }

    public static byte[] getManufSkillBytes (String[] _menuList,
            ArrayList<ManufSkill> _list, Manufacture _manuf)
    {
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());

            String[] _str = CategoryManager
                    .getCategoryStrByManufactureType(_manuf
                            .getManufactureType());
            output.writeByte(_str.length);
            for (String _s : _str)
            {
                output.writeUTF(_s);
            }

            output.writeShort(_list.size());

            for (ManufSkill _mSkill : _list)
            {
                output.writeInt(_mSkill.id);
                output.writeShort(_mSkill.icon);
                output.writeUTF(_mSkill.name);
                int goodsID = _mSkill.getGoodsID[1];
                Goods goods = GoodsContents.getGoods(goodsID);
                output.writeUTF(goods.getName());
                output.writeShort(goods.getIconID());
                output.writeByte(goods.getTrait().value());
                output.writeByte(_mSkill.category);
                output.writeInt(_mSkill.money);
            }

            output.writeByte(_menuList.length);

            for (String menu : _menuList)
            {
                output.writeUTF(menu);
                output.writeByte(0);
            }

            output.flush();

            return output.getBytes();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            try
            {
                output.close();
            }
            catch (IOException e)
            {
            }
            output = null;
        }
    }

    /**
     * 获取UI类型
     * 
     * @return
     */
    public static EUIType getType ()
    {
        return EUIType.ASSIST_SKILL_LIST;
    }
}
