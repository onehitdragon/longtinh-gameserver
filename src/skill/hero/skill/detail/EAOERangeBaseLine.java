package hero.skill.detail;


/**<p>
 *  Copyright: DGFun CO., (c) 2008
 *  </p>
 *  @文件   EAOERangeBaseLine.java
 *  @创建者  DingChu
 *  @版本    1.0
 *  @时间   2010-4-27 上午10:40:50
 *  @描述 ：
 **/

public enum EAOERangeBaseLine
{
    RELEASER("施放者"), TARGET("被施放者");

    String desc;

    EAOERangeBaseLine(String _desc)
    {
        desc = _desc;
    }

    public static EAOERangeBaseLine get (String _desc)
    {
        for (EAOERangeBaseLine base : EAOERangeBaseLine.values())
        {
            if (base.desc.equals(_desc))
            {
                return base;
            }
        }

        return null;
    }
}


