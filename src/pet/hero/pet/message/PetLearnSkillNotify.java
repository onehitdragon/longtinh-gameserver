package hero.pet.message;

import hero.pet.Pet;
import hero.skill.PetActiveSkill;
import hero.skill.PetSkill;

import java.io.IOException;
import java.util.List;

import yoyo.core.packet.AbsResponseMessage;

import hero.skill.detail.ESkillType;

/**
 * 宠物学习技能通知 0x5613
 * @author jiaodongjie
 *
 */
public class PetLearnSkillNotify extends AbsResponseMessage
{
	Pet pet;
	List<PetSkill> learnPetSkillList;

	public PetLearnSkillNotify(Pet pet, List<PetSkill> learnPetSkillList){
		this.pet = pet;
		this.learnPetSkillList = learnPetSkillList;
	}
	
	@Override
	public int getPriority ()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void write () throws IOException
	{
		yos.writeInt(pet.id);
		yos.writeInt(learnPetSkillList.size());
		for(PetSkill skill : learnPetSkillList){
			yos.writeInt(skill.id);
			yos.writeUTF(skill.name);
			yos.writeInt(skill.level);
			yos.writeShort(skill.iconID);
            if(skill.getType() == ESkillType.ACTIVE){
                PetActiveSkill activeSkill = (PetActiveSkill)skill;
                yos.writeShort(activeSkill.releaseAnimationID);
                yos.writeShort(activeSkill.activeAnimationID);
            }else {
                yos.writeShort(0);
                yos.writeShort(0);
            }
		}
	}

}
