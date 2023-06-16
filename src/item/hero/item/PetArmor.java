package hero.item;

import hero.share.CharacterDefine;
import hero.share.EMagic;
import hero.share.EVocation;
import hero.share.MagicFastnessList;

public class PetArmor extends PetEquipment
{
	
	public PetArmor()
	{
		durabilityConvertRate = PetEquipment.DURA_REDUCE_PARA_OF_ARMOR;
	}

	@Override
	public int getEquipmentType ()
	{
		// TODO Auto-generated method stub
		return PetEquipment.TYPE_ARMOR;
	}

	

	@Override
	public void initDescription ()
	{
		StringBuffer buff = new StringBuffer();

        buff.append(
                CharacterDefine.CHINESE_SPACE_ONE).append(
                getWearBodyPart().getDesc());
        buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(getNeedLevel())
                .append("级");

        if (bindType == BIND_TYPE_OF_WEAR)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append("装备后绑定");
        }
        else if (bindType == BIND_TYPE_OF_PICK)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append("拾取后绑定");
        }

        if (0 < atribute.stamina)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atribute.stamina).append(" 耐力");
        }
        if (0 < atribute.inte)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atribute.inte).append(" 智力");
        }
        if (0 < atribute.strength)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atribute.strength).append(" 力量");
        }
        if (0 < atribute.spirit)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atribute.spirit).append(" 精神");
        }
        if (0 < atribute.agility)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atribute.agility).append(" 敏捷");
        }
        if (0 < atribute.lucky)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atribute.lucky).append(" 幸运");
        }
        if (0 < atribute.hp)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atribute.hp).append(" 生命");
        }
        if (0 < atribute.mp)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atribute.mp).append(" 魔法");
        }

        if (0 < atribute.physicsDeathblowLevel)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atribute.physicsDeathblowLevel).append(" 物理爆击等级");
        }
        if (0 < atribute.magicDeathblowLevel)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atribute.magicDeathblowLevel).append(" 魔法爆击等级");
        }
        if (0 < atribute.hitLevel)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atribute.hitLevel).append(" 命中等级");
        }
        if (0 < atribute.duckLevel)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                    CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(
                    atribute.duckLevel).append(" 闪避等级");
        }
        if (0 < atribute.defense)
        {
            buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append("防御")
                    .append(CharacterDefine.CHINESE_SPACE_TWO).append(
                            CharacterDefine.DESC_COMPART_CHAR).append(
                            atribute.defense);
        }

        MagicFastnessList mfl = getMagicFastnessList();
        if (null != mfl)
        {
            int value = mfl.getEMagicFastnessValue(EMagic.SANCTITY);
            if (value > 0)
            {
                buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                        CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(value)
                        .append(" ").append(EMagic.SANCTITY.getName()).append(
                                "抗性");
            }
            value = mfl.getEMagicFastnessValue(EMagic.UMBRA);
            if (value > 0)
            {
                buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                        CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(value)
                        .append(" ").append(EMagic.UMBRA.getName())
                        .append("抗性");
            }
            value = mfl.getEMagicFastnessValue(EMagic.FIRE);
            if (value > 0)
            {
                buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                        CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(value)
                        .append(" ").append(EMagic.FIRE.getName()).append("抗性");
            }
            value = mfl.getEMagicFastnessValue(EMagic.WATER);
            if (value > 0)
            {
                buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                        CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(value)
                        .append(" ").append(EMagic.WATER.getName())
                        .append("抗性");
            }

            value = mfl.getEMagicFastnessValue(EMagic.SOIL);

            if (value > 0)
            {
                buff.append(CharacterDefine.DESC_NEW_LINE_CHAR).append(
                        CharacterDefine.DESC_ATTRIBUTE_MARK_CHAR).append(value)
                        .append(" ").append(EMagic.SOIL.getName()).append("抗性");
            }
        }

        description = buff.toString();
		
	}

	@Override
	public boolean canBeUse (EVocation evocation)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public short getAnimationID() {
		// TODO Auto-generated method stub
		return -1;
	}

}
