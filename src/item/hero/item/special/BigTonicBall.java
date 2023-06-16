package hero.item.special;

import java.util.Hashtable;
import java.util.Random;

import yoyo.core.queue.ResponseMessageQueue;

import hero.log.service.LogServiceImpl;
import hero.fight.service.FightServiceImpl;
import hero.item.SingleGoods;
import hero.item.SpecialGoods;
import hero.item.bag.exception.BagException;
import hero.item.dictionary.GoodsContents;
import hero.item.dictionary.SpecialGoodsDict;
import hero.item.message.ResponseSpecialGoodsBag;
import hero.item.service.GoodsDAO;
import hero.item.service.GoodsServiceImpl;
import hero.player.HeroPlayer;
import hero.share.message.Warning;
import hero.share.service.Tip;

/**
 * 大补丸...
 * @author Administrator
 * 在以后优化的时候可以考虑用OO方式把这样类型的物品做出一个父类.
 *
 */
public class BigTonicBall extends SpecialGoods {

	private int[][] TONIC_LIST;
	//---------规则---------
	//
	//---------规则---------
	/**
	 * 未激活状态
	 */
	public static final int TONINC_CODE = 0;
	/**
	 * 激活状态
	 */
	public static final int TONINC_ACTIVATE = 1;
	/**
	 * 只能手动使用的状态
	 */
	public static final int TONINC_UNAUTO = 2;
	/**
	 * 红丸子
	 */
	public static final int TONINC_RED = 0;
	/**
	 * 蓝丸子
	 */
	public static final int TONINC_BULE = 1;
	/**
	 * 剩余点数
	 */
	public int surplusPoint;
	
	private String oldDescription;
	
	private String nowDescription;
	/**
	 * 在背包中的位置
	 */
	private int location;
	
	/**
	 * 滋补类型(0=红/1=蓝)
	 */
	public int tonincType;
	
	/**
	 * 激活状态
	 * 0=未激活;1=已激活;2=手动类型物品
	 */
	public int isActivate;
	
//	public Hashtable<Integer, BigTonicBall> tonicList;
	
	/**
	 * 从数据库初始化数据
	 * @param _surplusPoint
	 * @param _type
	 * @param _tonicID
	 */
	public void initData(int _surplusPoint, int _type, int _index) {
		isActivate = _type;
		surplusPoint = _surplusPoint;
		location = _index;
	}
	
	public void copyGoodsData(SpecialGoods _goods, HeroPlayer _player)
	{
		setName(_goods.getName());
		setIconID(_goods.getIconID());
		setTrait(_goods.getTrait());
		if (_goods.useable()) {
			setUseable();
		}
		oldDescription = _goods.getDescription();
		nowDescription = "\n剩余:" + surplusPoint;
		replaceDescription(oldDescription, nowDescription);
		
		if(isActivate == TONINC_ACTIVATE){
			if(tonincType == 1) {
				_player.setBuleTonicBall(this);
			} else {
				_player.setRedTonicBall(this);
			}
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
					new Warning(Tip.TIP_ITEM_START_ACTIVATE.replaceAll("%fname", getName()), 
							Warning.UI_STRING_TIP));
		}
	}
	
	public BigTonicBall(int _id, short nums) {
		super(_id, nums);
		TONIC_LIST = GoodsServiceImpl.getInstance().getConfig().getSpecialConfig().big_tonic;
		for (int i = 0; i < TONIC_LIST.length; i++) {
			if(_id == TONIC_LIST[i][0]) {
				surplusPoint = TONIC_LIST[i][3];
				isActivate = TONIC_LIST[i][2];
				tonincType = TONIC_LIST[i][1];
			}
		}
	}
	
	// /**
	//  * 获得该物品的红蓝类型
	//  * @return
	//  */
	// public static boolean getRedOrBule(int _)
	// {
	// 	boolean result = false;
		
	// 	return result;
	// }
	
	private void descriptionUpdate(HeroPlayer _player)
	{
		nowDescription = "\n剩余:" + surplusPoint;
		replaceDescription(oldDescription, nowDescription);
		GoodsDAO.updateTonic(_player.getUserID(), location, getID(), surplusPoint, isActivate);
        ResponseMessageQueue.getInstance().put(
                _player.getMsgQueueIndex(),
                new ResponseSpecialGoodsBag(
                		_player.getInventory().getSpecialGoodsBag(), 
                		_player.getShortcutKeyList()));
	}
	
	/**
	 * 吃丸子.
	 * @param _player
	 * @return
	 */
	private boolean eatBall(HeroPlayer _player)
	{
		boolean remove = false;
		if(tonincType == TONINC_RED) {
			int hp = _player.getActualProperty().getHpMax() - _player.getHp();
			if(hp > 0) {
				if(surplusPoint > hp) {
					surplusPoint -= hp;
		            FightServiceImpl.getInstance().processAddHp(_player, _player,
		            		hp, true, false);
				} else {
					FightServiceImpl.getInstance().processAddHp(_player, _player,
							surplusPoint, true, false);
					ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
							new Warning(
									Tip.TIP_ITEM_TONIC_END.replaceAll("%fname", getName()), 
									Warning.UI_STRING_TIP));
					_player.setRedTonicBall(null);
					remove = true;
				}
				descriptionUpdate(_player);
			} else {
				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
						new Warning(Tip.TIP_ITEM_HP_IS_FULL, Warning.UI_STRING_TIP));
			}
		} else {
			int mp = _player.getActualProperty().getMpMax() - _player.getMp();
			if(mp > 0) {
				if(surplusPoint > mp) {
					surplusPoint -= mp;
		            _player.addMp(mp);
		            FightServiceImpl.getInstance().processSingleTargetMpChange(_player, true);
				} else {
		            _player.addMp(surplusPoint);
		            FightServiceImpl.getInstance().processSingleTargetMpChange(_player, true);
					ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
							new Warning(
									Tip.TIP_ITEM_TONIC_END.replaceAll("%fname", getName()), 
									Warning.UI_STRING_TIP));
					_player.setBuleTonicBall(null);
					remove = true;
				}
				descriptionUpdate(_player);
			} else {
				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
						new Warning(Tip.TIP_ITEM_MP_IS_FULL, Warning.UI_STRING_TIP));
			}
		}
		return remove;
	}
	
	/**
	 * 提供给脱离战斗后的自动使用
	 * @param _player
	 * @param _location
	 * @return
	 * @throws BagException 
	 */
	public boolean use(HeroPlayer _player)
	{
		boolean remove = false;
		remove = eatBall(_player);
		if(remove) {
            if (disappearImmediatelyAfterUse())
            {
                try {
					remove(_player, (short)location);
			        LogServiceImpl.getInstance().goodsUsedLog(
			        		_player.getLoginInfo().accountID,
			        		_player.getLoginInfo().username,
			                _player.getUserID(),
			                _player.getName(), 
			                getID(),getName(), 
			                getTrait().getDesc(), 
			                getType().getDescription());
				} catch (BagException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
		}
		return remove;
	}

	@Override
	public boolean beUse(HeroPlayer _player, Object _target, int _location) {
		// TODO Auto-generated method stub
		boolean remove = false;
		//eidt by zhengl; date: 2011-04-01; note: 战斗中也可以使用.
//		if (_player.isInFighting()) {
//			OutMsgQ.getInstance().put(_player.getMsgQueueIndex(), 
//					new Warning(Tip.TIP_ITEM_IN_FIGHTING, Warning.UI_STRING_TIP));
//			return remove;
//		}
		location = _location;
		if(isActivate == TONINC_UNAUTO) {
			remove = eatBall(_player);
		} else {
			if(tonincType == 1) {
				_player.setBuleTonicBall(this);
				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
						new Warning(Tip.TIP_ITEM_START_ACTIVATE.replaceAll("%fname", getName()), 
								Warning.UI_STRING_TIP));
			} else {
				_player.setRedTonicBall(this);
				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
						new Warning(Tip.TIP_ITEM_START_ACTIVATE.replaceAll("%fname", getName()), 
								Warning.UI_STRING_TIP));
			}
		}
		if(remove) {
			try {
				remove(_player, (short)_location);

                LogServiceImpl.getInstance().goodsUsedLog(
                		_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                		_player.getUserID(),_player.getName(),getID(),getName(),getTrait().getDesc(),
                		getType().getDescription());
			} catch (BagException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public boolean disappearImmediatelyAfterUse() {
		// TODO Auto-generated method stub
		return true;
	}
	
//    public String getDescription ()
//    {
//        return oldDescription + nowDescription;
//    }

	@Override
	public ESpecialGoodsType getType() {
		// TODO Auto-generated method stub
		return ESpecialGoodsType.BIG_TONIC;
	}

	@Override
	public void initDescription() {
		
		
	}

	@Override
	public boolean isIOGoods() {
		// TODO Auto-generated method stub
		return true;
	}

}
