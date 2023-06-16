package hero.gamepoint.service;

import java.io.IOException;

import me2.core.data.ResultMessage;

/**
 * Description:<br>
 * 
 * @author JOJO
 * @version 0.1
 */
public class TransGenResultMessage extends ResultMessage
{
    private String transID;

    public TransGenResultMessage(String transID)
    {
        this.transID = transID;
    }

    /*
     * (non-Javadoc)
     * @see me2.core.data.ResultMessage#getPriority()
     */
    @Override
    public int getPriority ()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * @see me2.core.data.ResultMessage#write()
     */
    @Override
    protected void write () throws IOException
    {
        output.writeUTF(transID);
    }

}
