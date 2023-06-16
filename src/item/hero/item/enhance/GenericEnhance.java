package hero.item.enhance;

import hero.item.Equipment;
import hero.item.detail.EBodyPartOfEquipment;
import hero.item.service.GoodsServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GenericEnhance.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-27 下午01:40:09
 * @描述 ：一般情况
 */

public class GenericEnhance
{
    /**
     * 12个细节{0=未打孔,0=未镶嵌}
     */
    public byte[][] detail = {
    		{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, 
    		{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0} };
    
    /**
     * 总提升值
     */
    public float sumModulus;

    /**
     * 构造
     * 
     * @param _equipmentType 装备类型值，在装备类中定义
     */
    public GenericEnhance(int _equipmentType)
    {
    	this.initModulus();
    }
    
    /**
     * 通过数据库存储的强化字符串以及身体部位,返回该对象对应的强化光效
     * 
     * @param _DBString
     * @param _body
     * @return
     */
    public short[] getFlashByDBString(String _DBString, EBodyPartOfEquipment _body)
    {
    	short[] result = new short[2];
		String[] genericEnhanceDataDesc = _DBString.split("#");
		//调整数据库中,强化字段的规则;
		//-1=无孔,0=空孔,1=碎裂型强化,2=成功型强化,3=闪光型强化
		for (int i = 0; i < genericEnhanceDataDesc.length; i++) {
			if( (!genericEnhanceDataDesc[i].equals("")) && genericEnhanceDataDesc[i] != null ) {
				byte enhance = Byte.parseByte(genericEnhanceDataDesc[i]);
				if(enhance == -1) {
					this.initAllGrid(i,false,(byte)0);
				} else {
					this.initAllGrid(i,true,enhance);
				}
			}
		}
		if(_body == EBodyPartOfEquipment.WEAPON) {
			result = this.getFlashView();
		} else if (_body == EBodyPartOfEquipment.BOSOM) {
			result = this.getArmorFlashView();
		}
    	return result;
    }
    
    /**
     * 初始化当前装备总强化率
     */
    public void initModulus(){
    	try {
    		sumModulus = 0.0F;
    		for(int i = 0;i < detail.length;i++)
    		{
    			if(detail[i][0] > 0){
    				if(detail[i][1] == 3){
    					//必加的基础属性
    					sumModulus += this.getBounsValue(i, detail[i][1]);
    					//可能附加1(自身闪光镶嵌并且不是第1颗)
    					if(i >= 1){
    						sumModulus += this.getBounsValue(i-1, detail[i-1][1]);
    					}
    					//可能附加2(自身闪光镶嵌并且之前2颗全部闪光)
    					if(i >= 2 && detail[i-1][1] == 3 && detail[i-2][1] == 3){
    						sumModulus += this.getBounsValue(i-2, detail[i-2][1]);
    					}
    				} else {
    					sumModulus += this.getBounsValue(i, detail[i][1]);
    				}
    			}
    		}
    		//for end
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * 获取单个孔的加成属性值
     * @param position 孔的位置
     * @return
     */
    public float getHoleModules(int position){
        float value = 0F;
        if(detail[position][0] > 0){
            if(detail[position][1] == 3){
                value = getBounsValue(position,detail[position][1]);
                if(position >= 1){
                    value += getBounsValue(position,detail[position-1][1]);
                }
                if(position >= 2 && detail[position-1][1] == 3 && detail[position-2][1] == 3){
                    value += getBounsValue(position,detail[position-2][1]);
                }
            }else{
                value = getBounsValue(position,detail[position][1]);
            }
        }
        return value;
    }
    
    /**
     * 根据镶嵌位置与镶嵌等级获得附加值
     * @param position
     * @param level
     * @return
     */
    private float getBounsValue(int position, int level){
    	float value = 0.0F;
    	if(position < GenericEnhance.HIGH_SIDE){
    		if(level == 1){
    			value = GenericEnhance.BONUS_CHIP_MODULUS;
    		} else if (level == 2) {
    			value = GenericEnhance.BONUS_SUCCESS_MODULUS;
			}else if (level == 3) {
				value = GenericEnhance.BONUS_FLASH_MODULUS;
			}
    	}else {
    		if(level == 1){
    			value = GenericEnhance.BONUS_CHIP_MODULUS_HIGH;
    		} else if (level == 2) {
    			value = GenericEnhance.BONUS_SUCCESS_MODULUS_HIGH;
			}else if (level == 3) {
				value = GenericEnhance.BONUS_FLASH_MODULUS_HIGH;
			}
		}
    	return value;
    }

    /**
     * 直接设置强化细节
     * 
     * @param _detailIndex 细节部位(第几个宝石插孔)
     * @param _enhanceLevel 细节强化等级(0=空孔,1=碎裂型强化,2=成功型强化,3=闪光型强化)
     * @return
     */
    public void setDetailEnhance (int _detailIndex, byte _enhanceLevel)
    {
        if (_detailIndex >= 0 && _detailIndex < detail.length)
        {
        	detail[_detailIndex][0] = 1;//设置为可镶嵌
            detail[_detailIndex][1] = _enhanceLevel;
        }
    }
    
    /**
     * 初始化各细节
     * @param _detailIndex
     * @param isActive
     * @param _enhanceLevel
     */
    public void initAllGrid (int _detailIndex, boolean isActive, byte _enhanceLevel)
    {
        if (_detailIndex >= 0 && _detailIndex < detail.length)
        {
        	if(!isActive) {
            	detail[_detailIndex][0] = 0;
                detail[_detailIndex][1] = 0;
        	} else {
            	detail[_detailIndex][0] = 1;
                detail[_detailIndex][1] = _enhanceLevel;
			}

        }
    }
    /**
     * 获得当前位置的宝石的级别.
     * @param _detailIndex
     * @return
     */
    public byte getJewelLevel (int _detailIndex) {
    	byte level = 0;
    	level = detail[_detailIndex][1];
    	return level;
    }
    
    /**
     * 摧毁宝石
     * @param _detailIndex 细节部位(第几个宝石插孔)
     * @return true=摧毁成功;false=无法摧毁
     */
    public boolean resetEnhanceLevel (int _detailIndex){
    	boolean result = false;
    	if(detail[_detailIndex][1] == 0){
    		return result;
    	}
        if (_detailIndex >= 0 && _detailIndex < detail.length && detail[_detailIndex][0] == 1)
        {
            detail[_detailIndex][1] = 0;
            result = true;
        }
        this.initModulus();
    	return result;
    }
    
    /**
     * 该位置是否有孔.
     * @param _index
     * @return
     */
    public boolean haveHole(int _index)
    {
    	boolean have = false;
        if (_index >= 0 && detail[_index][0] > 0)
        {
            return have = true;
        }
    	return have;
    }
    
    /**
     * 该位置是否已经镶嵌了宝石.
     * @param _index
     * @return
     */
    public boolean haveJewel(int _index)
    {
    	boolean have = false;
        if (_index >= 0 && detail[_index][0] > 0 && detail[_index][1] > 0)
        {
            return have = true;
        }
    	return have;
    }

    /**
     * 玩家强化升级（1级）
     * 
     * @param _detailIndex
     * @return
     */
    public boolean addDetailEnhance (int _detailIndex, byte level)
    {
        if (_detailIndex >= 0 && _detailIndex < detail.length)
        {
            detail[_detailIndex][1] = level;
            this.initModulus();
            return true;
        }

        return false;
    }
    /**
     * 装备打孔
     * @return
     */
    public boolean addPerforate ()
    {
    	boolean result = false;
        for (int i = 0; i < detail.length; i++)
        {
            if (detail[i][0] == 0){
            	result = true;
            	detail[i][0] = 1;
            	return result;
            }
        }

        return result;
    }
    /**
     * 获取孔数.
     * @return
     */
    public byte getHole(){
    	byte hole = 0;
    	for (byte i = 0; i < detail.length; i ++)
        {
        	if(detail[i][0] == 1){
        		hole += 1;
        	}
        }
    	return hole;
    }

    /**
     * 获取等级
     * 
     * @return
     */
    public byte getLevel ()
    {
        byte levelCount = 0;

        for (byte i = 0; i < detail.length; i ++)
        {
        	if(detail[i][1] > 0){
        		levelCount += 1;
        	}
        }

        return levelCount;
    }
    
    /**
     * 获得闪光宝石数量
     * @return
     */
    public byte getFlash ()
    {
        byte levelCount = 0;

        for (byte i = 0; i < detail.length; i ++)
        {
        	if(detail[i][1] == 3){
        		levelCount += 1;
        	}
        }

        return levelCount;
    }
    /**
     * 获取可强化的部位,没有则返回-1
     * @return
     */
    public byte getIndex ()
    {
        byte index = -1;

        for (byte i = 0; i < detail.length; i ++)
        {
            if(detail[i][0] != 0 && detail[i][1] == 0){
            	index = i;
            	break;
            }
        }

        return index;
    }
    

    /**
     * 获取基本属性加成系数（生命值、魔法值、力量、耐力、敏捷、智力、精神、幸运）
     * 
     * @return
     */
    public float getBasicModulus ()
    {
    	return this.sumModulus;
    }

    /**
     * 获取防御属性加成系数（护甲、闪避以及五种抗性）
     * 
     * @return
     */
    public float getDefenseModulus ()
    {
    	return this.sumModulus;
    }

    /**
     * 获取辅助属性加成系数（物理暴击、魔法暴击、命中）
     * 
     * @return
     */
    public float getAdjuvantModulus ()
    {
    	return this.sumModulus;
    }

    /**
     * 获取攻击属性加成系数（物理攻击力和魔法攻击力）
     * 
     * @return
     */
    public float getAttackModulus ()
    {
    	return this.sumModulus;
    }
    
    public float getSumModulus ()
    {
    	return this.sumModulus;
    }
    
    /**
     * 返回提升百分比的描述字符串
     * @return
     */
    public String getUpString () 
    {
    	String up = (this.sumModulus * 100) + "%";
    	return up;
    }
    
    /**
     * 返回完整的提升百分比的描述字符串
     * @return
     */
    public String getUpEndString () 
    {
    	String up = "";
    	String temp = "";
    	if(this.sumModulus > 0.0)
    	{
    		temp = String.valueOf(this.sumModulus * 100);
    		temp = temp.substring(0, temp.indexOf(".")) + "%";
    		up = GoodsServiceImpl.getInstance().getConfig().describe_enhance_string + temp;
    	}
    	return up;
    }
    
    /**
     * 返回单个宝石提升百分比的描述字符串
     * @return
     */
    public String getUpString (int _index) 
    {
    	String up = "";
    	up = (this.getHoleModules(_index) * 100) + "%";
    	return up;
    }
    
    /**
     * <p>返回当前装备发光的展示效果</p>
     * [0]=png;[1]=anu
     * @return
     */
    public short[] getFlashView()
    {
    	short[] view = new short[2];
    	view = GoodsServiceImpl.getInstance().getFlashView(getFlashLevel());
    	return view;
    }
    /**
     * <p>返回当前非武器装备发光的展示效果</p>
     * [0]=png;[1]=anu
     * @return
     */
    public short[] getArmorFlashView()
    {
    	short[] view = new short[2];
    	view = GoodsServiceImpl.getInstance().getArmorFlashView(getFlashLevel());
    	return view;
    }
    
    /**
     * <p>返回指定孔位的宝石的UI展示效果</p>
     * [0]=png;[1]=anu
     * @param _level
     * @return
     */
    public short[] getYetSetJewel(int _index)
    {
    	short[] view = {-1, -1};
    	byte level = detail[_index][1];
    	view = GoodsServiceImpl.getInstance().getYetSetJewel(level);
    	return view;
    }
    
    /**
     * 返回闪光等级.
     * @return
     */
    public byte getFlashLevel ()
    {
    	byte result = 0;
    	if(sumModulus >= 0.21F && sumModulus <= 0.40F)
    	{
    		result = 1;
    	}
    	else if(sumModulus >= 0.41F && sumModulus <= 0.74F)
    	{
    		result = 2;
    	}
    	else if(sumModulus >= 0.75F && sumModulus <= 1.44F)
    	{
    		result = 3;
    	}
    	else if(sumModulus >= 1.45F && sumModulus <= 1.91F)
    	{
    		result = 4;
    	}
    	else if(sumModulus >= 1.92F && sumModulus <= 2.50F)
    	{
    		result = 5;
    	}
    	else if(sumModulus >= 2.51F)
    	{
    		result = 6;
    	}
    	return result;
    }
    
    
    /**碎裂加成系数*/
    public static final float BONUS_CHIP_MODULUS = 0.03F;
    /**high碎裂加成系数*/
    public static final float BONUS_CHIP_MODULUS_HIGH = 0.05F;
    
    /**成功加成系数*/
    public static final float BONUS_SUCCESS_MODULUS = 0.05F;
    /**high成功加成系数*/
    public static final float BONUS_SUCCESS_MODULUS_HIGH = 0.10F;
    
    /**闪光加成系数*/
    public static final float BONUS_FLASH_MODULUS = 0.06F;
    /** high闪光加成系数*/
    public static final float BONUS_FLASH_MODULUS_HIGH = 0.15F;
    
    /**属性提升临界点*/
    public static final byte HIGH_SIDE = 9;
}
