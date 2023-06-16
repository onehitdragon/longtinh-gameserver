package hero.map.message;

import hero.map.Map;
import hero.npc.others.Animal;
import hero.player.HeroPlayer;

import java.io.IOException;
import java.util.ArrayList;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseAnimalInfoList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-5-12 下午04:48:15
 * @描述 ：
 */

public class ResponseAnimalInfoList extends AbsResponseMessage
{
    /**
     * 地图
     */
    private Map map;

    /**
     * 地图上的小动物
     * 
     * @param _map
     */
    public ResponseAnimalInfoList(Map _map)
    {
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
        ArrayList<Animal> animalList = map.getAnimalList();

        yos.writeByte(animalList.size());

        if (animalList.size() > 0)
        {
            for (Animal animal : animalList)
            {
                yos.writeInt(animal.getID());
                yos.writeByte(animal.getCellX());
                yos.writeByte(animal.getCellY());
                yos.writeShort(animal.getImageID());
                yos.writeShort(animal.getAnimationID());
            }
        }
    }
}
