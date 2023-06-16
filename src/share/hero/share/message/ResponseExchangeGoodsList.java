package hero.share.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


public class ResponseExchangeGoodsList extends AbsResponseMessage
{

    private byte   goodsType;

    private byte[] data;

    public ResponseExchangeGoodsList(byte _goodsType, byte[] _data)
    {
        goodsType = _goodsType;
        data = _data;
    }

    @Override
    public int getPriority ()
    {
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        yos.writeByte(goodsType);
        yos.writeBytes(data);
    }

}
