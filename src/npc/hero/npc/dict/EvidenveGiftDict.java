package hero.npc.dict;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javolution.util.FastMap;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import hero.item.Goods;
import hero.item.dictionary.GoodsContents;
import hero.npc.dict.QuestionDict.AwardData;
import hero.npc.function.system.EvidenveGetGift;

public class EvidenveGiftDict {
	
	private FastMap<Integer, EvidenveAward> awardDict;
	
	private ArrayList<EvidenveData> evidenveList;
	
	private static EvidenveGiftDict instance;
	
    public static EvidenveGiftDict getInstance ()
    {
        if (null == instance)
        {
            instance = new EvidenveGiftDict();
        }

        return instance;
    }
	
	private EvidenveGiftDict()
	{
		
	}
	
    public static void main (String[] args)
    {
    	EvidenveGiftDict.getInstance().load(
    			System.getProperty("user.dir") + "/" 
    			+ "res/data/npc/normal_npc/fun_data/evidenve_gift/", 
    			System.getProperty("user.dir") + "/" 
    			+ "res/data/npc/normal_npc/fun_data/evidenve_gift/award/");
    	EvidenveGiftDict.getInstance().getEvidenveData(1);
    }
    
    /**
     * 获得领取数据
     * @param _groupID
     * @return
     */
    public EvidenveData getEvidenveData(int _groupID)
    {
    	return this.evidenveList.get(_groupID);
    }
    
    /**
     * 功能名称获得
     * @return
     */
    public String[] getEvidenveGift()
    {
    	String[] functionName = new String[evidenveList.size()];
    	for (int i = 0; i < evidenveList.size(); i++) 
    	{
    		functionName[i] = evidenveList.get(i).name;
		}
    	return functionName;
    }
	
    private void loadAward(String _dataPath)
    {
		File dataPath;
        try
        {
            dataPath = new File(_dataPath);
            File[] dataFileList = dataPath.listFiles();
            awardDict = new FastMap<Integer, EvidenveAward>();
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
                		EvidenveAward awardData = new EvidenveAward();
                		awardData.id = Integer.valueOf(subE.elementTextTrim("id"));
                		String data = subE.elementTextTrim("exp");
                		if (data != null) 
                		{
                			awardData.exp = Integer.valueOf(data);
						}
                		data = subE.elementTextTrim("money");
                		if (data != null) 
                		{
                			awardData.money = Integer.valueOf(data);
						}
                		awardData.goodsSum = Integer.valueOf(subE.elementTextTrim("goodsSum"));
                		awardData.goodsList = new Goods[awardData.goodsSum];
                		int index = 0, goodsID = 0;
                		for (int i = 0; i < awardData.goodsSum; i++) 
                		{
                			index = i +1;
                			goodsID = Integer.valueOf(subE.elementTextTrim("goods"+ index +"ID"));
                			Goods goods = GoodsContents.getGoods(goodsID);
                			awardData.goodsList[i] = goods;
						}//end award data read for 
                		if (!this.awardDict.containsKey(awardData.id)) {
                			awardDict.put(awardData.id, awardData);
						}
                	}
                }
            }//end file read for
        }
        catch (Exception e) 
        {
			e.printStackTrace();
		}
    }
    
	
	public void load (String _funPath, String _awardPath)
	{
		loadAward(_awardPath);//最先加载奖励
		File dataPath;
        try
        {
            dataPath = new File(_funPath);
            File[] dataFileList = dataPath.listFiles();
            evidenveList = new ArrayList<EvidenveData>();
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
                		EvidenveData evidenve = new EvidenveData();
                		evidenve.id = Integer.valueOf(subE.elementTextTrim("id"));
                		evidenve.name = subE.elementTextTrim("name");
                		if (subE.elementTextTrim("isOpen").equals("是")) 
                		{
                			evidenve.isOpen = true;
						}
                		else 
                		{
                			evidenve.isOpen = false;
						}
                		if (evidenve.isOpen) 
                		{
                			evidenve.tableName = subE.elementTextTrim("tableName");
                			evidenve.awardID = Integer.valueOf(subE.elementTextTrim("awardID"));
                			evidenve.inputBoxSum = Integer.valueOf(subE.elementTextTrim("inputBoxSum"));
                			evidenve.inputBoxLenghts = new int[evidenve.inputBoxSum];
                			evidenve.inputBoxContents = new String[evidenve.inputBoxSum];
                			int index = 0;
                			for (int i = 0; i < evidenve.inputBoxSum; i++) 
                			{
                				index = i +1;
                				evidenve.inputBoxLenghts[i] = Integer.valueOf(
                						subE.elementTextTrim("inputBox"+index+"Lenght"));
                				evidenve.inputBoxContents[i] = subE.elementTextTrim(
                						"inputBox"+index+"Name");
							}
                			evidenve.wrongByInput = subE.elementTextTrim("wrongByInput");
                			evidenve.wrongByJoinIt = subE.elementTextTrim("wrongByJoinIt");
                			evidenve.wrongByUse = subE.elementTextTrim("wrongByUse");
                			evidenve.award = this.awardDict.get(evidenve.awardID);
                			evidenve.columnSum = Integer.valueOf(subE.elementTextTrim("columnSum"));
                			evidenve.columnNames = new String[evidenve.columnSum];
                			for (int i = 0; i < evidenve.columnSum; i++) 
                			{
                				index = i +1;
                				evidenve.columnNames[i] = subE.elementTextTrim("column"+index+"Name");
							}
                			evidenveList.add(evidenve);
						}
                	}
                }
            }//end for
        }
        catch (Exception e) 
        {
			e.printStackTrace();
		}
	}
	
    public static class EvidenveData
    { 
        public int   id, awardID, inputBoxSum, columnSum;
        
        public int[] inputBoxLenghts;
        
        public String[] inputBoxContents, columnNames;
        
        public EvidenveAward award;
        
        public boolean isOpen;
        
        public String name, tableName, wrongByInput, wrongByJoinIt, wrongByUse;
    }
	
    public static class EvidenveAward
    {
        public int   id, money, exp, goodsSum;

        public Goods[] goodsList;
    }

}
