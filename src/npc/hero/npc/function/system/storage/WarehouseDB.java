package hero.npc.function.system.storage;

import hero.item.EquipmentInstance;
import hero.item.enhance.EnhanceService;
import hero.item.service.EquipmentFactory;
import hero.item.special.BigTonicBall;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import yoyo.service.tools.database.DBServiceImpl;


/**
 * 仓库数据库操作类 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class WarehouseDB
{
    private static final String SEL_LVL_SQL       = "SELECT lvl FROM stroage_lvl WHERE nickname = ?";

    private static final String INSERT_LVL_SQL    = "INSERT INTO stroage_lvl SET lvl = 0,nickname = ?";

    private static final String UP_LVL_SQL        = "UPDATE stroage_lvl SET lvl = ? WHERE nickname = ?";

    private static final String INSERT_GOODS_SQL  = "INSERT INTO stroage SET nickname = ?,index_id=?,goods_id=?,goods_num=?,goods_type=?,single_goods_id=?";
    
    private static final String UPDATE_TINOC_SQL  = "UPDATE big_tonic_ball SET TYPE = ? WHERE single_goods_id=?";

    private static final String SEL_EQUIPMENT_SQL = "SELECT s.index_id,s.goods_id,s.goods_num,s.goods_type,e.equipment_id,e.creator_user_id,e.owner_user_id,e.current_durability,e.generic_enhance_desc,e.bloody_enhance_desc,e.be_sealed,e.bind FROM stroage s,equipment_instance e WHERE s.nickname = ? AND s.goods_id = e.instance_id AND s.goods_type = 0";

    private static final String SEL_GOODS_SQL     = "SELECT index_id,goods_id,goods_num,goods_type,single_goods_id FROM stroage WHERE nickname = ? AND goods_type = 1";

    private static final String DEL_SQL           = "DELETE FROM stroage WHERE nickname = ? AND index_id = ?";

    protected static byte selLvl (String _nickname)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        byte lvl = 0;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SEL_LVL_SQL);
            pstm.setString(1, _nickname);
            rs = pstm.executeQuery();
            if (rs.next())
            {
                lvl = rs.getByte(1);
            }
            else
            {
                insertLvl(_nickname);
            }
            return lvl;
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
        return lvl;
    }

    protected static void insertLvl (String _nickname)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(INSERT_LVL_SQL);
            pstm.setString(1, _nickname);
            pstm.execute();
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

    protected static void updateLvl (byte _lvl, String _nickname)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(UP_LVL_SQL);
            pstm.setByte(1, _lvl);
            pstm.setString(2, _nickname);
            pstm.execute();
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

    protected static void insertGoods (String _nickname, byte _index,
            int _goodsID, short _num, short _goodsType, int _singleGoodsID, boolean _isAutoBall)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(INSERT_GOODS_SQL);
            pstm.setString(1, _nickname);
            pstm.setByte(2, _index);
            pstm.setInt(3, _goodsID);
            pstm.setShort(4, _num);
            pstm.setShort(5, _goodsType);
            pstm.setInt(6, _singleGoodsID);
            pstm.execute();
            
            if (_isAutoBall) 
            {
            	pstm = conn.prepareStatement(UPDATE_TINOC_SQL);
            	pstm.setInt(1, BigTonicBall.TONINC_CODE);
            	pstm.setInt(2, _singleGoodsID);
            	pstm.execute();
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

    protected static void selGoods (String _nickname, Warehouse _warehouse)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            // 加载玩家仓库里非装备物品
            pstm = conn.prepareStatement(SEL_GOODS_SQL);
            pstm.setString(1, _nickname);
            rs = pstm.executeQuery();
            while (rs.next())
            {
                byte index = rs.getByte(1);
                int goodsID = rs.getInt(2);
                short num = rs.getShort(3);
                short _goodsType = rs.getShort(4);
                //add by zhengl; date: 2011-05-13; note: 加载的时候同时加载ID
                int single_goods_id = rs.getInt(5);
                EquipmentInstance _instance = null;
                _warehouse.addWarehouseGoods(index, goodsID, num, _goodsType,
                        _instance, single_goods_id);
            }
            rs.close();
            rs = null;
            pstm.close();
            pstm = null;
            // 加载玩家仓库里装备物品
            pstm = conn.prepareStatement(SEL_EQUIPMENT_SQL);
            pstm.setString(1, _nickname);
            rs = pstm.executeQuery();
            
            while (rs.next())
            {
                byte index = rs.getByte(1);
                int goodsID = rs.getInt(2);
                short num = rs.getShort(3);
                short _goodsType = rs.getShort(4);
                int _equipmentID = rs.getInt(5);
                int _creatorUserID = rs.getInt(6);
                int _ownerUserID = rs.getInt(7);
                int _currentDurabilityPoint = rs.getInt(8);
                String genericEnhanceDesc = rs.getString(9);
                String bloodyEnhanceDesc = rs.getString(10);
                byte existSeal = rs.getByte(11);
                byte isBind = rs.getByte(12);

                EquipmentInstance instance = EquipmentFactory.getInstance()
                        .buildFromDB(_creatorUserID, _ownerUserID, goodsID,
                                _equipmentID, _currentDurabilityPoint,
                                existSeal, isBind);
                EnhanceService.getInstance().parseEnhanceDesc(instance,
                        genericEnhanceDesc, bloodyEnhanceDesc);

                _warehouse.addWarehouseGoods(index, goodsID, num, _goodsType,
                        instance, 0);
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
    }

    public static void delGoods (String _nickname, byte _index)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DEL_SQL);
            pstm.setString(1, _nickname);
            pstm.setByte(2, _index);
            pstm.execute();
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
