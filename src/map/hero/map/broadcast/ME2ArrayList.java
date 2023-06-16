package hero.map.broadcast;

import java.util.ArrayList;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ME2ArrayList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-11-27 下午02:13:53
 * @描述 ：自定义对象列表
 */

public class ME2ArrayList extends ArrayList<Object>
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 构造
     */
    public ME2ArrayList()
    {
        super();
    }

    /**
     * 移除范围内的对象
     * 
     * @param _startIndex
     * @param _endIndex
     */
    public void remove (int _startIndex, int _endIndex)
    {
        super.removeRange(_startIndex, _endIndex);
    }
}
