package hero.ui;

import hero.item.EqGoods;
import hero.item.Equipment;
import hero.item.Goods;
import hero.npc.function.system.TaskPassageway;
import hero.skill.Skill;
import hero.skill.service.SkillServiceImpl;
import hero.task.Task;
import hero.task.Award.AwardGoodsUnit;

import java.io.IOException;
import java.util.ArrayList;

import yoyo.tools.YOYOOutputStream;


public class UI_AnswerQuestion {
	
    /**
     * 获取UI类型
     * 
     * @return
     */
    public static EUIType getType ()
    {
        return EUIType.ANSWER_QUESTION;
    }
    
    public static EUIType getEndType ()
    {
        return EUIType.TIP_UI;
    }
    
    /**
     * 问题格式化
     * @param _question
     * @param _answer
     * @return
     */
    public static byte[] getBytes(String _question, ArrayList<String> _answer, String _title)
    {
    	YOYOOutputStream output = new YOYOOutputStream();
    	
    	try 
    	{
    		output.writeByte(getType().getID());
    		output.writeUTF(_title);
    		output.writeUTF(_question);
    		output.writeByte(_answer.size());
    		for (int i = 0; i < _answer.size(); i++) 
    		{
    			output.writeUTF(_answer.get(i));
			}
			output.flush();
		} 
    	catch (IOException e) 
    	{
			e.printStackTrace();
		}
    	return output.getBytes();
    }
    
    public static byte[] getEndBytes(String _endContent, String _award, String _title)
    {
    	YOYOOutputStream output = new YOYOOutputStream();
    	
    	try 
    	{
    		output.writeByte(getEndType().getID());
    		output.writeUTF(_title);
    		output.writeUTF(_endContent);
    		output.writeUTF(_award);
			output.flush();
		} 
    	catch (IOException e) 
    	{
			e.printStackTrace();
		}
    	return output.getBytes();
    }

}
