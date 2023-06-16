package yoyo.service;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

public class ErrorResponse extends AbsResponseMessage
{

    private String context;

    public ErrorResponse(String context)
    {
        this.context = context;
    }

    @Override
    public int getPriority ()
    {
        return Priority.REAL_TIME.getValue();
    }


    @Override
    protected void write () throws IOException
    {
        yos.writeUTF(context);
    }

}
