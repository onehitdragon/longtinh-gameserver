package hero.npc.others;

import hero.npc.dict.DoorPlateDataDict.DoorPlateData;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 DoorPlate.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-9-27 下午02:34:03
 * @描述 ：
 */

public class DoorPlate extends ME2OtherGameObject
{
    private String tip;

    /**
     * 构造
     * 
     * @param _data
     */
    public DoorPlate(DoorPlateData _data)
    {
        super(_data.modelID);
        tip = _data.tip;
    }

    /**
     * 门牌提示
     * 
     * @return
     */
    public String getTip ()
    {
        return tip;
    }
}
