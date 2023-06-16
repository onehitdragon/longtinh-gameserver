package hero.duel.message;

import hero.duel.service.DuelServiceImpl;

import java.io.IOException;

import yoyo.core.packet.AbsResponseMessage;


/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseDuel.java
 * @创建者 Lulin
 * @版本 1.0
 * @时间 2008-12-30 上午11:41:36
 * @描述 ：决斗响应，包括：邀请通知、开始、结束
 */

public class ResponseDuel extends AbsResponseMessage
{
    /**
     * 等待确认中
     */
    private final static byte CONFIM              = 1;

    /**
     * 开始决斗
     */
    private final static byte BEGIN               = 2;

    /**
     * 结束决斗
     */
    private final static byte END                 = 3;

    /**
     * 正常结束
     */
    public final static byte  END_TYPE_OF_GENERIC = 0;

    /**
     * 一方离线结束
     */
    public final static byte  END_TYPE_OF_OFFLINE = 1;

    /**
     * 超时结束
     */
    public final static byte  END_TYPE_OF_TIMEOUT = 2;

    /**
     * 类型
     */
    private byte              type;

    /**
     * 目标临时对象ID
     */
    private int               objectID;

    /**
     * 目标昵称
     */
    private String            nickname;

    /**
     * 结束类型
     */
    private byte              endType;

    /**
     * 构造－决斗邀请
     * 
     * @param _objectID
     * @param _nickname
     */
    public ResponseDuel(int _objectID, String _nickname)
    {
        type = CONFIM;
        objectID = _objectID;
        nickname = _nickname;
    }

    /**
     * 构造－决斗开始
     * 
     * @param _objectID
     */
    public ResponseDuel(int _objectID)
    {
        type = BEGIN;
        objectID = _objectID;
    }

    /**
     * 构造－决斗结束
     * 
     * @param _endType
     */
    public ResponseDuel(byte _endType)
    {
        type = END;
        endType = _endType;
    }

    @Override
    public int getPriority ()
    {
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        yos.writeByte(type);

        switch (type)
        {
            case CONFIM:
            {
                yos.writeInt(objectID);
                yos.writeUTF(nickname);

                break;
            }
            case BEGIN:
            {
                yos.writeInt(objectID);
                //add by zhengl; date: 2011-04-18; note: 决斗时间间隔周期
                //刷新周期
                yos.writeByte(DuelServiceImpl.getInstance().getConfig().duel_time_alert_interval);
                //决斗开始倒计时
                yos.writeByte(DuelServiceImpl.getInstance().getConfig().duel_count_down);
                //决斗总时间
                yos.writeShort(DuelServiceImpl.getInstance().getConfig().duel_sum_time);
                break;
            }
            case END:
            {
                yos.writeByte(endType);

                break;
            }
        }
    }
}
