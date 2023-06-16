package hero.share;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ClientObjectMoveSpeed.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-10-24 下午05:51:24
 * @描述 ：走动速度
 */

public class MoveSpeed
{
    /**
     * 最慢速度
     */
    public final static byte SLOWEST                   = 1;

    /**
     * 较慢速度 (现在规定,疲劳诅咒,寒冰箭,等等影响玩家行动的魔法使用该速度)
     */
    public final static byte SLOWER                    = 2;

    /**
     * 玩家、怪物正常行走速度
     */
    public final static byte GENERIC                   = 3;

    /**
     * 较快速度 (现在规定,宠物,疾跑 等加持使用该速度)
     */
    public final static byte FASTER                    = 4;

    /**
     * 最快速度
     */
    public final static byte FASTEST                   = 5;
    
    //-------------说明-------------
    //当处于减速状态的时候暂时规定只有1档速度:SLOWER
    //当处于正常状态的时候肯定只有1档速度:GENERIC
    //当处于加速状态的时候因为加速等级不同而不同.
    //-------------说明-------------
    /**
     * 速度状态, 0=减速;1=正常;2=加速
     */
    public final static byte SPEED_LESSEN_STATE        = 0;
    /**
     * 速度状态, 0=减速;1=正常;2=加速
     */
    public final static byte SPEED_GENERIC_STATE       = 1;
    /**
     * 速度状态, 0=减速;1=正常;2=加速
     */
    public final static byte SPEED_ADD_STATE           = 2;
    
    /**
     * 根据速度状态 以及加速等级获得最终速度值.
     * @param _speed
     * @param _speedLevel
     * @return
     */
    public static byte getNowSpeed(byte _speed, int _speedLevel)
    {
    	byte speed = 3;
    	if(_speed == SPEED_LESSEN_STATE)
    	{
    		speed = LESSEN_SPEED_LIST[_speedLevel];
    	}
    	else if (_speed == SPEED_GENERIC_STATE) 
    	{
    		speed = GENERIC;
		}
    	else if (_speed == SPEED_ADD_STATE) 
    	{
    		speed = ADD_SPEED_LIST[_speedLevel];
		}
    	return speed;
    }
    
    /**
     * 各等级的加速效果 {普通,快,超快}
     */
    private final static byte[] ADD_SPEED_LIST          = {GENERIC, FASTER, FASTEST};
    /**
     * 各等级的减速效果 {普通,慢,超慢}
     */
    private final static byte[] LESSEN_SPEED_LIST       = {GENERIC, SLOWER, SLOWEST};

    /**
     * 普通怪物追人提升的速度
     */
    public final static byte NORMAL_MONSTER_ATTACK_ADD = 1;

    /**
     * BOSS怪物追人提升的速度
     */
    public final static byte BOSS_MONSTER_ATTACK_ADD   = 2;
}
