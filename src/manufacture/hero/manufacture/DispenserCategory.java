package hero.manufacture;

public enum DispenserCategory
{
    YS((byte) 0, "药水"), JZ((byte) 1, "卷轴"), CL((byte) 2, "材料");
    byte            id;

    String          name;

    static String[] categorys = {YS.name, JZ.name, CL.name };

    DispenserCategory(byte _id, String _name)
    {
        id = _id;
        name = _name;
    }

    public static DispenserCategory getCategory (String _name)
    {
        if (YS.name.equals(_name)) return YS;
        if (JZ.name.equals(_name)) return JZ;
        return CL;
    }

    public byte getId ()
    {
        return id;
    }

    public static String[] getCategorys ()
    {
        return categorys;
    }
}
