package hero.charge.net.parse;

import java.util.HashMap;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ParamModel.java
 * @创建者 ChenYaMeng
 * @版本 1.0
 * @时间 2010-6-25 下午03:02:41
 * @描述 ：xml 数据处理基类
 */

public abstract class XmlParamModel
{
    /**
     * 要解析的参数字符串，xml格式
     */
    public XmlParamModel(String _param)
    {
        parse(_param);

        process();
    }

    public XmlParamModel(HashMap<String, String> _param)
    {
        parse(_param);

        process();
    }

    /**
     * 处理
     */
    public abstract void process ();

    /**
     * 解析字符串
     */
    protected void parse (String _param)
    {
    }

    /**
     * 解析HashMap
     */
    protected void parse (HashMap<String, String> _param)
    {
    }
}
