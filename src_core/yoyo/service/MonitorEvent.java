package yoyo.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import yoyo.core.event.AbsEvent;

public class MonitorEvent extends AbsEvent<Map<String, String>>
{
    public MonitorEvent(String src)
    {
        super();
        this.src = src;
        this.context = new HashMap<String, String>();
    }

    public void put (String key, String value)
    {
        context.put(key, value);
    }

    public void putAll (Map<String, String> map)
    {
        context.putAll(map);
    }

    @Override
    public String toString ()
    {
        Set<Entry<String, String>> set = context.entrySet();
        StringBuffer buffer = new StringBuffer();
        buffer.append("\n" + src + ":\n");
        for (Entry entry : set)
        {
            buffer.append(entry.getKey());
            buffer.append(":");
            buffer.append(entry.getValue());
            buffer.append("\n");
        }
        return buffer.toString();
    }

}
