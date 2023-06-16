package hero.log.service;

public enum FlowLog
{
    GET(0, "获得"), LOSE(1, "失去");
    private int    id;

    private String name;

    FlowLog(int _id, String _name)
    {
        id = _id;
        name = _name;
    }

    public int getId ()
    {
        return id;
    }

    public String getName ()
    {
        return name;
    }
}
