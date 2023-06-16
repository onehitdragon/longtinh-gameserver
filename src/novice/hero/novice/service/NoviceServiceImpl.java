package hero.novice.service;

import hero.expressions.service.CEService;
import hero.item.EquipmentInstance;
import hero.item.bag.EBagType;
import hero.item.service.EquipmentFactory;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.map.Map;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.map.message.PlayerRefreshNotify;
import hero.map.message.ResponseMapBottomData;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.service.MapServiceImpl;
import hero.novice.message.EndNoviceWizard;
import hero.pet.Pet;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.player.message.MoneyChangeNofity;
import hero.player.service.PlayerServiceImpl;
import hero.share.EObjectLevel;
import hero.share.EVocation;
import hero.share.EVocationType;
import hero.ui.message.ResponseEuipmentPackageChange;
import org.apache.log4j.Logger;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.AbsServiceAdaptor;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 NoviceServiceImpl.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-9-27 上午10:10:33
 * @描述 ：
 */

public class NoviceServiceImpl extends AbsServiceAdaptor<NoviceConfig>
{
     private static Logger log = Logger.getLogger(NoviceServiceImpl.class);
    /**
     * 单例
     */
    private static NoviceServiceImpl instance;

    /**
     * 私有构造
     */
    private NoviceServiceImpl()
    {
        config = new NoviceConfig();
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static NoviceServiceImpl getInstance ()
    {
        if (null == instance)
        {
            instance = new NoviceServiceImpl();
        }

        return instance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.service.basic.ServiceAdaptor#clear(int)
     */
    public void clean (int _userID)
    {

    }

    /**
     * 获取新手地图编号
     * 
     * @return
     */
    public short getNoviceMapID ()
    {
        return config.novice_map_id;
    }

    /**
     * 获取新手
     * 
     * @return
     */
    public short getNoviceBornX ()
    {
        return config.novice_map_born_x;
    }

    /**
     * 获取新手
     * 
     * @return
     */
    public short getNoviceBornY ()
    {
        return config.novice_map_born_y;
    }

    /**
     * 完成新手引导
     * 
     * @param _player
     */
    public void completeNoviceWizard (HeroPlayer _player)
    {
        Map map = MapServiceImpl.getInstance().getNormalMapByID(
                PlayerServiceImpl.getInstance().getInitBornMapID(
                        _player.getClan()));

        if (null != map)
        {
            _player.setCellX(PlayerServiceImpl.getInstance().getInitBornX(
                    _player.getClan()));
            _player.setCellY(PlayerServiceImpl.getInstance().getInitBornY(
                    _player.getClan()));
            _player.setLevel(config.level_when_complete_novice_teaching);
            _player.setUpgradeNeedExp(CEService.expToNextLevel(_player
                    .getLevel(), _player.getUpgradeNeedExp()));

            PlayerServiceImpl.getInstance().addMoney(_player,
                    config.novice_monster_money + config.novice_task_money, 1,
                    PlayerServiceImpl.MONEY_DRAW_LOCATION_NONE, "新手奖励");

            EquipmentInstance ei = EquipmentFactory.getInstance().build(
                    _player.getUserID(), _player.getUserID(),
                    config.award_equipment_id);

            GoodsServiceImpl.getInstance().addEquipmentInstance2Body(_player,
                    ei, CauseLog.NOVICE);

            initSecondWeapon(_player);
            
            //add:	zhengl
            //date:	2010-07-20
            //note:	为策划测试添加极品装备  //50026
            /*if(_player.getVocation() == EVocation.LONG_DOU_SHI){
            	GoodsServiceImpl.getInstance().addGoods2Package(_player, 10072, 1, CauseLog.NOVICE);//10072
            	GoodsServiceImpl.getInstance().addGoods2Package(_player, 20093, 1, CauseLog.NOVICE);//20093
            } else {
            	GoodsServiceImpl.getInstance().addGoods2Package(_player, 14173, 1, CauseLog.NOVICE);
            	GoodsServiceImpl.getInstance().addGoods2Package(_player, 21054, 1, CauseLog.NOVICE);
            }
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 50026, 20, CauseLog.NOVICE);
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 50026, 20, CauseLog.NOVICE);
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 50026, 20, CauseLog.NOVICE);
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 50026, 20, CauseLog.NOVICE);
            
            //加打孔石测试
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 70000, 20, CauseLog.NOVICE);
            
            //add by zhengl ,增加一个转职物品,方便转职
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 4001, 1, CauseLog.NOVICE);
            //end
            

//          //给新手添加宠物蛋 , TODO　宠物蛋到底放在新手礼包里？
//            PetServiceImpl.getInstance().addPetEgg(_player);
             
            */
            //add end
            _player.addMoney(5000000);

            PlayerServiceImpl.getInstance().reCalculateRoleProperty(_player);
            PlayerServiceImpl.getInstance().refreshRoleProperty(_player);

            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new ResponseMapBottomData(_player, map, null));
            ResponseMessageQueue.getInstance().put(
                    _player.getMsgQueueIndex(),
                    new ResponseMapGameObjectList(
                            _player.getLoginInfo().clientType, map));

            _player.live(map);
            map.getPlayerList().add(_player);
            MapSynchronousInfoBroadcast.getInstance().put(map,
                    new PlayerRefreshNotify(_player), true, _player.getID());
            NoviceDAO.completeNoviceTeaching(_player);
        }
    }

    /**
     * 退出新手引导
     * 
     * @param _player
     */
    public void exitNoviceWizard (HeroPlayer _player)
    {
        Map map = MapServiceImpl.getInstance().getNormalMapByID(
                PlayerServiceImpl.getInstance().getInitBornMapID(
                        _player.getClan()));
log.info("退出新手引导 ...");
        if (null != map)
        {
            _player.setCellX(PlayerServiceImpl.getInstance().getInitBornX(
                    _player.getClan()));
            _player.setCellY(PlayerServiceImpl.getInstance().getInitBornY(
                    _player.getClan()));


            
            //add by zhengl ,增加一个转职物品,方便转职
//            GoodsServiceImpl.getInstance().addGoods2Package(_player, 4001, 1, CauseLog.NOVICE);
            //end

            _player.live(map);
            map.getPlayerList().add(_player);
            MapSynchronousInfoBroadcast.getInstance().put(map,
                    new PlayerRefreshNotify(_player), true, _player.getID());
            NoviceDAO.exitNoviceTeaching(_player);

            initSecondWeapon(_player);

            //add:	zhengl
            //date:	2010-07-20
            //note:	为策划测试添加极品装备  //50026
//            if(_player.getVocation() == EVocation.AN_LONG_YIN_SHI){
//            	GoodsServiceImpl.getInstance().addGoods2Package(_player, 10072, 1, CauseLog.NOVICE);//10072
//            	GoodsServiceImpl.getInstance().addGoods2Package(_player, 20093, 1, CauseLog.NOVICE);//20093
//            } else {
//            	GoodsServiceImpl.getInstance().addGoods2Package(_player, 14173, 1, CauseLog.NOVICE);
//            	GoodsServiceImpl.getInstance().addGoods2Package(_player, 21054, 1, CauseLog.NOVICE);
//            }
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 50026, 20, CauseLog.NOVICE);
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 50026, 20, CauseLog.NOVICE);
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 50026, 20, CauseLog.NOVICE);
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 50026, 20, CauseLog.NOVICE);
            
            //加打孔石,剥离石测试
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 70000, 20, CauseLog.NOVICE);
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 70000, 20, CauseLog.NOVICE);
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 70000, 20, CauseLog.NOVICE);
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 69999, 20, CauseLog.NOVICE);
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 69999, 20, CauseLog.NOVICE);
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 69999, 20, CauseLog.NOVICE);

            //add by zhengl ,增加一个转职物品,方便转职
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 4001, 1, CauseLog.NOVICE);
            //end
            
            _player.setLevel(50);
            _player.surplusSkillPoint += 100;
            //add by zhengl , 添加各种装备 测试换装的动画
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 11001, 1, CauseLog.NOVICE);
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 19001, 1, CauseLog.NOVICE);


//          //给新手添加宠物蛋 , TODO　宠物蛋到底放在新手礼包里？
//            PetServiceImpl.getInstance().addPetEgg(_player);

            _player.addMoney(3000000);
            //add end
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new ResponseMapBottomData(_player, map, null));
            ResponseMessageQueue.getInstance().put(
                    _player.getMsgQueueIndex(),
                    new ResponseMapGameObjectList(
                            _player.getLoginInfo().clientType, map));
        }
    }
    
    /**
     * <p>新手奖励获得然后离开新手状态</p>
     * add by zhengl
     * date:	2011-02-22
     * note:	符合现在的模式合理化代码结构
     * @param _player
     */
    public void novicePlayerAward (HeroPlayer _player) {
    	PlayerServiceImpl.getInstance().leaveNovice(_player);
    	_player.setMp(_player.getBaseProperty().getMpMax());
    	
    	if(config.is_novice_award) {
    		//add by zhengl; date: 2011-03-04; note: 加钱,加等级,加技能点
    		_player.addMoney(config.novice_award_money);
    		_player.setLevel(config.novice_award_level);
    		_player.surplusSkillPoint += config.novice_award_skill_point;
    		PlayerServiceImpl.getInstance().roleUpgrade(_player);
    		//end
    		//add by zhengl; date: 2011-03-04; note：添加活动期间新手奖励
    		int[] goodsList = config.getInitAwardList(_player.getVocation());
    		if(goodsList != null && goodsList.length > 0) {
    			for (int j = 0; j < goodsList.length; j++) {
    				GoodsServiceImpl.getInstance().addGoods2Package(
    						_player, goodsList[j], 1, CauseLog.NOVICE);
    			}
    		}
    		for (int i = 0; i < config.novice_award_item.length; i++) {
				GoodsServiceImpl.getInstance().addGoods2Package(
						_player, 
						config.novice_award_item[i][0], 
						config.novice_award_item[i][1], CauseLog.NOVICE);
			}
    		
    		// 加一个测试宠物 jiaodongjie 0314
//    		Pet pet1 = PetServiceImpl.getInstance().addPet(_player.getUserID(), 121);
//            pet1.feeding=250;
//            pet1.fun = Pet.PET_HERBIVORE_FUN;
//            pet1.bornFrom = 1;
//            pet1.bind = 1;
//            pet1.color = 1;
//            PetServiceImpl.getInstance().updatePet(_player.getUserID(),pet1);
    	}
    	
    }

    /**
     * DEMO 版，初始化第二件武器
     * 配置见 player.xml
     * @param _player
     */
    private void initSecondWeapon(HeroPlayer _player){
        int secondGoodsid = PlayerServiceImpl.getInstance().getConfig().getInitSecondWeapon(_player.getVocation());
        if(secondGoodsid > 0){
            GoodsServiceImpl.getInstance().addGoods2Package(_player, secondGoodsid, 1, CauseLog.NOVICE);
        }
    }
}
