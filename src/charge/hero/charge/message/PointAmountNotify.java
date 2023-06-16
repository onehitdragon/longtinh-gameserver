package hero.charge.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


public class PointAmountNotify extends AbsResponseMessage
{
    /**
     * 点数余额
     */
    private int pointAmount;

    /**
     * 构造
     * 
     * @param _pointAmount
     */
    public PointAmountNotify(int _pointAmount)
    {
        pointAmount = _pointAmount;
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
        yos.writeInt(pointAmount);
    }
}
