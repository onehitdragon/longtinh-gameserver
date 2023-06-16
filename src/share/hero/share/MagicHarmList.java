package hero.share;

import java.util.HashMap;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-10-14 上午10:29:27
 * 
 * <pre>
 *      Description:魔法伤害列表
 * </pre>
 */

public class MagicHarmList
{
    /**
     * 魔法伤害值映射表
     */
    private HashMap<EMagic, Float> magicMap;

    /**
     * 构造
     */
    public MagicHarmList()
    {
        magicMap = new HashMap<EMagic, Float>(5);
        magicMap.put(EMagic.SANCTITY, 0F);
        magicMap.put(EMagic.UMBRA, 0F);
        magicMap.put(EMagic.WATER, 0F);
        magicMap.put(EMagic.FIRE, 0F);
        magicMap.put(EMagic.SOIL, 0F);
        //add by zhengl; date: 2011-03-04; note: 添加全系魔法伤害属性
        magicMap.put(EMagic.ALL, 0F);
    }

    /**
     * 构造
     */
    public MagicHarmList(float _value)
    {
        magicMap = new HashMap<EMagic, Float>(5);
        magicMap.put(EMagic.SANCTITY, _value);
        magicMap.put(EMagic.UMBRA, _value);
        magicMap.put(EMagic.WATER, _value);
        magicMap.put(EMagic.FIRE, _value);
        magicMap.put(EMagic.SOIL, _value);
        //add by zhengl; date: 2011-03-04; note: 添加全系魔法伤害属性
        magicMap.put(EMagic.ALL, _value);
    }

    /**
     * 增加指定魔法的伤害值
     * 
     * @param _magic
     * @param _value
     * @return 增加后的该魔法伤害值
     */
    public float add (EMagic _magic, float _value)
    {
        Float value = magicMap.remove(_magic);

        value += _value;

        magicMap.put(_magic, value);

        return value;
    }

    /**
     * 重置指定魔法的伤害值
     * 
     * @param _magic
     * @param _value
     * @return 增加后的该魔法伤害值
     */
    public void reset (EMagic _magic, float _value)
    {
        magicMap.put(_magic, _value);
    }
    
    /**
     * 重置魔法伤害后附加ALL类型伤害
     * <p>
     * 
     * @param _magic
     * @param _value
     */
    public void resetByInte(EMagic _magic, float _value)
    {
    	float value = magicMap.get(EMagic.ALL) + _value;
    	magicMap.put(_magic, value);
    }

    /**
     * 重置全魔法伤害
     * 
     * @param _magicFastList
     */
    public void reset (MagicHarmList _magicFastList)
    {
        if (null != _magicFastList)
        {
        	//edit by zhengl; date: 2011-05-17; note: 魔法伤害增加的时候故意带入魔法伤害+ALL伤害的计算
            reset(EMagic.SANCTITY, _magicFastList.getEMagicHarmValue(EMagic.SANCTITY));
            reset(EMagic.UMBRA, _magicFastList.getEMagicHarmValue(EMagic.UMBRA));
            reset(EMagic.WATER, _magicFastList.getEMagicHarmValue(EMagic.WATER));
            reset(EMagic.FIRE, _magicFastList.getEMagicHarmValue(EMagic.FIRE));
            reset(EMagic.SOIL, _magicFastList.getEMagicHarmValue(EMagic.SOIL));
            //add by zhengl; date: 2011-03-04; note: 添加全系魔法伤害属性
            reset(EMagic.ALL, _magicFastList.getEMagicHarmValue(EMagic.ALL));
        }
    }

    /**
     * 重置
     */
    public void clear ()
    {
        magicMap.put(EMagic.SANCTITY, 0F);
        magicMap.put(EMagic.UMBRA, 0F);
        magicMap.put(EMagic.WATER, 0F);
        magicMap.put(EMagic.FIRE, 0F);
        magicMap.put(EMagic.SOIL, 0F);
        //add by zhengl; date: 2011-03-04; note: 添加全系魔法伤害属性
        magicMap.put(EMagic.ALL, 0F);
    }

    /**
     * 增加全魔法伤害
     * 
     * @param _magicFastList
     * @return 增加后的全魔法伤害值
     */
    public float[] add (MagicHarmList _magicHarmList)
    {
        float[] currentValue = null;

        if (null != _magicHarmList)
        {
            currentValue = new float[5];
            //edit by zhengl; date: 2011-05-17; note: 魔法伤害增加的时候故意带入魔法伤害+ALL伤害的计算
            currentValue[0] = add(EMagic.SANCTITY, _magicHarmList
            		.getEMagicHarmValue(EMagic.SANCTITY));
            currentValue[1] = add(EMagic.UMBRA, _magicHarmList
                    .getEMagicHarmValue(EMagic.UMBRA));
            currentValue[2] = add(EMagic.WATER, _magicHarmList
                    .getEMagicHarmValue(EMagic.WATER));
            currentValue[3] = add(EMagic.FIRE, _magicHarmList
                    .getEMagicHarmValue(EMagic.FIRE));
            currentValue[4] = add(EMagic.SOIL, _magicHarmList
                    .getEMagicHarmValue(EMagic.SOIL));
        }

        return currentValue;
    }

    /**
     * 返回特定魔法的伤害值
     * 
     * @param _magic
     * @return
     */
    public float getEMagicHarmValue (EMagic _magic)
    {
    	//eidt by zhengl; date: 2011-03-04; note: 返回伤害值附加了全属性伤害.
    	float harm = 1F;
    	if (_magic == null) 
    	{
    		System.out.println("传入魔法属性参数为NULL,这样的情况应该去排查技能表格");
    		_magic = EMagic.FIRE;
		}
    	if (_magic == EMagic.ALL) 
    	{
    		harm = magicMap.get(EMagic.ALL);
		}
    	else 
    	{
    		harm = magicMap.get(_magic) + magicMap.get(EMagic.ALL);
		}
        return harm;
    }
}
