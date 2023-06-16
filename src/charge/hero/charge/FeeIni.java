package hero.charge;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-4-12
 * Time: 下午4:24
 * 计费配置信息
 */
public class FeeIni {
    //计费类型 szf=神州付；ng=网游；sms=短信；error:其它计费，按错误处理
    public static final String FEE_TYPE_NG = "ng";
    public static final String FEE_TYPE_SMS = "sms";
    public static final String FEE_TYPE_SZF = "szf";
    public static final String FEE_TYPE_ERROR = "error";

    // 计费接口id（短信为端口号）
    public static final String FEE_URL_ID_HERO = "hero";
    public static final String FEE_URL_ID_JIUTIAN = "jiutian";
//    public static final String FEE_URL_ID_SZF = "szf-yoyo";

    public int status; //状态：0:开放，1:关闭 ,2:未找到计费规则，3:请求参数错误，4、无对应计费源
    public String feeType;  //计费类型：szf=神州付；ng=网游；sms=短信；error:其它计费，按错误处理
    public String feeCode; //计费代码（网游道具ID；短信指令）
    public String feeUrlID; //计费接口id（短信为端口号）
    public int price; //单价(单位：元)

    public String transID; //流水号(现只用于任务计费)
    public int sumPrice; //此次计费总价
}
