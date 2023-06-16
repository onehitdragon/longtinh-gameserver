package hero.npc.function.system.postbox;

import hero.guild.service.GuildServiceImpl;
import hero.item.EquipmentInstance;
import hero.item.enhance.EnhanceService;
import hero.item.service.EquipmentFactory;
import hero.social.service.SocialServiceImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import yoyo.service.tools.database.DBServiceImpl;


public class MailService
{
    private int                               mailID;

    private static MailService                instance;

    /**
     * 邮箱最大可以存放的邮件数
     */
    public static final int                   MAX_MAIL_SIZE          = 30;

    private HashMap<Integer, ArrayList<Mail>> mailDict;

    private static final String               SEL_MAXID_SQL          = "SELECT MAX(mail_id) FROM mail";

    private static final String               SELECT_MAIL_NUMBER_SQL = "SELECT COUNT(*) AS number FROM mail WHERE receiver_uid = ?";

    private static final String               INSERT_NEW_MAIL        = "INSERT INTO mail VALUE (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String               DEL_MAIL               = "DELETE FROM mail WHERE mail_id = ? LIMIT 1";
    
    private static final String               UPDATE_MAIL            = "update mail set type=4,money=0,freight_point=0,goods_id=0,number=0,read_finish=1 WHERE mail_id = ? LIMIT 1";
    
    private static final String               UPDATE_MAIL_READFINISH = "update mail set read_finish=1 WHERE mail_id = ? LIMIT 1";

    private static final String               DELETE_ALL_MAIL        = "DELETE FROM mail WHERE receiver_uid = ?";

    private static final String               SELECT_MAILS           = "SELECT * FROM mail left join"
                                                                             + " equipment_instance ON"
                                                                             + " mail.goods_id=equipment_instance.instance_id"
                                                                             + " WHERE mail.receiver_uid = ?";

    /**
     * 每一页显示的邮件数
     */
    private static final int                  PAGE_NUM               = 10;

    private ReentrantLock                     lock                   = new ReentrantLock();

    private MailService()
    {
        mailDict = new HashMap<Integer, ArrayList<Mail>>();
        loadMaxMailID();
    }

    public static MailService getInstance ()
    {
        if (instance == null)
            instance = new MailService();
        return instance;
    }

    private void loadMaxMailID ()
    {
        mailID = 0;
        Connection conn = null;
        Statement stm = null;
        ResultSet rs = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            stm = conn.createStatement();
            rs = stm.executeQuery(SEL_MAXID_SQL);
            if (rs.next())
            {
                mailID = rs.getInt(1);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                    rs = null;
                }
                if (stm != null)
                {
                    stm.close();
                    stm = null;
                }
                if (conn != null)
                {
                    conn.close();
                    conn = null;
                }
            }
            catch (SQLException e)
            {
            }
        }

    }

    /**
     * 玩家登陆时调用，加载玩家的邮件
     * 
     * @param _userID
     */
    public void loadMail (int _userID)
    {
        if (mailDict.get(_userID) == null)
        {
            mailDict.put(_userID, loadMailFromDB(_userID));
        }
    }

    /**
     * 玩家离线超时时调用，将内存中玩家邮件数据清除
     * 
     * @param _userID
     */
    public void clear (int _userID)
    {
        mailDict.remove(_userID);
    }

    /**
     * 得到一个可用的mailID
     * 
     * @return
     */
    public int getUseableMailID ()
    {
        try
        {
            lock.lock();

            return ++mailID;
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * 添加一份邮件
     * 
     * @param _mail
     * @param _isSystemMail
     * @return
     */
    public boolean addMail (Mail _mail, boolean _isSystemMail)
    {
        ArrayList<Mail> mailList = mailDict.get(_mail.getReceiverUserID());

        if (mailList != null)
        {
            if (mailList.size() >= MAX_MAIL_SIZE)
            {
                return false;
            }
        }

        if (insertMailToDB(_mail, _isSystemMail))
        {
            if (mailList != null)
            {
                mailList.add(_mail);
            }

            return true;
        }

        return false;
    }

    /**
     * 得到玩家的邮件列表
     * 
     * @param _userID
     * @param _page
     * @return
     */
    public List<Mail> getMailList (int _userID, short _page)
    {
        try
        {
            lock.lock();
            ArrayList<Mail> mailList = mailDict.get(_userID);
            //edit by zhengl; date: 2011-02-13; note: 添加邮件排序功能
            try {
            	mailList = getSortMails(mailList);
			} catch (Exception e) {
				e.printStackTrace();
			}

            if (mailList == null || mailList.size() <= _page * PAGE_NUM)
                return null;
            int begin = _page * PAGE_NUM;
            int end = (_page + 1) * PAGE_NUM <= mailList.size() ? (_page + 1)
                    * PAGE_NUM : mailList.size();

            return mailList.subList(begin, end);
        }
        finally
        {
            lock.unlock();
        }
    }
    
    /**
     * <p>邮件排序</p>
     * add by zhengl; date: 2011-02-13;
     * @param list
     * @return
     */
    private ArrayList<Mail> getSortMails (ArrayList<Mail> list)
    {
    	ArrayList<Mail> mailList = list;
    	ArrayList<Mail> oldList = new ArrayList<Mail>();
    	ArrayList<Mail> newList = new ArrayList<Mail>();
    	Mail temp = null;
    	//新老邮件定分组
    	for (int i = 0; i < mailList.size(); i++) {
    		temp = mailList.get(i);
    		if(temp.getReadFinish()) {
    			oldList.add(temp);
    		} else {
    			newList.add(temp);
			}
		}
    	//新老邮件分别数组化
    	Mail[] oldMails = new Mail[oldList.size()];
    	Mail[] newMails = new Mail[newList.size()];
    	for (int i = 0; i < oldList.size(); i++) {
    		oldMails[i] = oldList.get(i);
		}
    	for (int i = 0; i < newList.size(); i++) {
    		newMails[i] = newList.get(i);
		}
    	//新老邮件分别排序
    	Mail k = null;
		for (int i = 0; i < oldMails.length; i++) {
			for (int j = i + 1; j < oldMails.length; j++)
				if( oldMails[i].getDate().getTime() > oldMails[j].getDate().getTime() ) {
					k = oldMails[i];
					oldMails[i] = oldMails[j];
					oldMails[j] = k;
				}
		}
		for (int i = 0; i < newMails.length; i++) {
			for (int j = i + 1; j < newMails.length; j++)
				if( newMails[i].getDate().getTime() > newMails[j].getDate().getTime() ) {
					k = newMails[i];
					newMails[i] = newMails[j];
					newMails[j] = k;
				}
		}
		//新老邮件分别再次填充
		mailList = new ArrayList<Mail>();
		for (int i = newMails.length-1; i > -1; i--) {
			mailList.add(newMails[i]);
		}
		for (int i = oldMails.length-1; i > -1; i--) {
			mailList.add(oldMails[i]);
		}

    	return mailList;
    }
    
    /**
     * 查询发件人是收件人的什么人
     * @param _sender
     * @param _receiver
     * @return
     */
    public byte getSocial(String _sender, String _receiver)
    {
    	byte result = 3;
    	if(SocialServiceImpl.getInstance().beFriend(_sender, _receiver, true))
    	{
    		//如果是好友 那么优先显示.
    		result = 0;
    	}
    	else if(GuildServiceImpl.getInstance().isAssociate(_sender, _receiver))
    	{
    		result = 1;
    	}
    	return result;
    }

    /**
     * 获取玩家邮件数量
     * 
     * @param _userID 接受者编号
     * @return
     */
    public int getMailNumber (int _userID)
    {
        ArrayList<Mail> goodsList = mailDict.get(_userID);

        if (goodsList == null)
        {
            return 0;
        }
        else
        {
            return goodsList.size();
        }
    }
    
    /**
     * 获得未读邮件数量
     * @param _userID
     * @return
     */
    public int getUnreadMailNumber (int _userID)
    {
        ArrayList<Mail> list = mailDict.get(_userID);
        int result = 0;

        if (list != null)
        {
            for (Mail mail : list) 
            {
				if(!mail.getReadFinish()) 
				{
					result += 1;
				}
			}
        }
        return result;
    }

    /**
     * 根据玩家编号和邮件ID得到邮件对象
     * 
     * @param _userID
     * @param _mailID
     * @return
     */
    public Mail getMail (int _userID, int _mailID)
    {
        try
        {
            lock.lock();
            ArrayList<Mail> mails = mailDict.get(_userID);

            if (mails != null)
            {
                for (Mail mail : mails)
                {
                    if (mail.getID() == _mailID)
                        return mail;
                }
            }

            return null;
        }
        finally
        {
            lock.unlock();
        }
    }
    
    /**
     * 移除一份邮件的附件
     * 
     * @param _userID
     * @param _mailID
     */
    public boolean removeAttachment (int _userID, int _mailID)
    {
        try
        {
        	/**
        	 * by zhengl
        	 * 待完善标记
        	 * 该行代码可能不需要,因为仅仅是对ArrayList其中元素的值进行更改,不是remove. 
        	 */
            lock.lock();
            ArrayList<Mail> list = mailDict.get(_userID);

            if (list != null)
            {
            	for (Mail mail : list) {
                    if (mail.getID() == _mailID)
                    {
                    	mail.removeAttachment();
                        removeAttachmentFromDB(_mailID);
                        return true;
                    }
				}
            }

            return false;
        }
        finally
        {
            lock.unlock();
        }
    }
    
    public boolean readMail (int _userID, int _mailID)
    {
        try
        {
        	/**
        	 * by zhengl
        	 * 待完善标记
        	 * 该行代码可能不需要,因为仅仅是对ArrayList其中元素的值进行更改,不是remove. 
        	 */
            lock.lock();
            ArrayList<Mail> list = mailDict.get(_userID);

            if (list != null)
            {
            	for (Mail mail : list) {
                    if (mail.getID() == _mailID)
                    {
                    	mail.readMail();
                        readFinishFromDB(_mailID);
                        return true;
                    }
				}
            }

            return false;
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * 移除一份邮件
     * 
     * @param _userID
     * @param _mailID
     */
    public boolean removeMail (int _userID, int _mailID)
    {
        try
        {
            lock.lock();
            ArrayList<Mail> list = mailDict.get(_userID);

            if (list != null)
            {
                for (int i = 0; i < list.size(); i++)
                {
                    if (list.get(i).getID() == _mailID)
                    {
                        list.remove(i);
                        deleteMailFromDB(_mailID);
                        return true;
                    }
                }
            }

            return false;
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * 从数据库中加载指定昵称的玩家邮件
     * 
     * @param _userID
     * @return
     */
    private ArrayList<Mail> loadMailFromDB (int _userID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        ArrayList<Mail> mailList = new ArrayList<Mail>();
        Date date = new Date(System.currentTimeMillis());

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SELECT_MAILS);
            pstm.setInt(1, _userID);
            rs = pstm.executeQuery();

            Mail mail = null;

            int mailID, recevier_uid, money, gamePoint, goodsID, read, social;
            String receiver, sender;
            byte mailType;
            short number;
            int equipmentID, creatorUserID, ownerUserID, currentDurabilityPoint;
            String genericEnhanceDesc, bloodyEnhanceDesc, content, title;
            byte existSeal, isBind;

            EquipmentInstance instance;

            while (rs.next())
            {
                mailID = rs.getInt(1);
                recevier_uid = rs.getInt(2);
                receiver = rs.getString(3);
                sender = rs.getString(4);
                mailType = rs.getByte(5);
                money = rs.getInt(6);
                gamePoint = rs.getInt(7);
                goodsID = rs.getInt(8);
                number = rs.getShort(9);
                content = rs.getString(10);
                title = rs.getString(11);
                read = rs.getInt(12);
                Timestamp lastLogoutTime = rs.getTimestamp(13);
                date = new Date(lastLogoutTime.getTime());
                social = rs.getInt(14);

                if (Mail.TYPE_OF_MONTY == mailType)
                {
                    mail = new Mail(mailID, recevier_uid, receiver, sender,
                            mailType, money, content, title, date, (byte)social);
                }
                else if (Mail.TYPE_OF_GAME_POINT == mailType)
                {
                    mail = new Mail(mailID, recevier_uid, receiver, sender,
                            mailType, gamePoint, content, title, date, (byte)social);
                }
                else if (Mail.TYPE_OF_SINGLE_GOODS == mailType)
                {
                    mail = new Mail(mailID, recevier_uid, receiver, sender,
                            mailType, goodsID, number, null, content, title, date, (byte)social);
                }
                else if (Mail.TYPE_OF_EQUIPMENT == mailType)
                {
                    equipmentID = rs.getInt(14);
                    creatorUserID = rs.getInt(15);
                    ownerUserID = rs.getInt(16);
                    currentDurabilityPoint = rs.getInt(17);
                    genericEnhanceDesc = rs.getString(18);
                    bloodyEnhanceDesc = rs.getString(19);
                    existSeal = rs.getByte(20);
                    isBind = rs.getByte(21);

                    instance = EquipmentFactory.getInstance().buildFromDB(
                            creatorUserID, ownerUserID, goodsID, equipmentID,
                            currentDurabilityPoint, existSeal, isBind);
                    EnhanceService.getInstance().parseEnhanceDesc(instance,
                            genericEnhanceDesc, bloodyEnhanceDesc);
                    
                    mail = new Mail(mailID, recevier_uid, receiver, sender,
                            mailType, goodsID, number, instance, content, title, date, (byte)social);
                }
                else if (Mail.TYPE_OF_TXT == mailType) {
                    mail = new Mail(mailID, recevier_uid, receiver, sender,
                            mailType, 0, content, title, date, (byte)social);
				}
                //从数据库设置为已读
                if(read == 1) {
                	mail.readMail();
                }
                mailList.add(mail);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                    rs = null;
                }
                if (pstm != null)
                {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null)
                {
                    conn.close();
                    conn = null;
                }
            }
            catch (SQLException e)
            {
            }
        }

        return mailList;
    }

    /**
     * 向数据库中插入一封邮件
     * 
     * @param _mail
     */
    private boolean insertMailToDB (Mail _mail, boolean _isSystemMail)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();

            if (!_isSystemMail)
            {
                pstm = conn.prepareStatement(SELECT_MAIL_NUMBER_SQL);
                pstm.setInt(1, _mail.getReceiverUserID());
                rs = pstm.executeQuery();

                if (rs.next())
                {
                    int mailNumber = rs.getInt(1);

                    if (mailNumber >= 30)
                    {
                        return false;
                    }
                }
                
                pstm.close();
                pstm = null;
            }

            pstm = conn.prepareStatement(INSERT_NEW_MAIL);
            pstm.setInt(1, _mail.getID());
            pstm.setInt(2, _mail.getReceiverUserID());
            pstm.setString(3, _mail.getReceiverName());
            pstm.setString(4, _mail.getSender());
            pstm.setByte(5, _mail.getType());
            pstm.setInt(6, _mail.getMoney());
            pstm.setInt(7, _mail.getGamePoint());

            if (Mail.TYPE_OF_SINGLE_GOODS == _mail.getType())
            {
                pstm.setInt(8, _mail.getSingleGoods().getID());
                pstm.setShort(9, _mail.getSingleGoodsNumber());
            }
            else if (Mail.TYPE_OF_EQUIPMENT == _mail.getType())
            {
                pstm.setInt(8, _mail.getEquipment().getInstanceID());
                pstm.setShort(9, (short) 1);
            }
            else
            {
                pstm.setInt(8, 0);
                pstm.setShort(9, (short) 0);
            }
            pstm.setString(10, _mail.getContent());
            pstm.setString(11, _mail.getTitle());
            //最终还是考虑不使用直接set boolean 的方式存储.
            if( _mail.getReadFinish() ) {
            	pstm.setShort(12, (short)1);
            } else {
            	pstm.setShort(12, (short)0);
			}
            Timestamp timestamp =  new Timestamp(_mail.getDate().getTime());
            pstm.setTimestamp(13, timestamp);
            pstm.setInt(14, _mail.getSocial());

            if (pstm.executeUpdate() == 1)
            {
                return true;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (rs != null)
                {
                    rs.close();
                    rs = null;
                }
                if (pstm != null)
                {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null)
                {
                    conn.close();
                    conn = null;
                }
            }
            catch (SQLException e)
            {
            }
        }

        return false;
    }

    /**
     * 从数据库中删除一封邮件
     * 
     * @param _mailID
     */
    private void deleteMailFromDB (int _mailID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DEL_MAIL);
            pstm.setInt(1, _mailID);
            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null)
                {
                    conn.close();
                    conn = null;
                }
            }
            catch (SQLException e)
            {
            }
        }
    }
    
    private void readFinishFromDB (int _mailID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(UPDATE_MAIL_READFINISH);
            pstm.setInt(1, _mailID);
            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null)
                {
                    conn.close();
                    conn = null;
                }
            }
            catch (SQLException e)
            {
            }
        }
    }
    
    private void removeAttachmentFromDB (int _mailID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(UPDATE_MAIL);
            pstm.setInt(1, _mailID);
            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null)
                {
                    conn.close();
                    conn = null;
                }
            }
            catch (SQLException e)
            {
            }
        }
    }

    /**
     * 清除玩家所有邮件（删除角色时调用）
     * 
     * @param _userID
     */
    public void deleteRole (int _userID)
    {
        ArrayList<Mail> mailList = mailDict.remove(_userID);

        if (null != mailList && mailList.size() > 0)
        {
            Connection conn = null;
            PreparedStatement pstm = null;

            try
            {
                conn = DBServiceImpl.getInstance().getConnection();
                pstm = conn.prepareStatement(DELETE_ALL_MAIL);
                pstm.setInt(1, _userID);
                pstm.executeUpdate();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                try
                {
                    if (pstm != null)
                    {
                        pstm.close();
                        pstm = null;
                    }
                    if (conn != null)
                    {
                        conn.close();
                        conn = null;
                    }
                }
                catch (SQLException e)
                {
                }
            }
        }
    }
}
