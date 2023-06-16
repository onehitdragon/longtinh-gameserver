package hero.gm;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 NewQuestion.java
 * @创建者 ChenYaMeng
 * @版本 1.0
 * @时间 2010-7-6 下午01:55:51
 * @描述 ：参数封装类 - 玩家提出一个新问题
 */

public class ParamNewQuestion
{
    public String nickname;

    public byte   type;

    public String info;

    public ParamNewQuestion(String _nickname, byte _type, String _info)
    {
        nickname = _nickname;
        type = _type;
        info = _info;
    }
}
