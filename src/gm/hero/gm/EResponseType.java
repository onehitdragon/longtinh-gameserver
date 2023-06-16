package hero.gm;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EResponseType.java
 * @创建者 ChenYaMeng
 * @版本 1.0
 * @时间 2010-7-6 上午11:48:23
 * @描述 ：主动推送给GM工具的消息，放到线程中
 */

public enum EResponseType
{
    SEND_CHAT_CONTENT, // (主动推送) 聊天信息
    SEND_ROLE_ONLINE, // (主动推送) 服务器玩家进入游戏
    SEND_ROLE_OUTLINE, // (主动推送) 服务器玩家下线
    SEND_ROLE_LVL_UPDATE, // (主动推送) 服务器玩家等级变化
    SEND_NEW_QUEATION, // (主动推送) 玩家提出一个新问题
    SEND_QUEATION_EACH, // (主动推送) 玩家和GM对问题进行交互
    SEND_QUESTION_APPRAISE; // (主动推送) 玩家对GM回答进行评价
}
