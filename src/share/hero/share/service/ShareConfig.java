package hero.share.service;

import hero.share.RankMenuField;
import org.apache.log4j.Logger;
import org.dom4j.Element;

import yoyo.service.YOYOSystem;
import yoyo.service.base.AbsConfig;


import java.util.*;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 *
 * @文件 ShareConfig.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-4 下午02:42:14
 * @描述 ：
 */

public class ShareConfig extends AbsConfig
{
	private int    rmb_fee_point_convert;
	
	private String picture_data_path;
	
	private String high_path;
	
	private String middle_path;
	
	private String low_path;
	
	private String monetary_unit;
	
	private String sign_line_break;
	
	private String resource_DB_name;
	
	private String resource_DB_url;
	
	private String resource_DB_username;
	
	private String resource_DB_password;

    private static Logger log = Logger.getLogger(ShareConfig.class);
    public Map<Byte,RankMenuField> rankTypeMap = new HashMap<Byte,RankMenuField>();
//    public Map<Byte,RankMenuField> showMenuTypes = new HashMap<Byte,RankMenuField>();

    /**
     * 离线经验道具ID
     */
    public int hookExpGoodsID;

    /**
     * 开始计算离线经验的时间
     */
    public int hookHours;

    @Override
    public void init (Element _xmlNode) throws Exception
    {
    	Element ePicture = _xmlNode.element("data");
//    	Element para = _xmlNode.element("para");
    	Element params = _xmlNode.element("para");
    	Element resDBConfig = _xmlNode.element("resource_DB_config");
    	
    	resource_DB_name = resDBConfig.elementTextTrim("resource_DB_name");
    	resource_DB_url = resDBConfig.elementTextTrim("resource_DB_url");
    	resource_DB_username = resDBConfig.elementTextTrim("resource_DB_username");
    	resource_DB_password = resDBConfig.elementTextTrim("resource_DB_password");
    	
    	rmb_fee_point_convert = Integer.valueOf(ePicture.elementTextTrim("rmb_fee_point_convert"));
    	
    	high_path = YOYOSystem.HOME + ePicture.elementTextTrim("high_path");
    	middle_path = YOYOSystem.HOME + ePicture.elementTextTrim("middle_path");
    	low_path = YOYOSystem.HOME + ePicture.elementTextTrim("low_path");
        picture_data_path = YOYOSystem.HOME + ePicture.elementTextTrim("picture_data_path");
        monetary_unit = ePicture.elementTextTrim("monetary_unit");
        sign_line_break = ePicture.elementTextTrim("sign_line_break");

        hookExpGoodsID = Integer.parseInt(params.elementTextTrim("hook_exp_id"));
        hookHours = Integer.parseInt(params.elementTextTrim("hook_hours"));

        Element rankE = params.element("rank_types");
        Iterator<Element> rankTypes = rankE.elementIterator();
        while (rankTypes.hasNext()){
            Element rankType = rankTypes.next();
            log.debug("rank type name = " + rankType.getName());
            byte id = Byte.parseByte(rankType.elementTextTrim("id"));
            String name = rankType.elementTextTrim("name");
            String fields = rankType.elementTextTrim("fields");
            byte menuLevel = Byte.parseByte(rankType.elementTextTrim("menu_level"));

            log.debug("rank type id="+id+",name="+name+",fields="+fields);

            Element secondMenusE = rankType.element("show_type_menus");//二级菜单
            List<RankMenuField> secondMenuList = new ArrayList<RankMenuField>();
            if(secondMenusE != null){
                Iterator<Element> menuIt = secondMenusE.elementIterator();
                while (menuIt.hasNext()){
                    Element menu = menuIt.next();
                    byte sid = Byte.parseByte(menu.elementTextTrim("id"));
                    String sname = menu.elementTextTrim("name");
                    byte smenuLevel = Byte.parseByte(menu.elementTextTrim("menu_level"));

                    Element thirdMneusE = menu.element("child_menus");//三级菜单
                    List<RankMenuField> thirdMenuList = new ArrayList<RankMenuField>();
                    if(thirdMneusE != null){
                        Iterator<Element> childMenuIt = thirdMneusE.elementIterator();

                        while (childMenuIt.hasNext()){
                            Element childE = childMenuIt.next();
                            log.debug("child e name ="+childE.getName());
                            byte cid = Byte.parseByte(childE.elementTextTrim("id"));
                            String cname = childE.elementTextTrim("name");
                            byte cmenuLevel = Byte.parseByte(childE.elementTextTrim("menu_level"));
                            log.debug("child cid="+cid+",cname="+cname+",cmenulevel="+cmenuLevel);
                            String vocations = childE.elementTextTrim("vocation");

                            RankMenuField rmf = new RankMenuField();
                            rmf.id = cid;
                            rmf.name = cname;
                            rmf.menuLevel = cmenuLevel;
                            rmf.vocation = vocations; //三级的职业有可能是两个职业一块显示的,用","分隔

                            thirdMenuList.add(rmf);
                        }
                    }

                    log.debug("menu id="+id+",name="+name);

                    RankMenuField srmf = new RankMenuField();
                    srmf.id = sid;
                    srmf.name = sname;
                    srmf.menuLevel = smenuLevel;
                    srmf.childMenuList = thirdMenuList;
                    secondMenuList.add(srmf);
                }
            }

            List<String> fieldList = new ArrayList<String>();
            String[] fieldArray = fields.split(",");
            for (String s : fieldArray){
                fieldList.add(s);
            }

            RankMenuField rmf = new RankMenuField();
            rmf.id = id;
            rmf.name = name;
            rmf.fieldList = fieldList;
            rmf.menuLevel = menuLevel;
            rmf.childMenuList = secondMenuList;

            rankTypeMap.put(id,rmf);
        }


        log.debug("rank type map size = " + rankTypeMap.size());
    }
    
    /**
     * 高端路径
     * @return
     */
    public String getHighPath()
    {
    	return high_path;
    }
    /**
     * 中端路径
     * @return
     */
    public String getMiddlePath()
    {
    	return middle_path;
    }
    /**
     * 低端路径
     * @return
     */
    public String getLowPath()
    {
    	return low_path;
    }
    
    /**
     * 获得所有下载图片主目录
     * @return
     */
    public String getPicture()
    {
    	return picture_data_path;
    }
    
    public String getResourceDBname()
    {
    	return resource_DB_name;
    }
    
    public String getResourceDBurl()
    {
    	return resource_DB_url;
    }
    
    public String getResourceDBusername()
    {
    	return resource_DB_username;
    }
    
    public String getResourceDBpassword()
    {
    	return resource_DB_password;
    }
    
    /**
     * 货币单位
     * @return
     */
    public String getMonetaryUnit()
    {
    	return monetary_unit;
    }
    /**
     * 给客户端的通用换行符
     * @return
     */
    public String getSignLineBreak()
    {
    	return sign_line_break;
    }
    
    /**
     * 获取RMB-->游戏点数 兑换 比例
     * @return
     */
    public int getFeePointConvert()
    {
    	return rmb_fee_point_convert;
    }

}
