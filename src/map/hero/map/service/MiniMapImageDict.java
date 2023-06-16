package hero.map.service;

/**<p>
 *  Copyright: DGFun CO., (c) 2005
 *  </p>
 *  @author DING Chu
 *  @version 1.0
 *  @date 2008-8-21 上午10:03:08
 *
 *  <pre>
 *      Description:
 *  </pre>
 **/

import hero.share.service.LogWriter;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

public class MiniMapImageDict
{
    /**
     * 小地图图片映射列表（区域图片编号＝区域编号：图片字节数组）
     */
    private HashMap<Integer, byte[]> dictionary;

    /**
     * 单例
     */
    private static MiniMapImageDict  instance;

    /**
     * 私有构造
     */
    private MiniMapImageDict()
    {
        dictionary = new HashMap<Integer, byte[]>();
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static MiniMapImageDict getInstance ()
    {
        if (null == instance)
        {
            instance = new MiniMapImageDict();
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
            LogWriter.println("未找到指定的目录：" + _imagePath);

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
            e.printStackTrace();
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
     * 获取小地图图片字节数组
     * 
     * @param _imageID
     * @return
     */
    public byte[] getImageBytes (int _imageID)
    {
        return dictionary.get(_imageID);
    }
}
