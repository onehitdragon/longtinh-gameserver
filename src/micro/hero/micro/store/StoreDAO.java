package hero.micro.store;

import hero.item.EquipmentInstance;
import hero.item.detail.EGoodsType;
import hero.item.enhance.EnhanceService;
import hero.item.service.EquipmentFactory;
import hero.share.service.LogWriter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import yoyo.service.tools.database.DBServiceImpl;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 StoreDAO.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-12-15 下午02:03:45
 * @描述 ：
 */

public class StoreDAO
{
    private static Logger log = Logger.getLogger(StoreDAO.class);
    /**
     * 加载商店物品脚本
     */
    private static final String SELECT_STORE_SQL = "SELECT * FROM store_goods left join equipment_instance"
                                                         + " ON store_goods.goods_id=equipment_instance.instance_id"
                                                         + " WHERE store_goods.user_id = ? LIMIT "
                                                         + PersionalStore.MAX_SIZE;

    /**
     * 插入商店物品
     */
    private static final String INSERT_STORE_SQL = "INSERT INTO store_goods"
                                                         + " VALUES(?,?,?,?,?,?)";

    /**
     * 删除商店物品
     */
    private static final String DELETE_STORE_SQL = "DELETE FROM store_goods"
                                                         + " WHERE user_id = ? AND grid_index = ?";

    /**
     * 修改商品价格
     */
    private static final String UPDATE_PRICE_SQL = "UPDATE store_goods SET sale_price = ? WHERE"
                                                         + " user_id = ? AND grid_index = ?"
                                                         + " AND goods_id = ?";

    /**
     * 加载商店
     * 
     * @param _userID
     * @return
     */
    public static PersionalStore loadStore (int _userID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SELECT_STORE_SQL);

            pstm.setInt(1, _userID);
            set = pstm.executeQuery();

            PersionalStore store = null;
            EquipmentInstance instance;

            while (set.next())
            {
                if (null == store)
                {
                    store = new PersionalStore();
                }

                byte gridIndex, goodsType;
                short number;
                int goodsID, salePrice;

                int equipmentID, creatorUserID, ownerUserID, currentDurabilityPoint;
                String genericEnhanceDesc, bloodyEnhanceDesc;
                byte existSeal,isBind;

                gridIndex = set.getByte(2);
                goodsType = set.getByte(3);
                goodsID = set.getInt(4);
                number = set.getShort(5);
                salePrice = set.getInt(6);
                log.debug("load store gridindex="+gridIndex+",goodstype="+goodsType+",goodsid="+goodsID+",number="+number+",saleprice="+salePrice);
                if (EGoodsType.EQUIPMENT.value() == goodsType)
                {
                    equipmentID = set.getInt(8);
                    creatorUserID = set.getInt(9);
                    ownerUserID = set.getInt(10);
                    currentDurabilityPoint = set.getInt(11);
                    genericEnhanceDesc = set.getString(12);
                    bloodyEnhanceDesc = set.getString(13);
                    existSeal = set.getByte(14);
                    isBind = set.getByte(15);

                    instance = EquipmentFactory.getInstance().buildFromDB(
                            creatorUserID, ownerUserID, goodsID, equipmentID,
                            currentDurabilityPoint, existSeal, isBind);
                    if(instance.getOwnerType() == 1)
                        EnhanceService.getInstance().parseEnhanceDesc(instance,
                                genericEnhanceDesc, bloodyEnhanceDesc);

                    store.add(goodsType, gridIndex, 0, (short) 0, instance,
                            salePrice);
                }
                else
                {
                    store.add(goodsType, gridIndex, goodsID, number, null,
                            salePrice);
                }
            }

            return store;
        }
        catch (Exception e)
        {
            LogWriter.error(null, e);
        }
        finally
        {
            try
            {
                if (null != set)
                {
                    set.close();
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
            catch (Exception e)
            {

            }
        }

        return null;
    }

    /**
     * 将物品存储到商店中
     * 
     * @param _userID
     * @return
     */
    public static boolean insertGoods2Store (int _userID,
            int[][] _newGoodsDataList)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            conn.setAutoCommit(false);
            pstm = conn.prepareStatement(INSERT_STORE_SQL);

            for (int[] data : _newGoodsDataList)
            {
                pstm.setInt(1, _userID);
                pstm.setByte(2, (byte) data[4]);
                pstm.setByte(3, (byte) data[0]);
                pstm.setInt(4, data[2]);
                pstm.setShort(5, (short) data[3]);
                pstm.setInt(6, data[5]);

                pstm.addBatch();
            }

            pstm.executeBatch();
            conn.commit();
        }
        catch (Exception e)
        {
            LogWriter.error(null, e);
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
                    if (!conn.getAutoCommit())
                    {
                        conn.setAutoCommit(true);
                    }

                    conn.close();
                }
            }
            catch (Exception e)
            {

            }
        }

        return true;
    }

    /**
     * 将物品从商店删除
     * 
     * @param _userID
     * @return
     */
    public static boolean removeFromStore (int _userID, byte _gridIndex)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DELETE_STORE_SQL);
            pstm.setInt(1, _userID);
            pstm.setShort(2, _gridIndex);

            if (pstm.executeUpdate() == 1)
            {
                return true;
            }
        }
        catch (Exception e)
        {
            LogWriter.error(null, e);
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
            catch (Exception e)
            {

            }
        }

        return false;
    }

    /**
     * 修改商品价格
     * 
     * @param _userID 角色编号
     * @param _gridIndex 商品位置
     * @param _goodsID 商品编号
     * @param _newPrice 新的价格
     * @return 是否修改成功
     */
    public static boolean changePrice (int _userID, byte _gridIndex,
            int _goodsID, int _newPrice)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(UPDATE_PRICE_SQL);
            pstm.setInt(1, _newPrice);
            pstm.setInt(2, _userID);
            pstm.setByte(3, _gridIndex);
            pstm.setInt(4, _goodsID);

            if (pstm.executeUpdate() == 1)
            {
                return true;
            }
        }
        catch (Exception e)
        {
            LogWriter.error(null, e);
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
            catch (Exception e)
            {

            }
        }

        return false;
    }
}
