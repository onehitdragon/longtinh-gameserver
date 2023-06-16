package hero.pet.service;

import java.io.File;
import java.util.Iterator;

import javolution.util.FastMap;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import hero.pet.Pet;
import hero.pet.PetPK;
import hero.share.service.LogWriter;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 PetDictionary.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-13 上午09:56:50
 * @描述 ：
 */

public class PetDictionary
{
	private static Logger log = Logger.getLogger(PetDictionary.class);
    /**
     * 宠物字典
     */
    private FastMap<Integer, Pet>  dictionary;
    
//    private FastMap<Integer, Feed> feedDict;

    /**
     * 单例
     */
    private static PetDictionary instance;

    /**
     * 私有构造
     */
    private PetDictionary()
    {
        dictionary = new FastMap<Integer, Pet>();
//        feedDict = new FastMap<Integer, Feed>();
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static PetDictionary getInstance ()
    {
        if (null == instance)
        {
            instance = new PetDictionary();
        }

        return instance;
    }

    /**
     * 获取宠物
     * 此方法无法获得某个玩家的宠物
     * @param _petID 是宠物.xml的id,不是数据库ID
     * @return
     */
    public Pet getPet (int _aID)
    {
        return dictionary.get(_aID);
    }
    /**
     * 通过 petpk 获取宠物原型
     * @param pk
     * @return
     */
    public Pet getPet(PetPK pk){
        log.debug("get pet by pk : " + pk.intValue());
        for (Pet pet : dictionary.values()) {
            if (pet.aid == pk.intValue()) {
                return pet;
            }
        }
    	return null;
    }
    
    /**
     * 取宠物字典
     * @return
     */
    public FastMap<Integer, Pet> getPetDict(){
    	return dictionary;
    }
    
    /**
     * 获取饲料字典
     * @param feedId
     * @return
     */
//    public Feed getFeed(int feedId){
//    	return feedDict.get(feedId);
//    }

    /**
     * 加载宠物字典
     * 
     * @param _dataPath
     */
    public void load (String _dataPath, String _feed_dataPath) throws Exception
    {
        File dataPath;

        try
        {
            dataPath = new File(_dataPath);

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
                
                int aid;
                short kind,stage,type,iconID,fun;
                short imageID,animationID;
                int atk,speed,a_str,a_agi,a_intel,a_spi,a_luck;

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();

                    if (null != subE)
                    {
                        String data = null;

                        Pet pet = new Pet();
                        PetPK pk = new PetPK();

                        try
                        {
                            aid = Integer.parseInt(subE.elementTextTrim("id"));
                            pet.name = subE.elementTextTrim("name");
                            kind = Short.parseShort(subE.elementTextTrim("kind"));
                        	stage = Short.parseShort(subE.elementTextTrim("stage"));	
            				type = Short.parseShort(subE.elementTextTrim("type"));
            				iconID = Short.parseShort(subE.elementTextTrim("iconID"));
                			imageID = Short.parseShort(subE.elementTextTrim("imageID"));
                			animationID = Short.parseShort(subE.elementTextTrim("animationID"));
                			fun = Short.parseShort(subE.elementTextTrim("fun"));
                			
                			speed = Integer.parseInt(subE.elementTextTrim("speed"));
            				a_str = Integer.parseInt(subE.elementTextTrim("a_str"));
            				a_agi = Integer.parseInt(subE.elementTextTrim("a_agi"));
            				a_intel = Integer.parseInt(subE.elementTextTrim("a_intel"));
            				a_spi = Integer.parseInt(subE.elementTextTrim("a_spi"));
            				a_luck = Integer.parseInt(subE.elementTextTrim("a_luck"));
            				//add by zhengl; date: 2011-03-22; note: 坐骑宠依靠关联光环ID来进行提速
            				data = subE.elementTextTrim("mountFunction");
            				if(null != data){
            					pet.mountFunction = Integer.parseInt(data);
            				}
            				if(stage == 2 && type ==2){
            					data = subE.elementTextTrim("atk");
            					if(null != data){
            						pet.atk = Integer.parseInt(data);
            					}
                				data = subE.elementTextTrim("maxAtkHarm");
                				if( null != data){
                					pet.maxAtkHarm=Integer.parseInt(data);
                				}
                				data = subE.elementTextTrim("minAtkHarm");
                				if( null != data){
                					pet.minAtkHarm=Integer.parseInt(data);
                				} 
                				data = subE.elementTextTrim("magicHarm");
                				if(null != data){
                					pet.magicHarm = Integer.parseInt(data);
                				}
                				data = subE.elementTextTrim("maxMagicHarm");
                				if(null != data){
                					pet.maxMagicHarm = Integer.parseInt(data);
                				}
                				data = subE.elementTextTrim("minMagicHarm");
                				if(null != data){
                					pet.minMagicHarm = Integer.parseInt(data);
                				}
            				}
            				pk.setKind(kind);
                			pk.setStage(stage);
                			pk.setType(type);
                			pet.aid = aid;
                			pet.pk = pk;
                			pet.iconID = iconID;
                			pet.imageID = imageID;
                			pet.animationID = animationID;
                			pet.fun = fun;
                			pet.speed = speed;
            				
            				pet.a_str = a_str;
            				pet.a_agi = a_agi;
            				pet.a_intel = a_intel;
            				pet.a_spi = a_spi;
            				pet.a_luck = a_luck;
            			            			
//            				log.debug("load pet.xml pet.pk = " + pet.pk.intValue());
            				dictionary.put(aid, pet);
            				
                        }
                        
                        catch (Exception ex)
                        {
                            LogWriter.println("加载宠物出错，编号:" + pet.pk.getKind());
                        }

                    }
                }
                log.debug("dictionary size = " + dictionary.size());
            }
            
            // 加载饲料数据
            /*dataPath = new File(_feed_dataPath);

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
                
                int id;
                short type;
                String name;
                while(rootIt.hasNext()){
                	Element subE = rootIt.next();
                	if( null != subE){
                		id = Integer.parseInt(subE.elementTextTrim("id"));
                		type = Short.parseShort(subE.elementTextTrim("type"));
                		name = subE.elementTextTrim("name");
                		
                		Feed feed = new Feed(id,type,name);
                		feedDict.put(id, feed);
                	}
                }
                
            }*/
        }
        catch (Exception e)
        {
            LogWriter.error(this, e);
        }
    }
}
