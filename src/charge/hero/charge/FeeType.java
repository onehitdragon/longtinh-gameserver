package hero.charge;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-3-17
 * Time: 下午12:47
 * 计费类型
 */
public class FeeType {
    public byte id;
    public String name;
    public String desc;
    public FPType type;     // 充值、计费

    public RechargeTypeCard cardType;
}
