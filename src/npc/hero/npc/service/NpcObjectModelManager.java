package hero.npc.service;

import hero.npc.Npc;
import javolution.util.FastList;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 NpcObjectModelManager.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-16 下午04:21:33
 * @描述 ：NPC对象加载器
 */

public class NpcObjectModelManager
{
    /**
     * 模板列表
     */
    private static FastList<Class<Npc>> modelClassList = new FastList<Class<Npc>>();

    /**
     * 构造
     */
    private NpcObjectModelManager()
    {

    }

    /**
     * 注册NPC模板
     * 
     * @param _classNameIncludePackageName NPC完整类名（包含包名）
     */
    @SuppressWarnings("unchecked")
    public static void registeNpcModel (String _classNameIncludePackageName)
    {
        try
        {
            Class<Npc> c = (Class<Npc>) Class
                    .forName(_classNameIncludePackageName);
            modelClassList.add(c);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 移除NPC模板
     * 
     * @param _classNameIncludePackageName NPC完整类名（包含包名）
     */
    public static void removeNpcModel (String _className)
    {
        try
        {
            for (Class<Npc> c : modelClassList)
            {
                if (c.getName().equals(_className)
                        || c.getName().equals(_className))
                {
                    modelClassList.remove(c);

                    return;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 创建NPC对象
     * 
     * @param _className NPC类名
     * @return
     */
    public static Object createObject (String _className)
    {
        try
        {
            for (Class<Npc> c : modelClassList)
            {
                if (c.getSimpleName().equals(_className)
                        || c.getName().equals(_className))
                {
                    return c.newInstance();
                }
            }

            return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return null;
        }
    }
}
