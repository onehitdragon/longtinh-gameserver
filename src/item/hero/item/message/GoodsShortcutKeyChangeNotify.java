package hero.item.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 更改物品快捷键
 * 
 * @author Luke 陈路
 * @date Jul 30, 2009
 */
public class GoodsShortcutKeyChangeNotify extends AbsResponseMessage
{
    /**
     * 当前cd
     */
    int   curCD;

    /**
     * 最大cd
     */
    int   maxCD;

    /**
     * 类型
     */
    short type;

    /**
     * 快捷键
     */
    byte  key;

    public GoodsShortcutKeyChangeNotify(byte _key, int _curCD, int _maxCD,
            short _type)
    {
        key = _key;
        curCD = _curCD;
        maxCD = _maxCD;
        type = _type;
    }

    @Override
    public int getPriority ()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        // TODO Auto-generated method stub
        yos.writeByte(key);
        yos.writeInt(curCD);
        yos.writeInt(maxCD);
        yos.writeShort(type);
    }

}
