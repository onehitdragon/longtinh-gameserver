package hero.pet.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * 上下马的状态通知
 * @author Administrator
 * 
 *
 */
public class MountStateNotify extends AbsResponseMessage {

	private int      userID;
	
	private byte     stateMount;
	
	private short    pngMount;
	
	private short    anuMount;
	
	private String   name;
	
	private short    level;
	
	public static final byte MOUNT_STATE_UP = 0;
	
	public static final byte MOUNT_STATE_DOWN = 1;
	
	/**
	 * 上下马的状态通知
	 * @param _stateMount
	 * @param _pngMount
	 * @param _anuMount
	 */
	public MountStateNotify (int _userID, byte _stateMount, short _pngMount, short _anuMount, 
			String _name, int _level) {
		stateMount = _stateMount;
		pngMount = _pngMount;
		anuMount = _anuMount;
		userID = _userID;
		name = _name;
		level = (short)_level;
	}
	
	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void write() throws IOException {
		// TODO Auto-generated method stub
		yos.writeInt(userID);//主人ID
		yos.writeUTF(name);//马匹名字
		yos.writeShort(level);//马匹等级
		yos.writeByte(stateMount);//上/下马
		yos.writeShort(pngMount);//坐骑图片
		yos.writeShort(anuMount);//坐骑动画
	}

}
