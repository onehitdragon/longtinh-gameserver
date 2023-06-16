package hero.ui;

import hero.player.HeroPlayer;
import java.io.IOException;

import yoyo.tools.YOYOOutputStream;

/**
 * 查看玩家属性
 * 
 * @author Luke 陈路
 * @date Jul 30, 2009
 */
public class UI_PlayerView
{
    private static final String ENTER = "\n";

    /**
     * 获得字节流
     * 
     * @param _id
     * @param _player
     * @return
     */
    public static byte[] getBytes (byte _id, HeroPlayer _player)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();
        StringBuffer str = new StringBuffer();
        str.append("昵称：");
        str.append(_player.getName());
        str.append(ENTER);
        str.append("职业：");
        str.append(_player.getVocation().getDesc());
        str.append(ENTER);
        str.append("等级：");
        str.append(_player.getLevel());
        str.append(ENTER);
        str.append("生命值：");
        str.append(_player.getActualProperty().getHpMax());
        str.append(ENTER);

        if (_player.getVocation().getType().getID() == 1)
        {
            str.append("力气值：50|50");
        }
        else
        {
            str.append("魔法值：");
            str.append(_player.getActualProperty().getMpMax());
        }
        str.append(ENTER);
        str.append("位置：");
        str.append(_player.where().getName());

        try
        {
            output.writeByte(_id);
            output.writeUTF(str.toString());
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

    public static byte[] getBytes (byte _id, String _name)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();
        try
        {
            StringBuffer str = new StringBuffer();
            str.append("昵称：");
            str.append(_name);
            str.append(ENTER);
            str.append(ENTER);
            str.append("当前不在线");

            output.writeByte(_id);
            output.writeUTF(str.toString());
            output.flush();
            return output.getBytes();
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
        return null;
    }

    /**
     * 获取UI类型
     * 
     * @return
     */
    public static EUIType getType ()
    {
        return EUIType.GROUP_VIEW;
    }
}
