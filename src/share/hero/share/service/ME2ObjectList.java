package hero.share.service;

import hero.share.ME2GameObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ME2ObjectList extends ArrayList<ME2GameObject>
{
    /**
     * 
     */
    private static final long               serialVersionUID = 1L;

    /**
     * 临时ID与对象的映射关系
     */
    private HashMap<Integer, ME2GameObject> objectTable;

    public ME2ObjectList()
    {
        objectTable = new HashMap<Integer, ME2GameObject>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.ArrayList#add(java.lang.Object)
     */
    public synchronized boolean add (ME2GameObject _object)
    {
        if (null != _object && !contains(_object))
        {
            super.add(_object);
            objectTable.put(_object.getID(), _object);

            return true;
        }

        return false;
    }

    /**
     * 对象的ID被重置了
     * 
     * @param _object
     * @return
     */
    public int resetID (ME2GameObject _object)
    {
        int orgID = -1;

        if (_object != null && contains(_object))
        {
            Iterator<Map.Entry<Integer, ME2GameObject>> itr = objectTable
                    .entrySet().iterator();
            Map.Entry<Integer, ME2GameObject> entry;

            for (; itr.hasNext();)
            {
                entry = itr.next();

                if (entry.getValue() == _object)
                {
                    orgID = entry.getKey();
                    itr.remove();

                    break;
                }
            }

            objectTable.put(_object.getID(), _object);
        }

        return orgID;
    }

    /**
     * 从列表中移除
     * 
     * @param _role
     * @return
     */
    public synchronized boolean remove (ME2GameObject _role)
    {
        if (_role != null)
        {
            objectTable.remove(_role.getID());
        }

        return super.remove(_role);
    }

    /**
     * 从列表中移除
     * 
     * @param _objectName 对象名称
     * @return
     */
    public synchronized ME2GameObject remove (String _objectName)
    {
        if (_objectName != null)
        {
            for (int i = 0; i < super.size(); i++)
            {
                ME2GameObject object = super.get(i);

                if (object.getName().equals(_objectName))
                {
                    super.remove(i);
                    objectTable.remove(object.getID());

                    return object;
                }
            }
        }

        return null;
    }

    /**
     * 根据ID来返回当前地图对象，可以是玩家或NPC
     * 
     * @param id
     * @return
     */
    public ME2GameObject getObject (int _id)
    {
        return objectTable.get(_id);
    }
}
