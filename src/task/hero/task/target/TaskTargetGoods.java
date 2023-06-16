package hero.task.target;

import hero.item.SingleGoods;
import hero.share.service.Tip;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 TaskTargetGoods.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-9 下午03:52:31
 * @描述 ：获取物品类型
 */

public class TaskTargetGoods extends BaseTaskTarget implements
        INumberTypeTarget
{
    /**
     * 任务需要的物品
     */
    public SingleGoods          goods;

    /**
     * 数量
     */
    public short                number;

    public TaskTargetGoods(int _ID, SingleGoods _goods, short _number)
    {
        super(_ID);
        goods = _goods;
        number = _number;
        description = goods.getName();
    }

    @Override
    public ETastTargetType getType ()
    {
        // TODO Auto-generated method stub
        return ETastTargetType.GOODS;
    }

    @Override
    public boolean isCompleted ()
    {
        // TODO Auto-generated method stub
        return currentNumber >= number ? true : false;
    }

    @Override
    public String getDescripiton ()
    {
        // TODO Auto-generated method stub
        return new StringBuffer(description).append(Tip.SPACE)
                .append(currentNumber).append(Tip.SEPATATOR).append(number)
                .toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see hero.task.target.INumberTypeTarget#numberChanged(int)
     */
    public void numberChanged (int _changeNumber)
    {
        // TODO Auto-generated method stub
        currentNumber += _changeNumber;

        if (currentNumber >= number)
        {
            currentNumber = number;
        }
        else if (currentNumber < 0)
        {
            currentNumber = 0;
        }
    }

    @Override
    public BaseTaskTarget clone () throws CloneNotSupportedException
    {
        // TODO Auto-generated method stub
        return super.clone();
    }

    /**
     * @param _number
     */
    public void setCurrentNumber (short _number)
    {
        // TODO Auto-generated method stub
        currentNumber = _number;

        if (currentNumber > number)
        {
            currentNumber = number;
        }
    }

    @Override
    public void complete ()
    {
        // TODO Auto-generated method stub
        currentNumber = number;
    }

    @Override
    public boolean canTransmit ()
    {
        // TODO Auto-generated method stub
        return null == transmitMapInfo ? false : true;
    }
}
