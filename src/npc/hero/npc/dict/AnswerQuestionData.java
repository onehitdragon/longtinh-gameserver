package hero.npc.dict;

import hero.npc.dict.QuestionDict.AwardData;
import hero.npc.dict.QuestionDict.QuestionData;

import java.util.ArrayList;

/**
 * QuestionDict 字典的支持类.
 * @author zhengl
 *
 */
public class AnswerQuestionData {
	
	/**
	 * 答题到了第几题
	 */
	public int   step;
	/**
	 * 答题所获得的总分数
	 */
	public int   sumPoint;
	
    public int   id, questionSum, point, awardID, refreshType, refreshDay, refreshTimeSum;
    
    public boolean isOpen;

    public String   name;
    
    public ArrayList<QuestionData> question; 
    
    public AwardData award;
    
    public String[] startTime;
    
    public String[] endTime;

}
