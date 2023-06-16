package hero.charge.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javolution.util.FastMap;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import hero.charge.MallGoods;
import hero.charge.clienthandler.OperateMallGoods;
import hero.item.Goods;
import hero.item.detail.EGoodsTrait;
import hero.item.dictionary.GoodsContents;
import hero.share.service.LogWriter;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MallGoodsDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-1-24 下午07:49:36
 * @描述 ：
 */

public class MallGoodsDict
{
    /**
     * 装备列表
     */
    ArrayList<MallGoods>         equipmentList;

    /**
     * 药水列表
     */
    ArrayList<MallGoods>         medicamentList;

    /**
     * 神器列表
     */
    ArrayList<MallGoods>         materialList;

    /**
     * 技能书列表
     */
    ArrayList<MallGoods>         skillBookList;

    /**
     * 热卖物品列表
     */
    ArrayList<MallGoods>         hotGoodsList;

    /**
     * 宠物列表
     */
    ArrayList<MallGoods>         petList;
    /**
     * 宠物物品列表
     */
    ArrayList<MallGoods>		petGoodsList;
    /**
     * 宠物装备列表
     */
    ArrayList<MallGoods>		petEquipmentList;

    /**
     * 礼包
     */
    ArrayList<MallGoods>         bagList;

    /**
     * 商城物品表
     */
    FastMap<Integer, MallGoods>  mallGoodsTable;
    
    Hashtable<Byte, ArrayList<MallGoods>> goodsTable;

    /**
     * 单例
     */
    private static MallGoodsDict instance;

    /**
     * 私有构造
     */
    private MallGoodsDict()
    {
        equipmentList = new ArrayList<MallGoods>();
        medicamentList = new ArrayList<MallGoods>();
        materialList = new ArrayList<MallGoods>();
        skillBookList = new ArrayList<MallGoods>();
        hotGoodsList = new ArrayList<MallGoods>();
        petList = new ArrayList<MallGoods>();
        bagList = new ArrayList<MallGoods>();
        mallGoodsTable = new FastMap<Integer, MallGoods>();
        petList = new  ArrayList<MallGoods>();
        petGoodsList = new  ArrayList<MallGoods>();
        petEquipmentList = new  ArrayList<MallGoods>();
        goodsTable = new Hashtable<Byte, ArrayList<MallGoods>>();
        
        for (int i = 0; i < ChargeServiceImpl.getInstance().getConfig().type_string.length; i++) {
        	if(!ChargeServiceImpl.getInstance().getConfig().type_string[i].equals("nullPage"))
        	{
        		goodsTable.put((byte)i, new ArrayList<MallGoods>());
        	}
		}

    }

    /**
     * 获取商城物品
     * 
     * @param _goodsID
     * @return
     */
    public MallGoods getMallGoods (int _goodsID)
    {
        return mallGoodsTable.get(_goodsID);
    }
    
    /**
     * 获得整个商城.
     * @return
     */
    public Hashtable<Byte, ArrayList<MallGoods>> getMallTable ()
    {
    	return goodsTable;
    }

    /**
     * 获取物品列表
     * 
     * @param _goodsType
     * @return
     */
    public ArrayList<MallGoods> getGoodsList (byte _goodsType)
    {
        switch (_goodsType)
        {
            case OperateMallGoods.TYPE_HOT:
            {
                return hotGoodsList;
            }
            case OperateMallGoods.TYPE_PET:
            {
                return petList;
            }
            case OperateMallGoods.TYPE_EQUIPMENT:
            {
                return equipmentList;
            }
            case OperateMallGoods.TYPE_MATERIAL:
            {
                return materialList;
            }
            case OperateMallGoods.TYPE_MEDICAMENT:
            {
                return medicamentList;
            }
            case OperateMallGoods.TYPE_BAG:
            {
                return bagList;
            }
            case OperateMallGoods.TYPE_SKILL_BOOK:
            {
                return skillBookList;
            }
            case OperateMallGoods.TYPE_PET_EQUIP:
            {
                return petEquipmentList;
            }
            case OperateMallGoods.TYPE_PET_GOODS:
            {
                return petGoodsList;
            }
        }

        return null;
    }

    /**
     * 获取单例
     * 
     * @return 字典单例
     */
    public static MallGoodsDict getInstance ()
    {
        if (null == instance)
        {
            instance = new MallGoodsDict();
        }

        return instance;
    }

    /**
     * 加载防具模板对象
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

            if (dataFileList.length > 0)
            {
                String data;
                boolean isHot = false;
                Goods goods = null;

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
                            MallGoods mallGoods = new MallGoods();

                            try
                            {
                            	mallGoods.desc = "";
                                data = subE.elementTextTrim("id");

                                if (null != data)
                                {
                                    mallGoods.id = Integer.parseInt(data);

                                    data = subE.elementTextTrim("name");

                                    if (null != data)
                                    {
                                        mallGoods.name = data;
                                        data = subE.elementTextTrim("hot");

                                        if (null != data)
                                        {
                                            if (data.equals("是"))
                                            {
                                                isHot = true;
                                            }

                                            data = subE.elementTextTrim("type");

                                            if (null != data)
                                            {
                                                mallGoods.type = Byte.valueOf(data);

                                                data = subE.elementTextTrim("trait");

                                                if (null != data)
                                                {
                                                    mallGoods.trait = EGoodsTrait
                                                            .getTrait(data);

                                                    data = subE
                                                            .elementTextTrim("price");

                                                    if (null != data)
                                                    {
                                                        mallGoods.price = Integer
                                                                .parseInt(data);
                                                        data = subE
                                                                .elementTextTrim("icon");

                                                        if (null != data)
                                                        {
                                                            mallGoods.icon = Short
                                                                    .parseShort(data);
                                                            data = subE
                                                                    .elementTextTrim("goodsTypeNumber");

                                                            if (null != data)
                                                            {
                                                                mallGoods.goodsList = new int[Integer
                                                                        .parseInt(data)][2];

                                                                for (int i = 1; i <= mallGoods.goodsList.length; i++)
                                                                {
                                                                    data = subE
                                                                            .elementTextTrim("goods"
                                                                                    + i
                                                                                    + "ID");

                                                                    if (null != data)
                                                                    {
                                                                        mallGoods.goodsList[i - 1][0] = Integer
                                                                                .parseInt(data);
                                                                        goods = GoodsContents
                                                                                .getGoods(mallGoods.goodsList[i - 1][0]);

                                                                        if (null != goods)
                                                                        {
                                                                            data = subE
                                                                                    .elementTextTrim("goods"
                                                                                            + i
                                                                                            + "Num");

                                                                            if (null != data)
                                                                            {
                                                                                mallGoods.goodsList[i - 1][1] = Short
                                                                                        .parseShort(data);

                                                                                if (mallGoods.goodsList[i - 1][1] > 0)
                                                                                {
                                                                                    data = subE
                                                                                            .elementTextTrim("description");

                                                                                    if (1 == mallGoods.goodsList.length)
                                                                                    {
                                                                                        if (null != data)
                                                                                        {
                                                                                            mallGoods.desc = data
                                                                                                    + "\n"
                                                                                                    + goods
                                                                                                            .getDescription();
                                                                                        }
                                                                                        else
                                                                                        {
                                                                                            mallGoods.desc = goods
                                                                                                    .getDescription();
                                                                                        }
                                                                                    }
                                                                                    else
                                                                                    {
                                                                                        if (null != data)
                                                                                        {
                                                                                            mallGoods.desc = data;
                                                                                        }
                                                                                        else
                                                                                        {
                                                                                            LogWriter
                                                                                                    .println("商城物品无描述，编号："
                                                                                                            + mallGoods.id);
                                                                                        }
                                                                                    }
                                                                                }
                                                                                else
                                                                                {
                                                                                    LogWriter
                                                                                            .println("错误的商城物品数量，编号："
                                                                                                    + mallGoods.goodsList[i - 1][0]);

                                                                                    continue;
                                                                                }
                                                                            }
                                                                        }
                                                                        else
                                                                        {
                                                                            LogWriter
                                                                                    .println("错误的商城物品编号："
                                                                                            + mallGoods.goodsList[i - 1][0]);

                                                                            continue;
                                                                        }
                                                                    }
                                                                }

                                                                if (isHot)
                                                                {
                                                                    hotGoodsList
                                                                            .add(mallGoods);

                                                                    isHot = false;
                                                                }

                                                                if (null != goods
                                                                        && 1 == mallGoods.goodsList.length
                                                                        && 1 == mallGoods.goodsList[0][1]
                                                                        && 1 < goods
                                                                                .getMaxStackNums())
                                                                {
                                                                    mallGoods
                                                                            .setBuyNumberPerTime((byte) goods
                                                                                    .getMaxStackNums());
                                                                }
                                                                data = subE.elementTextTrim("description");
                                                                if(data != null) {
                                                                	mallGoods.desc = data;
                                                                }
                                                                mallGoodsTable
                                                                        .put(
                                                                                mallGoods.id,
                                                                                mallGoods);
                                                                //add by zhengl; date: 2011-03-09; note: 添加新规则下的商城物品列表
                                                                goodsTable.get(mallGoods.type).add(mallGoods);

                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                else
                                {
                                    LogWriter.println("商城物品数据错误，无编号");
                                }
                            }
                            catch (Exception e)
                            {
                                LogWriter.println("加载商城物品数据错误，编号:"
                                        + mallGoods.id);
                            }
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
