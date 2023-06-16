package hero.manufacture.service;

import hero.manufacture.Manufacture;
import hero.manufacture.ManufactureType;
import hero.manufacture.dict.ManufSkill;
import hero.manufacture.dict.ManufSkillDict;
import hero.player.HeroPlayer;
import hero.share.message.Warning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.base.AbsServiceAdaptor;
import yoyo.service.base.session.Session;


/**
 * 制造服务类 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class ManufactureServerImpl extends
        AbsServiceAdaptor<ManufactureServerConfig>
{
    private static ManufactureServerImpl  instance;

    /**
     * 每个玩家可以学习两种大类技能
     */
    private HashMap<Integer, List<Manufacture>> manufList;

    /**
     * 升级需要的金币数
     */
//    public final static int[]             MONEY_OF_UPGRADE     = {
//            2000, 10000, 50000, 250000                        };

    /**
     * 每个等级可获得的技能点上限
     */
//    public final static int[]             POINT_LIMIT          = {
//            1000, 6000, 31000, 156000, 156000                 };

    /**
     * 技能等级的称号
     */
//    public final static String[]          LEVEL_TITLE          = {
//            "初学", "熟练", "精通", "大师", "至尊"                      };
    public final static String[]          LEVEL_TITLE          = {
            "初学","精通", "专家"                     };



    /**
     * 根据玩家等级获取等级称号
     * @param level
     * @return
     */
    public String getTitle(int level){
        if(level <= 10){
            return LEVEL_TITLE[0];
        }else if(level <= 20){
            return LEVEL_TITLE[1];
        }else{
            return LEVEL_TITLE[2];
        }
    }

    /**
     * 学习一项新技能需要的金钱
     */
    public final static int               FREIFHT_OF_NEW_SKILL = 400;

    private ManufactureServerImpl()
    {
        config = new ManufactureServerConfig();
        manufList = new HashMap<Integer, List<Manufacture>>();
    }

    public static ManufactureServerImpl getInstance ()
    {
        if (instance == null)
            instance = new ManufactureServerImpl();
        return instance;
    }

    @Override
    protected void start ()
    {
        ManufSkillDict.getInstance().loadManufSkills(config.dataPath);
    }

    @Override
    public void createSession (Session _session)
    {
        List<Manufacture> _manufList = ManufactureDAO.loadManufByUserID(_session.userID);
        if (_manufList != null)
            manufList.put(_session.userID, _manufList);
    }

    /**
     * 通过玩家ID得到玩家的制造技能
     * 
     * @param _userID
     * @return
     */
    public List<Manufacture> getManufactureListByUserID (int _userID)
    {
        return manufList.get(_userID);
    }

    /**
     * 根据NPC名称和玩家USERID获取制造技能
     * @param _userID
     * @param _npcName
     * @return
     */
    public Manufacture getManufactureByUserIDAndNpcName(int _userID, String _npcName){
        for(Manufacture manuf : manufList.get(_userID)){
            if(manuf.getManufactureType().getName().equals(_npcName)){
                return manuf;
            }
        }
        return  null;
    }

    /**
     * 根据玩家USERID和大类技能类型ID获取制造技能
     * @param _userID
     * @param _type
     * @return
     */
    public Manufacture getManufactureByUserIDAndType(int _userID, byte _type){
        for(Manufacture manuf : manufList.get(_userID)){
            if(manuf.getManufactureType().getID() == _type){
                return manuf;
            }
        }
        return  null;
    }

    /**
     * 学习制造技能
     * 这里学习大类技能(如：药草匠，鉴宝匠，工艺匠，铁匠)
     * @param _userID
     * @param _type 制造类型
     * @return 是否学习成功
     */
    public boolean studyManufacture (HeroPlayer _player, ManufactureType _type)
    {
        if (manufList.get(_player.getUserID()) != null && manufList.get(_player.getUserID()).size()==2)
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new Warning("每个玩家最多只能学习两种制造技能！"));
            return false;
        }
        manufList.get(_player.getUserID()).add(new Manufacture(_type));
//        manufList.put(_player.getUserID(), new Manufacture(_type));
        ManufactureDAO.studyManuf(_player.getUserID(), _type);
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                new Warning(GET_BASIC + _type.getName() + MANUF_SKILL));

        return true;
    }

    /**
     * 得到可制作ID列表
     * 
     * @param _userID
     * @return
     */
    public ArrayList<Integer> getCanUseManufIDs (int _userID)
    {
        ArrayList<Integer> manufSkillIDList = new ArrayList<Integer>();
        List<Manufacture> manufactureList = manufList.get(_userID);
        for(Manufacture manuf : manufactureList){
            for (Integer id : manuf.getManufIDList()){
                manufSkillIDList.add(id);
            }
        }

        return manufSkillIDList;
    }

    /**
     * 学习制造条目
     * 
     * @param _userID
     * @param _manufID
     */
    public boolean addManufSkillItem (HeroPlayer _player, ManufSkill _skill,
            GetTypeOfSkillItem _getType)
    {

        List<Manufacture> _manufList = manufList.get(_player.getUserID());
        for(Manufacture manuf : _manufList){
            if(manuf.getManufactureType().getID() == _skill.type){
                if(!manuf.isStudyedManufSkillID(_skill.id)){
                    manuf.addManufID(_skill.id);
                    ManufactureDAO.addManufID(_player.getUserID(),_skill.id);
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                                new Warning(_getType.toString() + _skill.name));

                    return true;
                }else{
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                                    new Warning(STUDYED_MANUF_SKILL));

                    return false;
                }
            }else{
                ResponseMessageQueue.getInstance().put(
                    _player.getMsgQueueIndex(),
                    new Warning(NEED_MANUF_SKILL
                            + ManufactureType.get(_skill.type).getName()));

                return false;
            }
        }
        return false;

        /*if (null == skill || _manuf.type != skill.getManufactureType().getID())
        {
            OutMsgQ.getInstance().put(
                    _player.getMsgQueueIndex(),
                    new Warning(NEED_MANUF_SKILL
                            + ManufactureType.get(_manuf.type).getName()));

            return false;
        }

        if (!skill.isStudyedManufSkillID(_manuf.id))
        {
            skill.addManufID(_manuf.id);
            ManufactureDAO.addManufID(_player.getUserID(), _manuf.id);

            OutMsgQ.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(_getType.toString() + _manuf.name));

            return true;
        }
        else
        {
            OutMsgQ.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(STUDYED_MANUF_SKILL));

            return false;
        }*/
    }

    /**
     * 遗忘制作技能
     * 
     * @param _userID
     */
    public List<Manufacture> forgetManufactureByUserID (int _userID)
    {
        List<Manufacture> manufactureList = manufList.remove(_userID);

        if (manufactureList != null)
        {
            ManufactureDAO.forgetManufByUserID(_userID);
        }
        return manufactureList;
    }

    /**
     * 制造技能升级
     * 
     * @param _userID
     * @param _gather
     */
    public void lvlUp (int _userID, Manufacture _manuf)
    {
        _manuf.lvlUp();
        ManufactureDAO.updateManuf(_userID, _manuf);
    }

    /**
     * 添加制造技能点
     * 
     * @param _userID
     * @param _gather
     * @param _addPoint
     */
    public void addPoint (int _userID, Manufacture _manuf, int _addPoint)
    {
        _manuf.addPoint(_addPoint);
        ManufactureDAO.updateManuf(_userID, _manuf);
    }

    private static final String GET_BASIC           = "你获得了 ";

    private static final String MANUF_SKILL         = " 技能";

    private static final String NEED_MANUF_SKILL    = "需要 ";

    private static final String STUDYED_MANUF_SKILL = "您已学会此技能！";
}
