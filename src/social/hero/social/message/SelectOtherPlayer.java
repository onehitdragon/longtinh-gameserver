package hero.social.message;

import hero.chat.message.ChatResponse;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

import java.io.IOException;
import java.util.Random;

import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.queue.ResponseMessageQueue;

import javolution.util.FastList;


public class SelectOtherPlayer extends AbsResponseMessage {
	
	private byte type; //5=随机查询5个;4=按条件查询;6=按名字查询
	private String name;
	private byte sex;
	private byte vocation;
	private short level;
	private HeroPlayer who;
	//每个用到的地方都 new一次Random .这个东西以后有必要优化到 通用函数库里面
	private final static Random RANDOM = new Random();
	
	public SelectOtherPlayer(byte _type, byte _sex, byte _vocation, short _level, HeroPlayer _who) {
		type  = _type;
		sex = _sex;
		vocation = _vocation;
		level = _level;
		who = _who;
	}
	
	public SelectOtherPlayer(byte _type, String _name, HeroPlayer _who) {
		type  = _type;
		name = _name;
		who = _who;
	}
	
	public SelectOtherPlayer(byte _type, HeroPlayer _who) {
		type  = _type;
		who = _who;
	}
	
	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void write() throws IOException {
		// TODO Auto-generated method stub
        FastList<HeroPlayer> list = new FastList<HeroPlayer>();
        FastList<HeroPlayer> returnList = new FastList<HeroPlayer>();
        HeroPlayer player;
    	list = PlayerServiceImpl.getInstance().getPlayerListByClan(who.getClan());
    	int x = RANDOM.nextInt(list.size());
    	if(x + 5 > list.size()) {
    		x = list.size() - 5;
    		if(x < 0) {
    			x = 0;
    		}
    	}
        if(type == 5) {
        	for (int i = x; i < list.size(); i++) {
        		player = list.get(i);
        		returnList.add(player);
        		if(returnList.size() >= 5) {
        			break;
        		}
        	}
        	yos.writeByte(type);
        	
        } else if(type == 4) {
        	//type==2的情况下sex,vocation,level才有值.
        	for (int i = x; i < list.size(); i++) {
        		player = list.get(i);
        		//这个地方查询有可能少于五个玩家,但是,即便少于5个也符合需求,所以可以用这个逻辑
        		if(player.getSex().value() == sex 
        				&& player.getClan().getID() == vocation && player.getLevel() == level) {
        			returnList.add(player);
        		}
        		if(returnList.size() >= 5) {
        			break;
        		}
        	}
        	yos.writeByte(type);
		} else {
        	for (int i = x; i < list.size(); i++) {
        		player = list.get(i);
        		if(player.getName().indexOf(name) > -1) {
        			returnList.add(player);
        		}
        		if(returnList.size() >= 5) {
        			break;
        		}
        	}
        	yos.writeByte(type);
		}
        yos.writeByte(returnList.size());
        for (int i = 0; i < returnList.size(); i++) {
        	player = returnList.get(i);
			yos.writeUTF(player.getName());
			yos.writeShort(player.getLevel());
			yos.writeInt(player.getID());
		}
		
	}

}
