package hero.charge.service;

import hero.gm.ParamChatContent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import yoyo.service.YOYOSystem;
import yoyo.service.base.AbsConfig;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ChargeConfig.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-10-12 上午10:15:03
 * @描述 ：计费配置
 */

public class ChargeConfig extends AbsConfig
{
     private static Logger log = Logger.getLogger(ChargeConfig.class);
    /**
     * 游戏点数数据库HOST
     */
    public String point_amount_db_url;

    /**
     * 游戏点数数据库登陆用户名
     */
    public String point_amount_db_username;

    /**
     * 游戏点数数据库登陆秘密
     */
    public String point_amount_db_pwd;

    /**
     * 商城物品数据路径
     */
    public String mall_goods_data_path;
    
    /**
     * 计费类型路径
     */
    public String url_charge_type_path;

    /**
     * 计费信息路径
     */
    public String url_charge_info_path;

    /**
     * 开放的HTTP端口，用于接收计费服务器的数据同步（回调）
     */
    public int    port_callback;

    
    /**
     * 计费配置接口
     */
    public String fee_ini_url;
    /**
     * 神州付充值接口 
     */
    public String szf_rechange_url;
    /**
     * 网游充值接口
     */
    public String ng_rechange_url;
    /**
     * 加点接口
     */
    public String add_point_url;
    /**
     * 扣点接口
     */
    public String sub_point_url;
    /**
     * 查询点数接口 
     */
    public String query_point_url;
    /**
     * 查询消费记录接口
     */
    public String query_deduct_list_url;
    /**
     * 查询充值记录 
     */
    public String query_rechage_list_url;
    /**
     * 计费接口id和对应URL
     */
    public Map<String,String> feeIdsMap = new HashMap<String, String>();

    
    /**
     * 各类型对应的中文名称
     */
    public String[] type_string;
    
    public String notice_string;
    
    public short now_version;
    
    /**
     * 包裹扩容数据组
     */
    public String[][] bag_upgrade_data;



    @Override
    public void init (Element node) throws Exception
    {
        // TODO Auto-generated method stub
        try
        {
            Element paraElement = node.element("config");

            /*point_amount_db_url = "jdbc:mysql://"
                    + paraElement.elementTextTrim("point_amount_db_host")
                    + "/"
                    + paraElement.elementTextTrim("point_amount_db_name")
                    + "?connectTimeout=0&autoReconnect=true&failOverReadOnly=false";

            point_amount_db_username = paraElement
                    .elementTextTrim("point_amount_db_username");

            point_amount_db_pwd = paraElement
                    .elementTextTrim("point_amount_db_pwd");*/

            mall_goods_data_path = YOYOSystem.HOME
                    + paraElement.elementTextTrim("mall_goods_data_path");
            
            url_charge_type_path = YOYOSystem.HOME
            		+ paraElement.elementTextTrim("url_charge_type_path");

            url_charge_info_path = YOYOSystem.HOME + paraElement.elementTextTrim("url_charge_info_path");

            port_callback = Integer.parseInt(paraElement
                    .elementTextTrim("port_callback"));

            
            fee_ini_url = paraElement.elementTextTrim("fee_ini_url");
            
            szf_rechange_url = paraElement.elementTextTrim("szf_rechange_url");
            
            ng_rechange_url = paraElement.elementTextTrim("ng_rechange_url");
            
            add_point_url = paraElement.elementTextTrim("add_point_url");
            
            sub_point_url = paraElement.elementTextTrim("sub_point_url");
            
            query_point_url = paraElement.elementTextTrim("query_point_url");
            
            query_deduct_list_url = paraElement.elementTextTrim("query_deduct_list_url");
            
            query_rechage_list_url = paraElement.elementTextTrim("query_rechage_list_url");
            
            Element subE = paraElement.element("fee_ids");
            Iterator<Element> feeidsIt = subE.elementIterator();
            String id,url;
            while(feeidsIt.hasNext()){
            	Element eit = feeidsIt.next();
            	id = eit.elementTextTrim("id");
            	url = eit.elementTextTrim("url");
            	feeIdsMap.put(id, url);
            }

            
            type_string = paraElement.elementTextTrim("type_string").split(",");
            
            notice_string = paraElement.elementTextTrim("notice_string");
            
            now_version = Short.valueOf( paraElement.elementTextTrim("now_version") );
            
            String[] temp = paraElement.elementTextTrim("bag_upgrade_data").split(";");
            bag_upgrade_data = new String[temp.length][2];
            for (int i = 0; i < temp.length; i++) {
            	bag_upgrade_data[i] = temp[i].split(",");
			}
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    
    /**
     * 获得各类型对应的中文名称
     * @param _type
     * @return
     */
    public String getTypeDesc(byte _type)
    {
    	String desc = "";
    	try 
    	{
    		desc = type_string[_type];
		} 
    	catch (Exception e) 
    	{
    		log.info("warn:无法通过type获得type的中文描述type=" + _type);
			e.printStackTrace();
		}
    	return desc;
    }
}
