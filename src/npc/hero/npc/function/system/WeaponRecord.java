package hero.npc.function.system;

import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.BaseNpcFunction;
import hero.npc.function.ENpcFunctionType;
import hero.npc.message.NpcInteractiveResponse;
import hero.player.HeroPlayer;
import hero.ui.UI_WeaponRecord;

import java.util.ArrayList;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;


public class WeaponRecord extends BaseNpcFunction
{
    /**
     * 顶层操作菜单列表
     */
    private static final String[] mainMenuList            = {"兵器谱排行" };

    /**
     * 顶层操作菜单图标列表
     */
    private static final short[]  mainMenuMarkImageIDList = {1012 };

    public WeaponRecord(int npcID)
    {
        super(npcID);
    }

    @Override
    public ENpcFunctionType getFunctionType ()
    {
        return ENpcFunctionType.WEAPON_RECORD;
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList (
            HeroPlayer _player)
    {
        return optionList;
    }

    @Override
    public void initTopLayerOptionList ()
    {
        for (int i = 0; i < mainMenuList.length; i++)
        {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
//            data.miniImageID = mainMenuMarkImageIDList[i];
            data.miniImageID = getMinMarkIconID();
            data.optionDesc = mainMenuList[i];
            data.functionMark = getFunctionType().value()
                    * BaseNpcFunction.FUNCTION_EXPEND_MODULUS + i;

            optionList.add(data);
        }
    }

    @Override
    public void process (HeroPlayer _player, byte _step, int _topSelectIndex,
            YOYOInputStream _content) throws Exception
    {
        ResponseMessageQueue.getInstance().put(
                _player.getMsgQueueIndex(),
                new NpcInteractiveResponse(getHostNpcID(),
                        optionList.get(0).functionMark, (byte) 1,
                        UI_WeaponRecord.getBytes()));
    }

}
