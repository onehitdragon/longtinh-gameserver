package hero.item.service;

import hero.item.Weapon;
import hero.item.detail.EGoodsTrait;
import hero.item.enhance.WeaponBloodyEnhance;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 WeaponRankUnit.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-11-18 下午01:58:06
 * @描述 ：武器排名单元
 */

public class WeaponRankUnit
{
    /**
     * 武器模板
     */
    public Weapon              weapon;

    /**
     * 拥有者姓名
     */
    public String              ownerName;

    /**
     * 综合分数
     */
    public double              score;

    /**
     * 普通强化
     */
    public byte[]              genericEnhance;

    /**
     * 存在封印
     */
    public boolean             existSeal;

    /**
     * 普通强化描述
     */
    public String              genericEnhanceDesc = "";

    /**
     * 血腥强化属性
     */
    public WeaponBloodyEnhance bloodyEnhance;

    /**
     * 血腥强化属性描述
     */
    public String              bloodyEnhanceDesc;

    /**
     * 构造
     * 
     * @param _weapon 武器模板
     * @param _ownerName 拥有者姓名
     * @param _genericEnhanceDesc 普通强化描述
     * @param _bloodyEnhanceDesc 血腥强化描述
     * @param _score 排名积分
     */
    public WeaponRankUnit(Weapon _weapon, String _ownerName,
            String _genericEnhanceDesc, String _bloodyEnhanceDesc,
            boolean _existSeal)
    {
        weapon = _weapon;
        ownerName = _ownerName;
        existSeal = _existSeal;
        genericEnhance = new byte[12];
        bloodyEnhance = new WeaponBloodyEnhance(_weapon.getPveEnhanceID(),
                _weapon.getPvpEnhanceID());

        if (!_genericEnhanceDesc.equals(""))
        {
            genericEnhanceDesc = _genericEnhanceDesc;
            String[] genericEnhanceDescData = _genericEnhanceDesc.split("#");

            for (int i = 0; i < genericEnhanceDescData.length; i++)
            {
                genericEnhance[i] = Byte.parseByte(genericEnhanceDescData[i]);
            }
        }

        score = calculateRankScore(weapon, genericEnhance, bloodyEnhance);
    }

    /**
     * 计算武器排名
     */
    private static double calculateRankScore (Weapon weapon,
            byte[] _genericEnhance, WeaponBloodyEnhance _bloodyEnhance)
    {
        double magicAttack = 0;

        if (weapon.getMagicDamage() != null)
        {
            magicAttack = (weapon.getMagicDamage().maxDamageValue 
            		+ weapon.getMagicDamage().minDamageValue) / 2D;
        }

        double weaponDps = (weapon.getMinPhysicsAttack()
                + weapon.getMaxPhysicsAttack() + magicAttack)
                / (2 * weapon.getImmobilityTime());

        double genericEnhanceLevle = 0;

        for (byte enhanceLevel : _genericEnhance)
        { 
            genericEnhanceLevle += enhanceLevel;
        }

        genericEnhanceLevle /= 4;
        
        return (Math.pow(genericEnhanceLevle + 1, 2) / (2 * (genericEnhanceLevle + 1)))
                * (0.15 * _bloodyEnhance.getPvpNumber() + _bloodyEnhance
                        .getPveNumber())
                * weapon.getNeedLevel()
                * (weapon.getTrait() == EGoodsTrait.YU_ZHI ? 2 : 3)
                * weaponDps
                / 1000;
    }
}
