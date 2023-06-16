package hero.log.service;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-3-28
 * Time: 下午5:11
 * 加点，扣点时的 servicetype
 * 与 xj_account 数据库里的 service_type 表对应，如果有修改，则都要修改
 *
 */
public enum ServiceType {

    BUY_TOOLS(1,"购买道具"),BAG_EXPAN(2,"背包扩展"),BUY(3,"购买"),GM(4,"GM添加"),OFFLINE_HOOK_EXP(5,"购买离线挂机经验"),
    CHARGEUP(6,"充值"),PRESENT(7,"充值赠送"),FEE(8,"计费"),ACTIVE_PRESENT(9,"活动赠送");

    private int id;
    private String name;

    ServiceType(int id, String name) {
        this.id = id;
        this.name = name;
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
