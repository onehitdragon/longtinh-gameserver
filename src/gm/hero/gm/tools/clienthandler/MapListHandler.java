//package hero.gm.tools.clienthandler;
//
//import hero.map.Area;
//import hero.map.Map;
//import hero.map.service.AreaDict;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Set;
//import java.util.Map.Entry;
//
//import javolution.util.FastList;
//import cn.digifun.gamemanager.Broadcast;
//import cn.digifun.gamemanager.Handler;
//import cn.digifun.gamemanager.core.GMProtocolID;
//import cn.digifun.gamemanager.message.MapListResponse;
//
///**
// * <p>
// * Copyright: DGFun CO., (c) 2010
// * </p>
// * 
// * @文件 MapListHandler.java
// * @创建者 ChenYaMeng
// * @版本 1.0
// * @时间 2010-05-27 下午15:03:56
// * @描述 ：处理上行报文ID(short):110(请求地图列表)
// */
//public class MapListHandler extends Handler
//{
//
//    public MapListHandler()
//    {
//        super(GMProtocolID.REQUEST_MAP_LIST);
//    }
//
//    @Override
//    protected Handler newInstance ()
//    {
//        return new MapListHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int _sid = messageDataInputStream.readInt();
//
//        List<cn.digifun.gamemanager.core.Map> maps = new ArrayList<cn.digifun.gamemanager.core.Map>();
//
//        Set<Entry<Integer, Area>> set = AreaDict.getInstance().getAreaSet();
//        Iterator<Entry<Integer, Area>> iterator = set.iterator();
//        short areaNum = (short) set.size();
//
//        while (iterator.hasNext())
//        {
//            areaNum++;
//            Area area = iterator.next().getValue();
//            FastList<Map> maplist = area.getVisibleMapList();
//
//            for (int i = 0; i < maplist.size(); i++)
//            {
//                Map map = maplist.get(i);
//                cn.digifun.gamemanager.core.Map m = new cn.digifun.gamemanager.core.Map(
//                        map.getID(), map.getName(), map.getArea().getName());;
//                maps.add(m);
//            }
//        }
//
//        Broadcast.getInstance().send(this.message.getSessionID(),
//                new MapListResponse(_sid, maps));
//    }
//
//}
