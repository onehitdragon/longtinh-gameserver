package hero.manufacture.clientHandler;

import hero.item.Equipment;
import hero.item.EquipmentInstance;
import hero.item.Goods;
import hero.item.bag.exception.BagException;
import hero.item.detail.EGoodsType;
import hero.item.dictionary.GoodsContents;
import hero.item.message.ResponseEquipmentBag;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.manufacture.Manufacture;
import hero.manufacture.Odd;
import hero.manufacture.dict.ManufSkill;
import hero.manufacture.dict.ManufSkillDict;
import hero.manufacture.message.ManufListMessage;
import hero.manufacture.message.ManufNeedGoodsMessage;
import hero.manufacture.message.UpgradeSkillPoint;
import hero.manufacture.service.GetTypeOfSkillItem;
import hero.manufacture.service.ManufactureServerImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.FullScreenTip;
import hero.share.message.Warning;
import hero.share.service.Tip;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;


/**
 * 0x2f01
 * 5项生活技能都在这处理
 */
public class ManufClientHandler extends AbsClientProcess
{

    /**
     * 列表
     */
    private static final byte LIST = 0;
    /**
     * 制造
     */
    private static final byte MANUF = 1;
    /**
     * 查看所需材料
     */
    private static final byte NEED_GOODS = 2;
    /**
     * 提纯装备
     */
    private static final byte PURIFY = 3;

    public void read () throws Exception
    {
        try
        {
            byte type = yis.readByte();
            HeroPlayer player = PlayerServiceImpl.getInstance()
                    .getPlayerBySessionID(contextData.sessionID);
            if (player == null) return;
//            Manufacture manuf = ManufactureServerImpl.getInstance()
//                    .getManufactureByUserID(player.getUserID());
            List<Manufacture> manufactureList = ManufactureServerImpl.getInstance().getManufactureListByUserID(player.getUserID());
            if (manufactureList == null) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("玩家没有学习过制造技能！"));
                return;
            }
            // 列表
            if (type == LIST)
            {
                ResponseMessageQueue.getInstance().put(
                        player.getMsgQueueIndex(),
                        new ManufListMessage(
                                ManufactureServerImpl.getInstance().getTitle(player.getLevel()), manufactureList));
            }
            else if (type == MANUF)
            {
                // 制造
                int _manufSkillID = yis.readInt();
                byte _type = yis.readByte(); //用来获取 manufacture
                Manufacture manuf = ManufactureServerImpl.getInstance().getManufactureByUserIDAndType(player.getUserID(),_type);
                if(_manufSkillID > 0){
                    ManufSkill _mSkill = ManufSkillDict.getInstance().getManufSkillByID(_manufSkillID);
                    if (manuf == null || _mSkill == null) {

                        return;
                    }
                    if (hasPackage(player, _mSkill))
                    {
                        if (canManuf(player, _mSkill))
                        {
                            // 计算生成的物品
                            int random = getRandom(_mSkill);
                            int goodsID = _mSkill.getGoodsID[random];
                            short num = _mSkill.getGoodsNum[random];

                            // 移除物品。失败、成功、意外这三种结果都将消耗掉制作时候所需材料
                            removeGoods(player, _mSkill);

                            if (random == 0)
                            {
                                ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new Warning(Tip.TIP_MANUF_FAIL));

                                return;
                            }
                            else if (random == 1)
                            {
                                ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new Warning(Tip.TIP_MANUF_SUCCESS));
                            }
                            else
                            {
                                ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new Warning(Tip.TIP_MANUF_EXPECTED));

                                if (_mSkill.abruptID > 0)
                                {
                                    ManufSkill abrupt = ManufSkillDict
                                            .getInstance().getManufSkillByID(
                                                    _mSkill.abruptID);
                                    if (abrupt != null)
                                    {
                                        random = new Random().nextInt(100);
                                        if (random < 10)
                                        {
                                            ManufactureServerImpl
                                                    .getInstance()
                                                    .addManufSkillItem(
                                                            player,
                                                            _mSkill,
                                                            GetTypeOfSkillItem.COMPREHEND);
                                        }
                                    }
                                }
                            }



                            // 添加物品
                            Goods goods = GoodsContents.getGoods(goodsID);
                            GoodsServiceImpl.getInstance().addGoods2Package(player,
                                    goods, num, CauseLog.MANUF);

                            // 添加技能点
                            boolean canAddPoint = _mSkill.canAddPoint(manuf.getPoint());
                            if (canAddPoint)
                            {
                                ManufactureServerImpl.getInstance().addPoint(
                                        player.getUserID(), manuf, _mSkill.getPoint);
                                ResponseMessageQueue.getInstance().put(
                                        player.getMsgQueueIndex(),
                                        new UpgradeSkillPoint(manuf.getPoint()));
                            }
                        }
                        else
                        {
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                    new Warning(Tip.TIP_MANUF_GOODS_NOT_ENOUGH));
                        }
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_MANUF_BOX_NOT_ENOUGH));
                    }
                }else{//提纯装备时，不用 _manufID,直接到装备界面

                     ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),
                            new ResponseEquipmentBag(player.getInventory().getEquipmentBag(),player));
                }
            }
            else if (type == NEED_GOODS)
            {
                // 查看制造需要材料
                int _manufID = yis.readInt();
                ManufSkill _mSkill = ManufSkillDict.getInstance()
                        .getManufSkillByID(_manufID);
                if (_mSkill == null) { return; }
                ManufNeedGoodsMessage msg = new ManufNeedGoodsMessage(_manufID,
                        _mSkill.desc, player);
                for (int i = 0; i < _mSkill.needGoodsID.length; i++)
                {
                    if (_mSkill.needGoodsID[i] > 0)
                        msg.addNeedGoods(_mSkill.needGoodsID[i],
                                _mSkill.needGoodsNum[i]);
                }
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);
            }else if(type == PURIFY){ //提纯装备
                int equipID = yis.readInt();

                EquipmentInstance ei = player.getInventory().getEquipmentBag().getEquipmentByInstanceID(equipID);
                if(null != ei){
                    /** TODO
                     *
                     * 1.根据公式获取提纯后的物品ID，提纯后的物品类型只可能是材料
                     * 2.检查玩家这个材料包是否还有空间存放
                     * 3.删除此装备
                     * 4.给玩家添加获得的物品
                     */
                    int[] mGoodsID = generatePurifyGoodsID(ei);
                    boolean hasPackage = hasMaterialPackage(player);
                    if(hasPackage){
                        removeEquipment(player,ei);//删除此装备
                        for(int goodsid : mGoodsID){
                             // 添加物品
                            Goods goods = GoodsContents.getGoods(goodsid);
                            GoodsServiceImpl.getInstance().addGoods2Package(player,
                                    goods, 1, CauseLog.MANUF);
                        }
                    }else{
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("材料包已满，不能提纯"));
                    }
                }else{
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("获取装备数据错误，无法提纯！"));
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 根据装备的类型(武器或防具)、等级、品质生成材料物品ID数组
     * todo 公式未定
     * @param ei
     * @return
     */
    private int[] generatePurifyGoodsID(EquipmentInstance ei){
//        ei.getArchetype().getNeedLevel()
        int[] goodses = {320550,320550};
        return goodses;
    }

    /**
     * 删除玩家背包里的装备
     * @param _player
     * @param ei
     */
    private void removeEquipment(HeroPlayer _player, EquipmentInstance ei){
        try{
            GoodsServiceImpl.getInstance().
                    removeEquipmentOfBag(_player,_player.getInventory().getEquipmentBag(),ei,CauseLog.REFINED);
        } catch (BagException e) {
            e.printStackTrace();
        }
    }

    private int getRandom (ManufSkill _mSkill)
    {
        Odd[] getGoodsOddList = _mSkill.getGetGoodsOddList();//长度为3，已排序的(升序)
        int _random = new Random().nextInt(100);
        if (_random <= getGoodsOddList[2].odd)
            return getGoodsOddList[2].index;
        else if (_random <= getGoodsOddList[2].odd+getGoodsOddList[1].odd)
            return getGoodsOddList[1].index;
        return getGoodsOddList[0].index;
    }

    /**
     * 移除背包中制造原材料
     * 
     * @param _player
     * @param _skill
     */
    private void removeGoods (HeroPlayer _player, ManufSkill _skill)
    {
        for (int i = 0; i < _skill.needGoodsID.length; i++)
        {
            if (_skill.needGoodsID[i] > 0)
            {
                try
                {
                    GoodsServiceImpl.getInstance().deleteSingleGoods(_player,
                            _player.getInventory().getMaterialBag(),
                            _skill.needGoodsID[i], _skill.needGoodsNum[i],
                            CauseLog.MANUF);
                }
                catch (BagException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 判断玩家是否有空间存放制造的物品
     * 
     * @param _player
     * @param _skill
     * @return
     */
    private static boolean hasPackage (HeroPlayer _player, ManufSkill _skill)
    {
        Goods goods = GoodsContents.getGoods(_skill.getGoodsID[1]);
        if (goods != null)
        {
            EGoodsType type = goods.getGoodsType();
            if (type == EGoodsType.EQUIPMENT)
            {
                if (_player.getInventory().getEquipmentBag()
                        .getEmptyGridNumber() < 1) return false;
            }
            else if (type == EGoodsType.MATERIAL)
            {
                if (_player.getInventory().getMaterialBag()
                        .getEmptyGridNumber() < 1) return false;
            }
            else if (type == EGoodsType.MEDICAMENT)
            {
                if (_player.getInventory().getMedicamentBag()
                        .getEmptyGridNumber() < 1) return false;
            }
            else if (type == EGoodsType.SPECIAL_GOODS)
            {
                if (_player.getInventory().getSpecialGoodsBag()
                        .getEmptyGridNumber() < 1) return false;
            }
            else if (type == EGoodsType.TASK_TOOL)
            {
                if (_player.getInventory().getTaskToolBag()
                        .getEmptyGridNumber() < 1) return false;
            }
        }
        return true;
    }

    /**
     * 检查玩家材料背包是否还有空间
     * @param _player
     * @return
     */
    private static boolean hasMaterialPackage(HeroPlayer _player){
        return _player.getInventory().getMaterialBag().getEmptyGridNumber()>=1;
    }

    /**
     * 判断是否有足够的制作原材料
     * 
     * @param _player
     * @param _skill
     * @return
     */
    private static boolean canManuf (HeroPlayer _player, ManufSkill _skill)
    {

        for (int i = 0; i < _skill.needGoodsID.length; i++)
        {
            if (_skill.needGoodsID[i] > 0)
            {
                int num = _player.getInventory().getMaterialBag()
                        .getGoodsNumber(_skill.needGoodsID[i]);
                if (num < _skill.needGoodsNum[i]) return false;
            }
        }
        return true;
    }
}
