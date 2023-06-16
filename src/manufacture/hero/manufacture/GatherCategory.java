package hero.manufacture;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 10-12-22
 * Time: 下午8:16
 */
public enum GatherCategory {
    ORE((byte)0,"矿石"),HERBS((byte)1,"药草"),LEATHER((byte)2,"皮革");

    private byte id;
    private String name;

    static String[] categorys = {ORE.name,HERBS.name,LEATHER.name};

    GatherCategory(byte id, String name) {
        this.id = id;
        this.name = name;
    }

    public byte getId(){
        return id;
    }

    public static GatherCategory getGatherCategory(String _name){
        for(GatherCategory category : GatherCategory.values()){
            if(category.name.equals(_name)){
                return category;
            }
        }
        return ORE;
    }
}
