package hero.charge.clienthandler;

import hero.charge.MallGoods;
import hero.charge.message.ResponseMallGoodsList;
import hero.charge.message.ResponseRecharge;
import hero.charge.service.ChargeServiceImpl;
import hero.charge.service.MallGoodsDict;
import hero.item.detail.EGoodsType;
import hero.item.dictionary.GoodsContents;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.log.service.LogServiceImpl;
import hero.log.service.ServiceType;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;

import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 OperateMallGoods.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-1-24 下午09:22:50
 * @描述 ：
 */

public class OperateMallGoods extends AbsClientProcess
{
     private static Logger log = Logger.getLogger(OperateMallGoods.class);
    @Override
    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        if (player == null)
            return;

        byte operation = yis.readByte();

        if (OPERATION_OF_LIST == operation)
        {
        	short clientVersion = yis.readShort();

            Hashtable<Byte, ArrayList<MallGoods>> mall = 
            	MallGoodsDict.getInstance().getMallTable();

            if (null != mall)
            {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                        new ResponseMallGoodsList(mall, clientVersion));
            }
        }
        else if (OPERATION_OF_BUY == operation)
        {
            int goodsID = yis.readInt();
            byte number = yis.readByte();

            if(number<=0){
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_NUMBER_ERROR,Warning.UI_TOOLTIP_TIP));
                LogServiceImpl.getInstance().numberErrorLog(player.getLoginInfo().accountID,player.getLoginInfo().username,
                    player.getUserID(),player.getName(),number,"购买商城道具["+goodsID+"]输入的数量["+number+"]");
                return;
            }

            // 此处可加前置判断，如有无绑定手机号码等

            MallGoods goods = MallGoodsDict.getInstance().getMallGoods(goodsID);

            if (null != goods)
            {
                log.debug("goods price="+goods.price+",number="+number);
                log.debug("player point="+player.getChargeInfo().pointAmount);

                if (number * goods.price > player.getChargeInfo().pointAmount)
                {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_CHARGE_NOT_POINT));
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new ResponseRecharge());
                    return;
                }

                EGoodsType goodsType = null;
                byte equipmentNumber = 0, medicamentNumber = 0, materialNumber = 0, specialNumber = 0, taskToolNumber = 0;
                byte petNumber = 0, petGoodsNumber = 0, petEquipmentNumber = 0;

                for (int i = 0; i < goods.goodsList.length; i++)
                {
                    goodsType = GoodsContents.getGoodsType(goods.goodsList[i][0]);
                    log.debug("goods id="+goods.goodsList[i][0]+",goods type="+goodsType);
                    switch (goodsType)
                    {
                        case EQUIPMENT:
                        {
                            equipmentNumber++;

                            break;
                        }
                        case MATERIAL:
                        {
                            materialNumber++;

                            break;
                        }
                        case MEDICAMENT:
                        {
                            medicamentNumber++;

                            break;
                        }
                        case SPECIAL_GOODS:
                        {
                            specialNumber++;

                            break;
                        }
                        case TASK_TOOL:
                        {
                            taskToolNumber++;

                            break;
                        }
                        case PET_EQUIQ_GOODS:
                        {
                        	petEquipmentNumber++;
                        	break;
                        }
                        case PET:
                        {
                        	petNumber++;
                        	break;
                        }
                        case PET_GOODS:
                        {
                        	petGoodsNumber++;
                        	break;
                        }
                    }
                }

                if(petEquipmentNumber > player.getInventory().getPetEquipmentBag().getEmptyGridNumber()){
                	ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_CHARGE_PET_BAG_FULL));

                    return;
                }else if(petNumber > player.getInventory().getPetContainer().getEmptyGridNumber()){
                	ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_CHARGE_PET_LIST_FULL));

                    return;
                }else if(petGoodsNumber > player.getInventory().getPetGoodsBag().getEmptyGridNumber()){
                	ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_CHARGE_PET_ITEM_FULL));

                    return;
                }else if (specialNumber > player.getInventory().getSpecialGoodsBag()
                        .getEmptyGridNumber())
                {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_CHARGE_SPECIAL_FULL));

                    return;
                }
                else if (materialNumber > player.getInventory()
                        .getMaterialBag().getEmptyGridNumber())
                {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_CHARGE_MATERIAL_FULL));

                    return;
                }
                else if (medicamentNumber > player.getInventory()
                        .getMedicamentBag().getEmptyGridNumber())
                {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_CHARGE_MEDICAMENT_FULL));

                    return;
                }
                else if (equipmentNumber > player.getInventory()
                        .getEquipmentBag().getEmptyGridNumber())
                {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_CHARGE_EQUIPMENT_FULL));

                    return;
                }
                else if (taskToolNumber > player.getInventory()
                        .getTaskToolBag().getEmptyGridNumber())
                {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_CHARGE_TASK_TOOL_FULL));

                    return;
                }

                // 扣点成功后，添加物品
                int pointAmount = goods.price * number;
//                String tranID = ChargeServiceImpl.getInstance().getTransIDGen();//扣点
                log.debug("goods type = " + goodsType);
                boolean reducePoint = updatePointAmount(player,pointAmount,goodsID,goods.name,number);
//                boolean reducePoint = true;
                log.debug("reduce point = "+ reducePoint);
                if (null != goodsType && reducePoint)
//                if (null != goodsType)
                {
                    // 通知客户端绘制

                    for (int i = 0; i < goods.goodsList.length; i++)
                    {
                    	if(goodsType == EGoodsType.PET){
                    		PetServiceImpl.getInstance().addPet(player.getUserID(), goodsID);
                    	}else{
                            GoodsServiceImpl.getInstance().addGoods2Package(player,
                                    goods.goodsList[i][0],
                                    goods.goodsList[i][1] * number, CauseLog.MALL);
                    	}
                    }

                    // 点数改变日志记录
                    /*LogServiceImpl.getInstance().pointLog(tranID,
                            player.getLoginInfo().accountID,
                            player.getLoginInfo().username, player.getUserID(),
                            player.getName(), "add", pointAmount,
                            "购买商城道具：" + goods.name + " 数量：" + number,
                            player.getLoginInfo().publisher,
                            "goodsid,"+goods.id+";goodsname," + goods.name + ";num," + number);*/
                }

                log.info("购买成功....");
            }
        }
    }

    /**
     * 扣除点数
     * @param _player
     * @param _point 点数
     * @param mid 用户伪码
     * @param toolid 道具ID
     * @param toolName 道具名称
     * @param number 数量
     * @return
     */
    private boolean updatePointAmount (HeroPlayer _player,
            int _point,int toolid,String toolName,int number)
    {
        if(_point <=0 || number<=0){
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),new Warning(Tip.TIP_NUMBER_ERROR));

            LogServiceImpl.getInstance().numberErrorLog(_player.getLoginInfo().accountID,_player.getLoginInfo().username,
                    _player.getUserID(),_player.getName(),_point,"购买商城道具[id:"+toolid+"][name:"+toolName+"][number:"+number+"]的点数");

            return false;
        }
        return ChargeServiceImpl.getInstance().reducePoint(_player, _point, toolid,toolName,number, ServiceType.BUY_TOOLS);
    }

    /**
     * 查看列表
     */
    public static final byte OPERATION_OF_LIST = 1;

    /**
     * 购买
     */
    public static final byte OPERATION_OF_BUY  = 2;

    /**
     * 类型-装备
     */
    public static final byte TYPE_EQUIPMENT    = 1;

    /**
     * 类型-药水
     */
    public static final byte TYPE_MEDICAMENT   = 2;

    /**
     * 类型-神器
     */
    public static final byte TYPE_MATERIAL     = 3;

    /**
     * 类型-技能书
     */
    public static final byte TYPE_SKILL_BOOK   = 4;

    /**
     * 类型-宠物
     */
    public static final byte TYPE_PET          = 5;
    
    /**
     * 类型-礼包
     */
    public static final byte TYPE_BAG          = 6;

    /**
     * 类型-热卖
     */
    public static final byte TYPE_HOT          = 7;
    
    /**
     * 类型-宠物装备
     */
    public static final byte TYPE_PET_EQUIP = 8;
    /**
     * 类型-宠物物品
     */
    public static final byte TYPE_PET_GOODS = 9;
}
