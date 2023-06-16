package hero.micro.sports;

import java.util.ArrayList;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SportsTeam.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-12-17 下午03:04:53
 * @描述 ：竞技队伍
 */

public class SportsTeam
{
    /**
     * 队列类型
     */
    public byte               queueType;

    /**
     * 等级区间
     */
    public short              levelZoon;

    /**
     * 队伍成员积分总和
     */
    public int                teamPointTotal;

    /**
     * 进行时间（）
     */
    public long               executeTime;

    /**
     * 队伍编号
     */
    public int                teamID;

    /**
     * 代表的竞技势力
     */
    public ESportsClan        sportsClan;

    /**
     * 成员编号、积分列表（|编号、积分|）
     */
    public ArrayList<Integer> memberInfoList;

    /**
     * 活着的成员数量
     */
    public byte               liveMemberNumber;

    /**
     * 构造
     * 
     * @param _teamID 队伍编号
     * @param _queueType 竞技队伍类型
     * @param _teamLeaderUserID 队长编号
     */
    public SportsTeam(int _teamID, byte _queueType, int _teamLeaderUserID)
    {
        teamID = _teamID;
        queueType = _queueType;
        memberInfoList = new ArrayList<Integer>();
    }

    /**
     * 添加成员
     * 
     * @param _memberUserID
     * @param _memberPoint
     */
    public void addMemberInfo (int _memberUserID, int _memberPoint)
    {
        memberInfoList.add(_memberUserID);
        memberInfoList.add(_memberPoint);
    }

    /**
     * 成员进入场地
     */
    public void enterSpace (int _memberUserID)
    {
        liveMemberNumber++;
    }

    /**
     * 成员离开竞技场地
     */
    public void memberDie (int _memberUserID)
    {
        liveMemberNumber--;
    }

    /**
     * 获取活着的成员数量
     * 
     * @return
     */
    public byte getLiveMemberNumber ()
    {
        return liveMemberNumber;
    }

    /**
     * 竞技队伍类型－－2V2
     */
    public static final byte TYPE_OF_TWO   = 2;

    /**
     * 竞技队伍类型－－3V3
     */
    public static final byte TYPE_OF_THREE = 3;

    /**
     * 竞技队伍类型－－5V5
     */
    public static final byte TYPE_OF_FIVE  = 5;
}
