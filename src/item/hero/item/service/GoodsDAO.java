package hero.item.service;

import hero.guild.service.GuildServiceImpl;
import hero.item.bag.EquipmentBag;
import hero.item.bag.EquipmentContainer;
import hero.item.bag.Inventory;
import hero.item.bag.SingleGoodsBag;
import hero.item.detail.EGoodsType;
import hero.item.dictionary.GoodsContents;
import hero.item.enhance.EnhanceService;
import hero.item.special.BigTonicBall;
import hero.item.special.ESpecialGoodsType;
import hero.item.special.PetPerCard;
import hero.item.EquipmentInstance;
import hero.item.SingleGoods;
import hero.item.SpecialGoods;
import hero.player.HeroPlayer;
import hero.share.service.LogWriter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import yoyo.service.tools.database.DBServiceImpl;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GoodsDAO.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-17 下午05:47:58
 * @描述 ：物品数据库操作类
 */

public final class GoodsDAO
{
	private static Logger log = Logger.getLogger(GoodsDAO.class);
	
    /**
     * 查询最大特殊物品编号
     */
    private static final String SELECT_MAX_SPECAIL_GOODS_ID    = "SELECT MAX(id) as max_goods_id from player_single_goods";
    
    /**
     * 改变装备位置，包括身上和背包装备的穿戴、卸载
     */
    private static final String UPDATE_BAG_EQUIPMENT_LOCATION_SQL    = "UPDATE player_carry_equipment "
                                                                             + "SET container_type=?,package_index=? "
                                                                             + "WHERE instance_id=? LIMIT 1";

    /**
     * 删除角色携带装备，包括身上穿戴、背包里
     */
    private static final String DELETE_PLAYER_CARRY_EQUIPMENT_SQL    = "DELETE FROM player_carry_equipment "
                                                                             + "WHERE instance_id=? LIMIT 1";

    /**
     * 删除装备实例
     */
    private static final String DELETE_EQUIPMENT_INSTANCE_SQL        = "DELETE FROM equipment_instance "
                                                                             + "WHERE instance_id=? LIMIT 1";

    /**
     * 添加装备实例
     */
    private static final String INSERT_EQUIPMENT_INSTANCE_SQL        = "INSERT INTO equipment_instance(instance_id,"
                                                                             + "equipment_id,creator_user_id,owner_user_id,"
                                                                             + "current_durability,be_sealed,bind,owner_type) "
                                                                             + "VALUES (?,?,?,?,?,?,?,?)";

    /**
     * 整理背包的批处理
     */
    private static final String CLEAR_UP_EQUIPMENT_PACKAGE_BATCH_SQL = "UPDATE player_carry_equipment "
                                                                             + "SET package_index=? "
                                                                             + "WHERE instance_id=? LIMIT 1";

    /**
     * 改变非装备物品在背包中的数量、位置
     */
    private static final String UPDATA_SINGLE_GOODS_BAG_SQL          = "UPDATE player_single_goods "
                                                                             + "SET goods_number=? WHERE user_id=? "
                                                                             + "AND goods_id=? AND package_index=? LIMIT 1";

    /**
     * 向背包中添加装备
     */
    private static final String INSERT_EQUIPMENT_BAG_SQL             = "INSERT INTO player_carry_equipment(instance_id,"
                                                                             + "user_id,package_index,container_type) "
                                                                             + "VALUES (?,?,?,?)";

    /**
     * 向玩家身上添加装备
     */
    private static final String INSERT_EQUIPMENT_TO_BODY_SQL         = "INSERT INTO player_carry_equipment(instance_id,"
                                                                             + "user_id,container_type) "
                                                                             + "VALUES (?,?,?)";

    /**
     * 向背包中添加非装备物品
     */
    private static final String INSERT_SINGLE_GOODS_SQL              = "INSERT INTO player_single_goods"
                                                                             + "(user_id,goods_type,goods_id,"
                                                                             + "goods_number,package_index,id) VALUES (?,?,?,?,?,?)";

    /**
     * 删除指定位置的非装备物品
     */
    private static final String DELETE_GRID_SINGLE_GOODS_SQL         = "DELETE FROM player_single_goods "
                                                                             + "WHERE user_id=? AND package_index=? "
                                                                             + "AND goods_id=? LIMIT 1";

    /**
     * 删除某非装备物品
     */
    private static final String DELETE_SINGLE_GOODS_SQL              = "DELETE FROM player_single_goods "
                                                                             + "WHERE user_id=? AND goods_id=? "
                                                                             + "LIMIT 80";

    /**
     * 删除某类型背包的所有非装备物品
     */
    private static final String DELETE_PACKAGE_SINGLE_GOODS_SQL      = "DELETE FROM player_single_goods "
                                                                             + "WHERE user_id=? AND goods_type=? LIMIT 80";

    /**
     * 获取所有非装备物品
     */
    private static final String SELECT_SINGLE_GOODS_SQL              = "SELECT id,goods_id,goods_type,goods_number,"
                                                                             + "package_index from player_single_goods "
                                                                             + "where user_id=? LIMIT 280";
    private static final String SELECT_TONIC_GOODS_SQL              = "SELECT tonic_id,surplus_point,type "
        																	+ " from big_tonic_ball "
        																	+ " where single_goods_id=? LIMIT 1";
    private static final String SELECT_PET_PER_CARD_GOODS_SQL       = "SELECT card_id,surplus_point "
    																		+ " from pet_per_card "
    																		+ " where single_goods_id=? LIMIT 1";

    /**
     * 加载玩家装备
     */
    private static final String SELECT_EQUIPMENT_SQL                 = "SELECT player_carry_equipment.instance_id,equipment_id,"
                                                                             + "creator_user_id,current_durability,generic_enhance_desc"
                                                                             + ",bloody_enhance_desc,be_sealed,bind,container_type,"
                                                                             + "package_index FROM equipment_instance "
                                                                             + "JOIN player_carry_equipment "
                                                                             + "ON user_id=? AND "
                                                                             + "player_carry_equipment.instance_id="
                                                                             + "equipment_instance.instance_id LIMIT 100";

    /**
     * 更新背包装备所有者，包括在背包中的位置
     */
    private static final String UPDATE_EQUIPMENT_OWNER_SQL           = "UPDATE player_carry_equipment SET user_id=?,package_index=?"
                                                                             + " WHERE instance_id=? LIMIT 1";

    /**
     * 更新装备耐久度
     */
    private static final String UPDATE_EQUIPMENT_DURABILITY_SQL      = "UPDATE equipment_instance SET current_durability=?"
                                                                             + " WHERE instance_id=? LIMIT 1";

    /**
     * 移除装备封印
     */
    private static final String UPDATE_EQUIPMENT_SEAL_SQL            = "UPDATE equipment_instance SET be_sealed=0"
                                                                             + " WHERE instance_id=? LIMIT 1";

    /**
     * 设置装备绑定
     */
    private static final String UPDATE_EQUIPMENT_BIND_SQL            = "UPDATE equipment_instance SET bind = 1"
                                                                             + " WHERE instance_id=? LIMIT 1";

    /**
     * 更新武器血腥强化等级
     */
    private static final String UPDATE_WEAPON_ENHANCE_SQL            = "UPDATE equipment_instance SET bloody_enhance_desc=?"
                                                                             + " WHERE instance_id=? LIMIT 1";

    /**
     * 更新装备强化等级
     */
    private static final String UPDATE_EQUIPMENT_ENHANCE_SQL         = "UPDATE equipment_instance SET generic_enhance_desc=?"
                                                                             + " WHERE instance_id=? LIMIT 1";

    /**
     * 更新装备实例所有者
     */
    private static final String UPDATE_EQUIPMENT_INSTANCE_OWNER_SQL  = "UPDATE equipment_instance SET owner_user_id=?"
                                                                             + " WHERE instance_id=? LIMIT 1";

    /**
     * 插入玩家初始背包容量
     */
    private static final String UPDATE_BAG_SIZE_SQL                  = "UPDATE player SET bag_size=? WHERE user_id=? LIMIT 1";

    /**
     * 查找唯一装备实例
     */
    private static final String SEL_INSTANCE_SQL                     = "SELECT equipment_id,creator_user_id,owner_user_id,"
                                                                             + "current_durability,generic_enhance_desc,"
                                                                             + "bloody_enhance_desc,be_sealed,bind FROM equipment_instance"
                                                                             + " WHERE instance_id = ? LIMIT 1";

    /**
     * 更新灵魂所在地图编号
     */
    private static final String UPDATE_HOME_SQL                      = "UPDATE player SET home_id=? WHERE user_id=? LIMIT 1";
    
    /**
     * 销毁携带装备，包括身上穿着和背包中的
     * 
     * @param _equipmentInsID
     */
    public static boolean diceEquipment (int _equipmentInsID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DELETE_PLAYER_CARRY_EQUIPMENT_SQL);
            pstm.setInt(1, _equipmentInsID);

            if (pstm.executeUpdate() > 0)
            {
                pstm.close();
                pstm = null;

                pstm = conn.prepareStatement(DELETE_EQUIPMENT_INSTANCE_SQL);
                pstm.setInt(1, _equipmentInsID);

                if (pstm.executeUpdate() > 0)
                {
                    return true;
                }
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
                if (pstm != null)
                {
                    pstm.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            	e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * 将背包装备移走（移到别的容器中）
     * 
     * @param _equipmentInsID
     */
    public static boolean removeEquipmentOfBag (int _equipmentInsID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DELETE_PLAYER_CARRY_EQUIPMENT_SQL);
            pstm.setInt(1, _equipmentInsID);

            if (pstm.executeUpdate() > 0)
            {
                return true;
            }
        }
        catch (Exception ex)
        {
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }

        return false;
    }

    /**
     * 销毁装备实例
     * 
     * @param _equipmentInsID
     */
    public static boolean diceEquipmentInstance (int _equipmentInsID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            pstm = conn.prepareStatement(DELETE_EQUIPMENT_INSTANCE_SQL);
            pstm.setInt(1, _equipmentInsID);

            if (pstm.executeUpdate() > 0)
            {
                return true;
            }
        }
        catch (Exception ex)
        {
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }

        return false;
    }

    /**
     * 删除某个格子里的非装备物品
     * 
     * @param _ownerUserID 角色编号
     * @param _gridIndex 格子位置
     * @param _goodsID 物品编号
     */
    public static boolean removeSingleGoodsFromBag (int _ownerUserID,
            short _gridIndex, int _goodsID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DELETE_GRID_SINGLE_GOODS_SQL);
            pstm.setInt(1, _ownerUserID);
            pstm.setShort(2, _gridIndex);
            pstm.setInt(3, _goodsID);

            if (pstm.executeUpdate() > 0)
            {
                return true;
            }
        }
        catch (Exception ex)
        {
            log.error("删除某个格子里的非装备物品 error:", ex);
        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }

        return false;
    }

    /**
     * 删除所有该物品非装备物品
     * 
     * @param _ownerUserID 角色编号
     * @param _goodsID 物品编号
     */
    public static boolean removeSingleGoodsFromBag (int _ownerUserID,
            int _goodsID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DELETE_SINGLE_GOODS_SQL);
            pstm.setInt(1, _ownerUserID);
            pstm.setInt(2, _goodsID);

            if (pstm.executeUpdate() > 0)
            {
                return true;
            }
        }
        catch (Exception ex)
        {
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }

        return false;
    }

    /**
     * 整理非装备物品包
     * 
     * @param _ownerUserID 拥有者编号
     * @param _singleGoodsPackage 背包
     * @param _singleGoodsType 物品类型
     */
    public static void clearUpSingleGoodsPackage (int _ownerUserID,
            SingleGoodsBag _singleGoodsPackage, byte _singleGoodsType)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            /*add by zhengl; date: 2011-05-18; note: 解决conn.commit()异常*/
            conn.setAutoCommit(false);
            pstm = conn.prepareStatement(DELETE_PACKAGE_SINGLE_GOODS_SQL);
            pstm.setInt(1, _ownerUserID);
            pstm.setShort(2, _singleGoodsType);

            pstm.executeUpdate();

            pstm.close();
            pstm = null;

            conn.setAutoCommit(false);
            pstm = conn.prepareStatement(INSERT_SINGLE_GOODS_SQL);

            int[][] goodsList = _singleGoodsPackage.getAllItem();

            for (int i = 0; i < goodsList.length; i++)
            {
                if (0 != goodsList[i][0])
                {
                    pstm.setInt(1, _ownerUserID);
                    pstm.setShort(2, _singleGoodsType);
                    pstm.setInt(3, goodsList[i][0]);
                    pstm.setInt(4, goodsList[i][1]);
                    pstm.setInt(5, i);
                    int id = GoodsServiceImpl.getInstance().getUseableSpecailID();
                    pstm.setInt(6, id);

                    pstm.addBatch();

//                    GoodsServiceImpl.getInstance().useableSpecailIDAdd();
                }
            }

            pstm.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        }
        catch (Exception ex)
        {
            log.error("整理非装备物品包 error:", ex);

            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }
    }

    /**
     * 改变装备在背包中的位置
     * 
     * @param _equipmentInsID
     */
    public static void changeEquipmentLocation (int _equipmentInsID,
            byte _containerType, int _newGridIndex)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(UPDATE_BAG_EQUIPMENT_LOCATION_SQL);
            pstm.setInt(1, _containerType);
            pstm.setShort(2, (short) _newGridIndex);
            pstm.setInt(3, _equipmentInsID);

            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }
    }

    /**
     * 改变装备拥有者
     * 
     * @param _newMasterUserID 新主人
     * @param _equipmentInsID 装备实例编号
     * @param _newGridIndex 在新主人背包中的位置
     */
    public static boolean changeEquipmentOwner (int _newMasterUserID,
            int _equipmentInsID, int _newGridIndex)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(UPDATE_EQUIPMENT_OWNER_SQL);
            pstm.setInt(1, _newMasterUserID);
            pstm.setShort(2, (short) _newGridIndex);
            pstm.setInt(3, _equipmentInsID);

            pstm.executeUpdate();

            pstm.close();
            pstm = null;

            pstm = conn.prepareStatement(UPDATE_EQUIPMENT_INSTANCE_OWNER_SQL);

            pstm.setInt(1, _newMasterUserID);
            pstm.setInt(2, _equipmentInsID);

            pstm.executeUpdate();

            return true;
        }
        catch (Exception ex)
        {
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }

        return false;
    }

    /**
     * 改变装备耐久度
     * 
     * @param _equipmentInsList 需要更新耐久度的装备列表
     * @return
     */
    public static void updateEquipmentDurability (
            ArrayList<EquipmentInstance> _equipmentInsList)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            conn.setAutoCommit(false);
            pstm = conn.prepareStatement(UPDATE_EQUIPMENT_DURABILITY_SQL);

            for (EquipmentInstance ei : _equipmentInsList)
            {
                pstm.setInt(1, ei.getCurrentDurabilityPoint());
                pstm.setInt(2, ei.getInstanceID());

                pstm.addBatch();
            }

            pstm.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        }
        catch (Exception ex)
        {
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }
    }

    /**
     * 解除装备封印
     * 
     * @param _equipmentIns 解除封印的装备实例
     * @return
     */
    public static void removeEquipmentSeal (EquipmentInstance _equipmentIns)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(UPDATE_EQUIPMENT_SEAL_SQL);

            pstm.setInt(1, _equipmentIns.getInstanceID());

            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }
    }

    /**
     * 穿上装备时绑定
     * 
     * @param _equipmentIns 解除封印的装备实例
     * @return
     */
    public static void bindEquipment (EquipmentInstance _equipmentIns)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(UPDATE_EQUIPMENT_BIND_SQL);

            pstm.setInt(1, _equipmentIns.getInstanceID());

            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }
    }

    /**
     * 改变武器血腥强化数据
     * 
     * @param _weaponInstanceID 武器实例编号
     * @param _bloodyEnhanceDesc 血腥强化描述
     */
    public static void updateWeaponBloodyEnhance (int _weaponInstanceID,
            String _bloodyEnhanceDesc)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(UPDATE_WEAPON_ENHANCE_SQL);
            pstm.setString(1, _bloodyEnhanceDesc);
            pstm.setInt(2, _weaponInstanceID);

            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }
    }

    /**
     * 改变装备普通强化数据
     * 
     * @param _equipmentInstanceID 装备实例编号
     * @param _enhanceDesc 普通强化描述
     */
    public static void updateEquipmentEnhance (int _equipmentInstanceID,
            String _enhanceDesc)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(UPDATE_EQUIPMENT_ENHANCE_SQL);
            pstm.setString(1, _enhanceDesc);
            pstm.setInt(2, _equipmentInstanceID);

            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }
    }

    /**
     * 整理包裹装备，重新设置在包裹中的index
     * 
     * @param _equipmentList
     */
    public static void clearUpEquipmentList (EquipmentInstance[] _equipmentList)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            conn.setAutoCommit(false);
            pstm = conn.prepareStatement(CLEAR_UP_EQUIPMENT_PACKAGE_BATCH_SQL);

            for (short i = 0; i < _equipmentList.length; i++)
            {
                if (null != _equipmentList[i])
                {
                    pstm.setShort(1, i);
                    pstm.setInt(2, _equipmentList[i].getInstanceID());

                    pstm.addBatch();
                }
            }

            pstm.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        }
        catch (Exception ex)
        {
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }
    }
    
    /**
     * 初始化最大特殊物品ID
     */
    public static void load()
    {
    	Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SELECT_MAX_SPECAIL_GOODS_ID);
            resultSet = pstm.executeQuery();

            if (resultSet.next())
            {
                int maxGuildID = resultSet.getInt("max_goods_id");

                if (maxGuildID > 0)
                {
                    GoodsServiceImpl.getInstance().setUseableSpecailID(++maxGuildID);
                }
            }
        } 
        catch(Exception e) 
        {
        	log.error(": ",e);
        }
        finally
        {
            try
            {
                if (null != resultSet)
                {
                    resultSet.close();
                }
                if (null != pstm)
                {
                    pstm.close();
                }
                if (null != conn)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加非装备物品
     * 
     * @param _userID 角色标识
     * @param _singleGoodsType 物品类型
     * @param _goodsID 物品标识
     * @param _goodsNums 物品数量
     * @param _packageGridIndex 在包裹中的位置
     */
    public static void addSingleGoods (int _userID, byte _singleGoodsType,
            int _goodsID, int _goodsNums, int _packageGridIndex)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        PreparedStatement pstmInsert = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(INSERT_SINGLE_GOODS_SQL);
            pstm.setInt(1, _userID);
            pstm.setShort(2, _singleGoodsType);
            pstm.setInt(3, _goodsID);
            pstm.setInt(4, _goodsNums);
            pstm.setInt(5, _packageGridIndex);
            int id = GoodsServiceImpl.getInstance().getUseableSpecailID();
            pstm.setInt(6, id);
            

            pstm.executeUpdate();
//            GoodsServiceImpl.getInstance().useableSpecailIDAdd();
            pstm.close();
            pstm = null;
            if(_singleGoodsType == SingleGoods.TYPE_SPECIAL_GOODS)
            {
            	SpecialGoods specialGoods = (SpecialGoods) GoodsContents.getGoods(_goodsID);
            	//add by zhengl; date: 2011-03-15; note: 为了添加大补丸这类物品而不得已的修改.考虑将来优化
            	if(specialGoods.getType() == ESpecialGoodsType.BIG_TONIC)
            	{
            		BigTonicBall ball = (BigTonicBall)specialGoods;
            		//由包裹格子位置,userid,物品ID是一定能找出该物品来的,这个不用担心
            		String sql = "select id from player_single_goods where user_id=?"
            			+ " and goods_id=? and package_index=?";
            		pstm = conn.prepareStatement(sql);
            		pstm.setInt(1, _userID);
            		pstm.setInt(2, _goodsID);
            		pstm.setInt(3, _packageGridIndex);
            		ResultSet set = pstm.executeQuery();
            		while (set.next()) {
//            			int id = set.getInt("id");
            			sql = "insert into big_tonic_ball (single_goods_id,tonic_id,surplus_point,type) " 
            				+ " VALUES (?,?,?,?)";
            			pstmInsert = conn.prepareStatement(sql);
            			pstmInsert.setInt(1, id);
            			pstmInsert.setInt(2, _goodsID);
            			pstmInsert.setInt(3, ball.surplusPoint);
            			pstmInsert.setInt(4, ball.isActivate);
            			pstmInsert.executeUpdate();
					}
            	} else if(specialGoods.getType() == ESpecialGoodsType.PET_PER) {
            		//add by zhengl; date: 2011-03-20; note: 添加宠物卡
            		PetPerCard card = (PetPerCard)specialGoods;
            		//由包裹格子位置,userid,物品ID是一定能找出该物品来的,这个不用担心
            		String sql = "select id from player_single_goods where user_id=?"
            			+ " and goods_id=? and package_index=?";
            		pstm = conn.prepareStatement(sql);
            		pstm.setInt(1, _userID);
            		pstm.setInt(2, _goodsID);
            		pstm.setInt(3, _packageGridIndex);
            		ResultSet set = pstm.executeQuery();
            		while (set.next()) {
//            			int id = set.getInt("id");
            			sql = "insert into pet_per_card (single_goods_id,card_id,surplus_point) " 
            				+ " VALUES (?,?,?)";
            			pstmInsert = conn.prepareStatement(sql);
            			pstmInsert.setInt(1, id);
            			pstmInsert.setInt(2, _goodsID);
            			pstmInsert.setInt(3, card.surplusPoint);
            			pstmInsert.executeUpdate();
					}
				}
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
                    pstmInsert.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }
    }

    /**
     * 向背包添加新装备物品
     * 
     * @param _userID 玩家userID
     * @param _ei 装备实例
     * @param _gridIndex 背包格子位置
     */
    public static void buildEquipment2Bag (int _userID, EquipmentInstance _ei,
            int _gridIndex)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(INSERT_EQUIPMENT_INSTANCE_SQL);

            pstm.setInt(1, _ei.getInstanceID());
            pstm.setInt(2, _ei.getArchetype().getID());
            pstm.setInt(3, _ei.getCreatorUserID());
            pstm.setInt(4, _ei.getOwnerUserID());
            pstm.setInt(5, _ei.getCurrentDurabilityPoint());
            pstm.setByte(6, (byte) (_ei.existSeal() ? 1 : 0));
            pstm.setByte(7, (byte) (_ei.isBind() ? 1 : 0));
            pstm.setShort(8, _ei.getOwnerType());

            if (pstm.executeUpdate() == 1)
            {
                pstm.close();
                pstm = null;
                
                pstm = conn.prepareStatement(INSERT_EQUIPMENT_BAG_SQL);
                
                pstm.setInt(1, _ei.getInstanceID());
                pstm.setInt(2, _userID);
                pstm.setShort(3, (short) _gridIndex);
                
                if(_ei.getOwnerType() == 1){
                    pstm.setShort(4, (short)1);
                }else if(_ei.getOwnerType() == 2){
                	// 向宠物装备包里加装备,宠物装备保存在表 player_carry_equipment 里，
                	// 用 container_type=3 标志,container_type=4 标志在宠物身上
                	pstm.setShort(4, (short)3);
                }

                pstm.executeUpdate();
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
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }
    }

    /**
     * 向实例表中插入装备实例
     * 
     * @param _ei 装备实例
     */
    public static void buildEquipmentInstance (EquipmentInstance _ei)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        log.debug("向实例表中插入装备实例 id=" +_ei.getInstanceID()+"  ownertype="+_ei.getOwnerType());
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(INSERT_EQUIPMENT_INSTANCE_SQL);

            pstm.setInt(1, _ei.getInstanceID());
            pstm.setInt(2, _ei.getArchetype().getID());
            pstm.setInt(3, _ei.getCreatorUserID());
            pstm.setInt(4, _ei.getOwnerUserID());
            pstm.setInt(5, _ei.getCurrentDurabilityPoint());
            pstm.setByte(6, (byte) (_ei.existSeal() ? 1 : 0));
            pstm.setByte(7, (byte) (_ei.isBind() ? 1 : 0));
            pstm.setShort(8, _ei.getOwnerType());

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
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }
    }

    /**
     * 向玩家身上添加新装备
     * 
     * @param _userID 玩家userID
     * @param _ei 装备实例
     */
    public static void buildEquipment2Body (int _userID, EquipmentInstance _ei)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(INSERT_EQUIPMENT_INSTANCE_SQL);

            pstm.setInt(1, _ei.getInstanceID());
            pstm.setInt(2, _ei.getArchetype().getID());
            pstm.setInt(3, _ei.getCreatorUserID());
            pstm.setInt(4, _ei.getOwnerUserID());
            pstm.setInt(5, _ei.getCurrentDurabilityPoint());
            pstm.setByte(6, (byte) (_ei.existSeal() ? 1 : 0));
            pstm.setByte(7, (byte) (_ei.isBind() ? 1 : 0));
            pstm.setShort(8, _ei.getOwnerType());

            if (pstm.executeUpdate() == 1)
            {
                pstm.close();
                pstm = null;
                
                pstm = conn.prepareStatement(INSERT_EQUIPMENT_TO_BODY_SQL);

                pstm.setInt(1, _ei.getInstanceID());
                pstm.setInt(2, _userID);
                pstm.setShort(3, EquipmentBag.BODY);
                
                pstm.executeUpdate();
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
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }
    }

    /**
     * 向背包添加装备
     * 
     * @param _userID 玩家userID
     * @param _ei 装备实例
     * @param _packageIndex 背包格子位置
     */
    public static void addEquipment2Bag (int _userID, EquipmentInstance _ei,
            int _packageIndex)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(INSERT_EQUIPMENT_BAG_SQL);

            pstm.setInt(1, _ei.getInstanceID());
            pstm.setInt(2, _userID);
            pstm.setShort(3, (short) _packageIndex);
            
            if(_ei.getOwnerType() == 1){
                pstm.setShort(4, (short)1);
            }else if(_ei.getOwnerType() == 2){
            	// 向宠物装备包里加装备,宠物装备保存在表 player_carry_equipment 里，
            	//用 container_type=3 标志在宠物背包里,container_type=4 标志在宠物身上
            	pstm.setShort(4, (short)3);
            }

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
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }
    }
    
    /**
     * 使用宠物卡
     * @param _userID
     * @param _bagGridIndex
     * @param _goodsID
     * @param _surplusPoint
     */
    public static void updatePetPer(int _userID, int _bagGridIndex, int _goodsID, 
    		int _surplusPoint)
    {
		//由包裹格子位置,userid,物品ID是一定能找出该物品来的,这个不用担心
		String sql = "select id from player_single_goods where" 
			+ " user_id=? and goods_id=? and package_index=?";
        Connection conn = null;
        PreparedStatement pstm = null;
        PreparedStatement pstmCard = null;
        ResultSet resultSet = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, _userID);
            pstm.setInt(2, _goodsID);
            pstm.setInt(3, _bagGridIndex);
            resultSet = pstm.executeQuery();
            while (resultSet.next()) {
            	int id = resultSet.getInt("id");
            	sql = "update pet_per_card set surplus_point=? where single_goods_id=?";
            	pstmCard = conn.prepareStatement(sql);
            	pstmCard.setInt(1, _surplusPoint);
            	pstmCard.setInt(2, id);
            	pstmCard.executeUpdate();
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
                    pstmCard.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }
    }
    
    /**
     * 查询大补丸的索引ID.
     * @param _userID
     * @param _bagGridIndex
     * @param _goodsID
     * @param _surplusPoint
     * @param _type
     * @return
     */
    public static int selectTonic(int _userID, int _bagGridIndex, int _goodsID)
    {
		//由包裹格子位置,userid,物品ID是一定能找出该物品来的,这个不用担心
		String sql = "select id from player_single_goods where" 
			+ " user_id=? and goods_id=? and package_index=?";
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;
        int tonicID = 0;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, _userID);
            pstm.setInt(2, _goodsID);
            pstm.setInt(3, _bagGridIndex);
            resultSet = pstm.executeQuery();
            while (resultSet.next()) {
            	tonicID = resultSet.getInt("id");
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
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }
        return tonicID;
    }
    /**
     * 更新大补丸的最新变化
     * @param _userID
     * @param _bagGridIndex
     * @param _goodsID
     * @param _surplusPoint
     * @param _type
     */
    public static void updateTonic(int _userID, int _bagGridIndex, int _goodsID, 
    		int _surplusPoint, int _type)
    {
		//由包裹格子位置,userid,物品ID是一定能找出该物品来的,这个不用担心
		String sql = "select id from player_single_goods where" 
			+ " user_id=? and goods_id=? and package_index=?";
        Connection conn = null;
        PreparedStatement pstm = null;
        PreparedStatement pstmTonic = null;
        ResultSet resultSet = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, _userID);
            pstm.setInt(2, _goodsID);
            pstm.setInt(3, _bagGridIndex);
            resultSet = pstm.executeQuery();
            while (resultSet.next()) {
            	int id = resultSet.getInt("id");
            	sql = "update big_tonic_ball set surplus_point=?,type=? where single_goods_id=?";
            	pstmTonic = conn.prepareStatement(sql);
            	pstmTonic.setInt(1, _surplusPoint);
            	pstmTonic.setInt(2, _type);
            	pstmTonic.setInt(3, id);
            	pstmTonic.executeUpdate();
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
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }
    }

    /**
     * 更新某格子位置的物品数量
     * 
     * @param _userID 玩家userID
     * @param _goodsID 物品编号
     * @param _number 数量
     * @param _bagGridIndex 背包格子位置
     * @return 是否成功
     */
    public static boolean updateGridSingleGoodsNumberOfBag (int _userID,
            int _goodsID, int _number, short _bagGridIndex)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(UPDATA_SINGLE_GOODS_BAG_SQL);
            pstm.setInt(1, _number);
            pstm.setInt(2, _userID);
            pstm.setInt(3, _goodsID);
            pstm.setShort(4, _bagGridIndex);

            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return false;
        }
        finally
        {
            try
            {
                if (pstm != null)
                {
                    pstm.close();
                }
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
            }
        }

        return true;
    }
    
    /**
     * 只加载玩家已装备的装备物品
     * @param _player
     */
    public static void loadPlayerWearGoods(HeroPlayer _player){
    	Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SELECT_EQUIPMENT_SQL);
            pstm.setInt(1, _player.getUserID());

            resultSet = pstm.executeQuery();

            int instanceID, equipmentID, creatorUserID, currentDurabilityPoint;
            short containerType, packageIndex;
            String genericEnhanceDesc, bloodyEnhanceDesc;
            byte existSeal, isBind;

            while (resultSet.next())
            {
                instanceID = resultSet.getInt("instance_id");
                equipmentID = resultSet.getInt("equipment_id");
                creatorUserID = resultSet.getInt("creator_user_id");
                currentDurabilityPoint = resultSet.getInt("current_durability");
                containerType = resultSet.getShort("container_type");
                packageIndex = resultSet.getShort("package_index");
                genericEnhanceDesc = resultSet
                        .getString("generic_enhance_desc");
                bloodyEnhanceDesc = resultSet.getString("bloody_enhance_desc");
                existSeal = resultSet.getByte("be_sealed");
                isBind = resultSet.getByte("bind");

                EquipmentInstance ei = EquipmentFactory.getInstance()
                        .buildFromDB(creatorUserID, _player.getUserID(),
                                instanceID, equipmentID,
                                currentDurabilityPoint, existSeal, isBind); 
                
                if (null != ei)
                {
                	if(ei.getOwnerType() == 1)
                        EnhanceService.getInstance().parseEnhanceDesc(ei,
                                genericEnhanceDesc, bloodyEnhanceDesc);

                    if (EquipmentContainer.BODY == containerType)
                    {
                        _player.getBodyWear().wear(ei);
                    }
                } else {
                	log.debug("--loadPlayerWearGoods-用户ID为:"+_player.getUserID()+"的用户---");
                	log.debug("--loadPlayerWearGoods-加载instanceID为:"+instanceID+"的时候获得的是NULL---");
                }
            }
        }catch(Exception e){
        	log.error("只加载玩家已装备上的装备物品时 error : ",e);
        }finally
        {
            try
            {
                if (null != resultSet)
                {
                    resultSet.close();
                }
                if (null != pstm)
                {
                    pstm.close();
                }
                if (null != conn)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 只加载玩家的装备
     * @param _player
     */
    public static void loadPlayerEquipment(HeroPlayer _player){
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;
        PreparedStatement pstmSpecial = null;
        ResultSet specialSet = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SELECT_EQUIPMENT_SQL);
            pstm.setInt(1, _player.getUserID());

            resultSet = pstm.executeQuery();

            int instanceID, equipmentID, creatorUserID, currentDurabilityPoint;
            short containerType, packageIndex;
            String genericEnhanceDesc, bloodyEnhanceDesc;
            byte existSeal, isBind;

            while (resultSet.next())
            {
                instanceID = resultSet.getInt("instance_id");
                equipmentID = resultSet.getInt("equipment_id");
                creatorUserID = resultSet.getInt("creator_user_id");
                currentDurabilityPoint = resultSet.getInt("current_durability");
                containerType = resultSet.getShort("container_type");
                packageIndex = resultSet.getShort("package_index");
                genericEnhanceDesc = resultSet
                        .getString("generic_enhance_desc");
                bloodyEnhanceDesc = resultSet.getString("bloody_enhance_desc");
                existSeal = resultSet.getByte("be_sealed");
                isBind = resultSet.getByte("bind");

                EquipmentInstance ei = EquipmentFactory.getInstance()
                        .buildFromDB(creatorUserID, _player.getUserID(),
                                instanceID, equipmentID,
                                currentDurabilityPoint, existSeal, isBind);

                if (null != ei)
                {
                	if(ei.getOwnerType() == 1)
                        EnhanceService.getInstance().parseEnhanceDesc(ei,
                                genericEnhanceDesc, bloodyEnhanceDesc);

                    if (EquipmentContainer.BODY == containerType)
                    {
                        _player.getBodyWear().wear(ei);
                    }else if(EquipmentContainer.PET_BAG == containerType){//玩家宠物背包
                    	_player.getInventory().getPetEquipmentBag().add(packageIndex, ei);
                    }else if(EquipmentContainer.PET_BODY == containerType){
                    	//此时该装备在宠物身上，所以不在玩家的宠物背包里显示，只在宠物身上显示
                    	continue;
                    }
                    else
                    {
                        _player.getInventory().getEquipmentBag().add(
                                packageIndex, ei);
                    }
                } else {
                	log.debug("---只加载玩家的装备 用户ID为:"+_player.getUserID()+"的用户---");
                	log.debug("---只加载玩家的装备 加载instanceID为:"+instanceID+"的时候获得的是NULL---");
                }
            }

            if (null != resultSet)
            {
                resultSet.close();
            }

                resultSet = null;
                pstm.close();
                pstm = null;
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (null != resultSet)
                {
                    resultSet.close();
                }
                if (null != pstm)
                {
                    pstm.close();
                }
                if (specialSet != null) {
                	specialSet.close();
				}
                if (pstmSpecial != null) {
                	pstmSpecial.close();
				}
                if (null != conn)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 加载玩家物品（背包、穿戴）
     * 
     * @param _player 玩家
     */
    public static void loadPlayerGoods (HeroPlayer _player)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;
        PreparedStatement pstmSpecial = null;
        ResultSet specialSet = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SELECT_EQUIPMENT_SQL);
            pstm.setInt(1, _player.getUserID());

            resultSet = pstm.executeQuery();

            int instanceID, equipmentID, creatorUserID, currentDurabilityPoint;
            short containerType, packageIndex;
            String genericEnhanceDesc, bloodyEnhanceDesc;
            byte existSeal, isBind;

            while (resultSet.next())
            {
                instanceID = resultSet.getInt("instance_id");
                equipmentID = resultSet.getInt("equipment_id");
                creatorUserID = resultSet.getInt("creator_user_id");
                currentDurabilityPoint = resultSet.getInt("current_durability");
                containerType = resultSet.getShort("container_type");
                packageIndex = resultSet.getShort("package_index");
                genericEnhanceDesc = resultSet
                        .getString("generic_enhance_desc");
                bloodyEnhanceDesc = resultSet.getString("bloody_enhance_desc");
                existSeal = resultSet.getByte("be_sealed");
                isBind = resultSet.getByte("bind");

                EquipmentInstance ei = EquipmentFactory.getInstance()
                        .buildFromDB(creatorUserID, _player.getUserID(),
                                instanceID, equipmentID,
                                currentDurabilityPoint, existSeal, isBind); 
                
                if (null != ei)
                {
                	if(ei.getOwnerType() == 1)
                        EnhanceService.getInstance().parseEnhanceDesc(ei,
                                genericEnhanceDesc, bloodyEnhanceDesc);

                    if (EquipmentContainer.BODY == containerType)
                    {
                        _player.getBodyWear().wear(ei);
                    }else if(EquipmentContainer.PET_BAG == containerType){//玩家宠物背包
                    	_player.getInventory().getPetEquipmentBag().add(packageIndex, ei);
                    }else if(EquipmentContainer.PET_BODY == containerType){
                    	//此时该装备在宠物身上，所以不在玩家的宠物背包里显示，只在宠物身上显示
                    	continue;
                    }
                    else
                    {
                        _player.getInventory().getEquipmentBag().add(
                                packageIndex, ei);
                    }
                } else {
                	log.debug("---用户ID为:"+_player.getUserID()+"的用户---");
                	log.debug("---加载instanceID为:"+instanceID+"的时候获得的是NULL---");
                }
            }

            if (null != resultSet)
            {
                resultSet.close();
            }

            resultSet = null;
            pstm.close();
            pstm = null;

            pstm = conn.prepareStatement(SELECT_SINGLE_GOODS_SQL);
            pstm.setInt(1, _player.getUserID());

            resultSet = pstm.executeQuery();

            int goodsID, number, specialID;
            short goodsType;

            while (resultSet.next())
            {
                goodsID = resultSet.getInt("goods_id");
                goodsType = resultSet.getShort("goods_type");
                number = resultSet.getInt("goods_number");
                packageIndex = resultSet.getShort("package_index");

                switch (goodsType)
                {
                    case SingleGoods.TYPE_MATERIAL:
                    {
                        _player.getInventory().getMaterialBag().load(goodsID,
                                number, packageIndex);

                        break;
                    }
                    case SingleGoods.TYPE_MEDICAMENT:
                    {
                        _player.getInventory().getMedicamentBag().load(goodsID,
                                number, packageIndex);

                        break;
                    }
                    case SingleGoods.TYPE_TASK_TOOL:
                    {
                        _player.getInventory().getTaskToolBag().load(goodsID,
                                number, packageIndex);

                        break;
                    }
                    case SingleGoods.TYPE_SPECIAL_GOODS:
                    {
                    	_player.getInventory().getSpecialGoodsBag().load(
                    			goodsID, number, packageIndex);
                    	//add by zhengl; date: 2011-03-15; note: 加载大补丸
                    	SpecialGoods specialGoods = (SpecialGoods) GoodsContents.getGoods(goodsID);
                    	if(specialGoods instanceof BigTonicBall)
                    	{
                    		specialID = resultSet.getInt("id");
                    		pstmSpecial = conn.prepareStatement(SELECT_TONIC_GOODS_SQL);
                    		pstmSpecial.setInt(1, specialID);
                    		specialSet = pstmSpecial.executeQuery();
                    		while (specialSet.next()) {
                    			int surplus = specialSet.getInt("surplus_point");
                    			int type = specialSet.getInt("type");
                    			_player.getInventory().getSpecialGoodsBag().loadBigTonicBall(
                    					goodsID, number, packageIndex, surplus, type, specialGoods);
                    		}
                    	} else if (specialGoods instanceof PetPerCard) {
                    		specialID = resultSet.getInt("id");
                    		pstmSpecial = conn.prepareStatement(SELECT_PET_PER_CARD_GOODS_SQL);
                    		pstmSpecial.setInt(1, specialID);
                    		specialSet = pstmSpecial.executeQuery();
                    		while (specialSet.next()) {
                    			int surplus = specialSet.getInt("surplus_point");
                    			_player.getInventory().getSpecialGoodsBag().loadBigPetPerCard(
                    					goodsID, number, packageIndex, surplus,  specialGoods);
                    		}
						}

                        break;
                    }
                    case SingleGoods.TYPE_PET_GOODS:
                    {
                    	_player.getInventory().getPetGoodsBag().load(goodsID, number, packageIndex);
                    	break;
                    }
                }
            }
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (null != resultSet)
                {
                    resultSet.close();
                }
                if (null != pstm)
                {
                    pstm.close();
                }
                if (specialSet != null) {
                	specialSet.close();
				}
                if (pstmSpecial != null) {
                	pstmSpecial.close();
				}
                if (null != conn)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 加载玩家仓库特殊物品
     * @param _player
     * @param _goodsID
     * @param _specialID
     * @param number
     */
    public static void getStorageSpecialGoods(HeroPlayer _player, int _goodsID, int _specialID, 
    		int number, int packageIndex)
    {
        Connection conn = null;
        ResultSet resultSet = null;
        PreparedStatement pstmSpecial = null;
    	try {
    		
        	SpecialGoods specialGoods = (SpecialGoods) GoodsContents.getGoods(_goodsID);
        	conn = DBServiceImpl.getInstance().getConnection();
        	if(specialGoods instanceof BigTonicBall)
        	{
        		//通过仓库关联ID加载具体大补丸信息,再用这些信息生成一个大补丸.
        		pstmSpecial = conn.prepareStatement(SELECT_TONIC_GOODS_SQL);
        		pstmSpecial.setInt(1, _specialID);
        		resultSet = pstmSpecial.executeQuery();
        		while (resultSet.next()) 
        		{
        			int surplus = resultSet.getInt("surplus_point");
        			int type = resultSet.getInt("type");
        			_player.getInventory().getSpecialGoodsBag().loadBigTonicBall(
        					_goodsID, number, packageIndex, surplus, type, specialGoods);
        			//user_id,goods_type,goods_id,goods_number,package_index,id
        			pstmSpecial = conn.prepareStatement(INSERT_SINGLE_GOODS_SQL);
        			pstmSpecial.setInt(1, _player.getUserID());
        			pstmSpecial.setInt(2, ((SingleGoods) specialGoods).getSingleGoodsType());
        			pstmSpecial.setInt(3, _goodsID);
        			pstmSpecial.setInt(4, number);
        			pstmSpecial.setInt(5, packageIndex);
        			pstmSpecial.setInt(6, _specialID);
        			pstmSpecial.executeUpdate();
        		}
        	} else if (specialGoods instanceof PetPerCard) {
        		pstmSpecial = conn.prepareStatement(SELECT_PET_PER_CARD_GOODS_SQL);
        		pstmSpecial.setInt(1, _specialID);
        		resultSet = pstmSpecial.executeQuery();
        		while (resultSet.next()) 
        		{
        			int surplus = resultSet.getInt("surplus_point");
        			_player.getInventory().getSpecialGoodsBag().loadBigPetPerCard(
        					_goodsID, number, packageIndex, surplus,  specialGoods);
        			
        			pstmSpecial = conn.prepareStatement(INSERT_SINGLE_GOODS_SQL);
        			pstmSpecial.setInt(1, _player.getUserID());
        			pstmSpecial.setInt(2, ((SingleGoods) specialGoods).getSingleGoodsType());
        			pstmSpecial.setInt(3, _goodsID);
        			pstmSpecial.setInt(4, number);
        			pstmSpecial.setInt(5, packageIndex);
        			pstmSpecial.setInt(6, _specialID);
        			pstmSpecial.executeUpdate();
        		}
    		}
		} 
    	catch (Exception e) {
			e.printStackTrace();
		}
        finally
        {
            try
            {
                if (pstmSpecial != null)
                	pstmSpecial.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
            }
        }

    }

    // @ TODO add by lulin
    public static EquipmentInstance getEquipmentInstanceFromDB (int _instanceID)
    {
        EquipmentInstance instance = null;
        Connection conn = null;
        PreparedStatement pstm = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SEL_INSTANCE_SQL);
            pstm.setInt(1, _instanceID);
            ResultSet rs = pstm.executeQuery();
            if (rs.next())
            {
                int equipmentID = rs.getInt("equipment_id");
                int creatorUserID = rs.getInt("creator_user_id");
                int ownerUserID = rs.getInt("owner_user_id");
                int currentDurabilityPoint = rs.getInt("current_durability");
                String genericEnhanceDesc = rs
                        .getString("generic_enhance_desc");
                String bloodyEnhanceDesc = rs.getString("bloody_enhance_desc");
                byte existSeal = rs.getByte("be_sealed");
                byte isBind = rs.getByte("bind");

                instance = EquipmentFactory.getInstance().buildFromDB(
                        creatorUserID, ownerUserID, _instanceID, equipmentID,
                        currentDurabilityPoint, existSeal, isBind);

                EnhanceService.getInstance().parseEnhanceDesc(instance,
                        genericEnhanceDesc, bloodyEnhanceDesc);
            }

            rs.close();
            rs = null;
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
                    pstm.close();
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e)
            {
            }
        }
        return instance;
    }

    /**
     * 更新玩家背包大小
     * 
     * @param _player
     * @return
     */
    public static void updatePlayerBagSize (HeroPlayer _player)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(UPDATE_BAG_SIZE_SQL);

            Inventory inventory = _player.getInventory();

            String bagSizeDesc = new StringBuffer().append(
                    inventory.getEquipmentBag().getSize()).append(
                    BAG_SIZE_CONNECTOR).append(
                    inventory.getMedicamentBag().getSize()).append(
                    BAG_SIZE_CONNECTOR).append(
                    inventory.getMaterialBag().getSize()).append(
                    BAG_SIZE_CONNECTOR).append(
                    inventory.getSpecialGoodsBag().getSize()).append(
                    BAG_SIZE_CONNECTOR).append(
                    inventory.getPetEquipmentBag().getSize()).append(
                    BAG_SIZE_CONNECTOR).append(
                    inventory.getPetContainer().getSize()).append(
                    BAG_SIZE_CONNECTOR).append(
                    inventory.getPetGoodsBag().getSize()).toString();

            pstm.setString(1, bagSizeDesc);
            pstm.setInt(2, _player.getUserID());

            pstm.executeUpdate();
        }
        catch (Exception ex)
        {
            LogWriter.error(null, ex);
        }
        finally
        {
            try
            {
                if (null != pstm)
                {
                    pstm.close();
                }
                if (null != conn)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 更新灵魂所在地图
     * 
     * @param _playerUserID 角色编号
     * @param _homeID 灵魂所在地图编号
     */
    public static void updateHome (int _playerUserID, short _homeID)
    {
        Connection conn = null;
        PreparedStatement ps = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            ps = conn.prepareStatement(UPDATE_HOME_SQL);

            ps.setShort(1, _homeID);
            ps.setInt(2, _playerUserID);

            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            LogWriter.error(null, e);
        }
        finally
        {
            try
            {
                if (ps != null)
                {
                    ps.close();
                    ps = null;
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
     * 同类元素之间的连接符
     */
    private static final String BAG_SIZE_CONNECTOR = "&";
}
