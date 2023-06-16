package hero.pet;

public enum FeedType
{	
	NORMAL((short)1,"普通饲料"),HERBIVORE((short)2,"草食成长饲料"),CARNIVORE((short)3,"肉食成长饲料"),
    DADIJH((short)4,"大地精华"),LYCZ((short)5,"龙涎草汁");

    private short typeID;
    private String typeName;

	FeedType(short typeID,String typeName)
	{
		this.typeID = typeID;
		this.typeName = typeName;
	}

    public short getTypeID(){
        return typeID;
    }
    public String getTypeName(){
        return typeName;
    }
    public static FeedType getFeedType(String name){
        for(FeedType type : FeedType.values()){
            if(type.getTypeName().equals(name)){
                return type;
            }
        }
        return null;
    }
    public static FeedType getFeedType(short typeID){
        for(FeedType type : FeedType.values()){
            if(type.getTypeID()==typeID){
                return type;
            }
        }
        return null;
    }
}
