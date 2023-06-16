package hero.item.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


public class EnhanceAnswer extends AbsResponseMessage  {
	
	private byte answerType;
	
	public static final byte ANSWER_TYPE_RIGHT = 1;
	
	public static final byte ANSWER_TYPE_WRONG = 0;
	
	public EnhanceAnswer(byte _answerType) {
		answerType = _answerType;
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void write() throws IOException {
		// TODO Auto-generated method stub
		yos.writeByte(answerType);
	}

}
