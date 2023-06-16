package hero.item.dictionary;

import org.apache.log4j.Logger;

import hero.item.detail.EGoodsType;
import hero.item.dictionary.SpecialGoodsDict;
import hero.item.Goods;
import hero.item.service.GoodsIDLimitDefine;
import hero.pet.service.PetDictionary;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GoodsContents.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-25 下午04:30:42
 * @描述 ：物品目录，功能类，包含着所有物品的字典，提供查找物品功能
 */

public class GoodsContents
{
	private static Logger log = Logger.getLogger(GoodsContents.class);
    /**
     * 构造
     */
    private GoodsContents()
    {
    }

    /**
     * 获取物品类型
     * 
     * @return
     */
    public static EGoodsType getGoodsType (int _goodsID)
    {
        if (GoodsIDLimitDefine.MATERIAL_ID_LIMIT[0] <= _goodsID
                && GoodsIDLimitDefine.MATERIAL_ID_LIMIT[1] >= _goodsID)
        {
            return EGoodsType.MATERIAL;
        }
        else if (GoodsIDLimitDefine.MEDICAMENT_ID_LIMIT[0] <= _goodsID
                && GoodsIDLimitDefine.MEDICAMENT_ID_LIMIT[1] >= _goodsID)
        {
            return EGoodsType.MEDICAMENT;
        }
        else if (GoodsIDLimitDefine.TASK_GOODS_ID_LIMIT[0] <= _goodsID
                && GoodsIDLimitDefine.TASK_GOODS_ID_LIMIT[1] >= _goodsID)
        {
            return EGoodsType.TASK_TOOL;
        }
        else if (GoodsIDLimitDefine.WEAPON_ID_LIMIT[0] <= _goodsID
                && GoodsIDLimitDefine.WEAPON_ID_LIMIT[1] >= _goodsID)
        {
            return EGoodsType.EQUIPMENT;
        }
        else if (GoodsIDLimitDefine.ARMOR_ID_LIMIT[0] <= _goodsID
                && GoodsIDLimitDefine.ARMOR_ID_LIMIT[1] >= _goodsID)
        {
            return EGoodsType.EQUIPMENT;
        }
        else if (GoodsIDLimitDefine.SPECIAL_GOODS_LIMIT[0] <= _goodsID
                && GoodsIDLimitDefine.SPECIAL_GOODS_LIMIT[1] >= _goodsID)
        {
            return EGoodsType.SPECIAL_GOODS;
        }else if (GoodsIDLimitDefine.PET_EQUIP_ARMOT_LIMIT[0] <= _goodsID
                && GoodsIDLimitDefine.PET_EQUIP_ARMOT_LIMIT[1] >= _goodsID){
        	return EGoodsType.PET_EQUIQ_GOODS;
        }else if (GoodsIDLimitDefine.PET_EQUIP_WEAPON_LIMIT[0] <= _goodsID
                && GoodsIDLimitDefine.PET_EQUIP_WEAPON_LIMIT[1] >= _goodsID){
        	return EGoodsType.PET_EQUIQ_GOODS;
        }
        else if(GoodsIDLimitDefine.PET_SKILL_BOOK[0]<= _goodsID
        		&& GoodsIDLimitDefine.PET_SKILL_BOOK[1] >= _goodsID){
        	return EGoodsType.PET_GOODS;
        }
        else if (GoodsIDLimitDefine.PET_FEED_GOODS_LIMIT[0] <= _goodsID
                && GoodsIDLimitDefine.PET_FEED_GOODS_LIMIT[1] >= _goodsID){
        	return EGoodsType.PET_GOODS;
        }else if (GoodsIDLimitDefine.PET_REVIVE_LIMIT[0] <= _goodsID
                && GoodsIDLimitDefine.PET_REVIVE_LIMIT[1] >= _goodsID){
        	return EGoodsType.PET_GOODS;
        }else if (GoodsIDLimitDefine.PET_DICARD_LIMIT[0] <= _goodsID
                && GoodsIDLimitDefine.PET_DICARD_LIMIT[1] >= _goodsID){
        	return EGoodsType.PET_GOODS;
        }else if(GoodsIDLimitDefine.PET_LIMIT[0] <= _goodsID
        		&& GoodsIDLimitDefine.PET_LIMIT[1] >= _goodsID){
        	return EGoodsType.PET;
        }

        return null;
    }

    /**
     * 获取物品原型
     * 
     * @param _goodsID
     * @return
     */
    public static Goods getGoods (int _goodsID)
    {
        if (GoodsIDLimitDefine.MATERIAL_ID_LIMIT[0] <= _goodsID
                && GoodsIDLimitDefine.MATERIAL_ID_LIMIT[1] >= _goodsID)
        {
            return MaterialDict.getInstance().getMaterial(_goodsID);
        }
        else if (GoodsIDLimitDefine.MEDICAMENT_ID_LIMIT[0] <= _goodsID
                && GoodsIDLimitDefine.MEDICAMENT_ID_LIMIT[1] >= _goodsID)
        {
            return MedicamentDict.getInstance().getMedicament(_goodsID);
        }
        else if (GoodsIDLimitDefine.TASK_GOODS_ID_LIMIT[0] <= _goodsID
                && GoodsIDLimitDefine.TASK_GOODS_ID_LIMIT[1] >= _goodsID)
        {
            return TaskGoodsDict.getInstance().getTaskTool(_goodsID);
        }
        else if (GoodsIDLimitDefine.WEAPON_ID_LIMIT[0] <= _goodsID
                && GoodsIDLimitDefine.WEAPON_ID_LIMIT[1] >= _goodsID)
        {
            return WeaponDict.getInstance().getWeapon(_goodsID);
        }
        else if (GoodsIDLimitDefine.ARMOR_ID_LIMIT[0] <= _goodsID
                && GoodsIDLimitDefine.ARMOR_ID_LIMIT[1] >= _goodsID)
        {
            return ArmorDict.getInstance().getArmor(_goodsID);
        }
        else if (GoodsIDLimitDefine.SPECIAL_GOODS_LIMIT[0] <= _goodsID
                && GoodsIDLimitDefine.SPECIAL_GOODS_LIMIT[1] >= _goodsID)
        {
            return SpecialGoodsDict.getInstance().getSpecailGoods(_goodsID);
        }else if(GoodsIDLimitDefine.PET_SKILL_BOOK[0]<= _goodsID
        		&& GoodsIDLimitDefine.PET_SKILL_BOOK[1] >= _goodsID){
        	return SpecialGoodsDict.getInstance().getSpecailGoods(_goodsID);
        }
        else if (GoodsIDLimitDefine.PET_FEED_GOODS_LIMIT[0] <= _goodsID
                && GoodsIDLimitDefine.PET_FEED_GOODS_LIMIT[1] >= _goodsID){
        	return SpecialGoodsDict.getInstance().getSpecailGoods(_goodsID);
        }else if (GoodsIDLimitDefine.PET_REVIVE_LIMIT[0] <= _goodsID
                && GoodsIDLimitDefine.PET_REVIVE_LIMIT[1] >= _goodsID){
        	return SpecialGoodsDict.getInstance().getSpecailGoods(_goodsID);
        }else if (GoodsIDLimitDefine.PET_DICARD_LIMIT[0] <= _goodsID
                && GoodsIDLimitDefine.PET_DICARD_LIMIT[1] >= _goodsID){
        	return SpecialGoodsDict.getInstance().getSpecailGoods(_goodsID);
        }else if(GoodsIDLimitDefine.PET_EQUIP_ARMOT_LIMIT[0] <= _goodsID
                && GoodsIDLimitDefine.PET_EQUIP_ARMOT_LIMIT[1] >= _goodsID){
        	return PetEquipmentDict.getInstance().getPetArmor(_goodsID);
        }else if(GoodsIDLimitDefine.PET_EQUIP_WEAPON_LIMIT[0] <= _goodsID
                && GoodsIDLimitDefine.PET_EQUIP_WEAPON_LIMIT[1] >= _goodsID){
        	return PetEquipmentDict.getInstance().getPetWeapon(_goodsID);
        }


        return null;
    }

    /**
     * TODO 正式版获取商城销售物品
     * 区间不一样
     *     区间名                          普通 区间        商城销售区间
     * 1.材料编号区间 :                   320000-329999 ,420000-429999
        2.药水编号区间:                    310000-319999, 410000-419999
        3.任务道具编号区间:                330000-339999, 330000-339999
        4.武器编号区间:                    110800-111299 ,210800-210899
        5.防具编号区间:                    120000-149999 ,220000-249999
        6.特殊物品编号区间:                340000-349999 ,440000-449999
        7.宠物技能书区间:                  351000-354999,451000-454999
        8.宠物饲料编号区间:                355000-355999,455000-455999
        9.宠物复活卷轴区间:                356000-356999,346000-456999
        10.宠物洗点道具区间                357000-357999,457000-457999
        11.宠物装备---防具区间             151400-151699,251400-251699
        12.宠物装备---武器区间             151300-151399,251300-251399
        13.商城销售宠物区间                9100-9199,  9200-9299
     * @param _goodsID
     * @return
     */
//    public static EGoodsType getMallGoodsType(int _goodsID){
//
//    }
}
