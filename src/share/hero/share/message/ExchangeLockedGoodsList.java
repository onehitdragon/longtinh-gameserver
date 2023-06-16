package hero.share.message;

import hero.item.EquipmentInstance;
import hero.item.service.GoodsServiceImpl;
import hero.share.exchange.ExchangePlayer;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 锁定交易后给对方发送此时交易的物品列表
 * @author jiaodongjie
 * 0x3016
 */
public class ExchangeLockedGoodsList extends AbsResponseMessage
{
	private ExchangePlayer eplayer;
	
	public ExchangeLockedGoodsList(ExchangePlayer _eplayer){
		this.eplayer = _eplayer;
	}

	@Override
	public int getPriority ()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void write () throws IOException
	{
		yos.writeUTF(eplayer.nickname);
		yos.writeInt(eplayer.money);
		int size = eplayer.goodsID.length;
		yos.writeInt(size);
		for(int i=0; i<size; i++){
			yos.writeShort(eplayer.gridIndex[i]);
			yos.writeInt(eplayer.goodsID[i]);
			yos.writeShort(eplayer.goodsNum[i]);
			yos.writeByte(eplayer.goodsType[i]);
		}
	}

}
