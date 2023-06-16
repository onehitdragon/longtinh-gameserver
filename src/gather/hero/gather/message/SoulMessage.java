package hero.gather.message;

import hero.gather.MonsterSoul;
import hero.gather.dict.SoulInfo;
import hero.gather.dict.SoulInfoDict;

import java.io.IOException;
import java.util.ArrayList;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 灵魂列表消息 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class SoulMessage extends AbsResponseMessage
{
    private ArrayList<MonsterSoul> souls;

    public SoulMessage(ArrayList<MonsterSoul> _souls)
    {
        souls = _souls;
    }

    @Override
    public int getPriority ()
    {
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        yos.writeByte(souls.size());

        for (MonsterSoul s : souls)
        {
            SoulInfo soulInfo = SoulInfoDict.getInstance().getSoulInfoByID(
                    s.soulID);
            yos.writeInt(s.soulID);
            yos.writeUTF(soulInfo.soulName);
            yos.writeShort(soulInfo.soulIcon);
            yos.writeByte(s.num);
            yos.writeUTF(soulInfo.soulDes);
        }
    }

}
