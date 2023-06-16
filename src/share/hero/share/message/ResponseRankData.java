package hero.share.message;

import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.RankInfo;

import java.io.IOException;
import java.util.List;

import yoyo.core.packet.AbsResponseMessage;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-4-6
 * Time: 下午1:15
 * 0x5d20 返回排行标签和对应数据
 */
public class ResponseRankData extends AbsResponseMessage{
    private List<RankInfo> rankInfoList; //排行数据
    private List<String> fieldList; //标签名称

    public ResponseRankData(List<RankInfo> rankInfoList, List<String> fieldList) {
        this.rankInfoList = rankInfoList;
        this.fieldList = fieldList;
    }

    @Override
    protected void write() throws IOException {
        yos.writeByte(fieldList.size());
        for(String field : fieldList){
            yos.writeUTF(field);
        }
        yos.writeByte(rankInfoList.size());
        for (RankInfo info : rankInfoList){
            yos.writeByte(info.rank);
            yos.writeUTF(info.name);
            yos.writeUTF(info.vocation);
            yos.writeInt(info.value);
            yos.writeInt(info.userID);
            HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(info.userID);
            if(player !=null && player.isEnable()){
                yos.writeByte(1); //在线
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
