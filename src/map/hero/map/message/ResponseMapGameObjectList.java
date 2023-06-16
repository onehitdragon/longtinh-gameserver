package hero.map.message;

import hero.guild.Guild;
import hero.guild.service.GuildServiceImpl;
import hero.item.Armor;
import hero.item.Equipment;
import hero.item.EquipmentInstance;
import hero.item.Weapon;
import hero.item.Weapon.EWeaponType;
import hero.map.Map;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.micro.service.MicroServiceImpl;
import hero.micro.teach.MasterApprentice;
import hero.micro.teach.TeachService;
import hero.npc.Monster;
import hero.npc.Npc;
import hero.npc.dict.MonsterImageConfDict;
import hero.npc.dict.MonsterImageDict;
import hero.npc.dict.NpcImageConfDict;
import hero.npc.dict.NpcImageDict;
import hero.npc.dict.NpcImageConfDict.Config;
import hero.pet.Pet;
import hero.pet.clienthandler.OperatePet;
import hero.pet.message.PetChangeNotify;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.Constant;
import hero.share.EVocationType;
import hero.share.service.ME2ObjectList;
import hero.skill.PetActiveSkill;
import hero.skill.PetPassiveSkill;
import hero.skill.service.SkillServiceImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseMapGameObjectInfoList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-4-23 上午09:42:33
 * @描述 ：响应地图对象信息清单
 */

public class ResponseMapGameObjectList extends AbsResponseMessage
{
     private static Logger log = Logger.getLogger(ResponseMapGameObjectList.class);
    /**
     * 地图
     */
    private Map   map;

    /**
     * 客户端类型（高、中、低端）
     */
    private short clientType;
    
    
    public ResponseMapGameObjectList(short _clientType, Map _map)
    {
        clientType = _clientType;
        map = _map;
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
        ME2ObjectList npcList = map.getNpcList();
        ME2ObjectList monsterList = map.getMonsterList();
        ME2ObjectList otherPlayerList = map.getPlayerList();
        //这个方法太蠢了,下次改. by zhengl 2011-02-28;
        // for 4次
        //职业
        //png
        //anu
//        short[] armors = PlayerServiceImpl.getInstance().getConfig().getArmorViewByLevel(
//        		player.getLevel());
//        output.writeByte(EVocationType.PHYSICS.getID());
//        output.writeShort(armors[0]);
//        output.writeShort(armors[1]);
//        output.writeByte(EVocationType.RANGER.getID());
//        output.writeShort(armors[2]);
//        output.writeShort(armors[3]);
//        output.writeByte(EVocationType.MAGIC.getID());
//        output.writeShort(armors[4]);
//        output.writeShort(armors[5]);
//        output.writeByte(EVocationType.PRIEST.getID());
//        output.writeShort(armors[4]);
//        output.writeShort(armors[5]);
//        
//        //for 5 武器
//        //武器类型
//        //png
//        //anu
//        short[] weapons = PlayerServiceImpl.getInstance().getConfig().getWeaponViewByLevel(
//        		player.getLevel());
//        output.writeByte(EWeaponType.TYPE_JIAN.getID());
//        output.writeShort(weapons[0]);
//        output.writeShort(weapons[1]);
//        output.writeByte(EWeaponType.TYPE_GONG.getID());
//        output.writeShort(weapons[2]);
//        output.writeShort(weapons[3]);
//        output.writeByte(EWeaponType.TTYPE_ZHANG.getID());
//        output.writeShort(weapons[4]);
//        output.writeShort(weapons[5]);
//        output.writeByte(EWeaponType.TYPE_CHUI.getID());
//        output.writeShort(weapons[6]);
//        output.writeShort(weapons[7]);
//        output.writeByte(EWeaponType.TYPE_BISHOU.getID());
//        output.writeShort(weapons[8]);
//        output.writeShort(weapons[9]);

        // 其他玩家数量
        yos.writeShort((short) otherPlayerList.size());
        HeroPlayer otherPlayer = null;
        Npc npc = null;
        Monster monster = null;
        EquipmentInstance ei = null;
        Guild guild = null;

        for (int i = 0; i < otherPlayerList.size(); i++)
        {
            otherPlayer = (HeroPlayer) otherPlayerList.get(i);
            yos.writeInt(otherPlayer.getID());
            yos.writeUTF(otherPlayer.getName());
            yos.writeShort(otherPlayer.getLevel());
            yos.writeByte((otherPlayer.getSex().value()));
            yos.writeByte(otherPlayer.getClan().getID());
            yos.writeUTF(otherPlayer.spouse);

            guild = GuildServiceImpl.getInstance().getGuild(
                    otherPlayer.getGuildID());
            if (guild != null)
            {
                yos.writeUTF(guild.getName());
                yos.writeUTF(GuildServiceImpl.getInstance().getMemberRank(otherPlayer));
            }
            else
            {
                yos.writeUTF("");
                yos.writeUTF("");
            }

            MasterApprentice masterApprentice = TeachService.get(otherPlayer.getUserID());

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

            yos.writeByte(otherPlayer.getVocation().value());
            yos.writeInt(otherPlayer.getHp());
            yos.writeInt(otherPlayer.getActualProperty().getHpMax());
            yos.writeByte((otherPlayer.getVocation().getType().getID()));

            //edit by zhengl; date: 2011-02-13; note: 应策划需求,现在每种职业均使用魔法值作战.
//            if (otherPlayer.getVocation().getType() == EVocationType.PHYSICS)
//            {
//                output.writeInt(otherPlayer.getForceQuantity());
//                output.writeInt(100);
//            }
//            else
//            {
//                output.writeInt(otherPlayer.getMp());
//                output.writeInt(otherPlayer.getActualProperty().getMpMax());
//            }
            yos.writeInt(otherPlayer.getMp());
            yos.writeInt(otherPlayer.getActualProperty().getMpMax());
            //end
            yos.writeByte(otherPlayer.getCellX());
            yos.writeByte(otherPlayer.getCellY());
            yos.writeByte(otherPlayer.getDirection());

            // 衣服
            ei = otherPlayer.getBodyWear().getBosom();
            if (null != ei)
            {
            	//add by zhengl; date: 2011-02-15; note: 客户端需要此值来做分等级展示图片以节约客户端内存
            	yos.writeShort(ei.getArchetype().getNeedLevel());
                yos.writeShort(ei.getArchetype().getImageID());
                //add by zhengl ; date: 2011-01-17 ; note: 同时需要图片ID与动画ID
                yos.writeShort(ei.getArchetype().getAnimationID());
                //add by zhengl ; date: 2010-11-25 ; note: 其他玩家的服装是否分种族展示
                yos.writeByte( ((Armor)ei.getArchetype()).getDistinguish() );
                //add by zhengl; date: 2011-03-25; note: 加衣服光效
                yos.writeShort(ei.getGeneralEnhance().getArmorFlashView()[0]);
                yos.writeShort(ei.getGeneralEnhance().getArmorFlashView()[1]);
            }
            else
            {
            	//add by zhengl; date: 2011-02-15; note: 客户端需要此值来做分等级展示图片以节约客户端内存
            	yos.writeShort(0);
            	//edit by zhengl ; date: 2011-01-17 ; note: 同时需要图片ID与动画ID
                yos.writeShort( PlayerServiceImpl.getInstance().getConfig()
                        .getDefaultClothesImageID(otherPlayer.getSex()) );
                yos.writeShort( PlayerServiceImpl.getInstance().getConfig()
                        .getDefaultClothesAnimation(otherPlayer.getSex()) );
                yos.writeByte(0);
                yos.writeShort(-1);
                yos.writeShort(-1);
            }
            //头盔
            ei = otherPlayer.getBodyWear().getHead();
            if (null != ei)
            {
                yos.writeShort(ei.getArchetype().getImageID());
                //add by zhengl ; date: 2011-01-17 ; note: 同时需要图片ID与动画ID
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
            ei = otherPlayer.getBodyWear().getWeapon();
            if (null != ei)
            {
            	//edit by zhengl; date: 2011-02-21; note: 武器读取添加标记位
            	yos.writeByte(1);
            	//add by zhengl; date: 2011-02-15; note: 客户端需要此值来做分等级展示图片以节约客户端内存
            	yos.writeShort(ei.getArchetype().getNeedLevel());
                yos.writeShort(((Weapon) ei.getArchetype()).getImageID());
                //add by zhengl ; date: 2011-01-17 ; note: 同时需要图片ID与动画ID
                yos.writeShort(ei.getArchetype().getAnimationID());
//                output.writeByte(ei.getGeneralEnhance().getLevel());
                //add by zhengl ; date: 2010-11-25 ; note: 其他玩家武器攻击特效
                yos.writeShort( ((Weapon) ei.getArchetype()).getLightID() );
                //add by zhengl ; date: 2011-01-17 ; note: 同时需要图片ID与动画ID
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
//                output.writeShort((short) -1);// 没有武器
            }

            yos.writeByte(!otherPlayer.isDead());
            yos.writeByte(otherPlayer.isVisible());
            
            HashMap<Integer,Pet> viewPetMap = PetServiceImpl.getInstance().getViewPetList(otherPlayer.getUserID());
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
                                        otherPlayer.where(),
                                        new PetChangeNotify(otherPlayer.getID(),
                                                OperatePet.SHOW, pet.imageID,pet.pk.getType()), true,
                                        otherPlayer.getID());
                }
            }else{
            	yos.writeByte(0);
            }

            yos.writeByte(otherPlayer.isSelling());
            if (otherPlayer.isSelling())
            {
                yos.writeUTF((MicroServiceImpl.getInstance().getStore(
                        otherPlayer.getUserID()).name));
            }
        }

        if (null != map.fixedNpcImageIDList)
        {
            yos.writeByte(map.fixedNpcImageIDList.size());

            byte[] imageBytes;
            Config npcConfig;

            for (short imageID : map.fixedNpcImageIDList)
            {
                imageBytes = NpcImageDict.getInstance().getImageBytes(imageID);
                npcConfig = NpcImageConfDict.get(imageID);
                yos.writeShort(imageID);
                yos.writeShort(npcConfig.animationID);
                yos.writeByte(npcConfig.npcGrid);
                yos.writeShort(npcConfig.npcHeight);
                //edit by zhengl; date: 2011-02-18; note: 下发NPC阴影大小以及选中框大小
                yos.writeByte(npcConfig.shadowSize);//暂不启用
                
                //edit by zhengl ; date: 2010-11-19 ; note:NPC的阴影需要用新的模式
//                output.writeByte(npcConfig.headExcursionX);
//                output.writeByte(npcConfig.headExcursionY);
//                output.writeByte(npcConfig.shadowType);
//                output.writeByte(npcConfig.shadowX);
//                output.writeShort(npcConfig.shadowY);
                //end

                if (Constant.CLIENT_OF_HIGH_SIDE != clientType)
                {
                    yos.writeShort(imageBytes.length);
                    yos.writeBytes(imageBytes);
                }
            }
        }
        else
        {
            yos.writeByte(0);
        }
        //NPC下发
        yos.writeShort((short) npcList.size());
        Config npcConfig;
        for (int i = 0; i < npcList.size(); i++)
        {
            npc = (Npc) npcList.get(i);
            yos.writeInt(npc.getID());
            yos.writeUTF(npc.getTitle());
            yos.writeUTF(npc.getName());
            yos.writeByte(npc.getClan().getID());
            yos.writeUTF(npc.getHello());

            if (null != npc.getScreamContent())
            {
                yos.writeByte(1);
                yos.writeUTF(npc.getScreamContent());
            }
            else
            {
                yos.writeByte(0);
            }

            yos.writeByte(npc.canInteract());
            yos.writeByte(npc.getCellX());
            yos.writeByte(npc.getCellY());
            //add by zhengli 2010-11-02
//            output.writeByte(npc.getCellZ());
            //end

            if (null == npc.where().fixedNpcImageIDList
                    || !npc.where().fixedNpcImageIDList.contains(npc
                            .getImageID()))
            {
                yos.writeByte(1);

                byte[] imageBytes = NpcImageDict.getInstance()
                        .getImageBytes(npc.getImageID());

                yos.writeShort(npc.getImageID());
                yos.writeShort(npc.getAnimationID());
                npcConfig = NpcImageConfDict.get(npc.getImageID());
                yos.writeByte(npcConfig.npcGrid);
                yos.writeShort(npcConfig.npcHeight);
                //edit by zhengl; date: 2011-02-18; note: 下发NPC阴影大小以及选中框大小
                yos.writeByte(npcConfig.shadowSize);//暂不启用
                
//                output.writeByte(npcConfig.headExcursionX);
//                output.writeByte(npcConfig.headExcursionY);
//                output.writeByte(npcConfig.shadowType);
//                output.writeByte(npcConfig.shadowX);
//                output.writeShort(npcConfig.shadowY);

                if (Constant.CLIENT_OF_HIGH_SIDE != clientType)
                {
                    yos.writeShort(imageBytes.length);
                    yos.writeBytes(imageBytes);
                }
            }
            else
            {
                yos.writeByte(0);
            }

            yos.writeByte(npc.getImageType());
            yos.writeShort(npc.getImageID());
            yos.writeShort(npc.getAnimationID());
//            if(Map.IS_NEW_MAP)
//            	output.writeShort(npc.getAnimationID());  //npc动画id
        }
        //怪物下发
        if (null != map.fixedMonsterImageIDList)
        {
            yos.writeByte(map.fixedMonsterImageIDList.size());

            byte[] imageBytes;
            MonsterImageConfDict.Config monsterConfig;

            for (short imageID : map.fixedMonsterImageIDList)
            {
                imageBytes = MonsterImageDict.getInstance()
                        .getMonsterImageBytes(imageID);
                monsterConfig = MonsterImageConfDict.get(imageID);

                yos.writeShort(imageID);
                log.info("怪物imageID:" + imageID);
                yos.writeShort(monsterConfig.animationID);
                log.info("怪物animationID:" + monsterConfig.animationID);
                yos.writeByte(monsterConfig.grid);
                yos.writeShort(monsterConfig.monsterHeight);
                //add by zhengl; date: 2011-02-18; note: 添加怪物阴影大小及选中框大小
                yos.writeByte(monsterConfig.shadowSize);//暂不启用
                //del by zhengl; date: 2011-02-18; note: 删除现在不用的数据
//                output.writeByte(monsterConfig.centerExcursionX);
//                output.writeByte(monsterConfig.headExcursionX);
//                output.writeByte(monsterConfig.headExcursionY);
//                output.writeByte(monsterConfig.shadowType);
//                output.writeByte(monsterConfig.shadowX);
//                output.writeShort(monsterConfig.shadowY);

                if (Constant.CLIENT_OF_HIGH_SIDE != clientType)
                {
                    yos.writeShort(imageBytes.length);
                    yos.writeBytes(imageBytes);
                }
            }
        }
        else
        {
            yos.writeByte(0);
        }

        yos.writeShort((short) monsterList.size());
        log.info("monsterList.size()=" + monsterList.size());

        for (int i = 0; i < monsterList.size(); i++)
        {
            monster = (Monster) monsterList.get(i);

            yos.writeInt(monster.getID());
            yos.writeUTF(monster.getName());
            yos.writeShort(monster.getLevel());
            yos.writeByte(monster.getClan().getID());
            yos.writeByte(monster.isActiveAttackType());
            yos.writeByte(monster.getVocation().value());
            yos.writeByte(monster.getMonsterLevel().value());
            yos.writeByte(monster.getObjectLevel().value());
            yos.writeByte(monster.getTakeSoulUserID() > 0 ? 1 : 0);
            yos.writeInt(monster.getHp());
            yos.writeInt(monster.getActualProperty().getHpMax());
            yos.writeInt(monster.getMp());
            yos.writeInt(monster.getActualProperty().getMpMax());

            yos.writeByte(monster.getCellX());
            yos.writeByte(monster.getCellY());
            //add by zhengl 2010-11-02
//            output.writeByte(monster.getCellZ());
            //end
            yos.writeByte(monster.getDirection());

            if (null == monster.where().fixedMonsterImageIDList 
            		|| !monster.where().fixedMonsterImageIDList.contains(monster.getImageID()))
            {
                yos.writeByte(1);
                yos.writeShort(monster.getImageID());
                yos.writeShort(monster.getAnimationID());
//                log.info("怪物imageID:" + monster.getImageID());
//                log.info("怪物animationID:" + monster.getAnimationID());
                MonsterImageConfDict.Config config = MonsterImageConfDict
                        .get(monster.getImageID());

                yos.writeByte(config.grid);
                yos.writeShort(config.monsterHeight);
                //add by zhengl; date: 2011-02-18; note: 添加怪物阴影大小及选中框大小
                yos.writeByte(config.shadowSize);//暂不启用
                
//                output.writeByte(config.centerExcursionX);
//                output.writeByte(config.headExcursionX);
//                output.writeByte(config.headExcursionY);
//                output.writeByte(config.shadowType);
//                output.writeByte(config.shadowX);
//                output.writeShort(config.shadowY);

                if (Constant.CLIENT_OF_HIGH_SIDE != clientType)
                {
                    byte[] monsterImage = MonsterImageDict.getInstance()
                            .getMonsterImageBytes(monster.getImageID());

                    yos.writeShort(monsterImage.length);
                    yos.writeBytes(monsterImage);
                }
            }
            else
            {
                yos.writeByte(0);
            }

            yos.writeShort(monster.getImageID());
            yos.writeShort(monster.getAnimationID());
            yos.writeByte(monster.isVisible());
//            log.info("怪物imageID:" + monster.getImageID());
//            log.info("怪物animationID:" + monster.getAnimationID());
        }
        log.info("output size = " + String.valueOf(yos.size()));
    }
}
