package hero.ui;

import java.io.IOException;

import yoyo.tools.YOYOOutputStream;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2005
 * </p>
 * 
 * @file UI_Input_String.java
 * @author Ding Chu
 * @version 1.0
 * @date 2008-9-10 下午03:33:25
 * 
 * <pre>
 *       Description:文字输入框
 * </pre>
 */
public class UI_InputString
{
    /**
     * 获取UI绘制数据字节数组
     * 
     * @param _tip 输入框提示
     * @param _charNumLowerLimit 字符数量下限
     * @param _charNumUpLimit 字符数量上限
     * @return
     */
    public static byte[] getBytes (String _tip, int _charNumLowerLimit,
            int _charNumUpLimit)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());
            output.writeUTF(_tip);
            output.writeByte(_charNumLowerLimit);
            output.writeByte(_charNumUpLimit);
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
     * 获取UI绘制数据字节数组
     * 
     * @param _tip 输入框提示
     * @return
     */
    public static byte[] getBytes (String _tip)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());
            output.writeUTF(_tip);
            output.writeByte(1);
            output.writeByte(20);
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
        return EUIType.INPUT_STRING;
    }
}
