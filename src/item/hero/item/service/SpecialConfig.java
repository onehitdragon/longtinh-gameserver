package hero.item.service;

import org.dom4j.Element;

public class SpecialConfig {
	
	/**
	 * [物品编号],[药品类型],[使用类型],[点数]
	 */
	public int[][] big_tonic; 
	
	/**
	 * [物品编号],[使用次数],[技能ID]
	 */
	public int[][] pet_per_function;
	
	/**
	 * [物品编号],[技能ID]
	 */
	public int[][] pet_forever_function;
	
	/**
	 * [碎率],[正常率],[闪光率]
	 */
	public int[][] odds_enhance_list;
    /**
     * 特殊物品编号
     * 任务传送道具编号
     */
    public int number_transport; 
    
    /**
     * 复活道具
     */
    public int number_revive;
    /**
     * 创建公会道具编号
     */
    public int number_guild_build;

    /**
     * 背包扩展物品ID
     */
    public int bag_expan_goods_id;
    
    /**
     * 双倍经验
     */
    public int[][] experience_book_time;
    /**
     * 双倍经验icon
     */
    public short   experience_book_icon;
    
    
	
	public SpecialConfig (Element node) {
        String[] temp = node.elementTextTrim("pet_per_function").split(";");
        pet_per_function = new int[temp.length][3];
        for (int i = 0; i < temp.length; i++) {
        	String [] group = temp[i].split(",");
        	pet_per_function[i][0] = Integer.valueOf(group[0]);
        	pet_per_function[i][1] = Integer.valueOf(group[1]);
        	pet_per_function[i][2] = Integer.valueOf(group[2]);
		}
        
        temp = node.elementTextTrim("pet_forever_function").split(";");
        pet_forever_function = new int[temp.length][2];
        for (int i = 0; i < temp.length; i++) {
        	String [] group = temp[i].split(",");
        	pet_forever_function[i][0] = Integer.valueOf(group[0]);
        	pet_forever_function[i][1] = Integer.valueOf(group[1]);
		}
        
        temp = node.elementTextTrim("big_tonic").split(";");
        big_tonic = new int[temp.length][4];
        for (int i = 0; i < temp.length; i++) {
        	String [] group = temp[i].split(",");
        	big_tonic[i][0] = Integer.valueOf(group[0]);
        	big_tonic[i][1] = Integer.valueOf(group[1]);
        	big_tonic[i][2] = Integer.valueOf(group[2]);
        	big_tonic[i][3] = Integer.valueOf(group[3]);
		}
        
        temp = node.elementTextTrim("odds_enhance_list").split(";");
        odds_enhance_list = new int[temp.length][3];
        for (int i = 0; i < temp.length; i++) {
        	String [] group = temp[i].split(",");
        	odds_enhance_list[i][0] = Integer.valueOf(group[0]);
        	odds_enhance_list[i][1] = Integer.valueOf(group[1]);
        	odds_enhance_list[i][2] = Integer.valueOf(group[2]);
		}
        
        temp = node.elementTextTrim("experience_book_time").split(";");
        experience_book_time = new int[temp.length][2];
        for (int i = 0; i < temp.length; i++) {
        	String [] group = temp[i].split(",");
        	experience_book_time[i][0] = Integer.valueOf(group[0]);
        	experience_book_time[i][1] = Integer.valueOf(group[1]);
		}
        
        experience_book_icon = Short.valueOf( node.elementTextTrim("experience_book_icon") );
        
        //加载特殊道具编号
        number_transport = Integer.valueOf(node.elementTextTrim("number_transport"));
        number_revive = Integer.valueOf(node.elementTextTrim("number_revive"));
        number_guild_build = Integer.valueOf(node.elementTextTrim("number_guild_build"));

        //背包扩展需要的ID
        bag_expan_goods_id = Integer.parseInt(node.elementTextTrim("bag_expan_id"));
	}

}
