package hero.item.message;

import hero.item.bag.PetEquipmentBag;
import hero.item.service.GoodsServiceImpl;
import hero.ui.data.EquipmentPackageData;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 宠物背包装备列表
 * @author jiaodongjie
 *
 */
public class ResponsePetEquipmentBag extends AbsResponseMessage
{
	private PetEquipmentBag eqbag;
	
	public ResponsePetEquipmentBag(PetEquipmentBag eqbag){
		this.eqbag = eqbag;
	}
	@Override
	public int getPriority ()
	{
		return 0;
	}

	@Override
	protected void write () throws IOException
	{
		 yos.writeBytes(EquipmentPackageData.getData(eqbag, 
				 GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name));
		
	}

}
