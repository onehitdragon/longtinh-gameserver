//package hero.gm.tools.clienthandler;
//
//import hero.gm.service.GmDAO;
//import hero.item.service.GoodsServiceImpl;
//import hero.map.Map;
//import hero.map.message.ResponseAnimalInfoList;
//import hero.map.message.ResponseBoxList;
//import hero.map.message.ResponseMapBottomData;
//import hero.map.message.ResponseMapElementList;
//import hero.map.message.ResponseMapGameObjectList;
//import hero.map.message.ResponseMapMiniImage;
//import hero.map.message.ResponseSceneElement;
//import hero.map.service.MapServiceImpl;
//import hero.map.service.MiniMapImageDict;
//import hero.player.HeroPlayer;
//import hero.player.service.PlayerServiceImpl;
//import hero.task.service.TaskServiceImpl;
//
//import java.io.IOException;
//
//import me2.core.queue.OutMsgQ;
//import cn.digifun.gamemanager.Broadcast;
//import cn.digifun.gamemanager.Handler;
//import cn.digifun.gamemanager.core.GMProtocolID;
//import cn.digifun.gamemanager.message.ChangeMapResponse;
//
///**
// * <p>
// * Copyright: DGFun CO., (c) 2010
// * </p>
// * 
// * @文件 ChangeAccountPwHandler.java
// * @创建者 ChenYaMeng
// * @版本 1.0
// * @时间 2010-05-27 下午13:56:56
// * @描述 ：处理上行报文ID(short):112(请求切换地图)
// */
//public class ChangeMapHandler extends Handler
//{
//    public ChangeMapHandler()
//    {
//        super(GMProtocolID.REQUEST_CHANGE_MAP);
//    }
//
//    @Override
//    protected Handler newInstance ()
//    {
//        return new ChangeMapHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int _sid = messageDataInputStream.readInt();
//        /* String gmName = */messageDataInputStream.readUTF();
//        String mapName = messageDataInputStream.readUTF();
//        String name = messageDataInputStream.readUTF();
//
//        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByName(
//                name);
//        Map targetMap = MapServiceImpl.getInstance()
//                .getNormalMapByName(mapName);
//
//        if (targetMap == null)
//        {
//
//            Broadcast.getInstance().send(this.message.getSessionID(),
//                    new ChangeMapResponse(_sid, "该地图不存在"));
//        }
//        else
//        {
//            if (player != null && player.isEnable())
//            {
//                Map currentMap = player.where();
//                player.setCellX(targetMap.getBornX());
//                player.setCellY(targetMap.getBornY());
//                OutMsgQ.getInstance()
//                        .put(
//                                player.getMsgQueueIndex(),
//                                new ResponseMapBottomData(player, targetMap,
//                                        currentMap));
//                OutMsgQ.getInstance().put(
//                        player.getMsgQueueIndex(),
//                        new ResponseSceneElement(
//                                player.getLoginInfo().clientType, targetMap));
//                OutMsgQ.getInstance().put(
//                        player.getMsgQueueIndex(),
//                        new ResponseMapGameObjectList(
//                                player.getLoginInfo().clientType, targetMap));
//                TaskServiceImpl.getInstance().notifyMapNpcTaskMark(player,
//                        targetMap);
//
//                OutMsgQ.getInstance().put(
//                        player.getMsgQueueIndex(),
//                        new ResponseMapMiniImage(
//                                player.getLoginInfo().clientType, targetMap
//                                        .getMiniImageID(), MiniMapImageDict
//                                        .getInstance().getImageBytes(
//                                                targetMap.getMiniImageID())));
//
//                if (targetMap.getAnimalList().size() > 0)
//                {
//                    OutMsgQ.getInstance().put(player.getMsgQueueIndex(),
//                            new ResponseAnimalInfoList(targetMap));
//                }
//
//                OutMsgQ.getInstance().put(
//                        player.getMsgQueueIndex(),
//                        new ResponseMapElementList(
//                                player.getLoginInfo().clientType, targetMap));
//
//                if (targetMap.getTaskGearList().size() > 0)
//                {
//                    TaskServiceImpl.getInstance().notifyMapGearOperateMark(
//                            player, targetMap);
//                }
//
//                if (targetMap.getGroundTaskGoodsList().size() > 0)
//                {
//                    TaskServiceImpl
//                            .getInstance()
//                            .notifyGroundTaskGoodsOperateMark(player, targetMap);
//                }
//
//                if (targetMap.getBoxList().size() > 0)
//                {
//                    OutMsgQ.getInstance().put(player.getMsgQueueIndex(),
//                            new ResponseBoxList(targetMap.getBoxList()));
//                }
//
//                GoodsServiceImpl.getInstance().sendLegacyBoxList(targetMap,
//                        player);
//
//                player.gotoMap(targetMap);
//
//                Broadcast.getInstance().send(this.message.getSessionID(),
//                        new ChangeMapResponse(_sid));
//            }
//            else
//            {
//                if (GmDAO.changePlayerMap(name, targetMap))
//                {
//                    Broadcast.getInstance().send(this.message.getSessionID(),
//                            new ChangeMapResponse(_sid));
//                }
//                else
//                {
//                    Broadcast.getInstance().send(this.message.getSessionID(),
//                            new ChangeMapResponse(_sid, "地图跳转失败"));
//                }
//            }
//        }
//    }
//}
