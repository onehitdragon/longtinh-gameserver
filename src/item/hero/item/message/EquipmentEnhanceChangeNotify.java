package hero.item.message;

import hero.item.detail.EBodyPartOfEquipment;

import java.io.IOException;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 EquipmentEnhanceChangeNotify.java
 * @创建者 Lulin
 * @版本 1.0
 * @时间 2009-10-12 下午04:51:09
 * @描述 ：装备强化变化通知（各个部位的强化等级，只有当强化成功或强化失败时产生次报文）
 */

public class EquipmentEnhanceChangeNotify extends AbsResponseMessage
{
     private static Logger log = Logger.getLogger(EquipmentEnhanceChangeNotify.class);
    /**
     * 装备在装备栏中部位索引
     */
    private byte   bodypartIndex;

    /**
     * 装备实例编号
     */
    private int    equipmentInsID;

    /**
     * 强化数据
     * <p>
     * 12个细节{0=未打孔,0=未镶嵌}
     */
    private byte[][] enhanceData;
    /**装备发光图片ID*/
    private short flashPNG;
    /**装备发光动画ID*/
    private short flashANU;
    /**被强化装备的部位*/
    private byte body;
    /**被改变的数值*/
    private String changeValue;
    
    private byte isDamage;

    /**
     * 构造
     * <p>
     * old
     * 
     * @param _bodypartIndex 装备在装备栏中部位索引
     * @param _equipmentInsID 装备实例编号
     * @param _enhanceData 强化数据
     */
//    public EquipmentEnhanceChangeNotify(byte _bodypartIndex,
//            int _equipmentInsID, byte[][] _enhanceData, short _flashPNG, short _flashANU)
//    {
//        bodypartIndex = _bodypartIndex;
//        equipmentInsID = _equipmentInsID;
//        enhanceData = _enhanceData;
//        flashPNG = _flashPNG;
//        flashANU = _flashANU;
//    }
    
    public EquipmentEnhanceChangeNotify(int _equipmentInsID, byte[][] _enhanceData, 
    		short _flashPNG, short _flashANU, int _body, String _changeValue)
    {
        equipmentInsID = _equipmentInsID;
        enhanceData = _enhanceData;
        flashPNG = _flashPNG;
        flashANU = _flashANU;
        body = (byte)_body;
        changeValue = _changeValue;
        isDamage = 0; //默认未被摧毁.
    }
    
    public EquipmentEnhanceChangeNotify(int _equipmentInsID, byte[][] _enhanceData, 
    		short _flashPNG, short _flashANU, int _body, String _changeValue, byte _isDamage)
    {
        equipmentInsID = _equipmentInsID;
        enhanceData = _enhanceData;
        flashPNG = _flashPNG;
        flashANU = _flashANU;
        body = (byte)_body;
        changeValue = _changeValue;
        isDamage = _isDamage;
    }

    @Override
    public int getPriority ()
    {
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
    	//edit:	zhengl
    	//date:	2011-03-08
    	//note:	强化数据下发逻辑变更
//        output.writeByte(bodypartIndex); //不再需要装备到身上.
    	yos.writeByte(isDamage); //0=未因强化失败而摧毁; 1=因强化失败而摧毁
        yos.writeInt(equipmentInsID);
        yos.writeByte(body);
    	yos.writeShort(flashPNG);
    	yos.writeShort(flashANU);
    	yos.writeUTF(changeValue);
    	log.info("changeValue-->" + changeValue);
        yos.writeByte(enhanceData.length);
        for (int i = 0; i < enhanceData.length; i++) {
			if(enhanceData[i][0] == 1){
				//0,0,1,2,3 = 0,1,2,3,4 = 未镶嵌,空孔,碎,正常,闪光
				yos.writeByte(enhanceData[i][1] +1);
			} else {
				yos.writeByte(0);
			}
		}
    }
}
