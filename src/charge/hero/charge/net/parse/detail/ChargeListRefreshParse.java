package hero.charge.net.parse.detail;

import yoyo.core.queue.ResponseMessageQueue;
import hero.charge.message.SendChargeList;
import hero.charge.net.parse.XmlParamModel;
import hero.charge.service.ChargeServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import javolution.util.FastList;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ChargeListModel.java
 * @创建者 ChenYaMeng
 * @版本 1.0
 * @时间 2010-6-25 下午03:06:40
 * @描述 ： 充值方式列表，xml解析处理
 */

public class ChargeListRefreshParse extends XmlParamModel
{

    public ChargeListRefreshParse(String _param)
    {
        super(_param);
    }

    @Override
    public void process ()
    {
        // 向全体在线玩家发送充值方式列表
        /*FastList<HeroPlayer> list = PlayerServiceImpl.getInstance()
                .getPlayerList();
        for (HeroPlayer player : list)
        {
            if (player != null && player.isEnable())
            {
                OutMsgQ.getInstance().put(
                        player.getMsgQueueIndex(),
                        new SendChargeList(
                                ChargeServiceImpl.getInstance().mobileG,
                                ChargeServiceImpl.getInstance().cardG));
            }
        }*/
    }

    /**
     * 解析
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void parse (String _xmlParam)
    {
        // TODO 更新计费列表
//        ChargeServiceImpl.getInstance().updateRechargeTypeMobile(_xmlParam);
//        ChargeServiceImpl.getInstance().updateRechargeTypeCard(_xmlParam);
    }

}
