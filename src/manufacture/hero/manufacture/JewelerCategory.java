package hero.manufacture;

public enum JewelerCategory
{
    SS((byte) 0, "首饰"), CL((byte) 1, "材料");
    byte            id;

    String          name;

    static String[] categorys = {SS.name, CL.name };

    JewelerCategory(byte _id, String _name)
    {
        id = _id;
        name = _name;
    }

    public byte getId ()
    {
        return id;
    }

    public static JewelerCategory getCategory (String _name)
    {
        if (SS.name.equals(_name)) return SS;
        return CL;
    }

    public static String[] getCategorys ()
    {
        return categorys;
    }
}
