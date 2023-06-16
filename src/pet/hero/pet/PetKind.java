package hero.pet;

public enum PetKind
{
	ALL((short)0,"all"),
	DANGKANG((short)1,"当康"),
	GULUNIAO((short)2,"咕噜鸟"),
	MENGYANHU((short)3,"梦魇虎"),
	DUJIAOSHOU((short)4,"独角兽"),
	XINGMAO((short)5,"熊猫"),
	QILIN((short)6,"麒麟"),
	LINGWENYANG((short)7,"灵纹羊"),
	ZHUHUAI((short)8,"诸怀");
	
	short kindId;
	String name;
	PetKind(short kindId, String name){
		this.kindId = kindId;
		this.name = name;
	}
	
	public static PetKind getPetKind(String kindName){
		for(PetKind kind : PetKind.values()){
			if(kind.name.equals(kindName)){
				return kind;
			}
		}
		return null;
	}
	
	public static PetKind getPetKind(short kindId){
		for(PetKind kind : PetKind.values()){
			if(kind.kindId == kindId){
				return kind;
			}
		}
		return null;
	}
	
	public short getKindID(){
		return kindId;
	}
}
