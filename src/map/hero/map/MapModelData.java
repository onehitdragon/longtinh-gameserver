package hero.map;

import hero.map.detail.Cartoon;
import hero.map.detail.Door;
import hero.map.detail.OtherObjectData;
import hero.map.detail.PopMessage;

import java.util.ArrayList;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MapModelData.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-29 下午05:44:39
 * @描述 ：地图模型数据
 */

public class MapModelData
{
    /**
     * 编号
     */
    public short             id;

    /**
     * 名称
     */
    public String            name;

    /**
     * 素材切片编号
     */
    public short             tileImageID;

    /**
     * 地图类型值（是副本地图还是普通地图）
     */
    public int               mapTypeValue;

    /**
     * 关联的怪物编号（只有副本地图此属性有效）
     */
    public String            monsterModelIDAbout;

    /**
     * 地图气候值（无，雪，雨，气泡，云，花瓣）
     */
    public int               mapWeatherValue;

    /**
     * 地图宽度、高度（格子数）
     */
    public short             width, height;

    /**
     * 默认的出现坐标
     */
    public short             bornX, bornY;

    /**
     * 地图是否可被修改
     */
    public boolean           modifiable;

    /**
     * PK标识
     */
    public byte              pkMark;

    /**
     * 非玩家对象信息列表
     */
    public OtherObjectData[] notPlayerObjectList;
    
    /**
     * 装饰层对象信息列表
     */
    public OtherObjectData[] decorateObjectList;

    /**
     * 地图底层绘制数据（长x宽）
     */
    public byte[]            bottomCanvasData;
    
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
     * 地图资源层绘制数据（长x宽）
     */
    public byte[]            resourceCanvasMap;

    /**
     * 地图元素绘制数据（X坐标、Y坐标、元素图片编号）
     */
    public short[]            elementCanvasData;

    /**
     * 地图元素图片编号列表
     */
    public ArrayList<Short>  elementImageIDList;

    /**
     * 固有NPC图片编号列表（不重复的图片编号）
     */
    public ArrayList<Short>  fixedNpcImageIDList;

    /**
     * 固有怪物图片编号列表（不重复的图片编号）
     */
    public ArrayList<Short>  fixedMonsterImageIDList;

    /**
     * 地上可拾取任务物品图片编号列表（不重复的图片编号）
     */
    public ArrayList<Short>  groundTaskGoodsImageIDList;
    

    /**
     * 任务机关图片编号列表（不重复的图片编号）
     */
    public ArrayList<Short>  taskGearImageIDList;

    /**
     * 不可通行点二维分布图（一维长度：height，二维长度：width，0值可通行，1值不可通行）
     */
    public byte[][]          unpassMarkArray;

    /**
     * 有效不可通行点数据（数量为长度/2）
     */
    public byte[]            unpassData;

    /**
     * 地图内部跳转点数据列表（0：X坐标、1：Y坐标、2：目的X坐标、3：目的Y坐标）
     */
    public byte[][]          internalPorts;

    /**
     * 通往其他地图的跳转点数据列表
     */
    public Door[]            externalPortList;

    /**
     * 地图块自动弹出消息数据列表
     */
    public PopMessage[]      popMessageList;

    /**
     * 底层动画数据
     */
    public Cartoon[]         cartoonList;
    
    /**
     * 遮盖层动画数据
     */
    public Cartoon[]         cartoonList2;
    
    /**
     * 装饰层上的装饰数量
     */
    public int animNum;
}
