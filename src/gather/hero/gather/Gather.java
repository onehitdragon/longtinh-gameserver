package hero.gather;

import hero.gather.service.GatherServerImpl;
import hero.item.special.Gourd;

import java.util.ArrayList;

/**
 * 采集类 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class Gather
{
    /**
     * 是否需要保存数据
     */
    private boolean                save;

    /**
     * 技能等级
     */
    private byte                   lvl;

    /**
     * 当前技能点
     */
    private int                    point;

    /**
     * 炼化技能列表
     */
    private ArrayList<Integer>     refinedSkillIDList;

    /**
     * 灵魂列表
     */
    private ArrayList<MonsterSoul> souls;

    /**
     * 构造
     */
    public Gather()
    {
        lvl = 1;
        souls = new ArrayList<MonsterSoul>();
        refinedSkillIDList = new ArrayList<Integer>();
    }

    /**
     * 得到炼化技能列表
     * 
     * @return
     */
    public ArrayList<Integer> getRefinedList ()
    {
        return refinedSkillIDList;
    }

    /**
     * 添加炼化技能
     * 
     * @param _refinedID
     */
    public void addRefinedID (int _refinedID)
    {
        refinedSkillIDList.add(_refinedID);
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
    public boolean lvlUp ()
    {
        if (lvl < MAX_LVL)
        {
            lvl++;

            return true;
        }

        return false;
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
     * 设置技能点
     * 
     * @param _point
     */
    public void setPoint (int _point)
    {
        point = _point;
    }

    /**
     * 添加技能点
     * 
     * @param _point
     */
    public boolean addPoint (int _point)
    {
        if (point < GatherServerImpl.POINT_LIMIT[lvl - 1])
        {
            point += _point;

            if (point > GatherServerImpl.POINT_LIMIT[lvl - 1])
            {
                point = GatherServerImpl.POINT_LIMIT[lvl - 1];
            }

            return true;
        }

        return false;
    }

    /**
     * 得到技能点
     * 
     * @return
     */
    public int getPoint ()
    {
        return point;
    }

    /**
     * 添加怪物灵魂
     * 
     * @param _soulID
     * @param _gourd
     */
    public boolean addMosnterSoul (int _soulID, Gourd _gourd)
    {
        for (MonsterSoul s : souls)
        {
            if (s.soulID == _soulID)
            {
                if (s.num < _gourd.getAnimaMaxNumerPerType())
                {
                    s.num++;
                    save = true;
                }
                
                return true;
            }
        }

        if (souls.size() < _gourd.getMonsterTypeNumber())
        {
            souls.add(new MonsterSoul(_soulID));
            save = true;
            return true;
        }

        return false;
    }

    /**
     * 添加怪物灵魂
     * 
     * @param _soul
     */
    public void loadMonsterSoul (MonsterSoul _soul)
    {
        souls.add(_soul);
    }

    /**
     * 释放指定灵魂ID的灵魂
     * 
     * @param _index
     */
    public void releaseMonsterSoul (byte _index)
    {
        if (souls.size() > _index)
        {
            souls.remove(_index);

            save = true;
        }
    }

    /**
     * 释放指定灵魂ID和数量的灵魂
     * 
     * @param _soulID
     * @param _num
     */
    public void releaseMonsterSoul (int _soulID, short _num)
    {
        save = true;

        for (int i = 0; i < souls.size(); i++)
        {
            MonsterSoul s = souls.get(i);
            if (s.soulID == _soulID)
            {
                if (s.num > _num)
                {
                    s.num -= _num;
                    return;
                }
                else if (s.num == _num)
                {
                    souls.remove(i);
                    return;
                }
                else
                {
                    _num -= s.num;
                    souls.remove(i);
                    i--;
                }
            }
        }
    }

    /**
     * 释放所有灵魂
     */
    public void clear ()
    {
        save = true;
        souls.clear();
    }

    /**
     * 得到采集怪物灵魂的列表
     * 
     * @return
     */
    public ArrayList<MonsterSoul> getMonsterSoul ()
    {
        return souls;
    }

    /**
     * 是否有足够的指定灵魂ID和数量的灵魂
     * 
     * @param _soulID
     * @param _num
     * @return
     */
    public boolean enough (int _soulID, int _num)
    {
        int _count = 0;
        for (MonsterSoul s : souls)
        {
            if (s.soulID == _soulID)
            {
                _count += s.num;
            }
        }
        return _count >= _num;
    }

    /**
     * 得到指定灵魂ID的数量
     * 
     * @param _soulID
     * @return
     */
    public int getNumBySoulID (int _soulID)
    {
        int _count = 0;
        for (MonsterSoul s : souls)
        {
            if (s.soulID == _soulID)
            {
                _count += s.num;
            }
        }
        return _count;
    }

    /**
     * 是否已经学会指定炼制ID的技能
     * 
     * @param _refinedID
     * @return
     */
    public boolean isStudyedRefinedID (int _refinedID)
    {
        for (int _r : refinedSkillIDList)
        {
            if (_r == _refinedID)
                return true;
        }
        return false;
    }

    public final static byte MAX_LVL = 5;
}
