package hero.charge.service;

import hero.log.service.LogServiceImpl;
import hero.share.service.LogWriter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Properties;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import yoyo.service.YOYOSystem;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GamePointService.java
 * @创建者 ChenYaMeng
 * @版本 1.0
 * @时间 2010-6-30 下午01:33:02
 * @描述 ： 访问计费服务器进行查询、加点、扣点、清零操作
 */

public class ChargePointService
{
    private static ChargePointService instance           = null;

    /**
     * 游戏ID
     */
    public String                     GAME_ID            = "";

    /**
     * 网元号
     */
    public String                     SENDER             = "";

    /**
     * 服务URL
     */
    public String                     SERVICE_URL        = "";

    /**
     * 查询点数 action=query&gameid=&account=&Sender=
     */
    private static String             ACTION_SUFIX_QUERY = "action=query&gameid={0}&account={1}&Sender={2}";

    /**
     * 添加、扣除点数,当点数为负数时，扣除点数 action=add&gameid=&account=&point=&Sender=
     */
    private static String             ACTION_SUFIX_ADD   = "action=add&gameid={0}&account={1}&point={2}&Sender={3}&trid={4}";

    /**
     * 账号清零 action=setzero&gameid=&account=&Sender=
     */
    private static String             ACTION_SUFIX_ZERO  = "action=setzero&gameid={0}&account={1}&Sender={2}";

    private String                    CONFIG_FILE_PATH   = YOYOSystem.HOME
                                                                 + "res/config/charge/gamepoint.config";

    private Properties                properties         = new Properties();

    private ChargePointService()
    {
        try
        {
            properties.load(new FileReader(CONFIG_FILE_PATH));
            GAME_ID = properties.getProperty("gameid");
            SENDER = properties.getProperty("Sender");
            SERVICE_URL = properties.getProperty("GamePointURL");
        }
        catch (FileNotFoundException e)
        {
            // ME2Logger.error(this, e);
        }
        catch (IOException e)
        {
            // ME2Logger.error(this, e);
        }
    }

    public static ChargePointService getInstance ()
    {
        if (instance == null)
        {
            instance = new ChargePointService();
        }
        return instance;
    }

    /**
     * 发起请求，得到响应结果
     * 
     * @param _actionSufix
     * @return <code>
     * 
     * <?xml version="1.0" encoding="UTF-8"?>
     * <Response>
     * <ResultCode>0</ResultCode>
     * <Mid>13911098112</Mid>
     * <Point>1000</Point>
     * <State>0</State>
     * <TransID>1232421535352</TransID>
     * </Response>
     * 
     * </code><br>
     *         ResultCode: 0 操作成功 1 缺少参数 2 参数格式不对 3 调用IP非法 4 调用次数过多 5 接口内部错误 6
     *         数据被锁定 7 点数不足本次操作 8 无对应记录 9 远端返回操作失败
     * @throws IOException
     */
    private Response request (String _actionSufix) throws IOException
    {
        URL Url = new URL(SERVICE_URL + _actionSufix);

        // ME2Logger.info("GamePointService,request: " + Url.toString());

        HttpURLConnection connection = (HttpURLConnection) Url.openConnection();
        connection.setConnectTimeout(3000);
        connection.setDoInput(true);
        connection.connect();

        InputStream in = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        StringBuffer resp = new StringBuffer();
        String temp = reader.readLine();
        for (; temp != null;)
        {
            resp.append(temp);
            temp = reader.readLine();
        }
        reader.close();
        in.close();
        connection.disconnect();

        return parse(resp.toString()); // 解析
    }

    /**
     * 解析收到的xml数据 <Response><ResultCode>0</ResultCode><Gameid>3</Gameid><Account>abc</Account><Requests>2000</Requests><Results>6000</Results><State>0</State><TransID>65</TransID></Response>
     * 
     * @param _xmlResp
     * @return
     */
    private Response parse (String _xmlResp)
    {
        SAXReader reader = new SAXReader();
        try
        {
            Document document = reader.read(new ByteArrayInputStream(_xmlResp
                    .getBytes("UTF-8")));
            Element root = document.getRootElement();
            Element eleResultCode = root.element("ResultCode");
            String resultCode = eleResultCode.getText();
            if (resultCode.equals("0")) // 成功，读取各参数
            {
                // Mid
                Element eAccount = root.element("Account");
                String account = eAccount.getTextTrim();
                // Point
                Element elePoint = root.element("Results");
                String point = elePoint.getTextTrim();
                // State
                Element eleState = root.element("State");
                String state = eleState.getTextTrim();
                // TransID
                Element eleTransID = root.element("TransID");
                String transID = eleTransID.getTextTrim();

                Response resp = new Response(resultCode, account);
                resp.rawRespXML = _xmlResp;

                resp.setPoint(Integer.parseInt(point));
                resp.setState(state);
                resp.setTransID(transID);
                return resp;
            }
            else
            // 失败，只记录一个resultCode
            {
                Response resp = new Response(resultCode, null);
                return resp;
            }
        }
        catch (DocumentException e)
        {
            // ME2Logger.println("DocumentException ChargePointService.parse()::
            // 解析XML出错: "+ _xmlResp);
            // ME2Logger.error(this, e);
        }
        catch (UnsupportedEncodingException e)
        {
            // ME2Logger.println("UnsupportedEncodingException
            // ChargePointService.parse():: 解析XML出错: "
            // + _xmlResp);
            // ME2Logger.error(this, e);
        }
        return null;
    }

    /**
     * 查询该手机号对应的游戏点数
     * 
     * @param _account 手机号
     * @return -1表示查询异常
     */
    public Response query (String _account)
    {
        String actionSufix = MessageFormat.format(ACTION_SUFIX_QUERY, GAME_ID,
                _account, SENDER);
        try
        {
            Response resp = request(actionSufix);
            return resp;
        }
        catch (Exception e)
        {
            LogWriter.error("ChargePointService.query exception..._account:"
                    + _account, e);
        }
        return null;
    }

    /**
     * 修改玩家的点数
     * 
     * @param _tranID 流水号
     * @param _account
     * @param _point 负数时，表示扣除；正数时，表示添加
     * @see {@linkplain http://api.uen.cn/doc/Result.dic}
     * @return 0 操作成功; 1 缺少参数 ;2 参数格式不对; 3 调用IP非法 ;4 调用次数过多 ;5 接口内部错误 ; <br>
     *         6 数据被锁定; 7 点数不足本次操作 ;8 无对应记录 ;9 远端返回操作失败;NULL 出现异常
     */
    public Response modify (String _tranID, String _account, int _point)
    {
        String sPoint = String.valueOf(_point);// 防止4位数的数字，如1000,被格式化成1,000

        String actionSufix = MessageFormat.format(ACTION_SUFIX_ADD, GAME_ID,
                _account, sPoint, SENDER, _tranID);

        try
        {
            Response resp = request(actionSufix);
            return resp;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LogWriter.error("ChargePointService.modify exception..._account:"
                    + _account + " _point:" + sPoint, e);
        }
        return null;
    }

    /**
     * 将账号点数清零
     * 
     * @param _account
     * @return
     */
    public boolean reset (String _account)
    {
        String actionSufix = MessageFormat.format(ACTION_SUFIX_ZERO, GAME_ID,
                _account, SENDER);
        try
        {
            Response resp = request(actionSufix);
            if (resp.getResultCode().equals("0"))
            {
                return true;
            }
            else
            {
                LogServiceImpl.getInstance().chargeLog(
                        "ChargePointService.reset error result code..._account:"
                                + _account);
                LogServiceImpl.getInstance().chargeLog(
                        ">>>>> " + resp.getResultCode());
                return false;
            }
        }
        catch (Exception e)
        {
            LogWriter.error("ChargePointService.reset exception..._msisdn:"
                    + _account, e);
        }
        return false;
    }

    /**
     * 响应
     * 
     * @author ChenYaMeng
     */
    public static class Response
    {
        private String resultCode;

        private String account;

        private int    point;

        private String state;

        private String transID = null;

        private String rawRespXML, feeResult;

        public Response(String _resultCode, String _account)
        {
            resultCode = _resultCode;
            account = _account;
        }

        /**
         * 参见 {@linkplain http://api.uen.cn/doc/Result.dic}
         * 
         * @return 操作结果 :<br>
         *         0 操作成功; 1 缺少参数 ;2 参数格式不对; 3 调用IP非法 ;4 调用次数过多 ;5 接口内部错误 ; <br>
         *         6 数据被锁定; 7 点数不足本次操作 ;8 无对应记录 ;9 远端返回操作失败
         */
        public String getResultCode ()
        {
            return resultCode;
        }

        public String getMSISDN ()
        {
            return account;
        }

        void setPoint (int _point)
        {
            point = _point;
        }

        /**
         * 当前点数
         * 
         * @return
         */
        public int getPoint ()
        {
            return point;
        }

        void setState (String _state)
        {
            state = _state;
        }

        public String getState ()
        {
            return state;
        }

        public void setTransID (String _transID)
        {
            transID = _transID;
        }

        public String getTransID ()
        {
            return transID;
        }

        public String getRawRespXML ()
        {
            return rawRespXML;
        }

        public void setRawRespXML (String _rawRespXML)
        {
            rawRespXML = _rawRespXML;
        }

        public void setFeeResult (String _feeResult)
        {
            feeResult = _feeResult;
        }

        public String getFeeResult ()
        {
            return feeResult;
        }
    }

    public static void main (String[] args)
    {
        // 修改
        // Response response = ChargePointService.getInstance()
        // .modify("abc", -7000);

        // if (response == null)
        // {
        // // 返回为空，记录到日志里面
        // System.out.println("返回为空");
        // }
        // else
        // {
        // String resultCode = response.getResultCode();
        // if (resultCode.equals("0"))
        // {
        // // 点数修改成功
        // System.out.println("成功");
        // }
        // else
        // {
        // System.out.println(">>>>> " + "resultCode:" + resultCode);
        // }
        // }

         //查询
         String username = "00000000001";
         Response response = ChargePointService.getInstance().query(
         username);
         if (response == null)
         {
         System.out.println("返回为空");
         }
         else
         {
         String resultCode = response.getResultCode();
         if (resultCode.equals("0"))
         {
         System.out.println("成功");
         System.out.println(response.account);
         System.out.println(response.point);
         }
         else
         {
         System.out.println(">>>>> " + "resultCode:" + resultCode);
         }
         }

//        // 清零
//        String username = "11111114";
//        if (ChargePointService.getInstance().reset("00000000001"))
//        {
//            System.out.println("清零成功：" + username);
//        }
//        else
//        {
//            System.out.println("清零失败：" + username);
//        }
    }
}
