package hero.duel.service;

import org.dom4j.Element;

import yoyo.service.base.AbsConfig;


public class PvpServerConfig extends AbsConfig
{
	/**
	 * 决斗剩余时间提示间隔
	 */
	public byte			duel_time_alert_interval;
	/**
	 * 决斗倒计时
	 */
	public byte			duel_count_down;
	/**
	 * 决斗总时间
	 */
	public short		duel_sum_time;

    @Override
    public void init (Element _xmlNode) throws Exception
    {
    	Element element = _xmlNode.element("duelconfig");
    	
    	duel_time_alert_interval = Byte.parseByte(element.elementTextTrim("duel_time_alert_interval"));
    	duel_count_down = Byte.parseByte(element.elementTextTrim("duel_count_down"));
    	duel_sum_time = Short.valueOf(element.elementTextTrim("duel_sum_time"));
    }

}
