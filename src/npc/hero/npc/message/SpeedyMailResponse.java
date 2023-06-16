package hero.npc.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


public class SpeedyMailResponse extends AbsResponseMessage{
	private byte isUse;
	public SpeedyMailResponse(byte _isUse) {
		
	}
	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	protected void write() throws IOException {
		yos.writeByte(isUse);
		
	}

}
