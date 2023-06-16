package hero.item.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


public class UpgradeBagAnswer extends AbsResponseMessage {
	public static final byte SEND_TO_CHARGE = 0;
	private String tip;
	private byte type;
	
	public UpgradeBagAnswer(String _tip, int _type) {
		tip = _tip;
		type = (byte)_type;
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void write() throws IOException {
		// TODO Auto-generated method stub
		yos.writeByte(type);
		yos.writeUTF(tip);
	}

}
