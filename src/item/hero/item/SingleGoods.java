package hero.item;

import hero.player.HeroPlayer;
import hero.share.ME2GameObject;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SingleGoods.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-11-21 下午06:00:58
 * @描述 ：不会被改变属性的物品，与Equipment同属Goods子类
 */

public abstract class SingleGoods extends Goods
{
    /**
     * 是否可用
     */
    protected boolean useable;

    /**
     * 构造
     * 
     * @param _stackNums
     */
    public SingleGoods(short _stackNums)
    {
        super(_stackNums);
        // TODO Auto-generated constructor stub
    }

    /**
     * 设置是否可被使用
     * 
     * @return
     */
    public void setUseable ()
    {
        useable = true;
    }

    /**
     * 是否可使用
     * 
     * @return
     */
    public boolean useable ()
    {
        return useable;
    }

    /**
     * 被使用
     * 
     * @param _player 使用者
     * @param _target 作用目标
     * @return
     */
    public abstract boolean beUse (HeroPlayer _player, Object _target);

    /**
     * 获取非装备物品类型
     * 
     * @return
     */
    public abstract byte getSingleGoodsType ();

    /**
     * 药水
     */
    public final static byte TYPE_MEDICAMENT    = 1;

    /**
     * 材料
     */
    public final static byte TYPE_MATERIAL      = 2;

    /**
     * 任务道具
     */
    public final static byte TYPE_TASK_TOOL     = 3;

    /**
     * 特殊物品、其他物品
     */
    public final static byte TYPE_SPECIAL_GOODS = 4;
    
    /**
     * 宠物物品
     * 买的宠物也放在这里
     */
    public final static byte TYPE_PET_GOODS = 5;
}
