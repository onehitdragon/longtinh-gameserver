package hero.skill.message;

import hero.player.HeroPlayer;
import hero.share.ME2GameObject;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 SkillAnimationNotify.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2008-12-31 上午11:18:54
 * @描述 ：技能动画特效通知
 */

public class SkillAnimationNotify extends AbsResponseMessage
{
     private static Logger log = Logger.getLogger(SkillAnimationNotify.class);
    /**
     * 施放者对象类型
     */
    private byte    releaserObjectType;

    /**
     * 施放者编号
     */
    private int     releaserID;

    /**
     * 目标对象信息列表（类型、编号）
     */
    private int[][] targetList;

    /**
     * 施法特效动画编号
     */
    private int     releaseAnimationID;
    
    /**
     * 技能的施展动作编号
     */
    private byte     actionID;
    /**
     * 施法特效图片ID
     */
    private int     releaseImageID;
    
    /**
     * 层级关系(底层光环/上层特效)
     */
    private byte     tierRelation;

    /**
     * 承受特效动画编号
     */
    private int     acceptAnimationID;
    /**
     * 承受特效图片编号
     */
    private int     acceptImageID;

    /**
     * 主动技能的方向
     */
    private byte    direction;
    
    /**
     * 承受动画高度
     */
    private byte    heightRelation;
    
    /**
     * 施法动画高度
     */
    private byte    releaseHeightRelation;
    
    /**
     * 施法动作是否区分方向
     */
    private byte    isDirection;

    /**
     * 构造（单体技能）
     * 
     * @param _releaser 施放者
     * @param _releaseAnimationID 施法动画
     * @param _direction 施放动画的方向
     * @param _target 目标
     * @param _accepteAnimationID 作用动画
     * 
     * @param _actionID 技能施展动作; 
     * @param _tierRelation 层级关系; 1=上层,2=下层;
     */
    public SkillAnimationNotify(ME2GameObject _releaser,
            int _releaseAnimationID, int _releaseImageID, ME2GameObject _target,
            int _accepteAnimationID, int _acceptImageID, byte _actionID, byte _tierRelation, 
            byte _releaseHeightRelation, byte _heightRelation,  byte _isDirection)
    {
        if (null != _releaser)
        {
            releaserObjectType = _releaser.getObjectType().value();
            releaserID = _releaser.getID();
            releaseAnimationID = _releaseAnimationID;
            releaseImageID = _releaseImageID;
            direction = _releaser.getDirection();
            actionID = _actionID;
            tierRelation = _tierRelation;
            releaseHeightRelation = _releaseHeightRelation;
            isDirection = _isDirection;
        }

        if (0 != _accepteAnimationID)
        {
            acceptAnimationID = _accepteAnimationID;
            acceptImageID = _acceptImageID;
            heightRelation = _heightRelation;
            targetList = new int[1][2];
            targetList[0][0] = _target.getObjectType().value();
            targetList[0][1] = _target.getID();
        }
    }

    /**
     * 构造（群体技能）
     * 
     * @param _releaser 施放者
     * @param _targetList 目标列表
     * @param _direction 施放动画的方向
     * @param _releaseAnimationID 施法动画
     * @param _accepteAnimationID 作用动画
     */
    public SkillAnimationNotify(ME2GameObject _releaser,
            ArrayList<ME2GameObject> _targetList, int _releaseAnimationID, int _releaseImageID,
            int _accepteAnimationID, int _acceptImageID, byte _actionID, byte _tierRelation, 
            byte _releaseHeightRelation, byte _heightRelation, byte _isDirection)
    {
        if (null != _releaser)
        {
            releaserObjectType = _releaser.getObjectType().value();
            releaserID = _releaser.getID();
            releaseAnimationID = _releaseAnimationID;
            releaseImageID = _releaseImageID;
            direction = _releaser.getDirection();
            actionID = _actionID;
            tierRelation = _tierRelation;
            releaseHeightRelation = _releaseHeightRelation;
            isDirection = _isDirection;
        }

        if (0 != _accepteAnimationID)
        {
            acceptAnimationID = _accepteAnimationID;
            acceptImageID = _acceptImageID;
            heightRelation = _heightRelation;
            targetList = new int[_targetList.size()][2];

            ME2GameObject target;

            for (int i = 0; i < _targetList.size(); i++)
            {
                target = _targetList.get(i);
                targetList[i][0] = target.getObjectType().value();
                targetList[i][1] = target.getID();
            }
        }
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
        // TODO Auto-generated method stub
        yos.writeShort(releaseImageID);
        yos.writeShort(releaseAnimationID);
//        log.info("开始发送技能攻击到目标身上的效果");
//        log.info("施法特效动画编号:releaseAnimationID" + releaseAnimationID);
        yos.writeByte(actionID);
        yos.writeByte(tierRelation);
        //add by zhengl; date: 2011-02-15; note: 施法动画添加高度
        yos.writeByte(releaseHeightRelation);
//        log.info("施法高度关系:" + releaseHeightRelation);
        yos.writeByte(releaserObjectType);
        yos.writeInt(releaserID);
        yos.writeByte(direction);
        //add by zhengl; date: 2011-02-24; note: 施法技能特效是否按方向播放
        yos.writeByte(isDirection);

        if (0 == acceptAnimationID)
        {
            yos.writeByte(0);
        }
        else
        {
            yos.writeByte(targetList.length);
            yos.writeShort(acceptImageID);
            yos.writeShort(acceptAnimationID);
            //add by zhengl; date: 2011-02-15; note: 承受动画添加高度
            yos.writeByte(heightRelation);
//            log.info("承受特效图片编号:acceptImageID" + acceptImageID);
//            log.info("承受特效动画编号:acceptAnimationID" + acceptAnimationID);
//            log.info("承受特效高度关系" + heightRelation);

            for (int[] targetInfo : targetList)
            {
                yos.writeByte(targetInfo[0]);
                yos.writeInt(targetInfo[1]);
            }
        }
    }
}
