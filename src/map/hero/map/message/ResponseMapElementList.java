package hero.map.message;

import hero.map.Map;
import hero.npc.dict.NpcImageConfDict;
import hero.npc.dict.NpcImageDict;
import hero.npc.dict.NpcImageConfDict.Config;
import hero.npc.others.DoorPlate;
import hero.npc.others.GroundTaskGoods;
import hero.npc.others.RoadInstructPlate;
import hero.npc.others.TaskGear;
import hero.share.Constant;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseMapElementList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-8-4 下午04:36:45
 * @描述 ：响应地图元素（包括任务机关、地上任务物品、路牌）
 */

public class ResponseMapElementList extends AbsResponseMessage
{
     private static Logger log = Logger.getLogger(ResponseMapElementList.class);
    /**
     * 地图
     */
    private Map   map;

    /**
     * 客户端类型（高、中、低端）
     */
    private short clientType;

    /**
     * 响应地图元素（包括任务机关、地上任务物品、路牌）
     * 
     * @param _map
     */
    public ResponseMapElementList(short _clientType, Map _map)
    {
        clientType = _clientType;
        map = _map;
    }

    @Override
    public int getPriority ()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        // TODO Auto-generated method stub

        // 路牌信息
        if (null != map.getRoadPlateList())
        {
            ArrayList<RoadInstructPlate> roadPlateList = map.getRoadPlateList();

            yos.writeByte(roadPlateList.size());

            if (roadPlateList.size() > 0)
            {
                for (RoadInstructPlate roadPlate : roadPlateList)
                {
                    yos.writeInt(roadPlate.getID());
                    yos.writeByte(roadPlate.getCellX());
                    yos.writeByte(roadPlate.getCellY());
                    yos.writeUTF(roadPlate.getContent());
                }
            }
        }
        else
        {
            yos.writeByte(0);
        }

        // 室内门牌信息
        if (null != map.getDoorPlateList())
        {
            ArrayList<DoorPlate> doorPlateList = map.getDoorPlateList();

            yos.writeByte(doorPlateList.size());

            if (doorPlateList.size() > 0)
            {
                for (DoorPlate doorPlate : doorPlateList)
                {
                    yos.writeInt(doorPlate.getID());
                    yos.writeByte(doorPlate.getCellX());
                    yos.writeByte(doorPlate.getCellY());
                    yos.writeUTF(doorPlate.getTip());
                }
            }
        }
        else
        {
            yos.writeByte(0);
        }

        // 任务机关
        if (null != map.taskGearImageIDList)
        {
            yos.writeByte(map.taskGearImageIDList.size());
            log.info("map.taskGearImageIDList.size()---->" + map.taskGearImageIDList.size());
            Config npcConfig;
            byte[] imageBytes;

            for (short imageID : map.taskGearImageIDList)
            {
                imageBytes = NpcImageDict.getInstance().getImageBytes(imageID);
                npcConfig = NpcImageConfDict.get(imageID);
                //edit by zheng; date: 2011-02-22; note: 删除多余数据
//                output.writeShort(imageID);
                //edit by zhengl; date: 2011-01-09; note: 适应客户端而修改
                yos.writeShort(imageID);
                yos.writeShort(npcConfig.animationID);
                //end
                //edit by zheng; date: 2011-02-22; note: 添加必要数据
                yos.writeByte(npcConfig.npcGrid);
                yos.writeShort(npcConfig.npcHeight);
                yos.writeByte(npcConfig.shadowSize);
                //end

                if (Constant.CLIENT_OF_HIGH_SIDE != clientType)
                {
                    yos.writeShort(imageBytes.length);
                    yos.writeBytes(imageBytes);
                }
            }

            yos.writeByte(map.getTaskGearList().size());

            for (TaskGear gear : map.getTaskGearList())
            {
                yos.writeInt(gear.getID());
                yos.writeUTF(gear.getName());
                yos.writeUTF(gear.getDesc());
                yos.writeUTF(gear.getOptionDesc());
                yos.writeByte(gear.getCellX());
                yos.writeByte(gear.getCellY());
                yos.writeShort(gear.getImageID());
            }
        }
        else
        {
            yos.writeByte(0);
        }

        // 地上任务物品
        if (null != map.groundTaskGoodsImageIDList)
        {
            yos.writeByte(map.groundTaskGoodsImageIDList.size());
            Config npcConfig;
            byte[] imageBytes;

            for (short imageID : map.groundTaskGoodsImageIDList)
            {
                imageBytes = NpcImageDict.getInstance().getImageBytes(imageID);
                npcConfig = NpcImageConfDict.get(imageID);
                //edit by zheng; date: 2011-02-22; note: 删除多余数据
//                output.writeShort(imageID);
                //edit by zhengl; date: 2011-01-09; note: 适应客户端而修改
                yos.writeShort(imageID);
                yos.writeShort(npcConfig.animationID);
                //edit by zheng; date: 2011-02-22; note: 添加必要数据
                yos.writeByte(npcConfig.npcGrid);
                yos.writeShort(npcConfig.npcHeight);
                yos.writeByte(npcConfig.shadowSize);
                //end

                if (Constant.CLIENT_OF_HIGH_SIDE != clientType)
                {
                    yos.writeShort(imageBytes.length);
                    yos.writeBytes(imageBytes);
                }
            }

            yos.writeByte(map.getGroundTaskGoodsList().size());

            for (GroundTaskGoods taskGoods : map.getGroundTaskGoodsList())
            {
                yos.writeInt(taskGoods.getID());
                yos.writeUTF(taskGoods.getName());
                yos.writeByte(taskGoods.getCellX());
                yos.writeByte(taskGoods.getCellY());
                yos.writeShort(taskGoods.getImageID());
            }
        }
        else
        {
            yos.writeByte(0);
        }
    }
}
