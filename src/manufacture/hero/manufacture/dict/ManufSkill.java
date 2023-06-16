package hero.manufacture.dict;

import hero.manufacture.Odd;

import java.util.*;

/**
 * 制造技能数据模板 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class ManufSkill
{
    /**
     * 制造技能ID
     */
    public int     id;

    /**
     * 制造技能名
     */
    public String  name;

    /**
     * 制造类型(1-药剂师,2-宝石匠,3-工艺匠,4-铁匠)
     */
    public byte    type;

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
     * 学习需要的金钱
     */
    public int     money;
    /**
     * 学习需要的等级
     * 如果不填写则默认为 1
     */
    public byte needLevel = 1;

    /**
     * 学习需要的熟练度(0--400)
     */
    public short needSkillPoint;

    /**
     * 得到的技能点
     */
//    public int     point;
    /**
     * 获得的熟练度
     * 现在每个阶段获得的熟练度都是固定的
     */
    public final byte getPoint = 1;

    /**
     * 意外得到的制作技能ID
     */
    public int     abruptID;

    /**
     * 描述
     */
    public String  desc;

    /**
     * 三个阶段所需要的熟练度
     * 0:一阶段  1:二阶段  2:三阶段
     */
    public int[] stagesNeedPoint = new int[3];

    /**
     *  三个阶段获得熟练度的几率
     */
    public byte[] stagesGetPointOdd = new byte[3];

    /**
     * 制造需要的物品ID
     */
    public int[]   needGoodsID  = new int[8];

    /**
     * 制造需要的物品数量
     */
    public short[] needGoodsNum = new short[8];

    /**
     * 产生物品ID，0-失败，1-成功，2-意外
     */
    public int[]   getGoodsID   = new int[3];

    /**
     * 产生物品的数量
     */
    public short[] getGoodsNum  = new short[3];

    /**
     * 产生物品的失败、成功、意外几率
     */
    public byte[] getGoodsOdd = new byte[3];

    /**
     * 用来保存产生物品的失败、成功、意外几率
     * 设置时排序
     * 在随机几率时可以确定是哪个几率
     */
    private Odd[] getGoodsOddList = new Odd[3];

    /**
     * 熟练度最大值
     */
    private final static int MAX_SKILL_POINT = 400;

    public Odd[] getGetGoodsOddList() {
        return getGoodsOddList;
    }

    public void setGetGoodsOddList(List<Odd> _getGoodsOddList) {
        //todo 排序,升序
        OddCompare compare = new OddCompare();
        Collections.sort(_getGoodsOddList,compare);
        this.getGoodsOddList[0] = _getGoodsOddList.get(0);
        this.getGoodsOddList[1] = _getGoodsOddList.get(1);
        this.getGoodsOddList[2] = _getGoodsOddList.get(2);
    }

    /**
     * 根据玩家当前制造熟练度阶段对应的几率判断是否可以增加熟练度
     * @param point
     */
    public boolean canAddPoint(int point){
        if(point >= MAX_SKILL_POINT) return false;
        Random r = new Random();
        int odd = r.nextInt(100);
        byte stage = getStage(point);
        return stage <= 3 && stagesGetPointOdd[stage - 1] > odd;
    }

    /**
     * 根据玩家熟练度判断是在哪个阶段
     * @param point
     * @return
     */
    private byte getStage(int point){
        if(point <= stagesNeedPoint[0]){
            return 1;
        }else if(point <= stagesNeedPoint[1]){
            return 2;
        }else if(point <= stagesNeedPoint[2]){
            return 3;
        }else if(point > stagesNeedPoint[2]){
            return 4; //如果是在这个阶段，表示可以制造但不能再提升熟练度了
        }
        return 1;
    }



}


class OddCompare implements Comparator{

    public int compare(Object o1, Object o2) {
        Odd odd1 = (Odd)o1;
        Odd odd2 = (Odd)o2;
        if(odd1.odd > odd2.odd){
            return 1;
        }else if(odd1.odd == odd2.odd){
            return 0;
        }
        return -1;
    }
}
