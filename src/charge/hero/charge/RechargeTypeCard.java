package hero.charge;

/**
 *  @描述 ：充值卡类型
 **/

public enum RechargeTypeCard
{
    CMCC(0,"移动"),CU(1,"联通"), CT(2,"电信");

    private int id;
    private String name;

    RechargeTypeCard(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static RechargeTypeCard getCardTypeByName(String name){
        for(RechargeTypeCard card : RechargeTypeCard.values()){
            if(card.name.equals(name)){
                return card;
            }
        }
        return null;
    }

    public int getId(){
        return id;
    }
}


