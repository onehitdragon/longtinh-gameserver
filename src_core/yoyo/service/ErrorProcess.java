package yoyo.service;

import yoyo.core.process.AbsClientProcess;

public class ErrorProcess extends AbsClientProcess
{
    private String context;

    public ErrorProcess(String context)
    {
        this.context = context;
    }

    @Override
    public void read () throws Exception
    {
    }

}
