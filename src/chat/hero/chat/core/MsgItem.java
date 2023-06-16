package hero.chat.core;

import hero.player.HeroPlayer;

public class MsgItem
{
    public byte       type;      // 消息类型

    public String     srcName;   // 发送者名字

    public String     destName;  // 接受者名字

    public HeroPlayer target;    // 接受者

    public String     content;   // 内容

    public short      clan;      // 阵营

    public int        groupID;   // 队伍ID

    public int        guildID;   // 公会ID
    
    public boolean 	  showMiddle = false; //是否显示在界面中央(出系统公告的地方),默认是false(底部)
}
