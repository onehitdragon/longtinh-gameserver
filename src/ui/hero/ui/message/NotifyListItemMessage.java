package hero.ui.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


public class NotifyListItemMessage extends AbsResponseMessage
{
    private byte    step;

    private boolean isIndex;

    private int     itemID;

    /**
     * @param _step 当前界面的step
     * @param _isIndex 后面的itemID是否是索引，true-索引，false-选项ID
     * @param _itemID
     */
    public NotifyListItemMessage(byte _step, boolean _isIndex, int _itemID)
    {
        step = _step;
        isIndex = _isIndex;
        itemID = _itemID;
    }

    @Override
    public int getPriority ()
    {
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        yos.writeByte(step);
        yos.writeByte(isIndex ? 0 : 1);
        yos.writeInt(itemID);
    }

}
