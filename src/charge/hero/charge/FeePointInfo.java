package hero.charge;

/**
 *  计费信息
 * @author jiaodongjie
 *
 */
public class FeePointInfo
{
    public byte id; //id

    public String fpcode; //计费点代码
    
    public String name; //计费点名称

    public int  price;  // 计费点金额(元) ,充值的金额单位是 分

    public int presentPoint; //额外赠送的点数

    public byte typeID; //所属的计费类型

    public String desc;  //提示说明

    public FPType type; // 充值 、 计费

    public FeePointInfo clone() {
        FeePointInfo fpt = new FeePointInfo();
        fpt.id = this.id;
        fpt.fpcode = this.fpcode;
        fpt.name = this.name;
        fpt.price = this.price;
        fpt.presentPoint = this.presentPoint;
        fpt.typeID = this.typeID;
        fpt.desc = this.desc;
        fpt.type = this.type;
        return fpt;
    }
}


