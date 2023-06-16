package hero.share.message;

import hero.share.letter.Letter;
import hero.share.letter.LetterService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;

public class ResponseLetterList extends AbsResponseMessage
{
    private static Logger log = Logger.getLogger(ResponseLetterList.class);
    private ArrayList<Letter> letterList;

    public ResponseLetterList(ArrayList<Letter> _letterList)
    {
        letterList = _letterList;
    }

    @Override
    public int getPriority ()
    {
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        if (letterList == null)
        {
            yos.writeByte(0);

            return;
        }
        log.debug("response letter size = " + letterList.size());
        yos.writeByte(letterList.size());

        for (Letter l : letterList)
        {
            log.debug("letter id="+l.letterID+", title : "+l.title);
            yos.writeByte(l.type);//邮件类型 0:系统邮件  1:普通邮件
            yos.writeInt(l.letterID);
            yos.writeUTF(l.title);
            yos.writeUTF(l.senderName);
            yos.writeUTF(l.content);
            yos.writeUTF(getTime(l.sendTime));
            yos.writeByte(l.isRead ? 1 : 0);
            yos.writeByte(l.isSave ? 100 : lastDay(l.sendTime));
        }
    }

    private byte lastDay (long time)
    {
        long nowTime = System.currentTimeMillis();
        byte day = (byte) ((LetterService.SAVE_TIME - (nowTime - time)) / (24 * 60 * 60 * 1000));
        if (day < 1)
            day = 1;
        return day;
    }

    private String getTime (long time)
    {
        StringBuffer buf = new StringBuffer();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        buf.append(c.get(Calendar.YEAR) + "/");
        buf.append((c.get(Calendar.MONTH) + 1) + "/");
        buf.append(c.get(Calendar.DAY_OF_MONTH));
//        buf.append("\n");
//        buf.append(c.get(Calendar.HOUR_OF_DAY) + ":");
//        buf.append(c.get(Calendar.MINUTE));
        buf.delete(0,2);
        return buf.toString();
    }

}
