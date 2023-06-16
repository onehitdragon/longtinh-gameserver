package hero.item.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 药品使用失败
 * 
 * @author Luke chen
 * @date 2009-4-14
 */
public class NodifyMedicamentCDTime extends AbsResponseMessage
{
    int goodID;

    public NodifyMedicamentCDTime(int _id)
    {
        goodID = _id;
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
        yos.writeInt(goodID);
    }
}
