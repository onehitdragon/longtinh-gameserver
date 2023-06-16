package hero.fight.message;

import hero.fight.service.FightServiceImpl;
import hero.item.EquipmentInstance;
import hero.item.Weapon;
import hero.item.Weapon.EWeaponType;
import hero.player.HeroPlayer;
import hero.share.ME2GameObject;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GenericAttackViewNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-1-14 上午09:51:03
 * @描述 ：普通攻击动作视图通知
 */

public class GenericAttackViewNotify extends AbsResponseMessage
{
    /**
     * 攻击方对象类型
     */
    private byte attackerObjectType;

    /**
     * 攻击方对象ID
     */
    private int  attackerObjectID;

    /**
     * 目标对象类型
     */
    private byte targetObjectType;

    /**
     * 目标对象ID
     */
    private int  targetObjectID;
    
    private ME2GameObject attacker;

    /**
     * 构造
     * 
     * @param _attackerObjectType 攻击者对象类型
     * @param _attackerObjectID 攻击者对象编号
     * @param _targetObjectType 目标对象类型
     * @param _targetObjectID 目标对象编号
     */
    public GenericAttackViewNotify(byte _attackerObjectType,
            int _attackerObjectID, byte _targetObjectType, int _targetObjectID)
    {
        attackerObjectType = _attackerObjectType;
        attackerObjectID = _attackerObjectID;
        targetObjectType = _targetObjectType;
        targetObjectID = _targetObjectID;
    }
    
    public GenericAttackViewNotify(byte _attackerObjectType,
            int _attackerObjectID, byte _targetObjectType, int _targetObjectID, ME2GameObject _attacker)
    {
        attackerObjectType = _attackerObjectType;
        attackerObjectID = _attackerObjectID;
        targetObjectType = _targetObjectType;
        targetObjectID = _targetObjectID;
        attacker = _attacker;
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
        yos.writeByte(attackerObjectType);
        yos.writeInt(attackerObjectID);
        yos.writeByte(targetObjectType);
        yos.writeInt(targetObjectID);
        //add by zhengl; date: 2011-02-21; note: 添加攻击动作下发以外的攻击承受动作下发
        if (attacker != null && attacker instanceof HeroPlayer) {
        	HeroPlayer player = (HeroPlayer)attacker;
        	EquipmentInstance weapon = player.getBodyWear().getWeapon();
        	if(weapon != null) {
            	EWeaponType wType = ((Weapon)weapon.getArchetype()).getWeaponType();
            	yos.writeShort(FightServiceImpl.getInstance().getFightTarget(wType)[0]);
            	yos.writeShort(FightServiceImpl.getInstance().getFightTarget(wType)[1]);
        	} else {
    			yos.writeShort(-1);
    			yos.writeShort(-1);
    		}
		} else {
			yos.writeShort(-1);
			yos.writeShort(-1);
		}
    }
}
