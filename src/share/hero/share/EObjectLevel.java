package hero.share;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-10-16 上午10:24:15
 * @描述 ：
 */

public enum EObjectLevel
{
    NORMAL(1, "普通")
    {
        public int getBaseExperiencePara ()
        {
            return 1;
        }

        public int getHpCalPara ()
        {
            return 1;
        }

        public int getMpCalPara ()
        {
            return 1;
        }

        public int getPhysicsAttckCalPara ()
        {
            return 1;
        }
    },
    PROMINENT(2, "精英")
    {
        public int getBaseExperiencePara ()
        {
            return 2;
        }

        public int getHpCalPara ()
        {
            return 4;
        }

        public int getMpCalPara ()
        {
            return 2;
        }

        public int getPhysicsAttckCalPara ()
        {
            return 2;
        }
    },
    HERO(3, "英雄")
    {
        public int getBaseExperiencePara ()
        {
            return 4;
        }

        public int getHpCalPara ()
        {
            return 8;
        }

        public int getMpCalPara ()
        {
            return 4;
        }

        public int getPhysicsAttckCalPara ()
        {
            return 4;
        }
    },
    MONARCH(4, "王者")
    {
        public int getBaseExperiencePara ()
        {
            return 8;
        }

        public int getHpCalPara ()
        {
            return 16;
        }

        public int getMpCalPara ()
        {
            return 16;
        }

        public int getPhysicsAttckCalPara ()
        {
            return 6;
        }
    },
    JINN(5, "神灵")
    {
        public int getBaseExperiencePara ()
        {
            return 16;
        }

        public int getHpCalPara ()
        {
            return 64;
        }

        public int getMpCalPara ()
        {
            return 64;
        }

        public int getPhysicsAttckCalPara ()
        {
            return 10;
        }
    };

    /**
     * 描述
     */
    private String desc;

    /**
     * 标识值
     */
    private int    value;

    /**
     * 获取描述
     * 
     * @return
     */
    public String getDesc ()
    {
        return desc;
    }

    private EObjectLevel(int _value, String _desc)
    {
        value = _value;
        desc = _desc;
    }

    public static EObjectLevel getNPCLevel (String _desc)
    {
        for (EObjectLevel type : EObjectLevel.values())
        {
            if (type.getDesc().equals(_desc))
            {
                return type;
            }
        }

        return null;
    }

    /**
     * 获取标识值
     * 
     * @return
     */
    public int value ()
    {
        return value;
    }

    /**
     * 基础经验计算参数
     * 
     * @return
     */
    public abstract int getBaseExperiencePara ();

    /**
     * HP计算参数
     * 
     * @return
     */
    public abstract int getHpCalPara ();

    /**
     * MP计算参数
     * 
     * @return
     */
    public abstract int getMpCalPara ();

    /**
     * 物理攻击力计算参数
     * 
     * @return
     */
    public abstract int getPhysicsAttckCalPara ();
}
