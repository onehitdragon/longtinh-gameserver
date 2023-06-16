package hero.ui;

import java.io.IOException;
import java.util.ArrayList;

import yoyo.tools.YOYOOutputStream;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2005
 * </p>
 * 
 * @file UI_SelectOperationWithTip.java
 * @author Ding Chu
 * @version 1.0
 * @date 2008-9-10 下午04:50:47
 */

public class UI_SelectOperationWithTip
{
    /**
     * 获取UI绘制数据字节数组
     * 
     * @param _tip 提示
     * @param _operateMenuArray 操作选项菜单数组
     * @return
     */
    public static byte[] getBytes (String _tip, String[][] _operateMenuArray)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());
            output.writeUTF(_tip);
            output.writeByte(_operateMenuArray.length);

            for (String[] menu : _operateMenuArray)
            {
                output.writeUTF(menu[0]);
                output.writeUTF(menu[1]);
                output.writeByte(0);
            }

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

    /**
     * 获取UI类型
     * 
     * @return
     */
    public static EUIType getType ()
    {
        return EUIType.SELECT_WITH_TIP;
    }
}
