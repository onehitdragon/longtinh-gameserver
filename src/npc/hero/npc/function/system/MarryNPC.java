package hero.npc.function.system;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;

import hero.dungeon.Dungeon;
import hero.dungeon.service.DungeonServiceImpl;
import hero.effect.service.EffectServiceImpl;
import hero.group.Group;
import hero.group.service.GroupServiceImpl;
import hero.guild.Guild;
import hero.guild.service.GuildServiceImpl;
import hero.item.bag.exception.BagException;
import hero.item.dictionary.GoodsContents;
import hero.item.service.GoodsServiceImpl;
import hero.item.special.Divorce;
import hero.item.special.MarryRing;
import hero.log.service.CauseLog;
import hero.lover.message.ResponseMarryRelationShow;
import hero.lover.service.LoverLevel;
import hero.map.Map;
import hero.map.message.*;
import hero.map.service.MapServiceImpl;
import hero.map.service.MiniMapImageDict;
import hero.npc.message.AskPlayerAgreeWedding;
import hero.npc.message.AskPlayerBuyDivorce;
import hero.npc.message.NpcInteractiveResponse;
import hero.player.define.EClan;
import hero.player.message.ResponsePlayerMarryStatus;
import hero.share.ME2GameObject;
import hero.share.service.ME2ObjectList;
import hero.share.service.Tip;
import hero.social.ESocialRelationType;
import hero.social.service.SocialServiceImpl;
import hero.task.service.TaskServiceImpl;
import hero.ui.UI_SelectOperation;
import hero.chat.service.ChatQueue;
import hero.chat.service.ChatServiceImpl;
import hero.lover.service.LoverServiceImpl;
import hero.lover.service.LoverServiceImpl.MarryStatus;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.BaseNpcFunction;
import hero.npc.function.ENpcFunctionType;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.ui.UI_InputString;
import org.apache.log4j.Logger;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;

/**
 * 婚姻NPC
 * 
 * @author Luke
 * @date Jun 19, 2009
 */
public class MarryNPC extends BaseNpcFunction
{
    private static Logger log = Logger.getLogger(MarryNPC.class);




    /**
     * 顶层操作菜单图标列表
     */
//    private static final short[]       mainMenuMarkImageIDList = {1008,1008,1008};



    private static ArrayList<byte[]>[] loverMenuOptionData     = new ArrayList[Tip.FUNCTION_LOVE_MENU_LIST.length];

//    private static ArrayList<byte[]>[] loverDivorceMenuOptionData     = new ArrayList[Tip.FUNCTION_LOVE_DIVORCE_MENU_LIST.length];

    /**
     * 婚礼礼堂地图
     */
    public static final short  marryMapId = 406; // 人族
    public static final short  marryMapId2 = 407;//魔族

    /**
     * 月老所在地图，种族不一样，地图不一样
     */
    public static final short  marryNPCMapId = 7;//人族
    public static final short  marryNPCMapId2 = 65; //魔族

    /**
     * 用来保存此时结婚双方的userID
     */
    public static final int[] marryer = new int[2];

    /**
     * 办婚礼需要的金钱
     */
    private static final int           CASH                    = 2000000;



    enum Step
    {
        TOP(1), DIVORCE(10);

        byte tag;

        Step(int _tag)
        {
            tag = (byte) _tag;
        }
    }

    /**
     * 是否可以进入婚礼礼堂
     */
    public static boolean canEntry = false;

    public MarryNPC(int npcID)
    {
        super(npcID);
    }

    @Override
    public ENpcFunctionType getFunctionType ()
    {
        return ENpcFunctionType.MARRY_NPC;
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList (
            HeroPlayer _player)
    {
        ArrayList<NpcHandshakeOptionData> temp = new ArrayList<NpcHandshakeOptionData>();
        for(NpcHandshakeOptionData data : optionList){
            temp.add(data);
        }
        return temp;
    }

    @Override
    public void initTopLayerOptionList ()
    {
        ArrayList<byte[]> data1 = new ArrayList<byte[]>();


        for (int i = 0; i < Tip.FUNCTION_LOVE_MENU_LIST.length; i++)
        {
            if(i == 0){//离婚不用输入对方名称,离婚有二级菜单
                data1.add(UI_InputString.getBytes(Tip.FUNCTION_LOVE_INPUT));
                loverMenuOptionData[i] = data1;
            }

            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
//            data.miniImageID = mainMenuMarkImageIDList[i];
            data.miniImageID = getMinMarkIconID();
            data.optionDesc = Tip.FUNCTION_LOVE_MENU_LIST[i];
            data.functionMark = getFunctionType().value()
                    * BaseNpcFunction.FUNCTION_EXPEND_MODULUS + i;

            optionList.add(data);

            if (null != loverMenuOptionData[i])
            {
                data.followOptionData = new ArrayList<byte[]>(
                        loverMenuOptionData[i].size());

                for (byte[] b : loverMenuOptionData[i])
                {
                    data.followOptionData.add(b);
                }
            }
        }
    }

    @Override
    public void process (HeroPlayer _player, byte _step, int selectIndex,
            YOYOInputStream _content) throws Exception
    {
        log.debug("MarryNPC _step = " + _step +"  selectIndex = " + selectIndex);
        if (_step == Step.TOP.tag)
        {
            switch (selectIndex)
            {
                case 0: // 结婚
                {
                    String name = _content.readUTF();
                    log.debug("结婚 name = " + name);
                    if (name.equals(_player.getName()))
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning("不能为自己"));
                        
                        return;
                    }
                    else
                    {
                        HeroPlayer other = PlayerServiceImpl.getInstance()
                                .getPlayerByName(name);

                        if (other == null || !other.isEnable())
                        {
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning("对方不在线"));

                            return;
                        }else{
                            String myLover = LoverServiceImpl.getInstance().whoMarriedMe(_player.getName());
                            if(null != myLover && myLover.equals(name)){
                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning("你们已经结过婚了，不用再结了！"));
                                return;
                            }
                            myLover = LoverServiceImpl.getInstance().whoLoveMe(_player.getName());
                            if(myLover == null || !myLover.equals(name)){
                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning("你和"+name+"不是恋人，不能结婚！"));
                                return;
                            }

                            if(other.getSex() == _player.getSex()){//在恋人时就得验证
                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning("同性不能结婚"));

                                return;
                            }else if(other.getClan() != _player.getClan()){
                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning("不同种族的玩家，不能结婚"));

                                return;
                            }else if(_player.getLevel() < 20){
                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning("您的等级不够20级，不能结婚"));

                                return;
                            }else if(other.getLevel() < 20){
                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning("对方等级不够20级，不能结婚"));

                                return;
                            }else if(_player.getLoverValue() < 3000 || other.getLoverValue() < 3000){
                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning("你们的爱情度不够 3000，不能结婚"));

                                return;
                            }else{
                                log.debug("start ......");

                                /*String myLover = LoverServiceImpl.getInstance().whoMarriedMe(_player.getName());
                                if(null != myLover && myLover.equals(name)){
                                    OutMsgQ.getInstance().put(
                                            _player.getMsgQueueIndex(),
                                            new Warning("你们已经结过婚了，不用再结了！"));
                                    return;
                                }
                                myLover = LoverServiceImpl.getInstance().whoLoveMe(_player.getName());
                                if(myLover == null || !myLover.equals(name)){
                                    OutMsgQ.getInstance().put(
                                            _player.getMsgQueueIndex(),
                                            new Warning("你和"+name+"不是恋人，不能结婚！"));
                                    return;
                                }*/
                                int goodsID = MarryGoods.RANG.getId();
                                int rangnum = _player.getInventory().getSpecialGoodsBag().getGoodsNumber(goodsID);
                                if(rangnum == 0){
                                    ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning("结婚需要结婚戒指哦，去买一个再来吧！"));

                                    return;
                                }else{
                                    rangnum = other.getInventory().getSpecialGoodsBag().getGoodsNumber(goodsID);
                                    if(rangnum == 0){
                                        ResponseMessageQueue.getInstance().put(
                                            _player.getMsgQueueIndex(),
                                            new Warning("对方没有结婚戒指，让他(她)去买一个再来吧！"));

                                        return;
                                    }
                                }

                                if(_player.getGroupID() == 0 || other.getGroupID() == 0){
                                    ResponseMessageQueue.getInstance().put(
                                            _player.getMsgQueueIndex(),
                                            new Warning("结婚需要恋人组队，且队伍中只有两人才可以！"));

                                    return;
                                }else if(_player.getGroupID() != other.getGroupID()){
                                    ResponseMessageQueue.getInstance().put(
                                            _player.getMsgQueueIndex(),
                                            new Warning("结婚需要恋人组成一队！"));

                                    return;
                                }else {
                                    Group group = GroupServiceImpl.getInstance().getGroup(_player.getGroupID());
                                    if(group.getMemberNumber() > 2){
                                        ResponseMessageQueue.getInstance().put(
                                            _player.getMsgQueueIndex(),
                                            new Warning("结婚需要恋人组成一队且队伍只有两个人！"));

                                        return;
                                    }
                                }


                                if(myLover.equals(name)){
                                    String content = "玩家\""+_player.getName()+"\"现在想和你结婚，你同意吗？";
                                    ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(),
                                        new AskPlayerAgreeWedding(_player,other,content,(byte)2));
                                }else{
                                    ResponseMessageQueue.getInstance().put(
                                            _player.getMsgQueueIndex(),
                                            new Warning(name + " 不是你的恋人，不能和你结婚"));
                                    return;
                                }

                            }
                        }
                    }

                    break;
                }
                /*case 2: //其它玩家参加婚礼,婚礼双方玩家的好友列表或者公会列表中,且结婚双方玩家在婚礼现场中
                {
                    log.debug("other player join marry.....");
                    if(canEntry){//结婚双方在婚礼现场中
                        log.debug("other player can entry...");
                        if(SocialServiceImpl.getInstance().beFriend(marryer[0],_player.getUserID())
                                || SocialServiceImpl.getInstance().beFriend(marryer[1],_player.getUserID())){//和结婚双方的其中一方是好友
                            log.debug("beFriend 好友参加婚礼");
                            playerGoTOMarryMap(_player);
                        }else if(_player.getGuildID() != 0 ){
                            Guild guild = GuildServiceImpl.getInstance().getGuild(_player.getGuildID());
                            if(guild.getMember(marryer[0]) != null || guild.getMember(marryer[1]) != null){//在结婚双方其中一方的工会中
                                log.debug("beGuild 工友参加婚礼");
                               playerGoTOMarryMap(_player);
                            }
                        }else{
                            log.debug("no friend and no guild");
                            OutMsgQ.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning("必须是好友或同一工会的才可能参加婚礼！"));
                        }
                    }else{
                        OutMsgQ.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning("现在不能进入婚礼礼堂！"));
                    }
                    break;
                }*/
                /*case 0: //求婚，必须要有订婚戒指
                {
                    String name = _content.readUTF();
                    if(LoverServiceImpl.getInstance().whoLoveMe(_player.getName()) != null){
                        OutMsgQ.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning("你已经订婚了，不能再向别人求婚!"));
                        break;
                    }
                    if(LoverServiceImpl.getInstance().whoMarriedMe(_player.getName()) != null){
                        OutMsgQ.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning("你已经结婚了，不能再向别人求婚!"));
                        break;
                    }
                    if(LoverServiceImpl.getInstance().whoLoveMe(name) != null){
                        OutMsgQ.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning("对方已经和别人订婚了!"));
                        break;
                    }
                    if(LoverServiceImpl.getInstance().whoMarriedMe(name) != null){
                        OutMsgQ.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning("对方已经和别人结婚了!"));
                        break;
                    }
                    int goodsID = MarryGoods.RANG.getId();
                    
                    log.debug(" player propose ring num = " + (_player.getInventory().getSpecialGoodsBag().getGoodsNumber(goodsID)));
                    if(_player.getInventory().getSpecialGoodsBag().getGoodsNumber(goodsID) > 0){

                        //  这里先问一下对方同不同意
                        HeroPlayer otherPlayer = PlayerServiceImpl.getInstance().getPlayerByName(name);
                        if(null != otherPlayer){
                            String content = "玩家 \""+ _player.getName() +" \"现在向你求婚，你同意吗？";

                            OutMsgQ.getInstance().put(otherPlayer.getMsgQueueIndex(),
                                    new AskPlayerAgreeWedding(_player,otherPlayer,content,(byte)1));
                        }else{
                            OutMsgQ.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning("玩家\""+name+"\"不在线!"));
                        }


                    }else{
                        OutMsgQ.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning("求婚必须要有求婚戒指哦！\r\n请准备好求婚戒指再来吧.\r\n祝你好运！"));
                    }
                    break;
                }*/
                case 1: //离婚,协议离婚夫妻二人必须要组队，且队伍中不能有其它人 ,npc 取消强制离婚选项
                {
                    log.debug("NPC 离婚.....");
                    if(LoverServiceImpl.getInstance().whoMarriedMe(_player.getName()) != null){
//                        OutMsgQ.getInstance().put(
//                                _player.getMsgQueueIndex(),
//                                new NpcInteractiveResponse(getHostNpcID(), optionList
//                                        .get(selectIndex).functionMark,
//                                        Step.DIVORCE.tag,
//                                        UI_SelectOperation.getBytes(Tip.FUNCTION_LOVE_DIVORCE_TYPE,Tip.FUNCTION_LOVE_DIVORCE_MENU_LIST,loverDivorceMenuOptionData)));
                        log.debug("协议离婚 divorce");
                        String name = _player.spouse;
    //                    log.debug("协议离婚 : "+name);
                        HeroPlayer otherMarryPlayer = PlayerServiceImpl.getInstance().getPlayerByName(name);
                        if(otherMarryPlayer != null){
                            String myLover = LoverServiceImpl.getInstance().whoMarriedMe(_player.getName());
                            log.debug("mylover = " + myLover);
                            if(myLover.equals(name)){
    //                            divorce(_player,otherMarryPlayer,optionIndex);
                                int num = _player.getInventory().getSpecialGoodsBag().getGoodsNumber(MarryGoods.DIVORCE.getId());
                                if(num == 0){
                                    num = otherMarryPlayer.getInventory().getSpecialGoodsBag().getGoodsNumber(MarryGoods.DIVORCE.getId());
                                    if(num == 0){
                                        //没有离婚证明，提示发起者购买离婚证明
                    //                    OutMsgQ.getInstance().put(_player.getMsgQueueIndex(),new AskPlayerBuyDivorce((byte)1,"没有离婚协议，\n是否要购买！"));
                                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new Warning("没有离婚协议，是否去商城购买！",Warning.UI_TOOLTIP_AND_EVENT_TIP,Warning.SUBFUNCTION_UI_POPUP_COMM_CHARGE));
                                        return;
                                    }
                                }

                                if(_player.getGroupID() > 0){
                                    if(otherMarryPlayer.getGroupID() > 0 && _player.getGroupID() == otherMarryPlayer.getGroupID()){
                                        log.debug("divorce in same group ...");
                                         if(GroupServiceImpl.getInstance().getGroup(_player.getGroupID()).getMemberNumber() == 2){
                                             log.debug("divorce group only 2 ");
                                             ResponseMessageQueue.getInstance().put(otherMarryPlayer.getMsgQueueIndex(),
                                                            new AskPlayerAgreeWedding(_player,otherMarryPlayer,_player.getName()+"要和你离婚，\n你同意吗？",(byte)3));

                                             ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new Warning(Tip.TIP_SEND_MESSAGE_NOTIFY,Warning.UI_STRING_TIP));
                                         }else{
                                              ResponseMessageQueue.getInstance().put(
                                                _player.getMsgQueueIndex(),
                                                new Warning("夫妻队伍里必须只有双方两个人！"));
                                         }
                                    }else{
                                         ResponseMessageQueue.getInstance().put(
                                                _player.getMsgQueueIndex(),
                                                new Warning("夫妻双方必须要单独组成一个队伍！"));
                                    }
                                }else{
                                    ResponseMessageQueue.getInstance().put(
                                                _player.getMsgQueueIndex(),
                                                new Warning("夫妻双方必须要单独组成一个队伍！"));
                                }

                            }else{
                                ResponseMessageQueue.getInstance().put(
                                                _player.getMsgQueueIndex(),
                                                new Warning(name + "和你不是夫妻！"));
                            }
                        }else{
                            ResponseMessageQueue.getInstance().put(
                                                _player.getMsgQueueIndex(),
                                                new Warning("对方不在线！"));
                        }
                    }else{
                        ResponseMessageQueue.getInstance().put(
                                            _player.getMsgQueueIndex(),
                                            new Warning("你还没有结婚呢，离什么婚！"));
                    }
                    break;
                }
                case 2:
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new Warning(Tip.TIP_MARRY_DESC,Warning.UI_TOOLTIP_TIP));
                    break;
                }
            }
        }
        if(_step == Step.DIVORCE.tag){
            byte optionIndex = _content.readByte();
            log.debug("optionIndex = " + optionIndex);
            switch(optionIndex){
                case 0:
                {
                    log.debug("协议离婚 divorce");
//                    String name = _content.readUTF(); //夫妻的另一方
                    String name = _player.spouse;
//                    log.debug("协议离婚 : "+name);
                    HeroPlayer otherMarryPlayer = PlayerServiceImpl.getInstance().getPlayerByName(name);
                    if(otherMarryPlayer != null){
                        String myLover = LoverServiceImpl.getInstance().whoMarriedMe(_player.getName());
                        log.debug("mylover = " + myLover);
                        if(myLover.equals(name)){
//                            divorce(_player,otherMarryPlayer,optionIndex);
                            int num = _player.getInventory().getSpecialGoodsBag().getGoodsNumber(MarryGoods.DIVORCE.getId());
                            if(num == 0){
                                num = otherMarryPlayer.getInventory().getSpecialGoodsBag().getGoodsNumber(MarryGoods.DIVORCE.getId());
                                if(num == 0){
                                    //没有离婚证明，提示发起者购买离婚证明
                //                    OutMsgQ.getInstance().put(_player.getMsgQueueIndex(),new AskPlayerBuyDivorce((byte)1,"没有离婚协议，\n是否要购买！"));
                                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new Warning("没有离婚协议，是否去商城购买！",Warning.UI_TOOLTIP_AND_EVENT_TIP,Warning.SUBFUNCTION_UI_POPUP_COMM_CHARGE));
                                    return;
                                }
                            }
                            if(_player.getGroupID() > 0){
                                if(otherMarryPlayer.getGroupID() > 0 && _player.getGroupID() == otherMarryPlayer.getGroupID()){
                                    log.debug("divorce in same group ...");
                                     if(GroupServiceImpl.getInstance().getGroup(_player.getGroupID()).getMemberNumber() == 2){
                                         log.debug("divorce group only 2 ");
                                         ResponseMessageQueue.getInstance().put(otherMarryPlayer.getMsgQueueIndex(),
                                                        new AskPlayerAgreeWedding(_player,otherMarryPlayer,_player.getName()+"要和你离婚，\n你同意吗？",(byte)3));

                                         ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new Warning(Tip.TIP_SEND_MESSAGE_NOTIFY,Warning.UI_STRING_TIP));
                                     }else{
                                          ResponseMessageQueue.getInstance().put(
                                            _player.getMsgQueueIndex(),
                                            new Warning("夫妻队伍里必须只有双方两个人！"));
                                     }
                                }else{
                                     ResponseMessageQueue.getInstance().put(
                                            _player.getMsgQueueIndex(),
                                            new Warning("夫妻双方必须要单独组成一个队伍！"));
                                }
                            }else{
                                ResponseMessageQueue.getInstance().put(
                                            _player.getMsgQueueIndex(),
                                            new Warning("夫妻双方必须要单独组成一个队伍！"));
                            }

                        }else{
                            ResponseMessageQueue.getInstance().put(
                                            _player.getMsgQueueIndex(),
                                            new Warning(name + "和你不是夫妻！"));
                        }
                    }else{
                        ResponseMessageQueue.getInstance().put(
                                            _player.getMsgQueueIndex(),
                                            new Warning("对方不在线！"));
                    }
                    break;
                }
                case 1:
                {
                    /*log.debug("强制离婚 diovrce");
//                    String name = _content.readUTF(); //夫妻的另一方
                    String name = _player.spouse;
                    log.debug("强制离婚 diovrce : " + name);
                    String myLover = LoverServiceImpl.getInstance().whoMarriedMe(_player.getName());
                    log.debug("强制离婚 diovrce mylover = " + myLover);
                    if(myLover.equals(name)){
                        int goodsID = MarryGoods.FORCE_DIVORCE.getId();
                        if(_player.getInventory().getSpecialGoodsBag().getGoodsNumber(goodsID) > 0){
                            Divorce divorce = (Divorce) GoodsContents.getGoods(goodsID);
                            log.debug("divore goods = " + divorce.getName());
                            divorce.setCanUse(true);
                            divorce.setOtherName(name);
                            if(divorce.beUse(_player,null,-1)){ //这里直接把玩家背包里的道具用掉了
                                if (divorce.disappearImmediatelyAfterUse())
                                    {
                                        divorce.remove(_player, (short)-1);
                                    }
                            }
                            log.debug("divorece ..end .. ");
                        }else{
                            OutMsgQ.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning("强制离婚必须要有离婚证明才行"));
                        }
                    }else{
                        OutMsgQ.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning(name + "和你不是夫妻！"));
                    }*/
                    break;
                }

            }
        }
    }

    /**
     * 结婚
     * @param _player
     * @param otherName
     * @return
     */
    public static boolean married(HeroPlayer _player, String otherName){
        log.debug("married。。。。");

//            if(LoverServiceImpl.getInstance().noHadOtherMarry(marryMapId))
//            {
                log.debug("开始 ");
                MarryStatus status = LoverServiceImpl.getInstance()
                        .registerMarriage(_player.getName(), otherName, _player.getClan().getID());
                if (status == MarryStatus.SUCCESS)
                {
                    HeroPlayer otherMarryPlayer = PlayerServiceImpl.getInstance().getPlayerByName(otherName);
                    if(playerGoTOMarryMap(_player,null) && playerGoTOMarryMap(_player,otherMarryPlayer)){
                        // 成为夫妻
                        marryer[0] = _player.getUserID();
                        marryer[1] = otherMarryPlayer.getUserID();

                        _player.canRemoveAllFromMarryMap = true;
                        otherMarryPlayer.canRemoveAllFromMarryMap = true;

                        _player.spouse = otherName;
                        otherMarryPlayer.spouse = _player.getName();

                        _player.loverLever = LoverLevel.ZHI;
                        otherMarryPlayer.loverLever = LoverLevel.ZHI;

                        /*PlayerServiceImpl
                            .getInstance()
                            .addMoney(
                                    _player,
                                    -CASH,
                                    1,
                                    PlayerServiceImpl.MONEY_DRAW_LOCATION_NONE,"结婚花费");*/

                        ResponseMessageQueue.getInstance().put(
                                _player.getMsgQueueIndex(),
                                new Warning("恭喜！你和\""+otherName+"\"已成为夫妻!"));

                        for (int i = 0; i < 3; i++)
                        {
                            ChatQueue
                                    .getInstance()
                                    .add(
                                            ChatServiceImpl.TOP_SYSTEM_WORLD,
                                            null,
                                            null,
                                            null,
                                            "恭喜"
                                                    + _player.getName()
                                                    + "与" + otherName + "喜结连理，永不分离。");
                        }

                        canEntry = true;

                        _player.marryed = true;
                        otherMarryPlayer.marryed = true;

                        Timer loverValueTimer = PlayerServiceImpl.getInstance().getLoverValueTimerByUserID(_player.getUserID());
                         if(loverValueTimer == null){
                             loverValueTimer = PlayerServiceImpl.getInstance().getLoverValueTimerByUserID(otherMarryPlayer.getUserID());
                             if(loverValueTimer == null){
                                PlayerServiceImpl.getInstance().startLoverValueTimer(_player);
                             }
                         }
                        log.debug("can entry marry map = " + canEntry);
                        return true;
                    }else{
                        LoverServiceImpl.getInstance().marryFaild(_player.getName(),otherName);
                        ResponseMessageQueue.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new Warning("结婚双方进入婚礼礼堂时出错，结婚失败！"));
                    }


                }
                else if (status == MarryStatus.NO_TIME)
                {
                    // 时间没有到
                    ResponseMessageQueue.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new Warning("没有到登记结婚的时间"));

                }
                else if (status == MarryStatus.MARRIED)
                {
                    // 已结过婚
                    ResponseMessageQueue.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new Warning("你已婚"));

                }
                else if (status == MarryStatus.NOT_LOVER)
                {
                    // 还没有订婚
                    ResponseMessageQueue.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new Warning("你还没订婚"));

                }
            /*}else{
                 OutMsgQ.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new Warning("现在有玩家正在结婚，请一会儿再来"));
            }
*/
        return false;
    }

    /**
     * 设置恋人之前的判断逻辑
     * @param player
     * @param name2
     * @return
     */
    public static boolean propose(HeroPlayer player, String name2){
        HeroPlayer otherPlayer = PlayerServiceImpl.getInstance().getPlayerByName(name2);
        if(otherPlayer == null){
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("对方不在线！"));
            return false;
        }
        if(player.getSex() == otherPlayer.getSex()){
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("本游戏不支持同性恋！"));
            return false;
        }
        String othername = LoverServiceImpl.getInstance().whoLoveMe(player.getName());
        if(null != othername){
            log.debug("你已经有恋人了");
             ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new Warning("你已经有恋人了！"));
            return false;
        }
        othername = LoverServiceImpl.getInstance().whoMarriedMe(player.getName());
        if(null != othername){
            log.debug("你已经结婚了！");
             ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new Warning("你已经结婚了！"));
            return false;
        }

        String whoLoveOther = LoverServiceImpl.getInstance().whoLoveMe(name2);
        if(null != whoLoveOther){
            log.debug("对方已经有恋人了");
             ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new Warning("对方已经有恋人了！"));
            return false;
        }
        whoLoveOther = LoverServiceImpl.getInstance().whoMarriedMe(name2);
        if(null != whoLoveOther){
            log.debug("对方已经结婚了！");
             ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new Warning("对方已经结婚了！"));
            return false;
        }

        ResponseMessageQueue.getInstance().put(otherPlayer.getMsgQueueIndex(),
                new AskPlayerAgreeWedding(player,otherPlayer,player.getName()+"想要和你成为恋人，\n你同意吗？",(byte)1));

        return true;
    }

    /**
     * 对方同意后，成为恋人
     * @param player
     * @param otherPlayer
     * @return
     */
    public static boolean propose(HeroPlayer player,  HeroPlayer otherPlayer){
        log.debug("propose。。。");
        MarryStatus status = LoverServiceImpl.getInstance().registerLover(player.getName(), otherPlayer.getName());
        log.debug("propose marry status = " + status);
        if(status == MarryStatus.LOVED_NO_MARRY){
            log.debug("你已经有恋人了");
             ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new Warning("你已经有恋人了！"));
             otherPlayer = PlayerServiceImpl.getInstance().getPlayerByName(player.spouse);
            if(null == otherPlayer){
                otherPlayer = PlayerServiceImpl.getInstance().getOffLinePlayerInfoByName(player.spouse);
                otherPlayer.setLoverValue(player.getLoverValue());
                otherPlayer.loverLever = player.loverLever;
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new ResponseMarryRelationShow((byte)1,otherPlayer));
//             OutMsgQ.getInstance().put(otherPlayer.getMsgQueueIndex(),new ResponseMarryRelationShow((byte)1,player));
            }
        }
        if(status == MarryStatus.MARRIED){
            log.debug("你已经结婚了！");
             ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new Warning("你已经结婚了！"));
            otherPlayer = PlayerServiceImpl.getInstance().getPlayerByName(player.spouse);
            if(null == otherPlayer){
                otherPlayer = PlayerServiceImpl.getInstance().getOffLinePlayerInfoByName(player.spouse);
                otherPlayer.setLoverValue(player.getLoverValue());
                otherPlayer.loverLever = player.loverLever;
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new ResponseMarryRelationShow((byte)2,otherPlayer));
//             OutMsgQ.getInstance().put(otherPlayer.getMsgQueueIndex(),new ResponseMarryRelationShow((byte)2,player));
            }
        }
        if(status == MarryStatus.NOT_LOVER){
            ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new Warning("成为恋人失败！"));
            return false;
        }
        if(status == MarryStatus.LOVED_SUCCESS){

             player.spouse = otherPlayer.getName();
             otherPlayer.spouse = player.getName();

             ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new Warning("恭喜，你与\""+otherPlayer.getName()+"\"成为恋人！"));
             ResponseMessageQueue.getInstance().put(
                                        otherPlayer.getMsgQueueIndex(),
                                        new Warning("恭喜，你与\""+player.getName()+"\"成为恋人！"));

             ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new ResponsePlayerMarryStatus(player));

             ResponseMessageQueue.getInstance().put(
                                        otherPlayer.getMsgQueueIndex(),
                                        new ResponsePlayerMarryStatus(otherPlayer));

             ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new ResponseMarryRelationShow((byte)1,otherPlayer));
             ResponseMessageQueue.getInstance().put(otherPlayer.getMsgQueueIndex(),new ResponseMarryRelationShow((byte)1,player));

             Timer loverValueTimer = PlayerServiceImpl.getInstance().getLoverValueTimerByUserID(player.getUserID());
             if(loverValueTimer == null){
                 loverValueTimer = PlayerServiceImpl.getInstance().getLoverValueTimerByUserID(otherPlayer.getUserID());
                 if(loverValueTimer == null){
                    PlayerServiceImpl.getInstance().startLoverValueTimer(player);
                 }
             }

             log.debug("LOVED_SUCCESS .. ResponseMarryRelationShow end ...");
//             for (int i = 0; i < 5; i++)
//             {
                ChatQueue
                        .getInstance()
                        .add(ChatServiceImpl.TOP_SYSTEM_WORLD,
                                null,
                                null,
                                null,
                                "刚刚 \""+ player.getName()+ "\" 与 \"" + otherPlayer.getName() + "\" 成为恋人，恭喜他们！");
//             }

        }
        return status == MarryStatus.LOVED_SUCCESS;
    }

    /**
     * 和恋人分手
     * @param player
     * @param otherName
     */
    public static void breakUp(HeroPlayer player, String otherName){
        HeroPlayer otherPlayer = PlayerServiceImpl.getInstance().getPlayerByName(otherName);
        if(otherPlayer == null){
//            OutMsgQ.getInstance().put(player.getMsgQueueIndex(),new Warning("对方不在线"));
//            return;
            otherPlayer = PlayerServiceImpl.getInstance().getOffLinePlayerInfoByName(otherName);
        }
        String lover = LoverServiceImpl.getInstance().whoLoveMe(player.getName());
        if(lover == null || (!lover.equals(otherName))){
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("对方不是你的恋人！"));
        }else{
            LoverServiceImpl.getInstance().updateMarryStatus(player.getName(),otherName,MarryStatus.BREAK_UP);

            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("你已成功与 "+otherName+" 分手！"));

            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new ResponseMarryRelationShow((byte)0,null));

            Timer loverValueTimer = PlayerServiceImpl.getInstance().getLoverValueTimerByUserID(player.getUserID());
             if(loverValueTimer == null){
                 loverValueTimer = PlayerServiceImpl.getInstance().getLoverValueTimerByUserID(otherPlayer.getUserID());
                 if(loverValueTimer != null){
                     loverValueTimer.cancel();
                     PlayerServiceImpl.getInstance().removeLoverValueTimer(otherPlayer);
                 }
             }else {
                 loverValueTimer.cancel();
                 PlayerServiceImpl.getInstance().removeLoverValueTimer(player);
             }

            player.clearLoverValue();

            if(otherPlayer.isEnable()){
                otherPlayer.clearLoverValue();
                ResponseMessageQueue.getInstance().put(otherPlayer.getMsgQueueIndex(),new Warning(player.getName()+"已与你分手！"));
                ResponseMessageQueue.getInstance().put(otherPlayer.getMsgQueueIndex(),new ResponseMarryRelationShow((byte)0,null));
            }else {
                PlayerServiceImpl.getInstance().updatePlayerLoverValue(otherPlayer.getUserID(),0);
            }

            ChatQueue.getInstance()
                        .add(ChatServiceImpl.TOP_SYSTEM_WORLD,
                                null,
                                null,
                                null,
                                "刚刚 \""+ player.getName()+ "\" 与 \"" + otherPlayer.getName() + "\" 分手了。");

        }
    }

    /**
     * 离婚
     * @param _player
     * @param otherMarryPlayer
     * @param 离婚类型 0：协议离婚 1:强制离婚
     * @return
     */
    public static boolean divorce(HeroPlayer _player, HeroPlayer otherMarryPlayer, byte type ){
        log.debug("divorce 。。。 player groupid = " + _player.getGroupID() +" other groupid = " + otherMarryPlayer.getGroupID());
        if(type == 0){
            log.debug("divorce 协议离婚");

            int num = _player.getInventory().getSpecialGoodsBag().getGoodsNumber(MarryGoods.DIVORCE.getId());
            if(num == 0){
                num = otherMarryPlayer.getInventory().getSpecialGoodsBag().getGoodsNumber(MarryGoods.DIVORCE.getId());
                if(num == 0){
                    //没有离婚证明，提示发起者购买离婚证明
//                    OutMsgQ.getInstance().put(_player.getMsgQueueIndex(),new AskPlayerBuyDivorce((byte)1,"没有离婚协议，\n是否要购买！"));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new Warning("没有离婚协议，是否去商城购买！",Warning.UI_TOOLTIP_AND_EVENT_TIP,Warning.SUBFUNCTION_UI_POPUP_COMM_CHARGE));
                    return false;
                }
            }

            if(_player.getGroupID() > 0){
                if(otherMarryPlayer.getGroupID() > 0 && _player.getGroupID() == otherMarryPlayer.getGroupID()){
                    log.debug("divorce in same group ...");
                     if(GroupServiceImpl.getInstance().getGroup(_player.getGroupID()).getMemberNumber() == 2){
                         log.debug("divorce group only 2 ");
                         return divorce(_player,otherMarryPlayer,false);
                     }else{
                          ResponseMessageQueue.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new Warning("夫妻队伍里必须只有双方两个人！"));
                     }
                }else{
                     ResponseMessageQueue.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new Warning("夫妻双方必须要单独组成一个队伍！"));
                }
            }else{
                ResponseMessageQueue.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new Warning("夫妻双方必须要单独组成一个队伍！"));
            }
        }else if(type == 1){
            log.debug("divorce 强制离婚");
            int num = _player.getInventory().getSpecialGoodsBag().getGoodsNumber(MarryGoods.FORCE_DIVORCE.getId());
            if(num == 0){

                //没有离婚证明，提示发起者购买离婚证明
//                OutMsgQ.getInstance().put(_player.getMsgQueueIndex(),new AskPlayerBuyDivorce((byte)2,"你没有强制离婚证明，\n是否要购买！"));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new Warning("你没有强制离婚证明，是否去商城购买！",Warning.UI_TOOLTIP_AND_EVENT_TIP,Warning.SUBFUNCTION_UI_POPUP_COMM_CHARGE));
                return false;

            }
            return divorce(_player,otherMarryPlayer, true);
        }
        return false;
    }

    private static boolean divorce(HeroPlayer _player, HeroPlayer otherMarryPlayer,boolean force){
        log.debug("离婚开始了。。。。");
        MarryStatus status = LoverServiceImpl.getInstance().divorce(_player.getName());
        log.debug("status = " + status);
         if(status == MarryStatus.DIVORCE_SUCCESS){
             marryer[0] = 0;
             marryer[1] = 0;

             _player.spouse = "";
            otherMarryPlayer.spouse ="";

             ResponseMessageQueue.getInstance().put(
                _player.getMsgQueueIndex(),
                new Warning("你已与\""+otherMarryPlayer.getName()+"\"离婚！"));
             ResponseMessageQueue.getInstance().put(
                otherMarryPlayer.getMsgQueueIndex(),
                new Warning("你已与\""+_player.getName()+"\"离婚！"));

             ResponseMessageQueue.getInstance().put(
                            _player.getMsgQueueIndex(),
                            new ResponsePlayerMarryStatus(_player));

             ResponseMessageQueue.getInstance().put(
                            otherMarryPlayer.getMsgQueueIndex(),
                            new ResponsePlayerMarryStatus(otherMarryPlayer));

             for (int i = 0; i < 3; i++)
             {
                ChatQueue
                        .getInstance()
                        .add(ChatServiceImpl.TOP_SYSTEM_WORLD,
                                null,
                                null,
                                null,
                                "刚刚 \""+ _player.getName()+ "\" 与 \"" + otherMarryPlayer.getName() + "\" 离婚了。");
             }

             canEntry = false;

//             _player.getInventory().getSpecialGoodsBag().remove(MarryGoods.FORCE_DIVORCE.getId());
             if(force){
                 log.debug("force delete goods....");
                 try {
                     int dnum = _player.getInventory().getSpecialGoodsBag().getGoodsNumber(MarryGoods.FORCE_DIVORCE.getId());
                     if(dnum > 0){
                         GoodsServiceImpl.getInstance().deleteSingleGoods(_player,
                                 _player.getInventory().getSpecialGoodsBag(),MarryGoods.FORCE_DIVORCE.getId(), CauseLog.DIVORCE);
                     }else{
                         dnum = otherMarryPlayer.getInventory().getSpecialGoodsBag().getGoodsNumber(MarryGoods.FORCE_DIVORCE.getId());
                         if(dnum > 0){
                                GoodsServiceImpl.getInstance().deleteSingleGoods(otherMarryPlayer,
                                    otherMarryPlayer.getInventory().getSpecialGoodsBag(),MarryGoods.FORCE_DIVORCE.getId(), CauseLog.DIVORCE);
                         }
                     }
                     GoodsServiceImpl.getInstance().deleteSingleGoods(_player,
                             _player.getInventory().getSpecialGoodsBag(),MarryGoods.RANG.getId(), CauseLog.DIVORCE);

                     GoodsServiceImpl.getInstance().deleteSingleGoods(otherMarryPlayer,
                        otherMarryPlayer.getInventory().getSpecialGoodsBag(),MarryGoods.RANG.getId(), CauseLog.DIVORCE);
                     log.debug("force delete goods..end ....");
                 } catch (BagException e) {
                     e.printStackTrace();
                 }
             }else{
                 log.debug("not force delete goods....");
                 try {
                     GoodsServiceImpl.getInstance().deleteSingleGoods(_player,
                             _player.getInventory().getSpecialGoodsBag(),MarryGoods.DIVORCE.getId(), CauseLog.DIVORCE);

                     GoodsServiceImpl.getInstance().deleteSingleGoods(otherMarryPlayer,
                        otherMarryPlayer.getInventory().getSpecialGoodsBag(),MarryGoods.DIVORCE.getId(), CauseLog.DIVORCE);

                     GoodsServiceImpl.getInstance().deleteSingleGoods(_player,
                             _player.getInventory().getSpecialGoodsBag(),MarryGoods.RANG.getId(), CauseLog.DIVORCE);

                     GoodsServiceImpl.getInstance().deleteSingleGoods(otherMarryPlayer,
                        otherMarryPlayer.getInventory().getSpecialGoodsBag(),MarryGoods.RANG.getId(), CauseLog.DIVORCE);

                  log.debug("not  force delete goods....");

                 } catch (BagException e) {
                     e.printStackTrace();
                 }
             }

             _player.marryed = false;
             otherMarryPlayer.marryed = false;

             PlayerServiceImpl.getInstance().dbUpdate(_player);
             PlayerServiceImpl.getInstance().dbUpdate(otherMarryPlayer);

             ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new ResponseMarryRelationShow((byte)0,null));
             ResponseMessageQueue.getInstance().put(otherMarryPlayer.getMsgQueueIndex(),new ResponseMarryRelationShow((byte)0,null));

             Timer loverValueTimer = PlayerServiceImpl.getInstance().getLoverValueTimerByUserID(_player.getUserID());
             if(loverValueTimer == null){
                 loverValueTimer = PlayerServiceImpl.getInstance().getLoverValueTimerByUserID(otherMarryPlayer.getUserID());
                 if(loverValueTimer != null){
                     loverValueTimer.cancel();
                     PlayerServiceImpl.getInstance().removeLoverValueTimer(otherMarryPlayer);
                 }
             }else {
                 loverValueTimer.cancel();
                 PlayerServiceImpl.getInstance().removeLoverValueTimer(_player);
             }
             _player.clearLoverValue();
             otherMarryPlayer.clearLoverValue();
             log.debug("离婚结束了。。。。");
             return true;
         }else if(status == MarryStatus.LOVED_NO_MARRY){
             ResponseMessageQueue.getInstance().put(
                _player.getMsgQueueIndex(),
                new Warning("你还没有结婚，现在不能离婚！"));
         }else if(status == MarryStatus.DIVORCED){
             ResponseMessageQueue.getInstance().put(
                _player.getMsgQueueIndex(),
                new Warning("你还没有订婚或已离婚！"));
         }
        return false;
    }

    /**
     *  结婚双方跳转到婚礼礼堂地图副本
     * @param _player 第一个进入礼堂副本的玩家 ,当第一个进入时，参数 _other=null
     * @param _other  第二个进入礼堂副本的玩家
     * @return
     */
    private static boolean playerGoTOMarryMap(HeroPlayer _player, HeroPlayer _other) {
        log.debug("goto MarryMap 。。。now player [ "+_player.getName()+" ] mapid= "+_player.where().getID());
        try{
//            Map currentMap = _player.where();
            short mapid = _player.getClan()==EClan.LONG_SHAN?marryMapId:marryMapId2;

            Map targetMap = MapServiceImpl.getInstance().getNormalMapByID(mapid);
            if (null == targetMap)
            {
                log.error("不存在婚礼礼堂地图，ID:" + mapid);
                return false;
            }

            DungeonServiceImpl.getInstance().marryerGotoMarryDungeon(_player,_other,mapid);

            log.debug("goto MarryMap success now player [ "+_player.getName()+" ] mapid= "+_player.where().getID());

            return true;
        }catch(Exception e){
            log.error("玩家"+ _player.getName() +"进入礼堂地图 error : ", e);
            return false;
        }
    }

    /**
     * 如果是结婚中的一方退出礼堂，则把礼堂中的所有玩家都传送到月老地图
     * 且不能再进入礼堂
     * @param player
     */
    public static void loverExitMarryMap(HeroPlayer player){
        if(player.getUserID() == marryer[0] || player.getUserID() == marryer[1]){
            removeAllPlayer(player.getClan().getID());
        }
    }

    /**
     * 传送婚礼礼堂里的所有玩家到月老地图
     */
    public static void removeAllPlayer(short clan){
        MarryNPC.canEntry = false;
        ArrayList<HeroPlayer> playerList = new ArrayList<HeroPlayer>();

        Map map = MapServiceImpl.getInstance().getNormalMapByID(MarryNPC.marryMapId);
        Map marryNPCMap = MapServiceImpl.getInstance().getNormalMapByID(MarryNPC.marryNPCMapId);

        if(clan == EClan.LONG_SHAN.getID()){
            map = MapServiceImpl.getInstance().getNormalMapByID(MarryNPC.marryMapId);
            marryNPCMap = MapServiceImpl.getInstance().getNormalMapByID(MarryNPC.marryNPCMapId);
        }else{
            map = MapServiceImpl.getInstance().getNormalMapByID(MarryNPC.marryMapId2);
            marryNPCMap = MapServiceImpl.getInstance().getNormalMapByID(MarryNPC.marryNPCMapId2);
        }

//        Map marryNPCMap = MapServiceImpl.getInstance().getNormalMapByID(MarryNPC.marryNPCMapId);
//        Map marryNPCMap2 = MapServiceImpl.getInstance().getNormalMapByID(MarryNPC.marryNPCMapId2);
        if(map.getPlayerList().size()>0){
            ME2ObjectList list = map.getPlayerList();
            if(null != list)
                for (ME2GameObject aList : list) {
                    HeroPlayer player = (HeroPlayer) aList;

                    log.debug("marryMap player : " + player.getName() +" , canRemoveAllFromMarryMap = " + player.canRemoveAllFromMarryMap);
                    playerList.add(player);
                }
        }
        for(HeroPlayer player : playerList){
            log.debug("player : "+player.getName() +" out MarryMap");

            ResponseMessageQueue.getInstance()
                        .put(player.getMsgQueueIndex(),
                                new Warning("结婚的玩家已经退出礼堂，婚礼结束！"));

            DungeonServiceImpl.getInstance().playerLeftDungeon(player);
            gotoMap(player,map,marryNPCMap);
        }

    }

    /**
     * 玩家进出礼堂
     * @param _player
     * @param _currMap
     * @param targetMap
     */
    private static void gotoMap(HeroPlayer _player, Map _currMap, Map targetMap){
        _player.setCellX(targetMap.getBornX());
            _player.setCellY(targetMap.getBornY());
            ResponseMessageQueue.getInstance().put(
                    _player.getMsgQueueIndex(),
                    new ResponseMapBottomData(_player, targetMap,
                            _currMap));
            ResponseMessageQueue.getInstance().put(
                    _player.getMsgQueueIndex(),
                    new ResponseSceneElement(
                            _player.getLoginInfo().clientType,
                            targetMap));
            ResponseMessageQueue.getInstance().put(
                    _player.getMsgQueueIndex(),
                    new ResponseMapGameObjectList(_player
                            .getLoginInfo().clientType, targetMap));
            TaskServiceImpl.getInstance().notifyMapNpcTaskMark(
                    _player, targetMap);
            //add by zhengl; date: 2011-03-24; note: 加载其他玩家和怪物的BUFF.
            EffectServiceImpl.getInstance().sendEffectList(_player, targetMap);
            /*OutMsgQ
                    .getInstance()
                    .put(
                            _player.getMsgQueueIndex(),
                            new ResponseMapMiniImage(
                                    _player.getLoginInfo().clientType,
                                    targetMap.getMiniImageID(),
                                    MiniMapImageDict
                                            .getInstance()
                                            .getImageBytes(
                                                    targetMap
                                                            .getMiniImageID())));*/

            if (targetMap.getAnimalList().size() > 0)
            {
                ResponseMessageQueue.getInstance().put(
                        _player.getMsgQueueIndex(),
                        new ResponseAnimalInfoList(targetMap));
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponsePetInfoList(_player));
            }

            ResponseMessageQueue.getInstance().put(
                    _player.getMsgQueueIndex(),
                    new ResponseMapElementList(_player
                            .getLoginInfo().clientType, targetMap));

            if (targetMap.getTaskGearList().size() > 0)
            {
                TaskServiceImpl.getInstance()
                        .notifyMapGearOperateMark(_player,
                                targetMap);
            }

            if (targetMap.getGroundTaskGoodsList().size() > 0)
            {
                TaskServiceImpl.getInstance()
                        .notifyGroundTaskGoodsOperateMark(_player,
                                targetMap);
            }

            if (targetMap.getBoxList().size() > 0)
            {
                ResponseMessageQueue.getInstance()
                        .put(
                                _player.getMsgQueueIndex(),
                                new ResponseBoxList(targetMap
                                        .getBoxList()));
            }

            GoodsServiceImpl.getInstance().sendLegacyBoxList(
                    targetMap, _player);

            _player.gotoMap(targetMap);
    }
}
