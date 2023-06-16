package hero.map.message;

import hero.map.WorldMap;
import hero.share.service.Tip;

import java.io.IOException;
import java.util.List;

import yoyo.core.packet.AbsResponseMessage;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-3-23
 * Time: 下午3:39
 * 各界地图显示
 * 0x2a17
 */
public class ResponseWorldMaps extends AbsResponseMessage{

    private List<WorldMap> worldMapList;
    private WorldMap maxWorldMap;
    private byte flag;//是否有世界地图数据  1:有   0:没有
    private String tip;

    public ResponseWorldMaps(List<WorldMap> worldMapList, WorldMap maxWorldMap,byte flag,String tip) {
        this.worldMapList = worldMapList;
        this.maxWorldMap = maxWorldMap;
        this.flag = flag;
        this.tip = tip;
    }

    @Override
    protected void write() throws IOException {
        yos.writeByte(flag);
        if(flag == 0){
//            output.writeUTF(Tip.TIP_NO_WORLD_MAPS);
            yos.writeUTF(tip);
        }
        if(flag == 1){
            yos.writeUTF(maxWorldMap.name);
            yos.writeByte(maxWorldMap.type);
            yos.writeShort(maxWorldMap.png);
            yos.writeShort(maxWorldMap.anu);
            yos.writeShort(maxWorldMap.width);
            yos.writeShort(maxWorldMap.height);

            if(worldMapList != null){
                yos.writeByte(worldMapList.size());

                for(WorldMap worldMap : worldMapList){
                    yos.writeShort(worldMap.cellX);
                    yos.writeShort(worldMap.cellY);
                    yos.writeShort(worldMap.mapID);
                    yos.writeUTF(worldMap.name);
                    yos.writeShort(worldMap.bornX);
                    yos.writeShort(worldMap.bornY);
                }
            }else {
                yos.writeByte(0);
            }
        }

    }

    @Override
    public int getPriority() {
        return 0;
    }
}
