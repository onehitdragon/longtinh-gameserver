package hero.gather;

public enum RefinedCategory
{
    WZ((byte) 0, "物质"), JT((byte) 1, "晶体");
    byte                    id;

    String                  name;

    static String[] categorys = {WZ.name, JT.name };

    RefinedCategory(byte _id, String _name)
    {
        id = _id;
        name = _name;
    }
    
    public byte getId() {
        return id;
    }

    public static RefinedCategory getCategory (String _name)
    {
        if (WZ.name.equals(_name)) return WZ;
        return JT;
    }

    public static String[] getCategorys ()
    {
        return categorys;
    }
}
