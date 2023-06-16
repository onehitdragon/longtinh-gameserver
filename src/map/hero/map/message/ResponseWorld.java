package hero.map.message;

import hero.map.WorldMap;
import hero.map.service.MapServiceImpl;
import hero.map.service.WorldMapDict;

import java.io.IOException;
import java.util.List;

import yoyo.core.packet.AbsResponseMessage;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-3-23
 * Time: 下午4:41
 * 返回大世界信息  (1.神龙界,2.魔龙界,3.仙界)
 * 0x2a20
 */
public class ResponseWorld extends AbsResponseMessage{

    private byte typeID;

    public ResponseWorld(byte typeID) {
        this.typeID = typeID;
    }

    @Override
    protected void write() throws IOException {

        yos.writeByte(typeID); //某界ID

        yos.writeUTF(MapServiceImpl.getInstance().getWorldNameByType((byte)(WorldMapDict.TYPE_WORLD_MAP-1))); //name
        String[] infos =  MapServiceImpl.getInstance().getConfig().world_map_png_anu;
        yos.writeShort(Short.parseShort(infos[0]));//png
        yos.writeShort(Short.parseShort(infos[1]));//anu
        yos.writeShort(Short.parseShort(infos[2]));//宽
        yos.writeShort(Short.parseShort(infos[3]));//高

        List<WorldMap> mapList = WorldMapDict.getInstance().getWorldMapListByType(WorldMapDict.TYPE_WORLD_MAP);
        yos.writeByte(mapList.size());
        for (WorldMap worldMap : mapList){
            yos.writeByte(worldMap.type);
            yos.writeUTF(worldMap.name);
            yos.writeShort(worldMap.cellX);
            yos.writeShort(worldMap.cellY);
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
