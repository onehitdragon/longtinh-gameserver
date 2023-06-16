package hero.skill.detail;


/**<p>
 *  Copyright: DGFun CO., (c) 2008
 *  </p>
 *  @文件   EAOERangeType.java
 *  @创建者  DingChu
 *  @版本    1.0
 *  @时间   2010-4-27 上午10:39:51
 *  @描述 ：
 **/

public enum EAOERangeType
{
    CENTER("中心模式"), FRONT_RECT("前方矩形模式");

    String desc;

    EAOERangeType(String _desc)
    {
        desc = _desc;
    }

    public static EAOERangeType get (String _desc)
    {
        for (EAOERangeType mode : EAOERangeType.values())
        {
            if (mode.desc.equals(_desc))
            {
                return mode;
            }
        }

        return null;
    }
}


