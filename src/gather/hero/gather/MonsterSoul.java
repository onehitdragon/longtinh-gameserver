package hero.gather;

/**
 * 怪物灵魂类 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class MonsterSoul
{
    /**
     * 灵魂ID
     */
    public int  soulID;

    /**
     * 数量
     */
    public byte num;

    public MonsterSoul(int _soulID)
    {
        soulID = _soulID;
        num = 1;
    }

    public MonsterSoul(int _soulID, byte _num)
    {
        soulID = _soulID;
        num = _num;
    }
}
