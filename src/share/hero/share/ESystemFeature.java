package hero.share;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ESystemFeature.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-20 上午11:06:07
 * @描述 ：
 */

public enum ESystemFeature
{
    /**
     * 亡者
     */
    DEAD("亡者"),
    /**
     * 英雄
     */
    HERO("英雄");

    /**
     * 描述
     */
    private String desciption;

    /**
     * 构造
     * 
     * @param _desciption
     */
    ESystemFeature(String _desciption)
    {
        desciption = _desciption;
    }

    /**
     * 获取描述
     * 
     * @return
     */
    public String getDesc ()
    {
        return desciption;
    }

    /**
     * 根据描述获取系统特征
     * 
     * @param _desciption
     * @return
     */
    public static ESystemFeature getFeatureByDesc (String _desciption)
    {
        for (ESystemFeature feature : ESystemFeature.values())
        {
            if (feature.getDesc().equals(_desciption))
            {
                return feature;
            }
        }

        return null;
    }
}
