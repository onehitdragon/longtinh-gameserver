package hero.item.detail;

public enum EPetBodyPartOfEquipment implements BodyPartOfEquipment
{
	/**
     * 头部
     */
    HEAD(0, "头部"),
    /**
     * 身躯
     */
    BODY(1,"身躯"),
    /**
     * 爪部
     */
    CLAW(2,"爪部"),
    /**
     * 尾部
     */
    TAIL(3,"尾部");
	
	/**
     * 身体部位标识
     */
    private int  bodyPartValue;

    /**
     * 身体部位描述
     */
    private String desc;

	private EPetBodyPartOfEquipment(int bodyPartValue, String desc)
	{
		this.bodyPartValue = bodyPartValue;
		this.desc = desc;
	}
    
	/**
	 * 根据部位标识获取
	 * @param bodyPartValue
	 * @return
	 */
    public static EPetBodyPartOfEquipment getBodyPart(int bodyPartValue){
    	for(EPetBodyPartOfEquipment part : EPetBodyPartOfEquipment.values()){
    		if(part.bodyPartValue == bodyPartValue){
    			return part;
    		}
    	}
    	return null;
    }
    
    /**
     * 根据描述获取
     * @param desc
     * @return
     */
    public static EPetBodyPartOfEquipment getBodyPart(String desc){
    	for(EPetBodyPartOfEquipment part : EPetBodyPartOfEquipment.values()){
    		if(part.desc.equals(desc)){
    			return part;
    		}
    	}
    	return null;
    }
    
    /**
     * 获取部位标识
     * 
     * @return
     */
    public int value ()
    {
        return bodyPartValue;
    }

    /**
     * 获取描述
     * 
     * @return
     */
    public String getDesc ()
    {
        return desc;
    }
}
