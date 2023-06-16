package hero.gm;

import hero.gm.ParamChatContent;
import hero.gm.ParamNewQuestion;
import hero.gm.ParamQuestionAppraise;
import hero.gm.ParamQuestionEach;
import hero.gm.ParamRoleLvlUpdate;
import hero.gm.ParamRoleOnline;
import hero.gm.ParamRoleOutline;
//import cn.digifun.gamemanager.core.ChatChanle;
//import cn.digifun.gamemanager.core.Role;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseToGmTool.java
 * @创建者 ChenYaMeng
 * @版本 1.0
 * @时间 2010-7-6 上午10:38:40
 * @描述 ：响应GM工具的封装
 */

public class ResponseToGmTool
{
    /**
     * 响应类型，枚举值
     */
    public EResponseType         responseType;

    /**
     * 会话ID，在通用响应中使用
     */
    public int                   sessionID;

    /**
     * 主动推送_聊天响应
     */
    public ParamChatContent      chatContent;

    /**
     * 主动推送_角色上线
     */
    public ParamRoleOnline       roleOnline;

    /**
     * 主动推送_角色下线
     */
    public ParamRoleOutline      roleOutline;

    /**
     * 主动推送_角色升级
     */
    public ParamRoleLvlUpdate    roleLvlUpdate;

    /**
     * 主动推送_GM发起问题
     */
    public ParamNewQuestion      newQuestion;

    /**
     * 主动推送_GM交互信息
     */
    public ParamQuestionEach     questionEach;

    /**
     * 主动推送_评价GM
     */
    public ParamQuestionAppraise questionAppraise;

    /**
     * 构造
     * 
     * @param _responseType
     * @param _sessionID
     * @param _response
     */
    public ResponseToGmTool(EResponseType _responseType, int _sessionID)
    {
        responseType = _responseType;
        sessionID = _sessionID;
    }

    /**
     * 设置聊天内容
     * 
     * @param _chanle
     * @param _content
     */
//    public void setChatContent (ChatChanle _chanle, String _content)
//    {
//        chatContent = new ParamChatContent(_chanle, _content);
//    }

    /**
     * 设置玩家上线
     * 
     * @param _rol
     */
//    public void setRoleOnline (Role _rol)
//    {
//        roleOnline = new ParamRoleOnline(_rol);
//    }

    /**
     * 设置玩家下线
     * 
     * @param _nickname
     */
    public void setRoleOutline (String _nickname)
    {
        roleOutline = new ParamRoleOutline(_nickname);
    }

    /**
     * 设置玩家升级
     * 
     * @param _nickname
     * @param _lvl
     * @param _occupetions
     */
    public void setRoleLvlUpdate (String _nickname, int _lvl, String _occupetion)
    {
        roleLvlUpdate = new ParamRoleLvlUpdate(_nickname, _lvl, _occupetion);
    }

    /**
     * 设置GM发起问题
     * 
     * @param _nickname
     * @param _type
     * @param _infos
     */
    public void setNewQuestion (String _nickname, byte _type, String _info)
    {
        newQuestion = new ParamNewQuestion(_nickname, _type, _info);
    }

    /**
     * 设置GM问题交互
     * 
     * @param _sid
     * @param _id
     * @param _content
     */
    public void setQuestionEach (int _sid, int _id, String _content)
    {
        questionEach = new ParamQuestionEach(_sid, _id, _content);
    }

    /**
     * 设置GM问题评价
     * 
     * @param _id
     * @param _appraise
     */
    public void setQuestionAppraise (int _id, byte _appraise)
    {
        questionAppraise = new ParamQuestionAppraise(_id, _appraise);
    }
}
