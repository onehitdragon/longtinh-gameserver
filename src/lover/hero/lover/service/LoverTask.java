package hero.lover.service;

import java.util.TimerTask;

/**
 * 大榕树计划任务
 * 
 * @author Luke 陈路
 * @date Jul 30, 2009
 */
public class LoverTask extends TimerTask
{

    @Override
    public void run ()
    {
        // TODO Auto-generated method stub
        LoverDAO.deleteTimeOut();
        LoverServiceImpl.getInstance().getTimer().schedule(new LoverTask(),
                LoverServiceImpl.getInstance().getTomorrow());
    }

}
