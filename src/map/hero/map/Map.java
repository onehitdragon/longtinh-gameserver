package hero.map;

import hero.dungeon.Dungeon;
import hero.item.legacy.MonsterLegacyBox;
import hero.map.detail.Cartoon;
import hero.map.detail.Door;
import hero.map.detail.OtherObjectData;
import hero.map.detail.PopMessage;
import hero.map.service.WeatherManager;
import hero.npc.Npc;
import hero.player.HeroPlayer;
import hero.share.Direction;
import hero.share.service.ME2ObjectList;

import java.util.ArrayList;

import hero.npc.Monster;

import hero.npc.others.Animal;
import hero.npc.others.Box;
import hero.npc.others.DoorPlate;
import hero.npc.others.GroundTaskGoods;
import hero.npc.others.RoadInstructPlate;
import hero.npc.others.TaskGear;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Map.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-29 下午03:29:38
 * @描述 ：地图
 */

public class Map
{
    /**
     * 编号
     */
    private short                        id;

    /**
     * 名称
     */
    private String                       name;

    /**
     * 素材切片编号
     */
    private short                        tileID;

    /**
     * 地图宽度、高度（格子数）
     */
    private short                        width, height;

    /**
     * 默认的出现坐标
     */
    private short                        bornX, bornY;

    /**
     * 地图类型（是副本地图还是普通地图）
     */
    private EMapType                     mapType;

    /**
     * 所属副本
     */
    private Dungeon                      dungeon;

    /**
     * 气候
     */
    private EMapWeather                  weather;

    /**
     * 地图上小怪关联的怪物编号（此编号为地图上唯一存在的怪）
     */
    private String                       monsterModelIDAbout;

    /**
     * 地图对应小地图图片编号
     */
    private int                          microMapID;

    /**
     * 人族死亡后复活去往的地图编号
     */
    private short                        targetMapIDAfterDie;

    /**
     * 人族使用游戏道具后去往的地图编号
     */
    private short                        targetMapIDAfterUseGoods;

    /**
     * 魔族死亡后复活去往的地图编号
     */
    private short                        mozuTargetMapIDAfterDie;

    /**
     * 魔族使用游戏道具后去往的地图编号
     */
    private short                        mozuTargetMapIDAfterUseGoods;

    /**
     * 地图是否可被修改
     */
    private boolean                      modifiable;

    /**
     * 0 可决斗,可PK; 1 可决斗,不可PK ;2 不可决斗,可PK ; 3 不可决斗,不可PK
     */
    private byte                         pkMark;

    /**
     * 所属区域
     */
    private Area                         area;

    /**
     * 地图上的玩家列表
     */
    private ME2ObjectList                playerList;

    /**
     * 地图上的怪物列表
     */
    private ME2ObjectList                monsterList;

    /**
     * 地图上的中立NPC列表
     */
    private ME2ObjectList                npcList;

    /**
     * 地图上的动物列表
     */
    private ArrayList<Animal>            animalList;

    /**
     * 地图上的任务机关列表
     */
    private ArrayList<TaskGear>          gearList;

    /**
     * 地图上的路牌
     */
    private ArrayList<RoadInstructPlate> roadPlateList;

    /**
     * 地图上的室内门牌
     */
    private ArrayList<DoorPlate>         doorPlateList;

    /**
     * 地图上的宝箱列表
     */
    private ArrayList<Box>               boxList;

    /**
     * 地图上的任务物品列表
     */
    private ArrayList<GroundTaskGoods>   groundTaskGoodsList;

    /**
     * 地图上的怪物掉落的箱子
     */
    private ArrayList<MonsterLegacyBox>  legacyBoxList;

    /**
     * 固有NPC图片编号列表（不重复的图片编号）
     */
    public ArrayList<Short>              fixedNpcImageIDList;

    /**
     * 固有怪物图片编号列表（不重复的图片编号）
     */
    public ArrayList<Short>              fixedMonsterImageIDList;

    /**
     * 地上可拾取任务物品图片编号列表（不重复的图片编号）
     */
    public ArrayList<Short>              groundTaskGoodsImageIDList;

    /**
     * 任务机关图片编号列表（不重复的图片编号）
     */
    public ArrayList<Short>              taskGearImageIDList;

    /**
     * 地图底层绘制数据（长x宽）
     */
    public byte[]                        bottomCanvasData;

    /**
     * 地图资源层绘制数据（长x宽）
     */
    public byte[]                        resourceCanvasMap;

    /**
     * 地图元素绘制数据（X坐标、Y坐标、元素图片编号）
     */
    public short[]                        elementCanvasData;

    /**
     * 地图元素图片编号列表
     */
    public ArrayList<Short>              elementImageIDList;

    /**
     * 不可通行点二维分布图（一维长度：height，二维长度：width，0值可通行，1值不可通行）
     */
    public byte[][]                      unpassMarkArray;

    /**
     * 有效不可通行点数据（数量为长度/2）
     */
    public byte[]                        unpassData;

    /**
     * 通往其他地图的门列表
     */
    public Door[]                        doorList;

    /**
     * 地图内部跳转点数据列表（0：X坐标、1：Y坐标、2：目的X坐标、3：目的Y坐标）
     */
    public byte[][]                      internalTransportList;

    /**
     * 地图块自动弹出消息数据列表
     */
    public PopMessage[]                  popMessageList;

    /**
     * 动画数据
     */
    public Cartoon[]                     cartoonList;
    
    /**
     * 第一层旋转
     */
    public byte[] transformData1;
    
    /**
     * 遮盖层旋转
     */
    public byte[] transformData2;
    
    
    /**
     * 资源层旋转
     */
    public byte[] resourceTransformData;
    
    /**
     * 遮盖层动画数据
     */
    public Cartoon[]         cartoonList2;
    
    /**
     * 装饰层上的装饰数量
     */
    public int animNum;
    
    /**
     * 这个地图上所有的装饰物
     */
    public OtherObjectData[] decoraterList;
    
    /**
     * 此地图是否可以摆摊
     */
    public boolean canStore;
    

    /**
     * 构造
     */
    public Map(short _id, String _name)
    {
        id = _id;
        name = _name;
        npcList = new ME2ObjectList();
        monsterList = new ME2ObjectList();
        playerList = new ME2ObjectList();
        animalList = new ArrayList<Animal>();
        gearList = new ArrayList<TaskGear>();
        roadPlateList = new ArrayList<RoadInstructPlate>();
        doorPlateList = new ArrayList<DoorPlate>();
        boxList = new ArrayList<Box>();
        groundTaskGoodsList = new ArrayList<GroundTaskGoods>();
        legacyBoxList = new ArrayList<MonsterLegacyBox>();
    }

    /**
     * 获取地图编号
     * 
     * @return
     */
    public short getID ()
    {
        return id;
    }

    /**
     * 设置编号
     * 
     * @param _id
     */
    public void setID (int _id)
    {
        id = (short) _id;
    }

    /**
     * 获取地图名
     * 
     * @return
     */
    public String getName ()
    {
        return name;
    }

    /**
     * 设置地图名
     * 
     * @param _name
     */
    public void setName (String _name)
    {
        name = _name;
    }

    /**
     * 获取地图类型
     * 
     * @return
     */
    public EMapType getMapType ()
    {
        return mapType;
    }

    /**
     * 设置地图类型
     * 
     * @param _mapType
     */
    public void setMapType (EMapType _mapType)
    {
        mapType = _mapType;
    }

    /**
     * 打开外部门
     * 
     * @param _monsterModelID 关联的怪物模板编号
     * @return 打开的门列表
     */
    public ArrayList<Door> openTheDoor (String _monsterModelID)
    {
        ArrayList<Door> openedDoorList = new ArrayList<Door>();

        for (Door point : doorList)
        {
            if (point.monsterIDAbout.equals(_monsterModelID))
            {
                openedDoorList.add(point);
            }
        }

        return openedDoorList;
    }

    /**
     * 获取地图素材编号
     * 
     * @return
     */
    public short getTileID ()
    {
        return tileID;
    }

    /**
     * 设置地图素材编号
     * 
     * @param _tileID
     */
    public void setTileID (int _tileID)
    {
        tileID = (short) _tileID;
    }

    /**
     * 设置人族死亡复活后的地图编号
     * 
     * @param _mapID
     */
    public void setTargetMapIDAfterDie (short _mapID)
    {
        targetMapIDAfterDie = _mapID;
    }

    /**
     * 获取人族死亡复活后的地图编号
     * 
     * @return
     */
    public short getTargetMapIDAfterDie ()
    {
        return targetMapIDAfterDie;
    }

    /**
     * 获取魔族死亡后复活的地图编号
     * @return
     */
    public short getMozuTargetMapIDAfterDie() {
        return mozuTargetMapIDAfterDie;
    }

    /**
     *
     * 设置魔族死亡后复活后的地图编号
     * @param mozuTargetMapIDAfterDie
     */
    public void setMozuTargetMapIDAfterDie(short mozuTargetMapIDAfterDie) {
        this.mozuTargetMapIDAfterDie = mozuTargetMapIDAfterDie;
    }

    /**
     * 获取魔族使用道具后去往的地图编号
     * @return
     */
    public short getMozuTargetMapIDAfterUseGoods() {
        return mozuTargetMapIDAfterUseGoods;
    }

    /**
     * 设置魔族使用道具后去往的地图编号
     * @param mozuTargetMapIDAfterUseGoods
     */
    public void setMozuTargetMapIDAfterUseGoods(short mozuTargetMapIDAfterUseGoods) {
        this.mozuTargetMapIDAfterUseGoods = mozuTargetMapIDAfterUseGoods;
    }

    /**
     * 设置人族使用道具后去往的地图编号
     * 
     * @param _mapID
     */
    public void setTargetMapIDAfterUseGoods (short _mapID)
    {
        targetMapIDAfterUseGoods = _mapID;
    }

    /**
     * 获取人族使用道具后去往的地图编号
     * 
     * @return
     */
    public short getTargetMapIDAfterUseGoods ()
    {
        return targetMapIDAfterUseGoods;
    }

    /**
     * 获取地图宽度
     * 
     * @return
     */
    public short getWidth ()
    {
        return width;
    }

    /**
     * 设置地图宽度
     * 
     * @param _width
     */
    public void setWidth (int _width)
    {
        width = (short) _width;
    }

    /**
     * 获取地图高度
     * 
     * @return
     */
    public short getHeight ()
    {
        return height;
    }

    /**
     * 设置地图高度
     * 
     * @param _height
     */
    public void setHeight (int _height)
    {
        height = (short) _height;
    }

    /**
     * 获取小地图图片编号
     * 
     * @return
     */
    public int getMiniImageID ()
    {
        return microMapID;
    }

    /**
     * 设置小地图图片编号
     * 
     * @param _microMapID
     */
    public void setMiniImageID (int _microMapID)
    {
        microMapID = _microMapID;
    }

    /**
     * 获取地图气候
     * 
     * @return
     */
    public EMapWeather getWeather ()
    {
        return weather;
    }

    /**
     * 设置地图气候
     * 
     * @param _weather
     */
    public void setWeather (EMapWeather _weather)
    {
        weather = _weather;

        if (EMapWeather.NONE != _weather)
        {
            WeatherManager.getInstance().add(this);
        }
    }

    /**
     * 获取出生点X坐标
     * 
     * @return
     */
    public short getBornX ()
    {
        return bornX;
    }

    /**
     * 设置出生点X坐标
     * 
     * @param _bornX
     */
    public void setBornX (short _bornX)
    {
        bornX = _bornX;
    }

    /**
     * 获取地图出生点Y坐标
     * 
     * @return
     */
    public short getBornY ()
    {
        return bornY;
    }

    /**
     * 设置出生点X坐标
     * 
     * @param _bornX
     */
    public void setBornY (short _bornY)
    {
        bornY = _bornY;
    }

    /**
     * 设置关联怪物编号
     * 
     * @param _monsterModelID
     */
    public void setMonsterModelIDAbout (String _monsterModelID)
    {
        monsterModelIDAbout = _monsterModelID;
    }

    /**
     * 获取关联的怪物编号
     * 
     * @return
     */
    public String getMonsterModelIDAbout ()
    {
        return monsterModelIDAbout;
    }

    /**
     * 修改地图底层绘制数据
     * 
     * @param x
     * @param y
     * @param newTileId
     */
    public void changeBottomCanvasData (short x, short y, short newTileId)
    {
        if (modifiable)
        {
            bottomCanvasData[y * width + x] = (byte) newTileId;
        }
    }

    /**
     * 修改地图元素绘制数据
     * 
     * @param x
     * @param y
     * @param newTileId
     */
    public void changeElementData (short x, short y, short newTileId)
    {
        if (modifiable)
        {
            elementCanvasData[y * width + x] = (byte) newTileId;
        }
    }

    /**
     * 是否可修改
     * 
     * @return
     */
    public boolean isModifiable ()
    {
        return modifiable;
    }

    /**
     * 设置是否可被修改
     * 
     * @param _yesOrNo
     */
    public void setModifiable (boolean _yesOrNo)
    {
        modifiable = _yesOrNo;
    }

    /**
     * 获取普通NPC列表
     * 
     * @return
     */
    public ME2ObjectList getNpcList ()
    {
        return npcList;
    }

    /**
     * 获取怪物列表
     * 
     * @return
     */
    public ME2ObjectList getMonsterList ()
    {
        return monsterList;
    }

    /**
     * 获取玩家列表
     * 
     * @return
     */
    public ME2ObjectList getPlayerList ()
    {
        return playerList;
    }

    /**
     * 获取动物列表
     * 
     * @return
     */
    public ArrayList<Animal> getAnimalList ()
    {
        return animalList;
    }

    /**
     * 获取任务机关列表
     * 
     * @return
     */
    public ArrayList<TaskGear> getTaskGearList ()
    {
        return gearList;
    }

    /**
     * 获取路牌列表
     * 
     * @return
     */
    public ArrayList<RoadInstructPlate> getRoadPlateList ()
    {
        return roadPlateList;
    }

    /**
     * 获取室内门牌列表
     * 
     * @return
     */
    public ArrayList<DoorPlate> getDoorPlateList ()
    {
        return doorPlateList;
    }

    /**
     * 获取宝箱列表
     * 
     * @return
     */
    public ArrayList<Box> getBoxList ()
    {
        return boxList;
    }

    /**
     * 存在同编号的箱子
     * 
     * @param _boxModelID
     * @return
     */
    public Box existBox (String _boxModelID)
    {
        for (Box box : boxList)
        {
            if (box.getModelID().equals(_boxModelID))
            {
                return box;
            }
        }

        return null;
    }

    /**
     * 刷新所有箱子
     */
    public void refreshBox ()
    {
        for (Box box : boxList)
        {
            box.rebirth(true);
        }
    }

    /**
     * 获取地上的任务物品
     * 
     * @return
     */
    public ArrayList<GroundTaskGoods> getGroundTaskGoodsList ()
    {
        return groundTaskGoodsList;
    }

    /**
     * 获取地图上的玩家
     * 
     * @param _objectID 玩家ObjectID
     * @return
     */
    public final HeroPlayer getPlayer (int _objectID)
    {
        return (HeroPlayer) playerList.getObject(_objectID);
    }

    /**
     * 获取地图上的玩家
     * 
     * @param _id 玩家ObjectID
     * @return
     */
    public final Npc getNpc (int _objectID)
    {
        return (Npc) npcList.getObject(_objectID);
    }

    /**
     * 获取地图上的怪物
     * 
     * @param _id 怪物ObjectID
     * @return
     */
    public final Monster getMonster (int _objectID)
    {
        return (Monster) monsterList.getObject(_objectID);
    }

    /**
     * 是否是去往外部的跳转点
     * 
     * @param _x
     * @param _y
     * @return
     */
    public final boolean isDxternaPort (short _x, short _y)
    {
        for (int i = 0; i < doorList.length; i++)
        {
            if ((doorList[i].x == _x) && (doorList[i].y == _y))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取某坐标的外部地图信息
     * 
     * @param _x
     * @param _y
     * @return
     */
    public short[] getExternalPort (short _x, short _y)
    {
        for (int i = 0; i < doorList.length; i++)
        {
            if ((doorList[i].x == _x) && (doorList[i].y == _y))
            {
                short[] p = new short[4];

                p[0] = doorList[i].direction;
                p[1] = doorList[i].targetMapID;
                p[2] = doorList[i].targetMapX;
                p[3] = doorList[i].targetMapY;

                return p;
            }
        }

        return null;
    }

    /**
     * 获取目标地图的出现坐标数据
     * 
     * @param _targetMapID
     * @param _x
     * @param _y
     * @return
     */
    public short[] getTargetMapPoint (int _targetMapID, short _x, short _y)
    {
        for (int i = 0; i < doorList.length; i++)
        {
            if (doorList[i].x == _x && doorList[i].y == _y
                    && doorList[i].targetMapID == _targetMapID)
            {
                short[] p = new short[2];

                p[0] = doorList[i].targetMapX;
                p[1] = doorList[i].targetMapY;

                return p;
            }
        }

        return null;
    }

    /**
     * 获取怪物掉落箱子列表
     * 
     * @param _box
     */
    public ArrayList<MonsterLegacyBox> getLegacyBoxList ()
    {
        return legacyBoxList;
    }

    /**
     * 是否有效地图坐标
     * 
     * @param _x
     * @param _y
     * @return
     */
    public boolean isLegalPoint (int _x, int _y)
    {
        return (_x >= 0 && _y >= 0 && _x < width && _y < height);
    }

    /**
     * 设置区域
     * 
     * @param _area
     */
    public void setArea (Area _area)
    {
        area = _area;
    }

    /**
     * 获取区域
     * 
     * @return
     */
    public Area getArea ()
    {
        return area;
    }

    /**
     * 获取PK标记
     * 
     * @return
     */
    public byte getPKMark ()
    {
        return pkMark;
    }

    /**
     * 设置PK标记
     * 
     * @param _pkMark
     */
    public void setPKMark (byte _pkMark)
    {
        pkMark = _pkMark;
    }

    /**
     * 是否是可行走的格子
     * 
     * @param _cellX
     * @param _cellY
     * @return
     */
    public boolean isRoad (int _cellX, int _cellY)
    {
        if (isLegalPoint(_cellX, _cellY))
        {
            return unpassMarkArray[_cellY][_cellX] == 0;
        }

        return false;
    }
    
    
    /**
     * 面对的方向是否可以攻击
     * @param _cellX
     * @param _cellY
     * @return
     */
    public boolean attackIsRoad (int _cellX, int _cellY, int _targetX, int _targetY)
    {
    	boolean result = false;
    	int x = _cellX;
    	int y = _cellY;
    	if(x == _targetX && y == _targetY)
    	{
    		//重叠
    		result = true;
    	}
    	else if(x == _targetX)
    	{
    		//2者处于x轴平行,
        	while (!(y == _targetY)) 
        	{
        		if(y > _targetY)
        		{
        			y -= 1;
        		}
        		else
        		{
        			y += 1;
        		}
        		if (isLegalPoint(_cellX, _cellY) && isLegalPoint(x, y))
        		{
        			result = unpassMarkArray[x][y] == 0;
        			if (!result) {
    					break;
    				}
        		}
    		}//end while
    	}
    	else if(y == _targetY)
    	{
    		//2者处于y轴平行,
        	while (!(x == _targetX)) 
        	{
        		if (x > _targetX) 
        		{
        			x -= 1;
        		}
        		else
        		{
        			x += 1;
        		}
        		if (isLegalPoint(_cellX, _cellY) && isLegalPoint(x, y))
        		{
        			result = unpassMarkArray[x][y] == 0;
        			if (!result) {
    					break;
    				}
        		}
    		}//end while
    	}
    	else 
    	{
    		//x,y均不在同一水平
//    		int[][] line = countStraightLine(_cellX, _cellY, _targetX, _targetY);
//    		for (int i = 0; i < line.length; i++) {
//    			result = unpassMarkArray[line[i][0]][line[i][1]] == 0;
//    			if (!result) {
//					break;
//				}
//			}
		}
        return result;
    }

    /**
     * 地图销毁
     */
    public void destroy ()
    {
        Monster monster;

        for (int i = 0; i < monsterList.size(); i++)
        {
            monster = (Monster) (monsterList.get(i));
            monster.destroy();
        }

        Npc npc;

        for (int i = 0; i < npcList.size(); i++)
        {
            npc = (Npc) (npcList.get(i));
            npc.destroy();
        }
    }

    /**
     * 设置所属副本
     * 
     * @param _dungeon
     */
    public void setDungeon (Dungeon _dungeon)
    {
        dungeon = _dungeon;
    }

    /**
     * 获取所属副本
     */
    public Dungeon getDungeon ()
    {
        return dungeon;
    }

    /**
     * 可决斗、可攻击
     */
    public static final int TYAPE_ALLOW_PK_AND_ALLOW_FIGHT = 1;

    /**
     * 不可决斗、可攻击
     */
    public static final int TYPE_NOT_PK_AND_ALLOW_FIGHT    = 2;

    /**
     * 可决斗、不可攻击
     */
    public static final int TYPE_ALLOW_PK_AND_NOT_FIGHT    = 3;

    /**
     * 不可决斗、不可攻击
     */
    public static final int TYPE_NOT_PK_AND_NOT_FIGHT      = 4;

    /**
     * 世界地图编号上限
     */
    public static final int WORLD_MAP_ID_UPPER_LIMIT       = 5000;
    
    /**
     * 是否是新地图
     */
    public static final boolean IS_NEW_MAP = true;
}
