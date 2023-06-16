package hero.item.dictionary;

import hero.item.Goods;
import hero.item.SpecialGiftBagData;
import hero.item.SpecialGoods;
import hero.item.special.*;
import hero.pet.FeedType;
import hero.share.service.LogWriter;

import java.io.File;
import java.util.Iterator;

import javolution.util.FastMap;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SpecialGoodsDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-8 下午05:01:48
 * @描述 ：特殊物品字典
 */

public class SpecialGoodsDict
{
    private static Logger log = Logger.getLogger(SpecialGoodsDict.class);
    /**
     * 单例
     */
    private static SpecialGoodsDict        instance;

    /**
     * 模板容器
     */
    private FastMap<Integer, SpecialGoods> dictionary;
    
    /**
     * 礼包功能容器
     */
    private FastMap<Integer, SpecialGiftBagData> giftBagDictionary;

    /**
     * 私有构造
     */
    private SpecialGoodsDict()
    {
        dictionary = new FastMap<Integer, SpecialGoods>();
        giftBagDictionary = new FastMap<Integer, SpecialGiftBagData>();
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static SpecialGoodsDict getInstance ()
    {
        if (instance == null)
        {
            instance = new SpecialGoodsDict();
        }

        return instance;
    }

    public Object[] getSpecialGoodsList ()
    {
        return dictionary.values().toArray();
    }

    /**
     * 根据编号获取特殊物品
     * 
     * @param _goodsID
     * @return
     */
    public SpecialGoods getSpecailGoods (int _goodsID)
    {
        return dictionary.get(_goodsID);
    }
    
    /**
     * 获得制定编号的礼包
     * @param _giftBagID
     * @return
     */
    public SpecialGiftBagData getBagData (int _giftBagID)
    {
    	return giftBagDictionary.get(_giftBagID);
    }
    
    public void loadGiftBagData(String _dataPath)
    {
        File dataPath;

        try
        {
            dataPath = new File(_dataPath);
        }
        catch (Exception e)
        {
            LogWriter.println("未找到指定的目录：" + _dataPath);

            return;
        }
        File[] dataFileList = dataPath.listFiles();
        String data;

        try 
        {
            for (File dataFile : dataFileList)
            {
                if (!dataFile.getName().endsWith(".xml"))
                {
                    continue;
                }
                log.debug(" special Goods xml name = " + dataFile.getName());
                SAXReader reader = new SAXReader();
                Document document = reader.read(dataFile);
                Element rootE = document.getRootElement();
                Iterator<Element> rootIt = rootE.elementIterator();
                SpecialGiftBagData giftBag = null;
                while (rootIt.hasNext())
                {
                	Element subE = rootIt.next();
                	
                    if (null != subE)
                    {
                    	giftBag = new SpecialGiftBagData();
                    	giftBag.id = Integer.valueOf(subE.elementTextTrim("id"));
                    	data = subE.elementTextTrim("goodsSum");
                    	if( data != null && (!data.equals("")) )
                    	{
                    		giftBag.goodsSum = Integer.valueOf(data);
                    	}
                    	else 
                    	{
                    		//得不到数量,不读这个物品
							continue;
						}
                    	giftBag.goodsList = new int[giftBag.goodsSum];
                    	giftBag.numberList = new int[giftBag.goodsSum];
                    	for (int i = 0; i < giftBag.goodsSum; i++) {
                    		giftBag.goodsList[i] = Integer.valueOf(
                    				subE.elementTextTrim("goods"+(i +1)+"ID"));
                    		giftBag.numberList[i] = Integer.valueOf(
                    				subE.elementTextTrim("goods"+(i +1)+"Num"));
						}
                    }
                    if(!giftBagDictionary.containsKey(giftBag.id))
                    {
                    	giftBagDictionary.put(giftBag.id, giftBag);
                    }
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
		}

    }

    /**
     * 加载特殊物品模板
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
        }
        catch (Exception e)
        {
            LogWriter.println("未找到指定的目录：" + _dataPath);

            return;
        }

        try
        {
            File[] dataFileList = dataPath.listFiles();
            String data;

            for (File dataFile : dataFileList)
            {
                if (!dataFile.getName().endsWith(".xml"))
                {
                    continue;
                }
                log.debug(" special Goods xml name = " + dataFile.getName());
                SAXReader reader = new SAXReader();
                Document document = reader.read(dataFile);
                Element rootE = document.getRootElement();
                Iterator<Element> rootIt = rootE.elementIterator();

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    if (null != subE)
                    {
                    	int id = Integer.parseInt(subE.elementTextTrim("id"));
                        SpecialGoods goods = SpecialGoodsBuilder.build(id,
                                Short.parseShort(subE
                                        .elementTextTrim("stackNumber")), subE
                                        .elementTextTrim("type"));

                        if (null != goods)
                        {
//                        	Log4j.instance(getClass()).debug("special goods id = " + id+ " type = " + goods.getType());
                            goods.setName(subE.elementTextTrim("name"));
                            goods.setTrait(subE.elementTextTrim("trait"));
                            goods.setNeedLevel(Short.parseShort(subE
                                    .elementTextTrim("levelLimit")));



                            if (subE.elementTextTrim("useable").equals("是"))
                            {
                                goods.setUseable();
                            }

                            if (subE.elementTextTrim("isOnlyInBag").equals("是"))
                            {
                                goods.setOnly();
                            }

                            if (subE.elementTextTrim("exchangeable")
                                    .equals("是"))
                            {
                                goods.setExchangeable();
                            }

                            if (subE.elementTextTrim("sellable").equals("是"))
                            {
                                goods.setCanBeSell();
                                goods.setPrice(Integer.parseInt(subE
                                        .elementTextTrim("price")));
                            }

                            data = subE.elementTextTrim("price");

                            if (null != data)
                            {
                                goods.setPrice(Integer.parseInt(data));
                            }

                            goods.appendDescription(subE
                                    .elementTextTrim("description"));
                            goods.setIconID(Short.parseShort(subE
                                    .elementTextTrim("icon")));

                            if (ESpecialGoodsType.DRAWINGS == goods.getType())
                            {
                                ((Drawings) goods).setNeedManufactureType(subE
                                        .elementTextTrim("needSkill"));
                                ((Drawings) goods)
                                        .setManufactureItemID(Integer
                                                .parseInt(subE
                                                        .elementTextTrim("getSkillItemID")));
                                int goodsID = Integer.parseInt(subE
                                        .elementTextTrim("getItemID"));

                                Goods getGoods = GoodsContents
                                        .getGoods(goodsID);

                                if (null != getGoods)
                                {
                                    goods.appendDescription(getGoods
                                            .getDescription());
                                }
                            }
                            else if (ESpecialGoodsType.PET_ARCHETYPE == goods
                                    .getType())
                            {
                            	int petAID = Integer.parseInt(subE.elementTextTrim("id"));
                                ((PetArchetype) goods).setPetAID(petAID);
                            }
                            else if (ESpecialGoodsType.SKILL_BOOK == goods
                                    .getType())
                            {
                                ((SkillBook) goods)
                                        .setSkillID(Integer
                                                .parseInt(subE
                                                        .elementTextTrim("getSkillItemID")));
                            }
                            else if (ESpecialGoodsType.PET_FEED == goods.getType()){
                                data = subE.elementTextTrim("feedtype");
                                ((PetFeed)goods).setFeedType(FeedType.getFeedType(data));
                            }else if (ESpecialGoodsType.PET_REVIVE == goods.getType()){
                            }else if (ESpecialGoodsType.PET_DICARD == goods.getType()){
                            }else if(ESpecialGoodsType.PET_SKILL_BOOK == goods.getType()){
                            	((PetSkillBook)goods).setSkillID(Integer.parseInt(subE.elementTextTrim("getSkillItemID")));
                            }else if(ESpecialGoodsType.HEAVEN_BOOK == goods.getType()){
                                data = subE.elementTextTrim("getHeavenBookPoint");
                                ((HeavenBook)goods).setSkillPoint(Short.parseShort(data));
                            }else if(ESpecialGoodsType.REPEATE_TASK_EXPAN == goods.getType()){
                                data = subE.elementTextTrim("use_times");
                                ((RepeateTaskExpan)goods).setUsedTimes(Integer.parseInt(data));
                            }

                            dictionary.put(goods.getID(), goods);
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
