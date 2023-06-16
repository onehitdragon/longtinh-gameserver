package hero.micro.teach;

import org.apache.log4j.Logger;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 MasterApprentice.java
 * @创建者 DingChu
 * @版本 1
 * @时间 2009-11-25 下午03:15:36
 * @描述 ：师徒
 */

public class MasterApprentice
{
    private static Logger log = Logger.getLogger(MasterApprentice.class);
    /**
     * 师傅角色编号
     */
    public int              masterUserID;

    /**
     * 师傅名称
     */
    public String           masterName;

    /**
     * 师傅是否在线
     */
    public boolean          masterIsOnline;

    /**
     * 徒弟列表
     */
    public ApprenticeInfo[] apprenticeList;

    /**
     * 徒弟数量
     */
    public int              apprenticeNumber;

    /**
     * 在线徒弟数量
     */
    private int  apprenticeOnlineNumber;

    public void addApprenticeOnlineNumber(boolean add){
        if(add){
            apprenticeOnlineNumber++;
        }else {
            apprenticeOnlineNumber--;
        }

        if(apprenticeOnlineNumber > apprenticeNumber){
            apprenticeOnlineNumber = apprenticeNumber;
        }
        if (apprenticeOnlineNumber < 0){
            apprenticeOnlineNumber = 0;
        }
    }

    public int getApprenticeOnlineNumber(){
        return apprenticeOnlineNumber;
    }

    /**
     * 解散所有徒弟
     */
    public void dismissAll(){
        apprenticeList = null;
        masterName = null;
        apprenticeNumber = 0;
        masterUserID = 0;
        apprenticeOnlineNumber = 0;
    }

    /**
     * 改变徒弟在线状态
     * 
     * @param _apprenticeUserID
     * @param _isOnline
     */
    public void changeApprenticeStatus (int _apprenticeUserID, boolean _isOnline)
    {
        if (null != apprenticeList && apprenticeList.length > 0)
        {
            for (ApprenticeInfo apprenticeInfo : apprenticeList)
            {
                if (null != apprenticeInfo)
                {
                    if (apprenticeInfo.userID == _apprenticeUserID)
                    {
                        apprenticeInfo.isOnline = _isOnline;

                        if(_isOnline){
                            if(apprenticeOnlineNumber < MAX_APPRENTICER_NUMBER)
                                addApprenticeOnlineNumber(true);
                        }else{
                            addApprenticeOnlineNumber(false);
                        }

                        return;
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
     * 设置师傅
     * 
     * @param _masterUserID 师傅编号
     * @param _name 师傅名字
     */
    public void setMaster (int _masterUserID, String _name, boolean _isOnline)
    {
        masterUserID = _masterUserID;
        masterName = _name;
        masterIsOnline = _isOnline;
    }

    /**
     * 离开师傅
     */
    public void leftMaster ()
    {
        masterUserID = 0;
        masterName = null;
        masterIsOnline = false;
    }

    /**
     * 添加新徒弟
     * 
     * @param _userID
     * @return 是否添加成功（当已在徒弟列表中将失败）
     */
    public synchronized boolean addNewApprenticer (int _userID, String _name)
    {
        if (null == apprenticeList)
        {
            apprenticeList = new ApprenticeInfo[MAX_APPRENTICER_NUMBER];
            apprenticeList[0] = new ApprenticeInfo(_userID, _name);
            apprenticeList[0].isOnline = true;

            apprenticeNumber++;
            if(apprenticeOnlineNumber < MAX_APPRENTICER_NUMBER)
                 addApprenticeOnlineNumber(true);

            return true;
        }

        for (int i = 0; i < MAX_APPRENTICER_NUMBER; i++)
        {
            if (null == apprenticeList[i])
            {
                apprenticeList[i] = new ApprenticeInfo(_userID, _name);
                apprenticeNumber++;
                apprenticeList[i].isOnline = true;
                if(apprenticeOnlineNumber < MAX_APPRENTICER_NUMBER)
                    addApprenticeOnlineNumber(true);

                return true;
            }
            else
            {
                if (apprenticeList[i].userID == _userID)
                {
                    return false;
                }
            }
        }

        return false;
    }

    /**
     * 添加从数据库加载的徒弟
     * 
     * @param _userID 玩家userID
     * @param _teachTimes 被授予知识的次数
     * @return 是否添加成功（当已在徒弟列表中将失败）
     */
    public synchronized boolean addNewApprenticer (int _userID, String _name,
            byte _teachTimes, short _levelOfLastTeach)
    {
        if (null == apprenticeList)
        {
            apprenticeList = new ApprenticeInfo[MAX_APPRENTICER_NUMBER];
            apprenticeList[0] = new ApprenticeInfo(_userID, _name, _teachTimes,
                    _levelOfLastTeach);

            apprenticeNumber++;

            return true;
        }

        for (int i = 0; i < MAX_APPRENTICER_NUMBER; i++)
        {
            if (null == apprenticeList[i])
            {
                apprenticeList[i] = new ApprenticeInfo(_userID, _name,
                        _teachTimes, _levelOfLastTeach);
                apprenticeNumber++;

                return true;
            }
        }

        return false;
    }

    /**
     * 遗弃徒弟
     * 
     * @param _apprenticeUserID 徒弟userID
     * @return 是否遗弃成功（当不在徒弟列表中将失败）
     */
    public synchronized String removeApprenticer (int _apprenticeUserID)
    {
        if (null != apprenticeList)
        {
            for (int i = 0; i < apprenticeNumber; i++)
            {
                log.debug("apprenticeList["+i+"].userID = " + apprenticeList[i].userID);
                if (null != apprenticeList[i]
                        && apprenticeList[i].userID == _apprenticeUserID)
                {
                    String apprenticeName = apprenticeList[i].name;

                    if(apprenticeList[i].isOnline)
                        addApprenticeOnlineNumber(false);

                    log.debug("apprenticeList["+i+"].name = " + apprenticeList[i].name);
                    if (apprenticeNumber > 1)
                    {
                        System.arraycopy(apprenticeList, i + 1, apprenticeList,
                                i, apprenticeNumber - i - 1);
                    }

                    apprenticeList[--apprenticeNumber] = null;


                    return apprenticeName;
                }
            }
        }

        return null;
    }

    /**
     * 是否有效，当没有师傅、没有徒弟时释放
     * 
     * @return
     */
    public boolean isValidate ()
    {
        if (0 == apprenticeNumber && 0 == masterUserID)
        {
            return false;
        }

        return true;
    }

    /**
     * 授予知识
     * 
     * @param _apprenticeUserID 徒弟userID
     */
    public synchronized byte teachKnowledge (int _apprenticeUserID,
            short _apprenticeLevel)
    {
        if (null != apprenticeList)
        {
            for (int i = 0; i < apprenticeNumber; i++)
            {
                if (null != apprenticeList[i]
                        && apprenticeList[i].userID == _apprenticeUserID)
                {
                    if (_apprenticeLevel >= 10)
                    {
                        if (_apprenticeLevel / 10 != apprenticeList[i].levelOfLastAccepted / 10)
                        {
                            apprenticeList[i].acceptedTimesThatTeach++;
                            apprenticeList[i].levelOfLastAccepted = _apprenticeLevel;

                            return apprenticeList[i].acceptedTimesThatTeach;
                        }
                        else
                        {
                            return RESULT_OF_TEACH_THAT_HAS_TEACHED;
                        }
                    }
                    else
                    {
                        return RESULT_OF_TEACH_THAT_LEVEL_NOTENOUGH;
                    }
                }
            }
        }

        return -3;
    }

    /**
     * 验证授予知识条件
     * 
     * @param _apprenticeUserID 徒弟userID
     */
    public byte authenTeachCondition (int _apprenticeUserID,
            short _apprenticeLevel)
    {
        if (null != apprenticeList)
        {
            for (int i = 0; i < apprenticeNumber; i++)
            {
                if (null != apprenticeList[i]
                        && apprenticeList[i].userID == _apprenticeUserID)
                {
                    if (_apprenticeLevel >= 10)
                    {
                        if (_apprenticeLevel / 10 == apprenticeList[i].levelOfLastAccepted / 10)
                        {
                            return RESULT_OF_TEACH_THAT_HAS_TEACHED;
                        }
                        else
                        {
                            return apprenticeList[i].acceptedTimesThatTeach;
                        }
                    }
                    else
                    {
                        return RESULT_OF_TEACH_THAT_LEVEL_NOTENOUGH;
                    }
                }
            }
        }

        return -3;
    }

    public class ApprenticeInfo
    {
        /**
         * 构造
         * 
         * @param _userID
         * @param _name
         */
        public ApprenticeInfo(int _userID, String _name)
        {
            userID = _userID;
            name = _name;
        }

        /**
         * 构造
         * 
         * @param _userID
         * @param _name
         */
        public ApprenticeInfo(int _userID, String _name,
                byte _acceptedTimesThatTeach, short _levelOfLastAccepted)
        {
            userID = _userID;
            name = _name;
            acceptedTimesThatTeach = _acceptedTimesThatTeach;
            levelOfLastAccepted = _levelOfLastAccepted;
        }

        /**
         * 编号
         */
        public int     userID;

        /**
         * 名称
         */
        public String  name;

        /**
         * 是否在线
         */
        public boolean isOnline;

        /**
         * 接受知识授予的次数
         */
        public byte    acceptedTimesThatTeach;

        /**
         * 最后 一次接受知识授予时的等级
         */
        public short   levelOfLastAccepted;
    }

    /**
     * 关系类型-师傅
     */
    public static final byte  RELATION_TYPE_OF_MASTER              = 1;

    /**
     * 关系类型-徒弟
     */
    public static final byte  RELATION_TYPE_OF_APPRENTICE          = 2;

    /**
     * 最多徒弟数量
     */
    public static final byte  MAX_APPRENTICER_NUMBER               = 5;

    /**
     * 不能授予知识原因－－等级不足10级
     */
    public static final byte  RESULT_OF_TEACH_THAT_LEVEL_NOTENOUGH = -1;

    /**
     * 不能授予知识原因－－当前等级区间已经授予过知识了
     */
    public static final byte  RESULT_OF_TEACH_THAT_HAS_TEACHED     = -2;

    /**
     * 徒弟在线时师傅打怪获得金钱提高比例
     * 当一个徒弟在线时 0.02f
     * 两个在线时 0.04f
     * 三个在线时 0.06f
     * 四个在线时 0.08f
     * 五个在线时 0.1f
     */
//    public static final float[] MONEY_MODULUS_OF_MASTER              = {0.02F,0.04F,0.06F,0.08F,0.1F};
    /**
     * 经过和策划商量，徒弟在线时师傅打怪获得金钱提高比例
     * 每增加一个在线徒弟增加金钱 1
     * by jiaodj 2011-05-13
      */
//    public static final float MONEY_MODULUS_OF_MASTER   =   0.1f;
    public static final int MONEY_ADD_OF_MASTER  = 1;

    /**
     * 师傅在线时徒弟打怪获得经验提高比例
     */
    public static final float EXP_MODULUS_WHEN_MASTER_ONLINE       = 0.02F;

    /**
     * 和师傅在同一队伍时徒弟打怪获得经验提高比例
     */
    public static final float EXP_MODULUS_WHEN_MASTER_IN_TEAM      = 0.1F;
}
