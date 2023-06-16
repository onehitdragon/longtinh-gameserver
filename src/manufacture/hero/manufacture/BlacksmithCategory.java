package hero.manufacture;

public enum BlacksmithCategory
{
    WQ((byte) 0, "武器"), KJ((byte) 1, "铠甲"), CL((byte) 2, "材料");
    byte            id;

    String          name;

    static String[] categorys = {WQ.name, KJ.name, CL.name };

    BlacksmithCategory(byte _id, String _name)
    {
        id = _id;
        name = _name;
    }

    public byte getId ()
    {
        return id;
    }

    public static BlacksmithCategory getCategory (String _name)
    {
        if (WQ.name.equals(_name)) return WQ;
        if (KJ.name.equals(_name)) return KJ;
        return CL;
    }
}
