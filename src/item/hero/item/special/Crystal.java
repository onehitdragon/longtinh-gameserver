package hero.item.special;

import java.util.ArrayList;
import java.util.Arrays;

import yoyo.core.queue.ResponseMessageQueue;

import hero.log.service.LogServiceImpl;
import hero.item.SpecialGoods;
import hero.item.message.NotifyPopEnhanceUI;
import hero.item.service.GoodsServiceImpl;
import hero.player.HeroPlayer;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Crystal.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-8-6 下午02:07:29
 * @描述 ：水晶，用于强化装备
 */

public class Crystal extends SpecialGoods
{
    /**
     * 强化装备等级下限
     */
    private byte equipmentLevelLower;

    /**
     * 强化装备等级上限
     */
    private byte equipmentLevelLimit;
    
    /**
     * 是否最高镶嵌孔位所需宝石.
     */
    private boolean isUltimaNeed;
    
    /**水晶类型 0=打孔;1=镶嵌;2=剥离宝石*/
    private byte useType;
    
    //水晶编号,物品等级下限,物品等级上限,宝石品级,是否最后3孔所需
    
    /**
     * 宝石品级 0=初级,1=中级,2=高级,3=顶级
     */
    private byte crystalLevel;
    
    /**
     * 是否终极宝石(女娲石)
     */
    private boolean isUltimate;

    /**
     * 构造
     * 
     * @param _id
     * @param _stackNums
     */
    public Crystal(int _id, short _stackNums)
    {
        super(_id, _stackNums);
        useType = 1;
        //是否打孔石.
        for(int sid : STONE_PWEDOEATE_LIST){
        	if(_id == sid){
        		this.useType = 0;
        		break;
        	}
        }
        //是否剥离石
        for(int eid : STONE_WRECK_LIST){
        	if(_id == eid){
        		this.useType = 2;
        		break;
        	}
        }
        isUltimaNeed = false;
        for (int i = 0; i < CORRESPONDING_TARGET_GOURD_LIST.length; i++) {
        	if(_id == CORRESPONDING_TARGET_GOURD_LIST[i][0]) {
        		crystalLevel = (byte)CORRESPONDING_TARGET_GOURD_LIST[i][3];
        		if(CORRESPONDING_TARGET_GOURD_LIST[i][4] == 1) {
        			isUltimaNeed = true;
        		}
        	}
		}

        for (int[] equipmentLimit : CORRESPONDING_TARGET_GOURD_LIST)
        {
            if (equipmentLimit[0] == getID())
            {
            	this.useType = 1;
                equipmentLevelLower = (byte) equipmentLimit[1];
                equipmentLevelLimit = (byte) equipmentLimit[2];
                if(equipmentLimit[4] == 0) 
                {
                	isUltimaNeed = false;
                }
                else
                {
                	isUltimaNeed = true;
                }
                break;
            }
        }
    }

    @Override
    public void initDescription ()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isIOGoods ()
    {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public ESpecialGoodsType getType ()
    {
        // TODO Auto-generated method stub
        return ESpecialGoodsType.CRYSTAL;
    }
    /**
     * 水晶类型 0=打孔;1=镶嵌;2=剥离宝石
     * @return
     */
    public byte getUseType ()
    {
        // TODO Auto-generated method stub
        return this.useType;
    }
    /**
     * 是否最高镶嵌孔位所需宝石
     * @return
     */
    public boolean getIsUltimaNeed() 
    {
    	return isUltimaNeed;
    }
    
    /**
     * 返回宝石品级(品级决定强化成功率情况)
     * <p>
     * 现在有4个品级(碎裂,普通,闪光)
     * <p>
     * 0, 80.00% 20.00% 0.00% 
     * <p>
     * 1, 19.00% 80.00% 1.00% 
     * <p>
     * 2,  5.00% 92.00% 3.00% 
     * <p>
     * 3, 30.00% 60.00% 10.00%
     * @return
     */
    public byte getCrystalLevel()
    {
    	return crystalLevel;
    }
    
    public int getEnhanceOdds(int _random)
    {
    	int resultEnhance = 0;
    	int result = 0;
    	int a = GoodsServiceImpl.getInstance().getConfig().getSpecialConfig().odds_enhance_list[crystalLevel][0];
    	int b = GoodsServiceImpl.getInstance().getConfig().getSpecialConfig().odds_enhance_list[crystalLevel][1];
    	int c = GoodsServiceImpl.getInstance().getConfig().getSpecialConfig().odds_enhance_list[crystalLevel][2];
    	
    	int[] list = new int[3];
    	list[0] = a;
    	list[1] = b;
    	list[2] = c;
    	Arrays.sort(list);
    	
    	if (_random <= list[0]) {
    		result = list[0];
		} else if (_random <= list[1]) {
			result = list[1];
		} else {
			result = list[2];
		}
    	if(result == a) 
    	{
    		resultEnhance = 1;
    	} 
    	else if (result == b) 
    	{
    		resultEnhance = 2;
		} 
    	else 
		{
			resultEnhance = 3;
		}
    	return resultEnhance;
    }

    /**
     * 满足等级要求
     * 
     * @param _equipmentLevel 装备等级
     * @return
     */
    public boolean conformLevel (int _equipmentLevel)
    {
        if (_equipmentLevel >= equipmentLevelLower
                && _equipmentLevel <= equipmentLevelLimit)
        {
            return true;
        }

        return false;
    }

    /**
     * 可强化的装备等级区间对应列表（[水晶编号,物品等级下限,物品等级上限,宝石品级,是否最后3孔所需]）
     * 	
     */
    public static int[][] CORRESPONDING_TARGET_GOURD_LIST = { 
    	{340007, 1, 19, 0, 0 }, {340008, 1, 19, 1, 0 }, {340009, 1, 19, 2, 0 }, 
    	{340010, 20, 39, 0, 0 }, {340011, 20, 39, 1, 0 },{340012, 20, 39, 2, 0 },
    	{340013, 40, 59, 0, 0 }, {340014, 40, 59, 1, 0 },{340015, 40, 59, 2, 0 },
    	{340016, 60, 61, 0, 0 }, {340017, 60, 61, 1, 0 },{340018, 60, 61, 2, 0 },
    	{340019, 1, 120, 3, 1}
    	};
    /**打孔石列表*/
    public static int[] STONE_PWEDOEATE_LIST = {340001};
    
    /**剥离石列表*/
    public static int[] STONE_WRECK_LIST = {340002};

    @Override
    public boolean disappearImmediatelyAfterUse ()
    {
    	if(useType == 2){//剥离宝石使用后自动消失
    		return true;
    	}
        return false;
    }

    @Override
    public boolean beUse (HeroPlayer _player, Object _target, int _locationOfBag)
    {
        // TODO Auto-generated method stub
    	if(useType != 2){
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new NotifyPopEnhanceUI(getID(), _locationOfBag));
    	}
        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                _player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),getType().getDescription());
        return true;
    }
}
