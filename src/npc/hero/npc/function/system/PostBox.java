package hero.npc.function.system;

import hero.charge.service.ChargeServiceImpl;
import hero.item.EquipmentInstance;
import hero.item.Goods;
import hero.item.bag.SingleGoodsBag;
import hero.item.bag.exception.BagException;
import hero.item.dictionary.GoodsContents;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.log.service.LogServiceImpl;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.BaseNpcFunction;
import hero.npc.function.ENpcFunctionType;
import hero.npc.function.system.postbox.Mail;
import hero.npc.function.system.postbox.MailService;
import hero.npc.message.NpcInteractiveResponse;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.MailStatusChanges;
import hero.share.message.Warning;
import hero.share.service.LogWriter;
import hero.share.service.Tip;
import hero.ui.UI_GoodsListWithOperation;
import hero.ui.UI_GridGoodsNumsChanged;
import hero.ui.UI_InputDigidal;
import hero.ui.UI_InputString;
import hero.ui.UI_MailGoodsList;
import hero.ui.UI_SelectOperation;
import hero.ui.message.NotifyListItemMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 PostBox.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-17 下午02:32:12
 * @描述 ：邮箱（寄、取钱、物品）
 */

public class PostBox extends BaseNpcFunction
{
    /**
     * 发送邮件需要的金币数
     */
    private final static int           MAIL_MONEY                   = 1;

    /**
     * 顶层操作菜单列表
     */
    private static final String[]      MAIN_MENU_LIST               = {
            "收信箱", "发信箱"                                          };

    /**
     * 顶层操作菜单图标列表
     */
    private static final short[]       MAIN_MENU_MARK_IMAGE_ID_LIST = {
            1009, 1009                                             };

    private static final String[]      MAIL_BOX_OPERTION_LIST       = {
            "查　　看", "上　　页", "下　　页", "收　　取", "回　　复", "删　　除"     };



    /**
     * 邮件系统
     */
    private static final String[]      SALE_MENU_LIST               = {
            "装备", "消耗", "材料", "金钱", "特殊"                 };
    /**
     * 邮件系统
     */
//    private static final String[]      SALE_MENU_LIST               = {
//    		  "装备", "货币", "药水", "材料", "特殊道具"                 };

    @SuppressWarnings("unchecked")
    private static ArrayList<byte[]>[] saleMenuOptionData           = new ArrayList[SALE_MENU_LIST.length];

    /**
     * 提示信息集合
     * 1:提示输入名称
     * 2:提示输入数量
     */
    @SuppressWarnings("unchecked")
    private static ArrayList<byte[]>[] saleSingleGoodsOptionData    = new ArrayList[Tip.FUNCTION_MAIL_SALE_OPERTION_LIST.length];

    /**
     * 输入玩家名称的提示.
     */
    @SuppressWarnings("unchecked")
    private static ArrayList<byte[]>[] saleEquipmentOptionData      = new ArrayList[Tip.FUNCTION_MAIL_SALE_OPERTION_LIST.length];

    private static final short         TITLE_LENGHT                 = 20;
    
    private static final short         CONTENT_LENGTH               = 200;
    
    private static final byte          OPTION_GET_ATTACHMENT        = 0;
    
    private static final byte          OPTION_PREC_PAGE             = 1;
    
    private static final byte          OPTION_NEXT_PAGE             = 2;
    
    private static final byte          OPTION_DELETE                = 3;
    
    private static final byte          OPTION_READ                  = 4;

    public PostBox(int _npcID)
    {
        super(_npcID);
        // TODO Auto-generated constructor stub
        ArrayList<byte[]> data = new ArrayList<byte[]>();
        byte[] inputString = UI_InputString.getBytes(Tip.FUNCTION_MAIL_NICKNAME);
        
        //非装备页签需要的数据
        data.add(UI_InputDigidal.getBytes(Tip.FUNCTION_MAIL_NUM));
        data.add(inputString);
        saleSingleGoodsOptionData[0] = data;

        //装备页签需要的文本数据
        data = new ArrayList<byte[]>();
        data.add(inputString);
        saleEquipmentOptionData[0] = data;

//        data = new ArrayList<byte[]>();
//        data.add(UI_InputDigidal.getBytes(MAIL_MONEY_TIP, 0, 1000000000));
//        data.add(inputString);
        saleMenuOptionData[0] = null;

        data = new ArrayList<byte[]>();
        data.add(UI_InputDigidal.getBytes(Tip.FUNCTION_MAIL_NUM, 0, 20));
        data.add(inputString);
        saleMenuOptionData[1] = null;//消耗
        
        data = new ArrayList<byte[]>();
        data.add(UI_InputDigidal.getBytes(Tip.FUNCTION_MAIL_NUM, 0, 20));
        data.add(inputString);
        saleMenuOptionData[2] = null;//材料
        
        data = new ArrayList<byte[]>();
        data.add(UI_InputDigidal.getBytes(Tip.FUNCTION_MAIL_MONEY, 0, 1000000000));
//        data.add( UI_InputString.getBytes(SALE_OPERTION_LIST[0]) );
        data.add(inputString);
        saleMenuOptionData[3] = data;//金钱
        
        data = new ArrayList<byte[]>();
        data.add(UI_InputDigidal.getBytes(Tip.FUNCTION_MAIL_NUM, 0, 20));
        data.add(inputString);
        saleMenuOptionData[4] = null;//特殊

    }

    @Override
    public ENpcFunctionType getFunctionType ()
    {
        // TODO Auto-generated method stub
        return ENpcFunctionType.POST_BOX;
    }

    /**
     * 操作步骤
     * @author Administrator
     *
     */
    enum Step
    {
    	/**
    	 * 请不要更改以下数值原始值.
    	 * 客户端会对下面值进行加减操作.
    	 */
    	//开启      //收取                      //删信                       //标记已读              //发送
        TOP(1), MAIL_BOX(10), MAIL_DEL(11), MAIL_READ(12), SEND_CATEGORY(20), 
        SEND_EQUIPMENT(21), SEND_XHDJ(22), SEND_CL(23), SEND_TSDJ(24);

        byte tag;

        Step(int _tag)
        {
            tag = (byte) _tag;
        }
    }

    @Override
    public void initTopLayerOptionList ()
    {
        // TODO Auto-generated method stub
        for (int i = 0; i < MAIN_MENU_LIST.length; i++)
        {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
//            data.miniImageID = MAIN_MENU_MARK_IMAGE_ID_LIST[i];
            data.miniImageID = getMinMarkIconID();
            data.optionDesc = MAIN_MENU_LIST[i];
            data.functionMark = getFunctionType().value()
                    * BaseNpcFunction.FUNCTION_EXPEND_MODULUS + i;
            optionList.add(data);
        }
    }

    /**
     * 返回一个小于等于_len长度的字符串
     * @param _len
     * @param _content
     * @return
     */
    private String stringLengthVerify (short _len, String _content)
    {
    	String result = "";
    	if(_content != null && _content.length() > _len) 
    	{
    		result = _content.substring(0, _len -1);
    	}
    	else 
    	{
    		result = _content;
		}
    	return result;
    }
    
    @Override
    public void process (HeroPlayer _player, byte _step, int _selectIndex,
            YOYOInputStream _content) throws Exception
    {
        if (Step.TOP.tag == _step)
        {
            if (0 == _selectIndex)// 收件箱
            {
                short page = 0;
                ResponseMessageQueue.getInstance().put(
                        _player.getMsgQueueIndex(),
                        new NpcInteractiveResponse(getHostNpcID(), 
                        		optionList.get(_selectIndex).functionMark,
                                Step.MAIL_BOX.tag, 
                                UI_MailGoodsList.getBytes(page, 
                                		MailService.getInstance().getMailList(_player.getUserID(), page),
                                        MAIL_BOX_OPERTION_LIST)
                                        )
                        );
            }
            else if (1 == _selectIndex)// 发送邮件类型列表
            {
                ResponseMessageQueue.getInstance().put(
                        _player.getMsgQueueIndex(),
                        new NpcInteractiveResponse(getHostNpcID(), optionList
                                .get(_selectIndex).functionMark,
                                Step.SEND_CATEGORY.tag, UI_SelectOperation
                                        .getBytes(Tip.FUNCTION_MAIL_CHOOSE, SALE_MENU_LIST,
                                                saleMenuOptionData)));
            }
        }
        else if (Step.MAIL_BOX.tag == _step)
        {
        	/**
        	 * optionIndex
        	 * 0=提取附件;1=翻上一页;2=翻下一页;3=删除选中邮件;4=把该邮件标记为已读
        	 */
            byte optionIndex = _content.readByte();
            int mailID = _content.readInt();
            short page = _content.readShort();
            
            if (optionIndex == OPTION_GET_ATTACHMENT)
            {
                Mail mail = MailService.getInstance().getMail(_player.getUserID(), mailID);
                if (mail != null)
                {
                	if(mail.getMoney() <= 0 && mail.getEquipment() == null 
                			&& mail.getSingleGoods() == null) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_NPC_MAIL_NOT_ATTACHMENT));
                        return;
                	}
                	boolean addFinish = false;
                    if (mail.getType() == Mail.TYPE_OF_MONTY)
                    {
                    	addFinish = PlayerServiceImpl.getInstance().addMoney(_player, 
                    			mail.getMoney(), 1,
                                PlayerServiceImpl.MONEY_DRAW_LOCATION_WARNING, "提取附件金币");
                    }
                    else if (mail.getType() == Mail.TYPE_OF_GAME_POINT)
                    {
                    	addFinish = ChargeServiceImpl.getInstance().updatePointAmount(_player, 
                    			mail.getGamePoint());
                    }
                    else
                    {
                        short[] addSuccessful = null;

                        if (mail.getType() == Mail.TYPE_OF_EQUIPMENT)
                        {
                            addSuccessful = GoodsServiceImpl.getInstance().addEquipmentInstance2Bag(
                            		_player, mail.getEquipment(), CauseLog.MAIL );
                        }
                        else
                        {
                            Goods goods = mail.getSingleGoods();
                            addSuccessful = GoodsServiceImpl.getInstance().addGoods2Package(
                            		_player, goods, mail.getSingleGoodsNumber(), CauseLog.MAIL );
                        }
                        if (null == addSuccessful && addFinish) {
                        	return;
                        }
                    }
//                    OutMsgQ.getInstance().put(_player.getMsgQueueIndex(),
//                            new NotifyListItemMessage(_step, false, mailID));
                    MailService.getInstance().removeAttachment(_player.getUserID(), mailID);
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new MailStatusChanges( MailStatusChanges.TYPE_OF_POST_BOX, false) );
                }
                else
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_NPC_MAIL_NOT_EXITS));
                }
            }
            else if (optionIndex == OPTION_PREC_PAGE)
            {
                if (page == 0)
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_NPC_NOT_PREC_PAGE));
                    return;
                }
                page--;
                List<Mail> mailsList = MailService.getInstance().getMailList(
                        _player.getUserID(), page);
                if (mailsList.size() == 0)
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_NPC_NOT_PREC_PAGE));
                }
                else
                {
                    NpcInteractiveResponse msg = new NpcInteractiveResponse(
                            getHostNpcID(),
                            optionList.get(_selectIndex).functionMark,
                            Step.MAIL_BOX.tag, UI_MailGoodsList.getBytes(page,
                                    mailsList, MAIL_BOX_OPERTION_LIST));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
                }
            }
            else if (optionIndex == OPTION_NEXT_PAGE)
            {
                page++;
                List<Mail> mailsList = MailService.getInstance().getMailList(
                        _player.getUserID(), page);
                if (mailsList.size() == 0)
                {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_NPC_NOT_NEXT_PAGE));
                }
                else
                {
                    NpcInteractiveResponse msg = new NpcInteractiveResponse(
                            getHostNpcID(),
                            optionList.get(_selectIndex).functionMark,
                            Step.MAIL_BOX.tag, UI_MailGoodsList.getBytes(page,
                                    mailsList, MAIL_BOX_OPERTION_LIST));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
                }
            }
            else if (optionIndex == OPTION_DELETE)
            {
                boolean result = MailService.getInstance().removeMail(_player.getUserID(), mailID);
                if(result) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(Tip.TIP_NPC_MAIL_DEL_FINISH));
                }
            }
            else if (optionIndex == OPTION_READ)
            {
            	MailService.getInstance().readMail(_player.getUserID(), mailID);
//            	OutMsgQ.getInstance().put(_player.getMsgQueueIndex(), new Warning(MAIL_DEL_FINISH));
            }
        }
        else if (Step.SEND_CATEGORY.tag == _step)
        {
        	//发送
            byte index = _content.readByte();
            NpcInteractiveResponse msg = null;

            if (index == 3)
            {
                // 发送货币
                int money = _content.readInt();
                String nickname = _content.readUTF();
            	String title = stringLengthVerify(TITLE_LENGHT, _content.readUTF());
            	if(title == "") {
            		title = Tip.FUNCTION_MAIL_DEFAULT_NEW_TITLE;
            	}
            	String content = this.stringLengthVerify(CONTENT_LENGTH, _content.readUTF());
                addNewMail(_player, Mail.TYPE_OF_MONTY, _step, (byte) 1, money,
                        1, nickname, title, content);

                return;
            }
            else if (index == 5)
            {
                // 发送纯文本邮件
                String nickname = _content.readUTF();
                String title = this.stringLengthVerify(TITLE_LENGHT, _content.readUTF());
            	if(title == "") {
            		title = Tip.FUNCTION_MAIL_DEFAULT_NEW_TITLE;
            	}
                String content = stringLengthVerify(CONTENT_LENGTH, _content.readUTF());
                addNewMail(_player, Mail.TYPE_OF_TXT, _step, (byte) 1,
                        0, 1, nickname, title, content);
                return;
            }
            else if (index == 6)
            {
                // 发送游戏点数
                int gamePoint = _content.readInt();
                String nickname = _content.readUTF();
                String title = this.stringLengthVerify(TITLE_LENGHT, _content.readUTF());
            	if(title == "") {
            		title = Tip.FUNCTION_MAIL_DEFAULT_NEW_TITLE;
            	}
                String content = stringLengthVerify(CONTENT_LENGTH, _content.readUTF());
                addNewMail(_player, Mail.TYPE_OF_GAME_POINT, _step, (byte) 1,
                        gamePoint, 1, nickname, title, content);
                return;
            }
            else if (index == 0)
            {
                // 查看装备
                msg = new NpcInteractiveResponse(getHostNpcID(), optionList
                        .get(1).functionMark, Step.SEND_EQUIPMENT.tag,
                        UI_GoodsListWithOperation.getData(Tip.FUNCTION_MAIL_SALE_OPERTION_LIST,
                                saleEquipmentOptionData, 
                                _player.getInventory().getEquipmentBag(), 
                                GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name));
            }
            else if (index == 1)
            {
                // 查看药水
                msg = new NpcInteractiveResponse(getHostNpcID(), optionList
                        .get(1).functionMark, Step.SEND_XHDJ.tag,
                        UI_GoodsListWithOperation.getBytes(
                        		Tip.FUNCTION_MAIL_SALE_OPERTION_LIST,
                                saleSingleGoodsOptionData, 
                                _player.getInventory().getMedicamentBag(), 
                                GoodsServiceImpl.getInstance().getConfig().medicament_bag_tab_name));
            }
            else if (index == 2)
            {
                // 查看材料
                msg = new NpcInteractiveResponse(getHostNpcID(), optionList
                        .get(1).functionMark, Step.SEND_CL.tag,
                        UI_GoodsListWithOperation.getBytes(Tip.FUNCTION_MAIL_SALE_OPERTION_LIST,
                                saleSingleGoodsOptionData, 
                                _player.getInventory().getMaterialBag(), 
                                GoodsServiceImpl.getInstance().getConfig().material_bag_tab_name));
            }
            else if (index == 4)
            {
                // 查看特殊道具
                msg = new NpcInteractiveResponse(
                		getHostNpcID(), 
                		optionList.get(1).functionMark, 
                		Step.SEND_TSDJ.tag,
                        UI_GoodsListWithOperation.getBytes(
                        		Tip.FUNCTION_MAIL_SALE_OPERTION_LIST,
                                saleSingleGoodsOptionData, 
                                _player.getInventory().getSpecialGoodsBag(), 
                                GoodsServiceImpl.getInstance().getConfig().special_bag_tab_name));
            }
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
        }
        else if (Step.SEND_EQUIPMENT.tag == _step)
        {
        	//邮寄装备类型物品
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int num = _content.readInt();
            String nickname = _content.readUTF();
            String title = this.stringLengthVerify(TITLE_LENGHT, _content.readUTF());
        	if(title == "") {
        		title = Tip.FUNCTION_MAIL_DEFAULT_NEW_TITLE;
        	}
            String content = stringLengthVerify(CONTENT_LENGTH, _content.readUTF());
            addNewMail(_player, Mail.TYPE_OF_EQUIPMENT, _step, gridIndex,
                    goodsID, num, nickname, title, content);
        }
        else if (Step.SEND_XHDJ.tag == _step)
        {
        	//邮寄消耗类型物品
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int nums = _content.readInt();
            String nickname = _content.readUTF();
            String title = this.stringLengthVerify(TITLE_LENGHT, _content.readUTF());
        	if(title == "") {
        		title = Tip.FUNCTION_MAIL_DEFAULT_NEW_TITLE;
        	}
            String content = stringLengthVerify(CONTENT_LENGTH, _content.readUTF());
            addNewMail(_player, Mail.TYPE_OF_SINGLE_GOODS, _step, gridIndex,
                    goodsID, nums, nickname, title, content);
        }
        else if (Step.SEND_CL.tag == _step)
        {
        	//邮寄CL类型物品
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int nums = _content.readInt();
            String nickname = _content.readUTF();
            String title = this.stringLengthVerify(TITLE_LENGHT, _content.readUTF());
        	if(title == "") {
        		title = Tip.FUNCTION_MAIL_DEFAULT_NEW_TITLE;
        	}
            String content = stringLengthVerify(CONTENT_LENGTH, _content.readUTF());
            addNewMail(_player, Mail.TYPE_OF_SINGLE_GOODS, _step, gridIndex,
                    goodsID, nums, nickname, title, content);
        }
        else if (Step.SEND_TSDJ.tag == _step)
        {
        	//邮寄TSDJ类型物品
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int nums = _content.readInt();
            String nickname = _content.readUTF();
            String title = this.stringLengthVerify(TITLE_LENGHT, _content.readUTF());
        	if(title == "") {
        		title = Tip.FUNCTION_MAIL_DEFAULT_NEW_TITLE;
        	}
            String content = stringLengthVerify(CONTENT_LENGTH, _content.readUTF());
            addNewMail(_player, Mail.TYPE_OF_SINGLE_GOODS, _step, gridIndex,
                    goodsID, nums, nickname, title, content);
        }
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList (
            HeroPlayer _player)
    {
        // TODO Auto-generated method stub
        return optionList;
    }

    private boolean hasMoney (HeroPlayer _player, byte _mailType, int _goodsID)
    {
        int needMoney = MAIL_MONEY;
        if (_mailType == Mail.TYPE_OF_MONTY)
        {
            needMoney += _goodsID;
        }
        return _player.getMoney() >= needMoney;
    }

    /**
     * 发送新邮件
     * 
     * @param _player 发送者
     * @param _gridIndex 背包中的索引
     * @param _goodsID 发送物品ID，如果发送的是金币则是金币数
     * @param _number 发送物品数量，如果是装备则是装备的强化等级
     * @param _nickname 接收者昵称
     */
    private void addNewMail (HeroPlayer _player, byte _mailType, byte _step,
            byte _gridIndex, int _goodsID, int _number, String _nickname, String _title, String _content)
    {
        HeroPlayer receiver = PlayerServiceImpl.getInstance().getPlayerByName(
                _nickname);
        int userID = 0;
        Date date = new Date(System.currentTimeMillis());
        //add by zhengl; date: 2011-02-14; note: 获得社交关系
        byte social = MailService.getInstance().getSocial(_player.getName(), _nickname);

        if (receiver == null)
        {
            userID = PlayerServiceImpl.getInstance().getUserIDByNameFromDB(_nickname);

            if (userID == 0)
            {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new Warning(Tip.TIP_NPC_MAIL_PLAYER + _nickname + Tip.TIP_NPC_MAIL_PLAYER_NOT_EXITS));

                return;
            }
        }
        else
        {
            userID = receiver.getUserID();
        }

        boolean receiverPostBoxFull = false;

        if (_mailType == Mail.TYPE_OF_MONTY)
        {
            if (_goodsID <= _player.getMoney())
            {
                Mail mail = new Mail(MailService.getInstance()
                        .getUseableMailID(), userID, _nickname, _player
                        .getName(), Mail.TYPE_OF_MONTY, _goodsID, _content, _title, date, social);

                if (MailService.getInstance().addMail(mail, false))
                {
                    PlayerServiceImpl.getInstance().addMoney(_player,
                            -_goodsID, 1,
                            PlayerServiceImpl.MONEY_DRAW_LOCATION_NONE, "邮寄金币");

                    // 邮件发送日志
                    LogServiceImpl.getInstance().mailLog(
                            _player.getLoginInfo().accountID,
                            _player.getUserID(), _player.getName(),
                            _player.getLoginInfo().loginMsisdn, mail.getID(),
                            userID, _nickname, _goodsID, 0, "");
                }
                else
                {
                    receiverPostBoxFull = true;
                }
            }
            else
            {
                // 玩家金钱不够
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new Warning(Tip.TIP_NPC_MAIL_MONEY_NOT_ENOUGH));

                return;
            }
        }
        else if (_mailType == Mail.TYPE_OF_TXT)
        {
            Mail mail = new Mail(MailService.getInstance()
                    .getUseableMailID(), userID, _nickname, _player
                    .getName(), Mail.TYPE_OF_TXT, _goodsID, _content, _title, date, social);

            if (MailService.getInstance().addMail(mail, false))
            {
                // 邮件发送日志
                LogServiceImpl.getInstance().mailLog(
                        _player.getLoginInfo().accountID,
                        _player.getUserID(), _player.getName(),
                        _player.getLoginInfo().loginMsisdn, mail.getID(),
                        userID, _nickname, 0, _goodsID, "");
            }
            else
            {
                receiverPostBoxFull = true;
            }
        }
        else if (_mailType == Mail.TYPE_OF_GAME_POINT)
        {
            if (_goodsID <= _player.getChargeInfo().pointAmount)
            {
                Mail mail = new Mail(MailService.getInstance()
                        .getUseableMailID(), userID, _nickname, _player
                        .getName(), Mail.TYPE_OF_GAME_POINT, _goodsID, _content, _title, date, social);

                if (MailService.getInstance().addMail(mail, false))
                {
                    ChargeServiceImpl.getInstance().updatePointAmount(_player,
                            -_goodsID);

                    // 邮件发送日志
                    LogServiceImpl.getInstance().mailLog(
                            _player.getLoginInfo().accountID,
                            _player.getUserID(), _player.getName(),
                            _player.getLoginInfo().loginMsisdn, mail.getID(),
                            userID, _nickname, 0, _goodsID, "");
                }
                else
                {
                    receiverPostBoxFull = true;
                }
            }
            else
            {
                // 玩家游戏点数不够
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new Warning(Tip.TIP_NPC_MAIL_POINT_AMOUNT_NOT_ENOUGH));

                return;
            }
        }
        else
        {
            try
            {
                int maxNum = 1;

                if (Step.SEND_EQUIPMENT.tag == _step)
                {
                    EquipmentInstance ei = _player.getInventory()
                            .getEquipmentBag().getEquipmentList()[_gridIndex];

                    if (null != ei && ei.getInstanceID() == _goodsID)
                    {
                        if (ei.getArchetype().exchangeable())
                        {
                            if (-1 != GoodsServiceImpl.getInstance()
                                    .removeEquipmentOfBag(
                                            _player,
                                            _player.getInventory()
                                                    .getEquipmentBag(), ei,
                                            CauseLog.MAIL))
                            {
                                Mail mail = new Mail(MailService.getInstance()
                                        .getUseableMailID(), userID, _nickname,
                                        _player.getName(),
                                        Mail.TYPE_OF_EQUIPMENT, 0, (short) 0,
                                        ei, _content, _title, date, social);

                                if (MailService.getInstance().addMail(mail,
                                        false))
                                {
                                    ResponseMessageQueue
                                            .getInstance()
                                            .put(
                                                    _player.getMsgQueueIndex(),
                                                    new NpcInteractiveResponse(
                                                            getHostNpcID(),
                                                            optionList.get(1).functionMark,
                                                            _step,
                                                            UI_GridGoodsNumsChanged
                                                                    .getBytes(
                                                                            _gridIndex,
                                                                            _goodsID,
                                                                            maxNum
                                                                                    - _number)));

                                    // 邮件发送日志
                                    LogServiceImpl.getInstance().mailLog(
                                            _player.getLoginInfo().accountID,
                                            _player.getUserID(),
                                            _player.getName(),
                                            _player.getLoginInfo().loginMsisdn,
                                            mail.getID(),
                                            userID,
                                            _nickname,
                                            0,
                                            0,
                                            _goodsID
                                                    + ","
                                                    + ei.getArchetype()
                                                            .getName() + ",1");
                                }
                                else
                                {
                                    receiverPostBoxFull = true;
                                }
                            }
                        }
                        else
                        {
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning(Tip.TIP_NPC_MAIL_NO_EXCHANGEABLE));

                            return;
                        }
                    }
                }
                else
                {
                    Goods _goods = GoodsContents.getGoods(_goodsID);
                    SingleGoodsBag goodsBag = null;

                    if (!_goods.exchangeable())
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_NPC_MAIL_NO_EXCHANGEABLE));

                        return;
                    }
                    else
                    {
                        if (Step.SEND_XHDJ.tag == _step)
                        {
                            maxNum = _player.getInventory().getMedicamentBag()
                                    .getAllItem()[_gridIndex][1];
                            goodsBag = _player.getInventory()
                                    .getMedicamentBag();
                        }
                        else if (Step.SEND_CL.tag == _step)
                        {
                            maxNum = _player.getInventory().getMaterialBag()
                                    .getAllItem()[_gridIndex][1];
                            goodsBag = _player.getInventory().getMaterialBag();
                        }
                        else if (Step.SEND_TSDJ.tag == _step)
                        {
                            maxNum = _player.getInventory()
                                    .getSpecialGoodsBag().getAllItem()[_gridIndex][1];
                            goodsBag = _player.getInventory()
                                    .getSpecialGoodsBag();
                        }

                        Mail mail = new Mail(MailService.getInstance()
                                .getUseableMailID(), userID, _nickname, _player
                                .getName(), Mail.TYPE_OF_SINGLE_GOODS, _goods
                                .getID(), (short) _number, null, _content, _title, date, social);

                        if (MailService.getInstance().addMail(mail, false))
                        {
                            ResponseMessageQueue
                                    .getInstance()
                                    .put(
                                            _player.getMsgQueueIndex(),
                                            new NpcInteractiveResponse(
                                                    getHostNpcID(),
                                                    optionList.get(1).functionMark,
                                                    _step,
                                                    UI_GridGoodsNumsChanged
                                                            .getBytes(
                                                                    _gridIndex,
                                                                    _goodsID,
                                                                    maxNum
                                                                            - _number)));

                            GoodsServiceImpl.getInstance().reduceSingleGoods(
                                    _player, goodsBag, _gridIndex, _goodsID,
                                    _number, CauseLog.MAIL);

                            // 邮件发送日志
                            LogServiceImpl.getInstance().mailLog(
                                    _player.getLoginInfo().accountID,
                                    _player.getUserID(),
                                    _player.getName(),
                                    _player.getLoginInfo().loginMsisdn,
                                    mail.getID(),
                                    userID,
                                    _nickname,
                                    0,
                                    0,
                                    _goodsID + "," + _goods.getName() + ","
                                            + _number);
                        }
                    }
                }
            }
            catch (BagException e)
            {
                e.printStackTrace();
                return;
            }
        }

        if (receiverPostBoxFull)
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(Tip.TIP_NPC_MAIL_PLAYER + _nickname + Tip.TIP_NPC_MAIL_BOX_FULL));
        }
        else
        {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new Warning(Tip.TIP_NPC_MAIL_SEND_SUCCESS));

            if (receiver != null && receiver.isEnable())
            {
                ResponseMessageQueue.getInstance().put(receiver.getMsgQueueIndex(),
                        new Warning(Tip.TIP_NPC_MAIL_GET_NEW));

                ResponseMessageQueue.getInstance().put(
                        receiver.getMsgQueueIndex(),
                        new MailStatusChanges(
                                MailStatusChanges.TYPE_OF_POST_BOX, true));
            }
        }
    }
}
