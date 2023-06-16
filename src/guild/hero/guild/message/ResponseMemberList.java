package hero.guild.message;

import hero.guild.EGuildMemberRank;
import hero.guild.Guild;
import hero.guild.GuildMemberProxy;

import java.io.IOException;
import java.util.List;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseMemberList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-11 下午16:40:33
 * @描述 ：响应公会成员列表
 */

public class ResponseMemberList extends AbsResponseMessage
{
    /**
     * 当前页数
     */
    private int                    currentPageNumber;

    /**
     * 总页数
     */
    private int                    totalPageNumber;

    /**
     * 请求者在公会中的等级
     */
    private EGuildMemberRank       rankOfSelf;

    /**
     * 公会当前人数
     */
    private int                    memberNumber;

    /**
     * 公会可以接纳的最多人数
     */
    private int                    memberNumberUpLimit;

    /**
     * 成员列表
     */
    private List<GuildMemberProxy> currentPageMemberList;
    
    private Guild                  guild;
    
    private int[]                  moneyList;
    
    private int[]                  numberList;

    /**
     * 构造
     * 
     * @param _currentPageMemberList 当前页公会成员列表
     * @param _rankOfSelf 自己的公会等级（决定了客户端的操作选项）
     * @param _currentPageNumber 当前显示的第几页
     * @param _totalPageNumber 公会成员显示总页数
     * @param _memberNumber 公会当前人数
     * @param _memberNumberUpLimit 公会人数上限
     */
    public ResponseMemberList(List<GuildMemberProxy> _currentPageMemberList,
            EGuildMemberRank _rankOfSelf, int _currentPageNumber,
            int _totalPageNumber, int _memberNumber, int _memberNumberUpLimit, Guild _guild)
    {
        currentPageNumber = _currentPageNumber;
        memberNumber = _memberNumber;
        totalPageNumber = _totalPageNumber;
        rankOfSelf = _rankOfSelf;
        memberNumberUpLimit = _memberNumberUpLimit;
        currentPageMemberList = _currentPageMemberList;
        guild = _guild;
        moneyList = guild.getUpGuildMoneyList();
        numberList = guild.getUpGuildNumberList();
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
        // TODO Auto-generated method stub
    	//add by zhengl; date: 2011-03-20; note: 添加帮会信息.
    	yos.writeByte(moneyList.length);
    	for (int i = 0; i < moneyList.length; i++) {
			yos.writeInt(moneyList[i]);
		}
    	yos.writeByte(numberList.length);
    	for (int i = 0; i < numberList.length; i++) {
			yos.writeShort(numberList[i]);
		}
    	//end
    	yos.writeByte(guild.getGuildLevel());
        yos.writeByte(rankOfSelf.value());
        yos.writeShort(memberNumber);
        yos.writeShort(memberNumberUpLimit);
        yos.writeByte(currentPageNumber);
        yos.writeByte(totalPageNumber);

        yos.writeByte(currentPageMemberList.size());

        for (GuildMemberProxy memberProxy : currentPageMemberList)
        {
            yos.writeInt(memberProxy.userID);
            yos.writeUTF(memberProxy.name);
            yos.writeByte(memberProxy.memberRank.value());
            yos.writeByte(memberProxy.isOnline);

            if (memberProxy.isOnline)
            {
                yos.writeByte(memberProxy.vocation.value());
                yos.writeShort(memberProxy.level);
                yos.writeByte(memberProxy.sex.value());
            }
        }
    }
}
