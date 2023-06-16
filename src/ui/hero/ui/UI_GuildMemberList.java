package hero.ui;

import hero.guild.Guild;
import hero.guild.GuildMemberProxy;

import java.io.IOException;
import java.util.List;

import yoyo.tools.YOYOOutputStream;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 UI_GuildMemberList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-11 上午11:00:45
 * @描述 ：公会成员列表
 */

public class UI_GuildMemberList
{
    /**
     * @param _menuList
     * @param _list
     * @param _guildMemberNumber 公会人数
     * @param _maxMemberNumber 公会人数上限
     * @param _currentPageNumber 当前页数
     * @param _totalPageNumber 总共页数
     * @return
     */
    public static byte[] getBytes (String[] _menuList,
            List<GuildMemberProxy> _list, int _guildMemberNumber,
            int _maxMemberNumber, int _currentPageNumber, int _totalPageNumber)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());

            output.writeShort(_guildMemberNumber);
            output.writeShort(_maxMemberNumber);
            output.writeByte(_currentPageNumber);
            output.writeByte(_totalPageNumber);
            output.writeByte(_list.size());

            GuildMemberProxy guildMemberProxy;

            for (byte i = 0; i < _list.size(); i++)
            {
                guildMemberProxy = _list.get(i);

                output.writeInt(guildMemberProxy.userID);
                output.writeUTF(guildMemberProxy.name);
                output.writeByte(guildMemberProxy.memberRank.value());
                output.writeByte(guildMemberProxy.isOnline);

                if (guildMemberProxy.isOnline)
                {
                    output.writeByte(guildMemberProxy.vocation.value());
                    output.writeShort(guildMemberProxy.level);
                    output.writeByte(guildMemberProxy.sex.value());
                }
            }

            output.writeByte(_menuList.length);

            for (String menu : _menuList)
            {
                output.writeUTF(menu);
                output.writeByte(0);
            }

            output.flush();

            return output.getBytes();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            try
            {
                output.close();
            }
            catch (IOException e)
            {
            }
            output = null;
        }
    }

    /**
     * 获取UI类型
     * 
     * @return
     */
    public static EUIType getType ()
    {
        return EUIType.GUILD_LIST;
    }
}
