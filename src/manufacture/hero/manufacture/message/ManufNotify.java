package hero.manufacture.message;

import java.io.IOException;
import java.util.List;

import yoyo.core.packet.AbsResponseMessage;

import hero.manufacture.Manufacture;

public class ManufNotify extends AbsResponseMessage
{
//    private byte type;

    private List<Manufacture> manufList;

    public ManufNotify(List<Manufacture> _manufList)
    {
//        type = _type;
        this.manufList = _manufList;
    }

    @Override
    public int getPriority ()
    {
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        yos.writeByte(manufList.size());
        for(Manufacture manuf : manufList){
            yos.writeByte(manuf.getManufactureType().getID());
        }
    }

}
