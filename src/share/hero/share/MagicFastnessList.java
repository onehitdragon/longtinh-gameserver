package hero.share;

import javolution.util.FastMap;

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
 *      Description:魔法抗性列表
 * </pre>
 */

public class MagicFastnessList
{
    /**
     * 魔法抗性值映射表
     */
    private FastMap<EMagic, Integer> magicMap;

    /**
     * 构造
     */
    public MagicFastnessList()
    {
        magicMap = new FastMap<EMagic, Integer>(5);
        magicMap.put(EMagic.SANCTITY, 0);
        magicMap.put(EMagic.UMBRA, 0);
        magicMap.put(EMagic.WATER, 0);
        magicMap.put(EMagic.FIRE, 0);
        magicMap.put(EMagic.SOIL, 0);
        magicMap.put(EMagic.ALL, 0);/*add by zhengl; date: 2011-04-21; note: 添加全魔法*/
    }

    /**
     * 构造
     */
    public MagicFastnessList(int _value)
    {
        magicMap = new FastMap<EMagic, Integer>(5);
        magicMap.put(EMagic.SANCTITY, _value);
        magicMap.put(EMagic.UMBRA, _value);
        magicMap.put(EMagic.WATER, _value);
        magicMap.put(EMagic.FIRE, _value);
        magicMap.put(EMagic.SOIL, _value);
        magicMap.put(EMagic.ALL, _value);/*add by zhengl; date: 2011-04-21; note: 添加全魔法*/
    }

    /**
     * 增加指定魔法的抗性值
     * 
     * @param _magic
     * @param _value
     * @return 增加后的该魔法抗性值
     */
    public int add (EMagic _magic, int _value)
    {
        int value = magicMap.remove(_magic);

        value += _value;

        if (value < 0)
            value = 0;

        magicMap.put(_magic, value);

        return value;
    }

    /**
     * 增加全魔法抗性
     * 
     * @param _magicFastList
     * @return 增加后的全魔法抗性值
     */
    public int[] add (MagicFastnessList _magicFastList)
    {
        int[] currentValue = null;

        if (null != _magicFastList)
        {
            currentValue = new int[5];

            currentValue[0] = _magicFastList
                    .getEMagicFastnessValue(EMagic.SANCTITY);
            if (currentValue[0] != 0)
            {
                currentValue[0] = add(EMagic.SANCTITY, currentValue[0]);
            }

            currentValue[1] = _magicFastList
                    .getEMagicFastnessValue(EMagic.UMBRA);
            if (currentValue[1] != 0)
            {
                currentValue[1] = add(EMagic.UMBRA, currentValue[1]);
            }

            currentValue[2] = _magicFastList
                    .getEMagicFastnessValue(EMagic.WATER);
            if (currentValue[2] != 0)
            {
                currentValue[2] = add(EMagic.WATER, currentValue[2]);
            }

            currentValue[3] = _magicFastList
                    .getEMagicFastnessValue(EMagic.FIRE);
            if (currentValue[3] != 0)
            {
                currentValue[3] = add(EMagic.FIRE, currentValue[3]);
            }

            currentValue[4] = _magicFastList
                    .getEMagicFastnessValue(EMagic.SOIL);
            if (currentValue[4] != 0)
            {
                currentValue[4] = add(EMagic.SOIL, currentValue[4]);
            }
        }

        return currentValue;
    }

    /**
     * 增加百分比全魔法抗性
     * 
     * @param _magicFastList 增加的魔法抗性列表
     * @param _modulus 增加的魔法抗性列表额外再增加的百分比系数
     * @return 增加后的全魔法抗性值
     */
    public int[] add (MagicFastnessList _magicFastList, float _modulus)
    {
        int[] currentValue = null;

        if (_modulus > 0 && null != _magicFastList)
        {
            currentValue = new int[6];

            currentValue[0] = (int) (_magicFastList.getEMagicFastnessValue(EMagic.SANCTITY) * _modulus);
            if (currentValue[0] != 0)
            {
                currentValue[0] = add(EMagic.SANCTITY, currentValue[0]);
            }

            currentValue[1] = (int) (_magicFastList
                    .getEMagicFastnessValue(EMagic.UMBRA) * _modulus);
            if (currentValue[1] != 0)
            {
                currentValue[1] = add(EMagic.UMBRA, currentValue[1]);
            }

            currentValue[2] = (int) (_magicFastList
                    .getEMagicFastnessValue(EMagic.WATER) * _modulus);
            if (currentValue[2] != 0)
            {
                currentValue[2] = add(EMagic.WATER, currentValue[2]);
            }

            currentValue[3] = (int) (_magicFastList
                    .getEMagicFastnessValue(EMagic.FIRE) * _modulus);
            if (currentValue[3] != 0)
            {
                currentValue[3] = add(EMagic.FIRE, currentValue[3]);
            }

            currentValue[4] = (int) (_magicFastList
                    .getEMagicFastnessValue(EMagic.SOIL) * _modulus);
            if (currentValue[4] != 0)
            {
                currentValue[4] = add(EMagic.SOIL, currentValue[4]);
            }
            //add by zhengl; date: 2011-04-21; note: 添加ALL类型魔法
            currentValue[5] = (int) (_magicFastList.getEMagicFastnessValue(EMagic.ALL) * _modulus);
            if (currentValue[5] != 0)
            {
                currentValue[5] = add(EMagic.ALL, currentValue[5]);
            }
        }

        return currentValue;
    }

    /**
     * 重置指定魔法抗性值
     * 
     * @param _magic
     * @param _value
     * @return 增加后的该魔法伤害值
     */
    public void reset (EMagic _magic, int _value)
    {
        magicMap.put(_magic, _value);
    }

    /**
     * 重置全魔法抗性值
     * 
     * @param _magicFastList
     */
    public void reset (MagicFastnessList _magicFastList)
    {
        if (null != _magicFastList)
        {
            magicMap.put(EMagic.SANCTITY, _magicFastList.getEMagicFastnessValue(EMagic.SANCTITY));
            magicMap.put(EMagic.UMBRA, _magicFastList.getEMagicFastnessValue(EMagic.UMBRA));
            magicMap.put(EMagic.WATER, _magicFastList.getEMagicFastnessValue(EMagic.WATER));
            magicMap.put(EMagic.FIRE, _magicFastList.getEMagicFastnessValue(EMagic.FIRE));
            magicMap.put(EMagic.SOIL, _magicFastList.getEMagicFastnessValue(EMagic.SOIL));
            magicMap.put(EMagic.ALL, _magicFastList.getEMagicFastnessValue(EMagic.ALL));
        }
    }

    /**
     * 重置
     */
    public void clear ()
    {
        magicMap.put(EMagic.SANCTITY, 0);
        magicMap.put(EMagic.UMBRA, 0);
        magicMap.put(EMagic.WATER, 0);
        magicMap.put(EMagic.FIRE, 0);
        magicMap.put(EMagic.SOIL, 0);
        magicMap.put(EMagic.ALL, 0);
    }

    /**
     * 降低全魔法抗性
     * 
     * @param _magicFastList
     * @return 增加后的全魔法抗性值
     */
    public int[] reduce (MagicFastnessList _magicFastList)
    {
        int[] currentValue = null;

        if (null != _magicFastList)
        {
            currentValue = new int[6];

            currentValue[0] = add(EMagic.SANCTITY, -_magicFastList
                    .getEMagicFastnessValue(EMagic.SANCTITY));
            currentValue[1] = add(EMagic.UMBRA, -_magicFastList
                    .getEMagicFastnessValue(EMagic.UMBRA));
            currentValue[2] = add(EMagic.WATER, -_magicFastList
                    .getEMagicFastnessValue(EMagic.WATER));
            currentValue[3] = add(EMagic.FIRE, -_magicFastList
                    .getEMagicFastnessValue(EMagic.FIRE));
            currentValue[4] = add(EMagic.SOIL, -_magicFastList
                    .getEMagicFastnessValue(EMagic.SOIL));
            currentValue[5] = add(EMagic.ALL, -_magicFastList
                    .getEMagicFastnessValue(EMagic.ALL));
        }

        return currentValue;
    }

    /**
     * 返回特定魔法的抗性
     * 
     * @param _magic
     * @return
     */
    public int getEMagicFastnessValue (EMagic _magic)
    {
    	int value = 0;
    	if (_magic != null) 
    	{
    		value = magicMap.get(_magic);
		}
    	else 
    	{
    		System.out.println("传入魔法属性参数为NULL,这样的情况应该去排查技能表格");
    		//为容错而采取默认火魔法属性
    		value = EMagic.FIRE.getID();
		}
    	return value;
    }
}
