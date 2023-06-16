package hero.ui;

import java.io.IOException;

import yoyo.tools.YOYOOutputStream;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2005
 * </p>
 * 
 * @file UI_DialogTip.java
 * @author Ding Chu
 * @version 1.0
 * @date 2008-9-10 下午04:56:01
 * 
 * <pre>
 *      Description:
 * </pre>
 */
public class UI_Tip
{
    /**
     * 获取UI类型
     * 
     * @return
     */
    public static EUIType getType ()
    {
        return EUIType.TIP;
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
