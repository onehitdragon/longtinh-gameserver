package hero.charge;

import hero.player.HeroPlayer;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 RequestInfo.java
 * @创建者 ChenYaMeng
 * @版本 1.0
 * @时间 2010-7-6 上午09:28:02
 * @描述 ： 向计费服务器发起请求的信息
 */

public class RequestInfo
{
    /**
     * 账户名
     */
    public String     account;

    /**
     * 玩家对象
     */
    public HeroPlayer player;

    public RequestInfo(String _account, HeroPlayer _player)
    {
        account = _account;
        player = _player;
    }
}
