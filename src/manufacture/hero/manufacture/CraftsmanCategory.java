package hero.manufacture;

public enum CraftsmanCategory
{
    WQ((byte) 0, "武器"), BJ((byte) 1, "布甲"), QJ((byte) 2, "轻甲"), CL((byte) 3,
            "材料");
    byte            id;

    String          name;

    static String[] categorys = {WQ.name, BJ.name, QJ.name, CL.name };

    CraftsmanCategory(byte _id, String _name)
    {
        id = _id;
        name = _name;
    }

    public byte getId ()
    {
        return id;
    }

    public static CraftsmanCategory getCategory (String _name)
    {
        if (WQ.name.equals(_name)) return WQ;
        if (BJ.name.equals(_name)) return BJ;
        if (QJ.name.equals(_name)) return QJ;
        return CL;
    }

    public static String[] getCategorys ()
    {
        return categorys;
    }
}
