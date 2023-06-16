package hero.pet.message;

import hero.pet.Pet;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 宠物装备上装备后，刷新宠物的属性值
 * 在游戏时宠物不显示身上的装备，所以不用宠物装备的图片
 * @author jiaodongjie
 *
 */
public class ResponseRefreshPetProperty extends AbsResponseMessage
{
	private Pet pet;
	public ResponseRefreshPetProperty(Pet pet){
		this.pet = pet;
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
		yos.writeInt(pet.getToNextLevelNeedTime());
		
		yos.writeInt(pet.mp);
		
		yos.writeInt(pet.str);
		yos.writeInt(pet.agi);
		yos.writeInt(pet.spi);
		yos.writeInt(pet.intel);
		yos.writeInt(pet.luck);
		
		yos.writeShort(pet.getBaseAttackImmobilityTime());
        yos.writeByte(pet.getAttackRange());
        
        yos.writeShort(pet.pk.getType());
        yos.writeUTF(pet.name);
        yos.writeInt(pet.level);
        yos.writeInt(pet.feeding);
        yos.writeInt(pet.fight_exp);
        yos.writeInt(pet.wit);
        yos.writeInt(pet.agile);
        yos.writeInt(pet.rage);
        
        yos.writeInt(pet.getATK());
        yos.writeInt(pet.getMagicHarm());
        yos.writeInt(pet.getSpeed());
        yos.writeInt(pet.hitLevel);
        yos.writeInt(pet.physicsDeathblowLevel);
        yos.writeInt(pet.magicDeathblowLevel);
	}

}
