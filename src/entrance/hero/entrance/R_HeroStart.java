package hero.entrance;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.PriorityManager;
import yoyo.service.ServiceManager;
import yoyo.service.base.session.SessionServiceImpl;
import yoyo.tools.YOYOPrintStream;
import hero.map.broadcast.BroadcastTaskManager;
import hero.share.service.DisorderlyService;
import hero.share.service.LogWriter;

public class R_HeroStart
{
    // public static SessionAcceptor socketAcceptor =null;
    public static void main (String[] args) throws Exception
    {
        System.out.println("vinh");
        System.out.println(BroadcastTaskManager.class.getName());
        long startTime = System.currentTimeMillis();

        YOYOPrintStream.init();
        LogWriter.init();

        ResponseMessageQueue.getInstance();

        try
        {
            SessionServiceImpl.getInstance();
            PriorityManager.getInstance().load();
            ServiceManager.getInstance().load();
            BroadcastTaskManager.getInstance();
            DisorderlyService.getInstance().start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.gc();
        // new Thread(new CountOnlineNumberOfPlayer()).start();
        long freightTime = System.currentTimeMillis() - startTime;
        System.out.println("Server startup in " + freightTime + " ms");
        LogWriter.println("Server startup in " + freightTime + " ms");
    }
}
