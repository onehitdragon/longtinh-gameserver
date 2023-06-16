package hero.novice.clienthandler;

import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

import hero.novice.message.EndNoviceWizard;
import hero.novice.service.NoviceServiceImpl;
import hero.pet.PetPK;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ExitNoviceWizard.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-9-27 上午11:18:12
 * @描述 ：退出新手引导
 */

public class ExitNoviceWizard extends AbsClientProcess
{
	private static Logger log = Logger.getLogger(ExitNoviceWizard.class);
    @Override
    public void read () throws Exception
    {
    	/**
    	 * 老新手引导代码,已弃用.
    	 */
        // TODO Auto-generated method stub
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        byte tag = yis.readByte();
    }
}
