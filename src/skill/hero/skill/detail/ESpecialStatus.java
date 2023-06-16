package hero.skill.detail;


/**<p>
 *  Copyright: DGFun CO., (c) 2008
 *  </p>
 *  @文件   ESpecialStatus.java
 *  @创建者  DingChu
 *  @版本    1.0
 *  @时间   2010-4-27 上午10:45:04
 *  @描述 ：
 **/

public enum ESpecialStatus
{
    /**
     * 潜行
     */
    HIDE("潜行"),
    /**
     * 沉默
     */
    DUMB("沉默"),
    /**
     * 晕厥
     */
    FAINT("晕厥"),
    /**
     * 昏睡
     */
    SLEEP("昏睡"),
    /**
     * 加速
     */
    MOVE_FAST("加速"),
    /**
     * 减速
     */
    MOVE_SLOWLY("减速"),
    /**
     * 嘲讽
     */
    LAUGH("嘲讽"),
    /**
     * 物理暴击
     */
    PHY_BOOM("物理暴击"),
    /**
     * 魔法暴击
     */
    MAG_BOOM("魔法暴击"),
    /**
     * 定身
     */
    STOP("定身");

    String desc;

    ESpecialStatus(String _desc)
    {
        desc = _desc;
    }

    public static ESpecialStatus get (String _desc)
    {
        for (ESpecialStatus specialStatus : ESpecialStatus.values())
        {
            if (specialStatus.desc.equals(_desc))
            {
                return specialStatus;
            }
        }

        return null;
    }
}


