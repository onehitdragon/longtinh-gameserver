package hero.guild.message;

import hero.guild.Guild;
import hero.player.HeroPlayer;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


public class ResponseGuildInfo extends AbsResponseMessage {

	private Guild				guild;
	
	public ResponseGuildInfo(Guild _guild) {
		guild = _guild;
	}
	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void write() throws IOException {
		// TODO Auto-generated method stub
    	yos.writeUTF(guild.getName());
    	yos.writeUTF(guild.getPresident().name);
    	yos.writeByte(guild.getGuildLevel());
		yos.writeShort(guild.getMemberList().size());
	}

}
