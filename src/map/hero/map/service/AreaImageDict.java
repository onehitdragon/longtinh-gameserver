package hero.map.service;

import hero.share.service.LogWriter;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 AreaImageDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-20 下午05:33:29
 * @描述 ：
 */

public class AreaImageDict
{
    /**
     * 区域地图图片映射列表（区域图片编号＝区域编号：图片字节数组）
     */
    private HashMap<Integer, byte[]> dictionary;

    /**
     * 单例
     */
    private static AreaImageDict     instance;

    /**
     * 私有构造
     */
    private AreaImageDict()
    {
        dictionary = new HashMap<Integer, byte[]>();
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static AreaImageDict getInstance ()
    {
        if (null == instance)
        {
            instance = new AreaImageDict();
        }

        return instance;
    }

    /**
     * 从图片目录加载图片
     * 
     * @param _imagePath
     */
    protected void init (String _imagePath)
    {
        File filePath;

        try
        {
            filePath = new File(_imagePath);
        }
        catch (Exception e)
        {
            LogWriter.println("未找到区域图片目录：" + _imagePath);

            return;
        }

        try
        {
            File[] fileList = filePath.listFiles();

            if (fileList.length > 0)
            {
                dictionary.clear();
            }

            for (int i = 0; i < fileList.length; i++)
            {
                int imageID = -1;

                if (fileList[i].getName().toLowerCase().endsWith(".png"))
                {
                    String imageFileName = fileList[i].getName();
                    imageID = Integer.parseInt(imageFileName.substring(0,
                            imageFileName.length() - 4));

                    dictionary.put(imageID, getImageBytes(fileList[i]));
                }
            }
        }
        catch (Exception e)
        {
            LogWriter.error(null, e);
        }
    }

    private byte[] getImageBytes (File _imageFile)
    {
        byte[] rtnValue = null;

        try
        {
            DataInputStream dis = new DataInputStream(new FileInputStream(
                    _imageFile));
            int imgFileByteSize = dis.available();
            rtnValue = new byte[imgFileByteSize];

            for (int pos = 0; (pos = dis.read(rtnValue, pos, imgFileByteSize
                    - pos)) != -1;);

            dis.close();
        }
        catch (Exception e)
        {
            LogWriter.error(null, e);
        }

        return rtnValue;
    }

    /**
     * 获取区域地图图片字节数组
     * 
     * @param _imageID
     * @return
     */
    public byte[] getImageBytes (int _imageID)
    {
        return dictionary.get(_imageID);
    }
}
