package hero.skill.message;

import hero.player.HeroPlayer;
import hero.skill.ActiveSkill;
import hero.skill.unit.ActiveSkillUnit;

import java.io.IOException;
import java.util.ArrayList;

import yoyo.core.packet.AbsResponseMessage;


public class RefreshSkillTime extends AbsResponseMessage {
	
	private ArrayList<ActiveSkill> activeSkillList;
	
	public RefreshSkillTime (HeroPlayer _player) {
		activeSkillList = _player.activeSkillList;
	}
	

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void write() throws IOException {
		 ActiveSkillUnit skillUnit = null;
		 yos.writeShort(activeSkillList.size());
		 for (ActiveSkill skill : activeSkillList) {
			 yos.writeInt(skill.id);
			 skillUnit = (ActiveSkillUnit) skill.skillUnit;
			 yos.writeShort(skillUnit.releaseTime * 1000);
			 if (skillUnit.releaseTime > 0) 
			 {
				 System.out.println("狂暴后速度列表:"+skillUnit.releaseTime);
			}
		 }
		
	}

}
