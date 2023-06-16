package hero.entrance;

import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 CountOnlineNumberOfPlayer.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-9-1 上午09:57:43
 * @描述 ：
 */

public class CountOnlineNumberOfPlayer implements Runnable
{
    private static Logger log = Logger.getLogger(CountOnlineNumberOfPlayer.class);
    public void run ()
    {
        log.info("统计线程已启动......");

        try
        {
            Thread.sleep(30000);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        while (true)
        {
            try
            {
                log.info("在线玩家数量："
                        + PlayerServiceImpl.getInstance().getPlayerList()
                        .size());

                Thread.sleep(60000);
            }
            catch (Exception e)
            {
                log.error("统计线程错误：" + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
