package hero.item.special;

import org.apache.log4j.Logger;

import hero.item.SpecialGoods;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SpecialGoodsBuilder.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-8 下午05:09:44
 * @描述 ：
 */

public class SpecialGoodsBuilder
{
	private static Logger log = Logger.getLogger(SpecialGoodsBuilder.class);
    /**
     * 私有构造
     */
    private SpecialGoodsBuilder()
    {

    }

    /**
     * 制造特殊物品模板
     * 
     * @param _goodsID
     * @return
     */
    public static SpecialGoods build (int _goodsID, short _stackNumber,
            String _typeDesc)
    {
        ESpecialGoodsType goodsType = ESpecialGoodsType.get(_typeDesc);

        if (null != goodsType)
        {
//        	log.debug("special goods build goods id= "+_goodsID+"  goodstype = " + goodsType);
            SpecialGoods specialGoods = null;

            switch (goodsType)
            {
                case GOURD:
                {
                    specialGoods = new Gourd(_goodsID, _stackNumber);

                    break;
                }
                case RATTAN:
                {
                    specialGoods = new Rattan(_goodsID, _stackNumber);

                    break;
                }
                case CRYSTAL:
                {
                    specialGoods = new Crystal(_goodsID, _stackNumber);

                    break;
                }
                case DRAWINGS:
                {
                    specialGoods = new Drawings(_goodsID, _stackNumber);

                    break;
                }
                case SEAL_PRAY:
                {
                    specialGoods = new SealPray(_goodsID, _stackNumber);

                    break;
                }
                case WORLD_HORN:
                {
                    specialGoods = new WorldHorn(_goodsID, _stackNumber);

                    break;
                }
                case MASS_HORN:
                {
                    specialGoods = new MassHorn(_goodsID, _stackNumber);

                    break;
                }
                case EXPERIENCE_BOOK:
                {
                    specialGoods = new ExperienceBook(_goodsID, _stackNumber);

                    break;
                }
                case EXP_BOOK_OFFLINE:
                {
                    specialGoods = new ExpBookOffline(_goodsID, _stackNumber);

                    break;
                }
                case HUNT_EXP_BOOK:
                {
                    specialGoods = new HuntExperienceBook(_goodsID,
                            _stackNumber);

                    break;
                }
                case SOUL_MARK:
                {
                    specialGoods = new SoulMark(_goodsID, _stackNumber);

                    break;
                }
                case SOUL_CHANNEL:
                {
                    specialGoods = new SoulChannel(_goodsID, _stackNumber);

                    break;
                }
                case EQUIPMENT_REPAIR:
                {
                    specialGoods = new EquipmentRepairSolvent(_goodsID,
                            _stackNumber);

                    break;
                }
                case PET_ARCHETYPE:
                {
                    specialGoods = new PetArchetype(_goodsID, _stackNumber);

                    break;
                }
                case SKILL_BOOK:
                {
                    specialGoods = new SkillBook(_goodsID, _stackNumber);

                    break;
                }
                case PET_FEED:
                {
                	specialGoods = new PetFeed(_goodsID, _stackNumber);
                	log.debug(" specialGoods is new petFeed");
                	break;
                }
                case PET_REVIVE:
                {
                	specialGoods = new PetRevive(_goodsID,_stackNumber);
                	log.debug(" specialGoods is new PetRevive");
                	break;
                }
                case PET_DICARD:
                {
                	specialGoods = new PetDicard(_goodsID, _stackNumber);
                	log.debug(" specialGoods is new PetDicard");
                	break;
                }
                case PET_SKILL_BOOK:
                {
                	specialGoods = new PetSkillBook(_goodsID, _stackNumber);
                	log.debug(" specialGoods is pet skill book ");
                	break;
                }
                case MARRY_RING:
                {
                    specialGoods = new MarryRing(_goodsID,_stackNumber);
                    break;
                }
                case DIVORCE:
                {
                    specialGoods = new Divorce(_goodsID, _stackNumber);
                    break;
                }
                case HEAVEN_BOOK:
                {
                    specialGoods = new HeavenBook(_goodsID,_stackNumber);
                    break;
                }
                //add by zhengl; date: 2011-02-14; note: 添加任务传送道具
                case TASK_TRANSPORT:
                {
                    specialGoods = new TaskTransportItem(_goodsID,_stackNumber);
                    break;
                }
                //add by zhengl; date: 2011-03-14; note: 大补丸功能.
                case BIG_TONIC:
                {
                	specialGoods = new BigTonicBall(_goodsID,_stackNumber);
                	break;
                }
                //add by zhengl; date: 2011-03-15; note: 洗点符功能.
                case RINSE_SKILL:
                {
                	specialGoods = new RinseSkill(_goodsID,_stackNumber);
                	break;
                }
                //add by zhengl; date: 2011-03-15; note: 复活石功能.
                case REVIVE_STONE:
                {
                	specialGoods = new ReviveStone(_goodsID,_stackNumber);
                	break;
                }
                //add by zhengl; date: 2011-03-16; note: 按次宠物卡.
                case PET_PER:
                {
                	specialGoods = new PetPerCard(_goodsID,_stackNumber);
                	break;
                }
                //add by zhengl; date: 2011-03-16; note: 计时宠物卡.
                case PET_TIME:
                {
//                	specialGoods = new GuildBuild(_goodsID,_stackNumber);
                	break;
                }
                case FLOWER:
                {   //鲜花  2011-03-20 jiaodongjie
                    specialGoods = new Flower(_goodsID,_stackNumber);
                    break;
                }
                case CHOCOLATE:
                {   //巧克力 2011-03-20 jiaodongjie
                    specialGoods = new Chocolate(_goodsID,_stackNumber);
                    break;
                }
                case GUILD_BUILD:
                {   //add by zhengl; date: 2011-03-16; note: 公会卡.
                	specialGoods = new GuildBuild(_goodsID,_stackNumber);
                	break;
                }
                case SPOUSE_TRANSPORT:
                {
                    specialGoods = new SpouseTransport(_goodsID,_stackNumber);
                    break;
                }
                case BAG_EXPAN:
                {
                    specialGoods = new BagExpan(_goodsID,_stackNumber);
                    break;
                }
                case PET_FOREVER:
                {
                	//add by zhengl; date: 2011-03-16; note: 永久坐骑卡
                	specialGoods = new PetForeverCard(_goodsID,_stackNumber);
                	break;
                }
                case GIFT_BAG:
                {
                	//add by zhengl; date: 2011-03-31; note: 礼包
                	specialGoods = new GiftBag(_goodsID,_stackNumber);
                	break;
                }
                case HOOK_EXP:
                {
                    //离线经验 add by jiaodongjie  2011-04-10
                    specialGoods = new HookExp(_goodsID,_stackNumber);
                    break;
                }
                case REPEATE_TASK_EXPAN:
                {
                    //循环任务扩展 add by jiaodj 2011-04-17
                    specialGoods = new RepeateTaskExpan(_goodsID,_stackNumber);
                    break;
                }
            }

            return specialGoods;
        }

        return null;
    }
}
