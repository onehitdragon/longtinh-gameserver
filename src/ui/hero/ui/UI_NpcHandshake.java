package hero.ui;

import hero.npc.detail.NpcHandshakeOptionData;

import java.io.IOException;
import java.util.ArrayList;

import yoyo.tools.YOYOOutputStream;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 UI_NPC_HANDSHAKE.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-19 上午10:00:08
 * @描述 ：与NPC交互第一步界面数据
 */

public class UI_NpcHandshake
{
    /**
     * 获取UI类型
     * 
     * @return
     */
    public static EUIType getType ()
    {
        return EUIType.NPC_HANDSHAKE;
    }

    /**
     * 获取UI绘制数据字节数组
     * 
     * @param _content
     * @return
     */
    public static byte[] getBytes (
            ArrayList<NpcHandshakeOptionData> _funcitonMarkList)
    {
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());

            if (null != _funcitonMarkList && _funcitonMarkList.size() > 0)
            {
                output.writeByte((byte) _funcitonMarkList.size());

                for (NpcHandshakeOptionData data : _funcitonMarkList)
                {
                    output.writeInt(data.functionMark);
                    output.writeShort(data.miniImageID);
                    output.writeUTF(data.optionDesc);

                    if (null != data.followOptionData)
                    {
                        output.writeByte(data.followOptionData.size());

                        for (byte[] b : data.followOptionData)
                        {
                            output.writeBytes(b);
                        }
                    }
                    else
                    {
                        output.writeByte(0);
                    }
                }
            }
            else
            {
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
}
