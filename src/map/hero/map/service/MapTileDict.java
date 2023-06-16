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

import hero.share.service.DataConvertor;
import hero.share.service.LogWriter;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

public class MapTileDict
{
    /**
     * 字典
     */
    private HashMap<Integer, char[]> dictionary;

    /**
     * 单例
     */
    private static MapTileDict       instance;

    /**
     * 获取单例
     * 
     * @return
     */
    public static MapTileDict getInstance ()
    {
        if (null == instance)
        {
            instance = new MapTileDict();
        }

        return instance;
    }

    /**
     * 私有构造
     */
    private MapTileDict()
    {
        dictionary = new HashMap<Integer, char[]>();
    }

    public void init (String _tilePath)
    {
        File filePath;

        try
        {
            filePath = new File(_tilePath);
        }
        catch (Exception e)
        {
            LogWriter.println("未找到指定的目录：" + _tilePath);

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

                    dictionary.put(imageID, getTileChars(fileList[i]));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private char[] getTileChars (File _tileFile) throws Exception
    {
        char[] rtnValue = null;

        try
        {
            int tilesFileSize = (int) _tileFile.length();

            if (1 == tilesFileSize % 2)
            {
                tilesFileSize++;
            }

            byte[] content = new byte[tilesFileSize];
            rtnValue = new char[tilesFileSize / 2];

            FileInputStream fis = new FileInputStream(_tileFile);
            fis.read(content, 0, (int) _tileFile.length());

            DataConvertor
                    .bytes2Chars((short) content.length, content, rtnValue);

            fis.close();
        }
        catch (Exception e)
        {
            LogWriter.error(null, e);
        }

        return rtnValue;
    }

    /**
     * 获取地图切片
     * 
     * @param _mapTileID
     * @return
     */
    public char[] getMapTileChars (int _mapTileID)
    {
        return dictionary.get(_mapTileID);
    }
}
