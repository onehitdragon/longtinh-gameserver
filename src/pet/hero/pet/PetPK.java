package hero.pet;

public class PetPK
{	
	/**
     * 种类: 
     */
    public short kind;
	
	/**
     * 阶段: 0:蛋     1:幼年     2:成年
     */
    public short stage; 
    
    /**
     * 类型： 0:蛋   1:草食      2:肉食
     * 
     */
    public short type;
    
    

	public PetPK()
	{
	}

	public PetPK(short kind, short stage, short type)
	{
		this.kind = kind;
		this.stage = stage;
		this.type = type;
	}

	

	public short getKind ()
	{
		return kind;
	}

	public void setKind (short kind)
	{
		this.kind = kind;
	}

	public short getStage ()
	{
		return stage;
	}

	public void setStage (short stage)
	{
		this.stage = stage;
	}

	public short getType ()
	{
		return type;
	}

	public void setType (short type)
	{
		this.type = type;
	}
	
	/**
	 * 是否为0
	 * @return
	 */
	public boolean is0(){
		if(this.kind==0 && this.stage==0 && this.type==0)
			return true;
		return false;
	}

	@Override
	public int hashCode ()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + kind;
		result = prime * result + stage;
		result = prime * result + type;
		return result;
	}

	@Override
	public boolean equals (Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		PetPK other = (PetPK)obj;
		if(kind != other.kind)
			return false;
		if(stage != other.stage)
			return false;
		if(type != other.type)
			return false;
		return true;
	} 
    
    public String toString(){
    	StringBuilder sb = new StringBuilder();
    	sb.append(getKind()).append(getStage()).append(getType());
    	return sb.toString();
    }
    
    public int intValue(){
    	return Integer.parseInt(toString());
    }
}
