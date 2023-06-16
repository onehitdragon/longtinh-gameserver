package hero.gather.dict;

/**
 * 炼化数据模板类 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class Refined
{
    /**
     * 炼制技能ID
     */
    public int     id;

    /**
     * 炼制技能名
     */
    public String  name;

    /**
     * 图片ID
     */
    public short   icon;

    /**
     * 是否可以在npc出学习
     */
    public boolean npcStudy;

    /**
     * 种类
     */
    public byte  category;

    /**
     * 需要的等级
     */
    public byte    needLvl;

    /**
     * 学习需要的金钱
     */
    public int     money;

    /**
     * 得到的技能点
     */
    public int     point;

    /**
     * 意外得到的制作技能ID
     */
    public int     abruptID;

    /**
     * 描述
     */
    public String  desc;

    /**
     * 制造需要的灵魂ID
     */
    public int[]   needSoulID  = new int[8];

    /**
     * 制造需要的灵魂数量
     */
    public short[] needSoulNum = new short[8];

    /**
     * 产生物品ID，0-失败，1-成功，2-意外
     */
    public int[]   getGoodsID  = new int[3];

    /**
     * 产生物品的数量
     */
    public short[] getGoodsNum = new short[3];

    /**
     * 需要的葫芦ID，炼制的时候需要，其它制作此值为0
     */
    public int     needGourd;

}
