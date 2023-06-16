package hero.gm.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 用来测试上下行时间
 * @author jiaodongjie
 *0x3c06
 */
public class TestResponseUpAndDownTime extends AbsResponseMessage
{
    private int key;//每次上行的标识
    private long uptime;    //上行时间，由客户端上发
    private long upsubtime;//上行消耗时间

    public TestResponseUpAndDownTime(int _key,long _uptime,long _upsubtime){
        this.uptime = _uptime;
        this.key = _key;
        this.upsubtime = _upsubtime;
    }

	@Override
	public int getPriority ()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void write () throws IOException
	{
        yos.writeInt(key);
		yos.writeLong(uptime);
        yos.writeLong(upsubtime);
        yos.writeLong(System.currentTimeMillis());
        yos.writeUTF("这是测试时间的报文。。。");
	}

}
