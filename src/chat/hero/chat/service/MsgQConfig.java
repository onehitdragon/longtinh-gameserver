package hero.chat.service;


import org.dom4j.Element;

import yoyo.service.base.AbsConfig;

/**
 * OutMesgQ策略类 Description:
 * 
 * @author Luke
 * @version 0.1
 */
public class MsgQConfig extends AbsConfig
{
    /**
     * 每个用户队列最大个数
     */
    private int  maxPri   = 2000;

    /**
     * 世界聊天最大个数
     */
    private int  maxWorld = 300;

    /**
     * 最大用户数量
     */
    private int  maxItem  = 2000;

    /**
     * 超时间隔
     */
    private long timeOut  = 5000;

    /**
     * 每个用户队列最大个数
     * 
     * @return
     */
    public int getMaxPri ()
    {
        return maxPri;
    }

    /**
     * 世界聊天最大个数
     * 
     * @return
     */
    public int getMaxWorld ()
    {
        return maxWorld;
    }

    /**
     * 最大用户数量
     * 
     * @return
     */
    public int getMaxItem ()
    {
        return maxItem;
    }

    /**
     * 超时间隔
     * 
     * @return
     */
    public long getTimeOut ()
    {
        return timeOut;
    }

    @Override
    public void init (Element root) throws Exception
    {
        String sMaxPri = root.valueOf("//chatservice/maxPri");
        if (sMaxPri != null && sMaxPri.equals(""))
            maxPri = Integer.parseInt(sMaxPri);
        String sMaxWorld = root.valueOf("//chatservice/maxWorld");
        if (sMaxWorld != null && sMaxWorld.equals(""))
            maxWorld = Integer.parseInt(sMaxWorld);
        String sMaxItem = root.valueOf("//chatservice/maxItem");
        if (sMaxItem != null && sMaxItem.equals(""))
            maxItem = Integer.parseInt(sMaxItem);
        String sTimeOut = root.valueOf("//chatservice/timeOut");
        if (sTimeOut != null && sTimeOut.equals(""))
            timeOut = Integer.parseInt(sTimeOut);
    }
}
