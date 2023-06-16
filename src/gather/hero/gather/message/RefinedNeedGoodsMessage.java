package hero.gather.message;

import hero.gather.Gather;
import hero.gather.dict.SoulInfo;
import hero.gather.dict.SoulInfoDict;

import java.io.IOException;
import java.util.ArrayList;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 炼制需要的材料消息 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class RefinedNeedGoodsMessage extends AbsResponseMessage
{
    private int                 refinedID;

    private String              name;

    private String              des;

    private Gather              gather;

    private ArrayList<SoulInfo> soulsList;

    private ArrayList<Short>    soulsNums;

    public RefinedNeedGoodsMessage(int _refinedID, String _des, Gather _gather)
    {
        refinedID = _refinedID;
        des = _des;
        gather = _gather;
        soulsList = new ArrayList<SoulInfo>();
        soulsNums = new ArrayList<Short>();
    }

    public void addNeedSoul (int _soulID, short soulNum)
    {
        SoulInfo g = SoulInfoDict.getInstance().getSoulInfoByID(_soulID);
        soulsList.add(g);
        soulsNums.add(soulNum);
    }

    @Override
    public int getPriority ()
    {
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        yos.writeInt(refinedID);
        yos.writeUTF(des);
        yos.writeByte(soulsList.size());
        for (int i = 0; i < soulsList.size(); i++)
        {
            SoulInfo soul = soulsList.get(i);
            yos.writeShort(soul.soulIcon);
            yos.writeUTF(soul.soulName);
            yos.writeShort(soulsNums.get(i));
            int num = gather.getNumBySoulID(soul.soulID);
            yos.writeByte(num >= soulsNums.get(i) ? 1 : 0);
            yos.writeShort(num);
        }
    }

}
