package hero.ui;

import hero.item.Goods;
import hero.npc.function.system.postbox.Mail;
import hero.player.define.ESex;
import hero.share.service.DateFormatter;
import hero.share.service.Tip;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import yoyo.tools.YOYOOutputStream;


public class UI_MailGoodsList
{
    private static final short MONEY_ICON = 245;

    private static final short POINT_ICON = 259;

    /**
     * 获取UI绘制数据字节数组
     * 
     * @return
     */
    public static byte[] getBytes (short _pageNum, List<Mail> _mailList,
            String[] _menuList)
    {
        YOYOOutputStream output = new YOYOOutputStream();

        try
        {
            output.writeByte(getType().getID());
            output.writeShort(_pageNum);

            if (null == _mailList)
            {
                output.writeByte(0);
            }
            else
            {
                output.writeByte(_mailList.size());

                for (Mail _mail : _mailList)
                {
                    output.writeInt(_mail.getID());
                    output.writeUTF(_mail.getSender());
                    //add by zhengl; date: 2011-01-27; note: 邮件功能合二为一.
                    output.writeUTF(_mail.getTitle());
                    output.writeUTF(_mail.getContent());
                    output.writeByte(_mail.getSocial()); //0好友、1工会、2系统公告、3其他玩家
                    output.writeUTF(DateFormatter.getStringTime("yyyy-MM-dd HH:mm", 
                    		_mail.getDate()) );//date
                    if (_mail.getReadFinish()) {
                    	output.writeByte(1); //已读
					} else {
						output.writeByte(0); //未读
					}
                    //end
                    if (_mail.getType() == Mail.TYPE_OF_TXT) {
                        output.writeShort(-1);
                        output.writeUTF("文本");
                        output.writeByte(0);
                        output.writeShort(1);
					}
                    else if (_mail.getType() == Mail.TYPE_OF_MONTY)
                    {
                        output.writeShort(MONEY_ICON);
                        output.writeUTF(_mail.getMoney() + Tip.FUNCTION_UI_DESC_OF_MONEY);
                        output.writeByte(0);
                        output.writeShort(1);
                    }
                    else if (_mail.getType() == Mail.TYPE_OF_TXT) {
						
					}
                    else if (_mail.getType() == Mail.TYPE_OF_GAME_POINT)
                    {
                        output.writeShort(POINT_ICON);
                        output.writeUTF(_mail.getGamePoint() + Tip.FUNCTION_UI_DESC_OF_GAME_POINT);
                        output.writeByte(0);
                        output.writeShort(1);
                    }
                    else if (_mail.getType() == Mail.TYPE_OF_SINGLE_GOODS)
                    {
                        output.writeShort(_mail.getSingleGoods().getIconID());
                        output.writeUTF(_mail.getSingleGoods().getName());
                        output.writeByte(_mail.getSingleGoods().getTrait().value());
                        output.writeShort(_mail.getSingleGoodsNumber());
                    }
                    else
                    {
                        Goods _sg = _mail.getEquipment().getArchetype();
                        output.writeShort(_sg.getIconID());
                        output.writeUTF(_sg.getName());
                        output.writeByte(_sg.getTrait().value());
                        output.writeShort(_mail.getSingleGoodsNumber());
                    }
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
        return EUIType.MAIL_GOODS_LIST;
    }




}
