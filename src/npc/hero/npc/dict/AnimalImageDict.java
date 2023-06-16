package hero.npc.dict;

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
 * @文件 AnimalImageDict.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-11 下午09:40:52
 * @描述 ：
 */

public class AnimalImageDict
{
    /**
     * 动物图片映射
     */
    private HashMap<Short, byte[]> animalImageMap = new HashMap<Short, byte[]>();

    /**
     * 单例
     */
    private static AnimalImageDict instance;

    /**
     * 构造
     */
    private AnimalImageDict()
    {
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static AnimalImageDict getInstance ()
    {
        if (null == instance)
        {
            instance = new AnimalImageDict();
        }

        return instance;
    }

    public void load (String _imagePath)
    {
        File[] imageFiles = new File(_imagePath).listFiles();

        for (int i = 0; i < imageFiles.length; i++)
        {
            short imageID = -1;

            if (imageFiles[i].getName().toLowerCase().endsWith(".png"))
            {
                String imageFileName = imageFiles[i].getName();
                imageID = Short.parseShort(imageFileName.substring(0,
                        imageFileName.length() - 4));

                animalImageMap.put(imageID, getImageBytes(imageFiles[i]));
            }
        }

        imageFiles = null;
    }

    private byte[] getImageBytes (File _imageFile)
    {
        byte[] rtnValue = null;

        try
        {
            DataInputStream dis = null;

            dis = new DataInputStream(new FileInputStream(_imageFile));
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
     * 获取动物图片
     * 
     * @param _imageID
     * @return
     */
    public byte[] getAnimalImageBytes (short _imageID)
    {
        return animalImageMap.get(_imageID);
    }
}
