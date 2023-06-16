package hero.gm;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 QuestionAppraise.java
 * @创建者 ChenYaMeng
 * @版本 1.0
 * @时间 2010-7-6 下午01:57:13
 * @描述 ：参数封装类 - 玩家对GM回答进行评价
 */

public class ParamQuestionAppraise
{
    public int  id;

    public byte appraise;

    public ParamQuestionAppraise(int _id, byte _appraise)
    {
        id = _id;
        appraise = _appraise;
    }
}
