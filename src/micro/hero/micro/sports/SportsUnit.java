package hero.micro.sports;

import hero.map.Map;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SportsUnit.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-12-21 下午04:57:48
 * @描述 ：竞技单元
 */

public class SportsUnit
{
    /**
     * 队伍1
     */
    public SportsTeam team1;

    /**
     * 队伍2
     */
    public SportsTeam team2;

    /**
     * 比赛场地
     */
    public Map        site;

    /**
     * 装备（准备、竞技中3种状态）
     */
    public byte       status;

    /**
     * 战斗持续时间
     */
    public long       fightKeepTime;

    /**
     * 准备时间
     */
    public long       readyKeepTime;

    /**
     * 构造
     * 
     * @param _team1 队伍1
     * @param _team2 队伍2
     * @param _site 比赛场地
     */
    public SportsUnit(SportsTeam _team1, SportsTeam _team2, Map _site)
    {
        team1 = _team1;
        team2 = _team2;
        site = _site;
        status = SportsService.STATUS_OF_READY;
    }

    /**
     * 开始战斗
     */
    public void start ()
    {
        status = SportsService.STATUS_OF_FIGHTING;
    }
}
