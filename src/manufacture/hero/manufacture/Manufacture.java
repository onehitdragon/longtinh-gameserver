package hero.manufacture;

import hero.manufacture.service.ManufactureServerImpl;

import java.util.ArrayList;

/**
 * 玩家制造类 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class Manufacture
{
    /**
     * 是否需要保存数据
     */
    private boolean            save;

    /**
     * 技能等级
     * 暂时不用 ---2010-12-21 jiaodongjie
     */
    private byte               lvl;

    /**
     * 当前熟练度
     * 熟练度是对于大类技能(铁匠、工艺匠等)说的
     * 每个大类技能下的技能条目共用大类技能对应的熟练度
     */
    private int                point;

    /**
     * 制造类型
     */
    private ManufactureType    type;

    /**
     * 制造条目编号列表
     */
    private ArrayList<Integer> manufSkillIDList;

    /**
     * 构造
     * 
     * @param _type
     */
    public Manufacture(ManufactureType _type)
    {
        lvl = 1;
        type = _type;
        manufSkillIDList = new ArrayList<Integer>();
    }

    public ManufactureType getManufactureType ()
    {
        return type;
    }

    public void setSave (boolean _change)
    {
        save = _change;
    }

    public boolean isSave ()
    {
        return save;
    }

    /**
     * 技能升级
     */
    public void lvlUp ()
    {
        lvl++;
        save = true;
    }

    /**
     * 当前技能等级
     * 
     * @return
     */
    public byte getLvl ()
    {
        return lvl;
    }

    /**
     * 设置技能等级
     * 
     * @param _lvl
     */
    public void setLvl (byte _lvl)
    {
        lvl = _lvl;
    }

    /**
     * 设置熟练度
     * 
     * @param _point
     */
    public void setPoint (int _point)
    {
        point = _point;
    }

    public void addPoint (int _point)
    {
        point += _point;
        save = true;
        /*if (point > ManufactureServerImpl.POINT_LIMIT[lvl - 1])
        {
            point = ManufactureServerImpl.POINT_LIMIT[lvl - 1];
        }*/
    }

    /**
     * 得到熟练度
     * 
     * @return
     */
    public int getPoint ()
    {
        return point;
    }

    /**
     * 添加制造ID
     * 
     * @param _manufID
     */
    public void addManufID (int _manufID)
    {
        manufSkillIDList.add(_manufID);
    }

    /**
     * 得到玩家可制造ID集合
     * 
     * @return
     */
    public ArrayList<Integer> getManufIDList ()
    {
        return manufSkillIDList;
    }

    public boolean isStudyedManufSkillID (int _manufSkillID)
    {
        return manufSkillIDList.contains(_manufSkillID);
    }

    public final static byte MAX_LVL = 5;
}
