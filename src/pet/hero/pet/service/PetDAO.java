package hero.pet.service;

import hero.expressions.service.CEService;
import hero.item.EquipmentInstance;
import hero.pet.Pet;
import hero.pet.PetList;
import hero.pet.PetPK;
import hero.share.service.LogWriter;
import hero.skill.ActiveSkill;
import hero.skill.PetActiveSkill;
import hero.skill.PetSkill;
import hero.skill.service.SkillServiceImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import yoyo.service.tools.database.DBServiceImpl;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 PetDAO.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-11-12 下午01:34:11
 * @描述 ：
 */

public class PetDAO
{
	private static Logger log = Logger.getLogger(PetDAO.class);
    /**
     * 加载宠物脚本
     */
    private static final String SELECT_SQL            = "SELECT * FROM pet WHERE user_id=?"
                                                              + " ORDER BY get_time ASC";
    /**
     * 加载骑宠
     */
    private static final String SELECT_MOUNT_SQL      = "SELECT pet_id from pet " 
    											+ "where status=1 and type=1 and stage=2 and user_id=?";

    /**
     * 添加宠物
     */
    private static final String INSERT_SQL            = "INSERT INTO pet(user_id,pet_id,stage,type,color,born_from,bind,name,kind) " +
														" VALUES(?,?,?,?,?,?,?,?,?)";
    /**
     * 查找最大ID值，因为在刚添加宠物后就需要宠物的ID值，所以必须提前把这个值取出来，添加完后再赋给 pet.id
     */
    private static final String SELECT_MAX_ID_PET_SQL = "SELECT MAX(t.pet_id) FROM pet t";

    /**
     * 清除宠物显示状态
     */
    private static final String CLEAR_OLD_STATUS_SQL  = "UPDATE pet SET status=0 WHERE user_id=? AND status=1 LIMIT 1";

    /**
     * 更新宠物显示状态
     */
    private static final String UPDATE_NOW_STATUS_SQL = "UPDATE pet SET status=1 WHERE user_id=? AND pet_id=? LIMIT 1";

    /**
     * 删除宠物
     */
    private static final String DELETE_SQL            = "DELETE FROM pet WHERE user_id=? AND pet_id=? LIMIT 1";

    /**
     * 修改宠物
     */
    private static final String UPDATE_PET_STAGE_SQL  = "UPDATE pet t SET t.stage=?,t.total_online_time=?,t.feeding=?,t.fun=?,t.type=?,t.curr_evolve_point=?," +
    		"t.curr_herb_point=?,t.curr_carn_point=?,t.curr_fight_point=?,t.mp=?,t.rage=?,t.wit=?," +
    		"t.agile=?,t.grow_exp=?,t.fight_exp=?,t.health_time=?,t.status=?,t.level=?,t.curr_level_time=? WHERE t.user_id=? AND t.pet_id=?";
    
    /**
     * 宠物升级
     */
    private static final String UPD_UPGRADE_PET_SQL = "UPDATE pet t SET t.level=?,t.curr_level_time=?,t.mp=? " +
    		" WHERE t.user_id=? AND t.pet_id=?";
    
    /**
     * 修改宠物名称
     */
    private static final String UPDATE_PET_NAME_SQL = "UPDATE pet t SET t.name=? WHERE t.user_id=? AND t.pet_id=?";
    
    /**
     * 修改宠物所有者
     */
    private static final String UPDATE_PET_OWNER_SQL = "UPDATE pet t set t.user_id=?,t.born_from=? WHERE t.user_id=? AND t.pet_id=?";
    
    /**
     * 宠物技能列表
     */
    private static final String SELECT_PET_SKILL_SQL = "SELECT * FROM pet_skill t WHERE t.pet_id=? ";
    
    /**
     * 增加技能SQL脚本
     */
    private static String INSERT_SKILL_SQL        = "INSERT INTO pet_skill(pet_id,skill_id) VALUES(?,?)";

    /**
     * 技能升级SQL脚本
     */
    private static String UPGRADE_SKILL_SQL       = "UPDATE pet_skill t SET t.skill_id = ? WHERE t.pet_id = ? AND t.skill_id = ? LIMIT 1";

    
    /**
     * 删除技能冷却时间SQL脚本
     */
    private static String DELETE_SKILL_CD_SQL     = "UPDATE pet_skill t SET t.trace_cd_time = 0 WHERE t.pet_id = ? AND  t.skill_id = ? LIMIT 1";
    /**
     * 更新技能冷却时间SQL脚本
     */
    private static String UPGRADE_SKILL_CD_SQL    = "UPDATE pet_skill t SET t.trace_cd_time = ? WHERE t.pet_id = ? AND t.skill_id = ?";

    /**
     * 更新宠物的装备
     */
    private static final String UPDATE_PET_EQUIPMENT = "UPDATE pet t SET t.equip_1=?, t.equip_2=?,t.equip_3=?,t.equip_4=? where t.pet_id=?";
    /**
     * 加载玩家宠物列表
     * 
     * @param _userID
     */
    public static PetList load (int _userID)
    {
    	log.debug("start load pet .. @@@@@@@");
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SELECT_SQL);

            pstm.setInt(1, _userID);
            set = pstm.executeQuery();

            PetList petList = new PetList();
            int viewsize=0,lastviewsize=0;

            while (set.next())
            {
            	int petID = set.getInt("pet_id");
                short viewStatus = set.getShort("status");
                short stage = set.getShort("stage");
                short type = set.getShort("type");
                short kind = set.getShort("kind");
                short color = set.getShort("color");
                short fun = set.getShort("fun");
                int curr_fight_point = set.getInt("curr_fight_point");
                int curr_evolve_time = set.getInt("curr_evolve_point");
                short born_from = set.getShort("born_from");
                short bind = set.getShort("bind");
                int feeding = set.getInt("feeding");
                int mp = set.getInt("mp");
                String name = set.getString("name");
                int curr_herb_point = set.getInt("curr_herb_point");
                int curr_carn_point = set.getInt("curr_carn_point");
                int health_time = set.getInt("health_time");
                
                int total_online_time = set.getInt("total_online_time");
                int level = set.getInt("level");
                int curr_level_time = set.getInt("curr_level_time");
                
                                
                PetPK pk = new PetPK(kind, stage, type);
                Pet pet = PetDictionary.getInstance().getPet(pk);//这里必须要用pk获取
                pet.id = petID;
                pet.pk = pk;
                pet.bind = bind;
                pet.color = color;
                pet.fun = fun;
                pet.currEvolvePoint = curr_evolve_time;
                pet.currFightPoint = curr_fight_point;
                pet.bornFrom = born_from;
                pet.feeding = feeding;
                pet.mp = mp;
                pet.name = name;
                pet.currCarnPoint = curr_carn_point;
                pet.currHerbPoint = curr_herb_point;
                pet.healthTime = health_time;
                pet.totalOnlineTime = total_online_time;
                pet.level = level;
                pet.currLevelTime = curr_level_time;
                
                if(type == 2){
                    int rage = set.getInt("rage");
                    int wit = set.getInt("wit");
                    int agile = set.getInt("agile");
                    int grow_exp = set.getInt("grow_exp");
                    int fight_exp = set.getInt("fight_exp");
                    
                    int equip_1 = set.getInt("equip_1");
                    int equip_2 = set.getInt("equip_2");
                    int equip_3 = set.getInt("equip_3");
                    int equip_4 = set.getInt("equip_4");
                    
                    pet.rage = rage;
                    pet.wit = wit;
                    pet.agile = agile;
                    pet.grow_exp = grow_exp;
                    pet.fight_exp = fight_exp;
                    
                    pet.str = CEService.playerBaseAttribute(pet.level,pet.a_str);
        			pet.agi = CEService.playerBaseAttribute(pet.level,pet.a_agi);
        			pet.intel = CEService.playerBaseAttribute(pet.level,pet.a_intel);
        			pet.spi = CEService.playerBaseAttribute(pet.level,pet.a_spi);
        			pet.luck = CEService.playerBaseAttribute(pet.level,pet.a_luck);
                    
                    pet.petEquList.add(equip_1);
                    pet.petEquList.add(equip_2);
                    pet.petEquList.add(equip_3);
                    pet.petEquList.add(equip_4);
                }
                /*if(pet.feeding > Pet.FEEDING_YELLOW_FULL){
                	pet.startHealthTime = System.currentTimeMillis();
                }*/
//                loadPetSkill(pet);
                
                
                petList.add(pet);
                log.debug("pet list add [ "+_userID+" --- " + pet.id+ "] end");
                if (viewStatus == 1)
                {
                	if(pet.pk.getStage() != Pet.PET_STAGE_EGG){//宠物蛋isview=false,但是viewStatus=1,是放在玩家身上的正在孵化的不显示的
                		pet.isView = true;
                	}
                	if(viewsize <= 2){
                		viewsize = petList.setViewPet(pet);
                		viewsize += 1;
                	}
                	if(lastviewsize <= 2){
                		petList.setLastTimesViewPetID(petID);
                		lastviewsize += 1;
                	}
                }
            }
            log.debug("load pet list end..@@@@@ " );
            set.close();
            pstm.close();
            conn.close();
            
            return petList;
            
        }
        catch (SQLException e)
        {
        	e.printStackTrace();
            LogWriter.error(null, e);
        }
        finally
        {
            try
            {
                if (null != set)
                {
                    set.close();
                }
                if (null != pstm)
                {
                    pstm.close();
                }

                if (null != conn)
                {
                    conn.close();
                }
            }
            catch (Exception e)
            {
            	e.printStackTrace();
            }
        }

        return null;
    }
    
    /**
     * 加载宠物技能列表
     * @param pet
     */
    public static ArrayList<int[]> loadPetSkill(Pet pet){
    	Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SELECT_PET_SKILL_SQL);
            
            pstm.setInt(1, pet.id);
            
            rs = pstm.executeQuery();
            
            ArrayList<Integer> existsCDSkillIDList = null;
            int skillID, traceCoolDownTime;
            ArrayList<int[]> skillInfoList = new ArrayList<int[]>();
            
            while(rs.next()){
            	skillID = rs.getInt("skill_id");
            	traceCoolDownTime = rs.getInt("trace_cd_time");
            	
            	skillInfoList.add(new int[]{skillID, traceCoolDownTime });

                if (traceCoolDownTime > 0)
                {
                    if (null == existsCDSkillIDList)
                    {
                        existsCDSkillIDList = new ArrayList<Integer>();
                    }

                    existsCDSkillIDList.add(skillID);
                }
            }
            
            rs.close();
            
            if (null != existsCDSkillIDList)
            {
                pstm.close();
                pstm = null;

                conn.setAutoCommit(false);
                pstm = conn.prepareStatement(DELETE_SKILL_CD_SQL);

                for (int id : existsCDSkillIDList)
                {
                    pstm.setInt(1, pet.id);
                    pstm.setInt(2, id);

                    pstm.addBatch();
                }

                pstm.executeBatch();
                conn.setAutoCommit(true);
            }
            
            
            pstm.close();
            
            return skillInfoList;
        }
        catch (Exception e)
        {
        	e.printStackTrace();
            log.error("从DB加载宠物技能列表 error : ", e);
        }
        finally
        {
            try
            {
                if (null != pstm)
                {
                    pstm.close();
                    pstm = null;
                }

                if (null != conn)
                {
                	if (!conn.getAutoCommit())
                    {
                        conn.setAutoCommit(true);
                    }
                    conn.close();
                    conn = null;
                }
            }
            catch (Exception e)
            {
            	e.printStackTrace();
            }
        }
        return null;
    }
    
    public static int selectMountPet(int _userID) {
    	int petID = 0;
    	Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(SELECT_MOUNT_SQL);
    		pstm.setInt(1, _userID);
    		rs = pstm.executeQuery();
    		if (rs.next()) {
    			petID = rs.getInt("pet_id");
    		}
    		pstm.close();
    		rs.close();
    		pstm = null;
    		rs = null;
        }catch(SQLException e){
        	e.printStackTrace();
        }finally{
        	try
			{
            	if(pstm != null){
    				pstm.close();
            		pstm = null;
            	}
            	if(conn != null){
            		conn.close();
            		conn = null;
            	}
			}catch(SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return petID;
    }
    
    /**
     * 添加技能
     * @param petID
     * @param skillID
     */
    public static boolean addSkill(int petID, List<PetSkill> skillList){
    	Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
        	log.debug("pet learn skill size = " + skillList.size());
            conn = DBServiceImpl.getInstance().getConnection();
            for(PetSkill skill : skillList){
            	if(skill.isNewSkill){
            		pstm = conn.prepareStatement(INSERT_SKILL_SQL);
            		pstm.setInt(1, petID);
            		pstm.setInt(2, skill.id);
            		pstm.execute();
            		pstm.close();
            	}else{
            		pstm = conn.prepareStatement(UPGRADE_SKILL_SQL);
            		pstm.setInt(1, skill.id);
            		pstm.setInt(2, petID);
            		pstm.setInt(3, skill._lowLevelSkillID);
            		pstm.executeUpdate();
            		pstm.close();
            	}
            	pstm = null;
            }
            return true;
        }catch(SQLException e){
        	log.error("宠物添加技能 to DB error : ",e);
        	return false;
        }finally{
        	try
			{
            	if(pstm != null){
    				pstm.close();
            		pstm = null;
            	}
            	if(conn != null){
            		conn.close();
            		conn = null;
            	}
			}catch(SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
    /**
     * 玩家下线时，保存宠物技能的冷却时间
     */
    public static void updatePetSkillTraceCD(Pet pet){
    	Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            List<PetActiveSkill> activeSkillList = pet.petActiveSkillList;
            List<PetActiveSkill> updateSkillList = null;

            for (PetActiveSkill skill : activeSkillList)
            {
                if (skill.reduceCoolDownTime > PetServiceImpl.VALIDATE_CD_TIME)
                {
                    if (null == updateSkillList)
                    {
                        updateSkillList = new ArrayList<PetActiveSkill>();
                    }

                    updateSkillList.add(skill);
                }
            }

            if (null != updateSkillList)
            {
                conn = DBServiceImpl.getInstance().getConnection();
                conn.setAutoCommit(false);
                pstm = conn.prepareStatement(UPGRADE_SKILL_CD_SQL);

                for (PetActiveSkill skill : updateSkillList)
                {
                    pstm.setInt(1, skill.reduceCoolDownTime - PetServiceImpl.VALIDATE_CD_TIME);
                    pstm.setInt(2, pet.id);
                    pstm.setInt(3, skill.id);

                    pstm.addBatch();
                }

                pstm.executeBatch();
                conn.setAutoCommit(true);
            }
            pstm.close();
        }
        catch (Exception e)
        {
            log.error("玩家下线时，保存宠物技能的冷却时间 error : ",e);
        }
        finally
        {
            try
            {
                if (null != pstm)
                {
                    pstm.close();
                }

                if (null != conn)
                {
                    if (!conn.getAutoCommit())
                    {
                        conn.setAutoCommit(true);
                    }

                    conn.close();
                }
            }
            catch (Exception e)
            {
            	e.printStackTrace();
            }
        }
    }
    
    /**
     * 为新手添加宠物蛋
     * @param _userID
     * @param _color
     * @param _petID
     */
    public  static void addPetForNewPlaye(int _userID, Pet _pet){
    	add(_userID,  _pet);
    }

    /**
     * 添加宠物
     * 
     * @param _userID
     */
    public static void add (int _userID, Pet _pet)
    {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            
            pstm = conn.prepareStatement(SELECT_MAX_ID_PET_SQL);
            rs = pstm.executeQuery();
            rs.next();
            int id = rs.getInt(1);
            log.debug("max pet id = " + id);
            rs.close();
            pstm.close();
            
            pstm = conn.prepareStatement(INSERT_SQL);

            pstm.setInt(1, _userID);
            pstm.setInt(2, id+1);
            pstm.setShort(3, _pet.pk.getStage());
            pstm.setShort(4, _pet.pk.getType());
            pstm.setShort(5, _pet.color);
            pstm.setShort(6, _pet.bornFrom);
            pstm.setShort(7, _pet.bind);
            pstm.setString(8, _pet.name);
            pstm.setShort(9, _pet.pk.getKind());

            pstm.execute();
            
            pstm.close();
            conn.close();
            
            _pet.id = id+1;

            log.debug("add pet end . pet id="+_pet.id);
        }        
        catch (Exception e)
        {
        	e.printStackTrace();
            log.error("add pet errors : ", e);
        }
        finally
        {
            try
            {
            	if(null != rs){
            		rs.close();
            		rs = null;
            	}
                if (null != pstm)
                {
                    pstm.close();
                    pstm = null;
                }

                if (null != conn)
                {
                    conn.close();
                    conn = null;
                }
            }
            catch (SQLException e)
            {
            	e.printStackTrace();
            	log.error("添加宠物蛋 errors : ",e);
            }
        }
    }
    
    /**
     * 修改宠物属性
     * @param _userID
     * @param _pet
     */
    public static void updatePet(int _userID, Pet _pet){
    	Connection conn = null;
    	PreparedStatement ps = null;
    	try{
    		log.debug("user : " + _userID+" , pet dao udpdate pet id = " + _pet.id +", pet healthtime=" +_pet.healthTime);
    		conn = DBServiceImpl.getInstance().getConnection();
    		ps = conn.prepareStatement(UPDATE_PET_STAGE_SQL);
    		ps.setShort(1, _pet.pk.getStage());
    		ps.setLong(2, _pet.totalOnlineTime);
    		ps.setInt(3, _pet.feeding>=0?_pet.feeding:0);
    		ps.setShort(4, _pet.fun);
    		ps.setShort(5, _pet.pk.getType());
    		ps.setInt(6, _pet.currEvolvePoint);
    		ps.setInt(7, _pet.currHerbPoint);
    		ps.setInt(8, _pet.currCarnPoint);
    		ps.setInt(9, _pet.currFightPoint);
    		ps.setInt(10, _pet.mp);
    		ps.setInt(11, _pet.rage);
    		ps.setInt(12, _pet.wit);
    		ps.setInt(13, _pet.agile);
    		ps.setInt(14, _pet.grow_exp);
    		ps.setInt(15, _pet.fight_exp);
    		ps.setInt(16, _pet.healthTime);
    		ps.setShort(17, _pet.viewStatus);
    		ps.setInt(18, _pet.level);
    		ps.setInt(19, _pet.currLevelTime);
    		ps.setInt(20, _userID);
    		ps.setInt(21, _pet.id);
    		
    		
    		ps.executeUpdate();
    		
    		log.debug("pet dao udpdate pet end....");
    		
    		ps.close();
    		conn.close();
    	}catch (SQLException e)
        {
    		e.printStackTrace();
    		
            log.error("修改宠物属性:", e);
        }finally
        {
            try
            {
                if (null != ps)
                {
                    ps.close();
                    ps = null;
                }

                if (null != conn)
                {
                    conn.close();
                    conn = null;
                }
            }
            catch (Exception e)
            {
            	e.printStackTrace();
            }
        }
    }
    
    /**
     * 宠物升级
     * @param _userID
     * @param pet
     */
    public static void upgradePet(int _userID, Pet pet){
    	
    	Connection conn = null;
    	PreparedStatement ps = null;
    	try{
    		conn = DBServiceImpl.getInstance().getConnection();
    		ps = conn.prepareStatement(UPD_UPGRADE_PET_SQL);
    		ps.setInt(1, pet.level);
    		ps.setInt(2, pet.currLevelTime);
    		ps.setInt(3, pet.mp);
    		
    		ps.setInt(4, _userID);
    		ps.setInt(5, pet.id);
    		
    		ps.executeUpdate();
    		ps.close();
    		conn.close();
    	}catch (Exception e)
        {
    		e.printStackTrace();
            LogWriter.error("宠物升级:", e);
        }finally
        {
            try
            {
                if (null != ps)
                {
                    ps.close();
                    ps = null;
                }

                if (null != conn)
                {
                    conn.close();
                    conn = null;
                }
            }
            catch (Exception e)
            {
            	e.printStackTrace();
            }
        }
    }
    
    /**
     * 修改宠物名称
     * @param _userID
     * @param _petID
     * @param name 名称
     * @return
     */
    public static int updatePetName(int _userID, int _petID, String name){
    	{
            Connection conn = null;
            PreparedStatement pstm = null;

            try
            {
                conn = DBServiceImpl.getInstance().getConnection();
                pstm = conn.prepareStatement(UPDATE_PET_NAME_SQL);
                
                pstm.setString(1, name);
                pstm.setInt(2, _userID);
                pstm.setInt(3, _petID);
                
                return pstm.executeUpdate();
                
            }catch (Exception e)
            {
            	e.printStackTrace();
                LogWriter.error("修改宠物名称 :  ", e);
            }
            finally
            {
                try
                {
                    if (null != pstm)
                    {
                        pstm.close();
                    }

                    if (null != conn)
                    {
                        conn.close();
                    }
                }
                catch (Exception e)
                {
                	return 0;
                }
            }
    	}
            return 0;
    }
    
    /**
     * 修改宠物的所有者(宠物交易)
     * @param buyerID 
     * @param sellerID
     * @param petID
     * @return success 1; fail 0
     */
    public static int updatePetOwner(int buyerID, int sellerID, int petID){
    	{
            Connection conn = null;
            PreparedStatement pstm = null;

            try
            {
                conn = DBServiceImpl.getInstance().getConnection();
                pstm = conn.prepareStatement(UPDATE_PET_OWNER_SQL);
                
                pstm.setInt(1, buyerID);
                pstm.setShort(2, (short)0);//交易产出
                pstm.setInt(3, sellerID);
                pstm.setInt(4, petID);
                
                int res = pstm.executeUpdate();
                return res;
            }catch (Exception e)
            {
            	
                LogWriter.error("交易宠物 error: ", e);
                return 0;
            }
            finally
            {
                try
                {
                    if (null != pstm)
                    {
                        pstm.close();
                    }

                    if (null != conn)
                    {
                        conn.close();
                    }
                }
                catch (Exception e)
                {
                	e.printStackTrace();
                }
            }
    	}
    }

    /**
     * 更新宠物显示状态
     * 
     * @param _userID 玩家编号
     * @param _viewPetID 宠物编号
     */
    public static void updateViewStatus (int _userID, int _viewPetID,
            byte _updateMark)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();

            if (PetServiceImpl.VIEW_STATUS_OF_UPDATE_OLD == _updateMark)
            {
                pstm = conn.prepareStatement(CLEAR_OLD_STATUS_SQL);
                pstm.setInt(1, _userID);
                pstm.executeUpdate();
            }
            else if (PetServiceImpl.VIEW_STATUS_OF_UPDATE_NOW == _updateMark)
            {
                pstm = conn.prepareStatement(UPDATE_NOW_STATUS_SQL);
                pstm.setInt(1, _userID);
                pstm.setInt(2, _viewPetID);
                pstm.executeUpdate();
            }
            else if (PetServiceImpl.VIEW_STATUS_OF_UPDATE_ALL == _updateMark)
            {
                /*pstm = conn.prepareStatement(CLEAR_OLD_STATUS_SQL);
                pstm.setInt(1, _userID);
                pstm.executeUpdate();*/

                pstm.close();
                pstm = conn.prepareStatement(UPDATE_NOW_STATUS_SQL);
                pstm.setInt(1, _userID);
                pstm.setInt(2, _viewPetID);
                pstm.executeUpdate();
            }
        }
        catch (SQLException e)
        {
        	e.printStackTrace();
            LogWriter.error(null, e);
        }
        finally
        {
            try
            {
                if (null != pstm)
                {
                    pstm.close();
                }

                if (null != conn)
                {
                    conn.close();
                }
            }
            catch (Exception e)
            {
            	e.printStackTrace();
            }
        }
    }

    /**
     * 放弃宠物
     * 
     * @param _userID 玩家编号
     * @param _petID 宠物编号
     */
    public static void dice (int _userID, int _petID)
    {
        Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(DELETE_SQL);

            pstm.setInt(1, _userID);
            pstm.setInt(2, _petID);

            pstm.execute();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
            LogWriter.error(null, e);
        }
        finally
        {
            try
            {
                if (null != pstm)
                {
                    pstm.close();
                }

                if (null != conn)
                {
                    conn.close();
                }
            }
            catch (Exception e)
            {
            	e.printStackTrace();
            }
        }
    }
    
    /**
     * 更新宠物的装备
     * @param pet
     * equip_1,equip_2,equip_3,equip_4,按顺序依次是 头部、身躯、爪部、尾部
     */
    public static void updPetEquipment(Pet pet){
    	Connection conn = null;
        PreparedStatement pstm = null;

        try
        {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement(UPDATE_PET_EQUIPMENT);
            
            pstm.setInt(1, pet.getPetBodyWear().getPetEqHead()==null?0:pet.getPetBodyWear().getPetEqHead().getInstanceID());
            pstm.setInt(2, pet.getPetBodyWear().getPetEqBody()==null?0:pet.getPetBodyWear().getPetEqBody().getInstanceID());
            pstm.setInt(3, pet.getPetBodyWear().getPetEqWeapon()==null?0:pet.getPetBodyWear().getPetEqWeapon().getInstanceID());
            pstm.setInt(4, pet.getPetBodyWear().getPetEqTail()==null?0:pet.getPetBodyWear().getPetEqTail().getInstanceID());
            pstm.setInt(5, pet.id);
            
            pstm.executeUpdate();
        }
        catch (SQLException e)
        {
        	e.printStackTrace();
            LogWriter.error(null, e);
        }
        finally
        {
            try
            {
                if (null != pstm)
                {
                    pstm.close();
                }

                if (null != conn)
                {
                    conn.close();
                }
            }
            catch (Exception e)
            {
            	e.printStackTrace();
            }
        }
    }
    
}
