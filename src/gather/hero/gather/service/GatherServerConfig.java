package hero.gather.service;

import org.dom4j.Element;

import yoyo.service.YOYOSystem;
import yoyo.service.base.AbsConfig;


/**
 * 采集系统配置类 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class GatherServerConfig extends AbsConfig
{
    public String gatherDataPath;

    public String soulsDataPath;

    @Override
    public void init (Element _xmlNode) throws Exception
    {

        Element dataPathE = _xmlNode.element("para");
        gatherDataPath = YOYOSystem.HOME
                + dataPathE.elementTextTrim("gather_data_dir");
        soulsDataPath = YOYOSystem.HOME
                + dataPathE.elementTextTrim("souls_data_dir");
    }

}
