package hero.micro.teach.message;

import hero.micro.service.MicroServiceImpl;
import hero.micro.teach.MasterApprentice;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;

import java.io.IOException;

import hero.share.message.Warning;
import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.queue.ResponseMessageQueue;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ResponseM_AList.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-10 下午02:41:04
 * @描述 ：
 */

public class ResponseMAList extends AbsResponseMessage
{
    private static Logger log = Logger.getLogger(ResponseMAList.class);
    /**
     * 师徒列表
     */
    private MasterApprentice masterApprenticeList;
    /**\
     * 当前玩家自己的USERID
     */
    private int selfID;

    /**
     * 是否是徒弟
     */
    private boolean isApprentice = false;

    /**
     * 构造
     * 
     * @param _masterApprenticeList
     */
    public ResponseMAList(MasterApprentice _masterApprenticeList, int _selfID)
    {
        masterApprenticeList = _masterApprenticeList;
        this.selfID = _selfID;
    }

    @Override
    public int getPriority ()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void write () throws IOException
    {
        MasterApprentice apprenticeMasterList = null;
        // TODO Auto-generated method stub
        if (null == masterApprenticeList){ //徒弟离开师傅后 masterUserID = 0,就没有师徒关系了
            yos.writeByte(0);
        }
        else
        {
            int number = 0;

            if (masterApprenticeList.masterUserID > 0)
            {
                number++;
            }

            number += masterApprenticeList.apprenticeNumber;

            if(masterApprenticeList.apprenticeNumber == 0 && masterApprenticeList.masterUserID>0){//玩家是徒弟，但也要把师傅的所有徒弟(不包含自己)显示出来
                isApprentice = true;
                apprenticeMasterList = MicroServiceImpl.getInstance()
                                    .getMasterApprentice(masterApprenticeList.masterUserID);
                if(apprenticeMasterList != null){
                    log.debug("徒弟的师徒列表 apprenticeMasterList apprenticeNumber = " + apprenticeMasterList.apprenticeNumber);
                    number += apprenticeMasterList.apprenticeNumber;
                    number--;//把自己减去
                }
            }


            yos.writeByte(number);
            yos.writeInt(selfID);
            yos.writeByte(isApprentice); //玩家是否是徒弟

            if (masterApprenticeList.masterUserID > 0)
            {
                if(masterApprenticeList.masterUserID == selfID){//玩家是师傅，查看师傅标签，提示没有师傅
                    HeroPlayer master = PlayerServiceImpl.getInstance()
                            .getPlayerByUserID(
                                    masterApprenticeList.masterUserID);
                    ResponseMessageQueue.getInstance().put(master.getMsgQueueIndex(),new Warning("你没有师傅"));
                }else{
                    yos.writeInt(masterApprenticeList.masterUserID);
                    yos.writeUTF(masterApprenticeList.masterName);
                    log.debug("master["+masterApprenticeList.masterName+"] is online = " + masterApprenticeList.masterIsOnline);
                    if (masterApprenticeList.masterIsOnline)
                    {
                        HeroPlayer master = PlayerServiceImpl.getInstance()
                                .getPlayerByUserID(
                                        masterApprenticeList.masterUserID);

                        if (null != master && master.isEnable())
                        {
                            yos.writeByte(true);
                            yos.writeByte(master.getVocation().value());
                            yos.writeShort(master.getLevel());
                            yos.writeByte(master.getSex().value());
    //                        output.writeByte(master.getUserID());
                        }
                        else
                        {
                            yos.writeByte(false);
                        }

                    }
                    else
                    {
                        yos.writeByte(false);
                    }

                    yos.writeByte(MasterApprentice.RELATION_TYPE_OF_MASTER);
                }
            }

            if ((!isApprentice) && masterApprenticeList.apprenticeNumber > 0) //玩家是师傅
            {
                HeroPlayer apprentice;
                log.debug("response MAList master's apprentice number = " + masterApprenticeList.apprenticeNumber);
                for (int i = 0; i < masterApprenticeList.apprenticeNumber; i++)
                {
                    log.debug("apprentice id="+masterApprenticeList.apprenticeList[i].userID+",name="+masterApprenticeList.apprenticeList[i].name);
                    yos
                            .writeInt(masterApprenticeList.apprenticeList[i].userID);
                    yos
                            .writeUTF(masterApprenticeList.apprenticeList[i].name);

                    if (masterApprenticeList.apprenticeList[i].isOnline)
                    {
                        apprentice = PlayerServiceImpl
                                .getInstance()
                                .getPlayerByUserID(
                                        masterApprenticeList.apprenticeList[i].userID);

                        if (null != apprentice && apprentice.isEnable())
                        {
                            yos.writeByte(true);
                            yos.writeByte(apprentice.getVocation().value());
                            yos.writeShort(apprentice.getLevel());
                            yos.writeByte(apprentice.getSex().value());
//                            output.writeInt(apprentice.getUserID());
                        }
                        else
                        {
                            yos.writeByte(false);
                        }
                    }
                    else
                    {
                        yos.writeByte(false);
                    }

                    yos
                            .writeByte(MasterApprentice.RELATION_TYPE_OF_APPRENTICE);
                }
            }else if(isApprentice && masterApprenticeList.apprenticeNumber == 0
                                        && apprenticeMasterList != null){//玩家是徒弟，也要把师傅的徒弟(全部，不包含自己)显示出来
                HeroPlayer apprentice;

                for (int i = 0; i < apprenticeMasterList.apprenticeNumber; i++)
                {
                    if(selfID != apprenticeMasterList.apprenticeList[i].userID){
                        yos.writeInt(apprenticeMasterList.apprenticeList[i].userID);
                        yos.writeUTF(apprenticeMasterList.apprenticeList[i].name);

                        if (apprenticeMasterList.apprenticeList[i].isOnline)
                        {
                            apprentice = PlayerServiceImpl
                                    .getInstance()
                                    .getPlayerByUserID(
                                            apprenticeMasterList.apprenticeList[i].userID);

                            if (null != apprentice && apprentice.isEnable())
                            {
                                yos.writeByte(true);
                                yos.writeByte(apprentice.getVocation().value());
                                yos.writeShort(apprentice.getLevel());
                                yos.writeByte(apprentice.getSex().value());
    //                            output.writeInt(apprentice.getUserID());
                            }
                            else
                            {
                                yos.writeByte(false);
                            }
                        }
                        else
                        {
                            yos.writeByte(false);
                        }

                        yos
                                .writeByte(MasterApprentice.RELATION_TYPE_OF_APPRENTICE);
                    }
                }
            }
        }


    }

}
