package hero.chat.clienthandler;

import hero.chat.service.ChatServiceImpl;
import hero.item.dictionary.GoodsContents;
import hero.item.service.GoodsServiceImpl;
import hero.item.special.RinseSkill;
import hero.item.special.SpecialGoodsService;
import hero.item.special.WorldHorn;
import hero.log.service.CauseLog;
import hero.log.service.LogServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerDAO;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.Tip;
import hero.social.service.SocialServiceImpl;

import java.io.IOException;

import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;


/**
 * 聊天上行报文处理
 * 
 * @author Luke 陈路
 * @date Jul 28, 2009
 */
public class ChatClientHandler extends AbsClientProcess
{
	private static Logger log = Logger.getLogger(ChatClientHandler.class);
    protected int getPriority ()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public void read () throws Exception
    {
        try
        {
            HeroPlayer speaker = PlayerServiceImpl.getInstance()
                    .getPlayerBySessionID(contextData.sessionID);
            byte type = yis.readByte();
            log.debug("chat type = " + type);

            boolean isBlack = PlayerServiceImpl.getInstance().playerChatIsBlank(speaker.getLoginInfo().accountID,speaker.getUserID());
            if(isBlack){
                ResponseMessageQueue.getInstance().put(speaker.getMsgQueueIndex(), 
                		new Warning(Tip.TIP_CHAT_OF_SHUT_UP, Warning.UI_STRING_TIP));
                return;
            }
            
            String content;

            if (type == ChatServiceImpl.PLAYER_SINGLE)
            {
                String name = yis.readUTF();
                content = yis.readUTF();

                HeroPlayer target = PlayerServiceImpl.getInstance()
                        .getPlayerByName(name);        
                if (target != null && target.isEnable())
                {
                    if (!SocialServiceImpl.getInstance().beBlack(
                            speaker.getUserID(), target.getUserID()))
                    {
                        content = ChatServiceImpl.parseGoodsInContent(speaker,
                                content);
                        //发自己
                        ChatServiceImpl.getInstance().sendSinglePlayer(
                                speaker, target.getName(), speaker,
                                content, false);
                        //发对方
                        ChatServiceImpl.getInstance().sendSinglePlayer(
                                speaker, target.getName(), target,
                                content, false);
                        ChatServiceImpl.getInstance().toGMaddChatContent(speaker.getName(),target.getName(),content);
                        // 聊天日志
                        LogServiceImpl.getInstance().talkLog(
                                speaker.getLoginInfo().accountID,
                                speaker.getUserID(), speaker.getName(),
                                speaker.getLoginInfo().loginMsisdn,
                                target.getUserID(), target.getName(), "私聊",
                                speaker.where().getName(), content);
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(speaker.getMsgQueueIndex(),
                                new Warning(Tip.TIP_CHAT_OF_TARGET_OFFLINE, Warning.UI_STRING_TIP));
                    }
                }
                else
                {
                    ResponseMessageQueue.getInstance().put(speaker.getMsgQueueIndex(),
                            new Warning(Tip.TIP_CHAT_OF_TARGET_OFFLINE, Warning.UI_STRING_TIP));
                }
            }
            /*
            else if(type == ChatServiceImpl.GM_REPLY)
            {
                
            }  
            */          
            else
            {
                content = yis.readUTF();
                log.debug("chat content == " + content);
                content = ChatServiceImpl.parseGoodsInContent(speaker, content);
                log.debug("parseGoodsInContent = " + content);
                if (type == ChatServiceImpl.PLAYER_WORLD)
                {
                	//用号角发言用 下行报文 0xd24 处理,号角没有时间限制.
                	if(System.currentTimeMillis() - speaker.chatWorldTime >= WORLD_CHANNEL_WAIT) {
                        ChatServiceImpl.getInstance().sendWorldPlayer(
                                speaker, content);
                        speaker.chatWorldTime = System.currentTimeMillis();
                        /**未完善功能记录
                         * add by zhengl
                         * 建议更新功能别写在这.因为将来玩家聊天会很频繁.
                         */
                        PlayerDAO.updateClanChatWait(speaker.getUserID(), speaker.chatWorldTime);
                        // 聊天日志
                        LogServiceImpl.getInstance().talkLog(
                                speaker.getLoginInfo().accountID,
                                speaker.getUserID(), speaker.getName(),
                                speaker.getLoginInfo().loginMsisdn, 0, "", "世界",
                                speaker.where().getName(), content);
                	} else {
            			int forgetGoods = speaker.getInventory().getSpecialGoodsBag().getGoodsNumber(
            					WorldHorn.WORLD_HORN_ID);
            			WorldHorn horn = (WorldHorn) GoodsContents.getGoods(WorldHorn.WORLD_HORN_ID);
            			if (forgetGoods <= 0) {
                        	ResponseMessageQueue.getInstance().put(speaker.getMsgQueueIndex(), 
                        			new Warning(Tip.TIP_CHAT_OF_WORLD_CHANNEL_WAIT, 
                        					Warning.UI_STRING_TIP));
            				return;
            			}
                        PlayerDAO.updateClanChatWait(speaker.getUserID(), speaker.chatWorldTime);
                        ChatServiceImpl.getInstance().sendWorldPlayer(
                                speaker, content);
                        PlayerDAO.updateClanChatWait(speaker.getUserID(), speaker.chatWorldTime);
                        // 聊天日志
                        LogServiceImpl.getInstance().talkLog(
                                speaker.getLoginInfo().accountID,
                                speaker.getUserID(), speaker.getName(),
                                speaker.getLoginInfo().loginMsisdn, 0, "", "世界",
                                speaker.where().getName(), content);
            			GoodsServiceImpl.getInstance().deleteSingleGoods(
            					speaker,
            					speaker.getInventory().getSpecialGoodsBag(),
                                horn, 1,
                                CauseLog.CLANCHAT);
                    	ResponseMessageQueue.getInstance().put(speaker.getMsgQueueIndex(), 
                    			new Warning(Tip.TIP_CHAT_OF_USE_END.replaceAll("%fn", horn.getName()), 
                    					Warning.UI_STRING_TIP));
					}
                }
                else if (type == ChatServiceImpl.PLAYER_MAP)
                {
                    ChatServiceImpl.getInstance().sendMapPlayer(speaker,
                            content);
                    // 聊天日志
                    LogServiceImpl.getInstance().talkLog(
                            speaker.getLoginInfo().accountID,
                            speaker.getUserID(), speaker.getName(),
                            speaker.getLoginInfo().loginMsisdn, 0, "", "地图",
                            speaker.where().getName(), content);
                }
                else if (type == ChatServiceImpl.PLAYER_GROUP)
                {
                    ChatServiceImpl.getInstance().sendGroupPlayer(speaker,
                            content);
                    // 聊天日志
                    LogServiceImpl.getInstance().talkLog(
                            speaker.getLoginInfo().accountID,
                            speaker.getUserID(), speaker.getName(),
                            speaker.getLoginInfo().loginMsisdn, 0, "", "队伍",
                            speaker.where().getName(), content);
                }
                else if (type == ChatServiceImpl.PLAYER_GUILD)
                {
                    ChatServiceImpl.getInstance().sendGuildContent(speaker,
                            content);
                    // 聊天日志
                    LogServiceImpl.getInstance().talkLog(
                            speaker.getLoginInfo().accountID,
                            speaker.getUserID(), speaker.getName(),
                            speaker.getLoginInfo().loginMsisdn, 0, "", "公会",
                            speaker.where().getName(), content);
                }
                else if (type == ChatServiceImpl.CLAN)
                {
                	if(System.currentTimeMillis() - speaker.chatClanTime >= CLAN_CHANNEL_WAIT) {
                        ChatServiceImpl.getInstance().sendClan(speaker,
                                speaker.getClan().getID(), content);
                        speaker.chatClanTime = System.currentTimeMillis();
                        /**未完善功能记录
                         * add by zhengl
                         * 建议更新功能别写在这.因为将来玩家聊天会很频繁.
                         */
                        PlayerDAO.updateClanChatWait(speaker.getUserID(), speaker.chatClanTime);
                        // 聊天日志
                        LogServiceImpl.getInstance().talkLog(
                                speaker.getLoginInfo().accountID,
                                speaker.getUserID(), speaker.getName(),
                                speaker.getLoginInfo().loginMsisdn, 0, "", "氏族",
                                speaker.where().getName(), content);
                	} else {
            			int forgetGoods = speaker.getInventory().getSpecialGoodsBag().getGoodsNumber(
            					WorldHorn.WORLD_HORN_ID);
            			WorldHorn horn = (WorldHorn) GoodsContents.getGoods(WorldHorn.WORLD_HORN_ID);
            			//edit by zhengl; date: 2011-05-03; note: 种族聊天不再使用喇叭
            			if (true || forgetGoods <= 0) {
                        	ResponseMessageQueue.getInstance().put(speaker.getMsgQueueIndex(), 
                        			new Warning(Tip.TIP_CHAT_OF_CLAN_CHANNEL_WAIT, 
                        					Warning.UI_STRING_TIP));
            				return;
            			}
                        PlayerDAO.updateClanChatWait(speaker.getUserID(), speaker.chatClanTime);
                        ChatServiceImpl.getInstance().sendClan(speaker,
                                speaker.getClan().getID(), content);
                        // 聊天日志
                        LogServiceImpl.getInstance().talkLog(
                                speaker.getLoginInfo().accountID,
                                speaker.getUserID(), speaker.getName(),
                                speaker.getLoginInfo().loginMsisdn, 0, "", "氏族",
                                speaker.where().getName(), content);
            			GoodsServiceImpl.getInstance().deleteSingleGoods(
            					speaker,
            					speaker.getInventory().getSpecialGoodsBag(),
                                horn, 1,
                                CauseLog.CLANCHAT);
            			ResponseMessageQueue.getInstance().put(speaker.getMsgQueueIndex(), 
            					new Warning(Tip.TIP_CHAT_OF_USE_END.replaceAll("%fn", horn.getName()), 
            							Warning.UI_STRING_TIP));
					}

                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private final static boolean IS_NOT_USE                = false;
    
    private final static int    WORLD_CHANNEL_WAIT     = 3*60*1000;
    
    private final static int    CLAN_CHANNEL_WAIT      = 30*1000;
}
