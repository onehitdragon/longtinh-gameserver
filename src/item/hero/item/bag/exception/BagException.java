package hero.item.bag.exception;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 PackageException.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-11-20 下午05:50:36
 * @描述 ：背包异常，对背包操作时发生的各种不正常现象的描述
 */

public class BagException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public BagException(String _message)
    {
        super(_message);
    }
}
