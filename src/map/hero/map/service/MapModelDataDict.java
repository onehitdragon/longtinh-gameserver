package hero.map.service;

import hero.map.Decorater;
import hero.map.Map;
import hero.map.MapModelData;
import hero.map.detail.Cartoon;
import hero.map.detail.Door;
import hero.map.detail.OtherObjectData;
import hero.map.detail.PopMessage;
import hero.npc.dict.GearDataDict;
import hero.npc.dict.GroundTaskGoodsDataDict;
import hero.npc.dict.MonsterDataDict;
import hero.npc.dict.NpcDataDict;
import hero.npc.dict.GroundTaskGoodsDataDict.GroundTaskGoodsData;
import hero.npc.service.NotPlayerServiceImpl;
import hero.share.service.DataConvertor;
import hero.share.service.LogWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javolution.util.FastMap;
import org.apache.log4j.Logger;

import static hero.npc.dict.MonsterDataDict.*;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MapModelDataDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-30 上午10:03:57
 * @描述 ：
 */

public class MapModelDataDict
{
    private static Logger log = Logger.getLogger(MapModelDataDict.class);
    /**
     * 字典容器
     */
    private FastMap<Short, MapModelData> dictionary;

    /**
     * 单例
     */
    private static MapModelDataDict      instance;

    /**
     * 获取单例
     * 
     * @return
     */
    public static MapModelDataDict getInstance ()
    {
        if (null == instance)
        {
            instance = new MapModelDataDict();
        }

        return instance;
    }

    /**
     * 私有构造
     */
    private MapModelDataDict()
    {
        dictionary = new FastMap<Short, MapModelData>();
    }

    /**
     * 根据地图编号获取地图模板数据
     * 
     * @param _mapID
     * @return
     */
    public MapModelData getMapModelData (short _mapID)
    {
        MapModelData mapModelData = dictionary.get(_mapID);

        if (null == mapModelData)
        {
            LogWriter.println("无法找到地图模板，编号：" + _mapID);
        }

        return mapModelData;
    }

    /**
     * 获取地图模板数据列表
     * 
     * @return
     */
    public ArrayList<MapModelData> getMapModelDataList ()
    {
        Iterator<MapModelData> iterator = dictionary.values().iterator();
        ArrayList<MapModelData> mapModelDataList = new ArrayList<MapModelData>();

        while (iterator.hasNext())
        {
            mapModelDataList.add(iterator.next());
        }

        return mapModelDataList;
    }

    protected void init (String _dataPath)
    {
        File fileList;

        try
        {
            fileList = new File(_dataPath);
        }
        catch (Exception e)
        {
            LogWriter.println("未找到地图模板文件目录：" + _dataPath);

            return;
        }

        try
        {
            File[] mapList = fileList.listFiles();

            if (mapList.length > 0)
            {
                dictionary.clear();
            }

            MapModelData mapData;

            for (File dataFile : mapList)
            {
                if (!dataFile.getName().endsWith(".map"))
                {
                    continue;
                }
                log.debug("== mapFile name = " + dataFile.getName());
                mapData = loadMapFile(dataFile);

                MapModelData existMapData = dictionary.get(mapData.id);

                if (null == existMapData)
                {
                    dictionary.put(mapData.id, mapData);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 从地图模板文件生成数据模板
     * 
     * @param _mapFile
     * @return
     */
    private MapModelData loadMapFile (File _mapFile)
    {
        FileInputStream fis = null;
        MapModelData mapData = new MapModelData();

        short i;
        byte[] twoBytes = new byte[2];
        int length;

        short portsNum;
        byte[] portsInfo;
        int npcNum;
        ArrayList<Integer> movePathList = new ArrayList<Integer>();
        int movePathNodeNums;

        try
        {
            fis = new FileInputStream(_mapFile);
            fis.read();// 元素图片的编号
            fis.read();// 元素图片切片的宽度（像素）
            fis.read();// 元素图片切片的高度（像素）

            fis.read(twoBytes, 0, 2);
            mapData.id = DataConvertor.bytes2Short(twoBytes);
            if(mapData.id != Short.parseShort(_mapFile.getName().substring(0,_mapFile.getName().indexOf(".map")))){
                log.error("地图名称 和 地图 ID 不匹配，找策划去！");
                return null;
            }
            log.debug("开始读取.map文件: id=" + mapData.id);
            //edit:	zhengl
            //date:	2010-10-25
            //note:	这个地方应该读取一个short,而原先读取1个byte的行为将导致新地图读取字节错位.
            fis.read(twoBytes, 0, 2);
            mapData.tileImageID = DataConvertor.bytes2Short(twoBytes);
//            mapData.tileImageID = (short) fis.read();
            //end.
            fis.read();// cellWidth
            fis.read();// cellHeight

            fis.read(twoBytes, 0, 2);
            mapData.pkMark = twoBytes[0];
            mapData.modifiable = twoBytes[1] == 1 ? true : false;
            mapData.mapTypeValue = fis.read();

            byte[] monsterIDAbout = new byte[fis.read()];

            if (monsterIDAbout.length > 0)
            {
                fis.read(monsterIDAbout);
                mapData.monsterModelIDAbout = DataConvertor
                        .bytes2String(monsterIDAbout);
            }

            length = fis.read();
            byte[] mapName = new byte[length];

            fis.read(mapName);
            // vinh - VH
            mapData.name = new String(mapName, "UTF-8");
            mapData.mapWeatherValue = fis.read();

            int width = fis.read();
            int height = fis.read();

            mapData.width = (short) width;
            mapData.height = (short) height;

            //装饰层层上的动画数量
            //新地图功能
            mapData.animNum = fis.read();
            npcNum = fis.read();

            mapData.bottomCanvasData = new byte[height * width];
            fis.read(mapData.bottomCanvasData, 0, height * width);
            
            //新地图功能.
            mapData.transformData1 = new byte[height * width];
            fis.read(mapData.transformData1, 0, height * width);

            short[] temp = new short[height * width];
            //新地图功能
            mapData.transformData2 = new byte[height * width];
            
            int upTileLayerSize = fis.read();
//            fis.read(mapData.transformData2,0,height * width);
            for(int z = 0; z < (width*height); z++){
            	temp[z] = -1;
            	mapData.transformData2[z] = 0;
            }
            for(int tileIndex = 0; tileIndex < upTileLayerSize; tileIndex++){
            	int row = fis.read();
            	int column = fis.read();
            	//edit by zhengl ; date: 2010-12-16 ; note: 该值超过127.
            	short tileId = (short)fis.read();
            	temp[row*width+column] = tileId;
            }
            for (int tileIndex = 0; tileIndex < upTileLayerSize; tileIndex++)
            {
	            int row = fis.read();
	            int column = fis.read();
	            byte tileTransform = (byte)(fis.read());
	            mapData.transformData2[row * width + column] = tileTransform;
            }

            log.debug("mapid="+mapData.id+",name["+mapData.name+"] transformData2 size="+mapData.transformData2.length);
            
            mapData.elementImageIDList = new ArrayList<Short>();
            ArrayList<Short> elementList = new ArrayList<Short>();

            short elementImageID;

            java.util.Map<Short, String> hashMap = new HashMap<Short, String>();
            for (short h = 0; h < height; h++)
            {
                for (short w = 0; w < width; w++)
                {
                    elementImageID = temp[h * width + w];
                    if (elementImageID > -1)
                    {
                        elementList.add(w);
                        elementList.add(h);
                        //del by zhengl 2010-11-04 note:+1是之前为了适配100.png,现在已无意义
//                        elementImageID += 1;
                        //end
                        elementList.add(elementImageID);
                        if (!mapData.elementImageIDList.contains(elementImageID))
                        {
                            mapData.elementImageIDList.add(elementImageID);
                        }
                    }
                }
            }
            
            mapData.elementCanvasData = new short[elementList.size()];

            for (short l = 0; l < mapData.elementCanvasData.length; l++)
            {
                mapData.elementCanvasData[l] = elementList.get(l).shortValue();
            }

            mapData.bornX = (short) fis.read();
            mapData.bornY = (short) fis.read();

            if(Map.IS_NEW_MAP){//添加装饰层各个元素
            	int npcModelIDBytesLength = 0;
                byte[] decModelIDBytes;
                int npcNameBytesLength = 0;
                byte[] npcNameBytes;
                String npcModelID;

                mapData.decorateObjectList = new OtherObjectData[mapData.animNum];

                short imageID;
                log.debug("mapData animNum = " + mapData.animNum);
                for (i = 0; i < mapData.animNum; i++)
                {
                    mapData.decorateObjectList[i] = new OtherObjectData();
                    fis.read(twoBytes, 0, 2);
                    mapData.decorateObjectList[i].pngId = DataConvertor.bytes2Short(twoBytes);
                                  
                    npcModelIDBytesLength = fis.read();
                    decModelIDBytes = new byte[npcModelIDBytesLength];
                    fis.read(decModelIDBytes, 0, npcModelIDBytesLength);

                    npcModelID = new String(decModelIDBytes, 0, npcModelIDBytesLength).toLowerCase();

                    npcNameBytesLength = fis.read();

                    if (0 < npcNameBytesLength)
                    {
                        npcNameBytes = new byte[npcNameBytesLength];
                        fis.read(npcNameBytes, 0, npcNameBytesLength);
                    }

                    mapData.decorateObjectList[i].animationID = Short.valueOf(npcModelID);
                    mapData.decorateObjectList[i].x = (byte) fis.read();
                    mapData.decorateObjectList[i].y = (byte) fis.read();
                    mapData.decorateObjectList[i].z = (byte) fis.read();
                    log.debug("decorateObjectList["+i+"].animationID = " + mapData.decorateObjectList[i].animationID);
                    movePathNodeNums = fis.read();
                    log.debug("movePathNodeNums = " + movePathNodeNums);
                    if(movePathNodeNums > 0) {
                        mapData.decorateObjectList[i].decorater = new Decorater[movePathNodeNums];
                        for(int n=0; n<movePathNodeNums; n++){
                        	fis.read(twoBytes, 0, 2);
                        	mapData.decorateObjectList[i].decorater[n] = new Decorater();
                        	mapData.decorateObjectList[i].decorater[n].decorateId = DataConvertor.bytes2Short(twoBytes);
                            log.debug("mapData.decorateObjectList["+n+"].decorater["+n+"].decorateId = " +
                                                                        mapData.decorateObjectList[i].decorater[n].decorateId);
                        	mapData.decorateObjectList[i].decorater[n].x = (byte)fis.read();
                        	mapData.decorateObjectList[i].decorater[n].y = (byte)fis.read();
                        	mapData.decorateObjectList[i].decorater[n].z = (byte)fis.read();
                        }
                    }
                }
                //decorate for end
            }
            
            int npcModelIDBytesLength = 0;
            byte[] npcModelIDBytes;
            int npcNameBytesLength = 0;
            byte[] npcNameBytes;
            String npcModelID;

            mapData.notPlayerObjectList = new OtherObjectData[npcNum];

            short imageID;

            for (i = 0; i < npcNum; i++)
            {
                mapData.notPlayerObjectList[i] = new OtherObjectData();
                fis.read(twoBytes, 0, 2);
                
                npcModelIDBytesLength = fis.read();
                npcModelIDBytes = new byte[npcModelIDBytesLength];
                fis.read(npcModelIDBytes, 0, npcModelIDBytesLength);

                npcModelID = new String(npcModelIDBytes, 0, npcModelIDBytesLength).toLowerCase();
                log.debug("npc modeID = " + npcModelID);
                if (npcModelID.startsWith(NotPlayerServiceImpl.NPC_MODEL_ID_PREFIX))
                {
                	if(Map.IS_NEW_MAP){
                    	//npc 动画id(不再使用,代码暂不删除,不会有影响.)
                    	mapData.notPlayerObjectList[i].animationID = DataConvertor.bytes2Short(twoBytes);
                	}
                	
                    if (null == mapData.fixedNpcImageIDList)
                    {
                        mapData.fixedNpcImageIDList = new ArrayList<Short>();
                    }

                    imageID = Short.parseShort(NpcDataDict.getInstance().getNpcData(npcModelID).imageID);

                    if (!mapData.fixedNpcImageIDList.contains(imageID))
                    {
                        mapData.fixedNpcImageIDList.add(imageID);
                    }
                }
                else if (npcModelID
                        .startsWith(NotPlayerServiceImpl.MONSTER_MODEL_ID_PREFIX))
                {
                    if (null == mapData.fixedMonsterImageIDList)
                    {
                        mapData.fixedMonsterImageIDList = new ArrayList<Short>();
                    }
                    MonsterDataDict.MonsterData monster = MonsterDataDict.getInstance().getMonsterData(npcModelID);
                    log.debug("map id="+mapData.id+" -- moster = " + monster.modelID);
                    String imgId = monster.imageID;
                    imageID = Short.parseShort(imgId);

                    if (!mapData.fixedMonsterImageIDList.contains(imageID))
                    {
                        mapData.fixedMonsterImageIDList.add(imageID);
                    }
                }
                else if (npcModelID
                        .startsWith(NotPlayerServiceImpl.GROUND_TASK_GOODS_MODEL_ID_PREFIX))
                {
                    if (null == mapData.groundTaskGoodsImageIDList)
                    {
                        mapData.groundTaskGoodsImageIDList = new ArrayList<Short>();
                    }
                    GroundTaskGoodsData gData = GroundTaskGoodsDataDict.getInstance()
                    .getTaskGoodsData(npcModelID);
                    if(gData != null) 
                    {
                    	imageID = gData.imageID;
                    }
                    else {
                    	imageID = -1;
                    	log.warn("加载任务机关图片失败npcModelID:"+npcModelID);
					}

                    if (!mapData.groundTaskGoodsImageIDList.contains(imageID))
                    {
                        mapData.groundTaskGoodsImageIDList.add(imageID);
                    }
                }
                else if (npcModelID
                        .startsWith(NotPlayerServiceImpl.GEAR_MODEL_ID_PREFIX))
                {
                    if (null == mapData.taskGearImageIDList)
                    {
                        mapData.taskGearImageIDList = new ArrayList<Short>();
                    }

                    imageID = GearDataDict.getInstance()
                            .getGearData(npcModelID).imageID;

                    if (!mapData.taskGearImageIDList.contains(imageID))
                    {
                        mapData.taskGearImageIDList.add(imageID);
                    }
                }

                npcNameBytesLength = fis.read();

                if (0 < npcNameBytesLength)
                {
                    npcNameBytes = new byte[npcNameBytesLength];
                    fis.read(npcNameBytes, 0, npcNameBytesLength);
                }

                mapData.notPlayerObjectList[i].modelID = npcModelID;
                mapData.notPlayerObjectList[i].x = (byte) fis.read();
                mapData.notPlayerObjectList[i].y = (byte) fis.read();
            	mapData.notPlayerObjectList[i].z = (byte) fis.read();
            	
            	movePathNodeNums = fis.read();

//            	for(int n=0; n<movePathNodeNums; n++){//这里用 decorater存放移动路径
//                	fis.read(twoBytes, 0, 2);
//                	mapData.notPlayerObjectList[i].decorater[n].decorateId = DataConvertor.bytes2Short(twoBytes);
//                	mapData.notPlayerObjectList[i].decorater[n].x = (byte)fis.read();
//                	mapData.notPlayerObjectList[i].decorater[n].y = (byte)fis.read();
//                	mapData.notPlayerObjectList[i].decorater[n].z = (byte)fis.read();
//                }
            	
            	//移动路径加载.
                if (movePathNodeNums > 0)
                {
                    movePathList.clear();

                    int x = mapData.notPlayerObjectList[i].x, y = mapData.notPlayerObjectList[i].y;
                    int nodeX, nodeY;

                    movePathList.add(x);
                    movePathList.add(y);

                    while (movePathNodeNums > 0)
                    {
                    	fis.read(twoBytes, 0, 2);//动画ID也忽略.
                        nodeX = fis.read();
                        nodeY = fis.read();
                        fis.read();//z坐标先忽略

                        if (nodeX == x)
                        {
                            if (y > nodeY)
                            {
                                while (y > nodeY)
                                {
                                    movePathList.add(x);
                                    movePathList.add(--y);
                                }
                            }
                            else
                            {
                                while (y < nodeY)
                                {
                                    movePathList.add(x);
                                    movePathList.add(++y);
                                }
                            }
                        }
                        else if (nodeY == y)
                        {
                            if (x > nodeX)
                            {
                                while (x > nodeX)
                                {
                                    movePathList.add(--x);
                                    movePathList.add(y);
                                }
                            }
                            else
                            {
                                while (x < nodeX)
                                {
                                    movePathList.add(++x);
                                    movePathList.add(y);
                                }
                            }
                        }
                        else
                        {
                            LogWriter.println(".map文件->怪物行走路径节点数据错误：" + mapData.name
                                    + "---"
                                    + mapData.notPlayerObjectList[i].modelID);

                            return null;
                        }

                        x = nodeX;
                        y = nodeY;

                        movePathNodeNums--;
                    }

                    int pathLength = movePathList.size() / 2;
                    mapData.notPlayerObjectList[i].movePath = new byte[pathLength][2];

                    for (int l = 0; l < pathLength; l++)
                    {
                        mapData.notPlayerObjectList[i].movePath[l][0] = movePathList
                                .get(l * 2).byteValue();
                        mapData.notPlayerObjectList[i].movePath[l][1] = movePathList
                                .get(l * 2 + 1).byteValue();
                    }
                }
            }

            npcNameBytes = null;

            mapData.resourceCanvasMap = new byte[height * width];
            fis.read(mapData.resourceCanvasMap, 0, height * width);
            
        	//资源层旋转
            //新地图附加的功能(旋转)
        	mapData.resourceTransformData = new byte[height * width];
        	fis.read(mapData.resourceTransformData,0,height * width);

            portsNum = (byte) fis.read();
            mapData.externalPortList = new Door[portsNum];
            portsInfo = new byte[2];

            for (i = 0; i < portsNum; i++)
            {
                mapData.externalPortList[i] = new Door();
                mapData.externalPortList[i].x = (short) fis.read();
                mapData.externalPortList[i].y = (short) fis.read();
                mapData.externalPortList[i].direction = (byte) fis.read();
                fis.read(portsInfo, 0, 2);
                mapData.externalPortList[i].targetMapID = DataConvertor.bytes2Short(portsInfo);
                log.debug("@ mapData.externalPortList["+i+"] targetMapID = " + mapData.externalPortList[i].targetMapID);
                mapData.externalPortList[i].targetMapX = (short) fis.read();
                mapData.externalPortList[i].targetMapY = (short) fis.read();
                mapData.externalPortList[i].visible = true;

                npcModelIDBytesLength = fis.read();

                if (npcModelIDBytesLength > 0)
                {
                    npcModelIDBytes = new byte[npcModelIDBytesLength];
                    fis.read(npcModelIDBytes, 0, npcModelIDBytesLength);

                    String monsterModelID = new String(npcModelIDBytes, 0,
                            npcModelIDBytesLength, "UTF-16BE").toLowerCase();

                    mapData.externalPortList[i].monsterIDAbout = monsterModelID;
                }

            }
            log.debug("@ mapData.externalPortList end..");
            int msgNum = fis.read();
            short msgLen = 0;
            byte[] tempMsg = null;
            mapData.popMessageList = new PopMessage[msgNum];

            for (i = 0; i < msgNum; i++)
            {
                mapData.popMessageList[i] = new PopMessage();
                mapData.popMessageList[i].x = (byte) fis.read();
                mapData.popMessageList[i].y = (byte) fis.read();
                fis.read(portsInfo, 0, 2);
                msgLen = DataConvertor.bytes2Short(portsInfo);
                tempMsg = new byte[msgLen];
                fis.read(tempMsg, 0, msgLen);
                mapData.popMessageList[i].msgContent = DataConvertor
                        .bytes2String(tempMsg, msgLen);
            }

            portsInfo = null;

            int internalPortNum = fis.read();
            mapData.internalPorts = new byte[internalPortNum][4];

            for (int n = 0; n < internalPortNum; n++)
            {
                mapData.internalPorts[n][0] = (byte) fis.read();
                mapData.internalPorts[n][1] = (byte) fis.read();
                mapData.internalPorts[n][2] = (byte) fis.read();
                mapData.internalPorts[n][3] = (byte) fis.read();
            }

            fis.read(twoBytes, 0, 2);
            short unpassNums = DataConvertor.bytes2Short(twoBytes);
            mapData.unpassData = new byte[unpassNums * 2];
            mapData.unpassMarkArray = new byte[height][width];

            for (int n = 0; n < mapData.unpassData.length;)
            {
                mapData.unpassData[n] = (byte) fis.read();
                mapData.unpassData[n + 1] = (byte) fis.read();
                mapData.unpassMarkArray[mapData.unpassData[n]][mapData.unpassData[n + 1]] = 1;

                n += 2;
            }

            int cartoonNum = fis.read();

            if (cartoonNum > 0)
            {
                mapData.cartoonList = new Cartoon[cartoonNum];

                for (i = 0; i < cartoonNum; i++)
                {
                    mapData.cartoonList[i] = new Cartoon();
                    mapData.cartoonList[i].x = (byte) fis.read();
                    mapData.cartoonList[i].y = (byte) fis.read();
                    mapData.cartoonList[i].firstTileIndex = (byte) fis.read();
                    int followFrameCount = fis.read();
                    mapData.cartoonList[i].followTileIndexList = new byte[followFrameCount];
                    fis.read(mapData.cartoonList[i].followTileIndexList);
                }
            }
            
            //下面是遮盖层动画
            cartoonNum = fis.read();

            if (cartoonNum > 0)
            {
                mapData.cartoonList2 = new Cartoon[cartoonNum];

                for (i = 0; i < cartoonNum; i++)
                {
                    mapData.cartoonList2[i] = new Cartoon();
                    mapData.cartoonList2[i].x = (byte) fis.read();
                    mapData.cartoonList2[i].y = (byte) fis.read();
                    mapData.cartoonList2[i].firstTileIndex = (byte) fis.read();
                    int followFrameCount = fis.read();
                    mapData.cartoonList2[i].followTileIndexList = new byte[followFrameCount];
                    fis.read(mapData.cartoonList2[i].followTileIndexList);
                }
            }
            log.debug(".map文件读取成功:" + mapData.id +" ["+ mapData.name+"]");
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
            LogWriter.println("error:.map文件读取失败，编号：" + mapData.id);
            log.error("error:.map文件读取失败，编号：" + mapData.id);

            LogWriter.error(this, ex);
        }
        finally
        {
            try
            {
                if (null != fis)
                {
                    fis.close();
                    fis = null;
                }
            }
            catch (IOException ioe)
            {

            }
        }
        return mapData;
    }
}
