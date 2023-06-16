package yoyo.core.event;

public abstract class AbsEvent<T>
{
    protected String src;

    protected String dest;

    protected T context;

    public AbsEvent()
    {
    }

    public AbsEvent(String source, String destination, T body)
    {
        this.src = source;
        this.dest = destination;
        this.context = body;
    }

    public String getSrc ()
    {
        return src;
    }

    public void setSrc (String src)
    {
        this.src = src;
    }

    public String getDest ()
    {
        return dest;
    }

    public void setDest (String dest)
    {
        this.dest = dest;
    }

    public T getContext ()
    {
        return this.context;
    }

    public void setContext (T coxt)
    {
        this.context = coxt;
    }
}
