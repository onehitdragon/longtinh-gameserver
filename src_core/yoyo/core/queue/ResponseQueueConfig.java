package yoyo.core.queue;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

public class ResponseQueueConfig
{
    int maxItem     = 2000;
    byte levelNum    = 3;
    int packetSize    = 90000;
    int packetOffSize = 50;
    int maxPacketSize = 500;

    public ResponseQueueConfig(String _xml) throws Exception
    {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(_xml));
        String sMaxItem = document.valueOf("//ResponseMessageQueue/maxItem");
        if (!StringUtils.isBlank(sMaxItem))
        {
            maxItem = Integer.parseInt(sMaxItem);
        }
        String sLevelNum = document.valueOf("//ResponseMessageQueue/levelNum");
        if (!StringUtils.isBlank(sLevelNum))
        {
            levelNum = Byte.parseByte(sLevelNum);
        }
        String sPacketSize = document.valueOf("//ResponseMessageQueue/packetSize");
        if (!StringUtils.isBlank(sPacketSize))
        {
            packetSize = Integer.parseInt(sPacketSize);
        }
        String sPacketOffSize = document.valueOf("//ResponseMessageQueue/packetOffSize");
        if (!StringUtils.isBlank(sPacketOffSize))
        {
            packetOffSize = Integer.parseInt(sPacketOffSize);
        }
        String sMaxPacketSize = document.valueOf("//ResponseMessageQueue/maxPacketSize");  
        if (!StringUtils.isBlank(sMaxPacketSize))
        {
            maxPacketSize = Integer.parseInt(sMaxPacketSize);
        }
    }
}
