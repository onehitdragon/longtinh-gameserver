package hero.share.letter;

/**
 * 玩家信件类 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class Letter implements Comparable
{
    /**
     * 信件ID
     */
    public int     letterID;

    /**
     * 类型 0:系统邮件  1:普通邮件
     */
    public byte type;

    /**
     * 标题
     */
    public String  title;

    /**
     * 邮寄人
     * 如果是系统发邮件，则名称里“系统”两个字必须要，客户端需要它来判断图标类型
     */
    public String  senderName;
    
    /**
     * 收信人userID
     */
    public int receiverUserID;

    /**
     * 收信人
     */
    public String  receiverName;

    /**
     * 内容
     */
    public String  content;

    /**
     * 发送时间
     */
    public long    sendTime;

    /**
     * 信件是否已经读过
     */
    public boolean isRead;

    /**
     * 信件是否已经保存
     */
    public boolean isSave;

    /**
     * @param _letterID
     * @param _title
     * @param _sender
     * @param _receiver_uid
     * @param _receiver
     * @param _content
     */
    public Letter(int _letterID, String _title, String _sender,int _receiver_uid,
            String _receiver, String _content)
    {
        type = 1;
        letterID = _letterID;
        title = _title;
        senderName = _sender;
        receiverUserID = _receiver_uid;
        receiverName = _receiver;
        content = _content;
        sendTime = System.currentTimeMillis();
        isRead = false;
        isSave = false;
    }

    public Letter(byte _type,int _letterID, String _title, String _sender,int _receiver_uid,
            String _receiver, String _content)
    {
        type = _type;
        letterID = _letterID;
        title = _title;
        senderName = _sender;
        receiverUserID = _receiver_uid;
        receiverName = _receiver;
        content = _content;
        sendTime = System.currentTimeMillis();
        isRead = false;
        isSave = false;
    }
    
    public Letter(){}

    /**
     * 系统邮件类型
     */
    public static final byte SYSTEM_TYPE = 0;
    /**
     * 普通邮件类型
     */
    public static final byte GENERIC_TYPE = 1;

    @Override
    public int compareTo(Object o) {
        Letter l = (Letter)o;
        if(l.type < l.type){
            return 1;
        }
        return 0;
    }
}
