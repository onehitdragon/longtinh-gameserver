/**
 * Copyright: Copyright (c) 2007
 * <br>
 * Company: Digifun
 * <br>
 */

package hero.map.service;

import hero.dungeon.Dungeon;
import hero.dungeon.service.DungeonServiceImpl;
import hero.item.dictionary.PetEquipmentDict;
import hero.map.Decorater;
import hero.map.EMapType;
import hero.map.EMapWeather;
import hero.map.Map;
import hero.map.MapModelData;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.map.detail.Door;
import hero.map.detail.OtherObjectData;
import hero.map.message.DisappearNotify;
import hero.npc.Monster;
import hero.npc.Npc;
import hero.npc.function.system.transmit.MapTransmitInfoDict;
import hero.npc.others.Animal;
import hero.npc.others.Box;
import hero.npc.others.DoorPlate;
import hero.npc.others.GroundTaskGoods;
import hero.npc.others.RoadInstructPlate;
import hero.npc.others.TaskGear;
import hero.npc.service.NotPlayerServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.Direction;
import hero.share.EObjectType;
import hero.share.ME2GameObject;
import hero.share.service.LogWriter;
import hero.share.service.ME2ObjectList;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import yoyo.service.base.AbsServiceAdaptor;
import yoyo.service.base.session.Session;

import javolution.util.FastMap;


public class MapServiceImpl extends AbsServiceAdaptor<MapConfig> implements
        IMapService
{
	private static Logger log = Logger.getLogger(MapServiceImpl.class);
    /**
     * 单例
     */
    private static MapServiceImpl  instance;

    /**
     * 地图元素图片字节字典
     */
    private FastMap<Short, byte[]> mapElementImageTable;

    /**
     * 普通地图字典（地图编号：地图）
     */
//    private FastMap<Short, Map>    normalMapDictionary;

    /**
     * 地图字典(地图编号: 地图)
     * 因为现在跳转到副本地图要和跳转到普通地图一样，不再用副本NPC了
     * 包含普通地图和副本地图
     */
    private FastMap<Short, Map>    mapDictionary;
    
    /**
     * 可以摆摊的地图ID列表
     * 应该在地图上指定或用EXCEL表配置，现在暂时这样写死
     */
    public static final short[] canStroeMapIDs = new short[]{401,118,408,120,7,65,28,86,35,93,51,109,117,125,138,149};

    /**
     * 私有构造
     */
    private MapServiceImpl()
    {
        config = new MapConfig();
        mapElementImageTable = new FastMap<Short, byte[]>();
//        normalMapDictionary = new FastMap<Short, Map>();
        mapDictionary = new FastMap<Short, Map>();
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static MapServiceImpl getInstance ()
    {
        if (instance == null)
        {
            instance = new MapServiceImpl();
        }
        return instance;
    }

    @Override
    protected void start ()
    {
        MapMusicDict.getInstance().init(config.map_music_config_path);
        MapRelationDict.getInstance().init(config.getMapRelationDataPath());
        AreaImageDict.getInstance().init(config.getAreaImagePath());
        MiniMapImageDict.getInstance().init(config.getMicroMapImagePath());
        MapTileDict.getInstance().init(config.getMapTileImagePath());
        AreaDict.getInstance().init(config.getAreaDataPath());
        MapModelDataDict.getInstance().init(config.getMapModelFilePath());
        PetEquipmentDict.getInstance().load(config.pet_equip_data_path);
        loadNormalMap();
        loadMapElementImage(config.getMapElementImagePath());
        MapTransmitInfoDict.getInstance();

        WorldMapDict.getInstance().load(config);
    }

    @Override
    public void createSession (Session _session)
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.service.basic.ServiceAdaptor#sessionFree(int)
     */
    @Override
    public void sessionFree (Session _session)
    {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(
                _session.userID);

        if (player != null)
        {
            player.where().getPlayerList().remove(player);

            MapSynchronousInfoBroadcast.getInstance().put(
                    player.where(),
                    new DisappearNotify(player.getObjectType().value(), player
                            .getID(),player.getHp(),player.getBaseProperty().getHpMax(),
                            player.getMp(),player.getBaseProperty().getMpMax()), true, player.getID());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.service.basic.ServiceAdaptor#clear(int)
     */
    public void clean (int _userID)
    {

    }
    
    /**
     * 根据地图ID判断该地图是否可以摆摊
     * @param mapID
     * @return
     */
    public boolean canStroe(short mapID){
    	for(int i=0; i<canStroeMapIDs.length; i++){
    		if(mapID == canStroeMapIDs[i]){
    			return true;
    		}
    	}
    	return false;
    }

    /**
     * 获取世界的名称
     * @param type 1:神龙界
     *              2:魔龙界
     *              3:仙界
     * @return
     */
    public String getWorldNameByType(byte type){
        return config.world_names[type-1];
    }

    /**
     * 根据地图编号获取地图
     * 
     * @param _mapID
     * @return
     */
    public Map getNormalMapByID (short _mapID)
    {
        return mapDictionary.get(_mapID);
    }

    /**
     * 根据地图名称获取地图
     * 
     * @param _mapName 地图名
     * @return
     */
    public Map getNormalMapByName (String _mapName)
    {
        Iterator<Map> mapIterator = mapDictionary.values().iterator();
        Map map;

        while (mapIterator.hasNext())
        {
            map = mapIterator.next();

            if (map.getName().equals(_mapName))
                return map;
        }

        return null;
    }

    /**
     * 获取地图元素图片字节
     * 
     * @param _elementImageID
     * @return
     */
    public byte[] getElementImageBytes (short _elementImageID)
    {
        return mapElementImageTable.get(_elementImageID);
    }

    private void loadMapElementImage (String _imageFilePath)
    {
        File[] imageFiles = new File(_imageFilePath).listFiles();

        for (int i = 0; i < imageFiles.length; i++)
        {
            short imageID = -1;

            if (imageFiles[i].getName().toLowerCase().endsWith(".png"))
            {
                String imageFileName = imageFiles[i].getName();
                imageID = Short.parseShort(imageFileName.substring(0,
                        imageFileName.length() - 4));

                mapElementImageTable.put(imageID, getImageBytes(imageFiles[i]));
            }
        }

        imageFiles = null;
    }

    private byte[] getImageBytes (File _imageFile)
    {
        byte[] rtnValue = null;

        try
        {
            DataInputStream dis = null;

            dis = new DataInputStream(new FileInputStream(_imageFile));
            int imgFileByteSize = dis.available();
            rtnValue = new byte[imgFileByteSize];

            for (int pos = 0; (pos = dis.read(rtnValue, pos, imgFileByteSize
                    - pos)) != -1;);

            dis.close();
        }
        catch (Exception e)
        {
            LogWriter.error(this, e);
        }

        return rtnValue;
    }

    /**
     * 因为现在跳转到副本地图和跳转到普通地图一样
     * 现在改为加载所有地图
     */
    private void loadNormalMap ()
    {
        ArrayList<MapModelData> mapModelDataList = MapModelDataDict
                .getInstance().getMapModelDataList();

        for (MapModelData mapModelData : mapModelDataList)
        {
//            if (mapModelData.mapTypeValue == EMapType.GENERIC.getTypeValue())
//            {
        	log.debug("start build map id = "+mapModelData.id);
                Map map = buildMap(mapModelData);

                if (map == null)
                {
                    log.debug("加载地图数据失败：" + mapModelData.id + "   "
                            + mapModelData.name);
                } else {
                    log.debug("加载地图数据成功：" + mapModelData.id + "   "
                            + mapModelData.name);
				}

                if (null != map)
                {
                    mapDictionary.put(map.getID(), map);
                }
//            }
        }

        for (Map map : mapDictionary.values()){
            log.debug("#### mapid="+map.getID()+",name["+map.getName()+"] transformData2 size="+map.transformData2.length);
        }
    }

    /**
     * 根据地图模板数据创建地图
     * 
     * @param _mapModelData
     * @return
     */
    private Map buildMap (MapModelData _mapModelData)
    {
        Map map = new Map(_mapModelData.id, _mapModelData.name);

        try
        {
            map.setBornX(_mapModelData.bornX);
            map.setBornY(_mapModelData.bornY);
            map.setHeight(_mapModelData.height);
            map.setWidth(_mapModelData.width);
            map.setModifiable(_mapModelData.modifiable);
            map.setPKMark(_mapModelData.pkMark);
            map.setTileID(_mapModelData.tileImageID);
            map.setMapType(EMapType.getMapType(_mapModelData.mapTypeValue));
            map.setMonsterModelIDAbout(_mapModelData.monsterModelIDAbout);
            map.setWeather(EMapWeather.get(_mapModelData.mapWeatherValue));

            short[] relation = MapRelationDict.getInstance()
                    .getRelationByMapID(map.getID());

            if (null != relation)
            {
                map.setMiniImageID(relation[1]);
                map.setTargetMapIDAfterDie(relation[2]);
                map.setTargetMapIDAfterUseGoods(relation[3]);
                //加上魔族返回的地图，当玩家是魔族时使用 -- add by jiaodj 2011-05-16
                log.debug("setMozuTargetMapIDAfterDie="+relation[8]+",setMozuTargetMapIDAfterUseGoods="+relation[9]);
                map.setMozuTargetMapIDAfterDie(relation[8]);
                map.setMozuTargetMapIDAfterUseGoods(relation[9]);

                if (0 != relation[4])
                {
                    if (0 != relation[5])
                    {
                        AreaDict.getInstance().getAreaByID(relation[4]).add(
                                map, true, relation[6], relation[7]);
                    }
                    else
                    {
                        AreaDict.getInstance().getAreaByID(relation[4]).add(
                                map, false, 0, 0);
                    }
                }
            }
            else
            {
                LogWriter.println("找不到地图关系:" + map.getName());
            }

            map.bottomCanvasData = _mapModelData.bottomCanvasData;
            map.resourceCanvasMap = _mapModelData.resourceCanvasMap;
            map.elementCanvasData = _mapModelData.elementCanvasData;
            map.elementImageIDList = _mapModelData.elementImageIDList;
            map.fixedNpcImageIDList = _mapModelData.fixedNpcImageIDList;
            map.fixedMonsterImageIDList = _mapModelData.fixedMonsterImageIDList;
            map.groundTaskGoodsImageIDList = _mapModelData.groundTaskGoodsImageIDList;
            map.taskGearImageIDList = _mapModelData.taskGearImageIDList;
            map.cartoonList = _mapModelData.cartoonList;
            map.doorList = _mapModelData.externalPortList;
            
            if(Map.IS_NEW_MAP){//新地图
                map.transformData1 = _mapModelData.transformData1;
                map.resourceTransformData = _mapModelData.resourceTransformData;
                map.transformData2 = _mapModelData.transformData2;
                map.decoraterList = _mapModelData.decorateObjectList;
                log.debug("build mapid="+map.getID()+",["+map.getName()+"],map.transformData2="+map.transformData2.length+",_mapModelData.transformData2="+_mapModelData.transformData2);
            }
            log.debug("map door List size = " + map.doorList.length);
            for (Door switchPoint : map.doorList)
            {
                log.debug("switchPoint mapID="+switchPoint.targetMapID);
                switchPoint.targetMapName = MapModelDataDict.getInstance()
                        .getMapModelData(switchPoint.targetMapID).name;
            }

            map.internalTransportList = _mapModelData.internalPorts;
            map.popMessageList = _mapModelData.popMessageList;
            map.unpassData = _mapModelData.unpassData;
            map.unpassMarkArray = _mapModelData.unpassMarkArray;

            if (_mapModelData.notPlayerObjectList.length > 0)
            {
            	if (_mapModelData.notPlayerObjectList.length > 500) 
            	{
					log.warn("加载notPlayerObjectList.length=" 
							+ _mapModelData.notPlayerObjectList.length);
				}
//            	log.info("notPlayerObjectList.length = " + _mapModelData.notPlayerObjectList.length);
                for (OtherObjectData npcData : _mapModelData.notPlayerObjectList)
                {
                	log.debug("npcData.modelID  = " + npcData.modelID);
                    if (npcData.modelID
                            .startsWith(NotPlayerServiceImpl.NPC_MODEL_ID_PREFIX))
                    {
                        Npc npc = NotPlayerServiceImpl.getInstance()
                                .buildNpcInstance(npcData.modelID);

                        if (null != npc)
                        {
                            npc.live(map);
                            npc.setOrgMap(map);
                            npc.setOrgX(npcData.x);
                            npc.setOrgY(npcData.y);
                            npc.setCellX(npcData.x);
                            npc.setCellY(npcData.y);
                            //del by zhengl ; date: 2010-11-18 ; note: npcData.animationID弃用.
//                          if(Map.IS_NEW_MAP){
//                          	npc.setAnimationID(npcData.animationID);//npc动画id
//                          }
                          //end

                            npc.active();

                            map.getNpcList().add(npc);
                        }
                        else
                        {
                            LogWriter.println("未能加载NPC：" + npcData.modelID
                                    + "---" + map.getName());
                            log.debug("未能加载NPC：" + npcData.modelID
                                    + "---" + map.getName());;
                        }
                    }
                    else if (npcData.modelID
                            .startsWith(NotPlayerServiceImpl.MONSTER_MODEL_ID_PREFIX))
                    {
                    	log.debug("开始加载怪物");
                        Monster monster = NotPlayerServiceImpl.getInstance()
                                .buildMonsterInstance(npcData.modelID);
                        if (null != monster)
                        {
                            monster.live(map);
                            monster.setOrgMap(map);
                            monster.setOrgX(npcData.x);
                            monster.setOrgY(npcData.y);
                            monster.setCellX(npcData.x);
                            monster.setCellY(npcData.y);
                            
                            if(Map.IS_NEW_MAP){
                            	monster.setOrgZ(npcData.z);
                            	monster.setCellZ(npcData.z);
                            }
                            monster.active(npcData.movePath);
                            map.getMonsterList().add(monster);
                            log.debug("已加载怪物 : = " + npcData.modelID);
                        }
                        else
                        {
                            LogWriter.println("未能加载怪物：" + npcData.modelID
                                    + "---" + map.getName());
                            log.debug("未能加载怪物：" + npcData.modelID
                                    + "---" + map.getName());
                        }
                    }
                    else if (npcData.modelID
                            .startsWith(NotPlayerServiceImpl.ANIMAL_MODEL_ID_PREFIX))
                    {
                    	log.debug("开始加载小动物....");
                        Animal animal = NotPlayerServiceImpl.getInstance()
                                .buildAnimalInstance(npcData.modelID);

                        if (null != animal)
                        {
                            animal.live(map);
                            animal.setOrgX(npcData.x);
                            animal.setOrgY(npcData.y);
                            animal.setCellX(npcData.x);
                            animal.setCellY(npcData.y);

                            map.getAnimalList().add(animal);
                            log.debug("已加载小动物 id= " + npcData.modelID);
                        }
                        else
                        {
                            LogWriter.println("未能加载小动物：" + npcData.modelID
                                    + "---" + map.getName());
                            log.debug("未能加载小动物：" + npcData.modelID
                                    + "---" + map.getName());

                        }
                    }
                    else if (npcData.modelID
                            .startsWith(NotPlayerServiceImpl.ROAD_PLATE_MODEL_ID_PREFIX))
                    {
                        RoadInstructPlate roadPlate = NotPlayerServiceImpl
                                .getInstance().buildRoadPlate(npcData.modelID);

                        if (null != roadPlate)
                        {
                            roadPlate.setCellX(npcData.x);
                            roadPlate.setCellY(npcData.y);

                            map.getRoadPlateList().add(roadPlate);
                        }
                        else
                        {
                            LogWriter.println("未能加载路牌：" + npcData.modelID
                                    + "---" + map.getName());

                        }
                    }
                    else if (npcData.modelID
                            .startsWith(NotPlayerServiceImpl.DOOR_PLATE_MODEL_ID_PREFIX))
                    {
                        DoorPlate doorPlate = NotPlayerServiceImpl
                                .getInstance().buildDoorPlate(npcData.modelID);

                        if (null != doorPlate)
                        {
                            doorPlate.setCellX(npcData.x);
                            doorPlate.setCellY(npcData.y);

                            map.getDoorPlateList().add(doorPlate);
                        }
                        else
                        {
                            log.error("未能加载室内门牌：" + npcData.modelID
                                    + "---" + map.getName());

                        }
                    }
                    else if (npcData.modelID
                            .startsWith(NotPlayerServiceImpl.GEAR_MODEL_ID_PREFIX))
                    {
                        TaskGear gear = NotPlayerServiceImpl.getInstance()
                                .buildGearInstance(npcData.modelID);
                        log.debug("gear = " + gear);
                        if (null != gear)
                        {
                            gear.setCellX(npcData.x);
                            gear.setCellY(npcData.y);

                            map.getTaskGearList().add(gear);
                        }
                        else
                        {
                            log.error("未能加载任务机关：" + npcData.modelID
                                    + "---" + map.getName());

                        }
                    }
                    else if (npcData.modelID
                            .startsWith(NotPlayerServiceImpl.BOX_MODEL_ID_PREFIX))
                    {
                        Box box = map.existBox(npcData.modelID);

                        if (null == box)
                        {
                            box = NotPlayerServiceImpl.getInstance()
                                    .buildBoxInstance(npcData.modelID);

                            if (null != box)
                            {
                                box.addRandomLocation(npcData.x, npcData.y);
                                box.live(map);

                                map.getBoxList().add(box);
                            }
                            else
                            {
                                LogWriter.println("未能加载宝箱：" + npcData.modelID
                                        + "---" + map.getName());
                            }
                        }
                        else
                        {
                            box.addRandomLocation(npcData.x, npcData.y);
                        }
                    }
                    else if (npcData.modelID
                            .startsWith(NotPlayerServiceImpl.GROUND_TASK_GOODS_MODEL_ID_PREFIX))
                    {
                        GroundTaskGoods taskGoods = NotPlayerServiceImpl
                                .getInstance().buildGroundTaskGood(
                                        npcData.modelID);

                        if (null != taskGoods)
                        {
                            taskGoods.setCellX(npcData.x);
                            taskGoods.setCellY(npcData.y);
                            taskGoods.live(map);

                            map.getGroundTaskGoodsList().add(taskGoods);
                        }
                        else
                        {
                            LogWriter.println("未能加载地图上的任务物品：" + npcData.modelID
                                    + "---" + map.getName());

                        }
                    }
                }
//                log.info("加载了"+map.getGroundTaskGoodsList().size()+"个任务物品");
            }

            map.refreshBox();

            return map;
        }
        catch (Exception e)
        {
            log.error("build map error : ", e);
            return null;
        }
    }

    /**
     * 还原副本地图
     * 
     * @param _mapModelData
     * @return
     */
    public Map buildDungeonMap (byte _pattern, MapModelData _mapModelData,
            String _dungeonName, boolean _monsterAboutExsits)
    {
        Map map = new Map(_mapModelData.id, _dungeonName
                + Dungeon.DUNGEON_MAP_NAME_SEPARATE_CHAR + _mapModelData.name);

        try
        {
            map.setBornX(_mapModelData.bornX);
            map.setBornY(_mapModelData.bornY);
            map.setHeight(_mapModelData.height);
            map.setWidth(_mapModelData.width);
            map.setModifiable(_mapModelData.modifiable);
            map.setPKMark(_mapModelData.pkMark);
            map.setTileID(_mapModelData.tileImageID);
            map.setMapType(EMapType.getMapType(_mapModelData.mapTypeValue));
            map.setMonsterModelIDAbout(_mapModelData.monsterModelIDAbout);
            map.setWeather(EMapWeather.get(_mapModelData.mapWeatherValue));

            short[] relation = MapRelationDict.getInstance()
                    .getRelationByMapID(map.getID());

            if (null != relation)
            {
                map.setMiniImageID(relation[1]);
                map.setTargetMapIDAfterDie(relation[2]);
                map.setTargetMapIDAfterUseGoods(relation[3]);
                //加上魔族返回的地图，当玩家是魔族时使用 -- add by jiaodj 2011-05-16
                map.setMozuTargetMapIDAfterDie(relation[8]);
                map.setMozuTargetMapIDAfterUseGoods(relation[9]);

                if (0 != relation[4])
                {
                    if (0 != relation[5])
                    {
                        AreaDict.getInstance().getAreaByID(relation[4]).add(
                                map, true, relation[6], relation[7]);
                    }
                    else
                    {
                        AreaDict.getInstance().getAreaByID(relation[4]).add(
                                map, false, 0, 0);
                    }
                }
            }

            map.bottomCanvasData = _mapModelData.bottomCanvasData;
            map.resourceCanvasMap = _mapModelData.resourceCanvasMap;
            map.elementCanvasData = _mapModelData.elementCanvasData;
            map.elementImageIDList = _mapModelData.elementImageIDList;
            map.fixedNpcImageIDList = _mapModelData.fixedNpcImageIDList;
            map.fixedMonsterImageIDList = _mapModelData.fixedMonsterImageIDList;
            map.groundTaskGoodsImageIDList = _mapModelData.groundTaskGoodsImageIDList;
            map.taskGearImageIDList = _mapModelData.taskGearImageIDList;
            map.cartoonList = _mapModelData.cartoonList;
            map.doorList = _mapModelData.externalPortList.clone();

            map.internalTransportList = _mapModelData.internalPorts;
            map.popMessageList = _mapModelData.popMessageList;
            map.unpassData = _mapModelData.unpassData;
            map.unpassMarkArray = _mapModelData.unpassMarkArray;
            
            if(Map.IS_NEW_MAP){//新地图
                map.transformData1 = _mapModelData.transformData1;
                map.transformData2 = _mapModelData.transformData2; //旋转层赋值  add by jiaodj 2011-04-25
                map.decoraterList = _mapModelData.decorateObjectList;
                map.resourceTransformData = _mapModelData.resourceTransformData;
            }

            if (_mapModelData.notPlayerObjectList.length > 0)
            {
                for (OtherObjectData npcData : _mapModelData.notPlayerObjectList)
                {
                    if (npcData.modelID
                            .startsWith(NotPlayerServiceImpl.NPC_MODEL_ID_PREFIX))
                    {
                        Npc npc = NotPlayerServiceImpl.getInstance()
                                .buildNpcInstance(npcData.modelID);

                        if (null != npc)
                        {
                            npc.live(map);
                            npc.setOrgMap(map);
                            npc.setOrgX(npcData.x);
                            npc.setOrgY(npcData.y);
                            npc.setCellX(npcData.x);
                            npc.setCellY(npcData.y);
                            //del by zhengl ; date: 2010-11-18 ; note: npcData.animationID弃用.
//                            if(Map.IS_NEW_MAP){
//                            	npc.setAnimationID(npcData.animationID);//npc动画id
//                            }
                            //end

                            npc.active();

                            map.getNpcList().add(npc);
                        }
                        else
                        {
                            LogWriter.println("未能加载NPC：" + npcData.modelID
                                    + "---" + map.getName());
                        }
                    }
                    else if (npcData.modelID
                            .startsWith(NotPlayerServiceImpl.MONSTER_MODEL_ID_PREFIX))
                    {
                        if (null == map.getMonsterModelIDAbout()
                                && _monsterAboutExsits
                                || (null != map.getMonsterModelIDAbout() && _monsterAboutExsits))
                        {
                            Monster monster = null;

                            if (Dungeon.PATTERN_OF_DIFFICULT == _pattern)
                            {
                                monster = NotPlayerServiceImpl
                                        .getInstance()
                                        .buildMonsterInstance(
                                                npcData.modelID
                                                        + Dungeon.SUFFIX_OF_HERO_MONSTER_ID);
                            }
                            else
                            {
                                monster = NotPlayerServiceImpl.getInstance()
                                        .buildMonsterInstance(npcData.modelID);
                            }

                            if (null != monster)
                            {
                                monster.live(map);
                                monster.setOrgMap(map);
                                monster.setOrgX(npcData.x);
                                monster.setOrgY(npcData.y);
                                monster.setCellX(npcData.x);
                                monster.setCellY(npcData.y);

                                monster.active(npcData.movePath);
                                map.getMonsterList().add(monster);
                            }
                            else
                            {
                                LogWriter.println("未能加载怪物：" + npcData.modelID
                                        + "---" + map.getName());

                            }
                        }
                    }
                    else if (npcData.modelID
                            .startsWith(NotPlayerServiceImpl.ANIMAL_MODEL_ID_PREFIX))
                    {
                        Animal animal = NotPlayerServiceImpl.getInstance()
                                .buildAnimalInstance(npcData.modelID);

                        if (null != animal)
                        {
                            animal.live(map);
                            animal.setOrgX(npcData.x);
                            animal.setOrgY(npcData.y);
                            animal.setCellX(npcData.x);
                            animal.setCellY(npcData.y);

                            map.getAnimalList().add(animal);
                        }
                        else
                        {
                            LogWriter.println("未能加载小动物：" + npcData.modelID
                                    + "---" + map.getName());

                        }
                    }
                    else if (npcData.modelID
                            .startsWith(NotPlayerServiceImpl.ROAD_PLATE_MODEL_ID_PREFIX))
                    {
                        RoadInstructPlate roadPlate = NotPlayerServiceImpl
                                .getInstance().buildRoadPlate(npcData.modelID);

                        if (null != roadPlate)
                        {
                            roadPlate.setCellX(npcData.x);
                            roadPlate.setCellY(npcData.y);

                            map.getRoadPlateList().add(roadPlate);
                        }
                        else
                        {
                            LogWriter.println("未能加载路牌：" + npcData.modelID
                                    + "---" + map.getName());

                        }
                    }
                    else if (npcData.modelID
                            .startsWith(NotPlayerServiceImpl.DOOR_PLATE_MODEL_ID_PREFIX))
                    {
                        DoorPlate doorPlate = NotPlayerServiceImpl
                                .getInstance().buildDoorPlate(npcData.modelID);

                        if (null != doorPlate)
                        {
                            doorPlate.setCellX(npcData.x);
                            doorPlate.setCellY(npcData.y);

                            map.getDoorPlateList().add(doorPlate);
                        }
                        else
                        {
                            LogWriter.println("未能加载室内门牌：" + npcData.modelID
                                    + "---" + map.getName());

                        }
                    }
                    else if (npcData.modelID
                            .startsWith(NotPlayerServiceImpl.GEAR_MODEL_ID_PREFIX))
                    {
                        TaskGear gear = NotPlayerServiceImpl.getInstance()
                                .buildGearInstance(npcData.modelID);

                        if (null != gear)
                        {
                            gear.setCellX(npcData.x);
                            gear.setCellY(npcData.y);

                            map.getTaskGearList().add(gear);
                        }
                        else
                        {
                            LogWriter.println("未能加载任务机关：" + npcData.modelID
                                    + "---" + map.getName());

                        }
                    }
                    else if (npcData.modelID
                            .startsWith(NotPlayerServiceImpl.BOX_MODEL_ID_PREFIX))
                    {
                        if (null == map.getMonsterModelIDAbout()
                                && _monsterAboutExsits
                                || (null != map.getMonsterModelIDAbout() && _monsterAboutExsits))
                        {
                            Box box = map.existBox(npcData.modelID);

                            if (null == box)
                            {
                                box = NotPlayerServiceImpl.getInstance()
                                        .buildBoxInstance(npcData.modelID);

                                if (null != box)
                                {
                                    box.addRandomLocation(npcData.x, npcData.y);
                                    box.live(map);

                                    map.getBoxList().add(box);
                                }
                                else
                                {
                                    LogWriter.println("未能加载宝箱："
                                            + npcData.modelID + "---"
                                            + map.getName());

                                }
                            }
                            else
                            {
                                box.addRandomLocation(npcData.x, npcData.y);
                            }
                        }
                    }
                    else if (npcData.modelID
                            .startsWith(NotPlayerServiceImpl.GROUND_TASK_GOODS_MODEL_ID_PREFIX))
                    {
                        GroundTaskGoods taskGoods = NotPlayerServiceImpl
                                .getInstance().buildGroundTaskGood(
                                        npcData.modelID);

                        if (null != taskGoods)
                        {
                            taskGoods.setCellX(npcData.x);
                            taskGoods.setCellY(npcData.y);
                            taskGoods.live(map);

                            map.getGroundTaskGoodsList().add(taskGoods);
                        }
                        else
                        {
                            LogWriter.println("未能加载地图上的任务物品：" + npcData.modelID
                                    + "---" + map.getName());
                        }
                    }
                }
//                log.info(map.getID()+"副本加载了"+map.getGroundTaskGoodsList().size()+"个任务物品");
            }

            for (Door door : map.doorList)
            {
                door.targetMapName = MapModelDataDict.getInstance()
                        .getMapModelData(door.targetMapID).name;

                if (null != door.monsterIDAbout)
                {
                    for (ME2GameObject monster : map.getMonsterList())
                    {
                        if (-1 != ((Monster) monster).getModelID().indexOf(
                                door.monsterIDAbout))
                        {
                            door.visible = false;

                            break;
                        }
                    }
                }
            }

            map.refreshBox();

            return map;
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return null;
        }
    }

    /**
     * 获得圆形范围内的所有玩家，无视隐身、阵营(队列的顺序是按照以圆点的距离由近到远)
     * 
     * @param _cellX
     * @param _cellY
     * @param _radio
     * @return
     */
    public final ArrayList<HeroPlayer> getAllPlayerListInCircle (Map _map,
            int _cellX, int _cellY, int _radius)
    {
    	if(_map == null) {
//    		log.debug("mapserviceimli -- 889 line, map is null");
            return null;
    	}
        ME2ObjectList mapPlayerList = _map.getPlayerList();
        ArrayList<HeroPlayer> rangePlayerList = new ArrayList<HeroPlayer>();

        for (ME2GameObject player : mapPlayerList)
        {
            boolean radius = (_cellX - player.getCellX()) * (_cellX - player.getCellX())
                    + (_cellY - player.getCellY())*(_cellY - player.getCellY()) <= _radius*_radius;
            /*if (player.isEnable()
                    && !player.isDead()
                    && Math.sqrt(Math.pow(_cellX - player.getCellX(), 2)
                            + Math.pow(_cellY - player.getCellY(), 2)) <= _radius)*/
            if(player.isEnable()
                    && !player.isDead()
                    && radius)
            {
                int i = 0;

                for (; i < rangePlayerList.size(); i++)
                {
                    boolean isrange = (_cellX - player.getCellX())*(_cellX - player.getCellX()) + (_cellY - player.getCellY())*(_cellY - player.getCellY())
                                   <= (_cellX - rangePlayerList.get(i).getCellX())*(_cellX - rangePlayerList.get(i).getCellX()) + (_cellY - rangePlayerList.get(i).getCellY())*(_cellY - rangePlayerList.get(i).getCellY());

                    /*if (Math.sqrt(Math.pow(_cellX - player.getCellX(), 2)
                            + Math.pow(_cellY - player.getCellY(), 2)) <= Math
                            .sqrt(Math.pow(_cellX
                                    - rangePlayerList.get(i).getCellX(), 2)
                                    + Math.pow(
                                            _cellY
                                                    - rangePlayerList.get(i)
                                                            .getCellY(), 2)))*/
                    if(isrange)
                    {
                        break;
                    }
                }

                rangePlayerList.add(i, (HeroPlayer) player);
            }
        }

        return rangePlayerList;
    }

    /**
     * 获得圆形范围内的玩家(队列的顺序是按照以圆点的距离由近到远)
     * 
     * @param _cellX
     * @param _cellY
     * @param _radio
     * @return
     */
    public final ArrayList<HeroPlayer> getMonsterValidateTargetListInCircle (
            Map _map, Monster _monster, int _radius)
    {
        if (_map.getPlayerList().size() == 0)
            return null;

        ArrayList<HeroPlayer> rangePlayerList = new ArrayList<HeroPlayer>();
        ME2GameObject player;

        for (int j = 0; j < _map.getPlayerList().size(); j++)
        {
            try
            {
                player = _map.getPlayerList().get(j);
            }
            catch (Exception e)
            {
                return null;
            }

            boolean radius = (_monster.getCellX()-player.getCellX()) * (_monster.getCellX()-player.getCellX())
                                + (_monster.getCellY() - player.getCellY())*(_monster.getCellY() - player.getCellY()) <= _radius*_radius;

            /*if (player.isEnable()
                    && !player.isDead()
                    & player.isVisible()
                    && player.getClan() != _monster.getClan()
                    && Math.sqrt(Math.pow(_monster.getCellX()
                            - player.getCellX(), 2)
                            + Math.pow(_monster.getCellY() - player.getCellY(),
                                    2)) <= _radius)*/
            if(player.isEnable() && !player.isDead() && player.isVisible() && player.getClan() != _monster.getClan() && radius)
            {
                int i = 0;

                for (; i < rangePlayerList.size(); i++)
                {
                    boolean isrange = (_monster.getCellX()-player.getCellX())*(_monster.getCellX()-player.getCellX())
                                        + (_monster.getCellY()-player.getCellY())*(_monster.getCellY()-player.getCellY())
                                        <= (_monster.getCellX()-rangePlayerList.get(i).getCellX())*(_monster.getCellX()-rangePlayerList.get(i).getCellX())
                                            + (_monster.getCellY()-rangePlayerList.get(i).getCellY()) * (_monster.getCellY()-rangePlayerList.get(i).getCellY());
                    /*if (Math.sqrt(Math.pow(_monster.getCellX()
                            - player.getCellX(), 2)
                            + Math.pow(_monster.getCellY() - player.getCellY(),
                                    2)) <= Math.sqrt(Math.pow(_monster
                            .getCellX()
                            - rangePlayerList.get(i).getCellX(), 2)
                            + Math.pow(_monster.getCellY()
                                    - rangePlayerList.get(i).getCellY(), 2)))*/
                    if(isrange)
                    {
                        break;
                    }
                }

                rangePlayerList.add(i, (HeroPlayer) player);
            }
        }

        return rangePlayerList.size() == 0 ? null : rangePlayerList;
    }

    /**
     * 获取前方矩形内同阵营对象列表
     * 
     * @param _width
     * @param _length
     * @param _host
     * @param _numsLimit
     * @return
     */
    public final ArrayList<ME2GameObject> getFriendsObjectListInForeRange (
            Map _map, int _width, int _length, ME2GameObject _host,
            int _numsLimit)
    {
        ArrayList<ME2GameObject> rangeObjectList = new ArrayList<ME2GameObject>();

        int xRadio = _width / 2;
        int leftX, rightX, upY, downY;

        switch (_host.getDirection())
        {
            case Direction.UP:
            {
                leftX = _host.getCellX() - xRadio;
                rightX = _host.getCellX() + xRadio;
                upY = _host.getCellY() - _length;
                downY = _host.getCellY() - 1;

                break;
            }
            case Direction.DOWN:
            {
                leftX = _host.getCellX() - xRadio;
                rightX = _host.getCellX() + xRadio;
                upY = _host.getCellY() + 1;
                downY = _host.getCellY() + _length;

                break;
            }
            case Direction.LEFT:
            {
                leftX = _host.getCellX() - _length;
                rightX = _host.getCellX() - 1;
                upY = _host.getCellY() - xRadio;
                downY = _host.getCellY() + xRadio;

                break;
            }
            case Direction.RIGHT:
            {
                leftX = _host.getCellX() + 1;
                rightX = _host.getCellX() + _length;
                upY = _host.getCellY() - xRadio;
                downY = _host.getCellY() + xRadio;

                break;
            }
            default:
            {
                return null;
            }
        }

        if (_host.getObjectType() == EObjectType.PLAYER)
        {
            HeroPlayer player = null;
            ME2ObjectList mapPlayerList = _map.getPlayerList();

            for (int i = 0; i < mapPlayerList.size(); i++)
            {
                player = (HeroPlayer) mapPlayerList.get(i);

                if (player.isEnable() && !player.isDead()
                        && _host.getClan() == player.getClan()
                        && player.getCellX() >= leftX
                        && player.getCellX() <= rightX
                        && player.getCellY() >= upY
                        && player.getCellY() <= downY)
                {
                    rangeObjectList.add(player);

                    if (_numsLimit != -1
                            && rangeObjectList.size() == _numsLimit)
                    {
                        return rangeObjectList;
                    }
                }
            }
        }
        else if (_host.getObjectType() == EObjectType.MONSTER)
        {
            ME2ObjectList mapMonsterList = _map.getMonsterList();
            Monster monster = null;

            for (int i = 0; i < mapMonsterList.size(); i++)
            {
                monster = (Monster) mapMonsterList.get(i);

                if (_host.getID() != monster.getID()
                        && monster.getCellX() >= leftX
                        && monster.getCellX() <= rightX
                        && monster.getCellY() >= upY
                        && monster.getCellY() <= downY)
                {
                    rangeObjectList.add(monster);

                    if (_numsLimit != -1
                            && rangeObjectList.size() == _numsLimit)
                    {
                        return rangeObjectList;
                    }
                }
            }
        }

        return rangeObjectList;
    }

    /**
     * 获取前方矩形内同阵营对象列表
     * 
     * @param _player
     * @param _width
     * @param _length
     * @param _numsLimit
     * @return
     */
    public final ArrayList<HeroPlayer> getFriendsPlayerListInForeRange (
            HeroPlayer _player, int _width, int _length, int _numsLimit)
    {
        ArrayList<HeroPlayer> rangePlayerList = new ArrayList<HeroPlayer>();

        int xRadio = _width / 2;
        int leftX, rightX, upY, downY;

        switch (_player.getDirection())
        {
            case Direction.UP:
            {
                leftX = _player.getCellX() - xRadio;
                rightX = _player.getCellX() + xRadio;
                upY = _player.getCellY() - _length;
                downY = _player.getCellY() - 1;

                break;
            }
            case Direction.DOWN:
            {
                leftX = _player.getCellX() - xRadio;
                rightX = _player.getCellX() + xRadio;
                upY = _player.getCellY() + 1;
                downY = _player.getCellY() + _length;

                break;
            }
            case Direction.LEFT:
            {
                leftX = _player.getCellX() - _length;
                rightX = _player.getCellX() - 1;
                upY = _player.getCellY() - xRadio;
                downY = _player.getCellY() + xRadio;

                break;
            }
            case Direction.RIGHT:
            {
                leftX = _player.getCellX() + 1;
                rightX = _player.getCellX() + _length;
                upY = _player.getCellY() - xRadio;
                downY = _player.getCellY() + xRadio;

                break;
            }
            default:
            {
                return null;
            }
        }

        HeroPlayer player = null;
        ME2ObjectList mapPlayerList = _player.where().getPlayerList();

        for (int i = 0; i < mapPlayerList.size(); i++)
        {
            player = (HeroPlayer) mapPlayerList.get(i);

            if (player.isEnable() && !player.isDead()
                    && _player.getClan() == player.getClan()
                    && player.getCellX() >= leftX
                    && player.getCellX() <= rightX && player.getCellY() >= upY
                    && player.getCellY() <= downY)
            {
                rangePlayerList.add(player);

                if (_numsLimit != -1 && rangePlayerList.size() == _numsLimit)
                {
                    return rangePlayerList;
                }
            }
        }

        return rangePlayerList;
    }

    /**
     * 获取攻击者前方横向和纵向矩形范围内的可攻击对象，包括敌方玩家和怪物
     * 
     * @param _width 横向范围（格子数，单数）
     * @param _length 纵向范围（格子数）
     * @param _attacker 攻击者
     * @param _numsLimit 目标数量限制，-1为无限制
     * @return
     */
    public final ArrayList<ME2GameObject> getAttackableObjectListInForeRange (
            Map _map, int _width, int _length, ME2GameObject _attacker,
            int _numsLimit)
    {
        ArrayList<ME2GameObject> rangeObjectList = new ArrayList<ME2GameObject>();

        int xRadio = _width / 2;
        int leftX, rightX, upY, downY;

        switch (_attacker.getDirection())
        {
            case Direction.UP:
            {
                leftX = _attacker.getCellX() - xRadio;
                rightX = _attacker.getCellX() + xRadio;
                upY = _attacker.getCellY() - _length;
                downY = _attacker.getCellY() + 1;

                break;
            }
            case Direction.DOWN:
            {
                leftX = _attacker.getCellX() - xRadio;
                rightX = _attacker.getCellX() + xRadio;
                upY = _attacker.getCellY() + 1;
                downY = _attacker.getCellY() + _length;

                break;
            }
            case Direction.LEFT:
            {
                leftX = _attacker.getCellX() - _length;
                rightX = _attacker.getCellX() - 1;
                upY = _attacker.getCellY() - xRadio;
                downY = _attacker.getCellY() + xRadio;

                break;
            }
            case Direction.RIGHT:
            {
                leftX = _attacker.getCellX() + 1;
                rightX = _attacker.getCellX() + _length;
                upY = _attacker.getCellY() - xRadio;
                downY = _attacker.getCellY() + xRadio;

                break;
            }
            default:
            {
                return null;
            }
        }

        if (_attacker.getObjectType() == EObjectType.PLAYER)
        {
            ME2ObjectList mapPlayerList = _map.getPlayerList();
            HeroPlayer player = null;

            for (int i = 0; i < mapPlayerList.size(); i++)
            {
                player = (HeroPlayer) mapPlayerList.get(i);

                if (player.isEnable()
                        && !player.isDead()
                        && (_attacker.getClan() != player.getClan() || ((HeroPlayer) _attacker)
                                .getDuelTargetUserID() == player.getUserID())
                        && player.getCellX() >= leftX
                        && player.getCellX() <= rightX
                        && player.getCellY() >= upY
                        && player.getCellY() <= downY)
                {
                    rangeObjectList.add(player);

                    if (_numsLimit != -1
                            && rangeObjectList.size() == _numsLimit)
                    {
                        return rangeObjectList;
                    }
                }
            }

            ME2ObjectList mapMonsterList = _map.getMonsterList();
            Monster monster = null;

            for (int i = 0; i < mapMonsterList.size(); i++)
            {
                monster = (Monster) mapMonsterList.get(i);

                if (monster.isVisible()
                        && _attacker.getClan() != monster.getClan()
                        && monster.getCellX() >= leftX
                        && monster.getCellX() <= rightX
                        && monster.getCellY() >= upY
                        && monster.getCellY() <= downY)
                {
                    rangeObjectList.add(monster);

                    if (_numsLimit != -1
                            && rangeObjectList.size() == _numsLimit)
                    {
                        return rangeObjectList;
                    }
                }
            }

        }
        else if (_attacker.getObjectType() == EObjectType.MONSTER)
        {
            ME2ObjectList mapPlayerList = _map.getPlayerList();
            HeroPlayer player = null;

            for (int i = 0; i < mapPlayerList.size(); i++)
            {
                player = (HeroPlayer) mapPlayerList.get(i);

                if (player.isEnable() && !player.isDead()
                        && _attacker.getClan() != player.getClan()
                        && player.getCellX() >= leftX
                        && player.getCellX() <= rightX
                        && player.getCellY() >= upY
                        && player.getCellY() >= downY)
                {
                    rangeObjectList.add(player);

                    if (_numsLimit != -1
                            && rangeObjectList.size() == _numsLimit)
                    {
                        return rangeObjectList;
                    }
                }
            }
        }

        return rangeObjectList;
    }

    /**
     * 获取以某点为中心，范围内的可攻击对象，包括敌方玩家和怪物
     * 
     * @param _cellX
     * @param _cellY
     * @param _range
     * @param _attacker
     * @param _numsLimit 目标数量限制，-1为无限制
     * @return
     */
    public final ArrayList<ME2GameObject> getAttackableObjectListInRange (
            Map _map, int _cellX, int _cellY, int _range,
            ME2GameObject _attacker, int _numsLimit)
    {
        ArrayList<ME2GameObject> rangeObjectList = new ArrayList<ME2GameObject>();

        if (_attacker.getObjectType() == EObjectType.PLAYER)
        {
            ME2ObjectList mapPlayerList = _map.getPlayerList();
            HeroPlayer player = null;

            for (int i = 0; i < mapPlayerList.size(); i++)
            {
                player = (HeroPlayer) mapPlayerList.get(i);

                if (player.isEnable()
                        && !player.isDead()
                        && (_attacker.getClan() != player.getClan() || ((HeroPlayer) _attacker)
                                .getDuelTargetUserID() == player.getUserID())
                        && (Math.abs(player.getCellX() - _cellX) <= _range)
                        && (Math.abs(player.getCellY() - _cellY)) <= _range)
                {
                    rangeObjectList.add(player);

                    if (_numsLimit != -1
                            && rangeObjectList.size() == _numsLimit)
                    {
                        return rangeObjectList;
                    }
                }
            }

            ME2ObjectList mapMonsterList = _map.getMonsterList();
            Monster monster = null;

            for (int i = 0; i < mapMonsterList.size(); i++)
            {
                monster = (Monster) mapMonsterList.get(i);

                if (monster.isVisible()
                        && _attacker.getClan() != monster.getClan()
                        && (Math.abs(monster.getCellX() - _cellX) <= _range)
                        && (Math.abs(monster.getCellY() - _cellY)) <= _range)
                {
                    rangeObjectList.add(monster);

                    if (_numsLimit != -1
                            && rangeObjectList.size() == _numsLimit)
                    {
                        return rangeObjectList;
                    }
                }
            }
        }
        else if (_attacker.getObjectType() == EObjectType.MONSTER)
        {
            ME2ObjectList mapPlayerList = _map.getPlayerList();
            HeroPlayer player = null;

            for (int i = 0; i < mapPlayerList.size(); i++)
            {
                player = (HeroPlayer) mapPlayerList.get(i);

                if (player.isEnable() && !player.isDead()
                        && _attacker.getClan() != player.getClan()
                        && (Math.abs(player.getCellX() - _cellX) <= _range)
                        && (Math.abs(player.getCellY() - _cellY)) <= _range)
                {
                    rangeObjectList.add(player);

                    if (_numsLimit != -1
                            && rangeObjectList.size() == _numsLimit)
                    {
                        return rangeObjectList;
                    }
                }
            }
        }

        return rangeObjectList;
    }

    /**
     * 获得范围友方列表
     * 
     * @param _cellX
     * @param _cellY
     * @param _range
     * @param _host
     * @param _numsLimit
     * @return
     */
    public final ArrayList<ME2GameObject> getFriendsObjectInRange (Map _map,
            int _cellX, int _cellY, int _range, ME2GameObject _host,
            int _numsLimit)
    {
        ArrayList<ME2GameObject> rangeObjectList = new ArrayList<ME2GameObject>();

        if (_host.getObjectType() == EObjectType.PLAYER)
        {
            ME2ObjectList mapPlayerList = _map.getPlayerList();
            HeroPlayer player = null;

            for (int i = 0; i < mapPlayerList.size(); i++)
            {
                player = (HeroPlayer) mapPlayerList.get(i);

                if (player.isEnable() && !player.isDead()
                        && _host.getClan() == player.getClan()
                        && (Math.abs(player.getCellX() - _cellX) <= _range)
                        && (Math.abs(player.getCellY() - _cellY)) <= _range)
                {
                    rangeObjectList.add(player);

                    if (_numsLimit != -1
                            && rangeObjectList.size() == _numsLimit)
                    {
                        return rangeObjectList;
                    }
                }
            }
        }
        else if (_host.getObjectType() == EObjectType.MONSTER)
        {
            ME2ObjectList mapPlayerList = _map.getPlayerList();
            HeroPlayer player = null;

            for (int i = 0; i < mapPlayerList.size(); i++)
            {
                player = (HeroPlayer) mapPlayerList.get(i);

                if (player.isEnable() && !player.isDead()
                        && (Math.abs(player.getCellX() - _cellX) <= _range)
                        && (Math.abs(player.getCellY() - _cellY)) <= _range)
                {
                    rangeObjectList.add(player);

                    if (_numsLimit != -1
                            && rangeObjectList.size() == _numsLimit)
                    {
                        return rangeObjectList;
                    }
                }
            }
        }

        return rangeObjectList;
    }

    /**
     * 获得范围友方列表
     * 
     * @param _player
     * @param _cellX
     * @param _cellY
     * @param _range
     * @param _numsLimit
     * @return
     */
    public final ArrayList<HeroPlayer> getFriendsPlayerInRange (
            HeroPlayer _player, int _cellX, int _cellY, int _range,
            int _numsLimit)
    {
        ArrayList<HeroPlayer> rangePlayerList = new ArrayList<HeroPlayer>();

        ME2ObjectList mapPlayerList = _player.where().getPlayerList();
        HeroPlayer player = null;

        for (int i = 0; i < mapPlayerList.size(); i++)
        {
            player = (HeroPlayer) mapPlayerList.get(i);

            if (player.isEnable() && !player.isDead()
                    && _player.getClan() == player.getClan()
                    && (Math.abs(player.getCellX() - _cellX) <= _range)
                    && (Math.abs(player.getCellY() - _cellY)) <= _range)
            {
                rangePlayerList.add(player);

                if (_numsLimit != -1 && rangePlayerList.size() == _numsLimit)
                {
                    return rangePlayerList;
                }
            }
        }

        return rangePlayerList;
    }

    /**
     * 获得队伍中的范围玩家列表（正方形）
     * 
     * @param _player
     * @param _cellX 范围基准X坐标
     * @param _cellY 范围基准Y坐标
     * @param _range 正方形的宽
     * @param _numsLimit 数量上限
     * @return
     */
    public final ArrayList<HeroPlayer> getGroupPlayerInRange (
            HeroPlayer _player, int _cellX, int _cellY, int _range,
            int _numsLimit)
    {
        if (_player.getObjectType() == EObjectType.PLAYER
                && _player.getGroupID() > 0)
        {
            Map map = _player.where();

            ArrayList<HeroPlayer> playerList = new ArrayList<HeroPlayer>();
            ME2ObjectList mapPlayerList = map.getPlayerList();
            HeroPlayer player = null;

            for (int i = 0; i < mapPlayerList.size(); i++)
            {
                player = (HeroPlayer) mapPlayerList.get(i);

                if (player.getGroupID() == _player.getGroupID()
                        && _player.getClan() == player.getClan()
                        && player.isEnable() && !player.isDead()
                        && (Math.abs(player.getCellX() - _cellX) <= _range)
                        && (Math.abs(player.getCellY() - _cellY)) <= _range)
                {
                    playerList.add(player);

                    if (_numsLimit != -1 && playerList.size() == _numsLimit)
                    {
                        break;
                    }
                }
            }

            return playerList;
        }

        return null;
    }

    /**
     * 获取前方矩形内同队伍对象列表
     * 
     * @param _player
     * @param _cellX
     * @param _cellY
     * @param _width
     * @param _length
     * @param _numsLimit
     * @return
     */
    public final ArrayList<HeroPlayer> getGroupPlayerListInForeRange (
            HeroPlayer _player, int _width, int _length, int _numsLimit)
    {
        ArrayList<HeroPlayer> playerList = new ArrayList<HeroPlayer>();

        int xRadio = _width / 2;
        int leftX, rightX, upY, downY;

        switch (_player.getDirection())
        {
            case Direction.UP:
            {
                leftX = _player.getCellX() - xRadio;
                rightX = _player.getCellX() + xRadio;
                upY = _player.getCellY() - _length;
                downY = _player.getCellY() - 1;

                break;
            }
            case Direction.DOWN:
            {
                leftX = _player.getCellX() - xRadio;
                rightX = _player.getCellX() + xRadio;
                upY = _player.getCellY() + 1;
                downY = _player.getCellY() + _length;

                break;
            }
            case Direction.LEFT:
            {
                leftX = _player.getCellX() - _length;
                rightX = _player.getCellX() - 1;
                upY = _player.getCellY() - xRadio;
                downY = _player.getCellY() + xRadio;

                break;
            }
            case Direction.RIGHT:
            {
                leftX = _player.getCellX() + 1;
                rightX = _player.getCellX() + _length;
                upY = _player.getCellY() - xRadio;
                downY = _player.getCellY() + xRadio;

                break;
            }
            default:
            {
                return null;
            }
        }

        HeroPlayer player = null;
        ME2ObjectList mapPlayerList = _player.where().getPlayerList();

        for (int i = 0; i < mapPlayerList.size(); i++)
        {
            player = (HeroPlayer) mapPlayerList.get(i);

            if (player.getGroupID() == _player.getGroupID()
                    && _player.getClan() == player.getClan()
                    && player.isEnable() && !player.isDead()
                    && player.getCellX() >= leftX
                    && player.getCellX() <= rightX && player.getCellY() >= upY
                    && player.getCellY() <= downY)
            {
                playerList.add(player);

                if (_numsLimit != -1 && playerList.size() == _numsLimit)
                {
                    break;
                }
            }
        }

        return playerList;
    }

    /**
     * 获取玩家当前处于哪界
     * @param player
     * @return
     */
    public byte getPlayerMapWorldType(HeroPlayer player){
        short mapID = player.where().getID();
        Map map = getNormalMapByID(mapID);
        if(map.getMapType() == EMapType.DUNGEON){//如果当前地图是副本，则显示副本的入口地图
            /*Dungeon dungeon = DungeonServiceImpl.getInstance().getWhereDungeon(player.getUserID());
            if(dungeon != null){
                log.debug("玩家在副本地图使用世界地图，副本入口地图 entry mapid="+dungeon.getEntranceMap().getID());
                mapID = WorldMapDict.getInstance().getIncludeDungeonMapID(dungeon.getEntranceMap().getID());
                log.debug("玩家在副本地图使用世界地图，副本入口所在的地图 mapid="+mapID);

            }*/
            return 0;  //副本地图里不能使用世界地图 by jiaodj 2011-04-25
        }

        byte worldType = WorldMapDict.getInstance().getTypeWorldMapByMapID(mapID);  //当前所在地图所属的世界的类型 1:神龙界  2:魔龙界  3:仙界
        return worldType;
    }

    /**
     * 获取玩家要去的那一界
     * @param mapID 将要去的地图ID
     * @return
     */
    public byte getPlayerMapWorldType(short mapID,HeroPlayer player){
        Map map = getNormalMapByID(mapID);
        if(map.getMapType() == EMapType.DUNGEON){//如果当前地图是副本，则显示副本的入口地图
            Dungeon dungeon = DungeonServiceImpl.getInstance().getWhereDungeon(player.getUserID());
            if(dungeon != null){
                mapID = WorldMapDict.getInstance().getIncludeDungeonMapID(dungeon.getEntranceMap().getID());
                log.debug("获取玩家要去的那一界，入口地图id="+mapID);
            }
        }

        byte worldType = WorldMapDict.getInstance().getTypeWorldMapByMapID(mapID);  //当前所在地图所属的世界的类型 1:神龙界  2:魔龙界  3:仙界
        return worldType;
    }
}
