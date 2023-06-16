package hero.npc.dict;

import hero.item.Goods;
import hero.item.dictionary.GoodsContents;
import hero.npc.dict.NpcDataDict.NpcData;
import hero.npc.service.NotPlayerServiceImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import javolution.util.FastMap;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class QuestionDict 
{
	private static QuestionDict instance;
	
	/**
     * 随机数生成器
     */
    private final static Random RANDOM = new Random();
	
    /**
     * 功能列表(没必要做成字典模式)
     */
    private ArrayList<AnswerQuestionData> anwserQuestion;
    
    /**
     * 奖励字典
     */
    private FastMap<Integer, AwardData> awardDict;
    
    /**
     * 问题字典 <功能id, <问题ID, 问题>>
     */
    private Hashtable<Integer, Hashtable<Integer, QuestionData>> questionDict;
    
    private QuestionDict ()
    {
    	awardDict = new FastMap<Integer, AwardData>();
    	anwserQuestion = new ArrayList<AnswerQuestionData>();
    	questionDict = new Hashtable<Integer, Hashtable<Integer,QuestionData>>();
    }
    
    /**
     * 功能名称获得
     * @return
     */
    public String[] getAnwserQuestionNames()
    {
    	String[] functionName = new String[anwserQuestion.size()];
    	for (int i = 0; i < anwserQuestion.size(); i++) 
    	{
    		functionName[i] = anwserQuestion.get(i).name;
		}
    	return functionName;
    }
    
    /**
     * 获得问题索引
     * @return
     */
    public int[] getAnwserQuestionIDs()
    {
    	int[] IDs = new int[anwserQuestion.size()];
    	for (int i = 0; i < anwserQuestion.size(); i++) 
    	{
    		IDs[i] = i;
		}
    	return IDs;
    }
    
    /**
     * 返回所有问题组,以及每组随机抽选的问题
     * @param _groupID
     * @return
     */
    public AnswerQuestionData getAnswerQuestionData(int _groupID)
    {
    	//为每组问题单独填充题目
    	AnswerQuestionData questionGroup = new AnswerQuestionData();
    	try 
    	{
    		AnswerQuestionData aqData = anwserQuestion.get(_groupID);
    		aqData.question = new ArrayList<QuestionData>();
    		
			Hashtable<Integer, QuestionData> data = new Hashtable<Integer, QuestionData>();
			//获得某组里面所有题目
			data = this.questionDict.get(aqData.id);
	        //-------获得问题键集合-------
	        Enumeration<Integer> keys = data.keys();
	        ArrayList<Integer> keyList = new ArrayList<Integer>();
	        while (keys.hasMoreElements()) {
				keyList.add(keys.nextElement());
			}
	        //-------获得问题键集合-------
	        //-------为键集合选取questionSum个随机索引-------
			ArrayList<Integer> randomIndex = new ArrayList<Integer>();
	        int size = keyList.size();
	        int index = 0;
	        for (; randomIndex.size() < aqData.questionSum;) 
	        {
	        	index = RANDOM.nextInt(size);
	        	if (!randomIndex.contains(index)) {
	        		randomIndex.add(index);
	        	}
	        }
	        //-------为键集合选取questionSum个随机索引-------
	        
	        for (int i = 0; i < randomIndex.size(); i++)
	        {
	        	//从随机索引里取索引-->用索引找key-->用key找问题
	        	QuestionData question = data.get( keyList.get( randomIndex.get(i) ) );
	        	if (question == null) {
					System.out.println("得到question为NULL,这不应该发生.");
				}
	        	aqData.question.add(question);
	        }
			questionGroup.award = aqData.award;
			questionGroup.awardID = aqData.awardID;
			questionGroup.endTime = aqData.endTime;
			questionGroup.id = aqData.id;
			questionGroup.isOpen = aqData.isOpen;
			questionGroup.name = aqData.name;
			questionGroup.point = aqData.point;
			questionGroup.question = aqData.question;
			questionGroup.questionSum = aqData.questionSum;
			questionGroup.refreshDay = aqData.refreshDay;
			questionGroup.refreshTimeSum = aqData.refreshTimeSum;
			questionGroup.refreshType = aqData.refreshType;
			questionGroup.startTime = aqData.startTime;
			questionGroup.sumPoint = 0;
			questionGroup.step = 0;
		} 
    	catch (Exception e) 
		{
			e.printStackTrace();
		}
    	return questionGroup;
    }
    
    public static void main (String[] args)
    {
    	QuestionDict.getInstance().load(
    			System.getProperty("user.dir") + "/" + "res/data/npc/normal_npc/fun_data/anwser_question/", 
    			System.getProperty("user.dir") + "/" + "res/data/npc/normal_npc/fun_data/question/", 
    			System.getProperty("user.dir") + "/" + "res/data/npc/normal_npc/fun_data/award/");
    	QuestionDict.getInstance().getAnswerQuestionData(1);
    }
    
    
    /**
     * 单例模式
     * 
     * @return 字典单例
     */
    public static QuestionDict getInstance ()
    {
        if (null == instance)
        {
            instance = new QuestionDict();
        }

        return instance;
    }
    
    /**
     * 改为题是否需要加载.
     * @param _questionID
     * @return
     */
    private boolean questionLoad(int _questionID)
    {
    	boolean result = false;
    	for (int i = 0; i < anwserQuestion.size(); i++) {
			if (anwserQuestion.get(i).id == _questionID) {
				result = true;
			}
		}
    	return result;
    }
    
    private void loadAward(String _dataPath)
    {
		File dataPath;
        try
        {
            dataPath = new File(_dataPath);
            File[] dataFileList = dataPath.listFiles();
            for (File dataFile : dataFileList)
            {
                if (!dataFile.getName().endsWith(".xml"))
                {
                    continue;
                }
                SAXReader reader = new SAXReader();
                Document document = reader.read(dataFile);
                Element rootE = document.getRootElement();
                Iterator<Element> rootIt = rootE.elementIterator();

                while (rootIt.hasNext())
                {
                	Element subE = rootIt.next();
                	if (null != subE)
                	{
                		AwardData awardData = new AwardData();
                		awardData.id = Integer.valueOf(subE.elementTextTrim("id"));
                		awardData.name = subE.elementTextTrim("name");
                		awardData.goodsSum = Integer.valueOf(subE.elementTextTrim("goodsSum"));
                		awardData.goodsMap = new HashMap<Integer, Goods>(awardData.goodsSum);
                		int point = 0, index = 0, goodsID = 0;
                		for (int i = 0; i < awardData.goodsSum; i++) 
                		{
                			index = i +1;
                			point = Integer.valueOf(subE.elementTextTrim("goods"+ index +"Point"));
                			goodsID = Integer.valueOf(subE.elementTextTrim("goods"+ index +"ID"));
                			Goods goods = GoodsContents.getGoods(goodsID);
                			if ( (!awardData.goodsMap.containsKey(point)) && goods != null ) 
                			{
                				awardData.goodsMap.put(point, goods);
							}
						}//end award data read for 
                		if (!this.awardDict.containsKey(awardData.id)) {
                			this.awardDict.put(awardData.id, awardData);
						}
                	}
                }
            }//end file read for
        }
        catch (Exception e) 
        {
			e.printStackTrace();
		}
    }
    
	private void loadQuestion (String _dataPath)
	{
		File dataPath;
        try
        {
            dataPath = new File(_dataPath);
            File[] dataFileList = dataPath.listFiles();
            for (File dataFile : dataFileList)
            {
                if (!dataFile.getName().endsWith(".xml"))
                {
                    continue;
                }
                SAXReader reader = new SAXReader();
                Document document = reader.read(dataFile);
                Element rootE = document.getRootElement();
                Iterator<Element> rootIt = rootE.elementIterator();

                while (rootIt.hasNext())
                {
                	Element subE = rootIt.next();
                	if (null != subE)
                	{
                		QuestionData questionData = new QuestionData();
                		questionData.aid = Integer.valueOf(subE.elementTextTrim("aid"));
                		questionData.answerQuestionID = Integer.valueOf(
                				subE.elementTextTrim("answerQuestionID"));
                		if (questionLoad(questionData.answerQuestionID)) 
                		{
                    		questionData.answerSum = Integer.valueOf(subE.elementTextTrim("answerSum"));
                    		questionData.answerList = new ArrayList<String>(questionData.answerSum);
                    		questionData.rightAnswer = Integer.valueOf(
                    				subE.elementTextTrim("rightAnswer"));
                    		questionData.question = subE.elementTextTrim("question");
                    		int index = 0;
                    		String answer = "";
                    		for (int i = 0; i < questionData.answerSum; i++) 
                    		{
                    			index = i +1;
                    			answer = subE.elementTextTrim("answer"+ index +"Content");
                    			if (answer != null) 
                    			{
                    				questionData.answerList.add(answer);
    							}
    						}//end answer data read for

                    		if( this.questionDict.containsKey(questionData.answerQuestionID) )
                    		{
                    			//从问题组当中取出该组问题,并对该组问题进行put
                    			this.questionDict.get(questionData.answerQuestionID).put(
                    					questionData.aid, questionData);
                    		}
                    		else 
                    		{
                    			//直接添加1个组以及该组第1个问题
                    			Hashtable<Integer, QuestionData> data = 
                    				new Hashtable<Integer, QuestionData>();
                    			data.put(questionData.aid, questionData);
                    			this.questionDict.put(questionData.answerQuestionID, data);
							}
						}
                	}
                }
            }//end file read for
        }
        catch (Exception e) 
        {
			e.printStackTrace();
		}
	}
	
	public void load (String _funPath, String _questionPath, String _awardPath)
	{
		loadAward(_awardPath);//最先加载奖励
		File dataPath;
        try
        {
            dataPath = new File(_funPath);
            File[] dataFileList = dataPath.listFiles();
            for (File dataFile : dataFileList)
            {
                if (!dataFile.getName().endsWith(".xml"))
                {
                    continue;
                }
                SAXReader reader = new SAXReader();
                Document document = reader.read(dataFile);
                Element rootE = document.getRootElement();
                Iterator<Element> rootIt = rootE.elementIterator();

                while (rootIt.hasNext())
                {
                	Element subE = rootIt.next();
                	if (null != subE)
                	{
                		AnswerQuestionData functionData = new AnswerQuestionData();
                		functionData.id = Integer.valueOf(subE.elementTextTrim("id"));
                		functionData.name = subE.elementTextTrim("name");
                		if (subE.elementTextTrim("isOpen").equals("是")) 
                		{
                			functionData.isOpen = true;
						}
                		else 
                		{
                			functionData.isOpen = false;
						}
                		if (functionData.isOpen) 
                		{
                			functionData.questionSum = Integer.valueOf(
                					subE.elementTextTrim("questionSum"));
                			functionData.point = Integer.valueOf(subE.elementTextTrim("point"));
                			functionData.awardID = Integer.valueOf(subE.elementTextTrim("awardID"));
                			functionData.award = this.awardDict.get(functionData.awardID);
                			functionData.refreshType = Integer.valueOf(
                					subE.elementTextTrim("refreshType"));
                			if (functionData.refreshType == REFRESH_TYPE_DAY) 
                			{
                				functionData.refreshDay = Integer.valueOf(
                						subE.elementTextTrim("refreshDay"));
							} 
                			else if (functionData.refreshType == REFRESH_TYPE_TIME) 
							{
                				functionData.refreshTimeSum = Integer.valueOf(
                						subE.elementTextTrim("refreshTimeSum"));
                				functionData.startTime = new String[functionData.refreshTimeSum];
                				functionData.endTime = new String[functionData.refreshTimeSum];
                				int index = 0;
                				for (int i = 0; i < functionData.refreshTimeSum; i++) 
                				{
                					index = i +1;
                					functionData.startTime[i] = subE.elementTextTrim(
                							"refresh"+ index +"StartTime");
                					functionData.endTime[i] = subE.elementTextTrim(
                							"refresh"+ index +"EndTime");
								}
							}
                			this.anwserQuestion.add(functionData);
						}
                	}
                }
            }//end for
        }
        catch (Exception e) 
        {
			e.printStackTrace();
		}
        
        loadQuestion(_questionPath); //最后加载问题
	}
	
	public final static int    REFRESH_TYPE_DAY = 1;
	
	public final static int    REFRESH_TYPE_TIME = 2;
	
//    public static class AnswerQuestionData
//    {
//        public int   id, questionSum, point, awardID, refreshType, refreshDay, refreshTimeSum;
//        
//        public boolean isOpen;
//
//        public String   name;
//        
//        public ArrayList<QuestionData> question; 
//        
//        public AwardData award;
//        
//        public String[] startTime;
//        
//        public String[] endTime;
//    }
    
    public static class AwardData
    {
        public int   id, goodsSum;
        //<需要分数, 奖励物品>
        public HashMap<Integer, Goods> goodsMap;

        public String   name;
    }
    
    public static class QuestionData
    {
        public int   aid, answerQuestionID, answerSum, rightAnswer;
        //<问题的答案>
        public ArrayList<String> answerList;

        public String   question;
    }

}
