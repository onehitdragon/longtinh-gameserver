package hero.item.enhance;

import hero.effect.detail.TouchEffect;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 WeaponBloodyEnhance.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-10-15 下午03:01:50
 * @描述 ：武器血腥强化（杀死BOSS和杀死玩家）
 */

public class WeaponBloodyEnhance
{
    /**
     * 宿主武器屠魔数据编号
     */
    private int         hostPveDataID;

    /**
     * 宿主武器杀戮数据编号
     */
    private int         hostPvpDataID;

    /**
     * 屠魔数量
     */
    private int         pveNumber;

    /**
     * 升级到下一级需要的屠魔数量
     */
    private int         pveUpgradeNumber;

    /**
     * 屠魔等级
     */
    private byte        pveLevel;

    /**
     * 屠魔持续性增益效果
     */
    private TouchEffect pveBuff;

    /**
     * 杀戮数量
     */
    private int         pvpNumber;

    /**
     * 升级到下一级需要的杀戮数量
     */
    private int         pvpUpgradeNumber;

    /**
     * 杀戮等级
     */
    private byte        pvpLevel;

    /**
     * 杀戮持续性增益效果
     */
    private TouchEffect pvpBuff;

    /**
     * 构造
     * 
     * @param _hostPveDataID 宿主武器屠魔数据编号
     * @param _hostPvpDataID 宿主武器屠杀戮据编号
     */
    public WeaponBloodyEnhance(int _hostPveDataID, int _hostPvpDataID)
    {
        hostPveDataID = _hostPveDataID;
        hostPvpDataID = _hostPvpDataID;

        pveUpgradeNumber = BASE_NUMBER_OF_PVE;
        pvpUpgradeNumber = BASE_NUMBER_OF_PVP;
    }

    /**
     * 添加屠魔数量
     * 
     * @return 屠魔等级是否发生变化
     */
    public boolean addPveNumber ()
    {
        pveNumber++;

        if (pveLevel < MAX_LEVEL)
        {
            int nextLevelNeedNumber = 0;
            byte oldPveLevel = 0;

            for (byte level = 1; level <= MAX_LEVEL; level++)
            {
                nextLevelNeedNumber += level * level * BASE_NUMBER_OF_PVE;

                if (pveNumber >= nextLevelNeedNumber)
                {
                    pveLevel = level;
                }
                else
                {
                    pveUpgradeNumber = nextLevelNeedNumber;

                    break;
                }
            }

            if (pveLevel != oldPveLevel)
            {
                pveBuff = EnhanceService.getInstance().getEffect(hostPveDataID,
                        pveLevel);

                return true;
            }
        }

        return false;
    }

    /**
     * 获取屠魔数量
     * 
     * @return
     */
    public int getPveNumber ()
    {
        return pveNumber;
    }

    /**
     * 获取下一级需要达到的屠魔数量
     * 
     * @return
     */
    public int getPveUpgradeNumber ()
    {
        return pveUpgradeNumber;
    }

    /**
     * 设置屠魔数量
     * 
     * @param _pveNumber
     */
    public void setPveNumber (int _pveNumber)
    {
        pveNumber = _pveNumber;

        if (pveNumber > 0)
        {
            int nextLevelNeedNumber = 0;

            for (byte level = 1; level <= MAX_LEVEL; level++)
            {
                nextLevelNeedNumber += level * level * BASE_NUMBER_OF_PVE;

                if (pveNumber >= nextLevelNeedNumber)
                {
                    pveLevel = level;
                }
                else
                {
                    pveUpgradeNumber = nextLevelNeedNumber;

                    break;
                }
            }

            if (pveLevel > 0)
            {
                pveBuff = EnhanceService.getInstance().getEffect(hostPveDataID,
                        pveLevel);
            }
        }
        else
        {
            pveUpgradeNumber = BASE_NUMBER_OF_PVE;
        }
    }

    /**
     * 获取屠魔等级
     * 
     * @param _pveLevel
     */
    public byte getPveLevel ()
    {
        return pveLevel;
    }

    /**
     * 添加杀戮数量
     * 
     * @return 杀戮等级是否发生变化
     */
    public boolean addPvpNumber ()
    {
        pvpNumber++;

        if (pvpLevel < MAX_LEVEL)
        {
            int nextLevelNeedNumber = 0;
            byte oldPvpLevel = pvpLevel;

            for (byte level = 1; level <= MAX_LEVEL; level++)
            {
                nextLevelNeedNumber += level * level * BASE_NUMBER_OF_PVP;

                if (pvpNumber >= nextLevelNeedNumber)
                {
                    pvpLevel = level;
                }
                else
                {
                    pvpUpgradeNumber = nextLevelNeedNumber;

                    break;
                }
            }

            if (pvpLevel != oldPvpLevel)
            {
                pvpBuff = EnhanceService.getInstance().getEffect(hostPvpDataID,
                        pvpLevel);

                return true;
            }
        }

        return false;
    }

    /**
     * 获取杀戮数量
     * 
     * @return
     */
    public int getPvpNumber ()
    {
        return pvpNumber;
    }

    /**
     * 获取下一级需要达到的杀戮数量
     * 
     * @return
     */
    public int getPvpUpgradeNumber ()
    {
        return pvpUpgradeNumber;
    }

    /**
     * 设置杀戮数量
     * 
     * @param _pvpNumber
     */
    public void setPvpNumber (int _pvpNumber)
    {
        pvpNumber = _pvpNumber;

        if (pveNumber > 0)
        {
            int nextLevelNeedNumber = 0;

            for (byte level = 1; level <= MAX_LEVEL; level++)
            {
                nextLevelNeedNumber += level * level * BASE_NUMBER_OF_PVP;

                if (pvpNumber >= nextLevelNeedNumber)
                {
                    pvpLevel = level;
                }
                else
                {
                    pvpUpgradeNumber = nextLevelNeedNumber;

                    break;
                }
            }

            if (pvpLevel > 0)
            {
                pvpBuff = EnhanceService.getInstance().getEffect(hostPvpDataID,
                        pvpLevel);
            }
        }
        else
        {
            pvpUpgradeNumber = BASE_NUMBER_OF_PVP;
        }
    }

    /**
     * 获取杀戮等级
     * 
     * @param _pvpLevel
     */
    public byte getPvpLevel ()
    {
        return pvpLevel;
    }

    /**
     * 获取触发型Pve效果
     * 
     * @return
     */
    public TouchEffect getPveBuff ()
    {
        return pveBuff;
    }

    /**
     * 获取触发型Pvp效果
     * 
     * @return
     */
    public TouchEffect getPvpBuff ()
    {
        return pvpBuff;
    }

    /**
     * 屠魔基数
     */
    private static final short BASE_NUMBER_OF_PVE = 10;

    /**
     * 杀戮基数
     */
    private static final short BASE_NUMBER_OF_PVP = 100;

    /**
     * 杀戮和屠魔强化最高等级
     */
    private static final byte  MAX_LEVEL          = 12;
}
