package hero.item.clienthandler;

import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;

import hero.item.EquipmentInstance;
import hero.item.enhance.EnhanceService;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EnhanceEquipment.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-10-21 上午11:24:21
 * @描述 ：装备强化请求处理
 */

public class EnhanceEquipment extends AbsClientProcess {
	private static Logger log = Logger.getLogger(EnhanceEquipment.class);
	
	private static final byte OPERATE_PERFORATE = 0;
	
	private static final byte OPERATE_ENHANCE = 1;
	
	private static final byte OPERATE_WRECK = 2;
	
	private static final byte OPERATE_SEARCH = 3;
	
	@Override
	public void read() throws Exception {
		// TODO Auto-generated method stub
//		byte locationOfEquipment = input.readByte(); //不再需要部位
		int equipmentIndex = yis.readByte();
//		int crystalID = input.readInt(); //只有强化时需要
//		byte crystalLocationOfBag = input.readByte(); //不在需要宝石所在背包位置.
		// 强化类型. 0=打孔;1=镶嵌;2=拨除;3=查询镶嵌是否可执行; 
		byte enhanceType = yis.readByte();
		HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(
				contextData.sessionID);

//		EquipmentInstance ei = player.getBodyWear().get(locationOfEquipment);
		EquipmentInstance ei = player.getInventory().getEquipmentBag().get(equipmentIndex);

		if(ei != null) {
			switch (enhanceType) {
				case OPERATE_PERFORATE:
				{
					EnhanceService.getInstance().perforateEquipment(player, ei);
					break;
				}
				case OPERATE_ENHANCE:
				{
					int crystalID = yis.readInt();
					byte jewelIndex = yis.readByte();
					EnhanceService.getInstance().enhanceEquipment(player, crystalID, jewelIndex, ei);
					break;
				}
				case OPERATE_WRECK:
				{
					log.debug("enhanceType == 2 剥离宝石..");
					// 如果是宝石拔除就需要多传递一个字段.crystalLocationOfBag=0,这里不用宝石在背包里的位置
					byte jewelIndex = yis.readByte();
					log.debug("jewelIndex = " + jewelIndex);
					EnhanceService.getInstance().jewelWreck(player, ei, jewelIndex);
					break;
				}
				case OPERATE_SEARCH:
				{
					int crystalID = yis.readInt();
					byte jewelIndex = yis.readByte();
					EnhanceService.getInstance().enhanceQuest(player, crystalID, jewelIndex, ei);
					break;
				}
			}//end switch
		}
	}
}
