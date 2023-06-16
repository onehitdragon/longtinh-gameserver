package hero.ui;

import java.io.IOException;

import yoyo.tools.YOYOOutputStream;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2005
 * </p>
 * 
 * @file UI_Input_Digidal.java
 * @author Ding Chu
 * @version 1.0
 * @date 2008-9-10 下午03:33:04
 * 
 * <pre>
 *      Description:数字输入框
 * </pre>
 */
public class UI_InputDigidal
{
    /**
     * 获取UI绘制数据字节数组
     * 
     * @param _tip 输入框提示
     * @param _valueLowerLimit 数值下限
     * @param _valueUpLimit 数值上限
     * @return
     */
    public static byte[] getBytes (String _tip, int _valueLowerLimit,
            int _valueUpLimit)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());
            output.writeByte(1);
            output.writeUTF(_tip);
            output.writeInt(_valueLowerLimit);
            output.writeInt(_valueUpLimit);
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
            output.writeByte(0);
            output.writeUTF(_tip);
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
        return EUIType.INPUT_DIGITAL;
    }
}
