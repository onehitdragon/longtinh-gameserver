package hero.manufacture.dict;

import hero.item.Goods;
import hero.item.dictionary.GoodsContents;
import hero.manufacture.*;
import hero.share.service.LogWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 制造技能数据模板字典 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class ManufSkillDict
{
    private static Logger log = Logger.getLogger(ManufSkillDict.class);
    private static ManufSkillDict        instance;

    private HashMap<Integer, ManufSkill> list;

    private ManufSkillDict()
    {
        list = new HashMap<Integer, ManufSkill>();
    }

    public void loadManufSkills (String[] paths)
    {
        for (byte i = 0; i < paths.length; i++)
            loadData(paths[i], (byte) (i + 1));
    }

    @SuppressWarnings("unchecked")
    private void loadData (String path, byte _type)
    {
        File dataPath;

        try
        {
            dataPath = new File(path);
        }
        catch (Exception e)
        {
            LogWriter.println("未找到指定的目录：" + path);
            return;
        }

        try
        {
            File[] dataFileList = dataPath.listFiles();

            String needLvlDesc;

            for (File dataFile : dataFileList)
            {
                if (!dataFile.getName().endsWith(".xml"))
                {
                    continue;
                }

                SAXReader reader = new SAXReader();
                Document document = reader.read(dataFile);
                Element rootE = document.getRootElement();
                Iterator<Element> rootIt = rootE.elementIterator();

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();
                    if (null != subE)
                    {
                        ManufSkill skill = new ManufSkill();
                        skill.id = Integer.parseInt(subE.elementTextTrim("id"));
                        skill.name = subE.elementTextTrim("name");
                        skill.type = _type;
                        skill.icon = Short.parseShort(subE
                                .elementTextTrim("icon"));
                        skill.npcStudy = subE.elementTextTrim("npcStudy")
                                .equals("是");
                        ManufactureType _mtype = ManufactureType.get(_type);
                        String category = subE.elementTextTrim("category");
                        log.debug("manufSkill id = " + skill.id +" -- category =" + category);
                        if (_mtype == ManufactureType.BLACKSMITH)
                            skill.category = BlacksmithCategory.getCategory(category).getId();
                        else if (_mtype == ManufactureType.CRAFTSMAN)
                            skill.category = CraftsmanCategory.getCategory(category).getId();
                        else if (_mtype == ManufactureType.DISPENSER)
                            skill.category = DispenserCategory.getCategory(category).getId();
                        else if(_mtype == ManufactureType.GRATHER)
                            skill.category = GatherCategory.getGatherCategory(category).getId();
                        else if(_mtype == ManufactureType.PURIFY)
                            skill.category = PurifyCategory.getPurifyCategory(category).getId();
                        else skill.category = JewelerCategory.getCategory(category).getId();
                        skill.needSkillPoint = Short.parseShort(subE.elementTextTrim("needSkillPoint"));
                        needLvlDesc = subE.elementTextTrim("lvl");
//                        skill.needLvl = getNeedLvl(needLvlDesc);
//                        skill.point = Integer.parseInt(subE
//                                .elementTextTrim("point"));
                        if(null != needLvlDesc){
                            skill.needLevel = Byte.parseByte(needLvlDesc);
                        }
                        String money = subE.elementTextTrim("money");
                        if (money != null && money.trim().length()>0)
                            skill.money = Integer.parseInt(money);

                        for(int i=0; i<skill.stagesNeedPoint.length; i++){
                            String data = subE.elementTextTrim("stage"+(i+1)+"NeedPoint");
                            if(null != data && data.trim().length()>0){
                                skill.stagesNeedPoint[i] = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("stage"+(i+1)+"GetPointOdd");
                            if(null != data && data.trim().length()>0){
                                skill.stagesGetPointOdd[i] = Byte.parseByte(data);
                            }
                        }

                        String abruptID = subE.elementTextTrim("abruptID");
                        if (abruptID != null && abruptID.trim().length()>0)
                            skill.abruptID = Integer.parseInt(abruptID);
                        for (int i = 0; i < skill.needGoodsID.length; i++)
                        {
                            String idStr = subE.elementTextTrim("goods"
                                    + (i + 1) + "ID");
                            if (idStr != null && idStr.trim().length()>0)
                            {
                                skill.needGoodsID[i] = Integer.parseInt(idStr);
                            }
                            String numStr = subE.elementTextTrim("goods"
                                    + (i + 1) + "Num");
                            if (numStr != null && numStr.trim().length()>0)
                            {
                                skill.needGoodsNum[i] = Short
                                        .parseShort(numStr);
                            }
                        }
                        List<Odd> getGoodsOddList = new ArrayList<Odd>();
                        for (int i = 0; i < skill.getGoodsID.length; i++)
                        {
                            String idStr = subE.elementTextTrim("getGoods"
                                    + (i + 1) + "ID");
                            if (idStr != null && idStr.trim().length()>0)
                            {
                                skill.getGoodsID[i] = Integer.parseInt(idStr);
                            }
                            String numStr = subE.elementTextTrim("getGoods"
                                    + (i + 1) + "Num");
                            if (numStr != null && numStr.trim().length()>0)
                            {
                                skill.getGoodsNum[i] = Short.parseShort(numStr);
                            }
                            String oddStr = subE.elementTextTrim("getGoods"+(i+1)+"Odd");
                            if(null != oddStr && oddStr.trim().length()>0){
                                skill.getGoodsOdd[i] = Byte.parseByte(oddStr);
                                getGoodsOddList.add(new Odd(skill.getGoodsOdd[i],(byte)i));
                            }
                        }
                        skill.setGetGoodsOddList(getGoodsOddList);
//                        Goods goods = GoodsContents
//                                .getGoods(skill.getGoodsID[1]);
//                        if(goods != null)    //没按几率
//                            skill.desc = DESC_1 + DESC_2 + skill.getPoint + DESC_3 + skill.getGoodsNum[1]
//                                    + DESC_4 + goods.getDescription();

                        list.put(skill.id, skill);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private byte getNeedLvl (String str)
    {
        if (str.equals("初学")) return 1;
        if (str.equals("熟练")) return 2;
        if (str.equals("精通")) return 3;
        if (str.equals("大师")) return 4;
        return 5;
    }

    public static ManufSkillDict getInstance ()
    {
        if (instance == null) instance = new ManufSkillDict();
        return instance;
    }

    public ManufSkill getManufSkillByID (int _manufID)
    {
        return list.get(_manufID);
    }

    /**
     * 得到所有的采集 技能对象
     * 
     * @return
     */
    public Iterator<ManufSkill> getManufSkills ()
    {
        return list.values().iterator();
    }

    private static final String DESC_1 = "可以提升";

    private static final String DESC_2 = "熟练度";

    private static final String DESC_3 = "点\n每次可制造数量：";

    private static final String DESC_4 = "\n";
}
