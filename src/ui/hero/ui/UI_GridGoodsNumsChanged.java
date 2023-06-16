package hero.ui;

import java.io.IOException;

import yoyo.tools.YOYOOutputStream;

import hero.item.bag.SingleGoodsBag;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 UI_GridGoodsNumsChanged.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-25 下午01:13:29
 * @描述 ：
 */

public class UI_GridGoodsNumsChanged
{
    /**
     * 获取UI类型
     * 
     * @return
     */
    public static EUIType getType ()
    {
        return EUIType.GRID_CHANGED;
    }

    public static byte[] getBytes (int _gridIndex, int _goodsID,
            int _currentNums)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());
            output.writeByte(_gridIndex);
            output.writeInt(_goodsID);
            output.writeByte(_currentNums);

            output.flush();

            return output.getBytes();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            try
            {
                output.close();
            }
            catch (IOException e)
            {
            }
            output = null;
        }
    }
}
