package hero.share.message;

import hero.share.RankMenuField;
import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-4-6
 * Time: 下午4:11
 * 0x5d21 返回排行菜单
 */
public class ResponseRankMenu extends AbsResponseMessage{
    private static Logger log = Logger.getLogger(ResponseRankMenu.class);

    private Map<Byte,RankMenuField> rankMainMenuFieldList; //主菜单 杀敌、财富、等级、实力、爱情
//    private Map<Byte,RankMenuField> rankSecondMenuList; //操作按钮里的菜单项及其子菜单项

    public ResponseRankMenu(Map<Byte,RankMenuField> rankMainMenuFieldList) {
        this.rankMainMenuFieldList = rankMainMenuFieldList;
    }

    @Override
    protected void write() throws IOException {
        log.debug("rankMainMenuFieldList size="+rankMainMenuFieldList.size());
        yos.writeByte(rankMainMenuFieldList.size()); //主菜单数量
        for (RankMenuField rmf : rankMainMenuFieldList.values()){
            yos.writeByte(rmf.id);
            yos.writeUTF(rmf.name);
            yos.writeByte(rmf.menuLevel);
            log.debug("main id="+rmf.id+",name="+rmf.name+",level="+rmf.menuLevel);
            /*output.writeByte(rmf.fieldList.size());//显示数据界面标签数量
            for (String field : rmf.fieldList){
                output.writeUTF(field); //标签名称  杀敌、财富、等级、实力、爱情
            }*/

            boolean hasSecondMenu = rmf.childMenuList != null && rmf.childMenuList.size()>0;
            yos.writeByte(hasSecondMenu); //是否有二级菜单
            if(hasSecondMenu){
                log.debug("Second MenuList size="+ rmf.childMenuList.size());
                yos.writeByte(rmf.childMenuList.size());//操作按钮里的菜单项
                for (RankMenuField srmf : rmf.childMenuList){
                    yos.writeByte(srmf.id);
                    yos.writeUTF(srmf.name);
                    yos.writeByte(srmf.menuLevel);
                    boolean hasChildMenu = srmf.childMenuList != null && srmf.childMenuList.size()>0;
                    yos.writeByte(hasChildMenu); //是否有三级菜单
                    if(hasChildMenu){
                        log.debug("third MenuList size="+srmf.childMenuList.size());
                        yos.writeByte(srmf.childMenuList.size());
                        for (RankMenuField crmf : srmf.childMenuList){
                            yos.writeByte(crmf.id);
                            yos.writeUTF(crmf.name);
                            yos.writeByte(crmf.menuLevel);
                        }
                    }
                }
            }
        }


    }

    @Override
    public int getPriority() {
        return 0;
    }
}
