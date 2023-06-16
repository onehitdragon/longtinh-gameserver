package hero.skill.detail;


/**<p>
 *  Copyright: DGFun CO., (c) 2008
 *  </p>
 *  @文件   EMathCaluOperator.java
 *  @创建者  DingChu
 *  @版本    1.0
 *  @时间   2010-4-27 上午10:42:22
 *  @描述 ：
 **/

public enum EMathCaluOperator
{
    MUL("乘"), DIV("除"), ADD("加"), DEC("减");

    String desc;

    EMathCaluOperator(String _desc)
    {
        desc = _desc;
    }

    public static EMathCaluOperator get (String _desc)
    {
        for (EMathCaluOperator operator : EMathCaluOperator.values())
        {
            if (operator.desc.equals(_desc))
            {
                return operator;
            }
        }

        return null;
    }
    
    /**
     * 获取与指定的操作符相反操作符
     * @param operator
     * @return
     */
    public static EMathCaluOperator getReverseCaluOperator(EMathCaluOperator operator){
    	if(operator != null)
        	switch(operator){
        		case MUL:{
        			return DIV;
        		}
        		case DIV:{
        			return MUL;
        		}
        		case ADD:{
        			return DEC;
        		}
        		case DEC:{
        			return ADD;
        		}
        	}
    	return null;
    }
}


