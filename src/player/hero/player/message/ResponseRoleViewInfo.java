/**
 * Copyright: Copyright (c) 2007
 * <br>
 * Company: Digifun
 * <br>
 */

package hero.player.message;

import hero.guild.Guild;
import hero.guild.service.GuildServiceImpl;
import hero.item.Armor;
import hero.item.EquipmentInstance;
import hero.item.Weapon;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.micro.service.MicroServiceImpl;
import hero.micro.teach.MasterApprentice;
import hero.micro.teach.TeachService;
import hero.pet.Pet;
import hero.pet.clienthandler.OperatePet;
import hero.pet.message.PetChangeNotify;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import hero.share.EVocationType;
import hero.skill.PetActiveSkill;
import hero.skill.PetPassiveSkill;
import hero.skill.service.SkillServiceImpl;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseRoleViewInfo.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-4-23 上午10:08:18
 * @描述 ：
 */

public class ResponseRoleViewInfo extends AbsResponseMessage
{
    private static Logger log = Logger.getLogger(ResponseRoleViewInfo.class);
    /**
     * 玩家对象
     */
    private HeroPlayer player;

    /**
     * 是否新手
     */
    private boolean    isNovice;

    /**
     * @param picker
     * @param where
     */
    public ResponseRoleViewInfo(HeroPlayer _player, boolean _isNovice)
    {
        player = _player;
        isNovice = _isNovice;
    }
    
    /**
     * <p>构造函数</p>
     * 合理化代码结构
     * @param _player
     * @param _isNovice
     */
    public ResponseRoleViewInfo(HeroPlayer _player)
    {
        player = _player;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me2.core.data.ResultMessage#getPriority()
     */
    @Override
    public int getPriority ()
    {
        return 0;
    }

    /*
     * (non-Javadoc)
     * @see me2.core.data.ResultMessage#write()
     */
    @Override
    protected void write () throws IOException
    {
        yos.writeInt(player.getID());
        yos.writeUTF(player.getName());
        yos.writeShort(player.getLevel());
        yos.writeInt(player.getMoney());
        yos.writeByte(player.getSex().value());
        yos.writeByte(player.getClan().getID());

        Guild tmp = GuildServiceImpl.getInstance()
                .getGuild(player.getGuildID());

        if (tmp != null)
        {
            yos.writeUTF(tmp.getName());
            yos.writeUTF(GuildServiceImpl.getInstance().getMemberRank(player));
        }
        else
        {
            yos.writeUTF("");
            yos.writeUTF("");
        }

        MasterApprentice masterApprentice = TeachService.get(player.getUserID());

        if(masterApprentice != null){
            yos.writeByte(1);//是否有师徒关系 1有 0:没有
            yos.writeUTF(masterApprentice.masterName==null?"":masterApprentice.masterName);
            yos.writeByte(masterApprentice.apprenticeNumber);
            for(int j=0; j<masterApprentice.apprenticeNumber; j++){
                yos.writeUTF(masterApprentice.apprenticeList[j].name==null?"":masterApprentice.apprenticeList[j].name);
            }
        }else {
            yos.writeByte(0);
        }

        yos.writeByte(player.getVocation().value());
        yos.writeInt(player.getHp());
        yos.writeInt(player.getActualProperty().getHpMax());
        yos.writeByte(player.getVocation().getType().getID());

        //edit by zhengl; date: 2011-02
//        if (player.getVocation().getType() == EVocationType.PHYSICS)
//        {
//            output.writeInt(player.getForceQuantity());
//            output.writeInt(player.getForceQuantity() + player.getGasQuantity());
//        }
//        else
//        {
//            output.writeInt(player.getMp());
//            output.writeInt(player.getActualProperty().getMpMax());
//        }
        yos.writeInt(player.getMp());
        yos.writeInt(player.getActualProperty().getMpMax());

        yos.writeInt(player.getExp());
        yos.writeInt(player.getUpgradeNeedExp());
        /*add by zhengl; date: 2011-04-20; note: 添加展示用*/
        yos.writeInt(player.getExpShow()); 
        yos.writeInt(player.getUpgradeNeedExpShow());
        yos.writeShort(player.getActualAttackImmobilityTime());
        yos.writeByte(player.getAttackRange());

        EquipmentInstance ei = player.getBodyWear().getBosom();

        if (null != ei)
        {
            yos.writeShort(ei.getArchetype().getImageID());
            log.debug("1 ei ["+ei.getArchetype().getID()+"] imageID="+ei.getArchetype().getImageID());
            //add by zhengl ; date: 2011-01-17 ; note: 添加动画ID下发
            yos.writeShort(ei.getArchetype().getAnimationID());
            //add by zhengl ; date: 2010-11-25 ; note:是否分种族展示不同效果.
            yos.writeByte( ((Armor)ei.getArchetype()).getDistinguish() );
            
            //edit by zhengl; date: 2011-03-17; note: 发送必要的强化效果
            yos.writeShort(ei.getGeneralEnhance().getArmorFlashView()[0]);
            yos.writeShort(ei.getGeneralEnhance().getArmorFlashView()[1]);
        }
        else
        {
            yos.writeShort(PlayerServiceImpl.getInstance().getConfig()
                    .getDefaultClothesImageID(player.getSex()));
            yos.writeShort(PlayerServiceImpl.getInstance().getConfig()
                    .getDefaultClothesAnimation(player.getSex()));
            yos.writeByte(0);
            //edit by zhengl; date: 2011-03-18; note: 发送必要的强化效果
            yos.writeShort(-1);
            yos.writeShort(-1);
        }
        //头盔
        ei = player.getBodyWear().getHead();
        if (null != ei)
        {
            yos.writeShort(ei.getArchetype().getImageID());
            //add by zhengl ; date: 2011-01-17 ; note: 添加动画ID下发
            yos.writeShort(ei.getArchetype().getAnimationID());
            log.debug("2 ei ["+ei.getArchetype().getID()+"] imageID="+ei.getArchetype().getImageID());
            //add by zhengl ; date: 2010-11-25 ; note: 其他玩家的头盔是否分种族展示
            yos.writeByte( ((Armor)ei.getArchetype()).getDistinguish() );
        }
        else
        {
            yos.writeShort(-1);
            yos.writeShort(-1);
            yos.writeByte(0);
        }

        ei = player.getBodyWear().getWeapon();

        if (null != ei)
        {
        	//edit by zhengl; date: 2011-02-21; note: 武器读取添加标记位
        	yos.writeByte(1);
            yos.writeShort(((Weapon) ei.getArchetype()).getImageID());
            //add by zhengl ; date: 2011-01-17 ; note: 添加动画ID下发
            yos.writeShort(((Weapon) ei.getArchetype()).getAnimationID());
            //edit by zhengl; date: 2011-03-17; note: 删除不必要的强化等级,以及发送必要的强化效果
//            output.writeByte(ei.getGeneralEnhance().getLevel());
            yos.writeShort(ei.getGeneralEnhance().getFlashView()[0]);
            yos.writeShort(ei.getGeneralEnhance().getFlashView()[1]);
            //end
            //add by zhengl ; date: 2010-11-25 ; note:同时下发武器攻击特效.
            yos.writeShort( ((Weapon)ei.getArchetype()).getLightID() );
            //add by zhengl ; date: 2011-01-17 ; note: 添加动画ID下发
            yos.writeShort(((Weapon) ei.getArchetype()).getLightAnimation());
            //edit by zhengl; date: 2011-01-28; note: 下发的值超过了127.
//            output.writeByte( ((Weapon) ei.getArchetype()).getWeaponType().getID() );
            yos.writeShort( ((Weapon) ei.getArchetype()).getWeaponType().getID() );
        }
        else
        {
        	//edit by zhengl; date: 2011-02-21; note: 武器读取添加标记位
        	yos.writeByte(0);
        }

        yos.writeByte(isNovice);
        
        if(!isNovice){
        	//如果不是新手，则显示其已显示的宠物
            HashMap<Integer,Pet> viewPetMap = PetServiceImpl.getInstance().getViewPetList(player.getUserID());
            if(viewPetMap != null && viewPetMap.size() > 0){
                int viewpetnum = viewPetMap.size();
                yos.writeByte(viewpetnum);
                for(Iterator<Pet> it = viewPetMap.values().iterator(); it.hasNext();){
                	Pet pet = it.next();
                	yos.writeByte(pet.isView);
                	yos.writeInt(pet.id);
                	yos.writeShort(pet.imageID);
                	//add by zhengl ; date: 2011-01-17 ; note: 添加动画
                	yos.writeShort(pet.animationID);
                	yos.writeShort(pet.pk.getType());
                	yos.writeShort(pet.fun);
                	if((!pet.isView) && pet.viewStatus==1){
                		PetServiceImpl.getInstance().hatchPet(player, pet);
                	}
                	
                	PetServiceImpl.getInstance().writePetSkillID(pet, yos);
                	
                	if(pet.pk.getStage() == Pet.PET_STAGE_ADULT && pet.pk.getType() == Pet.PET_TYPE_CARNIVORE){
                		PetServiceImpl.getInstance().reCalculatePetProperty(pet);
                		SkillServiceImpl.getInstance().petReleasePassiveSkill(pet, 1);
                	}
                    MapSynchronousInfoBroadcast.getInstance().put(
                                        player.where(),
                                        new PetChangeNotify(player.getID(),
                                                OperatePet.SHOW, pet.imageID,pet.pk.getType()), true,
                                        player.getID());
                }
            }else{
            	yos.writeByte(0);
            }
        }

        yos.writeByte(player.isSelling());
        if (player.isSelling())
        {
            yos.writeUTF((MicroServiceImpl.getInstance().getStore(
                    player.getUserID()).name));
        }
        
        yos.writeUTF(player.spouse);
        //add by zhengl ; date: 2010-11-16 ; note: 技能点下发
        yos.writeShort(player.surplusSkillPoint);
        
        log.info("output size = " + String.valueOf(yos.size()));
    }
}
