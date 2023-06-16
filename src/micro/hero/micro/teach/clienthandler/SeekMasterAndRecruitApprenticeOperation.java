package hero.micro.teach.clienthandler;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;
import hero.micro.teach.TeachService;
import hero.micro.teach.message.MAOperateConfirm;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SeekMasterAndRecruitApprenticeOperation.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-26 下午03:47:58
 * @描述 ：拜师、纳徒操作（拜师、收弟子、确认拜师请求、确认收徒邀请）
 */

public class SeekMasterAndRecruitApprenticeOperation extends AbsClientProcess
{
    @Override
    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        byte requestOperateType = yis.readByte();

        switch (requestOperateType)
        {
            case TYPE_OF_R_A:
            {
                int apprenticeObjectID = yis.readInt();

                HeroPlayer apprentice = player.where().getPlayer(
                        apprenticeObjectID);

                if (TeachService.authenRecruitAppr(player, apprentice))
                {
                    ResponseMessageQueue.getInstance().put(
                            apprentice.getMsgQueueIndex(),
                            new MAOperateConfirm(TYPE_OF_R_A, player
                                    .getUserID(), player.getName()));

                    TeachService.waitingReply(apprentice);
                }

                break;
            }
            case TYPE_OF_F_M:
            {
                int masterObjectID = yis.readInt();

                HeroPlayer master = player.where().getPlayer(masterObjectID);

                if (TeachService.authenFollowMaster(player, master))
                {
                    ResponseMessageQueue.getInstance().put(
                            master.getMsgQueueIndex(),
                            new MAOperateConfirm(TYPE_OF_F_M, player
                                    .getUserID(), player.getName()));

                    TeachService.waitingReply(master);
                }

                break;
            }
            case TYPE_OF_ACCEPT_R_A:
            {
                int masterUserID = yis.readInt();

                HeroPlayer master = PlayerServiceImpl.getInstance()
                        .getPlayerByUserID(masterUserID);

                TeachService.recruitApprentice(master, player);

                TeachService.cancelWaitingTimer(player);

                break;
            }
            case TYPE_OF_REFUSE_R_A:
            {
                int masterUserID = yis.readInt();

                HeroPlayer master = PlayerServiceImpl.getInstance()
                        .getPlayerByUserID(masterUserID);

                if (null != master)
                {
                    ResponseMessageQueue.getInstance().put(
                            master.getMsgQueueIndex(),
                            new Warning(player.getName()
                                    + Tip.TIP_MICRO_OF_REFUSE_BE_APPR));
                }

                TeachService.cancelWaitingTimer(player);

                break;
            }
            case TYPE_OF_ACCEPT_F_M:
            {
                int apprenticeUserID = yis.readInt();

                HeroPlayer apprentice = PlayerServiceImpl.getInstance()
                        .getPlayerByUserID(apprenticeUserID);

                TeachService.followMaster(apprentice, player);

                TeachService.cancelWaitingTimer(player);

                break;
            }
            case TYPE_OF_REFUSE_F_M:
            {
                int apprenticeUserID = yis.readInt();

                HeroPlayer apprentice = PlayerServiceImpl.getInstance()
                        .getPlayerByUserID(apprenticeUserID);

                if (null != apprentice)
                {
                    ResponseMessageQueue.getInstance().put(
                            apprentice.getMsgQueueIndex(),
                            new Warning(player.getName()
                                    + Tip.TIP_MICRO_OF_REFUSE_BE_MASTER));
                }

                TeachService.cancelWaitingTimer(player);

                break;
            }
        }

    }

    /**
     * 收纳徒弟
     */
    private static final byte   TYPE_OF_R_A             = 1;

    /**
     * 拜师
     */
    private static final byte   TYPE_OF_F_M             = 2;

    /**
     * 接受成为别人的徒弟
     */
    private static final byte   TYPE_OF_ACCEPT_R_A      = 3;

    /**
     * 接受成为别人的师傅
     */
    private static final byte   TYPE_OF_ACCEPT_F_M      = 4;

    /**
     * 拒绝成为别人的徒弟
     */
    private static final byte   TYPE_OF_REFUSE_R_A      = 5;

    /**
     * 拒绝成为别人的师傅
     */
    private static final byte   TYPE_OF_REFUSE_F_M      = 6;




}
