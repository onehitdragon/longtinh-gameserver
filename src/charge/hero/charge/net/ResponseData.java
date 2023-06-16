package hero.charge.net;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseData.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-22 上午10:31:08
 * @描述 ：封装下行的所有报文数据的对象
 */
public class ResponseData
{
    private byte[] body;

    /**
     * 构造
     * 
     * @param body
     */
    public ResponseData(byte[] body)
    {
        this.body = body;
    }
    
    /**
     * 获取下行报文的数据体
     * 
     * @return
     */
    public byte[] getBody ()
    {
        return body;
    }
}
