package hero.guild.message;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


public class GuildUpNotify extends AbsResponseMessage {
	
	private int guildLevel;
	
	private int guildSize;

	public GuildUpNotify (int _guildLevel, int _guildSize)
	{
		guildLevel = _guildLevel;
		guildSize = _guildSize;
	}
	
	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void write() throws IOException {
		yos.writeByte(guildLevel);
		yos.writeShort(guildSize);
		
	}

}
