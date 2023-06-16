package hero.manufacture;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 10-12-22
 * Time: 下午8:22
 * To change this template use File | Settings | File Templates.
 */
public enum PurifyCategory {
    WQ((byte) 0, "武器"), KJ((byte) 1, "铠甲"), CL((byte) 2, "材料"), BS((byte)3,"宝石");
    byte            id;

    String          name;

    static String[] categorys = {WQ.name,KJ.name,CL.name,BS.name};

    PurifyCategory(byte id, String name) {
        this.id = id;
        this.name = name;
    }

    public byte getId(){
        return id;
    }

    public static PurifyCategory getPurifyCategory(String _name){
        for(PurifyCategory category : PurifyCategory.values()){
            if(category.name.equals(_name)){
                return category;
            }
        }
        return WQ;
    }
}
