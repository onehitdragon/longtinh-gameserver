package hero.share;

import java.util.ArrayList;
import java.util.List;

import hero.player.define.EClan;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2005
 * </p>
 * 
 * @file Vocation.java
 * @author Ding Chu
 * @version 1.0
 * @date 2008-10-9 下午05:36:32
 * 
 * <pre>
 *      Description:
 * </pre>
 */
public enum EVocation
{
	ALL(0,"ALL"){

		@Override
		public boolean baseIs (EVocation vocation)
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public float getAgilityCalcPara ()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public EVocation getBasicVoction ()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public float getInteCalcPara ()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getLuckyCalcPara ()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getPhysicsAttackParaA ()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getPhysicsAttackParaB ()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getPhysicsAttackParaC ()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getPhysicsDefenceAgilityPara ()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getPhysicsDefenceSpiritPara ()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getSpiritCalcPara ()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getStaminaCalPara ()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getStrengthCalcPara ()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public EVocation[] getSubVoction (EClan clan)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public EVocationType getType ()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int wearableArmor ()
		{
			// TODO Auto-generated method stub
			return 0;
		}},
    LI_SHI (1, "力士")
    {
        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#getBasicVoction()
         */
        public EVocation getBasicVoction ()
        {
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#getSubVoction(uen.me2.server.define
         *      .Clan)
         */
        public EVocation[] getSubVoction (EClan _clan)
        {
        	EVocation[] list = null;
        	if (_clan == EClan.LONG_SHAN) 
        	{
				list = new EVocation[]{EVocation.JIN_GANG_LI_SHI, EVocation.QING_TIAN_LI_SHI };
			}
        	else
        	{
				list = new EVocation[]{EVocation.LUO_CHA_LI_SHI, EVocation.XIU_LUO_LI_SHI };
			}
            return list;
        }

        /**
         * 职业类型
         * 
         * @return
         */
        public EVocationType getType ()
        {
            return EVocationType.PHYSICS;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#wearableArmor()
         */
        public int wearableArmor ()
        {
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#getStrengthCalcPara()
         */
        public float getStrengthCalcPara ()
        {
            return 5;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#getAgilityCalcPara()
         */
        public float getAgilityCalcPara ()
        {
            return 4;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#getStaminaCalPara()
         */
        public float getStaminaCalPara ()
        {
            return 5;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#getInteCalcPara()
         */
        public float getInteCalcPara ()
        {
            return 3;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#getSpiritCalcPara()
         */
        public float getSpiritCalcPara ()
        {
            return 3;
        }

        /*
         * (non-Javadoc)
         * 
         * @see hero.player.define.EVocation#getLuckCalcPara()
         */
        public float getLuckyCalcPara ()
        {
            return 4;
        }

        public float getPhysicsAttackParaA ()
        {
            return 0.5F;
        }

        public float getPhysicsAttackParaB ()
        {
            return 0.3F;
        }

        public float getPhysicsAttackParaC ()
        {
            return 0F;
        }

        public int getPhysicsDefenceAgilityPara ()
        {
            return 3;
        }

        public int getPhysicsDefenceSpiritPara ()
        {
            return 0;
        }

        public boolean baseIs (EVocation _vocation)
        {
            return false;
        }
    }
    
    ,

    CHI_HOU (2, "斥候")
    {

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#getBasicVoction()
         */
        public EVocation getBasicVoction ()
        {
            return null;
        }

        /**
         * 职业类型
         * 
         * @return
         */
        public EVocationType getType ()
        {
            return EVocationType.RANGER;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#getSubVoction(uen.me2.server.define
         *      .Clan)
         */
        public EVocation[] getSubVoction (EClan _clan)
        {
        	EVocation[] list = null;
        	if (_clan == EClan.LONG_SHAN) 
        	{
				list = new EVocation[]{EVocation.LI_JIAN_CHI_HOU, EVocation.SHEN_JIAN_CHI_HOU};
			}
        	else
        	{
				list = new EVocation[]{EVocation.XIE_REN_CHI_HOU, EVocation.GUI_YI_CHI_HOU};
			}
            return list;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#wearableArmor()
         */
        public int wearableArmor ()
        {
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#getStrengthCalcPara()
         */
        public float getStrengthCalcPara ()
        {
            return 5;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#getAgilityCalcPara()
         */
        public float getAgilityCalcPara ()
        {
            return 4;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#getStaminaCalPara()
         */
        public float getStaminaCalPara ()
        {
            return 4;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#getInteCalcPara()
         */
        public float getInteCalcPara ()
        {
            return 3;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#getSpiritCalcPara()
         */
        public float getSpiritCalcPara ()
        {
            return 3;
        }

        public float getLuckyCalcPara ()
        {
            return 5;
        }

        public float getPhysicsAttackParaA ()
        {
            return 0.3F;
        }

        public float getPhysicsAttackParaB ()
        {
            return 0.5F;
        }

        public float getPhysicsAttackParaC ()
        {
            return 0F;
        }

        public int getPhysicsDefenceAgilityPara ()
        {
            return 1;
        }

        public int getPhysicsDefenceSpiritPara ()
        {
            return 0;
        }

        public boolean baseIs (EVocation _vocation)
        {
            return false;
        }
    }

    ,

    FA_SHI (3, "法师")
    {
        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#getBasicVoction()
         */
        public EVocation getBasicVoction ()
        {
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#getSubVoction(uen.me2.server.define
         *      .Clan)
         */
        public EVocation[] getSubVoction (EClan _clan)
        {
        	EVocation[] list = null;
        	if (_clan == EClan.LONG_SHAN) 
        	{
				list = new EVocation[]{EVocation.YU_HUO_FA_SHI, EVocation.TIAN_JI_FA_SHI};
			}
        	else
        	{
				list = new EVocation[]{EVocation.YAN_MO_FA_SHI, EVocation.XUAN_MING_FA_SHI};
			}
            return list;
        }

        /**
         * 职业类型
         * 
         * @return
         */
        public EVocationType getType ()
        {
            return EVocationType.MAGIC;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#wearableArmor()
         */
        public int wearableArmor ()
        {
            return 0;
        }

        /**
         * 与等级相关的基本力量值计算参数
         * 
         * @return
         */
        public float getStrengthCalcPara ()
        {
            return 3;
        }

        /**
         * 与等级相关的基本敏捷值计算参数
         * 
         * @return
         */
        public float getAgilityCalcPara ()
        {
            return 3;
        }

        /**
         * 与等级相关的基本耐力值计算参数
         * 
         * @return
         */
        public float getStaminaCalPara ()
        {
            return 4;
        }

        /**
         * 与等级相关的基本智力值计算参数
         * 
         * @return
         */
        public float getInteCalcPara ()
        {
            return 5.5F;
        }

        /**
         * 与等级相关的基本精神值计算参数
         * 
         * @return
         */
        public float getSpiritCalcPara ()
        {
            return 4.5F;
        }

        public float getLuckyCalcPara ()
        {
            return 5;
        }

        public float getPhysicsAttackParaA ()
        {
            return 0.1F;
        }

        public float getPhysicsAttackParaB ()
        {
            return 0.1F;
        }

        public float getPhysicsAttackParaC ()
        {
            return 0F;
        }

        public int getPhysicsDefenceAgilityPara ()
        {
            return 3;
        }

        public int getPhysicsDefenceSpiritPara ()
        {
            return 1;
        }

        public boolean baseIs (EVocation _vocation)
        {
            return false;
        }
    }

    ,

    WU_YI (4, "巫医")
    {

        /**
         * 由什么职业转变而来
         * 
         * @return
         */
        public EVocation getBasicVoction ()
        {
            return null;
        }

        /**
         * 可转变的职业列表
         * 
         * @return
         */
        public EVocation[] getSubVoction (EClan _clan)
        {
        	EVocation[] list = null;
        	if (_clan == EClan.LONG_SHAN) 
        	{
				list = new EVocation[]{EVocation.MIAO_SHOU_WU_YI, EVocation.LING_QUAN_WU_YI};
			}
        	else
        	{
				list = new EVocation[]{EVocation.XIE_JI_WU_YI, EVocation.YIN_YANG_WU_YI};
			}
            return list;
        }

        /**
         * 职业类型
         * 
         * @return
         */
        public EVocationType getType ()
        {
            return EVocationType.PRIEST;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#wearableArmor()
         */
        public int wearableArmor ()
        {
            return 0;
        }

        /**
         * 与等级相关的基本力量值计算参数
         * 
         * @return
         */
        public float getStrengthCalcPara ()
        {
            return 3F;
        }

        /**
         * 与等级相关的基本敏捷值计算参数
         * 
         * @return
         */
        public float getAgilityCalcPara ()
        {
            return 3;
        }

        /**
         * 与等级相关的基本耐力值计算参数
         * 
         * @return
         */
        public float getStaminaCalPara ()
        {
            return 4F;
        }

        /**
         * 与等级相关的基本智力值计算参数
         * 
         * @return
         */
        public float getInteCalcPara ()
        {
            return 4.5F;
        }

        /**
         * 与等级相关的基本精神值计算参数
         * 
         * @return
         */
        public float getSpiritCalcPara ()
        {
            return 5.5F;
        }

        public float getLuckyCalcPara ()
        {
            return 5;
        }

        public float getPhysicsAttackParaA ()
        {
            return 0.1F;
        }

        public float getPhysicsAttackParaB ()
        {
            return 0.1F;
        }

        public float getPhysicsAttackParaC ()
        {
            return 0F;
        }

        public int getPhysicsDefenceAgilityPara ()
        {
            return 3;
        }

        public int getPhysicsDefenceSpiritPara ()
        {
            return 1;
        }

        public boolean baseIs (EVocation _vocation)
        {
            return false;
        }
    }
    ,

    //--------------
    //下面为2转职业分类
    //--------------

    JIN_GANG_LI_SHI (5, "金刚力士")
    {
        /**
         * 由什么职业转变而来
         * 
         * @return
         */
        public EVocation getBasicVoction ()
        {
            return LI_SHI;
        }

        /**
         * 可转变的职业列表
         * 
         * @return
         */
        public EVocation[] getSubVoction (EClan _clan)
        {
            return null;
        }

        /**
         * 职业类型
         * 
         * @return
         */
        public EVocationType getType ()
        {
            return EVocationType.PHYSICS;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#wearableArmor()
         */
        public int wearableArmor ()
        {
            return 0;
        }

        /**
         * 与等级相关的基本力量值计算参数
         * 
         * @return
         */
        public float getStrengthCalcPara ()
        {
            return 6;
        }

        /**
         * 与等级相关的基本敏捷值计算参数
         * 
         * @return
         */
        public float getAgilityCalcPara ()
        {
            return 6;
        }

        /**
         * 与等级相关的基本耐力值计算参数
         * 
         * @return
         */
        public float getStaminaCalPara ()
        {
            return 7;
        }

        /**
         * 与等级相关的基本智力值计算参数
         * 
         * @return
         */
        public float getInteCalcPara ()
        {
            return 3;
        }

        /**
         * 与等级相关的基本精神值计算参数
         * 
         * @return
         */
        public float getSpiritCalcPara ()
        {
            return 3;
        }

        public float getLuckyCalcPara ()
        {
            return 4;
        }

        public float getPhysicsAttackParaA ()
        {
            return 0.5F;
        }

        public float getPhysicsAttackParaB ()
        {
            return 0.3F;
        }

        public float getPhysicsAttackParaC ()
        {
            return 0F;
        }

        public int getPhysicsDefenceAgilityPara ()
        {
            return 3;
        }

        public int getPhysicsDefenceSpiritPara ()
        {
            return 0;
        }

        public boolean baseIs (EVocation _vocation)
        {
            return _vocation == LI_SHI;
        }
    }
    ,

    QING_TIAN_LI_SHI (6, "擎天力士")
    {
        /**
         * 由什么职业转变而来
         * 
         * @return
         */
        public EVocation getBasicVoction ()
        {
            return LI_SHI;
        }

        /**
         * 可转变的职业列表
         * 
         * @return
         */
        public EVocation[] getSubVoction (EClan _clan)
        {
            return null;
        }

        /**
         * 职业类型
         * 
         * @return
         */
        public EVocationType getType ()
        {
            return EVocationType.PHYSICS;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#wearableArmor()
         */
        public int wearableArmor ()
        {
            return 0;
        }

        /**
         * 与等级相关的基本力量值计算参数
         * 
         * @return
         */
        public float getStrengthCalcPara ()
        {
            return 7F;
        }

        /**
         * 与等级相关的基本敏捷值计算参数
         * 
         * @return
         */
        public float getAgilityCalcPara ()
        {
            return 6;
        }

        /**
         * 与等级相关的基本耐力值计算参数
         * 
         * @return
         */
        public float getStaminaCalPara ()
        {
            return 6;
        }

        /**
         * 与等级相关的基本智力值计算参数
         * 
         * @return
         */
        public float getInteCalcPara ()
        {
            return 3;
        }

        /**
         * 与等级相关的基本精神值计算参数
         * 
         * @return
         */
        public float getSpiritCalcPara ()
        {
            return 3;
        }

        public float getLuckyCalcPara ()
        {
            return 4F;
        }

        public float getPhysicsAttackParaA ()
        {
            return 0.5F;
        }

        public float getPhysicsAttackParaB ()
        {
            return 0.3F;
        }

        public float getPhysicsAttackParaC ()
        {
            return 0;
        }

        public int getPhysicsDefenceAgilityPara ()
        {
            return 3;
        }

        public int getPhysicsDefenceSpiritPara ()
        {
            return 0;
        }

        public boolean baseIs (EVocation _vocation)
        {
            return _vocation == LI_SHI;
        }
    }    
    
    ,

    LUO_CHA_LI_SHI (7, "罗刹力士")
    {
        /**
         * 由什么职业转变而来
         * 
         * @return
         */
        public EVocation getBasicVoction ()
        {
            return EVocation.LI_SHI;
        }

        /**
         * 可转变的职业列表
         * 
         * @return
         */
        public EVocation[] getSubVoction (EClan _clan)
        {
            return null;
        }

        /**
         * 职业类型
         * 
         * @return
         */
        public EVocationType getType ()
        {
            return EVocationType.PHYSICS;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#wearableArmor()
         */
        public int wearableArmor ()
        {
            return 0;
        }

        /**
         * 与等级相关的基本力量值计算参数
         * 
         * @return
         */
        public float getStrengthCalcPara ()
        {
            return 6;
        }

        /**
         * 与等级相关的基本敏捷值计算参数
         * 
         * @return
         */
        public float getAgilityCalcPara ()
        {
            return 6;
        }

        /**
         * 与等级相关的基本耐力值计算参数
         * 
         * @return
         */
        public float getStaminaCalPara ()
        {
            return 7F;
        }

        /**
         * 与等级相关的基本智力值计算参数
         * 
         * @return
         */
        public float getInteCalcPara ()
        {
            return 3;
        }

        /**
         * 与等级相关的基本精神值计算参数
         * 
         * @return
         */
        public float getSpiritCalcPara ()
        {
            return 3;
        }

        public float getLuckyCalcPara ()
        {
            return 4F;
        }

        public float getPhysicsAttackParaA ()
        {
            return 0.5F;
        }

        public float getPhysicsAttackParaB ()
        {
            return 0.3F;
        }

        public float getPhysicsAttackParaC ()
        {
            return 0;
        }

        public int getPhysicsDefenceAgilityPara ()
        {
            return 3;
        }

        public int getPhysicsDefenceSpiritPara ()
        {
            return 0;
        }

        public boolean baseIs (EVocation _vocation)
        {
            return _vocation == LI_SHI;
        }
    }
    ,

    XIU_LUO_LI_SHI (8, "修罗力士")
    {
        /**
         * 由什么职业转变而来
         * 
         * @return
         */
        public EVocation getBasicVoction ()
        {
            return LI_SHI;
        }

        /**
         * 可转变的职业列表
         * 
         * @return
         */
        public EVocation[] getSubVoction (EClan _clan)
        {
            return null;
        }

        /**
         * 职业类型
         * 
         * @return
         */
        public EVocationType getType ()
        {
            return EVocationType.PHYSICS;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#wearableArmor()
         */
        public int wearableArmor ()
        {
            return 0;
        }

        /**
         * 与等级相关的基本力量值计算参数
         * 
         * @return
         */
        public float getStrengthCalcPara ()
        {
            return 7F;
        }

        /**
         * 与等级相关的基本敏捷值计算参数
         * 
         * @return
         */
        public float getAgilityCalcPara ()
        {
            return 6;
        }

        /**
         * 与等级相关的基本耐力值计算参数
         * 
         * @return
         */
        public float getStaminaCalPara ()
        {
            return 6F;
        }

        /**
         * 与等级相关的基本智力值计算参数
         * 
         * @return
         */
        public float getInteCalcPara ()
        {
            return 3;
        }

        /**
         * 与等级相关的基本精神值计算参数
         * 
         * @return
         */
        public float getSpiritCalcPara ()
        {
            return 3F;
        }

        public float getLuckyCalcPara ()
        {
            return 4;
        }

        public float getPhysicsAttackParaA ()
        {
            return 0.5F;
        }

        public float getPhysicsAttackParaB ()
        {
            return 0.3F;
        }

        public float getPhysicsAttackParaC ()
        {
            return 0;
        }

        public int getPhysicsDefenceAgilityPara ()
        {
            return 3;
        }

        public int getPhysicsDefenceSpiritPara ()
        {
            return 0;
        }

        public boolean baseIs (EVocation _vocation)
        {
            return _vocation == LI_SHI;
        }
    }
    ,

    LI_JIAN_CHI_HOU (9, "砺剑斥候")
    {
        /**
         * 由什么职业转变而来
         * 
         * @return
         */
        public EVocation getBasicVoction ()
        {
            return CHI_HOU;
        }

        /**
         * 可转变的职业列表
         * 
         * @return
         */
        public EVocation[] getSubVoction (EClan _clan)
        {
            return null;
        }

        /**
         * 职业类型
         * 
         * @return
         */
        public EVocationType getType ()
        {
            return EVocationType.RANGER;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#wearableArmor()
         */
        public int wearableArmor ()
        {
            return 0;
        }

        /**
         * 与等级相关的基本力量值计算参数
         * 
         * @return
         */
        public float getStrengthCalcPara ()
        {
            return 5.5F;
        }

        /**
         * 与等级相关的基本敏捷值计算参数
         * 
         * @return
         */
        public float getAgilityCalcPara ()
        {
            return 7.5F;
        }

        /**
         * 与等级相关的基本耐力值计算参数
         * 
         * @return
         */
        public float getStaminaCalPara ()
        {
            return 5;
        }

        /**
         * 与等级相关的基本智力值计算参数
         * 
         * @return
         */
        public float getInteCalcPara ()
        {
            return 3;
        }

        /**
         * 与等级相关的基本精神值计算参数
         * 
         * @return
         */
        public float getSpiritCalcPara ()
        {
            return 3;
        }

        public float getLuckyCalcPara ()
        {
            return 5;
        }

        public float getPhysicsAttackParaA ()
        {
            return 0.3F;
        }

        public float getPhysicsAttackParaB ()
        {
            return 0.5F;
        }

        public float getPhysicsAttackParaC ()
        {
            return 0;
        }

        public int getPhysicsDefenceAgilityPara ()
        {
            return 3;
        }

        public int getPhysicsDefenceSpiritPara ()
        {
            return 0;
        }

        public boolean baseIs (EVocation _vocation)
        {
            return _vocation == CHI_HOU;
        }
    }
    ,

    SHEN_JIAN_CHI_HOU (10, "神箭斥候")
    {
        /**
         * 与等级相关的基本力量值计算参数
         * 
         * @return
         */
        public EVocation getBasicVoction ()
        {
            return CHI_HOU;
        }

        /**
         * 可转变的职业列表
         * 
         * @return
         */
        public EVocation[] getSubVoction (EClan _clan)
        {
            return null;
        }

        /**
         * 职业类型
         * 
         * @return
         */
        public EVocationType getType ()
        {
            return EVocationType.RANGER;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#wearableArmor()
         */
        public int wearableArmor ()
        {
            return 0;
        }

        /**
         * 与等级相关的基本力量值计算参数
         * 
         * @return
         */
        public float getStrengthCalcPara ()
        {
            return 5.5F;
        }

        /**
         * 与等级相关的基本敏捷值计算参数
         * 
         * @return
         */
        public float getAgilityCalcPara ()
        {
            return 7;
        }

        /**
         * 与等级相关的基本耐力值计算参数
         * 
         * @return
         */
        public float getStaminaCalPara ()
        {
            return 5.5F;
        }

        /**
         * 与等级相关的基本智力值计算参数
         * 
         * @return
         */
        public float getInteCalcPara ()
        {
            return 3F;
        }

        /**
         * 与等级相关的基本精神值计算参数
         * 
         * @return
         */
        public float getSpiritCalcPara ()
        {
            return 3;
        }

        public float getLuckyCalcPara ()
        {
            return 5;
        }

        public float getPhysicsAttackParaA ()
        {
            return 0.3F;
        }

        public float getPhysicsAttackParaB ()
        {
            return 0.5F;
        }

        public float getPhysicsAttackParaC ()
        {
            return 0F;
        }

        public int getPhysicsDefenceAgilityPara ()
        {
            return 1;
        }

        public int getPhysicsDefenceSpiritPara ()
        {
            return 0;
        }

        public boolean baseIs (EVocation _vocation)
        {
            return _vocation == CHI_HOU;
        }
    }
    ,

    XIE_REN_CHI_HOU (11, "血刃斥候")
    {
        /**
         * 由什么职业转变而来
         * 
         * @return
         */
        public EVocation getBasicVoction ()
        {
            return CHI_HOU;
        }

        /**
         * 可转变的职业列表
         * 
         * @return
         */
        public EVocation[] getSubVoction (EClan _clan)
        {
            return null;
        }

        /**
         * 职业类型
         * 
         * @return
         */
        public EVocationType getType ()
        {
            return EVocationType.RANGER;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#wearableArmor()
         */
        public int wearableArmor ()
        {
            return 0;
        }

        /**
         * 与等级相关的基本力量值计算参数
         * 
         * @return
         */
        public float getStrengthCalcPara ()
        {
            return 5.5F;
        }

        /**
         * 与等级相关的基本敏捷值计算参数
         * 
         * @return
         */
        public float getAgilityCalcPara ()
        {
            return 7.5F;
        }

        /**
         * 与等级相关的基本耐力值计算参数
         * 
         * @return
         */
        public float getStaminaCalPara ()
        {
            return 5;
        }

        /**
         * 与等级相关的基本智力值计算参数
         * 
         * @return
         */
        public float getInteCalcPara ()
        {
            return 3;
        }

        /**
         * 与等级相关的基本精神值计算参数
         * 
         * @return
         */
        public float getSpiritCalcPara ()
        {
            return 3;
        }

        public float getLuckyCalcPara ()
        {
            return 5;
        }

        public float getPhysicsAttackParaA ()
        {
            return 0.3F;
        }

        public float getPhysicsAttackParaB ()
        {
            return 0.5F;
        }

        public float getPhysicsAttackParaC ()
        {
            return 0F;
        }

        public int getPhysicsDefenceAgilityPara ()
        {
            return 1;
        }

        public int getPhysicsDefenceSpiritPara ()
        {
            return 0;
        }

        public boolean baseIs (EVocation _vocation)
        {
            return _vocation == CHI_HOU;
        }
    }
    ,

    GUI_YI_CHI_HOU (12, "鬼羿斥候")
    {
        /**
         * 由什么职业转变而来
         * 
         * @return
         */
        public EVocation getBasicVoction ()
        {
            return CHI_HOU;
        }

        /**
         * 可转变的职业列表
         * 
         * @return
         */
        public EVocation[] getSubVoction (EClan _clan)
        {
            return null;
        }

        /**
         * 职业类型
         * 
         * @return
         */
        public EVocationType getType ()
        {
            return EVocationType.RANGER;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#wearableArmor()
         */
        public int wearableArmor ()
        {
            return 0;
        }

        /**
         * 与等级相关的基本力量值计算参数
         * 
         * @return
         */
        public float getStrengthCalcPara ()
        {
            return 5.5F;
        }

        /**
         * 与等级相关的基本敏捷值计算参数
         * 
         * @return
         */
        public float getAgilityCalcPara ()
        {
            return 7;
        }

        /**
         * 与等级相关的基本耐力值计算参数
         * 
         * @return
         */
        public float getStaminaCalPara ()
        {
            return 5.5F;
        }

        /**
         * 与等级相关的基本智力值计算参数
         * 
         * @return
         */
        public float getInteCalcPara ()
        {
            return 3;
        }

        /**
         * 与等级相关的基本精神值计算参数
         * 
         * @return
         */
        public float getSpiritCalcPara ()
        {
            return 3;
        }

        public float getLuckyCalcPara ()
        {
            return 5F;
        }

        public float getPhysicsAttackParaA ()
        {
            return 0.3F;
        }

        public float getPhysicsAttackParaB ()
        {
            return 0.5F;
        }

        public float getPhysicsAttackParaC ()
        {
            return 0F;
        }

        public int getPhysicsDefenceAgilityPara ()
        {
            return 1;
        }

        public int getPhysicsDefenceSpiritPara ()
        {
            return 0;
        }

        public boolean baseIs (EVocation _vocation)
        {
            return _vocation == CHI_HOU;
        }
    }
    
    ,

    YU_HUO_FA_SHI (13, "浴火法师")
    {
        /**
         * 由什么职业转变而来
         * 
         * @return
         */
        public EVocation getBasicVoction ()
        {
//            return LONG_HUN_SHI;
            return FA_SHI;
        }

        /**
         * 可转变的职业列表
         * 
         * @return
         */
        public EVocation[] getSubVoction (EClan _clan)
        {
            return null;
        }

        /**
         * 职业类型
         * 
         * @return
         */
        public EVocationType getType ()
        {
            return EVocationType.MAGIC;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#wearableArmor()
         */
        public int wearableArmor ()
        {
            return 0;
        }

        /**
         * 与等级相关的基本力量值计算参数
         * 
         * @return
         */
        public float getStrengthCalcPara ()
        {
            return 3;
        }

        /**
         * 与等级相关的基本敏捷值计算参数
         * 
         * @return
         */
        public float getAgilityCalcPara ()
        {
            return 3;
        }

        /**
         * 与等级相关的基本耐力值计算参数
         * 
         * @return
         */
        public float getStaminaCalPara ()
        {
            return 4.5F;
        }

        /**
         * 与等级相关的基本智力值计算参数
         * 
         * @return
         */
        public float getInteCalcPara ()
        {
            return 7;
        }

        /**
         * 与等级相关的基本精神值计算参数
         * 
         * @return
         */
        public float getSpiritCalcPara ()
        {
            return 6.5F;
        }

        public float getLuckyCalcPara ()
        {
            return 5;
        }

        public float getPhysicsAttackParaA ()
        {
            return 0.1F;
        }

        public float getPhysicsAttackParaB ()
        {
            return 0.1F;
        }

        public float getPhysicsAttackParaC ()
        {
            return 0F;
        }

        public int getPhysicsDefenceAgilityPara ()
        {
            return 3;
        }

        public int getPhysicsDefenceSpiritPara ()
        {
            return 1;
        }

        public boolean baseIs (EVocation _vocation)
        {
            return _vocation == FA_SHI;
        }
    }
    
    ,

    TIAN_JI_FA_SHI (14, "天机法师")
    {
        /**
         * 由什么职业转变而来
         * 
         * @return
         */
        public EVocation getBasicVoction ()
        {
            return FA_SHI;
        }

        /**
         * 可转变的职业列表
         * 
         * @return
         */
        public EVocation[] getSubVoction (EClan _clan)
        {
            return null;
        }

        /**
         * 职业类型
         * 
         * @return
         */
        public EVocationType getType ()
        {
            return EVocationType.MAGIC;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#wearableArmor()
         */
        public int wearableArmor ()
        {
            return 0;
        }

        /**
         * 与等级相关的基本力量值计算参数
         * 
         * @return
         */
        public float getStrengthCalcPara ()
        {
            return 3;
        }

        /**
         * 与等级相关的基本敏捷值计算参数
         * 
         * @return
         */
        public float getAgilityCalcPara ()
        {
            return 3;
        }

        /**
         * 与等级相关的基本耐力值计算参数
         * 
         * @return
         */
        public float getStaminaCalPara ()
        {
            return 5.5F;
        }

        /**
         * 与等级相关的基本智力值计算参数
         * 
         * @return
         */
        public float getInteCalcPara ()
        {
            return 6.5F;
        }

        /**
         * 与等级相关的基本精神值计算参数
         * 
         * @return
         */
        public float getSpiritCalcPara ()
        {
            return 6;
        }

        public float getLuckyCalcPara ()
        {
            return 5;
        }

        public float getPhysicsAttackParaA ()
        {
            return 0.1F;
        }

        public float getPhysicsAttackParaB ()
        {
            return 0.1F;
        }

        public float getPhysicsAttackParaC ()
        {
            return 0F;
        }

        public int getPhysicsDefenceAgilityPara ()
        {
            return 3;
        }

        public int getPhysicsDefenceSpiritPara ()
        {
            return 1;
        }

        public boolean baseIs (EVocation _vocation)
        {
            return _vocation == FA_SHI;
        }
    }
    
    ,

    YAN_MO_FA_SHI (15, "炎魔法师")
    {
        /**
         * 由什么职业转变而来
         * 
         * @return
         */
        public EVocation getBasicVoction ()
        {
            return FA_SHI;
        }

        /**
         * 可转变的职业列表
         * 
         * @return
         */
        public EVocation[] getSubVoction (EClan _clan)
        {
            return null;
        }

        /**
         * 职业类型
         * 
         * @return
         */
        public EVocationType getType ()
        {
            return EVocationType.MAGIC;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#wearableArmor()
         */
        public int wearableArmor ()
        {
            return 0;
        }

        /**
         * 与等级相关的基本力量值计算参数
         * 
         * @return
         */
        public float getStrengthCalcPara ()
        {
            return 3;
        }

        /**
         * 与等级相关的基本敏捷值计算参数
         * 
         * @return
         */
        public float getAgilityCalcPara ()
        {
            return 3;
        }

        /**
         * 与等级相关的基本耐力值计算参数
         * 
         * @return
         */
        public float getStaminaCalPara ()
        {
            return 4.5F;
        }

        /**
         * 与等级相关的基本智力值计算参数
         * 
         * @return
         */
        public float getInteCalcPara ()
        {
            return 7;
        }

        /**
         * 与等级相关的基本精神值计算参数
         * 
         * @return
         */
        public float getSpiritCalcPara ()
        {
            return 6.5F;
        }

        public float getLuckyCalcPara ()
        {
            return 5;
        }

        public float getPhysicsAttackParaA ()
        {
            return 0.1F;
        }

        public float getPhysicsAttackParaB ()
        {
            return 0.1F;
        }

        public float getPhysicsAttackParaC ()
        {
            return 0F;
        }

        public int getPhysicsDefenceAgilityPara ()
        {
            return 3;
        }

        public int getPhysicsDefenceSpiritPara ()
        {
            return 1;
        }

        public boolean baseIs (EVocation _vocation)
        {
            return _vocation == FA_SHI;
        }
    }
    
    ,

    XUAN_MING_FA_SHI (16, "玄冥法师")
    {
        /**
         * 由什么职业转变而来
         * 
         * @return
         */
        public EVocation getBasicVoction ()
        {
            return FA_SHI;
        }

        /**
         * 可转变的职业列表
         * 
         * @return
         */
        public EVocation[] getSubVoction (EClan _clan)
        {
            return null;
        }

        /**
         * 职业类型
         * 
         * @return
         */
        public EVocationType getType ()
        {
            return EVocationType.MAGIC;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#wearableArmor()
         */
        public int wearableArmor ()
        {
            return 0;
        }

        /**
         * 与等级相关的基本力量值计算参数
         * 
         * @return
         */
        public float getStrengthCalcPara ()
        {
            return 3;
        }

        /**
         * 与等级相关的基本敏捷值计算参数
         * 
         * @return
         */
        public float getAgilityCalcPara ()
        {
            return 3;
        }

        /**
         * 与等级相关的基本耐力值计算参数
         * 
         * @return
         */
        public float getStaminaCalPara ()
        {
            return 5.5F;
        }

        /**
         * 与等级相关的基本智力值计算参数
         * 
         * @return
         */
        public float getInteCalcPara ()
        {
            return 6.5F;
        }

        /**
         * 与等级相关的基本精神值计算参数
         * 
         * @return
         */
        public float getSpiritCalcPara ()
        {
            return 6;
        }

        public float getLuckyCalcPara ()
        {
            return 5;
        }

        public float getPhysicsAttackParaA ()
        {
            return 0.1F;
        }

        public float getPhysicsAttackParaB ()
        {
            return 0.1F;
        }

        public float getPhysicsAttackParaC ()
        {
            return 0F;
        }

        public int getPhysicsDefenceAgilityPara ()
        {
            return 3;
        }

        public int getPhysicsDefenceSpiritPara ()
        {
            return 1;
        }

        public boolean baseIs (EVocation _vocation)
        {
            return _vocation == FA_SHI;
        }
    }
    
    ,

    MIAO_SHOU_WU_YI (17, "妙手巫医")
    {
        /**
         * 由什么职业转变而来
         * 
         * @return
         */
        public EVocation getBasicVoction ()
        {
            return WU_YI;
        }

        /**
         * 可转变的职业列表
         * 
         * @return
         */
        public EVocation[] getSubVoction (EClan _clan)
        {
            return null;
        }

        /**
         * 职业类型
         * 
         * @return
         */
        public EVocationType getType ()
        {
            return EVocationType.PRIEST;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#wearableArmor()
         */
        public int wearableArmor ()
        {
            return 0;
        }

        /**
         * 与等级相关的基本力量值计算参数
         * 
         * @return
         */
        public float getStrengthCalcPara ()
        {
            return 3;
        }

        /**
         * 与等级相关的基本敏捷值计算参数
         * 
         * @return
         */
        public float getAgilityCalcPara ()
        {
            return 3;
        }

        /**
         * 与等级相关的基本耐力值计算参数
         * 
         * @return
         */
        public float getStaminaCalPara ()
        {
            return 4.7F;
        }

        /**
         * 与等级相关的基本智力值计算参数
         * 
         * @return
         */
        public float getInteCalcPara ()
        {
            return 6.3F;
        }

        /**
         * 与等级相关的基本精神值计算参数
         * 
         * @return
         */
        public float getSpiritCalcPara ()
        {
            return 7;
        }

        public float getLuckyCalcPara ()
        {
            return 5;
        }

        public float getPhysicsAttackParaA ()
        {
            return 0.1F;
        }

        public float getPhysicsAttackParaB ()
        {
            return 0.1F;
        }

        public float getPhysicsAttackParaC ()
        {
            return 0F;
        }

        public int getPhysicsDefenceAgilityPara ()
        {
            return 3;
        }

        public int getPhysicsDefenceSpiritPara ()
        {
            return 1;
        }

        public boolean baseIs (EVocation _vocation)
        {
            return _vocation == WU_YI;
        }
    }
    
    ,

    LING_QUAN_WU_YI (18, "灵泉巫医")
    {
        /**
         * 由什么职业转变而来
         * 
         * @return
         */
        public EVocation getBasicVoction ()
        {
            return WU_YI;
        }

        /**
         * 可转变的职业列表
         * 
         * @return
         */
        public EVocation[] getSubVoction (EClan _clan)
        {
            return null;
        }

        /**
         * 职业类型
         * 
         * @return
         */
        public EVocationType getType ()
        {
            return EVocationType.PRIEST;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#wearableArmor()
         */
        public int wearableArmor ()
        {
            return 0;
        }

        /**
         * 与等级相关的基本力量值计算参数
         * 
         * @return
         */
        public float getStrengthCalcPara ()
        {
            return 3;
        }

        /**
         * 与等级相关的基本敏捷值计算参数
         * 
         * @return
         */
        public float getAgilityCalcPara ()
        {
            return 3;
        }

        /**
         * 与等级相关的基本耐力值计算参数
         * 
         * @return
         */
        public float getStaminaCalPara ()
        {
            return 4;
        }

        /**
         * 与等级相关的基本智力值计算参数
         * 
         * @return
         */
        public float getInteCalcPara ()
        {
            return 6.5F;
        }

        /**
         * 与等级相关的基本精神值计算参数
         * 
         * @return
         */
        public float getSpiritCalcPara ()
        {
            return 6.5F;
        }

        public float getLuckyCalcPara ()
        {
            return 6;
        }

        public float getPhysicsAttackParaA ()
        {
            return 0.1F;
        }

        public float getPhysicsAttackParaB ()
        {
            return 0.1F;
        }

        public float getPhysicsAttackParaC ()
        {
            return 0F;
        }

        public int getPhysicsDefenceAgilityPara ()
        {
            return 3;
        }

        public int getPhysicsDefenceSpiritPara ()
        {
            return 1;
        }

        public boolean baseIs (EVocation _vocation)
        {
            return _vocation == WU_YI;
        }
    }
    
    ,

    XIE_JI_WU_YI (19, "血祭巫医")
    {
        /**
         * 由什么职业转变而来
         * 
         * @return
         */
        public EVocation getBasicVoction ()
        {
            return WU_YI;
        }

        /**
         * 可转变的职业列表
         * 
         * @return
         */
        public EVocation[] getSubVoction (EClan _clan)
        {
            return null;
        }

        /**
         * 职业类型
         * 
         * @return
         */
        public EVocationType getType ()
        {
            return EVocationType.PRIEST;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#wearableArmor()
         */
        public int wearableArmor ()
        {
            return 0;
        }

        /**
         * 与等级相关的基本力量值计算参数
         * 
         * @return
         */
        public float getStrengthCalcPara ()
        {
            return 3;
        }

        /**
         * 与等级相关的基本敏捷值计算参数
         * 
         * @return
         */
        public float getAgilityCalcPara ()
        {
            return 7;
        }

        /**
         * 与等级相关的基本耐力值计算参数
         * 
         * @return
         */
        public float getStaminaCalPara ()
        {
            return 6;
        }

        /**
         * 与等级相关的基本智力值计算参数
         * 
         * @return
         */
        public float getInteCalcPara ()
        {
            return 6;
        }

        /**
         * 与等级相关的基本精神值计算参数
         * 
         * @return
         */
        public float getSpiritCalcPara ()
        {
            return 6;
        }

        public float getLuckyCalcPara ()
        {
            return 7;
        }

        public float getPhysicsAttackParaA ()
        {
            return 0.1F;
        }

        public float getPhysicsAttackParaB ()
        {
            return 0.1F;
        }

        public float getPhysicsAttackParaC ()
        {
            return 0F;
        }

        public int getPhysicsDefenceAgilityPara ()
        {
            return 3;
        }

        public int getPhysicsDefenceSpiritPara ()
        {
            return 1;
        }

        public boolean baseIs (EVocation _vocation)
        {
            return _vocation == WU_YI;
        }
    }
    
    ,

    YIN_YANG_WU_YI (20, "阴阳巫医")
    {
        /**
         * 由什么职业转变而来
         * 
         * @return
         */
        public EVocation getBasicVoction ()
        {
            return WU_YI;
        }

        /**
         * 可转变的职业列表
         * 
         * @return
         */
        public EVocation[] getSubVoction (EClan _clan)
        {
            return null;
        }

        /**
         * 职业类型
         * 
         * @return
         */
        public EVocationType getType ()
        {
            return EVocationType.PRIEST;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uen.me2.server.model.Vocation#wearableArmor()
         */
        public int wearableArmor ()
        {
            return 0;
        }

        /**
         * 与等级相关的基本力量值计算参数
         * 
         * @return
         */
        public float getStrengthCalcPara ()
        {
            return 3;
        }

        /**
         * 与等级相关的基本敏捷值计算参数
         * 
         * @return
         */
        public float getAgilityCalcPara ()
        {
            return 7;
        }

        /**
         * 与等级相关的基本耐力值计算参数
         * 
         * @return
         */
        public float getStaminaCalPara ()
        {
            return 6;
        }

        /**
         * 与等级相关的基本智力值计算参数
         * 
         * @return
         */
        public float getInteCalcPara ()
        {
            return 6;
        }

        /**
         * 与等级相关的基本精神值计算参数
         * 
         * @return
         */
        public float getSpiritCalcPara ()
        {
            return 6;
        }

        public float getLuckyCalcPara ()
        {
            return 7;
        }

        public float getPhysicsAttackParaA ()
        {
            return 0.1F;
        }

        public float getPhysicsAttackParaB ()
        {
            return 0.1F;
        }

        public float getPhysicsAttackParaC ()
        {
            return 0F;
        }

        public int getPhysicsDefenceAgilityPara ()
        {
            return 3;
        }

        public int getPhysicsDefenceSpiritPara ()
        {
            return 1;
        }

        public boolean baseIs (EVocation _vocation)
        {
            return _vocation == WU_YI;
        }
    }
    ;

    
    
    
    
    
    /**
    
     * 
     * 职业编号
     */
    private byte   value;

    /**
     * 职业名称
     */
    private String desc;

    /**
     * 构造
     * 
     * @param _id
     */
    EVocation(int _value, String _desc)
    {
        value = (byte) _value;
        desc = _desc;
    }

    /**
     * 获取职业编号
     * 
     * @return
     */
    public byte value ()
    {
        return value;
    }

    /**
     * 获取职业名称
     * 
     * @return
     */
    public String getDesc ()
    {
        return desc;
    }

    /**
     * 获取描述对应的职业枚举
     * 
     * @param _desc
     * @return
     */
    public static EVocation getVocationByDesc (String _desc)
    {
        for (EVocation v : EVocation.values())
        {
            if (v.getDesc().equals(_desc))
            {
                return v;
            }
        }

        return null;
    }

    /**
     * 获取所有的职业名称
     * 
     * @return
     */
    public static String[] getVocationDescList ()
    {
        int length = EVocation.values().length;
        String[] list = new String[length];

        for (int i = 0; i < length; ++i)
        {
            list[i] = ((EVocation.values())[i].getDesc());
        }

        return list;
    }

    /**
     * 根据职业编号获取职业枚举
     * 
     * @param _id
     * @return
     */
    public static EVocation getVocationByID (int _id)
    {
        for (EVocation v : EVocation.values())
        {
            if (v.value() == _id)
            {
                return v;
            }
        }

        return null;
    }

    /**
     * 由什么职业转变而来
     * 
     * @return
     */
    public abstract EVocation getBasicVoction ();

    /**
     * 可转变的职业列表
     * 
     * @return
     */
    public abstract EVocation[] getSubVoction (EClan _clan);

    /**
     * 职业类型
     * 
     * @return
     */
    public abstract EVocationType getType ();

    /**
     * 与等级相关的基本力量值计算参数
     * 
     * @return
     */
    public abstract float getStrengthCalcPara ();

    /**
     * 与等级相关的基本敏捷值计算参数
     * 
     * @return
     */
    public abstract float getAgilityCalcPara ();

    /**
     * 与等级相关的基本耐力值计算参数
     * 
     * @return
     */
    public abstract float getStaminaCalPara ();

    /**
     * 与等级相关的基本智力值计算参数
     * 
     * @return
     */
    public abstract float getInteCalcPara ();

    /**
     * 与等级相关的基本精神值计算参数
     * 
     * @return
     */
    public abstract float getSpiritCalcPara ();

    /**
     * 幸运计算参数
     * 
     * @return
     */
    public abstract float getLuckyCalcPara ();

    /**
     * 可穿戴的防具
     * 
     * @return
     */
    public abstract int wearableArmor ();

    /**
     * 物理攻击力计算参数A
     * 
     * @return
     */
    public abstract float getPhysicsAttackParaA ();

    /**
     * 物理攻击力计算参数B
     * 
     * @return
     */
    public abstract float getPhysicsAttackParaB ();

    /**
     * 物理攻击力计算参数C
     * 
     * @return
     */
    public abstract float getPhysicsAttackParaC ();

    /**
     * 防御力计算参数A
     * 
     * @return
     */
    public abstract int getPhysicsDefenceAgilityPara ();

    /**
     * 防御力计算参数B
     * 
     * @return
     */
    public abstract int getPhysicsDefenceSpiritPara ();

    /**
     * 是否是其基础职业
     * 
     * @return
     */
    public abstract boolean baseIs (EVocation _vocation);
}
