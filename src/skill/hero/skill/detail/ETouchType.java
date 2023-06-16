package hero.skill.detail;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ETouchType.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-4-27 上午10:26:32
 * @描述 ：
 */

public enum ETouchType
{
    /**
     * 被近距离物理攻击
     */
    BE_ATTACKED_BY_NEAR_PHYSICS((byte) 1, "被近距离物理攻击")
    {
        public boolean canTouch (ETouchType _touchType, boolean _isSkillTouch)
        {
            return BE_ATTACKED_BY_NEAR_PHYSICS == _touchType;
        }
    },
    /**
     * 被远距离物理攻击
     */
    BE_ATTACKED_BY_DISTANCE_PHYSICS((byte) 2, "被远距离物理攻击")
    {
        public boolean canTouch (ETouchType _touchType, boolean _isSkillTouch)
        {
            return BE_ATTACKED_BY_DISTANCE_PHYSICS == _touchType;
        }
    },
    /**
     * 被物理攻击
     */
    BE_ATTACKED_BY_PHYSICS((byte) 3, "被物理攻击")
    {
        public boolean canTouch (ETouchType _touchType, boolean _isSkillTouch)
        {
            return BE_ATTACKED_BY_DISTANCE_PHYSICS == _touchType
                    || BE_ATTACKED_BY_NEAR_PHYSICS == _touchType;
        }
    },
    /**
     * 被魔法攻击
     */
    BE_ATTACKED_BY_MAGIC((byte) 4, "被魔法攻击")
    {
        public boolean canTouch (ETouchType _touchType, boolean _isSkillTouch)
        {
            return BE_ATTACKED_BY_MAGIC == _touchType;
        }
    },
    /**
     * 被攻击
     */
    BE_ATTACKED((byte) 5, "被攻击")
    {
        public boolean canTouch (ETouchType _touchType, boolean _isSkillTouch)
        {
            return BE_ATTACKED_BY_MAGIC == _touchType
                    || BE_ATTACKED_BY_DISTANCE_PHYSICS == _touchType
                    || BE_ATTACKED_BY_NEAR_PHYSICS == _touchType;
        }
    },
    /**
     * 近距离物理攻击触发
     */
    ATTACK_BY_NEAR_PHYSICS((byte) 6, "近距离物理攻击")
    {
        public boolean canTouch (ETouchType _touchType, boolean _isSkillTouch)
        {
            return ATTACK_BY_NEAR_PHYSICS == _touchType;
        }
    },
    /**
     * 远距离物理攻击触发
     */
    ATTACK_BY_DISTANCE_PHYSICS((byte) 7, "远距离物理攻击")
    {
        public boolean canTouch (ETouchType _touchType, boolean _isSkillTouch)
        {
            return ATTACK_BY_DISTANCE_PHYSICS == _touchType;
        }
    },
    /**
     * 物理攻击触发
     */
    ATTACK_BY_PHYSICS((byte) 8, "物理攻击")
    {
        public boolean canTouch (ETouchType _touchType, boolean _isSkillTouch)
        {
            return ATTACK_BY_NEAR_PHYSICS == _touchType
                    || ATTACK_BY_DISTANCE_PHYSICS == _touchType;
        }
    },
    /**
     * 魔法攻击触发
     */
    ATTACK_BY_MAGIC((byte) 8, "魔法攻击")
    {
        public boolean canTouch (ETouchType _touchType, boolean _isSkillTouch)
        {
            return ATTACK_BY_MAGIC == _touchType;
        }
    },
    /**
     * 使用辅助技能触发
     */
    RESUME_BY_MAGIC((byte) 8, "辅助魔法")
    {
        public boolean canTouch (ETouchType _touchType, boolean _isSkillTouch)
        {
            return RESUME_BY_MAGIC == _touchType;
        }
    },
    /**
     * 使用魔法触发（包括辅助技能、攻击技能）
     */
    USE_MAGIC((byte) 10, "使用魔法")
    {
        public boolean canTouch (ETouchType _touchType, boolean _isSkillTouch)
        {
            if (!_isSkillTouch)
            {
                return false;
            }

            return ATTACK_BY_NEAR_PHYSICS == _touchType
                    || ATTACK_BY_DISTANCE_PHYSICS == _touchType
                    || ATTACK_BY_MAGIC == _touchType
                    || RESUME_BY_MAGIC == _touchType;
        }
    },
    /**
     * 主动行为，包括普通攻击，使用技能
     */
    ACTIVE((byte) 11, "主动行为")
    {
        public boolean canTouch (ETouchType _touchType, boolean _isSkillTouch)
        {
            return ATTACK_BY_MAGIC == _touchType
                    || RESUME_BY_MAGIC == _touchType
                    || ATTACK_BY_NEAR_PHYSICS == _touchType
                    || ATTACK_BY_DISTANCE_PHYSICS == _touchType;
        }
    },
    
    //add:	zhengl
    //date:	2010-12-05
    //note:	添加触发技能的类型
    /**
     * 被暴击
     */
    BE_DEATHBLOW((byte) 12, "被暴击")
    {
        public boolean canTouch (ETouchType _touchType, boolean _isSkillTouch)
        {
            return BE_DEATHBLOW_BY_MAGIC == _touchType
                    || BE_DEATHBLOW_BY_PHYSICS == _touchType;
        }
    },
    
    /**
     * 被物理暴击
     */
    BE_DEATHBLOW_BY_PHYSICS((byte) 13, "被物理暴击")
    {
        public boolean canTouch (ETouchType _touchType, boolean _isSkillTouch)
        {
            return BE_DEATHBLOW_BY_PHYSICS == _touchType;
        }
    },
    
    /**
     * 被魔法暴击
     */
    BE_DEATHBLOW_BY_MAGIC((byte) 14, "被魔法暴击")
    {
        public boolean canTouch (ETouchType _touchType, boolean _isSkillTouch)
        {
            return BE_DEATHBLOW_BY_MAGIC == _touchType;
        }
    },
    
    /**
     * 发动暴击
     */
    TOUCH_DEATHBLOW((byte) 15, "发动暴击")
    {
        public boolean canTouch (ETouchType _touchType, boolean _isSkillTouch)
        {
            return TOUCH_DEATHBLOW_BY_MAGIC == _touchType
                    || TOUCH_DEATHBLOW_BY_PHYSICS == _touchType;
        }
    },
    
    /**
     * 发动物理暴击
     */
    TOUCH_DEATHBLOW_BY_PHYSICS((byte) 16, "发动物理暴击")
    {
        public boolean canTouch (ETouchType _touchType, boolean _isSkillTouch)
        {
            return TOUCH_DEATHBLOW_BY_PHYSICS == _touchType;
        }
    },
    
    /**
     * 发动魔法暴击
     */
    TOUCH_DEATHBLOW_BY_MAGIC((byte) 17, "发动魔法暴击")
    {
        public boolean canTouch (ETouchType _touchType, boolean _isSkillTouch)
        {
            return TOUCH_DEATHBLOW_BY_MAGIC == _touchType;
        }
    }
    //end add
    ;

    private byte   value;

    private String desc;

    /**
     * 构造
     * 
     * @param _value
     * @param _desc
     */
    ETouchType(byte _value, String _desc)
    {
        value = _value;
        desc = _desc;
    }

    /**
     * 值
     * 
     * @return
     */
    public byte value ()
    {
        return value;
    }

    /**
     * 根据描述获取触发方式
     * 
     * @param _desc
     * @return
     */
    public static ETouchType get (String _desc)
    {
        for (ETouchType touchType : ETouchType.values())
        {
            if (touchType.desc.equals(_desc))
            {
                return touchType;
            }
        }

        return null;
    }

    /**
     * 暂时全部都返回true
     * @param _touchType
     * @param _isSkillTouch
     * @return
     */
    public boolean canTouch (ETouchType _touchType,
            boolean _isSkillTouch) {
    	return true;
    }
    
    public boolean canTouch (ETouchType _unitType, ETouchType _printType) {
    	boolean result = false;
    	if(_unitType == _printType)
    	{
    		result = true;
    	}
    	return result;
    }
}