package hero.share.letter;

import hero.log.service.LogServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.MailStatusChanges;
import hero.share.service.LogWriter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.tools.database.DBServiceImpl;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 LetterService.java
 * @创建者 Lulin
 * @版本 1.0
 * @时间 2010-06-18 下午14:35:18
 * @描述 ：信件服务
 */

public class LetterService
{
    /**
     * 信件字典
     */
    private HashMap<Integer, ArrayList<Letter>> letterDict;

    /**
     * 单例
     */
    private static LetterService                instance;

    private ReentrantLock                       lock     = new ReentrantLock();

    /**
     * 信箱最大存储信件数
     */
    public static final int                     MAX_SIZE = 30;

    /**
     * 过期信件检查计时器
     */
    private Timer                               mCheckTimer;

    /**
     * 最大可用信件编号
     */
    private int                                 maxLetterID;

    /**
     * 私有构造
     */
    private LetterService()
    {
        maxLetterID = 0;
        letterDict = new HashMap<Integer, ArrayList<Letter>>();
        load();
        mCheckTimer = new Timer();
        CheckTask checkTask = new CheckTask();
        mCheckTimer.schedule(checkTask, CHECK_TIME, CHECK_TIME);
    }

    private void load ()
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SQL_OF_SELECT_ALL_LETTER);
            rs = pstm.executeQuery();

            int letterID;
            String title;
            String senderName;
            int receiverUserID;
            String receiverName;
            String content;
            long time;
            byte isRead;
            byte isSave;
            byte type;//类型 0:系统邮件  1:普通

            Letter letter;

            while (rs.next())
            {
                letterID = rs.getInt("letter_id");
                type = rs.getByte("type");
                title = rs.getString("title");
                senderName = rs.getString("sender");
                receiverUserID = rs.getInt("receiver_uid");
                receiverName = rs.getString("receiver");
                content = rs.getString("content");
                time = rs.getLong("send_time");
                isRead = rs.getByte("isread");
                isSave = rs.getByte("issave");

                letter = new Letter(type,letterID, title, senderName,
                        receiverUserID, receiverName, content);
                letter.sendTime = time;
                letter.isRead = isRead == 1;
                letter.isSave = isSave == 1;

                addToList(letter);

                if (maxLetterID < letterID)
                    maxLetterID = letterID;
            }
        }
        catch (Exception ex)
        {
            LogWriter.println(ex);
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
                LogWriter.println(e);
            }
        }
    }

    /**
     * 得到一个可用的信件ID
     * 
     * @return
     */
    public int getUseableLetterID ()
    {
        try
        {
            lock.lock();

            return ++maxLetterID;
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static LetterService getInstance ()
    {
        if (instance == null)
            instance = new LetterService();
        return instance;
    }

    /**
     * 根据玩家编号得到信件列表
     * 
     * @param _userID
     * @return
     */
    public ArrayList<Letter> getLetterList (int _userID)
    {
        return letterDict.get(_userID);
    }

    /**
     * 根据玩家昵称得到玩家信件数
     * 
     * @param _nickname
     * @return
     */
    public int getLetterNumber (int _userID)
    {
        ArrayList<Letter> letterList = letterDict.get(_userID);

        if (null != letterList)
        {
            return letterList.size();
        }

        return 0;
    }

    /**
     * 添加信件到列表
     * 
     * @param _letter
     */
    private void addToList (Letter _letter)
    {
        ArrayList<Letter> letterList = letterDict.get(_letter.receiverUserID);

        if (null == letterList)
        {
            letterList = new ArrayList<Letter>();

            letterDict.put(_letter.receiverUserID, letterList);
        }

        letterList.add(_letter);
    }

    /**
     * 添加一个新信件
     * 
     * @param _letter
     */
    public void addNewLetter (Letter _letter)
    {
        ArrayList<Letter> letterList = letterDict.get(_letter.receiverUserID);

        if (null == letterList)
        {
            letterList = new ArrayList<Letter>();

            letterDict.put(_letter.receiverUserID, letterList);
        }

        letterList.add(_letter);

        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SQL_OF_ADD_LETTER);
            pstm.setInt(1, _letter.letterID);
            pstm.setString(2, _letter.title);
            pstm.setString(3, _letter.senderName);
            pstm.setInt(4, _letter.receiverUserID);
            pstm.setString(5, _letter.receiverName);
            pstm.setString(6, _letter.content);
            pstm.setLong(7, _letter.sendTime);
            pstm.setInt(8,_letter.type);
            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
            LogWriter.println(ex);
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

        // 信件日志
        LogServiceImpl.getInstance().letterLog(_letter.senderName,
                _letter.letterID, _letter.receiverName, _letter.title,
                _letter.content);
    }

    /**
     * 是否存在未读信件
     * 
     * @param _userID
     * @return
     */
    public boolean existsUnreadedLetter (int _userID)
    {
        ArrayList<Letter> letterList = getLetterList(_userID);

        try
        {
            lock.lock();

            if (null == letterList || 0 == letterList.size())
            {
                return false;
            }

            for (Letter l : letterList)
            {
                if (!l.isRead)
                {
                    return true;
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
     * 设置指定信件为读过
     * 
     * @param _nickname
     * @param _letterID
     */
    public void settingToRead (int _userID, int _letterID)
    {
        ArrayList<Letter> letterList = getLetterList(_userID);

        try
        {
            lock.lock();

            if (null != letterList && letterList.size() > 0)
            {
                for (Letter l : letterList)
                {
                    if (l.letterID == _letterID)
                    {
                        l.isRead = true;

                        Connection conn = null;
                        PreparedStatement pstm = null;

                        try
                        {
                            conn = DBServiceImpl.getInstance().getConnection();
                            pstm = conn
                                    .prepareStatement(SQL_OF_UPDATE_READ_STATUS);
                            pstm.setInt(1, _letterID);
                            pstm.executeUpdate();
                        }
                        catch (Exception ex)
                        {
                            LogWriter.println(ex);
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

                        return;
                    }
                }
            }
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * 设置指定信件为保存
     * 
     * @param _userID
     * @param _letterID
     */
    public void settingToSaved (int _userID, int _letterID)
    {
        ArrayList<Letter> letterList = getLetterList(_userID);

        try
        {
            lock.lock();

            if (null != letterList && letterList.size() > 0)
            {
                for (Letter l : letterList)
                {
                    if (l.letterID == _letterID)
                    {
                        l.isSave = true;
                        l.isRead = true;

                        Connection conn = null;
                        PreparedStatement pstm = null;

                        try
                        {
                            conn = DBServiceImpl.getInstance().getConnection();
                            pstm = conn
                                    .prepareStatement(SQL_OF_UPDATE_SAVE_STATUS);
                            pstm.setInt(1, _letterID);
                            pstm.executeUpdate();
                        }
                        catch (Exception ex)
                        {
                            LogWriter.println(ex);
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

                        return;
                    }
                }
            }
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * 删除指定信件
     * 
     * @param _userID
     * @param _letterID
     */
    public void removeLetter (int _userID, int _letterID)
    {
        ArrayList<Letter> letterList = getLetterList(_userID);

        try
        {
            lock.lock();

            if (null != letterList && letterList.size() > 0)
            {
                for (int i = 0; i < letterList.size(); i++)
                {
                    if (letterList.get(i).letterID == _letterID)
                    {
                        letterList.remove(i);

                        Connection conn = null;
                        PreparedStatement pstm = null;

                        try
                        {
                            conn = DBServiceImpl.getInstance().getConnection();
                            pstm = conn
                                    .prepareStatement(SQL_OF_DELETE_ONE_LETTER);
                            pstm.setInt(1, _letterID);
                            pstm.executeUpdate();
                        }
                        catch (Exception ex)
                        {
                            LogWriter.println(ex);
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

                        return;
                    }
                }
            }
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * 清除玩家信件
     * 
     * @param _receiverName 收件人名字
     */
    public void deleteRole (int _userID)
    {
        letterDict.remove(_userID);

        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SQL_OF_DELELE_PLAYER_LETTER);
            pstm.setInt(1, _userID);
            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
            LogWriter.println(ex);
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

    private void checkTimeOut ()
    {
        ArrayList<Integer> timeoutList = new ArrayList<Integer>();

        long nowtime = System.currentTimeMillis();

        Iterator<ArrayList<Letter>> letterListIt = letterDict.values()
                .iterator();

        ArrayList<Letter> list;
        int receiverUserID;
        Letter letter;
        boolean existsUnreadedLetter = false;
        HeroPlayer receiver;
        ArrayList<Integer> invalidateLetterIDList = new ArrayList<Integer>();

        while (letterListIt.hasNext())
        {
            list = letterListIt.next();

            if (null != list)
            {
                receiverUserID = list.get(0).receiverUserID;

                for (int i = 0; i < list.size(); i++)
                {
                    try
                    {
                        letter = list.get(i);

                        if ((!letter.isSave)
                                && (nowtime - letter.sendTime >= SAVE_TIME))
                        {
                            list.remove(i);
                            invalidateLetterIDList.add(letter.letterID);
                            i--;
                            timeoutList.add(letter.letterID);
                        }
                        else
                        {
                            if (!letter.isRead)
                            {
                                existsUnreadedLetter = true;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        return;
                    }
                }

                if (!existsUnreadedLetter)
                {
                    receiver = PlayerServiceImpl.getInstance()
                            .getPlayerByUserID(receiverUserID);

                    if (null != receiver && receiver.isEnable())
                    {
                        ResponseMessageQueue
                                .getInstance()
                                .put(
                                        receiver.getMsgQueueIndex(),
                                        new MailStatusChanges(
                                                MailStatusChanges.TYPE_OF_LETTER,
                                                false));
                    }
                }
            }
        }

        delete(invalidateLetterIDList);
    }

    /**
     * 删除过期信件
     * 
     * @param _letterIDList 被删除的信件编号列表
     */
    private void delete (ArrayList<Integer> _letterIDList)
    {
        if (null != _letterIDList && _letterIDList.size() > 0)
        {
            Connection conn = null;
            PreparedStatement pstm = null;

            try
            {
                conn = DBServiceImpl.getInstance().getConnection();
                conn.setAutoCommit(false);
                pstm = conn.prepareStatement(SQL_OF_DELETE_ONE_LETTER);

                for (int letterID : _letterIDList)
                {
                    pstm.setInt(1, letterID);
                    pstm.addBatch();
                }

                pstm.executeBatch();
                conn.commit();
                conn.setAutoCommit(true);
            }
            catch (Exception ex)
            {
                LogWriter.println(ex);
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

    class CheckTask extends TimerTask
    {
        public void run ()
        {
            checkTimeOut();
        }
    }

    /**
     * 保存时间
     */
    public static final long    SAVE_TIME                   = 7 * 24 * 60 * 60
                                                                    * 1000;

    /**
     * 删除检查时间
     */
    public static final long    CHECK_TIME                  = 4 * 60 * 60 * 1000;

    /**
     * 数据库脚本-删除一封信
     */
    private static final String SQL_OF_DELETE_ONE_LETTER    = "DELETE FROM letter WHERE letter_id = ? LIMIT 1";

    /**
     * 数据库脚本-更新保存状态
     */
    private static final String SQL_OF_UPDATE_SAVE_STATUS   = "UPDATE letter SET issave = 1,isread = 1 WHERE letter_id = ? LIMIT 1";

    /**
     * 数据库脚本-更新是否已阅读状态
     */
    private static final String SQL_OF_UPDATE_READ_STATUS   = "UPDATE letter SET isread = 1 WHERE letter_id = ? LIMIT 1";

    /**
     * 数据库脚本-添加信件
     */
    private static final String SQL_OF_ADD_LETTER           = "INSERT INTO letter (letter_id,title,sender,receiver_uid,receiver,content,send_time,type) VALUES (?,?,?,?,?,?,?,?)";

    /**
     * 数据库脚本-加载所有信件
     */
    private static final String SQL_OF_SELECT_ALL_LETTER    = "SELECT * FROM letter order by type asc,send_time desc";

    /**
     * 删除某玩家的所有信件
     */
    private static final String SQL_OF_DELELE_PLAYER_LETTER = "DELETE FROM letter WHERE receiver_uid = ?";

}
