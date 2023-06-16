package hero.ui;

import hero.item.bag.EquipmentContainer;
import hero.item.bag.SingleGoodsBag;
import hero.item.expand.ExpandGoods;
import hero.item.service.GoodsServiceImpl;
import hero.ui.data.EquipmentListData;
import hero.ui.data.EquipmentPackageData;
import hero.ui.data.SingleGoodsListData;
import hero.ui.data.SingleGoodsPackageData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import yoyo.tools.YOYOOutputStream;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 UI_SingleGoodsList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-3-2 下午02:50:38
 * @描述 ：
 */

public class UI_GoodsListWithOperation
{

    public static byte[] getBytes (String[] _menuList,
            ArrayList<byte[]>[] _followOptionData,
            SingleGoodsBag _singleGoodsPackage, String _tabName)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());
            output.writeBytes(SingleGoodsPackageData.getData(
                    _singleGoodsPackage, false, null, 
                    _tabName));

            output.writeByte(_menuList.length);

            for (int i = 0; i < _menuList.length; i++)
            {
                String menu = _menuList[i];

                output.writeUTF(menu);

                if (null != _followOptionData && null != _followOptionData[i])
                {
                    output.writeByte(_followOptionData[i].size());
                    for (byte[] _data : _followOptionData[i])
                    {
                        output.writeBytes(_data);
                    }
                }
                else
                {
                    output.writeByte(0);
                }
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
     * 非装备数据
     * @param _menuList
     * @param _followOptionData
     * @param _singleGoodsPackage
     * @return
     */
    public static byte[] getStorageBytes (String[] _menuList, SingleGoodsBag _singleGoodsPackage, 
    		String _tabName)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());
            output.writeBytes(SingleGoodsPackageData.getData(
                    _singleGoodsPackage, false, null, _tabName));

            output.writeByte(_menuList.length);

            for (int i = 0; i < _menuList.length; i++)
            {
                String menu = _menuList[i];

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

    public static byte[] getData (String[] _menuList,
            ArrayList<byte[]>[] _followOptionData,
            EquipmentContainer _equipmentPackage, String _tabName)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());
            output.writeBytes(EquipmentPackageData.getData(_equipmentPackage, _tabName));

            output.writeByte(_menuList.length);

            for (int i = 0; i < _menuList.length; i++)
            {
                String menu = _menuList[i];
                output.writeUTF(menu);
                if (null != _followOptionData && null != _followOptionData[i])
                {
                    output.writeByte(_followOptionData[i].size());
                    for (byte[] _data : _followOptionData[i])
                    {
                        output.writeBytes(_data);
                    }
                }
                else
                {
                    output.writeByte(0);
                }
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

    public static byte[] getStorageData (String[] _menuList, EquipmentContainer _equipmentPackage, 
    		String _tabName)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());
            output.writeBytes(EquipmentPackageData.getData(_equipmentPackage, _tabName));
            output.writeByte(_menuList.length);

            for (int i = 0; i < _menuList.length; i++)
            {
                String menu = _menuList[i];
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

    public static byte[] getData (String[] _menuList,
            EquipmentContainer _equipmentPackage, String _tabName)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());
            output.writeBytes(EquipmentPackageData.getData(_equipmentPackage, _tabName));

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

    public static byte[] getBytes (String[] _menuList,
    		Hashtable<String, ArrayList<ExpandGoods>> _equipmentList, int _gridNumsOfExsitsGoods)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID()); // 6
            output.writeBytes(EquipmentListData.getData(_equipmentList, _gridNumsOfExsitsGoods));
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
        }

        return null;
    }

    public static byte[] getBytes (String[] _menuList,
            ArrayList<byte[]>[] _followOptionData,
            Hashtable<String, ArrayList<ExpandGoods>> _singleGoodsList, int _traceGoodsType)
    {
        // TODO Auto-generated method stub
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());  //客户端read的第5个数据
            output.writeBytes(SingleGoodsListData.getData(_singleGoodsList, _traceGoodsType));
            output.writeByte(_menuList.length);

            for (int i = 0; i < _menuList.length; i++)
            {
                String menu = _menuList[i];

                output.writeUTF(menu);

                if (null != _followOptionData && null != _followOptionData[i])
                {
                    output.writeByte(_followOptionData[i].size());

                    for (byte[] _data : _followOptionData[i])
                    {
                        output.writeBytes(_data);
                    }
                }
                else
                {
                    output.writeByte(0);
                }
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
        return EUIType.GOODS_OPERATE_LIST;
    }
}
