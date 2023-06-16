package hero.map.clienthandler;

import hero.dungeon.Dungeon;
import hero.dungeon.service.DungeonServiceImpl;
import hero.effect.service.EffectServiceImpl;
import hero.item.Goods;
import hero.item.dictionary.GoodsContents;
import hero.item.special.TaskTransportItem;
import hero.map.EMapType;
import hero.map.Map;
import hero.map.WorldMap;
import hero.map.message.*;
import hero.map.service.MapConfig;
import hero.map.service.MapServiceImpl;
import hero.map.service.WorldMapDict;
import hero.npc.Npc;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;
import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-3-23
 * Time: 上午10:41
 * 世界地图功能
 * 0x2a20
 */
public class WorldMapFun extends AbsClientProcess{

    private static Logger log = Logger.getLogger(WorldMapFun.class);

    private static final byte ENTER = 0;                //进入地图功能界面
    private static final byte DESC = 1;                 //查看地图描述
    private static final byte GOTO_MAP_START = 2;       //进入选中的地图前的逻辑
    private static final byte FIND_NPC = 3;             //查找NPC
    private static final byte WORLD = 4;                //世界地图
    private static final byte SHOW_SINGLE_WORLD = 5;   //查看某个世界的地图界面
    private static final byte GOTO_MAP = 6;             //进入选中的地图
    private static final byte GOTO_NPC_START = 7;      //传送到NPC位置前的逻辑
    private static final byte GOTO_NPC = 8;             //传送到其它地图NPC的位置

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        if(player != null){
            if(player.isSelling()){
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new ResponseWorldMaps(null,null,(byte)0,Tip.TIP_EXCHANGEING_NOT_USE_FUN));
//                OutMsgQ.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_EXCHANGEING_NOT_USE_FUN));
//                OutMsgQ.getInstance().put(player.getMsgQueueIndex(),new SwitchMapFailNotify(Tip.TIP_EXCHANGEING_NOT_USE_FUN));
                return;
            }
        }

        byte type = yis.readByte();

        switch (type){
            case ENTER:
            {   //根据玩家当前所在地图ID查找所在的世界的地图列表
//                log.info("世界地图 玩家所在地图 id="+player.where().getID()+",name="+player.where().getName());
                byte worldType = MapServiceImpl.getInstance().getPlayerMapWorldType(player);  //当前所在地图所属的世界的类型 1:神龙界  2:魔龙界  3:仙界

                if(worldType == 0){//有的地图没有在表里配置，例如：在某一房子里的地图
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new ResponseWorldMaps(null,null,(byte)0,Tip.TIP_NO_WORLD_MAPS));
                    return;
                }

                List<WorldMap> mapList = WorldMapDict.getInstance().getWorldMapListByType(worldType);
//                log.debug("worldtype=" + worldType+",mapid="+player.where().getID()+",maplist size = " + mapList.size());

                String name = "";

                if(mapList != null){
                    name = MapServiceImpl.getInstance().getWorldNameByType(worldType);
                }
                log.debug(" name ="+name);
                WorldMap maxWorldMap = WorldMapDict.getInstance().getMaxWorldMapByName(name); //世界
                log.debug("max world map = " + maxWorldMap);

                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new ResponseWorldMaps(mapList,maxWorldMap,(byte)1,""));

                break;
            }
            case DESC:
            {
                short mapID = yis.readShort();
                String desc = WorldMapDict.getInstance().getMapDesc(mapID);
                //add by zhengl; date: 2011-05-17; note: 添加容错,防止传入NULL参数
                if (desc == null) {
                	desc = "";
				}
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new ResponseMapDesc(desc,mapID));
                break;
            }
            case GOTO_MAP_START:
            {
                int transPortNum = player.getInventory().getSpecialGoodsBag().getGoodsNumber(TaskTransportItem.TASK_TRANSPORT_ITEM_ID);
                log.debug("trans port num="+transPortNum);
                if(transPortNum <= 0){
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_TRANSPORT_MAP_NEED_GOODS,Warning.UI_TOOLTIP_AND_EVENT_TIP,Warning.SUBFUNCTION_UI_POPUP_COMM_CHARGE));
                    break;
                }else {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_TRANSPORT_MAP_USE_GOODS,Warning.UI_TOOLTIP_AND_EVENT_TIP,Warning.EVENT_SERVER_TRANSFER_MAP));
                    break;
                }

            }
            case GOTO_NPC_START:
            {
                int transPortNum = player.getInventory().getSpecialGoodsBag().getGoodsNumber(TaskTransportItem.TASK_TRANSPORT_ITEM_ID);
                log.debug("trans port num="+transPortNum);
                if(transPortNum <= 0){
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_TRANSPORT_NPC_NEED_GOODS,Warning.UI_TOOLTIP_AND_EVENT_TIP,Warning.SUBFUNCTION_UI_POPUP_COMM_CHARGE));
                    break;
                }else {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                            new Warning(Tip.TIP_TRANSPORT_NPC_USE_GOODS,Warning.UI_TOOLTIP_AND_EVENT_TIP,Warning.EVENT_SERVER_TRANSFER_NPC));
                    break;
                }
            }
            case GOTO_MAP:
            {


                Goods goods = GoodsContents.getGoods(TaskTransportItem.TASK_TRANSPORT_ITEM_ID);

                 if(((TaskTransportItem)goods).beUse(player,null,-1)){ //这里要调用一下，要记录使用日志

                     short mapID = yis.readShort();
                    Map entranceMap = MapServiceImpl.getInstance().getNormalMapByID(mapID);
                    if(entranceMap == null){
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_MAP_OF_NONE_EXISTS+mapID));
                        return;
                    }
                    player.setCellX(entranceMap.getBornX());
                    player.setCellY(entranceMap.getBornY());

                    ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),
                            new ResponseMapBottomData(player, entranceMap,
                                    player.where()));

                    ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),
                            new ResponseMapGameObjectList(player
                                    .getLoginInfo().clientType, entranceMap));

                    player.gotoMap(entranceMap);
                    //add by zhengl; date: 2011-03-24; note: 加载其他玩家和怪物的BUFF.
                    EffectServiceImpl.getInstance().sendEffectList(player, entranceMap);

                     if(((TaskTransportItem) goods).disappearImmediatelyAfterUse()){
                        ((TaskTransportItem)goods).remove(player, (short)-1);
                     }
                 }else {
                     ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_USE_TRANSPORT_GOODS_FAIL,Warning.UI_TOOLTIP_TIP));
                 }

                break;
            }
            case GOTO_NPC:
            {
                Goods goods = GoodsContents.getGoods(TaskTransportItem.TASK_TRANSPORT_ITEM_ID);

                 if(((TaskTransportItem)goods).beUse(player,null,-1)){ //这里要调用一下，要记录使用日志

                        short mapID = yis.readShort();
                        int npcID = yis.readInt();

                        Map entranceMap = MapServiceImpl.getInstance().getNormalMapByID(mapID);
                        if(entranceMap == null){
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_MAP_OF_NONE_EXISTS+mapID));
                            return;
                        }

                        Npc npc = entranceMap.getNpc(npcID);
                        if(npc == null){
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_NOT_NPC+npcID));
                            return;
                        }

                        player.setCellX(npc.getCellX());
                        player.setCellY(npc.getCellY());

                        ResponseMessageQueue.getInstance().put(
                                player.getMsgQueueIndex(),
                                new ResponseMapBottomData(player, entranceMap,
                                        player.where()));

                        ResponseMessageQueue.getInstance().put(
                                player.getMsgQueueIndex(),
                                new ResponseMapGameObjectList(player
                                        .getLoginInfo().clientType, entranceMap));

                        player.gotoMap(entranceMap);
                        //add by zhengl; date: 2011-03-24; note: 加载其他玩家和怪物的BUFF.
                        EffectServiceImpl.getInstance().sendEffectList(player, entranceMap);

                         if(((TaskTransportItem) goods).disappearImmediatelyAfterUse()){
                          ((TaskTransportItem)goods).remove(player, (short)-1);
                         }
                 }else {
                     ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_USE_TRANSPORT_GOODS_FAIL,Warning.UI_TOOLTIP_TIP));
                 }

                break;
            }
            case FIND_NPC:
            {
                short mapID = yis.readShort();
                Map map = MapServiceImpl.getInstance().getNormalMapByID(mapID);
                if(map != null){
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new ResponseMapNpcList(map.getNpcList(),mapID)) ;
                }else {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_MAP_OF_NONE_EXISTS+mapID));
                }
                break;
            }
            case WORLD:
            {
                byte worldType = MapServiceImpl.getInstance().getPlayerMapWorldType(player);
                log.debug("进入世界地图.. world type="+worldType);
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new ResponseWorld(worldType));
                break;
            }
            case SHOW_SINGLE_WORLD:
            {
                byte showType = yis.readByte();//1:神龙界  2:魔龙界  3:仙界 (前两个与阵营值一样)
                log.debug("show single world type="+showType);
                String name = MapServiceImpl.getInstance().getWorldNameByType(showType);
                log.debug("show single world name = " + name);

                WorldMap maxWorldMap = WorldMapDict.getInstance().getMaxWorldMapByName(name); //世界
                log.debug("max world map = " + maxWorldMap.name);

                List<WorldMap> mapList = WorldMapDict.getInstance().getWorldMapListByType(showType);
                log.debug("show single world maps = " + mapList);
                /*for(WorldMap map : mapList){
                    log.debug("show single world map name="+ map.name+",type="+map.type+",id="+map.mapID);
                }*/

                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new ResponseWorldMaps(mapList,maxWorldMap,(byte)1,""));
                break;
            }

        }

    }
}
