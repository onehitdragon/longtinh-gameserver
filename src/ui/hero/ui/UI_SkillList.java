package hero.ui;

import hero.skill.Skill;

import java.io.IOException;
import java.util.ArrayList;

import yoyo.tools.YOYOOutputStream;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 UI_SkillList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-12 上午11:46:48
 * @描述 ：技能训练师处可见的技能列表
 */

public class UI_SkillList
{
    public static byte[] getBytes (String[] _menuList,
            ArrayList<Skill> _skillList)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());
            output.writeByte(_skillList.size());

            for (Skill skill : _skillList)
            {
                output.writeInt(skill.id);
                output.writeUTF(skill.name);
                output.writeByte(skill.level);
                output.writeShort(skill.iconID);
                output.writeUTF(skill.description);
                output.writeInt(skill.learnFreight);
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
        return EUIType.SKILL_LIST;
    }
}
