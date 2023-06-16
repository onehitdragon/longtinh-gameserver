package hero.gm;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 RoleLvlUpdate.java
 * @创建者 ChenYaMeng
 * @版本 1.0
 * @时间 2010-7-6 下午01:55:10
 * @描述 ：参数封装类 - 主动推送服务器玩家等级变化
 */

public class ParamRoleLvlUpdate
{
    public String nickname;

    public int    lvl;

    public String occupetion;

    public ParamRoleLvlUpdate(String _nickname, int _lvl, String _occupetion)
    {
        nickname = _nickname;
        lvl = _lvl;
        occupetion = _occupetion;
    }
}
