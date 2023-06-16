package hero.item.message;

import hero.item.Armor;
import hero.item.EqGoods;
import hero.item.Weapon;
import hero.item.detail.EBodyPartOfEquipment;
import hero.player.HeroPlayer;
import hero.player.define.ESex;
import hero.player.service.PlayerConfig;
import hero.player.service.PlayerServiceImpl;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseClothesOrWeaponChange.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-18 下午01:51:36
 * @描述 ：
 */

public class ClothesOrWeaponChangeNotify extends AbsResponseMessage
{
    /**
     * 玩家ID
     */
    private HeroPlayer   player;

    /**
     * 装备类型
     */
    private EBodyPartOfEquipment  equipmentType;

    /**
     * 新的图片编号
     */
    private short imageID;
    /**
     * 装备动画ID
     */
    private short animationID;
    /**
     * 是否卸下装备
     */
    private boolean isUnload;

    /**
     * 强化等级
     */
    private byte  enhanceLevel;
    
    private EqGoods eqGood;
    
    private short enhancePNG;
    
    private short enhanceANU;
    
    /**
     * 装备换装通知
     * @param _player
     * @param _equipmentType
     * @param _imageID
     * @param _enhanceLevel
     * @param _eqGood
     * @param _isUnload
     */
    public ClothesOrWeaponChangeNotify(HeroPlayer _player, EBodyPartOfEquipment _equipmentType,
            short _imageID, short _animationID, byte _enhanceLevel , EqGoods _eqGood, 
            boolean _isUnload, short _enhancePNG, short _enhanceANU)
    {
    	player = _player;
        equipmentType = _equipmentType;
        imageID = _imageID;
        animationID = _animationID;
        enhanceLevel = _enhanceLevel;
        eqGood = _eqGood;
        isUnload = _isUnload;
        enhancePNG = _enhancePNG;
        enhanceANU = _enhanceANU;
    }

    @Override
    public int getPriority ()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        // TODO Auto-generated method stub
        yos.writeInt(player.getID());
        //edit by zhengl ; date: 2010-11-24 ; note: 直接从枚举里获得值;不同类型下发各自的新增属性
        yos.writeByte(equipmentType.value()); // 0=头部,1=胸部,4=武器
        yos.writeByte(isUnload);
        if (isUnload) {
        	//卸下衣服需要发裸体图片,卸下头盔与武器只需要发是否卸下即可.
	        if(equipmentType == EBodyPartOfEquipment.BOSOM) {
            	//add by zhengl; date: 2011-02-24; note: 客户端需要此值来做分等级展示图片以节约客户端内存
            	yos.writeShort(0);
	        	//add by zhengl ; date: 2011-01-23 ; note: 加动画
	        	yos.writeShort(PlayerServiceImpl.getInstance().getConfig()
						.getDefaultClothesImageID(player.getSex()));
	        	yos.writeShort(PlayerServiceImpl.getInstance().getConfig()
						.getDefaultClothesAnimation(player.getSex()));
	        }
		} else {
	        if(equipmentType == EBodyPartOfEquipment.WEAPON) {
	        	yos.writeShort( ((Weapon)eqGood).getLightID() );
	        	yos.writeShort( ((Weapon)eqGood).getLightAnimation() );
	        	yos.writeShort( ((Weapon)eqGood).getWeaponType().getID() );
	        } else {
	        	yos.writeByte( ((Armor)eqGood).getDistinguish() );
			}
	        //add by zhengl ; date: 2011-01-16 ; note: 头盔,武器,胸甲 要下发动画ID
	        if(equipmentType == EBodyPartOfEquipment.WEAPON 
	        		|| equipmentType == EBodyPartOfEquipment.BOSOM 
	        		|| equipmentType == EBodyPartOfEquipment.HEAD) {
            	//add by zhengl; date: 2011-02-24; note: 客户端需要此值来做分等级展示图片以节约客户端内存
	        	short equipLevel = 0;
	        	if (eqGood instanceof Armor) {
	        		equipLevel = (short)((Armor)eqGood).getNeedLevel();
				} else if (eqGood instanceof Weapon) {
					equipLevel = (short)((Weapon)eqGood).getNeedLevel();
				} 
            	yos.writeShort(equipLevel);
	        	yos.writeShort(imageID);
	        	yos.writeShort(animationID);
	        }
	        //add by zhengl; date: 2011-03-16; note: 强化的图片在此下发
			yos.writeShort(enhancePNG);
			yos.writeShort(enhanceANU);
		}

    }
}
