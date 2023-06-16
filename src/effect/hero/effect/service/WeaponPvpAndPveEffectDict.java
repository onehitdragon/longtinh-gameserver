package hero.effect.service;

import hero.effect.dictionry.EffectDictionary;
import hero.item.Weapon;
import hero.item.detail.EGoodsTrait;
import hero.item.dictionary.WeaponDict;
import hero.item.enhance.EnhanceService;
import hero.share.service.LogWriter;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 WeaponPvpAndPveEffectDict.java
 * @创建者 Lulin
 * @版本 1.0
 * @时间 2009-2-16 下午04:17:45
 * @描述 ：武器的屠魔和杀戮效果字典
 */

public class WeaponPvpAndPveEffectDict
{
    /**
     * 单例
     */
    private static WeaponPvpAndPveEffectDict instance;

    /**
     * 效果编号列表字典（编号：效果编号列表）
     */
    private HashMap<Integer, int[]>          effectIDListTable;

    /**
     * 私有构造
     */
    private WeaponPvpAndPveEffectDict()
    {
        effectIDListTable = new HashMap<Integer, int[]>();
    }

    /**
     * 加载数据
     * 
     * @param _dataPath 数据所在目录
     */
    public void load (String _dataPath)
    {
        File dataPath;

        try
        {
            dataPath = new File(_dataPath);
            effectIDListTable.clear();
        }
        catch (Exception e)
        {
            LogWriter.println("未找到指定的目录：" + _dataPath);

            return;
        }

        try
        {
            File[] dataFileList = dataPath.listFiles();

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

                int id;
                int[] effectIDList;

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    if (null != subE)
                    {
                        id = Integer.parseInt(subE.elementTextTrim("id"));

                        effectIDList = new int[EnhanceService.MAX_LEVEL_OF_PVE_AND_PVP];

                        for (int i = 1; i <= effectIDList.length; i++)
                        {
                            effectIDList[i - 1] = Integer.parseInt(subE
                                    .elementTextTrim("level" + i + "ID"));

                            if (null == EffectDictionary.getInstance()
                                    .getEffectRef(effectIDList[i - 1]))
                            {
                                LogWriter.println("杀戮、屠魔效果不存在："
                                        + effectIDList[i - 1]);

                                return;
                            }
                        }

                        effectIDListTable.put(id, effectIDList);
                    }
                }
            }

            Object[] weaponList = WeaponDict.getInstance().getWeaponList();

            if (weaponList.length > 0)
            {
                for (Object weapon : weaponList)
                {
                    if (((Weapon) weapon).getTrait() == EGoodsTrait.YU_ZHI
                            || ((Weapon) weapon).getTrait() == EGoodsTrait.SHENG_QI)
                    {
                        if (!effectIDListTable.containsKey(((Weapon) weapon)
                                .getPveEnhanceID()))
                        {
                            LogWriter.println("武器关联的屠魔效果不存在，武器："
                                    + ((Weapon) weapon).getName() + "--"
                                    + ((Weapon) weapon).getTrait().getDesc()
                                    + "--"
                                    + ((Weapon) weapon).getPveEnhanceID());
                        }
                        else if (!effectIDListTable
                                .containsKey(((Weapon) weapon)
                                        .getPvpEnhanceID()))
                        {
                            LogWriter.println("武器关联的杀戮效果不存在，武器："
                                    + ((Weapon) weapon).getName() + "--"
                                    + ((Weapon) weapon).getTrait().getDesc()
                                    + "--"
                                    + ((Weapon) weapon).getPvpEnhanceID());
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static WeaponPvpAndPveEffectDict getInstance ()
    {
        if (instance == null)
        {
            instance = new WeaponPvpAndPveEffectDict();
        }

        return instance;
    }

    /**
     * 获取屠魔或者杀戮的效果ID
     * 
     * @param _weaponEnhanceID 效果编号
     * @param _enhanceLevel 屠魔或者杀戮等级
     * @return
     */
    public int getEffectID (int _weaponEnhanceID, int _enhanceLevel)
    {
        if (_enhanceLevel > 0
                && _enhanceLevel <= EnhanceService.MAX_LEVEL_OF_PVE_AND_PVP)
        {
            int[] _effectIDList = effectIDListTable.get(_weaponEnhanceID);

            if (_effectIDList != null)
            {
                return _effectIDList[_enhanceLevel - 1];
            }
        }

        return -1;
    }
}
