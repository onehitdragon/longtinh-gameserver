package yoyo.core.queue;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yoyo.core.packet.ResponseData;
import yoyo.core.packet.AbsResponseMessage;
import yoyo.service.YOYOSystem;
import yoyo.tools.Convertor;
import yoyo.tools.YOYOOutputStream;

public class ResponseMessageQueue
{
	private final Logger logger = LoggerFactory.getLogger(getClass());
    private static ResponseMessageQueue instance;
    
    private int maxItem = 2000;

    private int usableQueueIndex = 0;

    private int itemCount = 0;

    private int packetSize = 8000;

    private int packetOffSize = 500;

    private int  maxPacketSize = 500;

    private ResponseData rd_heart;

    private ResponseData rderror;
    
    private UserEvent[] userMsg = new UserEvent[maxItem];

    public static ResponseMessageQueue getInstance ()
    {
        if (instance == null)
        {
            instance = new ResponseMessageQueue();
        }
        return instance;
    }

    public static void destroy ()
    {
        if (instance != null)
        {
            instance.clear();
            instance = null;
        }
    }

    ResponseMessageQueue()
    {
        rd_heart = new ResponseData(new byte[]{1, 0, 2, 8, 0 });
        YOYOOutputStream output = null;
        try
        {
            output = new YOYOOutputStream();
            output.writeByte(1);
            output.writeShort(0);
            output.writeShort(1030);
            byte[] data = output.getBytes();
            Convertor.short2Bytes((short) (data.length - 3), data, 1);
            rderror = new ResponseData(data);
            rderror.setErrorMessage();
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
        finally
        {
            try
            {
                output.close();
            }
            catch (IOException e)
            {
            	e.printStackTrace();
            }
        }
        UserEvent.msgLevel = 3;
        clear();
        try
        {
            ResponseQueueConfig config = new ResponseQueueConfig(YOYOSystem.HOME+ "conf/server.xml");
            maxItem = config.maxItem;
            UserEvent.msgLevel = config.levelNum;
            packetSize = config.packetSize;
            packetOffSize = config.packetOffSize;
            maxPacketSize = config.maxPacketSize;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public synchronized int createItem ()
    {
        int size = 0;
        int msgQIndex = -1;

        if (usableQueueIndex >= maxItem)
        {
            usableQueueIndex = 0;
        }

        while (size < maxItem)
        {
            if (userMsg[usableQueueIndex++] == null)
            {
                msgQIndex = usableQueueIndex - 1;
                userMsg[msgQIndex] = new UserEvent();
                
                itemCount++;

                break;
            }

            size++;
        }
       
        return msgQIndex;
    }

    public synchronized void removeItem(int index)
    {
        if (index >= 0 && index < maxItem && userMsg[index] != null)
        {
            userMsg[index].clear();
            userMsg[index] = null;
            usableQueueIndex = index;
            itemCount--;
        }
    }

    public ResponseData getErrorData ()
    {
        return rderror;
    }

    public boolean put(int index, AbsResponseMessage event)
    {
        if (index < 0 || index >= maxItem)
        {
            return false;
        }
        if (userMsg[index] == null)
        {
            return false;
        }
        try
        {
        	if (event.getSize() >= 1024*20) 
        	{
				logger.warn("警告:单个消息超过20k;size=" + String.valueOf(event.getSize()));
			}
            synchronized (userMsg[index])
            {
                if (userMsg[index].getSize() < maxPacketSize)
                {
                    userMsg[index].addEvent(event.getPriority(), event.getBytes());
                    return true;
                }
                else 
                {
					logger.warn("严重警告:玩家队列超过最大BYTE,队列ID=" 
							+ String.valueOf(index) 
							+ "  size:" + String.valueOf(userMsg[index].getSize()));
				}
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public ResponseData get(int index)
    {
        if (index < 0 || index >= maxItem)
        {
        	logger.warn("!!!to yoyo-->获得消息ID非法,通知客户端断开 msgQueueIndex=" + index);
            return rderror;
        }
        if (userMsg[index] == null)
        {
        	logger.warn("!!!to yoyo-->通过消息ID获得玩家队列为空,通知客户端断开 msgQueueIndex=" + index);
            return rderror;
        }
        synchronized (userMsg[index])
        {
            int queueNum = userMsg[index].checkSize(packetSize,packetOffSize);
            YOYOOutputStream output = new YOYOOutputStream();
            if (queueNum > 0)
            {
                try
                {
                    output.writeByte(queueNum);

                    for (int i = 0; i < queueNum; i++)
                    {
                        byte[] temp = userMsg[index].getEvent();
                        output.writeBytes(temp);
                    }
                    output.flush();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    try
                    {
                        output.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                return new ResponseData(output.getBytes());
            }
            else
            {
                return rd_heart;
            }
        }
    }

    public int[] getEventNum (int sid)
    {
        if (userMsg[sid] != null)
        {
            synchronized (userMsg[sid])
            {
                return userMsg[sid].getNum();
            }
        }
        return null;
    }

    private void clear ()
    {
        for (int i = maxItem - 1; i >= 0; i--)
        {
            if (userMsg[i] != null)
            {
                userMsg[i].clear();
                userMsg[i] = null;
            }
        }
        itemCount = 0;
    }
}
