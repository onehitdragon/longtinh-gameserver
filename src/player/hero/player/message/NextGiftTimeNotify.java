package hero.player.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 下一个礼包领取时间通知
 * @author zhengl
 *
 */
public class NextGiftTimeNotify extends AbsResponseMessage {
	
	private String			name;
	
	private int				time;
	
	private String			content;
	
	private short			icon; 
	
	public NextGiftTimeNotify (int _time, String _name, String _content, int _icon)
	{
		name = _name;
		time = _time;
		content = _content;
		icon = (short)_icon;
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void write() throws IOException {
		// TODO Auto-generated method stub
		yos.writeInt(time * 60); //下发下一个礼包的时间 (秒)
		yos.writeUTF(""); //礼包名字
		yos.writeUTF(""); //礼包内容
		yos.writeShort(icon);
	}

}
