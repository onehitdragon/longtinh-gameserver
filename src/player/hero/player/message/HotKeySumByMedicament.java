package hero.player.message;

import hero.item.Goods;
import hero.item.detail.EGoodsType;
import hero.item.service.GoodsServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 
 * @author zhengl
 * **待完善. 该报文下发 会在快捷键关联物品变更的时候下发所有有药品的快捷键数据.
 * 可以优化为只下发变更的那个按键的药品数量
 *
 */
public class HotKeySumByMedicament extends AbsResponseMessage {

	private HeroPlayer player;
	
	private byte page;
	
	private byte index;
	
	private int  goodsID;

	private short count;
	
	private byte group;
	
	private boolean isUpdate;
	
	public boolean haveRelation (int _goodsID) {
		boolean result = false;
		if(isUpdate)
		{
			result = true;
		}
		return result;
	}
	
	public HotKeySumByMedicament(HeroPlayer _player, int _changeGoodsID) {
		player = _player;
		page = 1;
		isUpdate = false;
		int[][] shortKey = player.getShortcutKeyList();
        for (int j = 0; j < shortKey.length; j++)
        {
            if (shortKey[j][0] > 0)
            {
            	if(shortKey[j][0] == PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_GOODS) {
                    Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(shortKey[j][1]);
		            if (goods.getGoodsType() == EGoodsType.MEDICAMENT
		            		|| goods.getGoodsType() == EGoodsType.SPECIAL_GOODS)
		            {
		            	if(_changeGoodsID == shortKey[j][1]) {
		            		isUpdate = true;
		            	}
		            	group += 1;
		            }
            	}
            }
        }
	}
	
	public HotKeySumByMedicament(HeroPlayer _player) {
		player = _player;
		page = 1;
		int[][] shortKey = player.getShortcutKeyList();
        for (int j = 0; j < shortKey.length; j++)
        {
            if (shortKey[j][0] > 0)
            {
            	if(shortKey[j][0] == PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_GOODS) {
                    Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(shortKey[j][1]);
		            if (goods.getGoodsType() == EGoodsType.MEDICAMENT 
		            		|| goods.getGoodsType() == EGoodsType.SPECIAL_GOODS)
		            {
		            	group += 1;
		            }
            	}
            }
        }
	}
	
	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void write() throws IOException {
		int[][] shortKey = player.getShortcutKeyList();
        yos.writeByte(group); //有几组设定了药品的快捷键.
        if(group > 0) 
        {
        	for (int j = 0; j < shortKey.length; j++)
        	{
        		if (shortKey[j][0] > 0)
        		{
        			if(shortKey[j][0] == PlayerServiceImpl.SHUTCUT_KEY_TYPE_OF_GOODS) {
        				Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(shortKey[j][1]);
        				if (goods.getGoodsType() == EGoodsType.MEDICAMENT)
        				{
        					//是消耗品所以进行数量下发
        					count = (short)player.getInventory().getMedicamentBag().getGoodsNumber(
        							shortKey[j][1]);
        					index = (byte)j;
        					goodsID = shortKey[j][1];
        					//下发数据组
        					yos.writeByte(page);
        					yos.writeByte(index);
        					yos.writeInt(goodsID);
        					yos.writeShort(count);
        				}
        				else if (goods.getGoodsType() == EGoodsType.SPECIAL_GOODS) 
        				{
        					//是消耗品所以进行数量下发
        					count = (short)player.getInventory().getSpecialGoodsBag().getGoodsNumber(
        							shortKey[j][1]);
        					index = (byte)j;
        					goodsID = shortKey[j][1];
        					//下发数据组
        					yos.writeByte(page);
        					yos.writeByte(index);
        					yos.writeInt(goodsID);
        					yos.writeShort(count);
						}
        			}
        		}
        	}//for end
        }
	}

}
