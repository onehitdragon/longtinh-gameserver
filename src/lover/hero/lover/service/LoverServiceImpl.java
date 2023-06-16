package hero.lover.service;

import hero.item.bag.exception.BagException;
import hero.lover.message.ResponseMarryRelationShow;
import hero.map.Map;
import hero.map.service.MapServiceImpl;
import hero.pet.PetList;
import hero.pet.message.ResponseWearPetGridNumber;
import hero.pet.service.PetDAO;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

import javolution.util.FastMap;
import org.apache.log4j.Logger;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.AbsServiceAdaptor;
import yoyo.service.base.session.Session;

/**
 * 结婚服务
 * 
 * @author Luke Chen
 * @copyright Luke Chen
 * @date Jun 17, 2009
 */
public class LoverServiceImpl extends AbsServiceAdaptor<LoverServerConfig>
{
    private static Logger log = Logger.getLogger(LoverServiceImpl.class);
    private static LoverServiceImpl instance = null;

    /**
     * 构造互斥锁
     */
    private static Object           lock     = new Object();

    /**
     * 注册互斥锁
     */
    private static Object           lock1    = new Object();

    /**
     * 计时器
     */
    private Timer                   timer    = new Timer();

    private FastMap<Integer,Timer> removeAllPlayerOutMarryMapTimerMap = new FastMap<Integer, Timer>();

    /**
     * 是否可以结婚
     */
    protected boolean               canMarry = true;

    /**
     * 恋爱状态
     * 
     * @author Luke 陈路
     * @date Jul 30, 2009
     */
    public enum LoverStatus
    {
        /**
         * 没有关系
         */
        NONE,
        /**
         * 登记成功
         */
        REGISTER,
        /**
         * 订婚成功
         */
        SUCCESS,
        /**
         * 已经登记过了
         */
        REGISTERED,
        /**
         * 我已经订过婚
         */
        ME_SUCCESSED,
        /**
         * 对象已经订过婚
         */
        THEM_SUCCESSED,
    }

    /**
     * 结婚状态
     */
    public enum MarryStatus
    {
        /**
         *
         */
        NOT_LOVER(0),
        /**
         * 时间不够
         */
        NO_TIME(1),

        /**
         * 恋人关系
         */
        LOVED_SUCCESS(2),
        /**
         * 成功结婚
         */
        SUCCESS(3),

        /**
         * 分手
         */
        BREAK_UP(4),

        /**
         * 离婚成功
         */
        DIVORCE_SUCCESS(5),

         /**
         * 已离婚
         * 不保存到数据库
         */
        DIVORCED(6),

        /**
         * 已有恋人，但没结婚
         * 不保存到数据库
         */
        LOVED_NO_MARRY(7),
        /**
         * 已结婚，不能再结
         * 不保存到数据库
         */
        MARRIED(8);

        private int status;
        MarryStatus(int _status) {
            this.status = _status;
        }

        public int getStatus(){
            return status;
        }
    }

    public static LoverServiceImpl getInstance ()
    {
        synchronized (lock)
        {
            if (instance == null)
                instance = new LoverServiceImpl();
            return instance;
        }
    }

    public LoverServiceImpl()
    {
        LoverDAO.deleteTimeOut();
    }

    /**
     * 恋人
     * @param _uid1 登记双方的名字
     * @param _uid2
     * @return
     */
    public MarryStatus registerLover(String _name1, String _name2)
    {
        MarryStatus status = LoverDAO.propose(_name1, _name2);
        return status;
    }

    /**
     * 登记到大榕树
     *
     * @param _uid1 登记双方的名字
     * @param _uid2
     * @return
     */
    public LoverStatus registerLoverTree (String _name1, String _name2)
    {
        /*LoverStatus status = LoverDAO.registerLover(_name1, _name2);
        // log.info("status ================= "+status);
        return status;*/
        return null;
    }

    /**
     * 查找未婚妻、夫
     * @param name
     * @return
     */
    public String whoLoveMe(String name){
        return LoverDAO.whoLoveMe(name);
    }

    /**
     * 查找妻子或丈夫
     * @param name
     * @return
     */
    public String whoMarriedMe(String name){
        return LoverDAO.whoMarriedMe(name);
    }

    /**
     * 显示伴侣关系界面
     * @param player
     * @throws BagException 
     */
    public void showMarryRelation(HeroPlayer player) throws BagException{
        byte relation = 0;  // 0:无伴侣  1:有恋人关系  2:有夫妻关系
        String othername = whoLoveMe(player.getName());
        if(null == othername){
            othername = whoMarriedMe(player.getName());
            if(null != othername){
                relation = 2;
            }
        }else {
            relation = 1;
        }
        if(relation>0){
            HeroPlayer otherPlayer = PlayerServiceImpl.getInstance().getPlayerByName(othername);
            if(otherPlayer == null){ //对方不在线
                otherPlayer = PlayerServiceImpl.getInstance().getOffLinePlayerInfoByName(othername);
                otherPlayer.loverLever = player.loverLever;   // 对方的婚姻等级、爱情值和player一样
                otherPlayer.setLoverValue(player.getLoverValue());
                
//                PetList petList = PetDAO.load(otherPlayer.getUserID());
//                if(petList.getViewPet().size() > 0){
//                	otherPlayer.getBodyWearPetList().init(petList.getViewPet());
//    			}
            }
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new ResponseMarryRelationShow(relation,otherPlayer));
//            OutMsgQ.getInstance().put(player.getMsgQueueIndex(), new ResponseWearPetGridNumber(otherPlayer.getBodyWearPetList()));
        }else{
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new ResponseMarryRelationShow(relation,null));
        }
    }

    /**
     * 通用的修改状态方法
     * @param userName
     * @param otherName
     * @param status 状态
     */
    public void updateMarryStatus(String userName, String otherName, MarryStatus status){
        LoverDAO.updateMarryStatus(userName,otherName,status);
    }

    /**
     * 注册结婚
     * 
     * @param _uid1 注册双方的名字
     * @param _uid2
     * @return
     */
    public MarryStatus registerMarriage (String _uid1, String _uid2, short clanID)
    {
        MarryStatus status = MarryStatus.NO_TIME;

        synchronized (lock1)
        {
            if (canMarry)
            {
                status = LoverDAO.registerMarriage(_uid1, _uid2);
                if (status == MarryStatus.SUCCESS)
                {
                    HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByName(_uid1);
                    canMarry = false;
                    Timer ptimer = new Timer();
                    ptimer.schedule(new MarriageTask(clanID), 2 * 3600L);
                    removeAllPlayerOutMarryMapTimerMap.put(player.getUserID(),ptimer);
                }
            }
        }
        return status;
    }

    public Timer getRemoveAllPlayerOutMarryMapTimer(int userID){
        return removeAllPlayerOutMarryMapTimerMap.get(userID);
    }

    /**
     * 离婚
     * @param _name1
     * @param _name2
     */
    public MarryStatus divorce(String _name){
        MarryStatus status;

        synchronized (lock1)
        {
            status = LoverDAO.divorceMarriage(_name);
            if (status == MarryStatus.NOT_LOVER)
            {
                canMarry = true;
            }
        }
        return status;
    }

    /**
     * 是否有其它玩家结婚
     * 查出来此小时内结婚的玩家，只要看婚礼礼堂地图里是否有玩家就可以了，
     * 因为婚礼礼堂只开放一个小时，到时间后会强制所有玩家退出，而且在时间内如果结婚的玩家退出，则其它玩家也退出，同是也不能进入礼堂
     * 如果已退出礼堂地图，则 canMarry=true ，现在没有玩家结婚；
     * 如果未退出礼堂地图，则 canMarry=false，现在有玩家正在结婚，其它玩家不能结婚
     * @param mapID 婚礼地图ID，固定的
     * @return 如果没有其它玩家结婚 return true,反之,return false;
     */
    public boolean noHadOtherMarry(short mapID){
        Map map = MapServiceImpl.getInstance().getNormalMapByID(mapID);
        log.debug("loverservice had other marry map = " + map);
        if(null == map){
            canMarry = false;
            return false;
        }
        if(map.getPlayerList() != null && map.getPlayerList().size()>0){
            canMarry = false;
            return false;
        }
        canMarry = true;
        return true;
    }

    /**
     * 结婚进入礼堂失败
     * @param _name1
     * @param _name2
     */
    public void marryFaild(String _name1, String _name2){
        LoverDAO.marryFaild(_name1,_name2);
        timer.cancel();
    }

    /**
     * 某玩家是否与目标群体中的某一个结婚
     * 
     * @param _src 某玩家
     * @param _player 目标群体
     * @return
     */
    public String[] anotherInTream (String _srcName,
            ArrayList<HeroPlayer> _player)
    {
        return LoverDAO.hasMarried(_srcName, _player);
    }

    @Override
    protected void start ()
    {
        // 每天凌晨4点执行
        Date tomorrow = new Date();
        tomorrow.setTime(tomorrow.getTime() + 1000L * 60L * 60L * 24L);
        tomorrow.setHours(4);
        timer.schedule(new LoverTask(), tomorrow);
    }
    
    /**
     * 获得明天的现在的时分秒
     * 
     * @return
     */
    public Date getTomorrow ()
    {
        Date tomorrow = new Date();
        tomorrow.setTime(new Date().getTime() + 1000L * 60L * 60L * 24L);
        return tomorrow;
    }

    /**
     * 获得计时器
     * 
     * @return
     */
    public Timer getTimer ()
    {
        return timer;
    }

    /**
     * 婚姻升级
     * @param player
     */
    public void loverUpgrade(HeroPlayer player){
        LoverLevel currLevel = player.loverLever;
        LoverLevel level = getLoverLevel(player.getLoverValue());
        if(level.getLevel() - currLevel.getLevel() == 1){
            player.loverLever = level;
            player.changeExperienceModulus(EXP_MODULE[level.getLevel()-1]);
        }
    }
    
    
    /**
     * 根据玩家爱情度，获取婚姻等级
     * @param loverValue
     * @return
     */
    public LoverLevel getLoverLevel(int loverValue){
        LoverLevel loverLevel = LoverLevel.ZHI;
        if(loverValue >= 40000){
            loverLevel = LoverLevel.ZUANSHI;
        }else if(loverValue >= 27000){
            loverLevel = LoverLevel.JIN;
        }else if(loverValue >= 25000){
            loverLevel = LoverLevel.YIN;
        }else if(loverValue >= 15000){
            loverLevel = LoverLevel.TONG;
        }else if(loverValue >= 8000){
            loverLevel = LoverLevel.TIE;
        }
        return loverLevel;
    }

    private static final float[] EXP_MODULE = {0.01f,0.02f,0.03f,0.04f,0.05f,0.06f};

    /**
     * 恋人时爱情度的最大值，即结婚时最小值
     */
    public static final int LOVER_MAX_VALUE = 3000;
    /**
     * 恋人或夫妻两人双方同时在线时，每过一分钟获得到的爱情度值
     */
    public static final int ADD_LOVER_VALUE_FOR_PER_MINUTE = 1;
    /**
     * 仅恋人或夫妻两人组队练级时，每当有人升级，得到的爱情度值
     */
    public static final int ADD_LOVER_VALUE_FOR_UPGRADE = 500;
    /**
     * 使用鲜花时增加的爱情值
     */
    public static final int ADD_LOVER_VALUE_FOR_FLOWER = 100;
    /**
     * 使用巧克力时增加的爱情值
     */
    public static final int ADD_LOVER_VALUE_FOR_CHOCOLATE = 2000;
}
