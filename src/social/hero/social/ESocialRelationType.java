package hero.social;

/**<p>
 *  Copyright: DGFun CO., (c) 2008
 *  </p>
 *  @文件   ESocialRelationType.java
 *  @创建者  DingChu
 *  @版本    1.0
 *  @时间   2010-6-9 下午01:33:52
 *  @描述 ：个人社交关系类型（好友、屏蔽、仇人）
 **/

public enum ESocialRelationType
{
    /**
     * 好友
     */
    FRIEND(1),
    /**
     * 屏蔽
     */
    BLACK(2),
    /**
     * 仇人
     */
    ENEMY(3);

    byte value;

    ESocialRelationType(int _value)
    {
        value = (byte)_value;
    }

    /**
     * 获取社交关系类型
     * 
     * @param _value
     * @return
     */
    public static ESocialRelationType getSocialRelationType (int _value)
    {
        for (ESocialRelationType socialRelationType : ESocialRelationType
                .values())
        {
            if (socialRelationType.value == _value)
            {
                return socialRelationType;
            }
        }

        return null;
    }
    
    public byte value()
    {
        return value;
    }
}


