package hero.npc.function.system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;

import jxl.biff.HeaderFooter;

import hero.item.Goods;
import hero.item.detail.EGoodsType;
import hero.item.service.GoodsServiceImpl;
import hero.log.service.CauseLog;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.dict.EvidenveGiftDict;
import hero.npc.dict.QuestionDict;
import hero.npc.dict.QuestionDict.AwardData;
import hero.npc.dict.QuestionDict.QuestionData;
import hero.npc.function.BaseNpcFunction;
import hero.npc.function.ENpcFunctionType;
import hero.npc.message.NpcInteractiveResponse;
import hero.npc.service.NotPlayerServiceImpl;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.share.message.Warning;
import hero.share.service.Tip;
import hero.ui.UI_AnswerQuestion;

/**
 * 答题活动
 * @author Administrator
 *
 */
public class AnswerQuestion extends BaseNpcFunction {
	
    /**
     * 顶层操作项数据
     */
    protected ArrayList<NpcHandshakeOptionData> optionList;

	public AnswerQuestion(int npcID) {
		super(npcID);
	}

	@Override
	public ENpcFunctionType getFunctionType() {
		// TODO Auto-generated method stub
		return ENpcFunctionType.ANSWER_QUESTION;
	}

	@Override
	public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(HeroPlayer _player) {
		// TODO Auto-generated method stub
		return optionList;
	}

	@Override
	public void initTopLayerOptionList() {
		String[] optionName = QuestionDict.getInstance().getAnwserQuestionNames();
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
        TOP(1), ANSWER(2), END(3), CLOSE(4);

        byte tag;

        Step(int _tag)
        {
            tag = (byte) _tag;
        }
    }
    
    private Goods getGoods (HeroPlayer _player, AwardData award)
    {
		Goods goods = null;
		Iterator<Entry<Integer, Goods>>  iterator = award.goodsMap.entrySet().iterator();
		ArrayList<Integer> keyList = new ArrayList<Integer>();
		while(iterator.hasNext()) 
		{ 
			Entry<Integer, Goods> entry = iterator.next(); 
			int key = entry.getKey();
			keyList.add(key);
		}
		Integer[] keys = new Integer[keyList.size()];
		for (int i = 0; i < keys.length; i++) {
			keys[i] = keyList.get(i);
		}
		Arrays.sort(keys, Collections.reverseOrder());
		for (int i = 0; i < keys.length; i++) {
			if (_player.questionGroup.sumPoint >= keys[i]) {
				goods = award.goodsMap.get(keys[i]);
				break;
			}
		}
		return goods;
    }

	@Override
	public void process(HeroPlayer _player, byte _step, int selectIndex, YOYOInputStream _content)
			throws Exception {
		//0,用户进入答题步骤
		//1,随机获取[配置]数个题目
		//2,更新用户数据库,并把题目挂在用户player对象下面(因为NPC对象实例唯一,无法为每个用户存储不同的题目)
		//3,显示第1个题目
		if (_step == Step.TOP.tag) {
			if (!NotPlayerServiceImpl.getInstance().isInTime(selectIndex)) {
				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
						new Warning(Tip.TIP_NPC_QUESTION_NOT_BY_TIME, Warning.UI_TOOLTIP_TIP));
				return;
			}
			if (NotPlayerServiceImpl.getInstance().isJoinQuestion(selectIndex, _player.getUserID())) {
				ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), 
						new Warning(Tip.TIP_NPC_QUESTION_JOIN_IT, Warning.UI_TOOLTIP_TIP));
				return;
			}
			_player.questionGroup = QuestionDict.getInstance().getAnswerQuestionData(selectIndex);
			if (_player.questionGroup.question.size() > 0) {
				QuestionData question = _player.questionGroup.question.get(0);//获得第1个问题
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                        new NpcInteractiveResponse(getHostNpcID(), 
                        		optionList.get(selectIndex).functionMark,
                                Step.ANSWER.tag, 
                                UI_AnswerQuestion.getBytes(
                                		question.question, question.answerList, "得分：0分")));
			}
			_player.questionGroup.step = 1;
			//题目已下发,用户记录为已经触发该组答题
			NotPlayerServiceImpl.getInstance().joinQuestion(selectIndex, _player.getUserID());
			
		} else if (_step == Step.ANSWER.tag && _player.questionGroup != null) {
			//上一题答案
			int answer = _content.readByte() +1;
			String title = "";
			QuestionData question = _player.questionGroup.question.get(_player.questionGroup.step -1);
			if (question.rightAnswer == answer) {
				_player.questionGroup.sumPoint += _player.questionGroup.point;
				title = "上题正确   累计得分："+_player.questionGroup.sumPoint+"分";
			} else {
				title = "上题错误   累计得分："+_player.questionGroup.sumPoint+"分";
			}
			if (_player.questionGroup.question.size() > 0) {
                if (_player.questionGroup.step >= _player.questionGroup.questionSum) {
					//下发奖励
                	AwardData award = _player.questionGroup.award;
    				Goods goods = getGoods(_player, award);
    				if (goods != null) 
    				{
                    	if(goods.getGoodsType() == EGoodsType.PET)
                    	{
                    		PetServiceImpl.getInstance().addPet(_player.getUserID(), goods.getID());
                    	}
                    	else
                    	{
                            GoodsServiceImpl.getInstance().addGoods2Package(_player,
                            		goods.getID(),
                                    1, CauseLog.ANSWERQUESTION);
                    	}
                    	
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
                                new NpcInteractiveResponse(getHostNpcID(), 
                                		optionList.get(selectIndex).functionMark,
                                        Step.END.tag, 
                                        UI_AnswerQuestion.getEndBytes(
                                        		"答题结束,恭喜您获得", goods.getName(), title)));
                    	_player.questionGroup = null;
					}
    				else 
    				{
    		            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
    		                    new NpcInteractiveResponse(getHostNpcID(), 
    		                    		optionList.get(selectIndex).functionMark,
    		                            Step.END.tag, 
    		                            UI_AnswerQuestion.getEndBytes(
    		                            		"答题结束,你没能获得奖励，下次再努力吧", "", title)));
					}

				} else {
					question = _player.questionGroup.question.get(_player.questionGroup.step);
	                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
	                        new NpcInteractiveResponse(getHostNpcID(), 
	                        		optionList.get(selectIndex).functionMark,
	                                Step.ANSWER.tag, 
	                                UI_AnswerQuestion.getBytes(
	                                		question.question, question.answerList, title)));
	                _player.questionGroup.step += 1;
				}
			}
		} else if (_step == Step.END.tag && _player.questionGroup != null) {
			String title = "累计得分："+_player.questionGroup.sumPoint+"分";
			//判断是否下发奖励
        	AwardData award = _player.questionGroup.award;
        	Goods goods = getGoods(_player, award);
			if (goods != null) {
	        	if(goods.getGoodsType() == EGoodsType.PET)
	        	{
	        		PetServiceImpl.getInstance().addPet(_player.getUserID(), goods.getID());
	        	}
	        	else
	        	{
	                GoodsServiceImpl.getInstance().addGoods2Package(_player,
	                		goods.getID(),
	                        1, CauseLog.ANSWERQUESTION);
	        	}
	        	
	            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
	                    new NpcInteractiveResponse(getHostNpcID(), 
	                    		optionList.get(selectIndex).functionMark,
	                            Step.END.tag, 
	                            UI_AnswerQuestion.getEndBytes(
	                            		"答题结束,恭喜您获得", goods.getName(), title)));
	        	_player.questionGroup = null;
			} else {
	            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(),
	                    new NpcInteractiveResponse(getHostNpcID(), 
	                    		optionList.get(selectIndex).functionMark,
	                            Step.END.tag, 
	                            UI_AnswerQuestion.getEndBytes(
	                            		"答题结束,你没能获得奖励，下次再努力吧", "", title)));
			}

		}
	}

}
