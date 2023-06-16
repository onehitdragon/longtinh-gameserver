package hero.fight.clienthandler;

import hero.npc.Monster;
import hero.npc.dict.MonsterImageConfDict;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.player.clienthandler.ExitGame;
import hero.player.service.PlayerServiceImpl;
import hero.share.Constant;
import hero.share.EObjectType;
import hero.share.ME2GameObject;
import hero.share.message.Warning;
import hero.share.service.Tip;

import java.io.IOException;

import org.apache.log4j.Logger;

import hero.fight.service.FightServiceImpl;
import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 PhysicsAttack.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-30 上午11:45:49
 * @描述 ：普通物理攻击
 */

public class PhysicsAttack extends AbsClientProcess
{
     private static Logger log = Logger.getLogger(PhysicsAttack.class);

    /*
     * (non-Javadoc)
     * 
     * @see me2.core.handler.ClientHandler#read()
     */
    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);
        log.info("普通攻击.....");
        if (null == player || !player.isEnable() || player.isDead()
                || System.currentTimeMillis() - player.lastAttackTime < 1500)
        {
        	if (null == player) {
				log.error("null == player");
			}
        	if (!player.isEnable()) {
				log.error("玩家未激活.");
			}
        	if (player.isDead()) {
				log.error("玩家已经死亡:" + player.getName());
			}
        	if (System.currentTimeMillis() - player.lastAttackTime < 1500) {
				log.warn("玩家攻击间隔小于1.5秒:" + player.getName());
			}
            return;
        }

        try
        {
            byte targetType = yis.readByte();
            int targetObjectID = yis.readInt();

            ME2GameObject target = null;

            if (EObjectType.MONSTER.value() == targetType)
            {
                target = player.where().getMonster(targetObjectID);

                if (null == target)
                {
                	log.error("null == target, targetType=" 
                			+ String.valueOf(targetType) 
                			+ ", targetObjectID=" + String.valueOf(targetObjectID));
                    return;
                }
            }
            else if(EObjectType.PLAYER.value() == targetType)
            {
                target = player.where().getPlayer(targetObjectID);

                if (null == target
                        || ( player.getClan() == target.getClan() 
                        		&& player.getDuelTargetUserID() 
                        			!= ((HeroPlayer) target).getUserID()) )
                {
                	if (null == target) {
                    	log.error("null == target, targetType=" 
                    			+ String.valueOf(targetType) 
                    			+ ", targetObjectID=" + String.valueOf(targetObjectID));
					}
                    return;
                }
            }

            if (target != null && target.isEnable() && !target.isDead())
            {
            	//edit by zhengl ; date: 2011-01-06 ; note:攻击中怪物碰撞距离判定修改.
            	float distance = 0;
            	if (target instanceof Monster) {
            		
					MonsterImageConfDict.Config monsterConfig = 
						MonsterImageConfDict.get( ((Monster)target).getImageID() );
					distance = monsterConfig.grid/2;
				}
                /*if (Math.sqrt(Math.pow(player.getCellX() - target.getCellX(), 2)
                        + Math.pow(player.getCellY() - target.getCellY(), 2)) 
                        	<= (player.getAttackRange() + Constant.BALANCE_ATTACK_DISTANCE + distance))*/
                boolean inDistance = (player.getCellX() - target.getCellX())*(player.getCellX() - target.getCellX())
                        +(player.getCellY() - target.getCellY())*(player.getCellY() - target.getCellY())
                        <= (player.getAttackRange() + Constant.BALANCE_ATTACK_DISTANCE + distance)*(player.getAttackRange() + Constant.BALANCE_ATTACK_DISTANCE + distance);
                if(inDistance)
                {
                    FightServiceImpl.getInstance().processPhysicsAttack(player,
                            target);
                }
                else
                {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_FIGHT_OF_ATTACK_RANGE_ENOUGH));
                }
                //add:	zhengl
                //date:	2010-10-17
                //note:	添加宠物目标
//                PetServiceImpl.getInstance().setAttackTarget(player, target);
            }
        }
        catch (IOException e)
        {
        	log.error("...IOException...",e);
            e.printStackTrace();
        }
    }

}
