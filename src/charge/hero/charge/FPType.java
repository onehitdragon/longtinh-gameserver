package hero.charge;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-4-13
 * Time: 下午5:12
 */
public enum FPType {
    CHARGE(1,"充值"),FEE(2,"计费");
    private int typeID;
    private String name;

    FPType(int typeID,String name) {
        this.typeID = typeID;
        this.name = name;
    }

    public static FPType getFPTypeByName(String name){
        for (FPType fpt : FPType.values()){
            if(fpt.name.equals(name)){
                return fpt;
            }
        }
        return null;
    }

    public static FPType getFPTypeByTypeID(int typeID){
        for (FPType fpt : FPType.values()){
            if(fpt.typeID == typeID){
                return fpt;
            }
        }
        return null;
    }

}
