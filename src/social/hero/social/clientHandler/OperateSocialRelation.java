package hero.social.clientHandler;

import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.LogWriter;
import hero.share.service.Tip;
import hero.social.ESocialRelationType;
import hero.social.message.ResponseSocialRelationdList;
import hero.social.message.SelectOtherPlayer;
import hero.social.service.SocialServiceImpl;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 OperateSocialRelation.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-9 下午18:11:08
 * @描述 ：操作社交关系（包括好友、仇人、屏蔽三种关系，师徒、婚姻关系在其自身模块里处理）
 */

public class OperateSocialRelation extends AbsClientProcess
{
     private static Logger log = Logger.getLogger(OperateSocialRelation.class);
    /**
     * 查看列表
     */
    private static final byte OPERATION_OF_VIEW_LIST = 1;

    /**
     * 添加操作
     */
    private static final byte OPERATION_OF_ADD       = 2;

    /**
     * 移除操作
     */
    private static final byte OPERATION_OF_REMOVE    = 3;
    
    /**
     * 按条件查询
     */
    private static final byte SELECT_OF_OTHER_PLAYER                = 4;
    
    /**
     * 随机查询
     */
    private static final byte SELECT_OF_RANDOM    = 5;
    
    /**
     * 按名字查询
     */
    private static final byte SELECT_OF_NAME    = 6;

    /*
     * (non-Javadoc)
     * 
     * @see me2.core.handler.ClientHandler#read()
     */
    public void read () throws Exception
    {

        // TODO Auto-generated method stub
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        try
        {
            byte operation = yis.readByte();
            byte socialType = yis.readByte();

            switch (operation)
            {
                case OPERATION_OF_ADD:
                {
                    String name = yis.readUTF();

                    if(player.getName().equals(name)){
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new Warning(Tip.TIP_NOT_MYSELF));
                        return;
                    }

                    HeroPlayer target = PlayerServiceImpl.getInstance()
                            .getPlayerByName(name);

                    if (target == null || !target.isEnable())
                    {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),
                                new Warning(Tip.TIP_SOCIAL_0F_NULL_TARGET));
                    }
                    else
                    {
                        SocialServiceImpl.getInstance().add(player, target,
                                socialType);
                    }

                    break;
                }
                case OPERATION_OF_REMOVE:
                {
                    String name = yis.readUTF();

                    SocialServiceImpl.getInstance().remove(player, name,
                            socialType, true);

                    break;
                }
                case OPERATION_OF_VIEW_LIST:
                {
                    log.debug("进入社交列表 == "+OPERATION_OF_VIEW_LIST+",type="+socialType);
                    ResponseMessageQueue
                            .getInstance()
                            .put(
                                    player.getMsgQueueIndex(),
                                    new ResponseSocialRelationdList(
                                            socialType,
                                            SocialServiceImpl
                                                    .getInstance()
                                                    .getSocialRelationList(
                                                            player.getUserID(),
                                                            ESocialRelationType
                                                                    .getSocialRelationType(socialType))));

                    break;
                }
                case SELECT_OF_OTHER_PLAYER:
                {
                	byte sex = yis.readByte();
                	byte vocation = yis.readByte();
                	short level = yis.readShort();
                	//按条件查询
                	ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
                			new SelectOtherPlayer((byte)2, sex, vocation, level, player));
                }
                case SELECT_OF_RANDOM:
                {
                	//随机查询
                	ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
                			new SelectOtherPlayer((byte)1, player));
                }
                case SELECT_OF_NAME:
                {
                	String name = yis.readUTF();
                	//按名字查询
                	ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), 
                			new SelectOtherPlayer((byte)3, name, player));
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
