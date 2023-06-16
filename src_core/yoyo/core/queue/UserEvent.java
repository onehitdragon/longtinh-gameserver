package yoyo.core.queue;

import java.util.ListIterator;
import javolution.util.FastList;
import java.lang.ref.WeakReference;

public class UserEvent
{
    protected static byte msgLevel = 0;
    protected int size = 0;
    private int freeSize;
    private FastList<byte[]>[] fList = new FastList[msgLevel];

    protected UserEvent()
    {
        for (int i = 0; i < msgLevel; i++)
        {
            fList[i] = new FastList<byte[]>();
        }
    }

    protected void addEvent (int level, byte[] bytes)
    {
        fList[level].addLast(bytes);
        size += bytes.length;
    }

    protected byte[] getEvent ()
    {
        for (int i = 0; i < msgLevel; i++)
        {
            if (!fList[i].isEmpty())
            {
                byte[] value = fList[i].removeFirst();
                size -= value.length;
                return value;
            }
        }
        return null;
    }

    protected int checkSize (int maxSize, int offSize)
    {
        int s = 0;
        int n = 0;
        for (int i = 0; i < fList.length; i++)
        {
            ListIterator<byte[]> iterator = fList[i].listIterator();
            for (int j = 0; iterator.hasNext(); j++)
            {
                s += ((byte[]) iterator.next()).length;
                if (s <= maxSize + offSize)
                {
                    n++;
                    if (s >= maxSize)
                    {
                        return n;
                    }
                }
                else
                {
                    return n;
                }
            }
        }

        freeSize = maxSize - n;
        return n;
    }

    protected int getSize ()
    {
        return size;
    }
    
    public int getFreeSize ()
    {
        return freeSize;
    }
    
    protected int[] getNum ()
    {
        int[] num = new int[fList.length];
        for (int i = 0; i < fList.length; i++)
        {
            num[i] = fList[i].size();
        }
        return num;
    }

    protected void clear ()
    {
        for (int i = 0; i < msgLevel; i++)
        {
            fList[i].clear();
        }
    }
}