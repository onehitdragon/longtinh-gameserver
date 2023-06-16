package hero.charge.service;

import hero.charge.*;
import hero.charge.message.PointAmountNotify;
import hero.gm.service.GmDAO;
import hero.gm.service.GmServiceImpl;
import hero.log.service.LogServiceImpl;
import hero.log.service.ServiceType;
import hero.player.HeroPlayer;
import hero.player.LoginInfo;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.LogWriter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import hero.share.service.Tip;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.AbsServiceAdaptor;
import yoyo.service.base.session.Session;


/**
 * 神州付充值不存在多次请求情况
    网游存在有多次请求，每次请求都会同步返回结果
    短信存在多次请求，多次请求都完成后，统一返回一个结果
 */
public class ChargeServiceImpl extends AbsServiceAdaptor<ChargeConfig> {
    private static Logger log = Logger.getLogger(ChargeServiceImpl.class);

    private static ChargeServiceImpl instance;
//  public static  int transID;

    private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");


    private List<RequestInfo> infoList;

    private List<FeeType> feeTypeList = new ArrayList();
    private List<FeePointInfo> fpList = new ArrayList();

    private static final byte chargepid = 1;
    public static final byte PAYTYPE_SZF = 1;

    public static final byte PAYTYPE_NG = 2;
    public static final byte RECHARGE_TYPE_ME = 1;
    public static final byte RECHARGE_TYPE_OTHER = 2;

    private static final String REQUEST_METHOD_POST = "POST";
    private static final String REQUEST_METHOD_GET = "GET";

    /**
     * 充完加点即扣
     */
    public static final String CK = "ck";
    /**
     * 充完加点不扣
     */
    public static final String CA = "ca";
    /**
     * 充值类型
     */
    public static final String CHARGE_TYPE = "szf";

    static volatile int transid=0;
    private static NumberFormat numberFormat = new DecimalFormat("000000");

    private ChargeServiceImpl() {
        this.config = new ChargeConfig();
        try {
            this.infoList = new ArrayList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ChargeServiceImpl getInstance() {
        if (instance == null) {
            instance = new ChargeServiceImpl();
        }
        return instance;
    }

    public synchronized String getTransIDGen() {
        String transID = GmServiceImpl.gameID + "-" + GmServiceImpl.serverID + "-" + format.format(new Date());
        log.debug("transID = " + transID);
        return transID;
    }

    public void createSession(Session _session) {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_session.userID);

        if (null != player) {
        	ChargeDAO.loadTimeInfo(player);
        }
    }

    public void start() {
        try {
            readChargeList();

            MallGoodsDict.getInstance().load(config.mall_goods_data_path);
            //经验启动
            ExperienceBookService.getInstance().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readChargeList() {
        File dataPath;
        try {
            dataPath = new File(config.url_charge_info_path);
            File[] dataFileList = dataPath.listFiles();

            for (File dataFile : dataFileList) {
                if (!(dataFile.getName().endsWith(".xml"))) {
                    continue;
                }
                SAXReader reader = new SAXReader();
                Document document = reader.read(dataFile);

                Element root = document.getRootElement();
                Iterator eit = root.elementIterator();

                String data;

                while (eit.hasNext()) {
                    FeePointInfo fpInfo = new FeePointInfo();
                    Element subE = (Element) eit.next();
                    fpInfo.id = Byte.parseByte(subE.elementTextTrim("id"));
                    fpInfo.fpcode = subE.elementTextTrim("fpcode");
                    fpInfo.name = subE.elementTextTrim("name");
                    fpInfo.price = Integer.parseInt(subE.elementTextTrim("price"));
                    fpInfo.presentPoint = Integer.parseInt(subE.elementTextTrim("present_point"));
                    fpInfo.typeID = Byte.parseByte(subE.elementTextTrim("typeID"));
                    data = subE.elementTextTrim("feetype");
                    fpInfo.type = FPType.getFPTypeByName(data);
                    fpInfo.desc = subE.elementTextTrim("desc");
                    log.debug("fp name="+fpInfo.name+",price="+fpInfo.price+",type="+fpInfo.type);
                    fpList.add(fpInfo);
                }


            }

            File dataPath2 = new File(config.url_charge_type_path);
            log.debug("datapath2 = " + dataPath2);
            File[] dataFileList2 = dataPath2.listFiles();
            log.debug("dataFileList2 size = " + dataFileList2.length);

            for (File dataFile : dataFileList2) {
                if (!(dataFile.getName().endsWith(".xml"))) {
                    continue;
                }
                SAXReader reader = new SAXReader();
                Document document = reader.read(dataFile);

                Element root = document.getRootElement();
                Iterator eit = root.elementIterator();

                String data;

                while (eit.hasNext()) {
                    FeeType feeType = new FeeType();
                    Element subE = (Element) eit.next();
                    feeType.id = Byte.parseByte(subE.elementTextTrim("id"));
                    feeType.name = subE.elementTextTrim("name");

                    data = subE.elementTextTrim("card_type");
                    if(data != null){
                        feeType.cardType = RechargeTypeCard.getCardTypeByName(data);
                    }

                    feeType.desc = subE.elementTextTrim("desc");
                    data = subE.elementTextTrim("feetype");

                    feeType.type = FPType.getFPTypeByName(data);
                    log.debug("feetype name="+feeType.name+",cardtype="+feeType.cardType+",type="+feeType.type);
                    feeTypeList.add(feeType);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("fpList size="+fpList.size());
        log.info("feeTypeList size ="+feeTypeList.size());
    }

    /**
     * 根据计费类型和价格获取计费伪码
     * @param type
     * @param price
     * @return
     */
    public FeePointInfo getFpcodeByTypeAndPrice(FPType type,int price){
        for (FeePointInfo fpt : fpList){
            if(fpt.type == type && fpt.price==price){
                return fpt;
            }
        }
        return null;
    }

    public FeePointInfo getFpcodeByTypeAndPrice(byte id){
        for (FeePointInfo fpt : fpList){
            if(fpt.id == id){
                return fpt;
            }
        }
        return null;
    }

    /**
     * 根据计费类型和价格和计费伪码获取计费伪码
     * @param fpcode
     * @param price
     * @param type
     * @return
     */
    public FeePointInfo getFpInfoByFpcodeAndPrice(String fpcode,int price,FPType type){
        for (FeePointInfo fpt : fpList){
            if(fpt.fpcode.equals(fpcode) && fpt.price==price && fpt.type == type){
                return fpt;
            }
        }
        return null;
    }


    /**
     * 获取充值计费类型列表
     * @return
     */
    public List<FeeType> getFeeTypeListForRecharge() {
        List<FeeType> rechargeFeeList = new ArrayList<FeeType>();
        for(FeeType feeType : feeTypeList){
            if(feeType.type == FPType.CHARGE){
                rechargeFeeList.add(feeType);
            }
        }
        log.info("rechargeFeeList size="+rechargeFeeList.size());
        return rechargeFeeList;
    }

    /**
     * 获取充值计费信息列表
     * @return
     */
    public List<FeePointInfo> getFpListForRecharge() {
        List<FeePointInfo> rechargeFeePointInfoList = new ArrayList<FeePointInfo>();

        for(FeePointInfo fpt : fpList){
            FeePointInfo fptx = fpt.clone();
            if(fptx.type == FPType.CHARGE){
                int presentPoint = GmDAO.getPresentPoint(fptx.price);
                if(presentPoint>0){
                    fptx.name += "（活动赠送 "+presentPoint+" 点）";
                }
                rechargeFeePointInfoList.add(fptx);
            }
        }
        log.info("rechargeFeePointInfoList size="+rechargeFeePointInfoList.size());
        return rechargeFeePointInfoList;
    }

    public FeePointInfo getFeePointInfoById(byte id) {
        for (FeePointInfo fp : fpList) {
            if (fp.id == id) {
                return fp;
            }
        }
        return null;
    }

    public FeeType getFeeTypeById(byte id){
        for(FeeType feeType : feeTypeList){
            if(feeType.id == id){
                return feeType;
            }
        }
        return null;
    }

    /**
     * 给任务计费的接口
     * @param accountID  账号ID
     * @param userID    角色ID
     * @param swcode    软件代码(客户端版本号即可)
     * @param fpcode   计费伪码
     * @param msisdn   手机号
     * @param mobileUserID 从移动返回的 userid
     * @param publisher  渠道号(客户端上传)
     * @param sumPrice 此次计费总价
     * @return  FeeIni 计费配置信息，如果 feeIni.status>0 则获取计费配置失败
     */
    public FeeIni getFeeIniForTask(int accountID,int userID,String swcode,String msisdn,
                                   String mobileUserID,int publisher,int sumPrice){
        FeeIni feeIni = new FeeIni();
        String paytransid = getTransIDGen();
        log.debug("给任务计费的接口,sumprice="+sumPrice);
        FeePointInfo feePointInfo = getFpcodeByTypeAndPrice(FPType.FEE,sumPrice);
        log.debug("feepointInfo = " + feePointInfo);
        if(feePointInfo == null){
            feeIni.status = 1;
            return feeIni;
        }
        String fpcode = feePointInfo.fpcode;
        log.debug("给任务计费的接口,根据价格获取的计费伪码:fpcode="+fpcode);
        feeIni = getFeeIni(accountID,userID,swcode,fpcode,paytransid,msisdn,mobileUserID,publisher,CK);
        feeIni.transID = paytransid;
        feeIni.sumPrice = sumPrice;

        if(feeIni.status==0 && feeIni.feeType.equals(FeeIni.FEE_TYPE_SMS)){
            saveFeeIniInfo(accountID,userID,mobileUserID,sumPrice,paytransid);
        }
        return feeIni;
    }

    /**
     * 保存获取的短信计费信息
     * xj_account数据库里的
     * @param accountID
     * @param userID
     * @param msisdn
     * @param mobileUserID
     * @param publisher
     * @param sumPrice
     * @param transID
     */
    public void saveFeeIniInfo(int accountID,int userID,String mobileUserID,int sumPrice,String transID){
        ChargeDAO.saveSmsFeeIni(accountID,userID,transID,mobileUserID,sumPrice,GmServiceImpl.serverID);
    }

    /**
     * 获取计费配置信息
     * @param accountID  用户帐号
     * @param userID  角色id
     * @param swcode  软件代码
     * @param fpcode  计费点伪码
     * @param paytransid  流水号
     * @param msisdn    手机号
     * @param mobileUserID  移动平台返回的userid
     * @param publisher  渠道id
     * @return   成功返回：0#计费类型#计费代码（网游道具ID；短信指令）#计费接口id（短信为端口号）#单价
     *            失败只返回非0值
     * 状态：0:开放，1:关闭 ,2:未找到计费规则，3:请求参数错误，4、无对应计费源
     */
    public FeeIni getFeeIni(int accountID,int userID,String swcode,String fpcode,String paytransid,
                            String msisdn,String mobileUserID,int publisher,String dopoint){
        FeeIni ini = new FeeIni();

        String url = ChargeServiceImpl.getInstance().getConfig().fee_ini_url;
//        String url = "http://119.161.225.114:10086/ota/paypan/getfeeini.php";
        StringBuffer sf = new StringBuffer();
        sf.append("mid=").append(accountID)
            .append("&")
            .append("userid=").append(accountID)
            .append("&")
            .append("roleid=").append(userID)
            .append("&")
            .append("swcode=").append(swcode)
            .append("&")
            .append("fpcode=").append(fpcode)
            .append("&")
            .append("paytransid=").append(paytransid)
            .append("&")
            .append("msisdn=").append(msisdn)
            .append("&")
            .append("user_id=").append(mobileUserID)
            .append("&")
            .append("ditchid=").append(publisher)
            .append("&")
            .append("gameid=").append(GmServiceImpl.gameID)
            .append("&")
            .append("serverid=").append(GmServiceImpl.serverID);
        log.info("get fee ini parsm="+sf);
        try{
            //mid=47&userid=47&roleid=1061&swcode=2.0.0&fpcode=10022&paytransid=1-2-20110428034457&msisdn=null&user_id=210811121&ditchid=1001&gameid=1&serverid=2
        String result = requestUrl(url+"?"+sf.toString(),null,false,REQUEST_METHOD_GET);
        if(result.trim().length()>0){
            if(result.substring(0,1).equals("0")){//成功返回
                String[] resArr = result.split("#");
                ini.status = Integer.parseInt(resArr[0]);
                ini.feeType = resArr[1];
                ini.feeCode = resArr[2];
                ini.feeUrlID = resArr[3];
                ini.price = Integer.parseInt(resArr[4]);
            }else { //失败只返回非0值
                ini.status = Integer.parseInt(result);
            }
        }else {
            ini.status = 1;
        }
        }catch (Exception e){
            ini.status = 2;
            log.error("获取计费配置信息 error: ",e);
        }
        return ini;
    }


    /**
     * 神州付充值
     *
     * @param price 支付金额(目前是和面值一样)
     * @param transID
     * @param mid
     * @param userID
     * @param accountID
     * @param cardnum   充值卡卡号
     * @param cardpass  充值卡密码
     * @param cardsum  充值卡面值
     * @param cardtypecombine  //充值卡类型 0:移动；1:联通 ；2:电信
     * @param msisdn
     * @return 同步返回: 订单号#状态码#状态信息（响应码200时会异步同步充值结果）
     *          如果是当乐神州付返回： 00000000#当乐自定义状态码#
     */
    public String chargeUpSZF(String transID, int mid, int userID, int accountID, String cardnum, String cardpass,
                              int cardsum, byte cardtypecombine, String msisdn, int price, ServiceType serviceType,
                              int publisher,String ip,String bindmsisdn) {
        String szfurl = config.szf_rechange_url;

//      String szfurl = "http://192.168.0.66/shenzhoufu/pay.php";
//        String szfurl = "http://112.25.14.24/paybycard/pay.php";
        if(ip == null || ip.trim().length()==0){
            ip = "127.0.0.1";
        }

        String utp = "0";
        if(msisdn.startsWith("189")){
            utp = "1";
        }

        price = cardsum*100;

        /*if(publisher != 1){ // todo 测试当乐充值，当乐充值必须全部充完 正式公测后删除
            price = cardsum;
        }*/

        StringBuffer sf = new StringBuffer();
        sf.append("utp=").append(utp)
            .append("&")
            .append("uip=").append(ip)
            .append("&")
            .append("feetype=").append(CHARGE_TYPE)
            .append("&")
            .append("paymoney=").append(price) //支付金额(单位:分) todo 正式应该*100
            .append("&")
            .append("cardsum=").append(cardsum)
            .append("&")
            .append("cardnum=").append(cardnum)
            .append("&")
            .append("cardpass=").append(cardpass)
            .append("&")
            .append("cardtypecombine=").append(cardtypecombine)
            .append("&")
            .append("paytransid=").append(transID)
            .append("&")
            .append("mid=").append(accountID)
            .append("&")
            .append("userid=").append(accountID)
            .append("&")
            .append("roleid=").append(userID)
            .append("&")
            .append("gameid=").append(GmServiceImpl.gameID)
            .append("&")
            .append("serverid=").append(GmServiceImpl.serverID)
            .append("&")
            .append("bindmobile=").append(bindmsisdn)
            .append("&")
            .append("mobile=").append(msisdn)
            .append("&")
            .append("pid=").append(publisher)//.append(chargepid)  todo 先写成和channel_id一样的，到周一(4.25)商定一下
            .append("&")
            .append("servicetype=").append(serviceType.getId())
            .append("&")
            .append("channel_id=").append(publisher)
            .append("&")
            .append("dopoint=").append(CA);
        log.info("szf recharge parmas="+sf.toString());
        try {
            URL url = new URL(szfurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Charset", "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(sf.toString());
            writer.flush();

            int responseCode = conn.getResponseCode();
            log.info("responsecode = " + responseCode);

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = reader.readLine();
            /*String result = "";
            while ((line = reader.readLine()) != null){
                log.info("神州付充值结果: " + line);
                if(line.indexOf("#")>0){
                    result = line;
                }
            }*/

            line = line.replaceAll("\r\n", "");
//            result = result.replaceAll("\r\n", "");
            reader.close();
            conn.disconnect();
            log.info("神州付充值同步返回的结果: " + line);
            return line;
//            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 网游充值
     *
     * @param chargeid
     * @param transID
     * @param mid       int
     * @param accountID
     * @param userID
     * @param msisdn
     * @param dopoint   点数处理方式（ck：充完加点即扣；ca充完加点不扣）(充值时应该是 ca，消费是应该是 ck)
     * @return 0#状态码（成功）；失败 1#状态码 （自定义状态码004:计费参数不全）
     */
    public String chargeUpNg(String chargeid, String transID, int mid, int accountID, int userID, String msisdn,String bindMsisdn, ServiceType serviceType, int publisher, String dopoint) {
        String ngurl = config.feeIdsMap.get(chargeid);
        StringBuffer sf = new StringBuffer();
        sf.append("paytransid=").append(transID)
                .append("&")
                .append("mid=").append(accountID)
                .append("&")
                .append("userid=").append(accountID)
                .append("&")
                .append("roleid=").append(userID)
                .append("&")
                .append("gameid=").append(GmServiceImpl.gameID)
                .append("&")
                .append("serverid=").append(GmServiceImpl.serverID)
                .append("&")
                .append("mobile=").append(msisdn)
                .append("&")
                .append("bindmobile=").append(bindMsisdn)
                .append("&")
                .append("servicetype=").append(serviceType.getId())
                .append("&")
                .append("channel_id=").append(publisher)
                .append("&")
                .append("pid=").append(chargepid)
                .append("&")
                .append("dopoint=").append(dopoint);

        return requestUrl(ngurl, sf.toString(), false,REQUEST_METHOD_GET);
    }

    /**
     * 公共请求方法
     *
     * @param requestUrl
     * @param param
     * @param moreline   结果是否是多行
     * @return
     */

    private String requestUrl(String requestUrl, String param, boolean moreline){
        return requestUrl(requestUrl,param,moreline, REQUEST_METHOD_POST);
    }

    private String requestUrl(String requestUrl, String param, boolean moreline,String requestMethod) {
        StringBuffer sf = new StringBuffer("");
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod(requestMethod);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setDefaultUseCaches(false);

            if(requestMethod.equals(REQUEST_METHOD_POST)){
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(param);
                writer.flush();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            log.info("request url = " + requestUrl);
            while ((line = reader.readLine()) != null) {
                sf.append(line);
                if (moreline) sf.append("$$");
                log.info("response str = " + line);
            }
            reader.close();
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sf.toString().replaceAll("\r\n", "");
    }

    /**
     * 查询点数余额
     *
     * @param accountID
     * @return 成功返回：0#余额
     *         失败返回：1#对应错误提示信息
     */
    public String queryBalancePoint(int accountID) {
        String url = config.query_point_url;
//    String url = "http://112.25.14.24/usercenter/querypoint.php";
        StringBuffer sf = new StringBuffer();
        sf.append("userid=").append(accountID)
                .append("&")
                .append("gameid=").append(GmServiceImpl.gameID);

        return requestUrl(url, sf.toString(), false);
    }

    public int getBalancePoint(int accountID) {
        int point = 0;
        String result = queryBalancePoint(accountID);
        String[] res = result.split("#");
        if (res[0].equals("0"))
            point = Integer.parseInt(res[1]);

        log.debug(accountID + " -- getBalancePoint = " + point);
        return point;
    }

    /**
     * 查询消费记录
     *
     * @param accountID
     * @param userID
     * @param _startDate
     * @param _endDate
     * @param queryType  查询类型 （1：按时间查询；2：查询近10次）
     * @return 成功返回：每行以\n隔开（序列#时间#道具id#点数）
     *         1#2011-01-19 17:31:26#22#100\n
     *         2#2011-01-19 17:31:26#22#100\n
     *         或者：无消费记录
     *         失败返回：错误提示信息
     */
    public String queryConsumeDetail(int accountID, int userID, int queryType, String _startDate, String _endDate) {
        String url = config.query_deduct_list_url;

        StringBuffer sf = new StringBuffer();
        sf.append("gameid=").append(GmServiceImpl.gameID)
                .append("&")
                .append("userid=").append(accountID)
                .append("&")
                .append("roleid=").append(userID)
                .append("&")
                .append("serverid=").append(GmServiceImpl.serverID)
                .append("&")
                .append("querytype=").append(queryType);
//        if(queryType == 1){
        sf.append("&")
                .append("stime=").append(_startDate)
                .append("&")
                .append("etime=").append(_endDate);
//        }
        String result = requestUrl(url, sf.toString(), true);
        if (result.indexOf("#") > 0) {
            String[] res = result.split("\\$\\$");
            StringBuffer sd = new StringBuffer();
            for (String str : res) {
                String[] ss = str.split("#");
                sd.append(ss[0]).append(".").append(ss[1]).append(".");
                MallGoods goods = MallGoodsDict.getInstance().getMallGoods(Integer.parseInt(ss[2]));
                sd.append(goods.name).append(".").append(ss[3]).append("点");
                sd.append("#HH");
            }
            return sd.toString();
        }
        return result.replaceAll("\\$\\$", "#HH");
    }

    /**
     * 查询充值记录
     *
     * @param accountID
     * @param userID
     * @param _startDate
     * @param _endDa
     * @param queryType  查询类型 （1：按时间查询；2：查询近10次）te
     * @return 成功返回：每行以\n隔开（序列#时间#点数）
     *         1#2011-01-20 13:46:50#100\n
     *         2#2011-01-20 13:47:07#100\n
     *         或者：无充值记录
     *         失败返回：错误提示信息
     */
    public String queryChargeUpDetail(int accountID, int userID, int queryType, String _startDate, String _endDate) {
        String url = config.query_rechage_list_url;
//    String url = "http://112.25.14.24/usercenter/queryaddlist.php";
        StringBuffer sf = new StringBuffer();
        sf.append("gameid=").append(GmServiceImpl.gameID)
                .append("&")
                .append("userid=").append(accountID)
                .append("&")
                .append("roleid=").append(userID)
                .append("&")
                .append("serverid=").append(GmServiceImpl.serverID)
//                .append("serverid=2")//.append(3)
                .append("&")
                .append("querytype=").append(queryType);
//        if(queryType == 1){
        sf.append("&")
                .append("stime=").append(_startDate)
                .append("&")
                .append("etime=").append(_endDate);
//        }
        log.debug("query charge up params: " + sf.toString());

        String result = requestUrl(url, sf.toString(), true);
        if (result.indexOf("#") > 0) {
            String[] res = result.split("\\$\\$");
            StringBuffer sd = new StringBuffer();
            for (String str : res) {
                String[] ss = str.split("#");
                sd.append(ss[0]).append(".").append(ss[1]).append(".").append(ss[2]).append("点");
                sd.append("#HH");
            }

            return sd.toString();
        }

        return result.replaceAll("\\$\\$", "#HH");
    }

    /**
     * 加点
     *
     * @param _player
     * @param transID
     * @param point
     * @param rechargetype  充值方式 默认0 ；1：网游接口 2：神州付3：赠送 4：即充即扣 5：当乐
     * @param channel_id
     * @param servicetype  int
     * @return 成功返回：0#成功增加xx点
     *         错误返回：1#错误提示信息
     */
    public boolean addPoint(HeroPlayer _player, String transID, int point, byte rechargetype, int channel_id, ServiceType servicetype) {
        boolean addres = false;
        String url = config.add_point_url;

        StringBuffer sf = new StringBuffer("");
        sf.append("gameid=").append(GmServiceImpl.gameID)
                .append("&")
                .append("serverid=").append(GmServiceImpl.serverID)
                .append("&")
                .append("mid=").append(_player.getLoginInfo().accountID) //int
                .append("&")
                .append("userid=").append(_player.getLoginInfo().accountID)
                .append("&")
                .append("roleid=").append(_player.getUserID())
                .append("&")
                .append("paytransid=").append(transID)
                .append("&")
                .append("msisdn=").append(_player.getLoginInfo().loginMsisdn)
                .append("&")
                .append("point=").append(point)
                .append("&")
                .append("pid=").append(chargepid)
                .append("&")
                .append("rechargetype=").append(rechargetype)
                .append("&")
                .append("channel_id=").append(channel_id)
                .append("&")
                .append("servicetype=").append(servicetype.getId());

        log.info(url + "?" + sf.toString());
        String result = requestUrl(url, sf.toString(), false);
        if (result.trim().length() > 0) {
            String[] res = result.split("#");
            if ((res[0].equals("0")) && (_player != null)) {
                addres = updatePointAmount(_player, point);
                LogServiceImpl.getInstance().pointLog(transID, _player.getLoginInfo().accountID, _player.getLoginInfo().username,
                        _player.getUserID(), _player.getName(), "增加", point, servicetype.getName(), _player.getLoginInfo().publisher, "");
            }
        }

        return addres;
    }

    /**
     * 扣点
     *
     * @param _player
     * @param point
     * @param toolid
     * @param toolName
     * @param number
     * @param servicetype int
     * @return 成功返回：0#成功扣除xx点#流水号（由用户中心生成流水格式：gameid+userid+time）
     *         失败返回：1#对应错误提示信息
     */
    public boolean reducePoint(HeroPlayer _player, int point, int toolid, String toolName, int number, ServiceType servicetype) {
        boolean redres = false;
        String url = config.sub_point_url;

        StringBuffer sf = new StringBuffer();
        sf.append("gameid=").append(GmServiceImpl.gameID)
                .append("&")
                .append("serverid=").append(GmServiceImpl.serverID)
                .append("&")
                .append("mid=").append(_player.getLoginInfo().accountID)     // int
                .append("&")
                .append("userid=").append(_player.getLoginInfo().accountID)
                .append("&")
                .append("roleid=").append(_player.getUserID())
                .append("&")
                .append("point=").append(point)
                .append("&")
                .append("toolid=").append(toolid)
                .append("&")
                .append("servicetype=").append(servicetype.getId())
                .append("&")
                .append("channel_id=").append(_player.getLoginInfo().publisher);

        log.info(sf.toString());
        String result = requestUrl(url, sf.toString(), false);
        if (result.trim().length() > 0) {
            String[] res = result.split("#");
            if (res[0].equals("0")) {
                redres = updatePointAmount(_player, -point);
                LogServiceImpl.getInstance().pointLog(res[2], _player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(),
                        _player.getName(), "扣点", point, "购买'" + toolName + "', 数量:" + number + " ", _player.getLoginInfo().publisher, toolName);

                LogServiceImpl.getInstance().chargeLog(_player.getName() + " 购买'" + toolName + "', 数量:" + number + " 扣点成功," + res[1]);
//        return true;
            } else {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(res[1], Warning.UI_TOOLTIP_TIP));
                LogServiceImpl.getInstance().chargeLog(_player.getName() + " 购买'" + toolName + "', 数量:" + number + " 扣点失败");
            }
        }

        return redres;
    }

    public boolean updatePointAmount(HeroPlayer _player, int _pointAmount){
        return updatePointAmount(_player,_pointAmount,null);
    }

    public boolean updatePointAmount(HeroPlayer _player, int _pointAmount, ServiceType serviceType) {
        if (_player.isEnable()) {
            log.debug("update point amoutn .... point=" + _pointAmount);
            if (_pointAmount > 0) {
                if (-1 != _player.getChargeInfo().addPointAmount(_pointAmount)) {
                    log.debug("add point amount ...");
                    String tip = Tip.TIP_CHARGE_POINT_GAIN;
                    if(serviceType != null && (serviceType == ServiceType.ACTIVE_PRESENT || serviceType == ServiceType.PRESENT)){
                        tip = Tip.TIP_PRESENT_POINT;
                    }
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(tip + _pointAmount, Warning.UI_STRING_TIP));
                } else
                    return false;
            }

            if (_pointAmount < 0) {
                if (-1 != _player.getChargeInfo().reducePointAmount(-_pointAmount)) {
                    log.debug("reducePointAmount point amount ...");
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(Tip.TIP_CHARGE_POINT_CONSUME + (-_pointAmount), Warning.UI_STRING_TIP));
                } else {
                    return false;
                }

            }

            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new PointAmountNotify(_player.getChargeInfo().pointAmount));
        }

        return true;
    }

    /**
     *
     * @param ngUrlID  接口ID
     * @param _accountID  账号 ID
     * @param _toolsID  道具ID
     * @param mobileUserID  移动userid
     * @param userID    角色ID
     * @param publisher  渠道号
     * @param serviceType
     * @param price  道具单价
     * @param sumPrice 总价
     * @return
     */
    public boolean[] ngBuyMallTools(String ngUrlID, int _accountID, String _toolsID,String mobileUserID,
                                  int userID,int publisher,ServiceType serviceType,int price,int sumPrice){
        log.info("网游计费 sumprice="+sumPrice+",price="+price);
        int count = sumPrice/price;
        log.info("网游计费请求次数：count="+count);
        boolean[] res = new boolean[count];
        for(int i=0; i<count; i++){
            res[i] = ngSingleBuyMallTools(ngUrlID,_accountID,_toolsID,mobileUserID,userID,publisher,serviceType,price,sumPrice);
        }
        return res;
    }

    /**
     * 网游计费
     * @param ngUrlID 计费接口ID
     * @param _tranID  流水号
     * @param _accountID  账号ID
     * @param _toolsID  道具ID(计费配置接口返回的)
     * @return
     */
    private boolean ngSingleBuyMallTools(String ngUrlID, int _accountID, String _toolsID,String mobileUserID,
                                  int userID,int publisher,ServiceType serviceType,int price,int sumPrice)
    {
        log.info("ngBuyMallTool ngurlid="+ngUrlID+",accountID="+_accountID+",toolsid="+_toolsID);

        StringBuffer sfd = new StringBuffer();
        sfd.append("mid=").append(_accountID)
                .append("&")
                .append("userid=").append(_accountID)
                .append("&")
                .append("roleid=").append(userID)
                .append("&")
                .append("gameid=").append(GmServiceImpl.gameID)
                .append("&")
                .append("serverid=").append(GmServiceImpl.serverID)
                .append("&")
                .append("mobile=").append(mobileUserID)
                .append("&")
                .append("servicetype=").append(serviceType.getId())
                .append("&")
                .append("channel_id=").append(publisher)
                .append("&")
                .append("pid=").append(chargepid)
                .append("&")
                .append("dopoint=").append(CK);

        String url = config.feeIdsMap.get(ngUrlID);
//        String url = "";
        StringBuffer sf = new StringBuffer();

        transid++;

        String  transGen = "";

        if(ngUrlID.equals(FeeIni.FEE_URL_ID_JIUTIAN)){
            transGen = "C00227" +format.format(new Date()) + numberFormat.format(transid);

//            url = "http://112.25.14.23:7000/JiuTian/bizcontrol/BuyGameTool_ota?paytransid="+transGen+"&"+sfd;
            url = url+"?paytransid="+transGen+"&"+sfd;
            log.debug("jiutian url="+url);

            sf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append("<request>")
                .append("<msgType>BuyGameToolReq</msgType>")
                .append("<sender>593</sender>")
                .append("<userId>").append(mobileUserID).append("</userId>")
                .append("<cpId>C00227</cpId>")
                .append("<cpServiceId>120122078000</cpServiceId>")
                .append("<consumeCode>").append(_toolsID).append("</consumeCode>")
                .append("<fid>1000</fid>")
                .append("<transIDO>").append(transGen).append("</transIDO>")
                .append("<versionId>2_0_0</versionId>")
                .append("</request>");
        }
        if(ngUrlID.equals(FeeIni.FEE_URL_ID_HERO)){
            transGen = "C00216" +format.format(new Date()) + numberFormat.format(transid);

//            url = "http://112.25.14.23:7000/hero/bizcontrol/BuyGameTool_ota?paytransid="+transGen+"&"+sfd;
            url = url+"?paytransid="+transGen+"&"+sfd;
            log.info("hero url="+url);
            sf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append("<request>")
                .append("<msgType>BuyGameToolReq</msgType>")
                .append("<sender>592</sender>")
                .append("<userId>").append(mobileUserID).append("</userId>")
                .append("<cpId>C00216</cpId>")
                .append("<cpServiceId>120122080000</cpServiceId>")
                .append("<consumeCode>").append(_toolsID).append("</consumeCode>")
                .append("<fid>1000</fid>")
                .append("<transIDO>").append(transGen).append("</transIDO>")
                .append("<versionId>2_0_0</versionId>")
                .append("</request>");

        }
        log.info("ng buy mall param="+sf);

        String result = requestUrl(url,sf.toString(),false);
        log.info("ng buy mall tools result="+result);

        String[] reses = result.split("#");

        boolean res = false;
        if(reses[0].equals("0")){
            res = true;
        }
        //每次请求都记录日志
        LogServiceImpl.getInstance().feeLog(ngUrlID,_accountID,_toolsID,mobileUserID,userID,publisher,serviceType,price,sumPrice,transGen,reses[1],res?"成功":"失败");

        return res;
    }

    public static void main(String[] args){
        String transID = GmServiceImpl.gameID + "-" + GmServiceImpl.serverID + "-" + format.format(new Date());

        String date= format.format(new Date());
        transid++;
        FeeIni feeIni = getInstance().getFeeIni(12000,19362,"1.9.9","10019",transID,"15936599392","",0,CK);
        log.info("feeini status="+feeIni.status);
        log.info("feeini feeid="+feeIni.feeUrlID+",type="+feeIni.feeType+",code="+feeIni.feeCode+",price="+feeIni.price);

//        boolean res = getInstance().ngBuyMallTools(feeIni.feeUrlID, 12000, feeIni.feeCode,"",19362,0,ServiceType.FEE);
//        log.info("res="+res);
    }
}