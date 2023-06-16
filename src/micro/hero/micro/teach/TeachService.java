package hero.micro.teach;

import java.sql.Timestamp;
import java.util.Timer;

import javolution.util.FastMap;
import hero.group.Group;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.micro.teach.MasterApprentice.ApprenticeInfo;
import hero.player.HeroPlayer;
import hero.player.service.PlayerDAO;
import hero.player.service.PlayerServiceImpl;
import hero.share.letter.Letter;
import hero.share.letter.LetterService;
import hero.share.message.FullScreenTip;
import hero.share.message.MailStatusChanges;
import hero.share.message.Warning;
import hero.share.service.Tip;

import org.apache.log4j.Logger;

import yoyo.core.queue.ResponseMessageQueue;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 TeachService.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-12-8 上午09:55:46
 * @描述 ：
 */

public class TeachService
{
    private static Logger log = Logger.getLogger(TeachService.class);
    /**
     * 师徒关系列表
     */
    private static FastMap<Integer, MasterApprentice> masterApprenticeTable = new FastMap<Integer, MasterApprentice>();

    /**
     * 不在线的师徒关系表
     */
    private static FastMap<String, MasterApprentice> masterApprenticeOffLineList = new FastMap<String,MasterApprentice>();

    /**
     * 私有构造
     */
    private TeachService()
    {

    }

    /**
     * 登陆
     * 
     * @param _player
     */
    public static void login (HeroPlayer _player)
    {
        MasterApprentice masterApprentice = masterApprenticeTable.get(_player
                .getUserID());

        if (null == masterApprentice)
        {
            masterApprentice = new MasterApprentice();
            TeachDAO.loadMasterApprenticeRelation(_player.getUserID(),
                    masterApprentice);
            masterApprenticeTable.put(_player.getUserID(), masterApprentice);
        }

        HeroPlayer master = PlayerServiceImpl.getInstance().getPlayerByUserID(
                masterApprentice.masterUserID);

        log.debug("teach login _player="+_player.getUserID()+", master id="+masterApprentice.masterUserID);



        if (null != master) //_player 是徒弟
        {
            log.debug("_player is apprentice , master is online.. " + _player.getName());
            _player.changeExperienceModulus(MasterApprentice.EXP_MODULUS_WHEN_MASTER_ONLINE);
            masterApprentice.masterIsOnline = true;
        }

        MasterApprentice relationOfMaster = masterApprenticeTable
                .get(masterApprentice.masterUserID);

        if (null != relationOfMaster) //_player 是徒弟
        {
            log.debug("relationOfMaster login = " + relationOfMaster.getApprenticeOnlineNumber());
            relationOfMaster.changeApprenticeStatus(_player.getUserID(), true);
            log.debug("relationOfMaster login after = " + relationOfMaster.getApprenticeOnlineNumber());
        }

        if (null != masterApprentice.apprenticeList
                && masterApprentice.apprenticeNumber > 0)
        {
            log.debug("master login apprenticeNumber = " + masterApprentice.apprenticeNumber);
            log.debug("master login apprenticeOnLineNumber = " + masterApprentice.getApprenticeOnlineNumber());
            MasterApprentice relationOfApprentice;

            for (ApprenticeInfo apprenticeInfo : masterApprentice.apprenticeList)
            {
                log.debug("apprenticeInfo = " + apprenticeInfo);
                if (null != apprenticeInfo)
                {
                    relationOfApprentice = masterApprenticeTable
                            .get(apprenticeInfo.userID);

                    if (null != relationOfApprentice)
                    {
                        relationOfApprentice.masterIsOnline = true;

                        HeroPlayer apprentice = PlayerServiceImpl.getInstance()
                                .getPlayerByUserID(apprenticeInfo.userID);

                        if (null != apprentice)
                        {
                            if(masterApprentice.getApprenticeOnlineNumber() < MasterApprentice.MAX_APPRENTICER_NUMBER)
                                masterApprentice.addApprenticeOnlineNumber(true);

//                            changeMasterMoneyModulus(master,masterApprentice.getApprenticeOnlineNumber());
//                            _player.changeMoneyModulus(MasterApprentice.MONEY_MODULUS_OF_MASTER);

                            apprentice.changeExperienceModulus(MasterApprentice.EXP_MODULUS_WHEN_MASTER_ONLINE);
                            apprenticeInfo.isOnline = true;
                        }
                    }

                }
                else
                {
                    break;
                }
            }
        }
    }

    /**
     * 登出
     * 
     * @param _player
     */
    public static void logout (int _userID)
    {
        MasterApprentice masterApprentice = masterApprenticeTable.get(_userID);

        if (null != masterApprentice)
        {
            HeroPlayer master = PlayerServiceImpl.getInstance()
                    .getPlayerByUserID(masterApprentice.masterUserID);



            MasterApprentice relationOfMaster = masterApprenticeTable
                    .get(masterApprentice.masterUserID);

            if (null != relationOfMaster)
            {
                log.debug("user logout master online apprent number = "+relationOfMaster.getApprenticeOnlineNumber());
                relationOfMaster.changeApprenticeStatus(_userID, false);
                log.debug("user logout master online apprent number after= "+relationOfMaster.getApprenticeOnlineNumber());
            }

            if (null != masterApprentice.apprenticeList
                    && masterApprentice.apprenticeNumber > 0)
            {
                MasterApprentice relationOfApprentice;

                for (ApprenticeInfo apprenticeInfo : masterApprentice.apprenticeList)
                {
                    if (null != apprenticeInfo)
                    {
                        relationOfApprentice = masterApprenticeTable
                                .get(apprenticeInfo.userID);

                        if (null != relationOfApprentice)
                        {
                            relationOfApprentice.masterIsOnline = false;

                            HeroPlayer apprentice = PlayerServiceImpl
                                    .getInstance().getPlayerByUserID(
                                            apprenticeInfo.userID);

                            if (null != apprentice)
                            {
                                apprentice
                                        .changeExperienceModulus(-MasterApprentice.EXP_MODULUS_WHEN_MASTER_ONLINE);
                            }
                        }
                    }
                    else
                    {
                        break;
                    }
                }
            }

//            changeMasterMoneyModulus(master,masterApprentice.getApprenticeOnlineNumber());
        }
    }

    /**
     * @param _userID
     */
    public static void clear (int _userID)
    {
        masterApprenticeTable.remove(_userID);
    }

    /**
     * 清除师傅的所有徒弟
     * @param _userID 师傅ID
     */
    public static void dismissAll(int _userID){
        MasterApprentice master = get(_userID);
        log.debug("dissmissAll master = " + master);
        if(null != master){
            HeroPlayer masterPlayer = PlayerServiceImpl.getInstance().getPlayerByUserID(_userID);
            log.debug("dismiss all apprent , master = " + masterPlayer.getName());
            ApprenticeInfo[] apprenticeList = master.apprenticeList;
            log.debug("apprenticeList size = " + apprenticeList.length);
            HeroPlayer apprenticePlayer;
            if(TeachDAO.deleteAllMasterApprenticeRelation(_userID)){
                for(ApprenticeInfo apprentice : apprenticeList){
                    if(apprentice != null){
                        log.debug("deleted all master apprentice relation...");

                        MasterApprentice apprenticeRelation = masterApprenticeTable.get(apprentice.userID);

                        if (null != apprenticeRelation
                                && apprenticeRelation.masterUserID == _userID)
                        {
                            apprenticeRelation.leftMaster();

                            if (!apprenticeRelation.isValidate())
                            {
                                masterApprenticeTable.remove(apprentice.userID);
                            }
                        }


                        apprenticePlayer = PlayerServiceImpl.getInstance().getPlayerByUserID(apprentice.userID);
                        if(apprentice.isOnline){
                            ResponseMessageQueue.getInstance().put(apprenticePlayer.getMsgQueueIndex(),
                                        new Warning(masterPlayer.getName() + Tip.TIP_MICRO_OF_LEFT_MASTER));
                        }
                        Letter letter = new Letter(Letter.SYSTEM_TYPE,
                        		LetterService.getInstance().getUseableLetterID(), 
                        		Tip.TIP_MICRO_OF_SYSTEM, 
                        		Tip.TIP_MICRO_OF_SYSTEM, 
                        		apprenticePlayer.getUserID(), 
                        		apprenticePlayer.getName(), 
                        		masterPlayer.getName() + Tip.TIP_MICRO_OF_LEFT_MASTER);
                        LetterService.getInstance().addNewLetter(letter);
                        if(apprentice.isOnline)
                            ResponseMessageQueue.getInstance().put(
                                    apprenticePlayer.getMsgQueueIndex(),
                                    new MailStatusChanges(
                                            MailStatusChanges.TYPE_OF_LETTER, true));
                    }
                }

                ResponseMessageQueue.getInstance().put(masterPlayer.getMsgQueueIndex(), new Warning(Tip.TIP_MICRO_OF_DISMISS_ALL));

                masterApprenticeTable.remove(_userID);
                master.dismissAll();

                Letter letter = new Letter(Letter.SYSTEM_TYPE,
                		LetterService.getInstance().getUseableLetterID(), 
                		Tip.TIP_MICRO_OF_SYSTEM, 
                		Tip.TIP_MICRO_OF_SYSTEM,
                		master.masterUserID, masterPlayer.getName(), 
                		Tip.TIP_MICRO_OF_DISMISS_ALL);
                LetterService.getInstance().addNewLetter(letter);

                ResponseMessageQueue.getInstance().put(
                                    masterPlayer.getMsgQueueIndex(),
                                    new MailStatusChanges(
                                            MailStatusChanges.TYPE_OF_LETTER, true));
            }

        }
        log.debug("dismissAll end ...");
    }

    /**
     * 获取师徒关系
     * 
     * @param _userID
     * @return
     */
    public static MasterApprentice get (int _userID)
    {
        return masterApprenticeTable.get(_userID);
    }

    /**
     * 获取玩家不在线的师徒的关系
     * 先从内存里取，如果没有，则从数据库里加载
     * @param _userName
     * @return
     */
    public static MasterApprentice getOffLineMasterApprentice(String _userName){
        MasterApprentice ma = masterApprenticeOffLineList.get(_userName);
        if(ma == null){ //从数据库里加载
            log.debug("从数据库里加载不在线玩家的师徒关系 _username= " + _userName);
            ma = new MasterApprentice();
            ma = TeachDAO.loadMasterApprenticeRelationByName(_userName,ma);
            if(null != ma) {
                masterApprenticeOffLineList.put(_userName, ma);
            }
        }
        return ma;
    }

    /**
     * 验证收徒条件
     * 
     * @param _master
     * @param _apprentice
     */
    public static boolean authenRecruitAppr (HeroPlayer _master,
            HeroPlayer _apprentice)
    {
        if (null != _master && null != _apprentice)
        {
            if (_master.getLevel() < CAN_LEVEL_NOTENOUGH)
            {
                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(),
                        new Warning(Tip.TIP_MICRO_OF_REFUSE_BE_MASTER));

                return false;
            }

            if (_master.getLevel() - _apprentice.getLevel() < LEVEL_DIFFERENCE_NOTENOUGH)
            {
                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(),
                        new Warning(Tip.TIP_MICRO_OF_LEVEL_DIFFERENCE_NOTENOUGH));

                return false;
            }

            if (_master.getClan() != _apprentice.getClan())
            {
                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(),
                        new Warning(Tip.TIP_MICRO_OF_CLAN_DIFFERENT));

                return false;
            }

            MasterApprentice apprenticeRelation = masterApprenticeTable
                    .get(_apprentice.getUserID());

            if (null != apprenticeRelation)
            {
                if (0 < apprenticeRelation.masterUserID)
                {
                    ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(),
                            new Warning(Tip.TIP_MICRO_OF_EXIST_MASTER));

                    return false;
                }
            }

            MasterApprentice masterRelation = masterApprenticeTable.get(_master
                    .getUserID());

            if (null != masterRelation)
            {
                if (MasterApprentice.MAX_APPRENTICER_NUMBER <= masterRelation.apprenticeNumber)
                {
                    ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(),
                            new Warning(Tip.TIP_MICRO_OF_APPRENTICE_FULL));

                    return false;
                }
            }

            return true;
        }

        return false;
    }

    /**
     * 验证拜师条件
     * 
     * @param _operater
     * @param _apprentice
     */
    public static boolean authenFollowMaster (HeroPlayer _apprentice,
            HeroPlayer _master)
    {
        if (null != _master && null != _apprentice)
        {
            
            if(_apprentice.leftMasterTime > Timestamp.valueOf(DEFAULT_LEFT_MASTER_TIME).getTime()){
            	long distance = System.currentTimeMillis() - _apprentice.leftMasterTime;
                if(distance - LAST_LEFT_MASTER_DISTANCE > 0){
                    ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(),
                            new Warning(Tip.TIP_MICRO_LAST_LEFT_MASTER_DISTANCE));
    
                    return false;
                }
            }

            if (_master.getLevel() < CAN_LEVEL_NOTENOUGH)
            {
                ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(),
                        new Warning(Tip.TIP_MICRO_OF_REFUSE_BE_MASTER));

                return false;
            }

            if (_master.getLevel() - _apprentice.getLevel() < LEVEL_DIFFERENCE_NOTENOUGH)
            {
                ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(),
                        new Warning(Tip.TIP_MICRO_OF_LEVEL_DIFFERENCE_NOTENOUGH));

                return false;
            }

            if (_master.getClan() != _apprentice.getClan())
            {
                ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(),
                        new Warning(Tip.TIP_MICRO_OF_CLAN_DIFFERENT));

                return false;
            }

            MasterApprentice masterRelation = masterApprenticeTable.get(_master
                    .getUserID());

            if (null != masterRelation)
            {
                if (MasterApprentice.MAX_APPRENTICER_NUMBER <= masterRelation.apprenticeNumber)
                {
                    ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(),
                            new Warning(Tip.TIP_MICRO_OF_OTHER_APPRENTICE_FULL));

                    return false;
                }
            }

            MasterApprentice apprenticeRelation = masterApprenticeTable
                    .get(_apprentice.getUserID());

            if (null != apprenticeRelation)
            {
                if (0 < apprenticeRelation.masterUserID)
                {
                    ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(),
                            new Warning(Tip.TIP_MICRO_OF_MASTER_ONLY));

                    return false;
                }
            }

            return true;
        }

        return false;
    }

    /**
     * 收纳徒弟
     * 
     * @param _master 师傅
     * @param _apprenticeUserID 徒弟
     */
    public static void recruitApprentice (HeroPlayer _master,
            HeroPlayer _apprentice)
    {
        if (null != _master && null != _apprentice)
        {
            if (_master.getLevel() < CAN_LEVEL_NOTENOUGH)
            {
                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(),
                        new Warning(Tip.TIP_MICRO_OF_REFUSE_BE_MASTER));

                return;
            }

            if (_master.getLevel() - _apprentice.getLevel() < LEVEL_DIFFERENCE_NOTENOUGH)
            {
                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(),
                        new Warning(Tip.TIP_MICRO_OF_LEVEL_DIFFERENCE_NOTENOUGH));

                return;
            }

            if (_master.getClan() != _apprentice.getClan())
            {
                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(),
                        new Warning(Tip.TIP_MICRO_OF_CLAN_DIFFERENT));

                return;
            }

            MasterApprentice apprenticeRelation = masterApprenticeTable
                    .get(_apprentice.getUserID());

            if (null != apprenticeRelation)
            {
                if (0 < apprenticeRelation.masterUserID)
                {
                    ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(),
                            new Warning(Tip.TIP_MICRO_OF_EXIST_MASTER));

                    return;
                }
            }

            MasterApprentice masterRelation = masterApprenticeTable.get(_master
                    .getUserID());

            if (null != masterRelation)
            {
                if (MasterApprentice.MAX_APPRENTICER_NUMBER <= masterRelation.apprenticeNumber)
                {
                    ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(),
                            new Warning(Tip.TIP_MICRO_OF_APPRENTICE_FULL));
                    ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(),
                            new Warning(Tip.TIP_MICRO_OF_OTHER_APPRENTICE_FULL));

                    return;
                }
            }

            if (TeachDAO.insertMasterApprentice(_apprentice.getUserID(),
                    _apprentice.getName(), _master.getUserID(), _master
                            .getName()))
            {
                if (null == masterRelation)
                {
                    masterRelation = new MasterApprentice();
                    masterApprenticeTable.put(_master.getUserID(),
                            masterRelation);
                }

                masterRelation.addNewApprenticer(_apprentice.getUserID(),
                        _apprentice.getName());

                //给师傅加上ID和名称，在界面上显示师傅(包含是自己的情况)
//                masterRelation.masterUserID = _master.getUserID();
//                masterRelation.masterName = _master.getName();

                if (null == apprenticeRelation)
                {
                    apprenticeRelation = new MasterApprentice();
                    masterApprenticeTable.put(_apprentice.getUserID(),
                            apprenticeRelation);
                }

                apprenticeRelation.setMaster(_master.getUserID(), _master
                        .getName(), true);

                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(),
                        new Warning(_apprentice.getName() + Tip.TIP_MICRO_OF_BE_APPR));
                ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(),
                        new Warning(_master.getName() + Tip.TIP_MICRO_OF_BE_MASTER));

//                changeMasterMoneyModulus(_master,masterRelation.getApprenticeOnlineNumber());
            }
        }
    }

    /**
     * 投靠师傅
     * 
     * @param _master 师傅
     * @param _apprenticeUserID 徒弟
     */
    public static void followMaster (HeroPlayer _apprentice, HeroPlayer _master)
    {
        if (null != _apprentice && null != _master)
        {
            if (_master.getLevel() < CAN_LEVEL_NOTENOUGH)
            {
                ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(),
                        new Warning(Tip.TIP_MICRO_OF_REFUSE_BE_MASTER));

                return;
            }

            if (_master.getLevel() - _apprentice.getLevel() < LEVEL_DIFFERENCE_NOTENOUGH)
            {
                ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(),
                        new Warning(Tip.TIP_MICRO_OF_LEVEL_DIFFERENCE_NOTENOUGH));

                return;
            }

            if (_master.getClan() != _apprentice.getClan())
            {
                ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(),
                        new Warning(Tip.TIP_MICRO_OF_CLAN_DIFFERENT));

                return;
            }

            MasterApprentice masterRelation = masterApprenticeTable.get(_master
                    .getUserID());

            if (null != masterRelation)
            {
                if (MasterApprentice.MAX_APPRENTICER_NUMBER <= masterRelation.apprenticeNumber)
                {
                    ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(),
                            new Warning(Tip.TIP_MICRO_OF_OTHER_APPRENTICE_FULL));
                    ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(),
                            new Warning(Tip.TIP_MICRO_OF_APPRENTICE_FULL));

                    return;
                }
            }

            MasterApprentice apprenticeRelation = masterApprenticeTable
                    .get(_apprentice.getUserID());

            if (null != apprenticeRelation)
            {
                if (0 < apprenticeRelation.masterUserID)
                {
                    ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(),
                            new Warning(Tip.TIP_MICRO_OF_MASTER_ONLY));

                    return;
                }
            }

            if (TeachDAO.insertMasterApprentice(_apprentice.getUserID(),
                    _apprentice.getName(), _master.getUserID(), _master
                            .getName()))
            {
                if (null == masterRelation)
                {
                    masterRelation = new MasterApprentice();
                    masterApprenticeTable.put(_master.getUserID(),
                            masterRelation);
                }

                masterRelation.addNewApprenticer(_apprentice.getUserID(),
                        _apprentice.getName());

                //给师傅加上ID和名称，在界面上，如果玩家是师傅，也要显示师傅(包含是自己的情况)
//                masterRelation.masterUserID = _master.getUserID();
//                masterRelation.masterName = _master.getName();

                if (null == apprenticeRelation)
                {
                    apprenticeRelation = new MasterApprentice();
                    masterApprenticeTable.put(_apprentice.getUserID(),
                            apprenticeRelation);
                }

                apprenticeRelation.setMaster(_master.getUserID(), _master
                        .getName(), true);

                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(),
                        new Warning(_apprentice.getName() + Tip.TIP_MICRO_OF_BE_APPR));
                ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(),
                        new Warning(_master.getName() + Tip.TIP_MICRO_OF_BE_MASTER));

//                changeMasterMoneyModulus(_master,masterRelation.getApprenticeOnlineNumber());
            }
        }
    }

    /**
     * 根据师傅的在线徒弟数量，计算在打怪时增加的金钱
     * @param player
     * @return
     */
    public static int getMasterAddMoney(HeroPlayer player){
        int money = 0;
        MasterApprentice masterApprentice = masterApprenticeTable.get(player.getUserID());
        if(masterApprentice != null){
            if(masterApprentice.getApprenticeOnlineNumber()>0){
                for (int i=0; i<masterApprentice.getApprenticeOnlineNumber(); i++){
                    money += MasterApprentice.MONEY_ADD_OF_MASTER;
                }
            }
        }
        return money;
    }

    /**
     * 修改师傅获得金钱的系数
     * @param master
     * @param apprenticeOnlineNumber
     */
    /*private static void changeMasterMoneyModulus(HeroPlayer master,int apprenticeOnlineNumber){
        if(master != null){
            //先把之前加的系数清除，再根据在线徒弟数量加上系数
            master.clearMoneyModulus();
            if(apprenticeOnlineNumber > 0){
                for (int i=0; i<apprenticeOnlineNumber; i++){
                    master.changeMoneyModulus(MasterApprentice.MONEY_MODULUS_OF_MASTER);
                }
            }
        }
    }*/

    /**
     * 授予知识
     * 
     * @param _master 师傅
     * @param _apprentice 徒弟
     */
    public static void teachKnowledge (HeroPlayer _master,
            HeroPlayer _apprentice)
    {
        log.debug("授知识 teachKonwledge ...");
        if (_apprentice == null)
        {
            ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(),
                    new Warning("该徒弟不在线"));

            return;
        }

        MasterApprentice masterRelation = masterApprenticeTable.get(_master
                .getUserID());

        if (null != masterRelation)
        {
            if (_master.where() != _apprentice.where())
            {
                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(),
                        new Warning(Tip.TIP_MICRO_OF_THAT_NOT_TOGETHER));

                return;
            }

            byte authenResult = masterRelation.authenTeachCondition(_apprentice
                    .getUserID(), _apprentice.getLevel());

            if (MasterApprentice.RESULT_OF_TEACH_THAT_HAS_TEACHED == authenResult)
            {
                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(),
                        new Warning(Tip.TIP_MICRO_OF_THAT_HAS_TEACHED));
            }
            else if (MasterApprentice.RESULT_OF_TEACH_THAT_LEVEL_NOTENOUGH == authenResult)
            {
                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(),
                        new Warning(Tip.TIP_MICRO_OF_THAT_LEVEL_NOTENOUGH));
            }
            else if (0 <= authenResult)
            {
                if (0 != TEACH_GOODS_DATA[authenResult][0])
                {
                    if (0 == _master.getInventory().getMaterialBag()
                            .getEmptyGridNumber())
                    {
                        ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(),
                                new Warning(Tip.TIP_MICRO_OF_BAG_IF_FULL));

                        return;
                    }
                }

                byte timesOfTeach = masterRelation.teachKnowledge(_apprentice
                        .getUserID(), _apprentice.getLevel());

                if (TeachDAO.changeMasterApprentice(_apprentice.getUserID(),
                        timesOfTeach, _apprentice.getLevel()))
                {
                    ResponseMessageQueue.getInstance().put(
                            _master.getMsgQueueIndex(),
                            new FullScreenTip(Tip.TIP_MICRO_OF_TEACHED,
                                    TEACH_AWARDS[authenResult]));

                    GoodsServiceImpl.getInstance().addGoods2Package(_master,
                            TEACH_GOODS_DATA[authenResult][0],
                            TEACH_GOODS_DATA[authenResult][1], CauseLog.TEACH);

                    PlayerServiceImpl.getInstance().addMoney(_master,
                            TEACH_MONEY_DATA[authenResult], 1,
                            PlayerServiceImpl.MONEY_DRAW_LOCATION_NONE, "知识授予");

                    ResponseMessageQueue.getInstance().put(
                            _apprentice.getMsgQueueIndex(),
                            new FullScreenTip(Tip.TIP_MICRO_OF_BE_TEACHED,
                                    TEACH_TALK_CONTENT[authenResult]));
                    //del by zhengl; date: 2011-05-03; note:删除废弃的经验值添加代码
//                    PlayerServiceImpl.getInstance().addExperience(_apprentice,
//                            TEACH_EXP_DATA[authenResult], 1,
//                            PlayerServiceImpl.MONEY_DRAW_LOCATION_NONE);
                }
                else
                {
                    ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(),
                            new Warning(Tip.TIP_MICRO_OF_TEACH_FAILER));
                }
            }
            else
            {
                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(),
                        new Warning(Tip.TIP_MICRO_OF_TEACH_FAILER));
            }
        }
    }

    /**
     * @param _master 师傅
     * @param _apprentice 徒弟
     */
    public static boolean leftMaster (HeroPlayer _apprentice)
    {
        log.debug("left master _apprentice="+_apprentice +" , ma table size = " + masterApprenticeTable.size());
        try{
        MasterApprentice apprenticeRelation = masterApprenticeTable
                .get(_apprentice.getUserID());
        log.debug("left master apprenticeRelation = " + apprenticeRelation);
        if (null != apprenticeRelation && 0 != apprenticeRelation.masterUserID)
        {
            String masterName = apprenticeRelation.masterName;
            int masterUserID = apprenticeRelation.masterUserID;
            log.debug("left master name = " + apprenticeRelation.masterName);
            MasterApprentice masterRelation = masterApprenticeTable
                    .get(apprenticeRelation.masterUserID);

            apprenticeRelation.leftMaster();

            if (!apprenticeRelation.isValidate())
            {
                log.debug("masterApprenticeTable remove apprentice ...");
                masterApprenticeTable.remove(_apprentice.getUserID());
            }

            TeachDAO.deleteMasterApprentice(_apprentice.getUserID());

            PlayerServiceImpl.getInstance().updateLeftMasterTime(_apprentice);

            ResponseMessageQueue.getInstance().put(
                    _apprentice.getMsgQueueIndex(),
                    new Warning(Tip.TIP_MICRO_OF_REDUCE_APPRENTICE_HEADER + masterName
                            + Tip.TIP_MICRO_OF_REDUCE_APPRENTICE_ENDER));

            HeroPlayer master = PlayerServiceImpl.getInstance()
                    .getPlayerByUserID(masterUserID);

            if (null != masterRelation
                    && null != masterRelation.removeApprenticer(_apprentice
                            .getUserID()))
            {
                if (!apprenticeRelation.isValidate())
                {
                    log.debug("masterApprenticeTable remove master ...");
                    masterApprenticeTable
                            .remove(apprenticeRelation.masterUserID);
                }
                if(master != null){
                    masterRelation.addApprenticeOnlineNumber(false);
//                    changeMasterMoneyModulus(master,masterRelation.getApprenticeOnlineNumber());

                }
            }

            Letter letter = new Letter(Letter.SYSTEM_TYPE,LetterService.getInstance()
                    .getUseableLetterID(), Tip.TIP_MICRO_OF_SYSTEM, Tip.TIP_MICRO_OF_SYSTEM,
                    masterUserID, masterName, _apprentice.getName()
                            + Tip.TIP_MICRO_OF_LEFT_MASTER);
            LetterService.getInstance().addNewLetter(letter);



            if (null != master && master.isEnable())
            {
                ResponseMessageQueue.getInstance()
                        .put(
                                master.getMsgQueueIndex(),
                                new Warning(_apprentice.getName()
                                        + Tip.TIP_MICRO_OF_LEFT_MASTER));

                ResponseMessageQueue.getInstance().put(
                        master.getMsgQueueIndex(),
                        new MailStatusChanges(MailStatusChanges.TYPE_OF_LETTER,
                                true));
            }

            return true;
        }
        }catch (Exception e){
            log.error("left master error  : ",e);
        }

        return false;
    }

    /**
     * 遗弃徒弟
     * 
     * @param _master 师傅
     * @param _apprenticeUserID 徒弟编号
     */
    public static boolean reduceApprentice (HeroPlayer _master,
            int _apprenticeUserID)
    {
        MasterApprentice masterRelation = masterApprenticeTable.get(_master
                .getUserID());

        if (null != masterRelation)
        {
            String apprenticeName = masterRelation
                    .removeApprenticer(_apprenticeUserID);
            log.debug("remove apprenticer = " + apprenticeName);
            if (null != apprenticeName)
            {
                if (!masterRelation.isValidate())
                {
                    masterApprenticeTable.remove(_master.getUserID());
                }

                TeachDAO.deleteMasterApprentice(_apprenticeUserID);

                ResponseMessageQueue.getInstance().put(
                        _master.getMsgQueueIndex(),
                        new Warning(Tip.TIP_MICRO_OF_REDUCE_APPRENTICE_HEADER
                                + apprenticeName
                                + Tip.TIP_MICRO_OF_REDUCE_APPRENTICE_ENDER));

                MasterApprentice apprenticeRelation = masterApprenticeTable
                        .get(_apprenticeUserID);

                if (null != apprenticeRelation
                        && apprenticeRelation.masterUserID == _master
                                .getUserID())
                {
                    apprenticeRelation.leftMaster();

                    HeroPlayer apprentor = PlayerServiceImpl.getInstance().getPlayerByUserID(_apprenticeUserID);
                    if(apprentor != null){//遗弃不在线玩家，在线徒弟数量不减少
                        masterRelation.changeApprenticeStatus(_apprenticeUserID, false);
                    }

                    if (!apprenticeRelation.isValidate())
                    {
                        masterApprenticeTable.remove(_apprenticeUserID);
                    }

//                    changeMasterMoneyModulus(_master,masterRelation.getApprenticeOnlineNumber());

                }

                Letter letter = new Letter(Letter.SYSTEM_TYPE, LetterService.getInstance()
                        .getUseableLetterID(), Tip.TIP_MICRO_OF_SYSTEM, Tip.TIP_MICRO_OF_SYSTEM,
                        _apprenticeUserID, apprenticeName, _master.getName()
                                + Tip.TIP_MICRO_OF_LEFT_MASTER);
                LetterService.getInstance().addNewLetter(letter);

                HeroPlayer apprentice = PlayerServiceImpl.getInstance()
                        .getPlayerByUserID(_apprenticeUserID);

                if (null != apprentice && apprentice.isEnable())
                {
                    ResponseMessageQueue.getInstance()
                            .put(
                                    apprentice.getMsgQueueIndex(),
                                    new Warning(_master.getName()
                                            + Tip.TIP_MICRO_OF_LEFT_MASTER));

                    ResponseMessageQueue.getInstance().put(
                            apprentice.getMsgQueueIndex(),
                            new MailStatusChanges(
                                    MailStatusChanges.TYPE_OF_LETTER, true));
                }

                return true;
            }
        }

        return false;
    }

    /**
     * 出师
     * 新策划案取消出师 2011-01-16(jiaodongjie)
     * @param _apprentice 徒弟
     */
    public static void finishedStudy (HeroPlayer _apprentice)
    {
        MasterApprentice apprenticeRelation = masterApprenticeTable
                .get(_apprentice.getUserID());

        if (null != apprenticeRelation && 0 != apprenticeRelation.masterUserID)
        {
            int masterLevel;

            HeroPlayer master = PlayerServiceImpl.getInstance()
                    .getPlayerByUserID(apprenticeRelation.masterUserID);

            if (null != master)
            {
                masterLevel = master.getLevel();
            }
            else
            {
                masterLevel = PlayerDAO
                        .getRoleLevel(apprenticeRelation.masterUserID);
            }

//            if (_apprentice.getLevel() >= masterLevel)
            if(_apprentice.getLevel() >= FINISHE_STUDY_LEVEL) //达到 FINISHE_STUDY_LEVEL 级强制出师
            {
                String masterName = apprenticeRelation.masterName;
                apprenticeRelation.leftMaster();

                if (!apprenticeRelation.isValidate())
                {
                    masterApprenticeTable.remove(_apprentice.getUserID());
                }

                MasterApprentice masterRelation = masterApprenticeTable
                        .get(apprenticeRelation.masterUserID);

                if (null != masterRelation
                        && null != masterRelation.removeApprenticer(_apprentice
                                .getUserID()))
                {
                    if (!apprenticeRelation.isValidate())
                    {
                        masterApprenticeTable
                                .remove(apprenticeRelation.masterUserID);
                    }
                }

                TeachDAO.deleteMasterApprentice(_apprentice.getUserID());

                ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(),
                        new Warning(Tip.TIP_MICRO_OF_APRENTICE_FINISH_STUDY));

                Letter letter = new Letter(Letter.SYSTEM_TYPE, LetterService.getInstance()
                        .getUseableLetterID(), Tip.TIP_MICRO_OF_SYSTEM, Tip.TIP_MICRO_OF_SYSTEM,
                        apprenticeRelation.masterUserID, masterName,
                        _apprentice.getName() + Tip.TIP_MICRO_OF_APRENTICE_FINISH_STUDY);
                LetterService.getInstance().addNewLetter(letter);
                if (null != master && master.isEnable())
                {
                    ResponseMessageQueue.getInstance().put(
                            master.getMsgQueueIndex(),
                            new Warning(_apprentice.getName()
                                    + Tip.TIP_MICRO_OF_APRENTICE_FINISH_STUDY));

                    ResponseMessageQueue.getInstance().put(
                            master.getMsgQueueIndex(),
                            new MailStatusChanges(
                                    MailStatusChanges.TYPE_OF_LETTER, true));
                }
            }
        }
    }

    /**
     * 进入队伍
     * 
     * @param _apprentice 徒弟
     */
    public static void enterTeam (HeroPlayer _apprentice, Group _group)
    {
        MasterApprentice apprenticeRelation = masterApprenticeTable
                .get(_apprentice.getUserID());

        if (null != apprenticeRelation && 0 != apprenticeRelation.masterUserID
                && apprenticeRelation.masterIsOnline)
        {
            if (_apprentice.getGroupID() == PlayerServiceImpl.getInstance()
                    .getPlayerByUserID(apprenticeRelation.masterUserID)
                    .getGroupID())
            {
                _apprentice
                        .changeExperienceModulus(-MasterApprentice.EXP_MODULUS_WHEN_MASTER_ONLINE);
                _apprentice
                        .changeExperienceModulus(MasterApprentice.EXP_MODULUS_WHEN_MASTER_IN_TEAM);
            }
        }
    }

    /**
     * @param _userID
     */
    public static void delteRole (int _userID)
    {
        TeachDAO.deleteAll(_userID);

        MasterApprentice masterApprentice = masterApprenticeTable
                .remove(_userID);

        if (null != masterApprentice)
        {
            MasterApprentice relation;

            if (masterApprentice.apprenticeNumber > 0)
            {
                for (int i = 0; i < masterApprentice.apprenticeNumber; i++)
                {
                    relation = masterApprenticeTable
                            .get(masterApprentice.apprenticeList[i].userID);

                    if (null != relation)
                    {
                        relation.leftMaster();
                    }
                }
            }


            if (masterApprentice.masterUserID > 0)
            {
                relation = masterApprenticeTable
                        .get(masterApprentice.masterUserID);

                if (null != relation)
                {
                    relation.removeApprenticer(_userID);
                    relation.addApprenticeOnlineNumber(false);

                    HeroPlayer master = PlayerServiceImpl.getInstance().getPlayerByUserID(masterApprentice.masterUserID);
//                    changeMasterMoneyModulus(master,relation.getApprenticeOnlineNumber());
                }
            }
        }

    }

    /**
     * 等待对方回复，如果20秒没回复，则提示对方忙
     * @param askeder 被询问的玩家
     */
    public static void waitingReply(HeroPlayer askeder){
        askeder.waitingTimer = new Timer(); //初始化等待计时器
        askeder.waitingTimer.schedule(new WaitingResponse(askeder,waintingtime,true),0,1000);
        askeder.waitingTimerRunning = true;
    }

    /**
     * 如果玩家回复了，则取消等待计时
     * @param replyer
     */
    public static void cancelWaitingTimer(HeroPlayer replyer){
        if(replyer.waitingTimerRunning){
            replyer.waitingTimer.cancel();
            replyer.waitingTimerRunning = false;
            replyer.waitingTimer = null;
        }
    }

    /**
     * 等待回复的时间
     */
    private static final int waintingtime = 20;

    /**
     * 出师等级，达到此等级系统强制出师
     */
    private static final int   FINISHE_STUDY_LEVEL      = 40;

    /**
     * 可以收徒的等级
     */
    private static final int CAN_LEVEL_NOTENOUGH = 30;

    /**
     * 与上次脱离师傅间隔天数
     * 大于此数才能再次拜师
     * 用 long 型是考虑有可能比上次脱离时间多几分或几个小时，也可以
     */
    private static final long LAST_LEFT_MASTER_DISTANCE = 3*60*60*24L;
    
    /**
     * 数据库默认的离开师傅时间
     * 拜师时，如果玩家的上次离开师傅时间是这个值，则代表玩家没有拜过师傅
     */
    private static final String DEFAULT_LEFT_MASTER_TIME = "2011-01-01 00:00:00";

    /**
     * 相差10级才可以成为师徒
     */
    private static final int LEVEL_DIFFERENCE_NOTENOUGH = 10;


    /**
     * 知识授予时，徒弟获得的经验，共4次
     */
    private static final int[]    TEACH_EXP_DATA                    = {
            0, 2000, 4000, 8000                                    };

    /**
     * 知识授予时，师傅获得的金钱，共4次
     */
    private static final int[]    TEACH_MONEY_DATA                  = {
            0, 20000, 40000, 80000                                 };

    /**
     * 知识授予时，师傅获得的物品
     */
    private static final int[][]  TEACH_GOODS_DATA                  = {
            {0, 1 }, {0, 0 }, {551, 2 }, {551, 3 }                };

    /**
     * 授予知识时，徒弟看到的全屏提示内容
     */
    private static final String[] TEACH_TALK_CONTENT                = {
            "第一次被现在的师傅授予知识。\n\n虽然身在乱世，但我们不能只懂得打打杀杀，生活技巧的掌握对我们同样重要，在各个城市中都有采集和制作训练师，他们会让我们习得丰富的生活技巧。\n\n如果被师傅多次授予知识，可以得到额外的奖励，每十级可以被师傅授予一次知识。",
            "第二次被现在的师傅授予知识。\n\n在那些魔物凶猛的区域，你会体会到团结的力量，分工明确的小队可以让我们战无不胜，这个队伍中需要一个防御者，一个治愈者和三个善于攻击的家伙。\n\n获得了经验：2000",
            "第三次被现在的师傅授予知识。\n\n徒弟，看着你逐渐成长起来为师非常高兴，可能你正在彷徨觉得经验的提升越发的困难，但你要记住坚持才能胜利，不要放过每一个任务，它们能促进你的成长。\n\n获得了经验：4000",
            "第四次被现在的师傅授予知识。\n\n可能你已经注意到了，在进入那些凶猛怪区的时候有一个“困难”的选择，那是因为里面的怪物异常强大，我们需要在五十级的时候才能与之抗衡，而作为奖励我们可以从它们身上得到圣器，对，就是那些天神使用过的兵器。\n\n获得了经验：8000" };

    /**
     * 授予知识时，师傅看到的全屏提示内容
     */
    private static final String[] TEACH_AWARDS                      = {
            "第一次授予该徒弟知识。\n\n维持与徒弟的关系，多次进行知识授予会得到额外的奖励。\n\n徒弟每十级可以接受一次知识授予。\n\n获取物品：上古印记 x 1",
            "第二次授予该徒弟知识。\n\n获得金钱：20000",
            "第三次授予该徒弟知识。\n\n获得金钱：40000\n获得物品：上古印记 x 2",
            "第四次授予该徒弟知识。\n\n获得金钱：80000\n获得物品：上古印记 x 3"            };
}
