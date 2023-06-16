package hero.gamepoint.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Properties;

import me2.core.queue.OutMesgQ;
import me2.service.ServiceMan;
import me2.service.basic.player.IPlayer;
import navy.charge.service.ChargeServiceImpl;
import navy.inter.charge.CacheItem;
import navy.inter.charge.ShoppingItem;
import navy.inter.charge.ShoppingMall;
import navy.inter.message.SystemInfoServerHandler;
import navy.player.actor.NavyPlayer;
import navy.player.service.PlayerServiceImpl;
import navy.task.clienthandler.BuyHandler;
import navy.util.LogUtil;
import navy.util.ME2Logger;
import navy.util.SessionUtil;
import navy.util.UserInfo;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * Description:<br>
 * 新的游戏点数系统
 * 
 * @author Johnny
 * @version 0.1
 */
public class GamePointService {
	private static GamePointService instance = null;

	public static String SERVICE_URL = ""; // "http://api.uen.cn/game/Point?";

	private static String ACTION_SUFIX_QUERY = "action=query&mid={0}&Sender={1}"; // 查询点数

	private static String ACTION_SUFIX_ADD = "action=add&mid={0}&point={1}&Sender={2}"; // 添加、扣除点数,当点数为负数时，扣除点数

	private static String ACTION_SUFIX_ZERO = "action=setzero&mid={0}&Sender={1}"; // 账号清零

	/**
	 * 流水号
	 */
	private static volatile int transID = 0;

	// private Map<String, TransID> transMap = Collections
	// .synchronizedMap(new HashMap<String, TransID>()); // 保存流水号

	private static NumberFormat format = new DecimalFormat("0000000000");

	// private int delay = 1000 * 60 * 30;

	private String CONFIG_FILE_PATH = ServiceMan.getHomeDir() + "conf"
			+ File.separator + "GamePoint.config";

	private String TRANS_FILE_PATH = ServiceMan.getHomeDir() + "conf"
			+ File.separator + "transid.db";

	private Properties properties = new Properties();

	/**
	 * 网元号
	 */
	public String SENDER = "";

	private GamePointService() {
		try {
			properties.load(new FileReader(CONFIG_FILE_PATH));
			SENDER = properties.getProperty("Sender");
			SERVICE_URL = properties.getProperty("GamePointURL");

			File file = new File(TRANS_FILE_PATH);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String currentTransID = reader.readLine();
			if (currentTransID != null)
				transID = Integer.parseInt(currentTransID);

		} catch (FileNotFoundException e) {
			//ME2Logger.error(this, e);
		} catch (IOException e) {
			//ME2Logger.error(this, e);
		}
	}

	public static GamePointService getInstance() {
		if (instance == null) {
			instance = new GamePointService();
		}
		return instance;
	}

	/**
	 * 返回计费流水号
	 * 
	 * @return
	 */
	public synchronized String getTransIDGen(String msidn) {
		transID++;
		String transGen = format.format(transID);

		storeIntoFile();
		LogUtil.transGenLog(msidn, transGen);

		return SENDER + transGen;
	}

	private void storeIntoFile() {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(TRANS_FILE_PATH));
			writer.write(String.valueOf(transID));
			writer.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 请求点数平台接口，充点
	 * http://192.168.0.33:5588/callPoint?trid=1234567890&result=0&price=2
	 * 
	 * @param map
	 * @return
	 */
	public String handlerRequest(Map<String, String> map) {
		// 流水号
		String s_trid = map.get("trid");
		// 是否成功 0-成功
		String s_result = map.get("result");
		// 点数
		String s_price = map.get("price");
		// 手机号
		String s_mid = map.get("mid");
		// userID
		// String uid = map.get("uid");
		// 触发点编号
		// @ TODO 默认100-充值
		if (s_trid == null) {
			ME2Logger.error("计费回调流水号为空");
			return "parameter trid is null";
		} else if (s_result == null) {
			ME2Logger.error("计费回调ResultCode为空");
			return "parameter result is null";
		} else if (s_price == null) {
			ME2Logger.error("计费回调点数为空");
			return "parameter price is null";
		} else if (s_mid == null) {
			ME2Logger.error("计费回调手机号码为空");
			return "parameter mid is null";
		}

		String trid = s_trid.trim();
		if (trid.length() > 10) {
			trid = trid.substring(0, 10);
		}
		UserInfo info = SessionUtil.getInstance().getUserInfo(s_trid, false);
		if (info == null)
			info = new UserInfo(0, "未知", "1500");
		int result = Integer.parseInt(s_result.trim());
		int price = Integer.parseInt(s_price.trim());
		String mid = s_mid.trim();
		String str = "(未知)";
		if (result == 0) {
			SessionUtil.getInstance().getUserInfo(s_trid, true);
			NavyPlayer player = null;
			IPlayer iplayer = PlayerServiceImpl.getInstance().getPlayerByUID(info.userID);
			if (iplayer != null) {
				player = (NavyPlayer) iplayer;
			}
			if (player != null) {
				if (ChargeServiceImpl.DL_CLIENT.equals(player.getPlatformInfo().getPublisher())) {
					str = "(当乐)";
				} else {
					str = "(运营)";
				}
			}
			//add by 黄树振-给玩家加点
			//------------------------------
			 Response response = GamePointService.getInstance().modify(
	                    player.getMsisdn(), price,
	                    GamePointService.getInstance().SENDER);
			 if (response != null && response.getResultCode().trim().equals("0"))
	         {
			     log.info("回调：流水号："+trid);
			    CacheItem item = (CacheItem)player.getPlayerSession().removeValue(SENDER+trid);
			    log.info("物品1："+item);
                if (item != null)
                {   
                    int sellpostion = item.getSellposition();
                    log.info("物品2："+sellpostion);
                    switch (sellpostion)
                    {
                        case 0:// 商城
                            ShoppingItem sitem = ShoppingMall.getInstance().getShoppingItem(item.getItemID());
                            ShoppingMall.getInstance().buyItem(player, sitem);
                            break;

                        case 1:// 主线任务中出售物品
                            BuyHandler.buyItem(player, item.getItemID(), item.getPrice());
                            break;
                    }
                }
                //通知玩家加点成功
                OutMesgQ.getInstance().put(player.getSession(), new SystemInfoServerHandler("恭喜您充值成功",1));
	         }
			 
			 //记录日志
             String _touchPoint = "200";
             if (player.getPlatformInfo().is139()){
                 _touchPoint = "300";
             }
             LogUtil.pointHandlerLog(response.getTransID(), _touchPoint,
                     player.getUserID(), player.getMsisdn(), price, true, response.getResultCode()
                          , "充点日志", player.getPlatformInfo()
                             .getPublisher());
			//------------------------------
			LogUtil.payRequestLog(trid, info.touchPoint, info.userID, s_mid,
							price, "API扣费" + str, "" + result, "计费回调成功",
							info.publisher);
			return "OK";
		} else {
			LogUtil.payRequestLog(trid, info.touchPoint, info.userID, s_mid,
							price, "API扣费" + str, "" + result, "计费回调失败",
							info.publisher);
			return "FAIL";
		}

	}

	/**
	 * 查询该手机号对应的游戏点数
	 * 
	 * @param _msisdn
	 *            手机号
	 * @param _sender
	 *            网元字典。具体定义参见 {@linkplain http://api.uen.cn/doc/Sender.dic}
	 * @return -1表示查询异常
	 */
	public Response query(String _msisdn, String _sender) {
		String actionSufix = MessageFormat.format(ACTION_SUFIX_QUERY, _msisdn,
				_sender);
		try {
			Response resp = request(actionSufix);
			return resp;
		} catch (Exception e) {
			ME2Logger.error(this, e);

		}
		return null;
	}
	
	   /**
     * 查询该手机号对应的游戏点数
     * 
     * @param _msisdn
     *            手机号
     * @param _sender
     *            网元字典。具体定义参见 {@linkplain http://api.uen.cn/doc/Sender.dic}
     * @return -1  表示查询异常
     */
	public int queryPoint(String _msisdn, String _sender) {
	        String actionSufix = MessageFormat.format(ACTION_SUFIX_QUERY, _msisdn,
	                _sender);
	        int point = -1;
	        try {
	            Response resp = request(actionSufix);
	            if (resp != null && resp.getResultCode().equals("0"))
	            {
	                point = resp.getPoint();
	            }
	        } catch (Exception e) {
	            ME2Logger.error(this, e);
	            point = -1;
	        }
	        return point;
	    }

	/**
	 * 修改玩家的点数
	 * 
	 * @param _msisdn
	 * @param _point
	 *            负数时，表示扣除；正数时，表示添加
	 * @param _sender
	 *            网元编号，定义见{@linkplain http://api.uen.cn/doc/Sender.dic}
	 * @see {@linkplain http://api.uen.cn/doc/Result.dic}
	 * @return 0 操作成功; 1 缺少参数 ;2 参数格式不对; 3 调用IP非法 ;4 调用次数过多 ;5 接口内部错误 ; <br>
	 *         6 数据被锁定; 7 点数不足本次操作 ;8 无对应记录 ;9 远端返回操作失败;NULL 出现异常
	 */
	public Response modify(String _msisdn, int _point, String _sender) {
		String sPoint = String.valueOf(_point);// 防止4位数的数字，如1000,被格式化成1,000

		String actionSufix = MessageFormat.format(ACTION_SUFIX_ADD, _msisdn,
				sPoint, _sender);
		try {
			Response resp = request(actionSufix);
			return resp;
		} catch (Exception e) {
			ME2Logger.error(this, e);
		}
		return null;
	}

	/**
	 * 将账号点数清零
	 * 
	 * @param _msisdn
	 * @param _sender
	 * @return
	 */
	public boolean reset(String _msisdn, String _sender) {
		String actionSufix = MessageFormat.format(ACTION_SUFIX_ZERO, _msisdn,
				_sender);
		try {
			Response resp = request(actionSufix);
			if (resp.getResultCode().equals("0")) {
				return true;
			} else {
				ME2Logger
						.println("GamePointService.reset():: Return error code ,raw resp:  "
								+ resp.rawRespXML);
				return false;
			}
		} catch (Exception e) {
			ME2Logger.error(this, e);
		}
		return false;
	}

	/**
	 * @param _actionSufix
	 * @return <code>
	 * 
	 * <?xml version="1.0" encoding="UTF-8"?>
	 <Response>
	 <ResultCode>0</ResultCode>
	 <Mid>13911098112</Mid>
	 <Point>1000</Point>
	 <State>0</State>
	 <TransID>1232421535352</TransID>
	 </Response>
	 * 
	 * </code><br>
	 * ResultCode: 0 操作成功 1 缺少参数 2 参数格式不对 3 调用IP非法 4 调用次数过多 5 接口内部错误 6 数据被锁定 7
	 * 点数不足本次操作 8 无对应记录 9 远端返回操作失败
	 * @throws IOException
	 */
	private Response request(String _actionSufix) throws IOException {
		URL Url = new URL(SERVICE_URL + _actionSufix);

		ME2Logger.info("GamePointService,request: " + Url.toString());
		HttpURLConnection connection = (HttpURLConnection) Url.openConnection();
		connection.setConnectTimeout(3000);
		// connection.setReadTimeout(3000);
		connection.setDoInput(true);
		connection.connect();

		InputStream in = connection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		StringBuffer resp = new StringBuffer();
		String temp = reader.readLine();
		for (; temp != null;) {
			resp.append(temp);
			temp = reader.readLine();
		}
		reader.close();
		in.close();
		connection.disconnect();

		ME2Logger.info("response: " + resp.toString());
		return parse(resp.toString());
	}

	private Response parse(String _xmlResp) {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(new ByteArrayInputStream(_xmlResp
					.getBytes("UTF-8")));
			Element root = document.getRootElement();
			Element eleResultCode = root.element("ResultCode");
			String resultCode = eleResultCode.getText();
			if (resultCode.equals("0")) {
				// Mid
				Element eleMid = root.element("Mid");
				String mid = eleMid.getText();
				// Point
				Element elePoint = root.element("Point");
				String point = elePoint.getText();
				// State
				Element eleState = root.element("State");
				String state = eleState.getText();
				// TransID
				Element eleTransID = root.element("TransID");
				String transID = eleTransID.getText();

				Response resp = new Response(resultCode, mid);
				resp.rawRespXML = _xmlResp;

				resp.setPoint(Integer.parseInt(point));
				resp.setState(state);
				resp.setTransID(transID);
				return resp;
			} else {
				Response resp = new Response(resultCode, null);
				return resp;
			}
		} catch (DocumentException e) {
			ME2Logger.println("GamePointService.parse():: 解析XML出错1,xml: "
					+ _xmlResp);
			ME2Logger.error(this, e);
		} catch (UnsupportedEncodingException e) {
			ME2Logger.println("GamePointService.parse():: 解析XML出错2,xml: "
					+ _xmlResp);
			ME2Logger.error(this, e);
		}
		return null;
	}

	/**
	 * Description:<br>
	 * 
	 * @author Johnny
	 * @version 0.1
	 */
	public static class Response {
		private String resultCode;

		private String msisdn;

		private int point;

		private String state;

		private String transID = null;

		private String rawRespXML, feeResult;

		public Response(String _resultCode, String _msisdn) {
			resultCode = _resultCode;
			msisdn = _msisdn;
		}

		/**
		 * 参见 {@linkplain http://api.uen.cn/doc/Result.dic}
		 * 
		 * @return 操作结果 :<br>
		 *         0 操作成功; 1 缺少参数 ;2 参数格式不对; 3 调用IP非法 ;4 调用次数过多 ;5 接口内部错误 ; <br>
		 *         6 数据被锁定; 7 点数不足本次操作 ;8 无对应记录 ;9 远端返回操作失败
		 */
		public String getResultCode() {
			return resultCode;
		}

		public String getMSISDN() {
			return msisdn;
		}

		void setPoint(int _point) {
			point = _point;
		}

		/**
		 * 当前点数
		 * 
		 * @return
		 */
		public int getPoint() {
			return point;
		}

		void setState(String _state) {
			state = _state;
		}

		public String getState() {
			return state;
		}

		public void setTransID(String _transID) {
			transID = _transID;
		}

		public String getTransID() {
			return transID;
		}

		public String getRawRespXML() {
			return rawRespXML;
		}

		public void setRawRespXML(String _rawRespXML) {
			rawRespXML = _rawRespXML;
		}

		public void setFeeResult(String _feeResult) {
			feeResult = _feeResult;
		}

		public String getFeeResult() {
			return feeResult;
		}
	}

	public static void main(String[] args) {
		// String testXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		// + "<Response>" + "<ResultCode>6</ResultCode>"
		// + "<Mid>13911098112</Mid>" + "<Point>1000</Point>"
		// + "<State>0</State>" + "<TransID>1232421535352</TransID>"
		// + "</Response>";
		//
		// GamePointService.getInstance().parse(testXML);
		// GamePointService.getInstance().query("13911098112", "101"); //13850
		Response res = GamePointService.getInstance().modify("15996238971", -5900, "202");
		log.info(res.getResultCode());
	}
}

class TransID {
	private String msidn;

	public TransID(String msidn, String transid, long time) {
		super();
		this.msidn = msidn;
		this.transid = transid;
		this.time = time;
	}

	public String getMsidn() {
		return msidn;
	}

	public void setMsidn(String msidn) {
		this.msidn = msidn;
	}

	public String getTransid() {
		return transid;
	}

	public void setTransid(String transid) {
		this.transid = transid;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	private String transid;

	private long time;
}
