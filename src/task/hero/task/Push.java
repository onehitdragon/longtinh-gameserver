package hero.task;

public class Push {
	/**
	 * 常规模式计费模式
	 */
	public final static int         PUSH_TYPE_COMM = 0;
	/**
	 * 短信模式计费模式
	 */
	public final static int         PUSH_TYPE_SMS = 1;
	/**
	 * 移动计费平台代收费模式
	 */
	public final static int         PUSH_TYPE_MOBILE_PROXY = 2;
	
	/**
	 * 九天传奇代收费
	 */
	public final static int         PROXY_JTCQ_ID = 0;
	/**
	 * 英雄代收费
	 */
	public final static int         PROXY_HERO_ID = 1;
	/**
	 * 神州付代收费
	 */
	public final static int         PROXY_SZF_ID = 2;
	
	public int  					id;
	
	public int 						time;
	
	/**
	 * 点数
	 */
	public int 						point;
	
	/**
	 * 推广的物品ID
	 */
	public int 						goodsID;
	
	/**
	 * 正常模式推广语
	 */
	public String 					commPushContent;
	/**
	 * 正常模式2次确认
	 */
	public String 					commConfirmContent;
	
	/**
	 * 推广方式数量
	 */
	public int  					pushNum;
	/**
	 * 推广方式1
	 */
	public int[] 					pushType;
	
	/**
	 * 充值确认推广语1
	 */
	public String[] 				pushContent;
	
	/**
	 * 当天次数限制提示1
	 */
	public String[] 				limitContent;

}
