package hero.item.legacy;

import hero.item.Equipment;
import hero.item.Goods;
import hero.item.Material;
import hero.item.Medicament;
import hero.item.detail.EGoodsTrait;
import hero.item.detail.EGoodsType;
import hero.item.dictionary.GoodsContents;
import hero.item.service.GoodsConfig;
import hero.share.service.LogWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javolution.util.FastList;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 WorldLegacyDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-12-2 上午09:31:23
 * @描述 ：世界怪物掉落物品
 */

public class WorldLegacyDict
{
    private static Logger log = Logger.getLogger(WorldLegacyDict.class);
    /**
     * 世界掉落药水列表
     */
    private FastList<WorldLegacy>  medicamentList;
    /**
     * 药水物品列表
     */
    private FastList<Medicament> medicamentGoodsList;
    /**
     * 世界掉落装备列表
     */
    private FastList<WorldLegacy> equipmentList;
    /**
     * 装备物品列表
     */
    private FastList<Equipment> equipmentGoodsList;
    /**
     * 世界掉落材料列表
     */
    private FastList<WorldLegacy> materialList;
    /**
     * 材料列表
     */
    private FastList<Material> materialGoodsList;

    /**
     * 单例
     */
    private static WorldLegacyDict instance;


    /**
     * 私有构造
     */
    private WorldLegacyDict()
    {
        medicamentList = new FastList<WorldLegacy>();
        equipmentList = new FastList<WorldLegacy>();
        materialList = new FastList<WorldLegacy>();
    }

    /**
     * 获取掉落物品编号
     * 
     * @param _monsterLevel
     * @return
     */
    public ArrayList<Integer> getLegacyGoodID (short _monsterLevel)
    {
        ArrayList<Integer> goodsIDList = new ArrayList<Integer>();
        for (WorldLegacy legacy : medicamentList)
        {
            if (legacy.matchLimit(_monsterLevel) && legacy.legacyOdds())
            {
                int number = legacy.number;
                Random random = new Random();
                int size = legacy.goodsIDList.size();
                if(size > 0){
                    int index;
                    for (int i=0; i<number; i++){
                        index = random.nextInt(size);
                        goodsIDList.add(legacy.goodsIDList.get(index));
                    }
                }
            }
        }

        for (WorldLegacy legacy : materialList)
        {
            if (legacy.matchLimit(_monsterLevel) && legacy.legacyOdds())
            {
                int number = legacy.number;
                Random random = new Random();
                int size = legacy.goodsIDList.size();
                if(size > 0){
                    int index;
                    for (int i=0; i<number; i++){
                        index = random.nextInt(size);
                        goodsIDList.add(legacy.goodsIDList.get(index));
                    }
                }
            }
        }

        for (WorldLegacy legacy : equipmentList)
        {
            if (legacy.matchLimit(_monsterLevel) && legacy.legacyOdds())
            {
                int number = legacy.number;
                Random random = new Random();
                int size = legacy.goodsIDList.size();
                if(size > 0){
                    int index;
                    for (int i=0; i<number; i++){
                        index = random.nextInt(size);
                        goodsIDList.add(legacy.goodsIDList.get(index));
                    }
                }
            }
        }

        return goodsIDList;
    }

    /**
     * 加载数据
     * 
     * @param _dataPath
     */
    public void load (GoodsConfig config)
    {
        File dataPath;

        try
        {
            dataPath = new File(config.world_legacy_material_data_path);
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

                WorldLegacy legacy;
                String data;

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    if (null != subE)
                    {
                        try
                        {
                            legacy = new WorldLegacy(Short.parseShort(subE.elementTextTrim("startLevel")),
                                    Short.parseShort(subE.elementTextTrim("endLevel")),
                                    Integer.parseInt(subE.elementTextTrim("number")),
                                    Integer.parseInt(subE.elementTextTrim("itemStartID")),
                                    Integer.parseInt(subE.elementTextTrim("itemEndID")));

                            data = subE.elementTextTrim("startItemLevel");
                            if(data != null){
                                legacy.startItemLevel = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("endItemLevel");
                            if(data != null){
                                legacy.endItemLevel = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("trait");
                            if(data != null){
                                legacy.trait = EGoodsTrait.getTrait(data);
                            }
                            data = subE.elementTextTrim("odds");
                            legacy.odds = Float.parseFloat(data);
                            materialList.add(legacy);
                        }
                        catch (Exception e)
                        {
                            log.error("加载世界掉落材料物品数据出错:",e);
                        }
                    }
                }
            }

            dataPath = new File(config.world_legacy_equip_data_path);
            dataFileList = dataPath.listFiles();

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

                WorldLegacy legacy;
                String data;

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    if (null != subE)
                    {
                        try
                        {
                            legacy = new WorldLegacy(Short.parseShort(subE.elementTextTrim("startLevel")),
                                    Short.parseShort(subE.elementTextTrim("endLevel")),
                                    Integer.parseInt(subE.elementTextTrim("number")),
                                    Integer.parseInt(subE.elementTextTrim("itemStartID")),
                                    Integer.parseInt(subE.elementTextTrim("itemEndID")));

                            data = subE.elementTextTrim("startItemLevel");
                            if(data != null){
                                legacy.startItemLevel = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("endItemLevel");
                            if(data != null){
                                legacy.endItemLevel = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("trait");
                            if(data != null){
                                legacy.trait = EGoodsTrait.getTrait(data);
                            }
                            data = subE.elementTextTrim("odds");
                            legacy.odds = Float.parseFloat(data);
                            equipmentList.add(legacy);
                        }
                        catch (Exception e)
                        {
                           log.error("加载世界掉落装备物品数据出错",e);
                        }
                    }
                }
            }

            dataPath = new File(config.world_legacy_medicament_data_path);
            dataFileList = dataPath.listFiles();

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

                WorldLegacy legacy;
                String data;

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    if (null != subE)
                    {
                        try
                        {
                            legacy = new WorldLegacy(Short.parseShort(subE.elementTextTrim("startLevel")),
                                    Short.parseShort(subE.elementTextTrim("endLevel")),
                                    Integer.parseInt(subE.elementTextTrim("number")),
                                    Integer.parseInt(subE.elementTextTrim("itemStartID")),
                                    Integer.parseInt(subE.elementTextTrim("itemEndID")));

                            data = subE.elementTextTrim("startItemLevel");
                            if(data != null){
                                legacy.startItemLevel = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("endItemLevel");
                            if(data != null){
                                legacy.endItemLevel = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("trait");
                            if(data != null){
                                legacy.trait = EGoodsTrait.getTrait(data);
                            }
                            data = subE.elementTextTrim("odds");
                            legacy.odds = Float.parseFloat(data);
                            medicamentList.add(legacy);
                        }
                        catch (Exception e)
                        {
                           log.error("加载世界掉落药水物品数据出错",e);
                        }
                    }
                }
            }

            for(WorldLegacy w : medicamentList){
                w.loadAllLegacyGoodsID();
            }
            for (WorldLegacy w: materialList){
                w.loadAllLegacyGoodsID();
            }
            for (WorldLegacy w: equipmentList){
                w.loadAllLegacyGoodsID();
            }
        }
        catch (Exception e)
        {
            log.error("加载世界掉落物品出错：", e);
        }
    }                                                                                                       {

}


    /**
     * 获取单例
     * 
     * @return
     */
    public static WorldLegacyDict getInstance ()
    {
        if (null == instance)
        {
            instance = new WorldLegacyDict();
        }

        return instance;
    }

    /**
     * @author DC 掉落信息，当怪物的等级落在起始、终止区间时有0.1%几率掉落其中任意一种物品一个
     */
    private class WorldLegacy
    {
        /**
         * 起始怪物等级
         */
        public short              startMonsterLevel;

        /**
         * 终止怪物等级
         */
        public short              endMonsterLevel;

        /**
         * 掉落数量
         */
        public int number;

        /**
         *掉落物品起始编号
         */
        public int startItemID;

        /**
         * 掉落物品结束编号
         */
        public int endItemID;
        /**
         * 掉落物品起始等级
         */
        public int startItemLevel;
        /**
         * 掉落物品结束等级
         */
        public int endItemLevel;

        /**
         * 掉落物品的品质
         */
        public EGoodsTrait trait;
        /**
         * 掉落几率
         */
        public float odds;

        /**
         * 物品编号列表
         */
        public ArrayList<Integer> goodsIDList;

        /**
         * 构造
         */
        public WorldLegacy(short _startMonsterLevel, short _endMonsterLevel,int _number,int _startItemID,int _endItemID)
        {
            startMonsterLevel = _startMonsterLevel;
            endMonsterLevel = _endMonsterLevel;
            number = _number;
            startItemID = _startItemID;
            endItemID = _endItemID;
            goodsIDList = new ArrayList<Integer>();
        }

        /**
         * 是否匹配等级区间
         * 
         * @param _monsterLevel
         * @return
         */
        public boolean matchLimit (short _monsterLevel)
        {
            return _monsterLevel >= startMonsterLevel
                    && _monsterLevel <= endMonsterLevel;
        }

        /**
         * 根据几率是否掉落
         * @return
         */
        public boolean legacyOdds(){
            if(odds>0  && RANDOM_BUILDER.nextInt(ODDS_ENLARGE_MODULUE) <= odds*ODDS_ENLARGE_MODULUE ){
                return true;
            }
            return false;
        }

        /**
         * 获取所有可能掉落物品的编号
         * 
         * @return
         */
        public void loadAllLegacyGoodsID ()
        {
            FastList<Goods> goodslist = new FastList<Goods>();
            for (int i=startItemID; i<=endItemID; i++){
                Goods goods = GoodsContents.getGoods(i);
                if(goods != null && trait==goods.getTrait()
                        && goods.getNeedLevel()>=startItemLevel && goods.getNeedLevel()<=endItemLevel){
                    log.debug("monster level["+startMonsterLevel+" -- "+endMonsterLevel+"],world legacy goodsid = "+goods.getID()+",trait="+trait);
                    if(goods.getGoodsType() == EGoodsType.EQUIPMENT){
                        Equipment eq = (Equipment)goods;
                        if(eq.getBindType() == Equipment.BIND_TYPE_OF_WEAR || eq.getBindType()== Equipment.BIND_TYPE_OF_NOT){
                            goodslist.add(goods);
                        }
                    }else {
                        goodslist.add(goods);
                    }
                }
            }
            if(goodslist.size()>0){
                for (Goods goods : goodslist){
                    goodsIDList.add(goods.getID());
                }
            }
        }

        /**
         * 添加掉落物品
         * 
         * @param _goodsID
         */
        /*public void add (int _goodsID)
        {
            if (!goodsIDList.contains(_goodsID))
            {
                goodsIDList.add(_goodsID);
            }
        }*/
    }

    /**
     * 随机数生成器
     */
    private static final Random RANDOM_BUILDER          = new Random();

    /**
     * 一个等级区间掉落的最多种类
     */
//    private static final short  LEGACY_GOODS_MAX_NUMBER = 12;

    /**
     * 放大1000000倍
     */
    private static final int    ODDS_ENLARGE_MODULUE    = 1000000;

    /**
     * 掉落几率（被放大10000倍）
     */
//    private static final int    LEGACY_ODDS             = (int) (0.0004 * ODDS_ENLARGE_MODULUE);
}
