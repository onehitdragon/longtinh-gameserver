package hero.npc.function.system;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;

import hero.effect.service.EffectServiceImpl;
import hero.item.service.GoodsServiceImpl;
import hero.lover.service.LoverDAO;
import hero.map.Map;
import hero.map.message.ResponseAnimalInfoList;
import hero.map.message.ResponseBoxList;
import hero.map.message.ResponseMapBottomData;
import hero.map.message.ResponseMapElementList;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.message.ResponseMapMiniImage;
import hero.map.message.ResponsePetInfoList;
import hero.map.message.ResponseSceneElement;
import hero.map.service.MapServiceImpl;
import hero.map.service.MiniMapImageDict;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.BaseNpcFunction;
import hero.npc.function.ENpcFunctionType;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.LogWriter;
import hero.task.service.TaskServiceImpl;
import hero.ui.UI_InputString;

public class WeddingNPC extends BaseNpcFunction
{
    private static final String[]      mainMenuList            = {"预定婚礼(每月1~28号) 格式为(YYYY-MM-DD)" };

    private static final String        input                   = "请输入日期";

    private static ArrayList<byte[]>[] weddingMenuOptionData   = new ArrayList[mainMenuList.length];

    private static final short[]       mainMenuMarkImageIDList = {1008 };

    /**
     * 办婚礼需要的金钱
     */
    private static final int           CASH                    = 2000000;

    /**
     * 办婚礼的地图
     */
    private static final short         targetMapID             = 10;

    public WeddingNPC(int npcID)
    {
        super(npcID);
        // TODO Auto-generated constructor stub
    }

    @Override
    public ENpcFunctionType getFunctionType ()
    {
        // TODO Auto-generated method stub
        return ENpcFunctionType.WEDDING;
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList (
            HeroPlayer _player)
    {
        // TODO Auto-generated method stub
        ArrayList<NpcHandshakeOptionData> temp = new ArrayList<NpcHandshakeOptionData>();
        String str = null;
        if (_player.getMoney() >= CASH)
        {
            temp.add(optionList.get(0));
        }

        /* 今天是否有人结婚 */
        Calendar cab = Calendar.getInstance();
        if ((str = LoverDAO.whoWedding(cab.get(Calendar.YEAR) + "-"
                + (cab.get(Calendar.MONTH) + 1) + "-"
                + cab.get(Calendar.DAY_OF_MONTH))) != null)
        {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
            data.miniImageID = 1004;
            data.optionDesc = "参加" + str + "婚礼";
            data.functionMark = getFunctionType().value()
                    * BaseNpcFunction.FUNCTION_EXPEND_MODULUS + 1;
            temp.add(data);
        }
        return temp;
    }

    @Override
    public void initTopLayerOptionList ()
    {
        // TODO Auto-generated method stub
        ArrayList<byte[]> data1 = new ArrayList<byte[]>();
        data1.add(UI_InputString.getBytes(input, 10, 10));
        weddingMenuOptionData[0] = data1;

        for (int i = 0; i < mainMenuList.length; i++)
        {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
//            data.miniImageID = mainMenuMarkImageIDList[i];
            data.miniImageID = getMinMarkIconID();
            data.optionDesc = mainMenuList[i];
            data.functionMark = getFunctionType().value()
                    * BaseNpcFunction.FUNCTION_EXPEND_MODULUS + i;

            optionList.add(data);

            if (null != weddingMenuOptionData[i])
            {
                data.followOptionData = new ArrayList<byte[]>(
                        weddingMenuOptionData[i].size());

                for (byte[] b : weddingMenuOptionData[i])
                {
                    data.followOptionData.add(b);
                }
            }
        }
    }

    @Override
    public void process (HeroPlayer _player, byte _step, int selectIndex,
            YOYOInputStream _content) throws Exception
    {
        // TODO Auto-generated method stub
        if (_step == 1)
        {
            switch (selectIndex)
            {
                case 0:
                {
                    try
                    {
                        // 预约一个天结婚。
                        String date = _content.readUTF();
                        int[] userDate = new int[3];
                        int j = 0;
                        char v;
                        for (int i = 0; i < date.length(); i++)
                        {
                            v = date.charAt(i);
                            if (v != '-')
                            {
                                userDate[j] *= 10;
                                userDate[j] += (v - '0');
                            }
                            else
                            {
                                j++;
                            }
                        }

                        Calendar cab = Calendar.getInstance();
                        if (userDate[0] >= cab.get(Calendar.YEAR)
                                && userDate[0] <= 2050
                                && userDate[1] <= 12
                                && userDate[2] <= 28
                                && ((userDate[1] > cab.get(Calendar.MONTH)) || (userDate[1] == cab
                                        .get(Calendar.MONTH) && userDate[2] >= cab
                                        .get(Calendar.DAY_OF_MONTH))))
                        {
                            if (userDate[2] == cab.get(Calendar.DAY_OF_MONTH))
                            {
                                ResponseMessageQueue.getInstance().put(
                                        _player.getMsgQueueIndex(),
                                        new Warning("婚礼预约时间不能是今天"));
                            }
                            else
                            {
                                if (LoverDAO.registerWedding(date, _player
                                        .getName()))
                                {
                                    PlayerServiceImpl
                                            .getInstance()
                                            .addMoney(
                                                    _player,
                                                    -CASH,
                                                    1,
                                                    PlayerServiceImpl.MONEY_DRAW_LOCATION_NONE,"结婚花费");
                                    ResponseMessageQueue.getInstance().put(
                                            _player.getMsgQueueIndex(),
                                            new Warning("婚礼预定成功"));
                                }
                                else
                                {
                                    ResponseMessageQueue.getInstance().put(
                                            _player.getMsgQueueIndex(),
                                            new Warning("该日期已有人预定"));
                                }
                            }
                        }
                        else
                        {
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new Warning("日期不合法"));
                        }
                    }
                    catch (IOException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                    break;

                case 1:
                {
                    // 结婚地图跳转
                    // HeroMap targetMap =
                    // MapDict.getInstance().getMapByID(targetMapID);
                    // if (null == targetMap)
                    // {
                    // LogWriter.println("不存在的地图，ID:" + targetMapID);
                    //
                    // return;
                    // }
                    // _player.gotoMap(targetMap);
                    Calendar cab = Calendar.getInstance();
                    if (cab.get(Calendar.HOUR_OF_DAY) >= 19
                            && cab.get(Calendar.MINUTE) >= 30
                            && cab.get(Calendar.HOUR_OF_DAY) <= 20
                            && cab.get(Calendar.MINUTE) <= 30)
                    {
                        Map currentMap = _player.where();
                        Map targetMap = MapServiceImpl.getInstance()
                                .getNormalMapByID(targetMapID);
                        if (null == targetMap)
                        {
                            LogWriter.println("不存在的地图，ID:" + targetMapID);
                            return;
                        }
                        if (currentMap.getID() == targetMap.getID())
                        {
                            // LogWriter.println("当前地图和目标地图ID相同,当前地图：" +
                            // currentMap.getName()
                            // + ",目标地图：" + targetMap.getName());
                            // return;
                        }
                        _player.setCellX(targetMap.getBornX());
                        _player.setCellY(targetMap.getBornY());
                        ResponseMessageQueue.getInstance().put(
                                _player.getMsgQueueIndex(),
                                new ResponseMapBottomData(_player, targetMap,
                                        currentMap));
                        ResponseMessageQueue.getInstance().put(
                                _player.getMsgQueueIndex(),
                                new ResponseSceneElement(
                                        _player.getLoginInfo().clientType,
                                        targetMap));
                        ResponseMessageQueue.getInstance().put(
                                _player.getMsgQueueIndex(),
                                new ResponseMapGameObjectList(_player
                                        .getLoginInfo().clientType, targetMap));
                        TaskServiceImpl.getInstance().notifyMapNpcTaskMark(
                                _player, targetMap);
                        //add by zhengl; date: 2011-03-24; note: 加载其他玩家和怪物的BUFF.
                        EffectServiceImpl.getInstance().sendEffectList(_player, targetMap);
                        /*OutMsgQ
                                .getInstance()
                                .put(
                                        _player.getMsgQueueIndex(),
                                        new ResponseMapMiniImage(
                                                _player.getLoginInfo().clientType,
                                                targetMap.getMiniImageID(),
                                                MiniMapImageDict
                                                        .getInstance()
                                                        .getImageBytes(
                                                                targetMap
                                                                        .getMiniImageID())));*/

                        if (targetMap.getAnimalList().size() > 0)
                        {
                            ResponseMessageQueue.getInstance().put(
                                    _player.getMsgQueueIndex(),
                                    new ResponseAnimalInfoList(targetMap));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponsePetInfoList(_player));
                        }

                        ResponseMessageQueue.getInstance().put(
                                _player.getMsgQueueIndex(),
                                new ResponseMapElementList(_player
                                        .getLoginInfo().clientType, targetMap));

                        if (targetMap.getTaskGearList().size() > 0)
                        {
                            TaskServiceImpl.getInstance()
                                    .notifyMapGearOperateMark(_player,
                                            targetMap);
                        }

                        if (targetMap.getGroundTaskGoodsList().size() > 0)
                        {
                            TaskServiceImpl.getInstance()
                                    .notifyGroundTaskGoodsOperateMark(_player,
                                            targetMap);
                        }

                        if (targetMap.getBoxList().size() > 0)
                        {
                            ResponseMessageQueue.getInstance()
                                    .put(
                                            _player.getMsgQueueIndex(),
                                            new ResponseBoxList(targetMap
                                                    .getBoxList()));
                        }

                        GoodsServiceImpl.getInstance().sendLegacyBoxList(
                                targetMap, _player);

                        _player.gotoMap(targetMap);
                    }
                    else
                    {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new Warning("参加婚礼的时间为晚上7:30到8:30"));
                    }
                }
                    break;
            }
        }
    }

}
