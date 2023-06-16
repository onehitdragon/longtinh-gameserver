package hero.duel;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Duel.java
 * @创建者 Lulin
 * @版本 1.0
 * @时间 2008-12-30 上午11:41:36
 * @描述 ：同阵营决斗类
 */

public class Duel
{
    /**
     * 决斗双方userID
     */
    public int     player1UserID, player2UserID;

    /**
     * 是否在等待玩家确认开始
     */
    public boolean isConfirming;

    /**
     * 决斗开始时间
     */
    public long    startTime;

    /**
     * 决斗的地图编号
     */
    public int     duleMapID;

    /**
     * 构造
     * 
     * @param _player1UserID
     * @param _player2UserID
     */
    public Duel(int _player1UserID, int _player2UserID, int _mapID)
    {
        player1UserID = _player1UserID;
        player2UserID = _player2UserID;
        duleMapID = _mapID;
        isConfirming = true;
        startTime = System.currentTimeMillis();
    }
}
