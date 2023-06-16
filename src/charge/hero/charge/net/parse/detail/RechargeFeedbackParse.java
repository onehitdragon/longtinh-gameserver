package hero.charge.net.parse.detail;

import hero.charge.net.parse.XmlParamModel;
import hero.charge.service.ChargePointService;
import hero.charge.service.ChargeServiceImpl;
import hero.charge.service.CostToPointConfig;
import hero.charge.service.ChargePointService.Response;
import hero.log.service.LogServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.ResultFeeTip;
import hero.share.service.LogWriter;

import java.util.HashMap;
import java.util.Map.Entry;


import org.apache.commons.lang.StringUtils;

import yoyo.core.queue.ResponseMessageQueue;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 RechargeFeedback.java
 * @创建者 ChenYaMeng
 * @版本 1.0
 * @时间 2010-6-25 下午04:22:40
 * @描述 ：充值结果，xml解析处理
 */

public class RechargeFeedbackParse extends XmlParamModel
{
    /**
     * 用户
     */
    private HeroPlayer player;

    /**
     * 返回字符串
     */
    private String     resultStr;

    public RechargeFeedbackParse(HashMap<String, String> _param)
    {
        super(_param);
    }

    @Override
    public void process ()
    {
        if (player != null && player.isEnable())
        {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                    new ResultFeeTip(resultStr));
        }
    }

    /**
     * 充值结果处理
     * /shenyu?trid=000000017&result=0&price=5000&mid=15195884671&uid=0&triggerID=0&param=1&type=1
     */
    @Override
    protected void parse (HashMap<String, String> _param)
    {
        try
        {
            /*String rtn = "充卡失败,请与GM联系";
            String transID = _param.get("trid");

            if (transID != null)
            {
                transID = ChargePointService.getInstance().SENDER + transID;
                rtn += ",流水号:" + transID;
            }

            int price = Integer.parseInt(_param.get("price")) / 100;

            String result = _param.get("result");

            String mid = _param.get("mid");

            // 后加的1个参数 userid
            String paramStr = _param.get("param");

            int cardID = 0; // 充值卡类型ID
            String account = ""; // 账户
            int userID = 0; // 角色uid

            if (!StringUtils.isBlank(paramStr))
            {
                String[] p = paramStr.split("\\|");
                int len = p.length;

                if (len == 3)
                {
                    cardID = Integer.parseInt(p[0]);
                    account = p[1];
                    userID = Integer.parseInt(p[2]);
                }
                else
                {
                    // 参数不等于3
                    LogServiceImpl.getInstance().chargeLog(
                            "Error >>> RechargerFeedBack param number is not 3, but "
                                    + p.length);
                    LogServiceImpl.getInstance().chargeLog(paramStr);
                    LogServiceImpl.getInstance().chargeLog(" ");
                    for (Entry<String, String> entry : _param.entrySet())
                    {
                        LogServiceImpl.getInstance().chargeLog(
                                entry.getKey() + ":" + entry.getValue());
                    }
                }

            }
            else
            {
                // 参数值为空记录
                LogServiceImpl.getInstance().chargeLog(
                        "Error >>> RechargerFeedBack param is blank.");
            }

            if (result.equals("0"))
            {

                rtn = "冲卡成功";

                // 根据充值信息及可兑换点数
                String[] pointInfos = CostToPointConfig.getInstance()
                        .getChargeCardPoint(cardID, price);

                int point = Integer.parseInt(pointInfos[1]);

                // 向计费服务器发起<充值游戏点数>请求
                Response response = ChargePointService.getInstance().modify(
                        transID, account, point);

                player = PlayerServiceImpl.getInstance().getPlayerByUserID(
                        userID);

                if (player != null)
                {
                    if (response == null)
                    {
                        // 返回为空，记录到日志里面
                        rtn += ",返点失败，请与GM联系，流水号:" + transID;

                        // 点数改变日志记录
                        LogServiceImpl.getInstance().pointLog(transID,
                                player.getLoginInfo().accountID, account,
                                userID, player.getName(), "error", point,
                                "充值卡充值失败，流水号：" + transID + ",原因：HTTP响应为空",
                                player.getLoginInfo().publisher,
                                "");
                    }
                    else
                    {
                        String resultCode = response.getResultCode();
                        if (resultCode.equals("0"))
                        {
                            // 点数修改成功
                            rtn = "您充值" + price + "元成功，返点" + point + "点";

                            if (player.isEnable())
                            {
                                ChargeServiceImpl.getInstance()
                                        .updatePointAmount(player, point);
                            }

                            // 点数改变日志记录
                            LogServiceImpl.getInstance().pointLog(transID,
                                    player.getLoginInfo().accountID, account,
                                    userID, player.getName(), "add", point,
                                    "充值卡充值成功",player.getLoginInfo().publisher,"");
                        }
                        else
                        {
                            rtn += "，返点失败，请与GM联系，流水号:" + transID;

                            // 点数改变日志记录
                            LogServiceImpl.getInstance().pointLog(transID,
                                    player.getLoginInfo().accountID,
                                    account,
                                    userID,
                                    player.getName(),
                                    "error",
                                    point,
                                    "充值卡充值失败，流水号：" + transID
                                            + ",原因：返回的resultCode不为0，为"
                                            + resultCode,
                                   player.getLoginInfo().publisher,"");
                        }
                    }
                }
                else
                {
                    // 点数改变日志记录
                    LogServiceImpl.getInstance().pointLog(transID,
                            player.getLoginInfo().accountID, account, userID,
                            player.getName(), "error", point,
                            "充值卡充值失败，流水号：" + transID + ",原因：未知用户",
                            player.getLoginInfo().publisher,"");
                }
            }

            // 充值结果回调日志
//            LogServiceImpl.getInstance().chargeCardCallBackLog(result, account,
//                    userID, mid, transID, cardID, price,player.getLoginInfo().accountID,
//                    1,0,player.getLoginInfo().publisher,1,1,"");

            resultStr = rtn;*/

        }
        catch (Exception e)
        {
            e.printStackTrace();
            LogWriter.error("RechargeFeedbackParse.parse error.", e);
        }
    }
}
