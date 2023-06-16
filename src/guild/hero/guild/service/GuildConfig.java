package hero.guild.service;

import org.dom4j.Element;

import yoyo.service.base.AbsConfig;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GuildConfig.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-12 下午15:43:45
 * @描述 ：公会配置
 */

public class GuildConfig extends AbsConfig
{
    /**
     * 创建者的最低等级
     */
    public byte level_of_creator;

    /**
     * 创建公会时需要的金钱
     */
    public int  money_of_create;
    
    /**
     * 副会长总数量
     */
    public int  officer_sum;

    @Override
    public void init (Element node) throws Exception
    {
        // TODO Auto-generated method stub
        Element subNode = node.element("para");

        if (subNode != null)
        {
            level_of_creator = Byte.parseByte(subNode
                    .elementTextTrim("level_of_creator"));
            money_of_create = Integer.parseInt(subNode
                    .elementTextTrim("money_of_create"));
            
            officer_sum = Integer.parseInt(subNode.elementTextTrim("officer_sum"));
        }
    }
}
