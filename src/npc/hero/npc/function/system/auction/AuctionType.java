package hero.npc.function.system.auction;

/**
 * 拍卖物品类型 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public enum AuctionType
{
    /**
     * 武器
     */
    WEAPON((byte) 1),
    /**
     * 布甲
     */
    BU_JIA((byte) 2),
    /**
     * 轻甲
     */
    QING_JIA((byte) 3),
    /**
     * 重甲
     */
    ZHONG_JIA((byte) 4),
    /**
     * 配饰（戒指、项链、手镯）
     */
    PEI_SHI((byte) 5),
    /**
     * 药水
     */
    MEDICAMENT((byte) 6),
    /**
     * 材料
     */
    MATERIAL((byte) 7),
    /**
     * 特殊道具
     */
    SPECIAL((byte) 8);

    private byte id;

    AuctionType(byte _id)
    {
        id = _id;
    }

    public byte getID ()
    {
        return id;
    }

    public static AuctionType getType (byte _value)
    {
        if (_value == 1)
            return AuctionType.WEAPON;
        if (_value == 2)
            return AuctionType.BU_JIA;
        if (_value == 3)
            return AuctionType.QING_JIA;
        if (_value == 4)
            return AuctionType.ZHONG_JIA;
        if (_value == 5)
            return AuctionType.PEI_SHI;
        if (_value == 6)
            return AuctionType.MEDICAMENT;
        if (_value == 7)
            return AuctionType.MATERIAL;
        if (_value == 8)
            return AuctionType.SPECIAL;

        return null;
    }
}
