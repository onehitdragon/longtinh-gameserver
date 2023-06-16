package hero.gather.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 成功使用葫芦消息 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class UseGourdMessage extends AbsResponseMessage
{
    private int monsterID;

    public UseGourdMessage(int _monsterID)
    {
        monsterID = _monsterID;
    }

    @Override
    public int getPriority ()
    {
        // TODO 自动生成方法存根
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        yos.writeInt(monsterID);
    }

}
