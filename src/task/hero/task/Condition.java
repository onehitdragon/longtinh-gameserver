package hero.task;

import java.util.ArrayList;
import java.util.List;

import hero.lover.service.LoverServiceImpl;
import hero.manufacture.Manufacture;
import hero.manufacture.ManufactureType;
import hero.manufacture.service.ManufactureServerImpl;
import hero.micro.teach.MasterApprentice;
import hero.micro.teach.TeachService;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.task.target.TaskType;
import javolution.util.FastList;

import hero.player.define.EClan;
import hero.share.EVocation;
import org.apache.log4j.Logger;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Condition.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-10 上午11:15:58
 * @描述 ：
 */

public class Condition
{
    private static Logger log = Logger.getLogger(Condition.class);
    /**
     * 需要职业
     */
    public EVocation vocation;

    /**
     * 需要阵营
     */
    public EClan     clan;

    /**
     * 需要等级
     */
    public short     level;

    /**
     * 需要已完成的任务的编号
     */
    public int       completeTaskID;
    
    /**
     * 后续任务ID
     */
    public int       taskNext;

    /**
     * 任务类型
     * 自己，师徒，婚姻
     */
    public TaskType  taskType;

    /**
     * 需要掌握的生活技能
     * 当任务是生活技能升级任务时，玩家需要已学习过对应生活技能，才能接收此任务
     */
    public ManufactureType manufactureType;

    /**
     * 是否满足条件
     * 
     * @param _vocation
     * @param _clan
     * @param _level
     * @param _completeTaskIDList
     * @return
     */
    public boolean check (int userID, EVocation _vocation, EClan _clan, short _level,
            ArrayList<Integer> _completeTaskIDList)
    {
        log.debug("conditon check start ... player level= " + _level +"  condition level= " + level);
        if (null != vocation && vocation != EVocation.ALL && vocation != _vocation)
        {
            return false;
        }

        if (EClan.NONE != clan && clan != _clan)
        {
            return false;
        }
        if (_level < level)
        {
            return false;
        }
        if(null != manufactureType){
            List<Manufacture> manufactureList = ManufactureServerImpl.getInstance().getManufactureListByUserID(userID);
            if(null == manufactureList){
                log.debug("need manuf skill == null return false..");
                return false;
            }else {
                boolean noManuf = true;
                for(Manufacture manuf : manufactureList){
                    if(manuf.getManufactureType() == manufactureType){
                        noManuf = false;
                    }
                }
                if(noManuf){
                    log.debug("had manuf,but != task manuf...");
                    return false;
                }
            }
        }
        log.debug("task type = " + taskType);
        if(taskType != TaskType.SINGLE){
            if(taskType == TaskType.MARRY){
                //TODO 查看玩家是否已结婚，未婚 return false
                HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(userID);
               String spouse = LoverServiceImpl.getInstance().whoLoveMe(player.getName());
                if(spouse == null){
                    spouse = LoverServiceImpl.getInstance().whoMarriedMe(player.getName());
                    if(spouse == null){
                        return false;
                    }
                }
            }
            if(taskType == TaskType.MASTER){
                //TODO 查看玩家是否有师徒关系，没有 return false
                MasterApprentice masterApprentice = TeachService.get(userID);
                log.debug("task check master apprentice : " + masterApprentice);

                if(null != masterApprentice && !masterApprentice.isValidate()){
                    log.debug("task check master is not validate");
                    /*for(MasterApprentice.ApprenticeInfo app : masterApprentice.apprenticeList){
                        log.debug(" apprentice id = " + app.userID +" , name= " + app.name);
                    }
*/
                    return false;
                }else if(null == masterApprentice){
                    log.debug("task check master = null");
                    return false;
                }
            }
        }
        log.debug("next check contains taskid");
        if (0 != completeTaskID && null != _completeTaskIDList
                && !_completeTaskIDList.contains(completeTaskID))
        {
        	log.debug("not ");
            return false;
        }

        return true;
    }
}
