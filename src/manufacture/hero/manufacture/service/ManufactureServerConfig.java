package hero.manufacture.service;

import org.dom4j.Element;

import yoyo.service.YOYOSystem;
import yoyo.service.base.AbsConfig;


/**
 * 制造配置类 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class ManufactureServerConfig extends AbsConfig
{
    /*
     * DISPENSER((byte) 1, "药剂师"), 宝石匠 JEWELER((byte) 2, "宝石匠"), 工艺匠
     * CRAFTSMAN((byte) 3, "工艺匠"), 铁匠 BLACKSMITH((byte) 4, "铁匠");
     */
    public String[] dataPath = new String[4];

    @Override
    public void init (Element _xmlNode) throws Exception
    {
        // TODO 自动生成方法存根
        Element dataPathE = _xmlNode.element("para");
        dataPath[0] = YOYOSystem.HOME
                + dataPathE.elementTextTrim("dispenser_data_dir");
        dataPath[1] = YOYOSystem.HOME
                + dataPathE.elementTextTrim("jeweler_data_dir");
        dataPath[2] = YOYOSystem.HOME
                + dataPathE.elementTextTrim("craftsman_data_dir");
        dataPath[3] = YOYOSystem.HOME
                + dataPathE.elementTextTrim("blacksmith_data_dir");
    }

}
