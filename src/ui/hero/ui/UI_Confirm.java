package hero.ui;

import java.io.IOException;

import yoyo.tools.YOYOOutputStream;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 UI_Confirm.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-12 下午05:44:31
 * @描述 ：
 */

public class UI_Confirm
{
    /**
     * 获取UI类型
     * 
     * @return
     */
    public static EUIType getType ()
    {
        return EUIType.CONFIRM;
    }

    /**
     * 获取UI绘制数据字节数组
     * 
     * @param _content
     * @return
     */
    public static byte[] getBytes (String _content)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());
            output.writeUTF(_content);
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
