package hero.map.service;

import hero.map.MapModelData;
import hero.map.WorldMap;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-3-23
 * Time: 上午11:15
 * 世界地图数据加载
 */
public class WorldMapDict {
    private static Logger log = Logger.getLogger(WorldMapDict.class);
    private static WorldMapDict instance;

    /**
     * 神龙界地图列表
     */
    private List<WorldMap> shenLongJieMapsList;
    /**
     * 魔龙界地图列表
     */
    private List<WorldMap> moLongJieMapsList;
    /**
     * 仙界地图列表
     */
    private List<WorldMap> xianJieMapsList;
    /**
     * 世界列表
     */
    private List<WorldMap> worldMapList;

    public Map<Byte,List<WorldMap>> worldMaps;

    private static final byte TYPE_SHEN_LONG_JIE_MAP = 1; //人族阵营ID
    private static final byte TYPE_MO_LONG_JIE_MAP = 2;   //魔族阵营ID
    private static final byte TYPE_XIAN_JIE_MAP = 3;
    public static final byte TYPE_WORLD_MAP = 4;

    private WorldMapDict(){
        shenLongJieMapsList = new ArrayList<WorldMap>();
        moLongJieMapsList = new ArrayList<WorldMap>();
        xianJieMapsList = new ArrayList<WorldMap>();
        worldMapList = new ArrayList<WorldMap>();

        worldMaps = new HashMap<Byte,List<WorldMap>>();
    }

    public static WorldMapDict getInstance(){
        if(instance == null){
            instance = new WorldMapDict();
        }
        return instance;
    }

    public void load(MapConfig config){
        try{
//            log.debug("world maps path = " + config.world_maps);
//            log.debug("world_maps_mo_long_jie path = " + config.world_maps_mo_long_jie);
//            log.debug("world_maps_shen_long_jie path = " + config.world_maps_shen_long_jie);
//            log.debug("world_maps_xian_jie path = " + config.world_maps_xian_jie);
            File fileList = new File(config.world_maps);
            File[] dataFileList = fileList.listFiles();
            for(File file : dataFileList){
                if(!file.getName().endsWith(".xml")){
                    continue;
                }

                SAXReader reader = new SAXReader();
                Document document = reader.read(file);
                Element root = document.getRootElement();
                Iterator<Element> eit = root.elementIterator();

                short mapID,cellX,cellY,anu,png,width,height;
                String name,desc;
                WorldMap worldMap;
                while (eit.hasNext()){
                    Element e = eit.next();
                    mapID = Short.parseShort(e.elementTextTrim("mapID"));
                    cellX = Short.parseShort(e.elementTextTrim("cellX"));
                    cellY = Short.parseShort(e.elementTextTrim("cellY"));
                    name = e.elementTextTrim("name");
                    desc = e.elementTextTrim("desc");
                    png = Short.parseShort(e.elementTextTrim("png"));
                    anu = Short.parseShort(e.elementTextTrim("anu"));
                    width = Short.parseShort(e.elementTextTrim("width"));
                    height = Short.parseShort(e.elementTextTrim("height"));

                    worldMap = new WorldMap();
                    worldMap.mapID = mapID;
                    worldMap.cellX = cellX;
                    worldMap.cellY = cellY;
                    worldMap.name = name;
                    worldMap.desc = desc;
                    worldMap.type = TYPE_WORLD_MAP;

                    worldMap.anu = anu;
                    worldMap.png = png;
                    worldMap.width = width;
                    worldMap.height = height;

                    worldMapList.add(worldMap);
                    log.debug("load world map["+TYPE_WORLD_MAP+"]["+name+"] end.");
                }
                log.debug("world map size = " + worldMapList.size());
                worldMaps.put(TYPE_WORLD_MAP,worldMapList);
            }

            fileList = new File(config.world_maps_shen_long_jie);
            dataFileList = fileList.listFiles();
            for(File file : dataFileList){
                if(!file.getName().endsWith(".xml")){
                    continue;
                }

                SAXReader reader = new SAXReader();
                Document document = reader.read(file);
                Element root = document.getRootElement();
                Iterator<Element> eit = root.elementIterator();

                short mapID,cellX,cellY;
                short dungeons = 0;
                String name,desc;
                WorldMap worldMap;
                while (eit.hasNext()){
                    Element e = eit.next();
                    mapID = Short.parseShort(e.elementTextTrim("mapID"));
                    cellX = Short.parseShort(e.elementTextTrim("cellX"));
                    cellY = Short.parseShort(e.elementTextTrim("cellY"));
                    name = e.elementTextTrim("name");
                    desc = e.elementTextTrim("desc");

                    String ds = e.elementTextTrim("dungeon_entry_mapID");
                    if(ds != null && ds.trim().length()>0){
                        dungeons = Short.parseShort(ds);
                    }

                    worldMap = new WorldMap();
                    worldMap.mapID = mapID;
                    worldMap.cellX = cellX;
                    worldMap.cellY = cellY;
                    worldMap.name = name;
                    worldMap.desc = desc;
                    worldMap.dungeonEntryMapID = dungeons;
                    worldMap.type = TYPE_SHEN_LONG_JIE_MAP;

                    shenLongJieMapsList.add(worldMap);
                    log.debug("load world map["+TYPE_SHEN_LONG_JIE_MAP+"]["+name+"] end.");
                }
                log.debug("shen long map list size = " + shenLongJieMapsList.size());
                worldMaps.put(TYPE_SHEN_LONG_JIE_MAP,shenLongJieMapsList);
            }

            fileList = new File(config.world_maps_mo_long_jie);
            dataFileList = fileList.listFiles();
            for(File file : dataFileList){
                if(!file.getName().endsWith(".xml")){
                    continue;
                }

                SAXReader reader = new SAXReader();
                Document document = reader.read(file);
                Element root = document.getRootElement();
                Iterator<Element> eit = root.elementIterator();

                short mapID,cellX,cellY;
                short dungeons = 0;
                String name,desc;
                WorldMap worldMap;
                while (eit.hasNext()){
                    Element e = eit.next();
                    mapID = Short.parseShort(e.elementTextTrim("mapID"));
                    cellX = Short.parseShort(e.elementTextTrim("cellX"));
                    cellY = Short.parseShort(e.elementTextTrim("cellY"));
                    name = e.elementTextTrim("name");
                    desc = e.elementTextTrim("desc");

                    String ds = e.elementTextTrim("dungeon_entry_mapID");
                    if(ds != null && ds.trim().length()>0){
                        dungeons = Short.parseShort(ds);
                    }

                    worldMap = new WorldMap();
                    worldMap.mapID = mapID;
                    worldMap.cellX = cellX;
                    worldMap.cellY = cellY;
                    worldMap.name = name;
                    worldMap.desc = desc;
                    worldMap.dungeonEntryMapID = dungeons;
                    worldMap.type = TYPE_MO_LONG_JIE_MAP;

                    moLongJieMapsList.add(worldMap);
                    log.debug("load world map["+TYPE_MO_LONG_JIE_MAP+"]["+name+"] end.");
                }
                log.debug("world mo long map list size = " + moLongJieMapsList.size());
                worldMaps.put(TYPE_MO_LONG_JIE_MAP,moLongJieMapsList);
            }

            fileList = new File(config.world_maps_xian_jie);
            dataFileList = fileList.listFiles();
            for(File file : dataFileList){
                if(!file.getName().endsWith(".xml")){
                    continue;
                }

                SAXReader reader = new SAXReader();
                Document document = reader.read(file);
                Element root = document.getRootElement();
                Iterator<Element> eit = root.elementIterator();

                short mapID,cellX,cellY;
                short dungeons = 0;
                String name,desc;
                WorldMap worldMap;
                while (eit.hasNext()){
                    Element e = eit.next();
                    mapID = Short.parseShort(e.elementTextTrim("mapID"));
                    cellX = Short.parseShort(e.elementTextTrim("cellX"));
                    cellY = Short.parseShort(e.elementTextTrim("cellY"));
                    name = e.elementTextTrim("name");
                    desc = e.elementTextTrim("desc");

                    String ds = e.elementTextTrim("dungeon_entry_mapID");
                    if(ds != null && ds.trim().length()>0){
                        dungeons = Short.parseShort(ds);
                    }

                    worldMap = new WorldMap();
                    worldMap.mapID = mapID;
                    worldMap.cellX = cellX;
                    worldMap.cellY = cellY;
                    worldMap.name = name;
                    worldMap.desc = desc;
                    worldMap.dungeonEntryMapID = dungeons;
                    worldMap.type = TYPE_XIAN_JIE_MAP;

                    xianJieMapsList.add(worldMap);
                    log.debug("load world map["+TYPE_XIAN_JIE_MAP+"]["+name+"] end.");
                }
                log.debug("xian jie map list size = " + xianJieMapsList.size());
                worldMaps.put(TYPE_XIAN_JIE_MAP,xianJieMapsList);
            }

            //设置各地图出生点
            for (Iterator<List<WorldMap>> it = worldMaps.values().iterator(); it.hasNext();){
                List<WorldMap> mapList = it.next();
                for(WorldMap worldMap : mapList){
                    MapModelData map = MapModelDataDict.getInstance().getMapModelData(worldMap.mapID);
                    if(map != null){
                        worldMap.bornX = map.bornX;
                        worldMap.bornY = map.bornY;
                    }
                }
            }

        }catch (Exception e){
            log.error("load world maps error : ",e);
        }

    }

    public List<WorldMap> getWorldMapListByType(byte type){
        return worldMaps.get(type);
    }

    /**
     * 根据大世界名称获取WorldMap
     * @param name
     * @return
     */
    public WorldMap getMaxWorldMapByName(String name){
        for(WorldMap worldMap : worldMapList){
            if(worldMap.name.equals(name)){
                return worldMap;
            }
        }
        return null;
    }

    /**
     * 根据副本入口地图获取副本的入口所在的地图ID
     * @param dungeonEntryMapID
     * @return
     */
    public short getIncludeDungeonMapID(short dungeonEntryMapID){
        for(Iterator<List<WorldMap>> it = worldMaps.values().iterator(); it.hasNext();){
            List<WorldMap> mapList = it.next();
            for(WorldMap worldMap : mapList){
                if(worldMap.dungeonEntryMapID == dungeonEntryMapID){
                    return worldMap.mapID;
                }
            }
        }
        return 0;
    }

    /**
     * 根据地图ID查找该地图所在那界的地图列表
     * @param mapID
     * @return
     */
    public byte getTypeWorldMapByMapID(short mapID){
        for(Iterator<List<WorldMap>> it = worldMaps.values().iterator(); it.hasNext();){
            List<WorldMap> mapList = it.next();
            for(WorldMap worldMap : mapList){
                if(worldMap.mapID == mapID){
                    return worldMap.type;
                }
            }
        }
        return 0;
    }

    /**
     * 根据MAPID获取描述
     * @param mapID
     * @return
     */
    public String getMapDesc(short mapID){
        for(Iterator<List<WorldMap>> it = worldMaps.values().iterator(); it.hasNext();){
            List<WorldMap> mapList = it.next();
            for(WorldMap worldMap : mapList){
                if(worldMap.mapID == mapID){
                    return worldMap.desc;
                }
            }
        }
        return null;
    }
}
