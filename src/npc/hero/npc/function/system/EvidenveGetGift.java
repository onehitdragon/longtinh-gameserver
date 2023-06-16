package hero.npc.function.system;

import java.util.ArrayList;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;

import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.dict.EvidenveGiftDict;
import hero.npc.dict.QuestionDict;
import hero.npc.dict.EvidenveGiftDict.EvidenveAward;
import hero.npc.dict.EvidenveGiftDict.EvidenveData;
import hero.npc.function.BaseNpcFunction;
import hero.npc.function.ENpcFunctionType;
import hero.npc.function.system.AnswerQuestion.Step;
import hero.npc.message.NpcInteractiveResponse;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.share.service.ShareDAO;
import hero.share.service.ShareServiceImpl;
import hero.share.service.Tip;
import hero.ui.UI_AnswerQuestion;
import hero.ui.UI_EvidenveReceive;

/**
 * 凭输入验证码领取奖励
 * @author Administrator
 *
 */
public class EvidenveGetGift extends BaseNpcFunction {

    /**
     * 顶层操作项数据
     */
    protected ArrayList<NpcHandshakeOptionData> optionList;
    
	public EvidenveGetGift(int npcID) {
		super(npcID);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ENpcFunctionType getFunctionType() {
		// TODO Auto-generated method stub
		return ENpcFunctionType.EVIDENVE_GET_GIFT;
	}

	@Override
	public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(HeroPlayer _player) {
		// TODO Auto-generated method stub
		return optionList;
	}

	@Override
	public void initTopLayerOptionList() {
		String[] optionName = EvidenveGiftDict.getInstance().getEvidenveGift();
		optionList = new ArrayList<NpcHandshakeOptionData>();
        for (int i = 0; i < optionName.length; i++)
        {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
            data.miniImageID = getMinMarkIconID();
            data.optionDesc = optionName[i];
            data.functionMark = getFunctionType().value()
                    * BaseNpcFunction.FUNCTION_EXPEND_MODULUS + i;
            optionList.add(data);
        }
		
	}
	
    enum Step
    {
        TOP(1), INPUT(2);

        byte tag;

        Step(int _tag)
        {
            tag = (byte) _tag;
        }
    }

	@Override
	public void process(HeroPlayer _player, byte _step, int selectIndex, YOYOInputStream _content)
			throws Exception {
		EvidenveData evidenve = EvidenveGiftDict.getInstance().getEvidenveData(selectIndex);
		if (_step == Step.TOP.tag)
		{
			ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                    new NpcInteractiveResponse(getHostNpcID(), 
                    		optionList.get(selectIndex).functionMark,
                            Step.INPUT.tag, 
                            UI_EvidenveReceive.getBytes(
                            		evidenve.inputBoxLenghts, 
                            		evidenve.inputBoxContents, 
                            		evidenve.name)));
		} else if (_step == Step.INPUT.tag) {
			byte inputCount = _content.readByte();
			if (inputCount == evidenve.inputBoxSum) {
				String[] inputContent = new String[inputCount];
				for (int i = 0; i < inputCount; i++) {
					inputContent[i] = _content.readUTF();
				}
				boolean isRightInput = ShareDAO.InputVerify(evidenve.tableName, 
						evidenve.columnNames, inputContent);
				boolean isJoinIt = ShareDAO.isJoinIt(evidenve.tableName, 
						_player.getLoginInfo().accountID);
				boolean isByUse = ShareDAO.isByUse(evidenve.tableName, 
						evidenve.columnNames, inputContent);
				//做3个判断
				if (!isByUse) {
					if (!isJoinIt) {
						if (isRightInput) {
							//更新数据过程
							boolean update = ShareDAO.updateEvidenveRece(evidenve.tableName, 
									evidenve.columnNames, inputContent, _player.getLoginInfo().accountID, 
									_player.getUserID());
							if (update) {
								//发放奖励过程
								EvidenveAward eAward = evidenve.award;
								String awards = "";
								if (eAward.money > 0) {
									PlayerServiceImpl.getInstance().addMoney(_player,
											eAward.money, 1,
											PlayerServiceImpl.MONEY_DRAW_LOCATION_WARNING,
											"输入凭证领取奖励");
									awards += ShareServiceImpl.getInstance().getConfig().getSignLineBreak() 
										+ eAward.money 
										+ ShareServiceImpl.getInstance().getConfig().getMonetaryUnit()
										+ ShareServiceImpl.getInstance().getConfig().getSignLineBreak();
								}
								if (eAward.exp > 0) {
									PlayerServiceImpl.getInstance().addExperience(_player, 
											eAward.exp, 1, PlayerServiceImpl.MONEY_DRAW_LOCATION_WARNING);
									awards += eAward.exp 
										+ "经验值"
										+ ShareServiceImpl.getInstance().getConfig().getSignLineBreak();
								}
								for (int i = 0; i < eAward.goodsList.length; i++) 
								{
									/***待完善标记:这个地方没有考虑玩家背包满的情况,将来优化*/
									if (eAward.goodsList[i] != null) {
						                GoodsServiceImpl.getInstance().addGoods2Package(_player,
						                		eAward.goodsList[i].getID(),
						                        1, CauseLog.EVIDENVEGET);
										awards += eAward.goodsList[i].getName()
											+ ShareServiceImpl.getInstance().getConfig().getSignLineBreak();
									}
								}
								//下发奖励TIP通知过程
								ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
										new Warning(
												Tip.TIP_NPC_EVIDENVE_GET_END.replaceAll("%fa", awards), 
												Warning.UI_TOOLTIP_TIP));
							} else {
								ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
										new Warning(Tip.TIP_NPC_FAIL_OPERATE));
							}
						} else {
							ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
									new Warning(evidenve.wrongByInput, Warning.UI_TOOLTIP_TIP));
						}
					} else {
						ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
								new Warning(evidenve.wrongByJoinIt, Warning.UI_TOOLTIP_TIP));
					}
				} else {
					ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
							new Warning(evidenve.wrongByUse, Warning.UI_TOOLTIP_TIP));
				}
				//完成3个判断
				
			} else {
				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
						new Warning(Tip.TIP_NPC_FAIL_OPERATE));
			}
		}
	}

}
