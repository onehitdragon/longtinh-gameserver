package hero.map;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-3-23
 * Time: 上午11:13
 * 世界地图类
 */
public class WorldMap {
    public short mapID;
    public short cellX;
    public short cellY;
    public String name;
    public String desc;

    /**
     * 所包含的副本地图的入口地图ID
     */
    public short dungeonEntryMapID;

    /**
     * 1:神龙界
     * 2:魔龙界
     * 3:仙界
     *
     */
    public byte type;

    //下面的PNG、ANU、长、宽只用在显示大世界时
    /**
     * PNG图片ID
     */
    public short png;
    /**
     * ANU图片ID
     */
    public short anu;

    /**
     * 各世界的长宽
     */
    public short width;
    public short height;

    /**
     * 地图出生点
     */
    public short bornX;
    public short bornY;

}
