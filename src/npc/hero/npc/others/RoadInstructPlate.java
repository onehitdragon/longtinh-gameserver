package hero.npc.others;

import hero.npc.dict.RoadPlateDataDict.RoadPlateData;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 RoadInstructPlate.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-8 下午12:59:29
 * @描述 ：路指示牌
 */

public class RoadInstructPlate extends ME2OtherGameObject
{
    /**
     * 路牌内容
     */
    private String content;

    /**
     * 构造
     * 
     * @param _data
     */
    public RoadInstructPlate(RoadPlateData _data)
    {
        super(_data.modelID);
        content = _data.instructContent;
    }

    /**
     * @return
     */
    public String getContent ()
    {
        return content;
    }
}
