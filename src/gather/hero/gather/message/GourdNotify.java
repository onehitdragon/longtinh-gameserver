package hero.gather.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


public class GourdNotify extends AbsResponseMessage
{
    private boolean hasGourd;

    public GourdNotify(boolean _hasGourd)
    {
        hasGourd = _hasGourd;
    }

    @Override
    public int getPriority ()
    {
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        yos.writeByte(hasGourd ? 1 : 0);
    }

}
