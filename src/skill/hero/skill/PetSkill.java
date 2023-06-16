package hero.skill;

import hero.pet.PetKind;
import hero.skill.detail.ESkillType;

public abstract class PetSkill implements Cloneable
{
	/**
	 * 编号
	 */
	public int id;
	/**
	 * 名称
	 */
	public String name;
	/**
	 * 等级
	 */
	public int level;
	/**
	 * 类型
	 * 1:主动
	 * 2:被动
	 */
//	public byte type;
	/**
	 * 获得方式
	 * 1:自动学得
	 * 2:技能书学得
	 */
	public byte getFrom;
	/**
	 * 所需等级
	 */
	public int needLevel;
	/**
	 * 宠物种类
	 */
	public PetKind petKind;
	/**
     * 图标编号
     */
    public short iconID;

    /**
     * 描述
     */
    public String description;
    
    public PetSkill(int id, String name){
    	this.id = id;
    	this.name = name;
    }
    
    /**
     * 是否同名技能
     * 
     * @param _otherSkill
     * @return
     */
    public boolean isSameName (PetSkill _otherSkill)
    {
        return name.equals(_otherSkill.name);
    }
    
    public PetSkill clone() throws CloneNotSupportedException{
    	PetSkill skill = (PetSkill)super.clone();
    	return skill;
    }
    
    public abstract ESkillType getType();
    
    /**
     * 是否是新技能
     * 默认是新技能
     */
    public boolean isNewSkill = true;
    /**
     * 如果是旧技能，此技能就是旧技能的升级
     * 旧技能的id
     * 默认为-1
     */
    public int _lowLevelSkillID = -1;
}
