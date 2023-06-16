package hero.player.message;

import hero.guild.service.GuildServiceImpl;
import hero.micro.service.MicroServiceImpl;
import hero.player.HeroPlayer;
import hero.share.EVocationType;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponsePlayerBaseInfo.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-7-3 下午05:02:31
 * @描述 ：在社交等列表查看玩家信息
 */

public class ResponsePlayerBaseInfo extends AbsResponseMessage
{
    /**
     * 查看的玩家
     */
    private HeroPlayer player;

    /**
     * @param _player
     */
    public ResponsePlayerBaseInfo(HeroPlayer _player)
    {
        player = _player;
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
        if (null == player)
        {
            yos.writeUTF("");
        }
        else
        {
            StringBuffer sb = new StringBuffer("昵称：");
            sb.append(player.getName());
            sb.append("\n");
            
            sb.append("职业：");
            sb.append(player.getVocation().getDesc());
            sb.append("\n");
            
            sb.append("等级：");
            sb.append(player.getLevel());
            sb.append("\n");
            
            sb.append("性别：");
            sb.append(player.getSex().getDesc());
            sb.append("\n");
            
            sb.append("种族：");
            sb.append(player.getClan().getDesc());
            sb.append("\n");
            
//            sb.append("称号：");
//            sb.append(player.getVocation().getDesc());
//            sb.append("\n");
            
            sb.append("帮派：");
            sb.append( GuildServiceImpl.getInstance().getGuildName(player) );
            sb.append("\n");
            
            sb.append("帮派职务：");
            sb.append( GuildServiceImpl.getInstance().getMemberRank(player) );
            sb.append("\n");
            
            sb.append("配偶：");
            sb.append( player.spouse );
            sb.append("\n");
            
            sb.append("师傅：");
            sb.append(MicroServiceImpl.getInstance().getMasterName(player).length()>0?MicroServiceImpl.getInstance().getMasterName(player):"无");
            sb.append("\n");
            
            sb.append("徒弟：");
            sb.append( MicroServiceImpl.getInstance().getApprenticeNameList(player).length()>0?MicroServiceImpl.getInstance().getApprenticeNameList(player):"无" );
            sb.append("\n");

//            if (player.getVocation().getType() == EVocationType.MAGIC)
//            {
//                sb.append("\n魔法值：");
//                sb.append(player.getMp());
//                sb.append("/");
//                sb.append(player.getActualProperty().getMpMax());
//            }
//            else
//            {
//                sb.append("\n力气值：");
//                sb.append(player.getForceQuantity());
//                sb.append("|");
//                sb.append(player.getGasQuantity());
//            }
//
//            sb.append("\n所在地：");
//            sb.append(player.where().getName());

            yos.writeUTF(sb.toString());
        }
    }
}
