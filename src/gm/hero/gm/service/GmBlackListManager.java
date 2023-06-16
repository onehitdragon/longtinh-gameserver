package hero.gm.service;

//import cn.digifun.gamemanager.core.Black;
import hero.gm.BlackContainer;
import hero.gm.EBlackType;

public class GmBlackListManager
{
    /**
     * 唯一实例
     */
    private static GmBlackListManager instance           = null;

    /**
     * 账号黑名单容器，登录限制
     */
    private static BlackContainer     accountBlContainer = null;

    /**
     * 角色黑名单，登录限制
     */
    private static BlackContainer     roleBlContainer    = null;

    /**
     * 聊天黑名单，聊天限制
     */
    private static BlackContainer     chatBlContainer    = null;

    /**
     * 获取实例
     * 
     * @return
     */
    public static GmBlackListManager getInstance ()
    {
        if (null == instance)
        {
            instance = new GmBlackListManager();
        }
        return instance;
    }

    /**
     * 初始化，在GM服务开启时执行一次即可
     */
    public void init ()
    {
//        accountBlContainer = new BlackContainer(EBlackType.ACCOUNT_LOGIN);
//        roleBlContainer = new BlackContainer(EBlackType.ROLE_LOGIN);
//        chatBlContainer = new BlackContainer(EBlackType.ROLE_CHAT);
    }

    /**
     * 获取容器
     * 
     * @param _type
     * @return
     */
    public BlackContainer getContainer (EBlackType _type)
    {
        switch (_type)
        {
            case ACCOUNT_LOGIN:
                return accountBlContainer;
            case ROLE_LOGIN:
                return roleBlContainer;
            case ROLE_CHAT:
                return chatBlContainer;
            default:
                return null;
        }
    }

    /**
     * 添加
     * 
     * @param _type
     * @param _black
     */
//    public void addBlack (EBlackType _type, Black _black)
//    {
//        BlackContainer bc = getContainer(_type);
//        if (null != bc)
//        {
//
//            bc.add(_black);
//        }
//    }

    /**
     * 删除
     * 
     * @param _type
     * @param _name
     */
    public void removeBlack (EBlackType _type, String _name)
    {
        BlackContainer bc = getContainer(_type);
        if (null != bc)
        {
//            bc.remove(_name);
        }
    }    

    /**
     * 获取黑名单 终止时间
     * 
     * @param _type
     * @param _name
     * @return
     */
    public String getBlackEndTime (EBlackType _type, String _name)
    {
        BlackContainer bc = getContainer(_type);
        if (null != bc)
        {
//            Black b = bc.isExist(_name);
//            if (b != null)
//            {
//                return b.endTime;
//            }
        }

        return "";
    }
}
