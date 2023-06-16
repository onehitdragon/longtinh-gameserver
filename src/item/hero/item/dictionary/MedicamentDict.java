package hero.item.dictionary;

import hero.item.Medicament;
import hero.item.detail.AdditionEffect;
import hero.item.detail.EGoodsTrait;
import hero.share.service.LogWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import javolution.util.FastMap;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MaterialDict.java
 * @创建者 Luke chen
 * @版本 1.0
 * @时间 2009-2-16 下午04:17:45
 * @描述 ：药水字典
 */

public class MedicamentDict
{
    /**
     * 单例
     */
    private static MedicamentDict        instance;

    /**
     * 字典容器
     */
    private FastMap<Integer, Medicament> dictionary;

    /**
     * 私有构造，单例
     */
    private MedicamentDict()
    {
        dictionary = new FastMap<Integer, Medicament>();
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static MedicamentDict getInstance ()
    {
        if (instance == null)
        {
            instance = new MedicamentDict();
        }

        return instance;
    }

    public Object[] getMedicamentList ()
    {
        return dictionary.values().toArray();
    }

    /**
     * 根据编号获取材料
     * 
     * @param _medicamentID
     * @return
     */
    public Medicament getMedicament (int _medicamentID)
    {
        return dictionary.get(_medicamentID);
    }

    /**
     * 加载材料
     * 
     * @param _dataPath
     */
    @SuppressWarnings("unchecked")
    public void load (String _dataPath)
    {
        File dataPath;

        try
        {
            dataPath = new File(_dataPath);

            File[] dataFileList = dataPath.listFiles();

            ArrayList<AdditionEffect> additionEffectList = new ArrayList<AdditionEffect>();

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
                        String data = null;
                        additionEffectList.clear();

                        Medicament medicament = new Medicament(Short
                                .parseShort(subE.elementTextTrim("stackNums")));

                        try
                        {
                            if ((data = subE.elementTextTrim("id")) != null)
                                medicament.setID(Integer.parseInt(data));

                            if ((data = subE.elementTextTrim("name")) != null)
                                medicament.setName(data);

                            if ((data = subE.elementTextTrim("trait")) != null)
                            {
                                medicament.setTrait(EGoodsTrait.getTrait(data));
                            }

                            if ((data = subE.elementTextTrim("needLevel")) != null)
                                medicament.setNeedLevel(Byte.parseByte(data));

                            if ((data = subE.elementTextTrim("canUseInFight")) != null)
                            {
                                if (data.equals("无限制"))
                                {
                                    medicament.setCanUseInFight(true);
                                }
                                else if (data.equals("非战斗"))
                                {
                                    medicament.setCanUseInFight(false);
                                }
                            }

                            if ((data = subE
                                    .elementTextTrim("isDisappearAfterDead")) != null)
                            {
                                if (data.equals("消失"))
                                {
                                    medicament.setIsDisappearAfterDead(true);
                                }
                                else if (data.equals("不消失"))
                                {
                                    medicament.setIsDisappearAfterDead(false);
                                }
                            }

                            if ((data = subE.elementTextTrim("exchangeable"))
                                    .equals("可交易"))
                            {
                                medicament.setExchangeable();
                            }

                            if ((data = subE.elementTextTrim("price")) != null)
                                medicament.setPrice(Integer.parseInt(data));

                            if ((data = subE.elementTextTrim("delayType")) != null)
                                medicament.setPublicCdVariable(Integer
                                        .parseInt(data));

                            if ((data = subE.elementTextTrim("delayTime")) != null)
                                medicament.setMaxCdTime(Integer.parseInt(data));

                            if ((data = subE.elementTextTrim("hp_resume")) != null)
                                medicament.setResumeHp(Integer.parseInt(data));
                            if ((data = subE.elementTextTrim("mp_resume")) != null)
                                medicament.setResumeMp(Integer.parseInt(data));
                            if ((data = subE.elementTextTrim("qi_resume")) != null)
                                medicament.setResumeGasQuantity(Integer
                                        .parseInt(data));
                            if ((data = subE.elementTextTrim("li_resume")) != null)
                                medicament.setResumeForceQuantity(Integer
                                        .parseInt(data));

                            AdditionEffect additionSkill = null;

                            data = subE.elementTextTrim("add_id1");

                            if (null != data)
                            {
                                additionSkill = new AdditionEffect();

                                additionSkill.effectUnitID = Integer
                                        .parseInt(data);
                                additionSkill.activeTimes = Byte.parseByte(subE
                                        .elementTextTrim("add_count1"));
                                additionSkill.activeOdds = Byte.parseByte(subE
                                        .elementTextTrim("add_percent1")) / 100F;

                                additionEffectList.add(additionSkill);
                            }

                            data = subE.elementTextTrim("add_id2");

                            if (null != data)
                            {
                                additionSkill = new AdditionEffect();

                                additionSkill.effectUnitID = Integer
                                        .parseInt(data);
                                additionSkill.activeTimes = Byte.parseByte(subE
                                        .elementTextTrim("add_count2"));
                                additionSkill.activeOdds = Byte.parseByte(subE
                                        .elementTextTrim("add_percent2")) / 100F;

                                additionEffectList.add(additionSkill);
                            }

                            data = subE.elementTextTrim("add_id3");

                            if (null != data)
                            {
                                additionSkill = new AdditionEffect();

                                additionSkill.effectUnitID = Integer
                                        .parseInt(data);
                                additionSkill.activeTimes = Byte.parseByte(subE
                                        .elementTextTrim("add_count3"));
                                additionSkill.activeOdds = Byte.parseByte(subE
                                        .elementTextTrim("add_percent3")) / 100F;

                                additionEffectList.add(additionSkill);
                            }

                            if (additionEffectList.size() > 0)
                            {
                                medicament.additionEffectList = new AdditionEffect[additionEffectList
                                        .size()];

                                for (int i = 0; i < additionEffectList.size(); i++)
                                    medicament.additionEffectList[i] = additionEffectList
                                            .get(i);
                            }

                            if ((data = subE.elementTextTrim("icon")) != null)
                                medicament.setIconID(Short.parseShort(data));
                            if ((data = subE.elementTextTrim("fireEffect")) != null)
                                medicament.setReleaseAnimation(Integer.parseInt(data));
                            
                            //add by zhengl; date: 2011-02-12; note: 添加特效图片
                            data = subE.elementTextTrim("fireEffectImage");
                            if (data != null) {
                                medicament.setReleaseImage(Integer.parseInt(data));
							}

                            if ((data = subE.elementTextTrim("description")) != null)
                            {
                                if (-1 != data.indexOf("\\n"))
                                {
                                    data = data.replaceAll("\\\\n", "\n");
                                }

                                medicament.appendDescription(data);
                            }

                            dictionary.put(medicament.getID(), medicament);
                        }
                        catch (Exception ex)
                        {
                            LogWriter.println("加载药水数据出错，编号:"
                                    + medicament.getID());
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            LogWriter.error(this, e);
        }
    }
}
