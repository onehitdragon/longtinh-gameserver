package hero.npc.function.system.postbox;

import java.util.Date;

import hero.item.EquipmentInstance;
import hero.item.SingleGoods;
import hero.item.dictionary.GoodsContents;

/**
 * 邮件类，可以发送物品、游戏币、计费点数 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class Mail
{
    /**
     * 邮件ID
     */
    private int               id;

    /**
     * 接收人编号
     */
    private int               receiverUserID;

    /**
     * 接收人
     */
    private String            receiverName;

    /**
     * 发送者
     */
    private String            senderName;
    
    /**
     * <p>发件人与收件人的社交关系(是他什么人,朋友还是同僚)<p>
     * 0好友、1工会、2系统公告、3其他玩家
     */
    private byte              social;

    /**
     * 邮件内容类型
     */
    private byte              contentType;

    /**
     * 游戏币
     */
    private int               money;

    /**
     * 计费点数
     */
    private int               gamePoint;

    /**
     * 非装备物品
     */
    private SingleGoods       singleGoods;

    /**
     * 非装备物品数量
     */
    private short             singleGoodsNumber;
    
    /**
     * 邮件内容
     */
    private String            content;
    /**
     * 邮件标题
     */
    private String            title;

    /**
     * 装备实例
     */
    private EquipmentInstance equipment;

    /**
     * 已读
     */
    private boolean           readFinish;
    
    /**
     * 附件是否还存在
     */
    private boolean           attachment;
    
    private Date              date;

    /**
     * 构造（游戏币，游戏点数）
     * 
     * @param _id 编号
     * @param _receiver 接受者
     * @param _sender 发送者
     * @param _type 类型
     * @param _numeric 数值
     */
    public Mail(int _id, int _receiver_uid, String _receiver, String _sender,
            byte _type, int _numeric, String _content, String _title, Date _date, byte _social)
    {
    	readFinish = false;
    	attachment = false;
        id = _id;
        receiverUserID = _receiver_uid;
        receiverName = _receiver;
        senderName = _sender;
        contentType = _type;
        content = _content;
        title = _title;
        date = _date;
        social = _social;
        
        if(content == null) {
        	content = "";
        }

        switch (contentType)
        {
            case Mail.TYPE_OF_MONTY:
            {
                money = _numeric;
                attachment = true;
                break;
            }
            case Mail.TYPE_OF_GAME_POINT:
            {
                gamePoint = _numeric;
                attachment = true;
                break;
            }
        }
    }

    /**
     * 构造
     * 
     * @param _id
     * @param _receiver
     * @param _sender
     * @param _instance
     */
    public Mail(int _id, int _receiver_uid, String _receiver, String _sender,
            byte _type, int _singleGoodsID, short _singleGoodsNumber,
            EquipmentInstance _equipment, String _content, String _title, Date _date, byte _social)
    {
    	readFinish = false;
    	attachment = false;
        id = _id;
        receiverUserID = _receiver_uid;
        receiverName = _receiver;
        senderName = _sender;
        contentType = _type;
        content = _content;
        title = _title;
        date = _date;
        social = _social;
        
        if(content == null) {
        	content = "";
        }

        switch (contentType)
        {
            case Mail.TYPE_OF_SINGLE_GOODS:
            {
                singleGoods = (SingleGoods) GoodsContents
                        .getGoods(_singleGoodsID);
                singleGoodsNumber = _singleGoodsNumber;
                attachment = true;
                break;
            }
            case Mail.TYPE_OF_EQUIPMENT:
            {
                equipment = _equipment;
                attachment = true;
                break;
            }
        }
    }

    /**
     * 获取编号
     * 
     * @return
     */
    public int getID ()
    {
        return id;
    }

    public int getReceiverUserID ()
    {
        return receiverUserID;
    }

    /**
     * 获取接受者姓名
     * 
     * @return
     */
    public String getReceiverName ()
    {
        return receiverName;
    }

    /**
     * 获取发送者姓名
     * 
     * @return
     */
    public String getSender ()
    {
        return senderName;
    }

    /**
     * 获取邮件类型
     * 
     * @return
     */
    public byte getType ()
    {
        return contentType;
    }

    /**
     * 获取游戏币
     * 
     * @return
     */
    public int getMoney ()
    {
        return money;
    }

    /**
     * 获取计费点数
     * 
     * @return
     */
    public int getGamePoint ()
    {
        return gamePoint;
    }

    /**
     * 获取非装备物品
     * 
     * @return
     */
    public SingleGoods getSingleGoods ()
    {
        return singleGoods;
    }

    /**
     * 获取非装备物品数量
     * 
     * @return
     */
    public short getSingleGoodsNumber ()
    {
        return singleGoodsNumber;
    }
    
    /**
     * 邮件日期
     * @return
     */
    public Date getDate () 
    {
    	return this.date;
    }

    /**
     * 获取装备
     * 
     * @return
     */
    public EquipmentInstance getEquipment ()
    {
        return equipment;
    }
    /**
     * 获得邮件正文
     * @return
     */
    public String getContent ()
    {
    	return content;
    }
    /**
     * 获得邮件标题
     * @return
     */
    public String getTitle ()
    {
    	return title;
    }
    
    public void readMail() {
    	this.readFinish = true;
    }
    
    /**
     * 邮件是否已读
     * @return
     */
    public boolean getReadFinish() {
    	return this.readFinish;
    }
    
    /**
     * 是否还有附件
     * @return
     */
    public boolean attachmentMail() {
    	return this.attachment;
    }
    /**
     * 获得发件人与收件人的社交关系
     * @return
     */
    public byte getSocial() {
    	return this.social;
    }
    
    /**
     * 提取附件.
     */
    public void removeAttachment () 
    {
    	this.gamePoint = 0;
    	this.money = 0;
    	this.singleGoods = null;
    	this.singleGoodsNumber = 0;
    	this.equipment = null;
    	this.attachment = false;
    	this.readFinish = true;
    	this.contentType = Mail.TYPE_OF_TXT; //提取附件之后邮件变成了纯文本邮件.
    }

    //-----------
    //邮件原始类型
    //-----------
    /**
     * 邮件类型－游戏币
     */
    public static final byte TYPE_OF_MONTY        = 0;

    /**
     * 邮件类型－计费点数
     */
    public static final byte TYPE_OF_GAME_POINT   = 1;

    /**
     * 邮件类型－非装备物品
     */
    public static final byte TYPE_OF_SINGLE_GOODS = 2;

    /**
     * 邮件类型－装备
     */
    public static final byte TYPE_OF_EQUIPMENT    = 3;
    
    /**
     * 邮件类型－纯文本
     */
    public static final byte TYPE_OF_TXT          = 4;
}
