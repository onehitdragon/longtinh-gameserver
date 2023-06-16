package hero.npc.ai.data;

import java.util.Random;

import hero.map.Map;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.npc.ENotPlayerIntention;
import hero.npc.Monster;
import hero.npc.message.MonsterDisengageFightNotify;
import hero.npc.message.MonsterRefreshNotify;
import hero.npc.service.NotPlayerServiceImpl;
import hero.share.Constant;
import hero.share.Direction;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 CallData.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-6-12 上午11:03:34
 * @描述 ：召唤其他怪物
 */

public class Call extends SpecialWisdom
{
    /**
     * 编号
     */
    public int       id;

    /**
     * 召唤时的喊话
     */
    public String    shoutContent;

    /**
     * 怪物模板编号列表
     */
    public String[]  monsterModelIDs;

    /**
     * 怪物出现数据列表（2维数据：数量、出现位置的定位方式、出现位置的X坐标、出现位置的Y坐标）
     */
    public short[][] monsterDataArray;

    @Override
    public byte getType ()
    {
        // TODO Auto-generated method stub
        return SpecialWisdom.CALL;
    }

    @Override
    public void think (Monster _dominator)
    {
        // TODO Auto-generated method stub
        //del by zhengl date: 2011-05-06; note: 客户端已经不再使用该值.统一使用:CLIENT_OF_HIGH_SIDE
        //减少MapSynchronousInfoBroadcast.infoList 的大小,较少for循环次数
    	//暂时未启用该逻辑
//        for (int i = 0; i < monsterModelIDs.length; i++)
//        {
//            String monsterModelID = monsterModelIDs[i];
//            Monster monster = null;
//
//            if (null != monster)
//            {
//                short[] monsterData = monsterDataArray[i];
//                short[][] locationList = new short[9][2];
//                short centerLocationX, centerLocationY;
//
//                if (LOCATION_TYPE_ABSTRACT_OF_MAP == monsterData[MONSTER_DATA_INDEX_OF_LOCATION_TYPE])
//                {
//                    centerLocationX = monsterData[MONSTER_DATA_INDEX_OF_LOCATION_X];
//                    centerLocationY = monsterData[MONSTER_DATA_INDEX_OF_LOCATION_Y];
//                }
//                else
//                {
//                    centerLocationX = (short) (_dominator.getCellX() + monsterData[MONSTER_DATA_INDEX_OF_LOCATION_X]);
//                    centerLocationY = (short) (_dominator.getCellY() + monsterData[MONSTER_DATA_INDEX_OF_LOCATION_Y]);
//                }
//
//                locationList[0][0] = centerLocationX;
//                locationList[0][1] = centerLocationY;
//                locationList[1][0] = (short) (centerLocationX - 1);
//                locationList[1][1] = centerLocationY;
//                locationList[2][0] = (short) (centerLocationX + 1);
//                locationList[2][1] = centerLocationY;
//                locationList[3][0] = centerLocationX;
//                locationList[3][1] = (short) (centerLocationY - 1);
//                locationList[4][0] = centerLocationX;
//                locationList[4][1] = (short) (centerLocationY + 1);
//                locationList[5][0] = (short) (centerLocationX - 1);
//                locationList[5][1] = (short) (centerLocationY - 1);
//                locationList[6][0] = (short) (centerLocationX + 1);
//                locationList[6][1] = (short) (centerLocationY - 1);
//                locationList[7][0] = (short) (centerLocationX - 1);
//                locationList[7][1] = (short) (centerLocationY + 1);
//                locationList[8][0] = (short) (centerLocationX + 1);
//                locationList[8][1] = (short) (centerLocationY + 1);
//
//                for (int number = 0; number < monsterData[MONSTER_DATA_INDEX_OF_NUMBER]; number++)
//                {
//                    monster = NotPlayerServiceImpl.getInstance()
//                            .buildMonsterInstance(monsterModelID);
//
//                    monster.setOrgMap(_dominator.where());
//                    monster.setOrgX(locationList[number][0]);
//                    monster.setOrgY(locationList[number][1]);
//                    monster.setCellX(monster.getOrgX());
//                    monster.setCellY(monster.getOrgY());
//                    monster.setDirection(_dominator.getDirection());
//                    monster.setExistsTime(NotPlayerServiceImpl.getInstance()
//                            .getConfig().ai_call_monster_exist_time);
//                    monster.copyHatredTargetList(_dominator
//                            .getHatredTargetList());
//                    monster.changeInvention(
//                            ENotPlayerIntention.AI_INTENTION_ATTACK, _dominator
//                                    .getAttackTarget());
//                    monster.live(_dominator.where());
//                    MapSynchronousInfoBroadcast.getInstance().put(
//                            Constant.CLIENT_OF_HIGH_SIDE,
//                            _dominator.where(),
//                            new MonsterRefreshNotify(
//                                    Constant.CLIENT_OF_HIGH_SIDE, monster),
//                            false, 0);
//                    MapSynchronousInfoBroadcast.getInstance().put(
//                            Constant.CLIENT_OF_MIDDLE_SIDE,
//                            _dominator.where(),
//                            new MonsterRefreshNotify(
//                                    Constant.CLIENT_OF_MIDDLE_SIDE, monster),
//                            false, 0);
//                    _dominator.where().getMonsterList().add(monster);
//
//                    monster.active();
//                }
//            }
//        }
    }

    /**
     * 怪物数量数据索引
     */
    public static final byte MONSTER_DATA_INDEX_OF_NUMBER        = 0;

    /**
     * 怪物数据定位类型索引
     */
    public static final byte MONSTER_DATA_INDEX_OF_LOCATION_TYPE = 1;

    /**
     * 怪物数据位置X坐标索引
     */
    public static final byte MONSTER_DATA_INDEX_OF_LOCATION_X    = 2;

    /**
     * 怪物数据数量Y坐标索引
     */
    public static final byte MONSTER_DATA_INDEX_OF_LOCATION_Y    = 3;

    /**
     * 地图固定坐标
     */
    public static final byte LOCATION_TYPE_ABSTRACT_OF_MAP       = 1;

    /**
     * 怪物相对坐标
     */
    public static final byte LOCATION_TYPE_MONSTER_SELF          = 2;
}
