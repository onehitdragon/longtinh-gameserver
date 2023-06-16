package hero.gm;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EBlackType.java
 * @创建者 ChenYaMeng
 * @版本 1.0
 * @时间 2010-6-13 14:56:20
 * @描述 ：黑名单类型枚举
 */
public enum EBlackType
{
    /**
     * 账户黑名单，限制登录
     */
    ACCOUNT_LOGIN,
    /**
     * 角色黑名单，限制登录
     */
    ROLE_LOGIN,
    /**
     * 聊天黑名单，限制发言
     */
    ROLE_CHAT;   
    
}
