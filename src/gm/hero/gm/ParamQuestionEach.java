package hero.gm;

/**<p>
 *  Copyright: DGFun CO., (c) 2008
 *  </p>
 *  @文件   QuestionEach.java
 *  @创建者  ChenYaMeng
 *  @版本    1.0
 *  @时间   2010-7-6 下午01:56:21
 *  @描述 ：参数封装类 - 玩家和GM对问题进行交互
 **/

public class ParamQuestionEach
{
    public int    sid;

    public int    id;

    public String content;

    public ParamQuestionEach(int _sid, int _id, String _content)
    {
        sid = _sid;
        id = _id;
        content = _content;
    }
}


