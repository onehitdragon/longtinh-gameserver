package hero.gather.dict;

import hero.gather.RefinedCategory;
import hero.share.service.LogWriter;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 炼化数据字典类 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class RefinedDict
{
    private static RefinedDict        instance;

    private HashMap<Integer, Refined> list;

    private RefinedDict()
    {
        list = new HashMap<Integer, Refined>();
    }

    @SuppressWarnings("unchecked")
    public void loadRefineds (String path)
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
                        Refined skill = new Refined();
                        skill.id = Integer.parseInt(subE.elementTextTrim("id"));
                        skill.name = subE.elementTextTrim("name");
                        skill.icon = Short.parseShort(subE
                                .elementTextTrim("icon"));
                        skill.npcStudy = subE.elementTextTrim("npcStudy")
                                .equals("是");
                        skill.category = RefinedCategory.getCategory(
                                subE.elementTextTrim("category")).getId();
                        needLvlDesc = subE.elementTextTrim("needLvl");
                        skill.needLvl = getNeedLvl(needLvlDesc);
                        skill.point = Integer.parseInt(subE
                                .elementTextTrim("point"));
                        skill.money = Integer.parseInt(subE
                                .elementTextTrim("money"));
                        String abruptID = subE.elementTextTrim("abruptID");
                        if (abruptID != null && !abruptID.equals(""))
                            skill.abruptID = Integer.parseInt(abruptID);
                        for (int i = 0; i < skill.needSoulID.length; i++)
                        {
                            String idStr = subE.elementTextTrim("goods"
                                    + (i + 1) + "ID");
                            if (idStr != null && !idStr.equals(""))
                            {
                                skill.needSoulID[i] = Integer.parseInt(idStr);
                            }
                            String numStr = subE.elementTextTrim("goods"
                                    + (i + 1) + "Num");
                            if (numStr != null && !numStr.equals(""))
                            {
                                skill.needSoulNum[i] = Short.parseShort(numStr);
                            }
                        }
                        for (int i = 0; i < skill.getGoodsID.length; i++)
                        {
                            String idStr = subE.elementTextTrim("getGoods"
                                    + (i + 1) + "ID");
                            if (idStr != null && !idStr.equals(""))
                            {
                                skill.getGoodsID[i] = Integer.parseInt(idStr);
                            }
                            String numStr = subE.elementTextTrim("getGoods"
                                    + (i + 1) + "Num");
                            if (numStr != null && !numStr.equals(""))
                            {
                                skill.getGoodsNum[i] = Short.parseShort(numStr);
                            }
                        }
                        String needGourd = subE.elementTextTrim("needGourd");
                        if (needGourd != null && !needGourd.equals(""))
                        {
                            skill.needGourd = Integer.parseInt(needGourd);
                        }

                        skill.desc = DESC_1 + needLvlDesc + DESC_2
                                + skill.point + DESC_3 + skill.getGoodsNum[1];

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

    public static RefinedDict getInstance ()
    {
        if (instance == null) instance = new RefinedDict();
        return instance;
    }

    public Refined getRefinedByID (int _refinedID)
    {
        return list.get(_refinedID);
    }

    /**
     * 得到所有的采集 技能对象
     * 
     * @return
     */
    public Iterator<Refined> getRefineds ()
    {
        return list.values().iterator();
    }

    private static final String DESC_1 = "可以提升";

    private static final String DESC_2 = "技能";

    private static final String DESC_3 = "点\n每次可制造数量：";
}
