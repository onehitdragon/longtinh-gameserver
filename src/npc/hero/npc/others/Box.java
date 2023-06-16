package hero.npc.others;

import hero.item.Goods;
import hero.item.service.GoodsServiceImpl;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.npc.dict.BoxDataDict;
import hero.npc.dict.BoxDataDict.BoxData;
import hero.npc.dict.PickType;
import hero.npc.message.BoxDisappearNofity;
import hero.npc.message.BoxRefreshNofity;
import hero.share.service.LogWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Box.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-11 下午07:02:33
 * @描述 ：箱子，每次出现时随机产生物品
 */

public class Box extends ME2OtherGameObject
{
    /**
     * 刷新间隔时间
     */
    private int                     rebirthInterval;

    /**
     * 实际掉落物品列表(物品：数量)
     */
    private HashMap<Goods, Integer> actualGoodsTable;

    /**
     * 随机刷新位置列表
     */
    private ArrayList<short[]>      randomLocationList;

    /**
     * 最后一次消失时间
     */
    private long                    lastDisappearTime;

    /**
     * 随机数生成器
     */
    private static final Random     random = new Random();

    private static boolean needGatherSkill = false;

    /**
     * 构造
     * 
     * @param _data
     */
    public Box(BoxData _data)
    {
        super(_data.modelID);
        randomLocationList = new ArrayList<short[]>();
        rebirthInterval = _data.rebirthInterval;
        actualGoodsTable = new HashMap<Goods, Integer>();
        if(_data.pickType == PickType.GATHER){
            needGatherSkill = true;
        }
    }

    /**
     * 是否需要采集技能
     * @return
     */
    public boolean isNeedGatherSkill(){
        return needGatherSkill;
    }

    /**
     * 生成实际掉落物品列表
     */
    private boolean buildActualGoodsTable ()
    {
        actualGoodsTable.clear();

        BoxData data = BoxDataDict.getInstance().getBoxData(getModelID());
        int[][] goodsInfos = data.fixedGoodsInfos;
        Goods goods;

        int randomIndex;
        int goodsNumber = random.nextInt(data.fixedGoodsTypeNumsPerTimes) + 1;

        for (int i = 0; i < goodsNumber;)
        {
            randomIndex = random.nextInt(goodsInfos.length);
            goods = GoodsServiceImpl.getInstance().getGoodsByID(
                    goodsInfos[randomIndex][0]);

            if (null == goods)
            {
                LogWriter.println("宝箱中存在错误的物品编号：" + goodsInfos[randomIndex][0]);

                break;
            }

            if (!actualGoodsTable.containsKey(goods))
            {
                actualGoodsTable.put(goods, random
                        .nextInt(goodsInfos[randomIndex][1]) + 1);

                i++;
            }
        }

        goodsInfos = data.randomGoodsInfos;

        if (null != data.randomGoodsInfos
                && data.randomGoodsTypeNumsPerTimes > 0)
        {
            goodsNumber = random.nextInt(data.randomGoodsTypeNumsPerTimes) + 1;

            for (int i = 0; i < goodsNumber;)
            {
                randomIndex = random.nextInt(goodsInfos.length);

                if (random.nextInt(10000) <= goodsInfos[randomIndex][1])
                {
                    goods = GoodsServiceImpl.getInstance().getGoodsByID(
                            goodsInfos[randomIndex][0]);

                    if (null == goods)
                    {
                        LogWriter.println("宝箱中存在错误的物品编号："
                                + goodsInfos[randomIndex][0]);

                        break;
                    }

                    if (!actualGoodsTable.containsKey(goods))
                    {
                        actualGoodsTable.put(goods, random
                                .nextInt(goodsInfos[randomIndex][2]) + 1);

                        i++;
                    }
                    else
                    {
                        continue;
                    }
                }
                else
                {
                    i++;
                }
            }
        }

        if (actualGoodsTable.size() > 0)
        {
            return true;
        }

        return false;
    }

    /**
     * 刷新
     */
    public void rebirth (boolean _isBuild)
    {
        if (buildActualGoodsTable())
        {
            int location = random.nextInt(randomLocationList.size());

            setCellX(randomLocationList.get(location)[0]);
            setCellY(randomLocationList.get(location)[1]);

            if (!_isBuild)
            {
                MapSynchronousInfoBroadcast.getInstance().put(where,
                        new BoxRefreshNofity(getID(), getCellX(), getCellY()),
                        false, 0);
            }
        }
    }

    /**
     * 消失
     */
    public void disappear ()
    {
        lastDisappearTime = System.currentTimeMillis();

        if (where.getPlayerList().size() > 0)
        {
            MapSynchronousInfoBroadcast.getInstance().put(where,
                    new BoxDisappearNofity(getID(), getCellY()), false, 0);
        }
    }

    /**
     * 添加随机位置
     * 
     * @param _x
     * @param _y
     */
    public void addRandomLocation (short _x, short _y)
    {
        randomLocationList.add(new short[]{_x, _y });
    }

    /**
     * 获取消失时间
     * 
     * @return
     */
    public long getDisappearTime ()
    {
        return lastDisappearTime;
    }

    /**
     * 获取刷新间隔时间
     * 
     * @return
     */
    public int getRebirthInterval ()
    {
        return rebirthInterval;
    }

    /**
     * 获取宝箱实际内喊物品
     * 
     * @return
     */
    public HashMap<Goods, Integer> getActualGoodsTable ()
    {
        return actualGoodsTable;
    }
}
