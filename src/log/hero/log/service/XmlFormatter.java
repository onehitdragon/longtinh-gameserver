package hero.log.service;

import java.text.SimpleDateFormat;
import java.util.Date;

public class XmlFormatter
{
	private static SimpleDateFormat dateFormatter;
	private StringBuffer logBuffer = new StringBuffer();
	private String rootElementName;
	private int count = 0;
	
	public XmlFormatter(String _rootElementName){
		dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
		rootElementName = _rootElementName;
		logBuffer.append("<").append(_rootElementName).append(">");
	}
	
	/**
     * 清空数据
     */
    public XmlFormatter reset ()
    {
        count = 0;
        logBuffer.delete(0, logBuffer.length());
        return this;
    }

    /**
     * 添加数据。内部将拼接成[_logUnit]的格式
     * 
     * @param _logUnit
     * @return
     */
    public XmlFormatter append(String _elementName,String _value)
    {
        count++;
//        _logUnit = _logUnit.replaceAll("\\]", "\\$").replaceAll("\\]", "\\$");
        logBuffer.append("<").append(_elementName).append(">")
        		.append(_value)
        		.append("</").append(_elementName).append(">");
        return this;
    }

    public XmlFormatter append (String _elementName,int _logUnit)
    {
        return append(_elementName,String.valueOf(_logUnit));
    }

    public XmlFormatter append (String _elementName,float _logUnit)
    {
        return append(_elementName,String.valueOf(_logUnit));
    }

    public XmlFormatter append (String _elementName,double _logUnit)
    {
        return append(_elementName,String.valueOf(_logUnit));
    }

    public XmlFormatter append (String _elementName,short _logUnit)
    {
        return append(_elementName,String.valueOf(_logUnit));
    }

    public XmlFormatter append (String _elementName,Date _date)
    {
        return append(_elementName,dateFormatter.format(_date));
    }

    /**
     * 默认使用info记录日志数据
     */
    public String flush ()
    {
        // if (count != getUnitCount())
        // {
        // log.info(this.getClass().getName() + " 数据不全。已生成的数据数量："
        // + count + "，需要数量：" + getUnitCount());
        // // logger.warn(this.getClass().getName() + " 数据不全。已生成的数据数量：" + count
        // // + "，需要数量：" + getUnitCount());
        // }
        // logger.info(logBuffer.toString());
    	logBuffer.append("</").append(rootElementName).append(">");
        return logBuffer.toString();
    }

    /**
     * 已记录的日志数据数量
     * 
     * @return
     */
    public int getCount ()
    {
        return count;
    }
}
