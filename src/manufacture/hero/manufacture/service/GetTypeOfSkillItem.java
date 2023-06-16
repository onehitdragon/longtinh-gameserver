package hero.manufacture.service;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GetTypeOfManufacture.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-8-25 上午11:52:47
 * @描述 ：生活技能条目获得方式
 */

public enum GetTypeOfSkillItem
{
    /**
     * NPC处学习或图纸中获得
     */
    LEARN("你学会了 "),
    /**
     * 制造的过程中领悟
     */
    COMPREHEND("你领悟了 "),
    /**
     * 系统向技能列表中添加
     */
    SYSTEM("你获得了 ");

    private String tipHeader;

    /**
     * 构造
     * 
     * @param _tip 提示前缀
     */
    private GetTypeOfSkillItem(String _tip)
    {
        tipHeader = _tip;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Enum#toString()
     */
    public String toString ()
    {
        return tipHeader;
    }
}
