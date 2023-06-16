package hero.npc.function.system.auction;

import hero.item.EquipmentInstance;
import hero.item.Goods;
import hero.item.dictionary.GoodsContents;
import hero.item.enhance.EnhanceService;
import hero.item.service.EquipmentFactory;
import hero.log.service.LogServiceImpl;
import hero.npc.function.system.postbox.Mail;
import hero.npc.function.system.postbox.MailService;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.tools.database.DBServiceImpl;


public class AuctionDict
{
    private static AuctionDict                            instance;

    /**
     * 拍卖ID
     */
    private int                                           auctionID;

    /**
     * 存放拍卖的武器
     */
    private ArrayList<AuctionGoods>                       weaponGoods;

    /**
     * 存放拍卖的布甲
     */
    private ArrayList<AuctionGoods>                       bjGoods;

    /**
     * 存放拍卖的轻甲
     */
    private ArrayList<AuctionGoods>                       qjGoods;

    /**
     * 存放拍卖的重甲
     */
    private ArrayList<AuctionGoods>                       zjGoods;

    /**
     * 存放拍卖的配饰
     */
    private ArrayList<AuctionGoods>                       psGoods;

    /**
     * 存放拍卖的药水
     */
    private ArrayList<AuctionGoods>                       xhdjGoods;

    /**
     * 存放拍卖的材料
     */
    private ArrayList<AuctionGoods>                       clGoods;

    /**
     * 存放拍卖的特殊道具
     */
    private ArrayList<AuctionGoods>                       tsdjGoods;

    /**
     * 存放拍卖行中所有物品
     */
    private HashMap<AuctionType, ArrayList<AuctionGoods>> auctionGoods;

    /**
     * 拍卖费用10%
     */
    public static final int                               AUCTION_PRICE      = 10;

    /**
     * 拍卖时间
     */
    public static final long                              AUCTION_TIME       = 24 * 60 * 60 * 1000;

    /**
     * 拍卖检查时间
     */
    public static final long                              AUCTION_CHECK_TIME = 1 * 60 * 60 * 1000;

    private Timer                                         mCheckTimer;

    /**
     * 每页显示的物品数
     */
    private static final byte                             PAGE_NUM           = 10;

    private static final String                           INSERT_SQL         = "INSERT INTO auction(auction_id,goods_id,user_id,nickname,enhance_level,num,price,type,begin_time) VALUES(?,?,?,?,?,?,?,?,?)";

    private static final String                           DEL_SQL            = "DELETE FROM auction WHERE auction_id = ? LIMIT 1";

    private static final String                           SEL_EQUIPMENT_SQL  = "SELECT a.*,e.equipment_id,e.creator_user_id,e.owner_user_id,e.current_durability,e.generic_enhance_desc,e.bloody_enhance_desc,e.be_sealed,e.bind FROM auction a,equipment_instance e WHERE a.goods_id = e.instance_id AND a.type<=?";

    private static final String                           SEL_GOODS_SQL      = "SELECT * FROM auction WHERE type>=?";

    private static final String                           AUCTION_TITLE      = "拍卖行";


    


    private ReentrantLock                                 lock               = new ReentrantLock();

    private AuctionDict()
    {
        weaponGoods = new ArrayList<AuctionGoods>();
        bjGoods = new ArrayList<AuctionGoods>();
        qjGoods = new ArrayList<AuctionGoods>();
        zjGoods = new ArrayList<AuctionGoods>();
        psGoods = new ArrayList<AuctionGoods>();
        xhdjGoods = new ArrayList<AuctionGoods>();
        clGoods = new ArrayList<AuctionGoods>();
        tsdjGoods = new ArrayList<AuctionGoods>();
        auctionGoods = new HashMap<AuctionType, ArrayList<AuctionGoods>>();
        auctionGoods.put(AuctionType.WEAPON, weaponGoods);
        auctionGoods.put(AuctionType.BU_JIA, bjGoods);
        auctionGoods.put(AuctionType.QING_JIA, qjGoods);
        auctionGoods.put(AuctionType.ZHONG_JIA, zjGoods);
        auctionGoods.put(AuctionType.PEI_SHI, psGoods);
        auctionGoods.put(AuctionType.MEDICAMENT, xhdjGoods);
        auctionGoods.put(AuctionType.MATERIAL, clGoods);
        auctionGoods.put(AuctionType.SPECIAL, tsdjGoods);
        load();
        mCheckTimer = new Timer();
        CheckTask checkTask = new CheckTask();
        mCheckTimer.schedule(checkTask, AUCTION_CHECK_TIME, AUCTION_CHECK_TIME);
    }

    private void load ()
    {
        // 从数据库中加载拍卖行物品数据
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;

        auctionID = 0;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            stm = conn.prepareStatement(SEL_EQUIPMENT_SQL);
            stm.setShort(1, AuctionType.PEI_SHI.getID());

            // 加载所有装备的拍卖物品
            rs = stm.executeQuery();

            while (rs.next())
            {
                int _auctionID = rs.getInt(1);
                int _goodsID = rs.getInt(2);
                int _ownerUid = rs.getInt(3);
                String _ownerNickname = rs.getString(4);
                short _enhanceLevel = rs.getShort(5);
                short _num = rs.getShort(6);
                int _price = rs.getInt(7);
                byte _typeID = rs.getByte(8);
                long _time = rs.getLong(9);
                AuctionType _type = AuctionType.getType(_typeID);
                int _equipmentID = rs.getInt(10);
                int _creatorUserID = rs.getInt(11);
                int _ownerUserID = rs.getInt(12);
                int _currentDurabilityPoint = rs.getInt(13);
                String genericEnhanceDesc = rs.getString(14);
                String bloodyEnhanceDesc = rs.getString(15);
                byte existSeal = rs.getByte(16);
                byte isBind = rs.getByte(17);

                EquipmentInstance instance = EquipmentFactory.getInstance()
                        .buildFromDB(_creatorUserID, _ownerUserID, _goodsID,
                                _equipmentID, _currentDurabilityPoint,
                                existSeal, isBind);

                EnhanceService.getInstance().parseEnhanceDesc(instance,
                        genericEnhanceDesc, bloodyEnhanceDesc);

                AuctionGoods _goods = new AuctionGoods(_auctionID, _goodsID,
                        _ownerUid, _ownerNickname, _enhanceLevel, _num, _price,
                        _type, instance, _time);
                addAuctionGoods(_goods, false);

                if (_auctionID > auctionID)
                    auctionID = _auctionID;
            }
            rs.close();
            rs = null;
            // 加载所有非装备的拍卖物品

            stm.close();
            stm = conn.prepareStatement(SEL_GOODS_SQL);
            stm.setShort(1, AuctionType.MEDICAMENT.getID());

            rs = stm.executeQuery();
            while (rs.next())
            {
                int _auctionID = rs.getInt(1);
                int _goodsID = rs.getInt(2);
                int _ownerUid = rs.getInt(3);
                String _ownerNickname = rs.getString(4);
                short _enhanceLevel = rs.getShort(5);
                short _num = rs.getShort(6);
                int _price = rs.getInt(7);
                byte _typeID = rs.getByte(8);
                long _time = rs.getLong(9);
                AuctionType _type = AuctionType.getType(_typeID);
                EquipmentInstance instance = null;
                AuctionGoods _goods = new AuctionGoods(_auctionID, _goodsID,
                        _ownerUid, _ownerNickname, _enhanceLevel, _num, _price,
                        _type, instance, _time);
                addAuctionGoods(_goods, false);

                if (_auctionID > auctionID)
                    auctionID = _auctionID;
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

    public static AuctionDict getInstance ()
    {
        if (instance == null)
            instance = new AuctionDict();
        return instance;
    }

    /**
     * 得到可用的拍卖ID
     * 
     * @return
     */
    public int getAuctionID ()
    {
        try
        {
            lock.lock();
            return ++auctionID;
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * 添加一个物品到拍卖行中
     * 
     * @param _goods 拍卖物品
     * @param _insertFlag 是否写数据库
     */
    public void addAuctionGoods (AuctionGoods _goods, boolean _insertFlag)
    {
        try
        {
            lock.lock();
            ArrayList<AuctionGoods> goodsList = auctionGoods.get(_goods
                    .getAuctionType());
            if (goodsList != null)
            {
                goodsList.add(_goods);
            }
        }
        finally
        {
            lock.unlock();
        }
        if (_insertFlag)
            insertDB(_goods);
    }

    private void insertDB (AuctionGoods _goods)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(INSERT_SQL);
            pstm.setInt(1, _goods.getAuctionID());
            pstm.setInt(2, _goods.getGoodsID());
            pstm.setInt(3, _goods.getOwnerUserID());
            pstm.setString(4, _goods.getOwnerNickname());
            pstm.setShort(5, _goods.getEnhanceLevel());
            pstm.setShort(6, _goods.getNum());
            pstm.setInt(7, _goods.getPrice());
            pstm.setShort(8, _goods.getAuctionType().getID());
            pstm.setLong(9, _goods.getAuctionTime());
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

    private void delDB (int _auctionID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DEL_SQL);
            pstm.setInt(1, _auctionID);
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
     * 将指定拍卖物品ID，拍卖类型的拍卖物品从拍卖行中移除
     * 
     * @param _auctionID 拍卖ID
     * @param _type 拍卖类型
     * @return 拍卖物品，如果值为null说明此物品已经给别人竞拍了
     */
    public AuctionGoods removeAuctionGoods (int _auctionID, AuctionType _type)
    {
        try
        {
            lock.lock();
            ArrayList<AuctionGoods> goodsList = auctionGoods.get(_type);
            if (goodsList == null)
                return null;
            for (int i = 0; i < goodsList.size(); i++)
            {
                if (goodsList.get(i).getAuctionID() == _auctionID)
                {
                    delDB(_auctionID);
                    return goodsList.remove(i);
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
     * 根据拍卖ID和拍卖类型找到拍卖物品
     * 
     * @param _auctionID 拍卖ID
     * @param _type 拍卖物品类型
     */
    public AuctionGoods getAuctionGoods (int _auctionID, AuctionType _type)
    {
        try
        {
            lock.lock();
            ArrayList<AuctionGoods> goodsList = auctionGoods.get(_type);
            if (goodsList == null)
                return null;
            for (AuctionGoods goods : goodsList)
            {
                if (goods.getAuctionID() == _auctionID)
                    return goods;
            }
            return null;
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * 将指定页数，指定拍卖类型的物品添加到指定的拍卖物品列表中
     * 
     * @param _page 指定页数
     * @param _goods 指定存放拍卖物品的列表
     * @param _type 拍卖物品类型
     * @return int[0] - 是否有上一页，int[1] - 是否有下一页
     */
    public int[] getAuctionGoods (int _page, ArrayList<AuctionGoods> _goods,
            AuctionType _type)
    {
        try
        {
            lock.lock();
            int[] re = {0, 0 };
            ArrayList<AuctionGoods> goodsList = auctionGoods.get(_type);
            if (goodsList == null || goodsList.size() <= _page * PAGE_NUM)
                return re;
            int begin = _page * PAGE_NUM;
            int end = (_page + 1) * PAGE_NUM <= goodsList.size() ? (_page + 1)
                    * PAGE_NUM : goodsList.size();
            for (int i = begin; i < end; i++)
            {
                _goods.add(goodsList.get(i));
            }
            return re;
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * 根据指定的拍卖物品类型和名称搜索，得到符合条件的拍卖物品列表
     * 
     * @param _type
     * @param _name
     * @return
     */
    public ArrayList<AuctionGoods> sreachAuctionGoods (AuctionType _type,
            String _name)
    {
        ArrayList<AuctionGoods> tempList = new ArrayList<AuctionGoods>();
        ArrayList<AuctionGoods> goodsList = auctionGoods.get(_type);
        for (AuctionGoods g : goodsList)
        {
            if (g.getInstance() == null)
            {
                Goods goods = GoodsContents.getGoods(g.getGoodsID());
                if (goods.getName().indexOf(_name) >= 0)
                {
                    addSreachAuctionGoods(tempList, g);
                }
            }
            else
            {
                if (g.getInstance().getArchetype().getName().indexOf(_name) >= 0)
                {
                    addSreachAuctionGoods(tempList, g);
                }
            }
        }
        if (tempList.size() < 20)
            return tempList;
        ArrayList<AuctionGoods> list = new ArrayList<AuctionGoods>();
        for (int i = 0; i < 20; i++)
        {
            list.add(tempList.get(i));
        }
        return list;
    }

    private void addSreachAuctionGoods (ArrayList<AuctionGoods> _goods,
            AuctionGoods _auctionGoods)
    {
        for (int i = 0; i < _goods.size(); i++)
        {
            if (_goods.get(i).getPrice() > _auctionGoods.getPrice())
            {
                _goods.add(i, _auctionGoods);
            }
        }
        _goods.add(_auctionGoods);
    }

    private void checkTimeOut ()
    {
        ArrayList<AuctionGoods> goodsList = new ArrayList<AuctionGoods>();
        try
        {
            lock.lock();
            long nowTime = System.currentTimeMillis();
            for (int i = 0; i < weaponGoods.size(); i++)
            {
                AuctionGoods agoods = weaponGoods.get(i);
                if (nowTime - agoods.getAuctionTime() > AUCTION_TIME)
                {
                    goodsList.add(weaponGoods.remove(i));
                    i--;
                }
            }
            for (int i = 0; i < bjGoods.size(); i++)
            {
                AuctionGoods agoods = bjGoods.get(i);
                if (nowTime - agoods.getAuctionTime() > AUCTION_TIME)
                {
                    goodsList.add(bjGoods.remove(i));
                    i--;
                }
            }
            for (int i = 0; i < qjGoods.size(); i++)
            {
                AuctionGoods agoods = qjGoods.get(i);
                if (nowTime - agoods.getAuctionTime() > AUCTION_TIME)
                {
                    goodsList.add(qjGoods.remove(i));
                    i--;
                }
            }
            for (int i = 0; i < zjGoods.size(); i++)
            {
                AuctionGoods agoods = zjGoods.get(i);
                if (nowTime - agoods.getAuctionTime() > AUCTION_TIME)
                {
                    goodsList.add(zjGoods.remove(i));
                    i--;
                }
            }
            for (int i = 0; i < psGoods.size(); i++)
            {
                AuctionGoods agoods = psGoods.get(i);
                if (nowTime - agoods.getAuctionTime() > AUCTION_TIME)
                {
                    goodsList.add(psGoods.remove(i));
                    i--;
                }
            }
            for (int i = 0; i < xhdjGoods.size(); i++)
            {
                AuctionGoods agoods = xhdjGoods.get(i);
                if (nowTime - agoods.getAuctionTime() > AUCTION_TIME)
                {
                    goodsList.add(xhdjGoods.remove(i));
                    i--;
                }
            }
            for (int i = 0; i < clGoods.size(); i++)
            {
                AuctionGoods agoods = clGoods.get(i);
                if (nowTime - agoods.getAuctionTime() > AUCTION_TIME)
                {
                    goodsList.add(clGoods.remove(i));
                    i--;
                }
            }
            for (int i = 0; i < tsdjGoods.size(); i++)
            {
                AuctionGoods agoods = tsdjGoods.get(i);
                if (nowTime - agoods.getAuctionTime() > AUCTION_TIME)
                {
                    goodsList.add(tsdjGoods.remove(i));
                    i--;
                }
            }
        }
        finally
        {
            lock.unlock();
            for (AuctionGoods goods : goodsList)
            {
                // @ TODO 给拍卖者发送邮件
                AuctionType type = goods.getAuctionType();

                if (type == AuctionType.MATERIAL
                        || type == AuctionType.MEDICAMENT
                        || type == AuctionType.SPECIAL)
                {
                    Mail mail = new Mail(MailService.getInstance()
                            .getUseableMailID(), goods.getOwnerUserID(), goods
                            .getOwnerNickname(), AUCTION_TITLE,
                            Mail.TYPE_OF_SINGLE_GOODS, goods.getGoodsID(),
                            goods.getNum(), null, "", 
                            Tip.TIP_NPC_MAIL_AUCTION_TITLE + goods.getName(), 
                            new Date(System.currentTimeMillis()), (byte)2);
                    MailService.getInstance().addMail(mail, true);
                    Goods _goods = GoodsContents.getGoods(goods.getGoodsID());
                    // 邮件发送日志
                    LogServiceImpl.getInstance().mailLog(
                            0,
                            0,
                            AUCTION_TITLE,
                            "",
                            mail.getID(),
                            0,
                            goods.getOwnerNickname(),
                            0,
                            0,
                            goods.getGoodsID() + "," + _goods.getName() + ","
                                    + goods.getNum());
                }
                else
                {
                    Mail mail = new Mail(MailService.getInstance()
                            .getUseableMailID(), goods.getOwnerUserID(), goods
                            .getOwnerNickname(), AUCTION_TITLE,
                            Mail.TYPE_OF_EQUIPMENT, 0, (short) 0, goods
                                    .getInstance(), "", Tip.TIP_NPC_MAIL_AUCTION_TITLE + goods.getName(), 
                                    new Date(System.currentTimeMillis()), (byte)2);
                    MailService.getInstance().addMail(mail, true);

                    // 邮件发送日志
                    LogServiceImpl.getInstance().mailLog(
                            0,
                            0,
                            AUCTION_TITLE,
                            "",
                            mail.getID(),
                            0,
                            goods.getOwnerNickname(),
                            0,
                            0,
                            goods.getInstance().getInstanceID()
                                    + ","
                                    + goods.getInstance().getArchetype()
                                            .getName() + ",1");
                }

                HeroPlayer player = PlayerServiceImpl.getInstance()
                        .getPlayerByName(goods.getOwnerNickname());
                if (player != null && player.isEnable())
                {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_NPC_GET_NEW_MAIL));
                }
                delDB(goods.getAuctionID());
            }
            goodsList.clear();
        }
    }

    class CheckTask extends TimerTask
    {
        public void run ()
        {
            checkTimeOut();
        }
    }
}
