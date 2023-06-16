package hero.log.service;

public enum LoctionLog
{
    BODY(0, "身上"), STORAGE(1, "仓库"), BAG(2, "背包"), PET_BODY(3,"宠物身上"), PET_BAG(4,"宠物背包");
    private int    id;

    private String name;

    LoctionLog(int _id, String _name)
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
