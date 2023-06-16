package hero.npc.dict;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 10-12-24
 * Time: 下午12:12
 */
public enum PickType {
    /**
     * 拾取：所有玩家都可以拾取
     */
    PICK("拾取"),
    /**
     * 采集需要学习采集技能
     */
    GATHER("采集");

    private String type;

    PickType(String type) {
        this.type = type;
    }

    static PickType getPickType(String typeName){
        for(PickType pickType : PickType.values()){
            if(pickType.type.equals(typeName)){
                return pickType;
            }
        }
        return  null;
    }

}
