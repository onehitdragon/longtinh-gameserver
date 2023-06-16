package hero.micro.teach.clienthandler;

import hero.micro.service.MicroServiceImpl;
import hero.micro.teach.TeachService;
import hero.micro.teach.message.ResponseMAList;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.LogWriter;
import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 OperateM_AList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-10 下午02:22:51
 * @描述 ：操作师徒列表（离开师傅、踢掉徒弟、传授知识）
 */

public class OperateMAList extends AbsClientProcess
{
    private static Logger log = Logger.getLogger(OperateMAList.class);
    /**
     * 查看列表
     */
    private static final byte OPERATION_OF_VIEW_LIST         = 1;

    /**
     * 离开师傅
     */
    private static final byte OPERATION_OF_LEFT_MASTER       = 2;

    /**
     * 踢掉徒弟
     */
    private static final byte OPERATION_OF_REDUCE_APPRENTICE = 3;

    /**
     * 传授知识
     */
    private static final byte OPERATION_OF_TEACH_KNOWLEDGE   = 4;

    /**
     * 解散所有徒弟
     */
    private static final byte OPERATION_OF_DISSMISS = 5;

    @Override
    public void read () throws Exception
    {

        // TODO Auto-generated method stub
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        try
        {
            byte operation = yis.readByte();

            switch (operation)
            {
                case OPERATION_OF_VIEW_LIST:
                {
                    ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),
                            new ResponseMAList(MicroServiceImpl.getInstance()
                                    .getMasterApprentice(player.getUserID()),player.getUserID()));

                    break;
                }
                case OPERATION_OF_LEFT_MASTER:
                {
                    if(TeachService.leftMaster(player)){
                        ResponseMessageQueue.getInstance().put(
                                player.getMsgQueueIndex(),
                                new ResponseMAList(MicroServiceImpl.getInstance()
                                        .getMasterApprentice(player.getUserID()),player.getUserID()));
                    }else{
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("离开师傅发生失败"));
                    }
                    break;
                }
                case OPERATION_OF_REDUCE_APPRENTICE:
                {
                    int apprenticeUserID = yis.readInt();
                    log.debug("apprenticeUserID = " + apprenticeUserID);
                    boolean flag = TeachService.reduceApprentice(player,
                            apprenticeUserID);
                    // OutMsgQ.getInstance().put(player.getMsgQueueIndex(), new
                    // MasterAndStudentRemove(flag));
                    if(flag){
                        ResponseMessageQueue.getInstance().put(
                                player.getMsgQueueIndex(),
                                new ResponseMAList(MicroServiceImpl.getInstance()
                                        .getMasterApprentice(player.getUserID()),player.getUserID()));
                    }else {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning("踢掉徒弟发生失败"));
                    }
                    break;
                }
                case OPERATION_OF_TEACH_KNOWLEDGE:
                {

                    int apprenticeUserID = yis.readInt();

                    HeroPlayer apprenticeUser = PlayerServiceImpl.getInstance()
                            .getPlayerByUserID(apprenticeUserID);

                    TeachService.teachKnowledge(player, apprenticeUser);

                    break;
                }
                case OPERATION_OF_DISSMISS:
                {
                    log.debug("dissmiss apprentice ...");

                    TeachService.dismissAll(player.getUserID());
                    
                    ResponseMessageQueue.getInstance().put(
                            player.getMsgQueueIndex(),
                            new ResponseMAList(MicroServiceImpl.getInstance()
                                    .getMasterApprentice(player.getUserID()),player.getUserID()));
                    break;
                }
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            LogWriter.error(null, e);
        }
    }
}
