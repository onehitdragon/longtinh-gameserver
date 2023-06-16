package hero.pet.message;

import hero.pet.Pet;
import hero.skill.PetActiveSkill;
import hero.skill.PetPassiveSkill;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 显示宠物技能列表   0x5614
 * @author jiaodongjie
 *
 */
public class ResponseShowPetSkillList extends AbsResponseMessage
{
	private Pet pet;
	public ResponseShowPetSkillList(Pet pet){
		this.pet = pet;
	}
	@Override
	public int getPriority ()
	{
		return 0;
	}

	@Override
	protected void write () throws IOException
	{
		yos.writeInt(pet.id);
		yos.writeInt(pet.petActiveSkillList.size());
		for(PetActiveSkill skill : pet.petActiveSkillList){
			yos.writeInt(skill.id);
			yos.writeUTF(skill.name);
			yos.writeInt(skill.level);
			yos.writeShort(skill.iconID);
			yos.writeByte(skill.getType().value());
			yos.writeUTF(skill.description);

            yos.writeInt(skill.coolDownTime);
            yos.writeInt(skill.reduceCoolDownTime);
            yos.writeByte(skill.isNeedTarget());
            yos.writeByte(skill.targetType.value());
            yos.writeByte(skill.targetDistance);
            yos.writeInt(skill.magicHarmHpValue);
            yos.writeInt(skill.physicsHarmValue);
            yos.writeByte(skill.rangeTargetNumber);
            yos.writeInt(skill.resumeHp);
            yos.writeInt(skill.resumeMp);
            yos.writeInt(skill.useMp);
            
            yos.writeUTF(skill.magicHarmType.getName());

            yos.writeShort(skill.releaseAnimationID);
            yos.writeShort(skill.activeAnimationID);
		}
		
		yos.writeInt(pet.petPassiveSkillList.size());
		for(PetPassiveSkill skill : pet.petPassiveSkillList){
			yos.writeInt(skill.id);
			yos.writeUTF(skill.name);
			yos.writeInt(skill.level);
			yos.writeShort(skill.iconID);
			yos.writeUTF(skill.description);
		}
	}

}
