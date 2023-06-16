package hero.skill.clienthandler;

import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.skill.service.SkillServiceImpl;
import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;

public class UpSkill extends AbsClientProcess {
     private static Logger log = Logger.getLogger(UpSkill.class);
	@Override
	public void read() throws Exception {

        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(contextData.sessionID);
        if (null == player)
        {
        	log.info("error: UpSkill.java -- 玩家对象为null");
            return;
        }

        byte what = yis.readByte();
        if(what == 1) {
        	int skillId = yis.readInt();
        	SkillServiceImpl.getInstance().learnSkill(player, skillId);
        } else {
        	SkillServiceImpl.getInstance().forgetSkill(player);
		}
	}

}
