/**
 * Copyright: Copyright (c) 2007
 * <br>
 * Company: Digifun
 * <br>
 */

package hero.map.message;

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
import hero.share.EVocationType;
import hero.skill.PetActiveSkill;
import hero.skill.PetPassiveSkill;
import hero.skill.service.SkillServiceImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import yoyo.core.packet.AbsResponseMessage;


/**
 * Description:通知玩家出现<br>
 * 
 * @Author Insunny
 * @CreateDate 2008-11-5
 */
public class PlayerRefreshNotify extends AbsResponseMessage
{
    private HeroPlayer emerger;

    /**
     * @param emerger
     * @param canBeAttack
     */
    public PlayerRefreshNotify(HeroPlayer emerger)
    {
        this.emerger = emerger;
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
     * 
     * @see me2.core.data.ResultMessage#write()
     */
    @Override
    protected void write () throws IOException
    {
        yos.writeInt(emerger.getID());
        yos.writeUTF(emerger.getName());
        yos.writeShort(emerger.getLevel());
        yos.writeByte(emerger.getSex().value());
        yos.writeByte(emerger.getClan().getID());
        yos.writeUTF(emerger.spouse);

        Guild guild = GuildServiceImpl.getInstance().getGuild(
                emerger.getGuildID());
        if (guild != null)
        {
            yos.writeUTF(guild.getName());
            yos.writeUTF(GuildServiceImpl.getInstance().getMemberRank(emerger));
        }
        else
        {
            yos.writeUTF("");
            yos.writeUTF("");
        }

        MasterApprentice masterApprentice = TeachService.get(emerger.getUserID());

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

        yos.writeByte(emerger.getVocation().value());
        yos.writeInt(emerger.getHp());
        yos.writeInt(emerger.getActualProperty().getHpMax());
        yos.writeByte(emerger.getVocation().getType().getID());

        if (emerger.getVocation().getType() == EVocationType.PHYSICS)
        {
            yos.writeInt(emerger.getForceQuantity());
            yos.writeInt(100);
        }
        else
        {
            yos.writeInt(emerger.getMp());
            yos.writeInt(emerger.getActualProperty().getMpMax());
        }

        yos.writeByte(emerger.getCellX());
        yos.writeByte(emerger.getCellY());
        yos.writeByte(emerger.getDirection());

        // 衣服
        EquipmentInstance ei = emerger.getBodyWear().getBosom();

        if (null != ei)
        {
        	//add by zhengl; date: 2011-02-15; note: 客户端需要此值来做分等级展示图片以节约客户端内存
        	yos.writeShort(ei.getArchetype().getNeedLevel());
            yos.writeShort(ei.getArchetype().getImageID());
            //add by zhengl ; date: 2011-01-17 ; note: 添加动画ID下发
            yos.writeShort(ei.getArchetype().getAnimationID());
            //add by zhengl ; date: 2010-11-25 ; note: 其他玩家的服装是否分种族展示
            yos.writeByte( ((Armor)ei.getArchetype()).getDistinguish() );
            //add by zhengl; date: 2011-03-25; note: 添加强化光效
            yos.writeShort(ei.getGeneralEnhance().getArmorFlashView()[0]);
            yos.writeShort(ei.getGeneralEnhance().getArmorFlashView()[1]);
        }
        else
        {
        	//add by zhengl; date: 2011-02-15; note: 客户端需要此值来做分等级展示图片以节约客户端内存
        	yos.writeShort(0);
            yos.writeShort( PlayerServiceImpl.getInstance().getConfig()
                    .getDefaultClothesImageID(emerger.getSex()) );
            yos.writeShort( PlayerServiceImpl.getInstance().getConfig()
                    .getDefaultClothesAnimation(emerger.getSex()) );
            yos.writeByte(0);
            yos.writeShort(-1);
            yos.writeShort(-1);
        }
        //头盔
        ei = emerger.getBodyWear().getHead();
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
        ei = emerger.getBodyWear().getWeapon();

        if (null != ei)
        {
        	//edit by zhengl; date: 2011-02-21; note: 武器读取添加标记位
        	yos.writeByte(1);
        	//add by zhengl; date: 2011-02-15; note: 客户端需要此值来做分等级展示图片以节约客户端内存
        	yos.writeShort(ei.getArchetype().getNeedLevel());
            yos.writeShort(((Weapon) ei.getArchetype()).getImageID());
            //add by zhengl ; date: 2011-01-17 ; note: 添加动画ID下发
            yos.writeShort(((Weapon) ei.getArchetype()).getAnimationID());
//            output.writeByte(ei.getGeneralEnhance().getLevel());
            //add by zhengl ; date: 2010-11-25 ; note: 其他玩家武器攻击特效
            yos.writeShort( ((Weapon) ei.getArchetype()).getLightID() );
            //add by zhengl ; date: 2011-01-17 ; note: 添加动画ID下发
            yos.writeShort( ((Weapon) ei.getArchetype()).getLightAnimation() );
            yos.writeShort( ((Weapon) ei.getArchetype()).getWeaponType().getID() );
            //add by zhengl; date: 2011-03-25; note: 添加武器强化光效
            yos.writeShort(ei.getGeneralEnhance().getFlashView()[0]);
            yos.writeShort(ei.getGeneralEnhance().getFlashView()[1]);
        }
        else
        {
        	//edit by zhengl; date: 2011-02-21; note: 武器读取添加标记位
        	yos.writeByte(0);
//            output.writeShort((short) -1);
        }

        yos.writeByte(emerger.isVisible());
        
        HashMap<Integer,Pet> viewPetMap = PetServiceImpl.getInstance().getViewPetList(emerger.getUserID());
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
            	
            	PetServiceImpl.getInstance().writePetSkillID(pet, yos);
            	if(pet.pk.getStage() == Pet.PET_STAGE_ADULT && pet.pk.getType() == Pet.PET_TYPE_CARNIVORE){
            		PetServiceImpl.getInstance().reCalculatePetProperty(pet);
            		SkillServiceImpl.getInstance().petReleasePassiveSkill(pet, 1);
            	}
                MapSynchronousInfoBroadcast.getInstance().put(
                                        emerger.where(),
                                        new PetChangeNotify(emerger.getID(),
                                                OperatePet.SHOW, pet.imageID,pet.pk.getType()), true,
                                        emerger.getID());
            }
        }else{
        	yos.writeByte(0);
        }

        yos.writeByte(emerger.isSelling());

        if (emerger.isSelling())
        {
            yos.writeUTF((MicroServiceImpl.getInstance().getStore(
                    emerger.getUserID()).name));
        }


    }

}
