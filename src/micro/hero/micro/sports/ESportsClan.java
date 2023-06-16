package hero.micro.sports;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ESportsClan.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-12-15 下午02:38:23
 * @描述 ：竞技场势力
 */

public enum ESportsClan
{
    CHI_YOU_MAN_YI(1, "蚩尤蛮夷"), YAN_LONG_YONG_SHI(2, "炎龙勇士"), TIAN_YU_ZHI_JUN(3,
            "天虞之军"), SHUN_WANG_WEI_DUI(4, "舜王卫队");

    /**
     * 编号
     */
    private int    id;

    /**
     * 名字
     */
    private String name;

    /**
     * 构造
     * 
     * @param _name
     */
    private ESportsClan(int _id, String _name)
    {
        id = _id;
        name = _name;
    }

    /**
     * 获取编号
     * 
     * @return
     */
    public int getID ()
    {
        return id;
    }

    /**
     * 获取势力名字
     * 
     * @return
     */
    public String getName ()
    {
        return name;
    }
}
