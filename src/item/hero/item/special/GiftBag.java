package hero.item.special;

import java.util.ArrayList;

import yoyo.core.queue.ResponseMessageQueue;


import hero.item.Goods;
import hero.item.SpecialGiftBagData;
import hero.item.SpecialGoods;
import hero.item.detail.EGoodsType;
import hero.item.dictionary.GoodsContents;
import hero.item.dictionary.SpecialGoodsDict;
import hero.item.message.ResponseSpecialGoodsBag;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.log.service.LogServiceImpl;
import hero.player.HeroPlayer;
import hero.share.message.Warning;
import hero.share.service.Tip;

public class GiftBag extends SpecialGoods {
	
	public GiftBag(int _id, short nums) {
		super(_id, nums);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean beUse(HeroPlayer _player, Object _target, int _location) {
		boolean remove = false;
		SpecialGiftBagData giftBag = SpecialGoodsDict.getInstance().getBagData(getID());
		EGoodsType goodsType = null;
		int equipment = 0, taskTool = 0, medicament = 0, material = 0, special = 0;
		for (int i = 0; i < giftBag.goodsSum; i++) {
			goodsType = GoodsContents.getGoodsType(giftBag.goodsList[i]);
			if(goodsType != null)
			{
				switch (goodsType) {
					case EQUIPMENT:
					{
						equipment += 1;
						break;
					}
					case MEDICAMENT:
					{
						medicament += 1;
						break;
					}
					case MATERIAL:
					{
						material += 1;
						break;
					}
					case TASK_TOOL:
					{
						taskTool += 1;
						break;
					}
					case SPECIAL_GOODS:
					{
						special += 1;
						break;
					}
					case PET:
					{
						//暂时不考虑这个
						break;
					}
					default:
					{
						//进入的类型异常
						break;
					}
				}
			}
		}
		boolean equipEmpty = true,medicamentEmpty = true,materialEmpty = true,taskEmpty = true,specialEmpty = true;
		int empty = _player.getInventory().getEquipmentBag().getEmptyGridNumber();
		if (empty < equipment) {
			equipEmpty = false;
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new Warning(
							Tip.TIP_ITEM_COMM_BAG_IS_FULL.replaceAll(
									"%fn", EGoodsType.EQUIPMENT.getDescription())));
		}
		empty = _player.getInventory().getMedicamentBag().getEmptyGridNumber();
		if (empty < medicament) {
			medicamentEmpty = false;
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new Warning(
							Tip.TIP_ITEM_COMM_BAG_IS_FULL.replaceAll(
									"%fn", EGoodsType.MEDICAMENT.getDescription())));
		}
		empty = _player.getInventory().getMaterialBag().getEmptyGridNumber();
		if (empty < material) {
			materialEmpty = false;
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new Warning(
							Tip.TIP_ITEM_COMM_BAG_IS_FULL.replaceAll(
									"%fn", EGoodsType.MATERIAL.getDescription())));
		}
		empty = _player.getInventory().getTaskToolBag().getEmptyGridNumber();
		if (empty < taskTool) {
			taskEmpty = false;
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new Warning(
							Tip.TIP_ITEM_COMM_BAG_IS_FULL.replaceAll(
									"%fn", EGoodsType.MATERIAL.getDescription())));
		}
		empty = _player.getInventory().getSpecialGoodsBag().getEmptyGridNumber();
		if (empty < special) {
			specialEmpty = false;
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new Warning(
							Tip.TIP_ITEM_COMM_BAG_IS_FULL.replaceAll(
									"%fn", EGoodsType.SPECIAL_GOODS.getDescription())));
		}
		if(equipEmpty && medicamentEmpty && materialEmpty && taskEmpty && specialEmpty)
		{
			Goods goods = null;
			for (int i = 0; i < giftBag.goodsSum; i++) {
				goods = GoodsContents.getGoods(giftBag.goodsList[i]);
				GoodsServiceImpl.getInstance().addGoods2Package(
						_player, goods, giftBag.numberList[i], CauseLog.OPENGIFTBAG);
			}
			//由于当前礼包肯定位于特殊物品背包,所以刷新特殊物品包即可
	        ResponseMessageQueue.getInstance().put(
	                _player.getMsgQueueIndex(),
	                new ResponseSpecialGoodsBag(
	                		_player.getInventory().getSpecialGoodsBag(), 
	                		_player.getShortcutKeyList()));
			remove = true;
	        LogServiceImpl.getInstance().goodsUsedLog(
	        		_player.getLoginInfo().accountID,
	        		_player.getLoginInfo().username,
	                _player.getUserID(),
	                _player.getName(), 
	                getID(),getName(), 
	                getTrait().getDesc(), 
	                getType().getDescription());
		}
		else 
		{
			remove = false;
		}
		return remove;
	}

	@Override
	public boolean disappearImmediatelyAfterUse() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public ESpecialGoodsType getType() {
		// TODO Auto-generated method stub
		return ESpecialGoodsType.GIFT_BAG;
	}

	@Override
	public void initDescription() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isIOGoods() {
		// TODO Auto-generated method stub
		return false;
	}

}
