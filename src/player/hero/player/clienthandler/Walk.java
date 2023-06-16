package hero.player.clienthandler;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

import hero.effect.Effect;
import hero.effect.detail.StaticEffect;
import hero.fight.service.FightServiceImpl;
import hero.login.LoginServiceImpl;
import hero.npc.Monster;
import hero.pet.Pet;
import hero.pet.clienthandler.OperatePet;
import hero.player.HeroPlayer;
import hero.player.message.OtherWalkNotify;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;
import hero.skill.detail.ESpecialStatus;

public class Walk extends AbsClientProcess
{
	private static Logger log = Logger.getLogger(Walk.class);
	//
	//2011-05-10 by zhengl
	//做速度判定的时候,采取以下规则即可:
	//1,骑宠速度 <=8
	//2,步行速度<=6
	//3,为避免错误的状态导致其他情况,减速暂时不纳入判定.
	//
	/**
	 * 正常速度移动最大格子数
	 * 正常值5
	 */
	private static final byte COM_MAX_GRID = 6;
	/**
	 * 减速DEBUFF最大格子数
	 * 正常值3
	 */
	private static final byte DEBUFF_ADD = 4;
	/**
	 * 加速BUFF最大格子数
	 * 正常值7
	 */
	private static final byte BUFF_ADD = 8;
	/**
	 * 坐骑BUFF最大格子数
	 * 正常值7
	 */
	private static final byte MOUNT_ADD = 8;
	
    public void read () throws Exception
    {
        try
        {
            HeroPlayer player = (HeroPlayer) PlayerServiceImpl.getInstance()
                    .getPlayerBySessionID(contextData.sessionID);
//            //add by zhengl; date: 2011-05-08; note: 测试
//            Monster monster = null;
//            player.addHp(5000);
//            for (int i = 0; i < player.where().getMonsterList().size(); i++) 
//            {
//            	monster = (Monster)player.where().getMonsterList().get(i);
//            	monster.setAttackerAtFirst(player);
//            	FightServiceImpl.getInstance().processReduceHp(
//            			player, monster, 20, true, false, null);
//    		}
//            //add by zhengl; date: 2011-05-08; note: 测试

            if (null != player)
            {
            	//del by zhengl ; date: 2011-01-05 ; note: 暂时不这样处理,代码勿删除
//            	if(player.illegalOperation >= 50) {
//            		OutMsgQ.getInstance().put(player.getMsgQueueIndex(), new Warning(ILLEGAL_MOVE));
//                	return;
//            	}
//            	long playerTime = input.readLong();
//            	if(player.timeVerifyMachine.size() >= 20) {
//            		long lastTime = 0;
//            		long nowTime = 0;
//            		byte i = 0;
//    				Iterator it = player.timeVerifyMachine.entrySet().iterator();
//    				while (it.hasNext()) {
//    					i += 1;
//    					Map.Entry pairs = (Map.Entry)it.next();
//    					nowTime = (long)pairs.getValue();
//    					if(i == player.timeVerifyMachine.size()) {
//    						
//    					} else if (i == 1) {
//							
//						}
//    				}
//            	} else {
//            		player.timeVerifyMachine.put(System.currentTimeMillis(), playerTime);
//				}
            	//end
            	player.walkCounter += 1;
            	if(player.walkCounter >= 50) {
            		long time = System.currentTimeMillis() - player.timeVerify;
            		log.info(player.getName()+"[移动50次花费]:" + String.valueOf(time));
            		if(time > 0 && time < 60001) 
            		{
            			ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
            					new Warning(Tip.TIP_PLAYER_ILLEGAL_MOVE, 
            							Warning.UI_TOOLTIP_TIP));
                		player.illegalOperation += 1;
            		}
            		player.timeVerify = System.currentTimeMillis();
            		player.walkCounter = 1;
            	}
                byte walkGridNum = yis.readByte();
                byte[] walkPath = new byte[walkGridNum];
                boolean slowly = false;
                boolean fast = false;
                boolean mount = false;
                boolean wrong = false;
                //1,战斗状态 
                //	1)冰冻攻击减速状态1
                //	2)正常状态2
                //	3)疾跑状态3
                //2,非战斗状态
                //	1)冰冻攻击减速状态1
                //	2)正常状态2
                //	3)疾跑状态3
                //	4)坐骑状态4

            	for (Effect ef : player.effectList) {
            		if (ef instanceof StaticEffect) {
            			if (((StaticEffect) ef).specialStatus == ESpecialStatus.HIDE) {
            				slowly = true;
            			} else if (((StaticEffect) ef).specialStatus == ESpecialStatus.MOVE_SLOWLY) {
            				slowly = true;
						} else if (((StaticEffect) ef).specialStatus == ESpecialStatus.MOVE_FAST) {
							fast = true;
						}
            		}
				}
                if(!player.isInFighting()) {
                	Pet[] pets = player.getBodyWearPetList().getPetList();
                	for (int i = 0; i < pets.length; i++) {
                		if(pets[i] != null && pets[i].pk.getType() == Pet.PET_TYPE_HERBIVORE) {
                			mount = true;
                		}
					}
                }
                if(slowly && fast) {
    				if( walkGridNum > (byte)COM_MAX_GRID ) {
    					wrong = true;
    				}
                } else if (slowly) {
    				if( walkGridNum > DEBUFF_ADD ) {
    					wrong = true;
    				}
				} else if (fast) {
    				if( walkGridNum > (byte)(BUFF_ADD) ) {
    					wrong = true;
    				}
				} else if (mount) {
					//上坐骑之后可以覆盖一切其他速度加持.
    				if( walkGridNum > (byte)(MOUNT_ADD) ) {
    					wrong = true;
    				}
				} else {
    				if( walkGridNum > (byte)COM_MAX_GRID ) {
    					wrong = true;
    				}
				}
                if(wrong) {
                	player.walkIllegalCounter += 1;
                	return;
                } else {
                	player.walkIllegalCounter -= 1;
				}
            	if (player.illegalOperation >= 2) {
					log.error("玩家"+player.getName()+"使用移动加速外挂,请注意");
					LoginServiceImpl.getInstance().resetPlayersStatus(
							player.getLoginInfo().accountID);
				}
            	if (player.walkIllegalCounter >= 2) {
            		ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
            				new Warning(Tip.TIP_PLAYER_ILLEGAL_MOVE_1, Warning.UI_TOOLTIP_TIP));
//            		return;
            	}
            	if (player.walkIllegalCounter >= 3) {
					log.error("玩家"+player.getName()+"使用篡改客户端,请注意");
					LoginServiceImpl.getInstance().resetPlayersStatus(
							player.getLoginInfo().accountID);
				}
                for (int i = 0; i < walkPath.length; i++)
                {
                	//整个for可以优化.没有必要每走1个格子 执行go方法一次.
                    walkPath[i] = yis.readByte();
                    player.go(walkPath[i]);
                }

                byte endX = yis.readByte();
                byte endY = yis.readByte();
                player.setCellX(endX);
                player.setCellY(endY);

                player.needUpdateDB = true;

                if (0 < player.where().getPlayerList().size())
                {
                	//add by zhengl; date: 2011-03-01; note:添加x,y最终移动的坐标
                    AbsResponseMessage msg = new OtherWalkNotify(player.getID(),
                            player.getMoveSpeed(), walkPath, endX, endY);

                    HeroPlayer other;

                    for (int i = 0; i < player.where().getPlayerList().size(); i++)
                    {
                        other = (HeroPlayer) player.where().getPlayerList()
                                .get(i);

                        if (other.isEnable() && other != player)
                        {
                            ResponseMessageQueue.getInstance().put(
                                    ((HeroPlayer) other).getMsgQueueIndex(),
                                    msg);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
