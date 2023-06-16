package hero.charge.message;

import hero.charge.FeePointInfo;
import hero.charge.FeeType;
import hero.charge.RechargeTypeCard;
import hero.charge.RechargeTypeMobile;
import hero.charge.service.CostToPointConfig;
import hero.map.message.ResponseMapGameObjectList;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SendChargeList.java
 * @创建者 ChenYaMeng
 * @版本 1.0
 * @时间 2010-6-25 下午04:27:37
 * @描述 ：发送计费方式列表   0x3b08
 */
public class SendChargeList extends AbsResponseMessage
{
	private static Logger log = Logger.getLogger(SendChargeList.class);
    /**
     * 手机计费数据存储
     */
//    private RechargeTypeMobile mobile;

    /**
     * 充值卡计费数据存储
     */
//    private RechargeTypeCard   card;

    /*public SendChargeList(RechargeTypeMobile _mobile, RechargeTypeCard _card)
    {
        mobile = _mobile;
        card = _card;
    }*/
	
	private List<FeePointInfo> fpList;
    private List<FeeType> feeTypeList;
	
	public SendChargeList(List<FeePointInfo> _fpList,List<FeeType> _feeTypeList){
		this.fpList = _fpList;
        this.feeTypeList = _feeTypeList;
	}

    @Override
    public int getPriority ()
    {
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        yos.writeByte((byte)feeTypeList.size());
        for(FeeType feeType : feeTypeList){
            yos.writeByte(feeType.id);
            yos.writeUTF(feeType.name);
            yos.writeUTF(feeType.desc);
        }

    	yos.writeByte((byte)fpList.size());
    	for(FeePointInfo info : fpList){
            yos.writeByte(info.id);
    		yos.writeUTF(info.fpcode);
            yos.writeByte(info.typeID);
    		yos.writeUTF(info.name);
            yos.writeInt(info.price);
            yos.writeUTF(info.desc);
    	}
    	
        /*int i, j = 0;
        int lenCard = 0;
        int lenPrice = 0;

        if (null == mobile)
        {
            output.writeByte(false);
        }
        else
        {
            // 标识 手机计费
            output.writeByte(true);

            output.writeShort(mobile.id);
            output.writeUTF(mobile.name);
            lenPrice = mobile.prices.length;
            output.writeByte(lenPrice);

            for (i = 0; i < lenPrice; ++i)
            {
                output.writeUTF(mobile.prices[i].fpcode);
                output.writeShort(mobile.prices[i].price);
            }
        }

        if (null == card)
        {
            output.writeByte(false);
        }
        else
        {
            // 标识 充值卡计费
            output.writeByte(true);

            output.writeShort(card.id);
            output.writeUTF(card.name);
            lenCard = card.cards.length;
            output.writeByte(lenCard);

            for (i = 0; i < lenCard; ++i)
            {
                output.writeShort(card.cards[i].id);
                output.writeUTF(card.cards[i].name);
                output.writeUTF(CostToPointConfig.getInstance().getChargeCardPoint(card.cards[i].id, 0)[0]);
                
                lenPrice = card.cards[i].prices.length;
                output.writeByte(lenPrice);

                for (j = 0; j < lenPrice; ++j)
                {
                    output.writeUTF(card.cards[i].prices[j].fpcode);
                    output.writeShort(card.cards[i].prices[j].price);
                }

            }
        }*/
    	log.info("output size = " + String.valueOf(yos.size()));
    }
}
