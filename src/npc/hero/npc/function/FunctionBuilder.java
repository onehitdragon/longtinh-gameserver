package hero.npc.function;

import static hero.npc.function.ENpcFunctionType.CHANGE_VOCATION;
import static hero.npc.function.ENpcFunctionType.DUNGEON_TRANSMIT;
import hero.npc.dict.DungeonManagerDict;
import hero.npc.function.system.*;
import hero.npc.function.system.exchange.TraderExchangeContentDict;
import hero.npc.function.system.trade.TraderSellContentDict;
import hero.player.define.EClan;
import hero.share.ESystemFeature;
import hero.share.EVocation;
import org.apache.log4j.Logger;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 FunctionBuilder.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-2-18 下午05:23:20
 * @描述 ：
 */

public class FunctionBuilder
{
    public static Logger log = Logger.getLogger(FunctionBuilder.class);
    public static BaseNpcFunction build (String _npcModelID, int _npcObjectID,
            int _functionID, EVocation _vocation, ESystemFeature feature, EClan _clan)
    {
        BaseNpcFunction function = null;
        ENpcFunctionType functionType = ENpcFunctionType.getType(_functionID);
        log.debug("function builder npc modelID = " + _npcModelID +" vocation=" + _vocation);
        switch (functionType)
        {
            case TASK:
            {
                function = new TaskPassageway(_npcObjectID, _npcModelID);

                break;
            }
            case AUCTION:
            {
                function = new Auction(_npcObjectID);

                break;
            }
            case EXCHANGE:
            {
                function = new Exchange(_npcObjectID, TraderExchangeContentDict
                        .getInstance().getExchangeGoodsList(_npcModelID));
                break;
            }
            case POST_BOX:
            {
                function = new PostBox(_npcObjectID);
                break;
            }
            case REPAIR:
            {
                function = new Repair(_npcObjectID);
                break;
            }
            case SKILL_EDUCATE:
            {
                function = new SkillEducate(_npcObjectID, _vocation, feature);

                break;
            }
            case STORAGE:
            {
                function = new Storage(_npcObjectID);
                break;
            }
            case TRADE:
            {

                function = new Trade(_npcObjectID, TraderSellContentDict
                        .getInstance().getSellGoodsList(
                                _npcModelID.toLowerCase()));

                break;
            }
            case TRANSMIT:
            {
                function = new Transmit(_npcObjectID, _npcModelID);

                break;
            }
            case GUILD_MANAGE:
            {
                function = new GuildManager(_npcObjectID);

                break;
            }
            case WEAPON_RECORD:
            {
                function = new WeaponRecord(_npcObjectID);

                break;
            }
            case GATHER_NPC:
            {
                function = new GatherNpc(_npcObjectID);
                break;
            }
            case MANUF_NPC:
            {
                function = new ManufNpc(_npcObjectID);

                break;
            }
            case LOVER_TREE:
            {
                function = new LoverTree(_npcObjectID);

                break;
            }
            case MARRY_NPC:
            {
                function = new MarryNPC(_npcObjectID);

                break;
            }
            case WEDDING:
            {
                function = new WedEmceeNPC(_npcObjectID);

                break;
            }
            case DUNGEON_TRANSMIT:
            {
                function = new DungeonTransmit(_npcObjectID, DungeonManagerDict
                        .getInstance().getDungeonID(_npcModelID.toLowerCase()));

                break;
            }
            case CHANGE_VOCATION:
            {
                function = new ChangeVocation(_npcObjectID, _vocation, _clan);

                break;
            }
            case ANSWER_QUESTION:
            {
            	function = new AnswerQuestion(_npcObjectID);
            	
            	break;
            }
            case EVIDENVE_GET_GIFT:
            {
            	function = new EvidenveGetGift(_npcObjectID);
            	
            	break;
            }
        }

        return function;
    }
}
