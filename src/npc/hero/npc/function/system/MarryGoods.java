package hero.npc.function.system;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 2010-11-11
 * Time: 20:04:24
 */
public enum MarryGoods{
    RANG(340004,"结婚戒指"),DIVORCE(340005,"离婚协议"),FORCE_DIVORCE(340006,"强制离婚证明"),TRANSPORT(340064,"夫妻传送符");

    private int id;
    private String name;

    MarryGoods(int _id, String _name){
        id = _id;
        name = _name;
    }

    public static MarryGoods getMarryGoodsByGoodsID(int id){
        for(MarryGoods goods : MarryGoods.values()){
            if(goods.id == id){
                return goods;
            }
        }
        return null;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
