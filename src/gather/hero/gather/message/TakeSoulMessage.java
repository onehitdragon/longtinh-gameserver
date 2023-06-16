package hero.gather.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 战斗结束开始收集灵魂消息 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class TakeSoulMessage extends AbsResponseMessage
{
    private int monsterID;

    private int userID;

    public TakeSoulMessage(int _monsterID, int _userID)
    {
        monsterID = _monsterID;
        userID = _userID;
    }

    @Override
    public int getPriority ()
    {
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        yos.writeInt(monsterID);
        yos.writeInt(userID);
    }

}
