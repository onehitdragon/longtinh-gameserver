package hero.lover.message;

import hero.guild.service.GuildServiceImpl;
import hero.item.Armor;
import hero.item.EquipmentInstance;
import hero.item.Weapon;
import hero.item.service.GoodsDAO;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 11-2-11
 * Time: 上午10:59
 * 伴侣关系界面信息
 * 0x5a02
 */
public class ResponseMarryRelationShow extends AbsResponseMessage{
    private byte relation;  // 0:无伴侣  1:有恋人关系  2:有夫妻关系
    private HeroPlayer player;

    public ResponseMarryRelationShow(byte relation, HeroPlayer _player) {
        this.relation = relation;
        this.player = _player;
    }

    @Override
    protected void write() throws IOException {
        yos.writeByte(relation);
        if(relation > 0){
            yos.writeUTF(player.getName());
            yos.writeShort(player.getLevel());
            yos.writeByte(player.getSex().value());
            yos.writeByte(player.getClan().getID());
            yos.writeByte(player.getVocation().value());
            yos.writeUTF(GuildServiceImpl.getInstance().getGuildName(player));
            yos.writeInt(player.getLoverValue());
            yos.writeInt(player.loverDays(player));
            yos.writeInt(PlayerServiceImpl.getInstance().getPlayerLoverValueOrder(player.getUserID()));
            if(relation == 2)
                yos.writeUTF(player.loverLever.getName());
            else yos.writeUTF("恋人");
            
            if(!player.isEnable()){
            	GoodsDAO.loadPlayerWearGoods(player);
            }
            
         // 衣服
            EquipmentInstance ei = player.getBodyWear().getBosom();

            if (null != ei)
            {
            	//add by zhengl; date: 2011-02-15; note: 客户端需要此值来做分等级展示图片以节约客户端内存
            	yos.writeShort(ei.getArchetype().getNeedLevel());
                yos.writeShort(ei.getArchetype().getImageID());
                //add by zhengl ; date: 2011-01-17 ; note: 添加动画ID下发
                yos.writeShort(ei.getArchetype().getAnimationID());
                //add by zhengl ; date: 2010-11-25 ; note: 其他玩家的服装是否分种族展示
                yos.writeByte( ((Armor)ei.getArchetype()).getDistinguish() );
            }
            else
            {
            	//add by zhengl; date: 2011-02-15; note: 客户端需要此值来做分等级展示图片以节约客户端内存
            	yos.writeShort(0);
                yos.writeShort( PlayerServiceImpl.getInstance().getConfig()
                        .getDefaultClothesImageID(player.getSex()) );
                yos.writeShort( PlayerServiceImpl.getInstance().getConfig()
                        .getDefaultClothesAnimation(player.getSex()) );
                yos.writeByte(0);
            }
            //头盔
            ei = player.getBodyWear().getHead();
            if (null != ei)
            {
                yos.writeShort(ei.getArchetype().getImageID());
                //add by zhengl ; date: 2011-01-17 ; note: 添加动画ID下发
                yos.writeShort(ei.getArchetype().getAnimationID());
                //add by zhengl ; date: 2010-11-25 ; note: 其他玩家的头盔是否分种族展示
                yos.writeByte( ((Armor)ei.getArchetype()).getDistinguish() );
            }
            else
            {
                yos.writeShort(-1);
                yos.writeShort(-1);
                yos.writeByte(0);
            }

            // 武器信息
            ei = player.getBodyWear().getWeapon();

            if (null != ei)
            {
            	//edit by zhengl; date: 2011-02-21; note: 武器读取添加标记位
            	yos.writeByte(1);
            	//add by zhengl; date: 2011-02-15; note: 客户端需要此值来做分等级展示图片以节约客户端内存
            	yos.writeShort(ei.getArchetype().getNeedLevel());
                yos.writeShort(((Weapon) ei.getArchetype()).getImageID());
                //add by zhengl ; date: 2011-01-17 ; note: 添加动画ID下发
                yos.writeShort(((Weapon) ei.getArchetype()).getAnimationID());
                yos.writeByte(ei.getGeneralEnhance().getLevel());
                //add by zhengl ; date: 2010-11-25 ; note: 其他玩家武器攻击特效
                yos.writeShort( ((Weapon) ei.getArchetype()).getLightID() );
                //add by zhengl ; date: 2011-01-17 ; note: 添加动画ID下发
                yos.writeShort( ((Weapon) ei.getArchetype()).getLightAnimation() );
                yos.writeShort( ((Weapon) ei.getArchetype()).getWeaponType().getID() );
            }
            else
            {
            	//edit by zhengl; date: 2011-02-21; note: 武器读取添加标记位
            	yos.writeByte(0);
//                output.writeShort((short) -1);
            }
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
