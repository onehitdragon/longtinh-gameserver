package hero.chat.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 获得物品通知
 * 
 * @author Luke 陈路
 * @date Jul 28, 2009
 */
public class GetGoodsNofity extends AbsResponseMessage
{
    /**
     * 物品名字
     */
    String name;

    /**
     * 描述信息
     */
    String content;

    /**
     * 品质RGB颜色
     */
    int    traitRGB;

    /**
     * 数量
     */
    int    num;

    public GetGoodsNofity(String _content, String _name, int _rgb, int _num)
    {
        content = _content;
        name = _name;
        traitRGB = _rgb;
        num = _num;
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
        yos.writeUTF(content);
        yos.writeUTF(name);
        yos.writeInt(traitRGB);
        yos.writeByte(num);
    }

}
