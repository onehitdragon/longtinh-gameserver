package hero.share.clienthandler;

import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.EVocation;
import hero.share.RankInfo;
import hero.share.RankMenuField;
import hero.share.message.ResponseRankData;
import hero.share.message.ResponseRankMenu;
import hero.share.service.ShareConfig;
import hero.share.service.ShareServiceImpl;
import org.apache.log4j.Logger;

import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-4-6
 * Time: 下午12:12
 * 排行榜界面操作  0x5d19
 */
public class RequestRank extends AbsClientProcess{
    private static Logger log = Logger.getLogger(RequestRank.class);

    private static final byte OPERATE_MAIN = 0; //进入界面
    private static final byte OPERATE_MENU = 1;//操作菜单
    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance()
                .getPlayerBySessionID(contextData.sessionID);

        byte type = yis.readByte();
        if(type == OPERATE_MAIN){
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new ResponseRankMenu(ShareServiceImpl.getInstance().getRankTypeMap()));
        }else {
            byte menuLevel = yis.readByte();//菜单等级
            byte menuID = yis.readByte();//主菜单ID
            log.debug("operate rank menu level="+menuLevel+",menuid="+menuID);
            RankMenuField rmf = ShareServiceImpl.getInstance().getRankTypeMap().get(menuID);

            if(menuLevel == 1 || menuLevel == 2){ //主菜单查看排行榜，或第二级菜单全职业查看
                List<RankInfo> rankInfoList = ShareServiceImpl.getInstance().getRankInfoList(menuID,0,0,false);
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new ResponseRankData(rankInfoList,rmf.fieldList));
            }else if(menuLevel == 3){
                byte secondMenuID = yis.readByte();
                byte thirdMenuID = yis.readByte();
                log.debug("operate rank second menuID ="+secondMenuID+",third menuid="+thirdMenuID);
                RankMenuField secondMenu = rmf.getChildRankMenuFieldByID(secondMenuID);//获取二级菜单
                if(secondMenu != null){
                    log.debug("second menu name="+secondMenu.name);
                    RankMenuField thirdMenu = secondMenu.getChildRankMenuFieldByID(thirdMenuID);//获取三级菜单
                    if(thirdMenu != null){
                        log.debug("third menu name="+thirdMenu.name+",vocation:"+thirdMenu.name);
                        String vocations = thirdMenu.vocation;
                        log.debug("rank vocation="+vocations);
                        int vocation1=0,vocation2=0;
                        boolean moreVocations = false;
                        if(vocations.indexOf(",")>0){
                            String[] vs = vocations.split(",");
                            vocation1 = Integer.parseInt(vs[0]);
                            vocation2 = Integer.parseInt(vs[1]);
                            moreVocations = true;
                        }else {
                            vocation1 = Integer.parseInt(vocations);
                        }
                        List<RankInfo> rankInfoList = ShareServiceImpl.getInstance().getRankInfoList(menuID,vocation1,vocation2,moreVocations);
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(),new ResponseRankData(rankInfoList,rmf.fieldList));
                    }
                }
            }
        }
    }
}
