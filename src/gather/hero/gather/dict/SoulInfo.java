package hero.gather.dict;

/**
 * 灵魂数据模板类 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class SoulInfo
{
    public int    soulID;

    public String soulName;

    public short  soulIcon;

    public String soulDes;

    public SoulInfo(int _soulID, String _soulName, short _soulIcon,
            String _soulDes)
    {
        soulID = _soulID;
        soulName = _soulName;
        soulIcon = _soulIcon;
        soulDes = _soulDes;
    }
}
