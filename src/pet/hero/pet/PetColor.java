package hero.pet;

public enum PetColor
{
	WHITE(1,"白色"),GOLDEN(2,"金色"),BLUE(3,"蓝色");
	
	private int id;
	private String color;
	
	PetColor(int id, String color){
		this.id = id;
		this.color = color;
	}

	public int getId ()
	{
		return id;
	}

	public void setId (int id)
	{
		this.id = id;
	}

	public String getColor ()
	{
		return color;
	}

	public void setColor (String color)
	{
		this.color = color;
	}
	
	public static PetColor get(int id){
		for(PetColor pc : PetColor.values()){
			if(pc.id == id){
				return pc;
			}
		}
		return WHITE;
	}
	
	/**
	 * 新手礼包里的宠物蛋 10%是金色，90%是白色
	 * @return
	 */
	public static PetColor getRandomPetEggColor(){
		int r = (int)(100 * Math.random());
		if(r <= 10)
			return GOLDEN;
		else
			return WHITE;
	}
}
