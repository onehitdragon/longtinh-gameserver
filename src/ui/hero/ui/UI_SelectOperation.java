package hero.ui;

import java.io.IOException;
import java.util.ArrayList;

import yoyo.tools.YOYOOutputStream;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2005
 * </p>
 * 
 * @file UI_SelectOperation.java
 * @author Ding Chu
 * @version 1.0
 * @date 2008-9-10 下午04:50:47
 * 
 * <pre>
 *              Description:
 * </pre>
 */
public class UI_SelectOperation
{
    /**
     * 获取UI绘制数据字节数组
     * 
     * @param _tip 提示
     * @param _operateMenuArray 操作选项菜单数组
     * @return
     */
    public static byte[] getBytes (String _tip, String[] _operateMenuArray)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());
            output.writeUTF(_tip);
            output.writeByte(_operateMenuArray.length);

            for (String menu : _operateMenuArray)
            {
                output.writeUTF(menu);
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
     * 获取UI绘制数据字节数组
     * 
     * @param _tip 提示
     * @param _operateMenuArray 操作选项菜单数组
     * @param _followOptionData
     * @return
     */
    public static byte[] getBytes (String _tip, String[] _operateMenuArray,
            ArrayList<byte[]>[] _followOptionData)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());
            output.writeUTF(_tip);
            output.writeByte(_operateMenuArray.length);

            for (int i = 0; i < _operateMenuArray.length; i++)
            {
                output.writeUTF(_operateMenuArray[i]);
                if (null != _followOptionData && null != _followOptionData[i])
                {
                    output.writeByte(_followOptionData[i].size());
                    for (byte[] _data : _followOptionData[i])
                    {
                        output.writeBytes(_data);
                    }
                }
                else
                {
                    output.writeByte(0);
                }
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
        return EUIType.SELECT;
    }
}
