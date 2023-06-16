package hero.npc.dict;

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

import hero.npc.service.NotPlayerServiceImpl;
import hero.share.service.LogWriter;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

public class NpcImageDict
{

    private HashMap<Short, byte[]> npcImageMap = new HashMap<Short, byte[]>();

    private static NpcImageDict    instance;

    private NpcImageDict()
    {
        load();
    }

    public static NpcImageDict getInstance ()
    {
        if (null == instance)
        {
            instance = new NpcImageDict();
        }

        return instance;
    }

    private void load ()
    {
        File[] imageFiles = new File(NotPlayerServiceImpl.getInstance()
                .getConfig().NPCImagePath).listFiles();

        for (int i = 0; i < imageFiles.length; i++)
        {
            short imageID = -1;
            if (imageFiles[i].getName().toLowerCase().endsWith(".png"))
            {
                String imageFileName = imageFiles[i].getName();
                imageID = Short.parseShort(imageFileName.substring(0,
                        imageFileName.length() - 4));

                npcImageMap.put(new Short(imageID),
                        getImageBytes(imageFiles[i]));
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

    public byte[] getImageBytes (short _imageID)
    {
        return npcImageMap.get(_imageID);
    }

}
