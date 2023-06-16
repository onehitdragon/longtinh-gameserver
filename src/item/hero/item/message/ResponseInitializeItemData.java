package hero.item.message;

import hero.item.service.GoodsServiceImpl;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


public class ResponseInitializeItemData extends AbsResponseMessage {

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void write() throws IOException {
		// TODO Auto-generated method stub
		//---------初始化强化装备各级所需金钱---------
		int[] perforateMoney = GoodsServiceImpl.getInstance().getConfig().perforate_money_list;
		yos.writeByte(perforateMoney.length);
		for (int i = 0; i < perforateMoney.length; i++) {
			yos.writeInt(perforateMoney[i]);
		}
		int[] enhanceMoney = GoodsServiceImpl.getInstance().getConfig().enhance_money_list;
		yos.writeByte(enhanceMoney.length);
		for (int i = 0; i < enhanceMoney.length; i++) {
			yos.writeInt(enhanceMoney[i]);
		}
		int[] wreckMoney = GoodsServiceImpl.getInstance().getConfig().wreck_money_list;
		yos.writeByte(wreckMoney.length);
		for (int i = 0; i < wreckMoney.length; i++) {
			yos.writeInt(wreckMoney[i]);
		}
		//---------初始化强化装备各级所需金钱---------
		//---------初始化强化各孔描述性文字---------
		yos.writeUTF(GoodsServiceImpl.getInstance().getConfig().describe_string);
		//---------初始化强化各孔描述性文字---------
	}

}
