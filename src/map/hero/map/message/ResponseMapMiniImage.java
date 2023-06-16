package hero.map.message;

import hero.map.Map;
import hero.map.service.MapServiceImpl;
import hero.share.Constant;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseMiniMap.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-11-27 上午10:07:18
 * @描述 ：
 */

public class ResponseMapMiniImage extends AbsResponseMessage
{
    /**
     * 客户端类型（高、中、低端）
     */
    private short  clientType;

    /**
     * 
     */
    private byte[] mapImage;

    /**
     * 
     */
    private int    imageID;

    /**
     * 构造
     * 
     * @param _imageID
     * @param _mapImage
     */
    public ResponseMapMiniImage(short _clientType, int _imageID,
            byte[] _mapImage)
    {
        clientType = _clientType;
        imageID = _imageID;
        mapImage = _mapImage;
    }

    @Override
    public int getPriority ()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        // TODO Auto-generated method stub
        yos.writeShort(imageID);

        if (Constant.CLIENT_OF_HIGH_SIDE != clientType)
        {
            yos.writeShort((short) mapImage.length);
            yos.writeBytes(mapImage);
        }
    }
}
