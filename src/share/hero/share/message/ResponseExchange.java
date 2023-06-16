package hero.share.message;

import hero.item.EquipmentInstance;
import hero.item.Weapon;
import hero.item.detail.EGoodsTrait;
import hero.share.exchange.Exchange;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


public class ResponseExchange extends AbsResponseMessage
{

    private byte              type;

    private int               exchangeID;

    private String            nickname;

    private int               money;

    private String            goodsName;

    private short             goodsIcon;

    private short             goodsNum;

    private EGoodsTrait       trait;

    private String            goodsDes;

    private boolean           isEquipment;

    private EquipmentInstance instance;
    
    private short gridIndex;

    public ResponseExchange(int _exchangeID, String _nickname)
    {
        type = Exchange.BEGIN;
        exchangeID = _exchangeID;
        nickname = _nickname;
    }

    public ResponseExchange(int _money)
    {
        type = Exchange.ADD_MONEY;
        money = _money;
    }

    public ResponseExchange(String _goodsName, short _goodsIcon,
            short _goodsNum, EGoodsTrait _goodsTrait, String _goodsDes)
    {
        type = Exchange.ADD_GOODS;
        isEquipment = false;
        goodsName = _goodsName;
        goodsIcon = _goodsIcon;
        goodsNum = _goodsNum;
        trait = _goodsTrait;
        goodsDes = _goodsDes;
    }

    public ResponseExchange(EquipmentInstance _instance)
    {
        type = Exchange.ADD_GOODS;
        isEquipment = true;
        instance = _instance;
    }

    public ResponseExchange(byte _type)
    {
        type = _type;
    }
    
    public ResponseExchange(byte _type,short _gridIndex)
    {
        type = _type;
        this.gridIndex = _gridIndex;
    }

    @Override
    public int getPriority ()
    {
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
//log.info("response exchange type = " + type);
        yos.writeByte(type);

        switch (type)
        {
            case Exchange.BEGIN:
            {
                yos.writeUTF(nickname);
                yos.writeInt(exchangeID);

                break;
            }
            case Exchange.ADD_MONEY:
            {
                yos.writeInt(money);

                break;
            }
            case Exchange.ADD_GOODS:
            {
                if (isEquipment)
                {
                    yos.writeByte(1);
                    yos.writeShort(instance.getArchetype().getIconID());// 物品图标
                    //edit by zhengl; date: 2011-03-13; note: 物品名称下发
                    StringBuffer name = new StringBuffer();
                    name.append(instance.getArchetype().getName());
                    int level = instance.getGeneralEnhance().getLevel();
                    if(level > 0)
                    {
                    	name.append("+");
                    	name.append(level);
                    }
                    int flash = instance.getGeneralEnhance().getFlash();
                    if(flash > 0)
                    {
                    	name.append("(闪");
                    	name.append(flash);
                    	name.append(")");
                    }
                    yos.writeUTF(name.toString());// 物品名称
                    yos.writeShort(1);// 物品数量

                    if (instance.getArchetype() instanceof Weapon)
                    {
                        yos.writeByte(1);// 武器
                        yos.writeBytes(instance.getArchetype()
                                .getFixPropertyBytes());
                        yos.writeByte(instance.isBind());

                        if (instance.getArchetype().existSeal())
                        {
                            yos.writeByte(true);
                        }
                        else
                        {
                            yos.writeByte(false);
                        }

                        yos.writeShort(instance.getCurrentDurabilityPoint());
                        yos.writeInt(instance.getArchetype()
                                .getRetrievePrice());
                        
                        //edit by zhengl; date: 2011-03-13; note: 新强化信息下发
                        yos.writeUTF(instance.getGeneralEnhance().getUpEndString());
						yos.writeShort(instance.getGeneralEnhance().getFlashView()[0]);
						yos.writeShort(instance.getGeneralEnhance().getFlashView()[1]);
                        yos.writeByte(instance.getGeneralEnhance().detail.length);
                        for (int j = 0; j < instance.getGeneralEnhance().detail.length; j++) {
                			if(instance.getGeneralEnhance().detail[j][0] == 1){
                				//0,0,1,2,3 = 0,1,2,3,4 = 未镶嵌,空孔,碎,正常,闪光
                				yos.writeByte(instance.getGeneralEnhance().detail[j][1] +1);
                			} else {
                				yos.writeByte(0);
                			}
                		}
                        //end
                    }
                    else
                    {
                        yos.writeByte(2);// 防具
                        yos.writeBytes(instance.getArchetype()
                                .getFixPropertyBytes());
                        yos.writeByte(instance.isBind());
                        yos.writeByte(instance.existSeal());
                        yos.writeShort(instance.getCurrentDurabilityPoint());
                        yos.writeInt(instance.getArchetype()
                                .getRetrievePrice());
                        //edit by zhengl; date: 2011-03-13; note: 新强化信息下发
                        yos.writeUTF(instance.getGeneralEnhance().getUpEndString());
						yos.writeShort(instance.getGeneralEnhance().getArmorFlashView()[0]);
						yos.writeShort(instance.getGeneralEnhance().getArmorFlashView()[1]);
                        yos.writeByte(instance.getGeneralEnhance().detail.length);
                        for (int j = 0; j < instance.getGeneralEnhance().detail.length; j++) {
                			if(instance.getGeneralEnhance().detail[j][0] == 1){
                				//0,0,1,2,3 = 0,1,2,3,4 = 未镶嵌,空孔,碎,正常,闪光
                				yos.writeByte(instance.getGeneralEnhance().detail[j][1] +1);
                			} else {
                				yos.writeByte(0);
                			}
                		}
                        //end
                    }
                }
                else
                {
                    yos.writeByte(0);
                    yos.writeUTF(goodsName);
                    yos.writeShort(goodsIcon);
                    yos.writeShort(goodsNum);
                    yos.writeByte(trait.value());
                    yos.writeUTF(goodsDes);
                }

                break;
            }
            case Exchange.CONFIM:
            {

                break;
            }
            case Exchange.EXCHANGE_CANCEL:
            {

                break;
            }
            case Exchange.EXCHANGE_LOCK:
            {
//log.info("response EXCHANGE_LOCK = " + Exchange.EXCHANGE_LOCK);
                break;
            }
            case Exchange.REMOVE_GOODS:
            {
            	yos.writeByte(-1);
                break;
            }
            case Exchange.REMOVE_SINGLE_GOODS:
            {
            	yos.writeShort(gridIndex);
            	break;
            }
        }
    }

}
