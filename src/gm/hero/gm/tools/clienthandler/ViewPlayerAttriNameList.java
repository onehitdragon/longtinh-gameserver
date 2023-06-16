//package hero.gm.tools.clienthandler;
//
//import hero.player.service.PlayerServiceImpl;
//import hero.share.Constant;
//import hero.share.EVocation;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import cn.digifun.gamemanager.Broadcast;
//import cn.digifun.gamemanager.Handler;
//import cn.digifun.gamemanager.core.GMProtocolID;
//import cn.digifun.gamemanager.core.ListAttribute;
//import cn.digifun.gamemanager.core.NumAttribute;
//import cn.digifun.gamemanager.core.RoleAttribute;
//import cn.digifun.gamemanager.message.SelRoleColumnResponse;
//
///**
// * <p>
// * Copyright: DGFun CO., (c) 2008
// * </p>
// * 
// * @文件 ViewPlayerAttriNameList.java
// * @创建者 ChenYaMeng
// * @版本 1.0
// * @时间 2010-6-29 上午10:21:19
// * @描述 ：角色属性字段列表
// */
//
//public class ViewPlayerAttriNameList extends Handler
//{
//
//    public ViewPlayerAttriNameList()
//    {
//        super(GMProtocolID.REQUEST_SEL_ROLE_COLUMN);
//    }
//
//    @Override
//    protected ViewPlayerAttriNameList newInstance ()
//    {
//        return new ViewPlayerAttriNameList();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int sid = messageDataInputStream.readInt();
//
//        List<RoleAttribute> list = new ArrayList<RoleAttribute>();
//
//        // 级别
//        RoleAttribute ra = new NumAttribute("级别", 1, PlayerServiceImpl
//                .getInstance().getConfig().max_level);
//        list.add(ra);
//
//        // 职业
//        ra = new ListAttribute("职业", EVocation.getVocationDescList());
//        list.add(ra);
//
//        // 金钱
//        ra = new NumAttribute("金钱", 0, Constant.INTEGER_MAX_VALUE);
//        list.add(ra);
//
//        // 经验
//        ra = new NumAttribute("经验", 0, Constant.INTEGER_MAX_VALUE);
//        list.add(ra);
//
//        Broadcast.getInstance().send(message.getSessionID(),
//                new SelRoleColumnResponse(sid, list));
//    }
//}
