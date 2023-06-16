package hero.pet.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.pet.Pet;
import hero.pet.message.ResponseTransactPet;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;

/**
 * 交易宠物
 * @author jiaodongjie
 * 改由交易物品报文处理交易宠物
 * TODO 此报文作废
 */
public class TransactPet extends AbsClientProcess
{

	@Override
	public void read () throws Exception
	{
		int buyerID = yis.readInt();
		int sellerID = yis.readInt();
		int petID = yis.readInt();
		
		HeroPlayer buyer = PlayerServiceImpl.getInstance().getPlayerByUserID(buyerID);
		HeroPlayer seller = PlayerServiceImpl.getInstance().getPlayerByUserID(sellerID);
		
		Pet pet = PetServiceImpl.getInstance().getPet(sellerID, petID);
		if(pet != null){
			if(pet.bind == 0){
    			int res = PetServiceImpl.getInstance().transactPet(buyerID, sellerID, petID);
    			ResponseMessageQueue.getInstance().put(
    					buyer.getMsgQueueIndex(),new ResponseTransactPet(buyerID,petID,res));
    			ResponseMessageQueue.getInstance().put(
    					seller.getMsgQueueIndex(),new ResponseTransactPet(sellerID,petID,res));
			}else{
				ResponseMessageQueue.getInstance().put(
						buyer.getMsgQueueIndex(),new Warning("交易失败！"));
				ResponseMessageQueue.getInstance().put(
						seller.getMsgQueueIndex(),new Warning("交易失败！已绑定的宠物不能交易！"));
			}
		}else{
			ResponseMessageQueue.getInstance().put(
					buyer.getMsgQueueIndex(),new Warning("交易失败！卖家没有此宠物"));
			ResponseMessageQueue.getInstance().put(
					seller.getMsgQueueIndex(),new Warning("交易失败！卖家没有此宠物"));
		}
	}

}
