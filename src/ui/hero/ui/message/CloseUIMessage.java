package hero.ui.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


public class CloseUIMessage extends AbsResponseMessage
{

    @Override
    public int getPriority ()
    {
        return 0;
    }

    @Override
    protected void write () throws IOException
    {

    }

}
