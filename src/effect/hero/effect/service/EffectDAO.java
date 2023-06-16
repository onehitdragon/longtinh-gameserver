package hero.effect.service;

import hero.effect.Effect;
import hero.player.HeroPlayer;
import hero.share.service.LogWriter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import yoyo.service.tools.database.DBServiceImpl;


/**
 * 效果DAO
 * 
 * @author Luke chen
 * @date 2009-4-3
 */
public class EffectDAO
{

    /**
     * 插入一个新效果
     */
    private final static String INSERT = "INSERT INTO player_effect(user_id, effect_id, keepTime, again) VALUES (?,?,?,?)";

    /**
     * 删除某个玩家的所有效果
     */
    private final static String DELETE = "DELETE FROM player_effect WHERE user_id=?";

    /**
     * 查找该玩家的效果
     */
    private final static String SELECT = "select * from player_effect where user_id=? LIMIT 100";

    /**
     * 删除用户的所有效果
     * 
     * @param _uid
     */
    public static void deletePlayerEffect (HeroPlayer _player)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;
        ArrayList<Effect> list = _player.effectList;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();

            pstm = conn.prepareStatement(SELECT);
            pstm.setInt(1, _player.getUserID());
            set = pstm.executeQuery();

            if (set != null)
            {
                int effect_id = 0;
                int keepTime = 0;
                int again;
                Effect effect = null;
                
                while (set.next())
                {
                    effect_id = set.getInt("effect_id");
                    keepTime = set.getInt("keepTime");
                    again = set.getInt("again");

//                    effect = EffectDictionary.getInstance().getEffectByClone(
//                            effect_id);

                    if (effect != null)
                    {
//                        ((NormalBufDebuf) effect).setCurKeepTime(keepTime);
//                        ((NormalBufDebuf) effect).setAgainCount(again);
                        list.add(effect);
                    }
                }

                if (null != pstm)
                {
                    pstm.close();
                }

                conn = DBServiceImpl.getInstance().getConnection();
                pstm = conn.prepareStatement(DELETE);
                pstm.setInt(1, _player.getUserID());
                pstm.executeUpdate();
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (set != null)
                    set.close();
                if (pstm != null)
                    pstm.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 插入用户持续效果
     * 
     * @param _uid
     * @param _list
     */
    public static void insertPlayerEffect (int _uid, List<Effect> _list)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            conn.setAutoCommit(false);
            pstm = conn.prepareStatement(INSERT);

            synchronized (_list)
            {
                int keepTime = 0;
                Effect effect;

                for (int i = 0; i < _list.size(); i++)
                {
                    effect = _list.get(i);

//                    byte type = effect.getType();
//
//                    if (type == Effect.NOR_BUFDEBUF
//                            && effect.getCurKeepTime() >= 60 * 5)
//                    {
//                        keepTime = ((NormalBufDebuf) effect).getCurKeepTime();
//                        pstm.setInt(1, _uid);
//                        pstm.setInt(2, effect.getEffectID());
//                        pstm.setInt(3, keepTime);
//                        pstm.setInt(4, ((NormalBufDebuf) effect)
//                                .getAgainCount());
//                        pstm.addBatch();
//                    }
                }
            }

            pstm.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        }
        catch (Exception ex)
        {
            // ex.printStackTrace();
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (pstm != null)
                    pstm.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
}
