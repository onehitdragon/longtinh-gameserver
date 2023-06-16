package hero.gm.service;

import javolution.util.FastMap;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 GMQuestionManager.java
 * @创建者 ChenYaMeng
 * @版本 1.0
 * @时间 2010-6-11 上午13:50:20
 * @描述 ：玩家提交给GM的问题管理类
 */
public class GmQuestionManager
{
    /**
     * 玩家的userID对应的问题ID
     */
    private FastMap<Integer, Integer>  container;

    /**
     * 唯一实例
     */
    private static GmQuestionManager instance;

    /**
     * 私有构造
     */
    private GmQuestionManager()
    {
        container = new FastMap<Integer, Integer>();
    }

    /**
     * 获取唯一实例
     * 
     * @return
     */
    public static GmQuestionManager getInstance ()
    {
        if (null == instance)
        {
            instance = new GmQuestionManager();
        }

        return instance;
    }

    /**
     * 根据玩家的userID查找问题ID
     * 
     * @param _userID
     * @return
     */
    public int getQuestionID (int _userID)
    {
        Integer questionID = container.get(_userID);

        if (questionID == null)
        {
            return 0;
        }

        return questionID;
    }

    /**
     * 添加问题
     * 
     * @param _userID
     * @param _questionID
     */
    public void putQuestion (int _userID, int _questionID)
    {
        container.put(_userID, _questionID);
    }

    /**
     * 删除问题
     * 
     * @param _userID
     */
    public void delQuestion (int _userID)
    {
        container.remove(_userID);
    }
}
