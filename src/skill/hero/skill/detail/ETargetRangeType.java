package hero.skill.detail;


/**<p>
 *  Copyright: DGFun CO., (c) 2008
 *  </p>
 *  @文件   ETargetRangeType.java
 *  @创建者  DingChu
 *  @版本    1.0
 *  @时间   2010-4-27 上午10:31:23
 *  @描述 ：
 **/

public enum ETargetRangeType
{
    /**
     * 单体
     */
    SINGLE("单体"),
    /**
     * 队伍
     */
    TEAM("团队"),
    /**
     * 群体
     */
    SOME("群体");

    String desc;

    ETargetRangeType(String _desc)
    {
        desc = _desc;
    }

    public static ETargetRangeType get (String _desc)
    {
        for (ETargetRangeType rangeType : ETargetRangeType.values())
        {
            if (rangeType.desc.equals(_desc))
            {
                return rangeType;
            }
        }

        return null;
    }
}


