package hero.player.message;

import java.io.IOException;

import hero.player.service.PlayerDAO;
import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;

import hero.guild.service.GuildServiceImpl;
import hero.map.message.ResponseMapGameObjectList;
import hero.micro.service.MicroServiceImpl;
import hero.player.HeroPlayer;
import hero.share.EMagic;
import hero.share.EVocationType;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponsePlayerAttribute.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-17 下午08:56:59
 * @描述 ：
 */

public class RefreshRoleProperty extends AbsResponseMessage
{
	private static Logger log = Logger.getLogger(RefreshRoleProperty.class);
    private static final String PERCENT_CHARACTOR = "%";

    /**
     * 角色
     */
    private HeroPlayer          player;

    /**
     * 构造
     * 
     * @param _player
     */
    public RefreshRoleProperty(HeroPlayer _player)
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
        yos.writeInt(player.getExp());
        yos.writeInt(player.getUpgradeNeedExp());
        /*add by zhengl; date: 2011-04-20; note: 添加展示用*/
        yos.writeInt(player.getExpShow()); 
        yos.writeInt(player.getUpgradeNeedExpShow());
        yos.writeInt(player.getHp());
        yos.writeInt(player.getActualProperty().getHpMax());
        yos.writeInt(player.getMp());
        yos.writeInt(player.getActualProperty().getMpMax());

        yos.writeShort(player.getActualProperty().getStrength());
        yos.writeShort(player.getActualProperty().getAgility());
        yos.writeShort(player.getActualProperty().getStamina());
        yos.writeShort(player.getActualProperty().getInte());
        yos.writeShort(player.getActualProperty().getSpirit());
        yos.writeShort(player.getActualProperty().getLucky());

        yos.writeInt(player.getActualProperty().getMaxPhysicsAttack());
        yos.writeInt(player.getActualProperty().getMinPhysicsAttack());
        
        int magic = (int)player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(
        		EMagic.FIRE);
        magic += (int)player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(
        		EMagic.SANCTITY);
        magic += (int)player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(
        		EMagic.SOIL);
        magic += (int)player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(
        		EMagic.UMBRA);
        magic += (int)player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(
        		EMagic.WATER);
        magic = magic/5;
        magic += (int)player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(
        		EMagic.ALL);
        yos.writeInt(magic); //法术攻击
        yos.writeInt(PlayerDAO.getPlayerFailerNumber(player.getUserID())); //被杀次数
        yos.writeInt(PlayerDAO.getPlayerWinnerNumber(player.getUserID())); //杀敌数量
        yos.writeInt(0); //切磋胜利
        yos.writeInt(0); //切磋次数

        yos.writeInt(player.getActualProperty().getBaseMagicHarmList()
                .getEMagicHarmValue(EMagic.UMBRA));
        yos.writeInt(player.getActualProperty().getBaseMagicHarmList()
                .getEMagicHarmValue(EMagic.SANCTITY));
        yos.writeInt(player.getActualProperty().getBaseMagicHarmList()
                .getEMagicHarmValue(EMagic.FIRE));
        yos.writeInt(player.getActualProperty().getBaseMagicHarmList()
                .getEMagicHarmValue(EMagic.WATER));
        yos.writeInt(player.getActualProperty().getBaseMagicHarmList()
                .getEMagicHarmValue(EMagic.SOIL));

        yos.writeUTF(String.valueOf(player.getActualProperty()
                .getPhysicsDeathblowOdds())
                + PERCENT_CHARACTOR);
        yos.writeUTF(String.valueOf(player.getActualProperty()
                .getMagicDeathblowOdds())
                + PERCENT_CHARACTOR);
        yos.writeUTF(String.valueOf(player.getActualProperty()
                .getPhysicsHitOdds())
                + PERCENT_CHARACTOR);
        yos.writeUTF(String.valueOf(player.getActualProperty()
                .getMagicHitOdds())
                + PERCENT_CHARACTOR);
        yos.writeInt(player.getActualProperty().getDefense());

        yos.writeInt(player.getActualProperty().getMagicFastnessList()
                .getEMagicFastnessValue(EMagic.UMBRA));
        yos.writeInt(player.getActualProperty().getMagicFastnessList()
                .getEMagicFastnessValue(EMagic.SANCTITY));
        yos.writeInt(player.getActualProperty().getMagicFastnessList()
                .getEMagicFastnessValue(EMagic.FIRE));
        yos.writeInt(player.getActualProperty().getMagicFastnessList()
                .getEMagicFastnessValue(EMagic.WATER));
        yos.writeInt(player.getActualProperty().getMagicFastnessList()
                .getEMagicFastnessValue(EMagic.SOIL));
        yos.writeUTF(String.valueOf(player.getActualProperty().getPhysicsDuckOdds())
                + PERCENT_CHARACTOR);
        yos.writeShort(player.getActualAttackImmobilityTime());
        String immobilityTime = String.valueOf(player.getActualAttackImmobilityTime()/1000F);
        yos.writeUTF(immobilityTime);
        yos.writeByte(player.getAttackRange());
        //add by zhengl; date: 2011-02-15; note: 玩家属性添加了 帮会,帮会职务,师傅,徒弟
        String guild = GuildServiceImpl.getInstance().getGuildName(player);
        String memberRank = GuildServiceImpl.getInstance().getMemberRank(player);
        String master = MicroServiceImpl.getInstance().getMasterName(player);
        String app = MicroServiceImpl.getInstance().getApprenticeNameList(player);
        yos.writeUTF(guild);
        yos.writeUTF(memberRank);
        yos.writeUTF(master);
        yos.writeUTF(app);
        yos.writeShort(player.surplusSkillPoint);
        log.info("output size = " + String.valueOf(yos.size()) 
        		+ "player id = " + String.valueOf(player.getUserID()));
    }
}
