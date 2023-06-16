//package hero.gm.tools.clienthandler;
//
//import hero.player.HeroPlayer;
//import hero.player.service.PlayerServiceImpl;
//import hero.share.EVocation;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import cn.digifun.gamemanager.Broadcast;
//import cn.digifun.gamemanager.Handler;
//import cn.digifun.gamemanager.core.GMProtocolID;
//import cn.digifun.gamemanager.core.RoleInfo;
//import cn.digifun.gamemanager.message.SelRoleInfoResponse;
//
///**
// * <p>
// * Copyright: DGFun CO., (c) 2010
// * </p>
// * 
// * @文件 ViewPlayerAttributeHandler.java
// * @创建者 ChenYaMeng
// * @版本 1.0
// * @时间 2010-06-21 12:36:56
// * @描述 ：处理上行报文ID(short):210(角色属性查询)
// */
//public class ViewPlayerAttributeHandler extends Handler
//{
//
//    public ViewPlayerAttributeHandler()
//    {
//        super(GMProtocolID.REQUEST_SEL_ROLE_INFO);
//    }
//
//    @Override
//    protected ViewPlayerAttributeHandler newInstance ()
//    {
//        return new ViewPlayerAttributeHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int sid = messageDataInputStream.readInt();
//        String nickname = messageDataInputStream.readUTF();
//
//        int userID = PlayerServiceImpl.getInstance().getUserIDByNameFromDB(
//                nickname);
//
//        if (userID > 0)
//        {
//            HeroPlayer player = PlayerServiceImpl.getInstance()
//                    .getPlayerByName(nickname);
//            boolean isOnline = true;
//            if (player == null)
//            {
//                isOnline = false;
//                player = PlayerServiceImpl.getInstance().getOffLinePlayerInfo(userID);
//            }
//            
//            if (player == null)
//            {
//                Broadcast.getInstance().send(message.getSessionID(),
//                        new SelRoleInfoResponse(sid, "不存在该角色1"));
//                return;
//            }
//
//            List<String> list = new ArrayList<String>();
//            list.add(String.valueOf(player.getLevel()));
//            list.add(player.getVocation().getDesc());
//            list.add(String.valueOf(player.getMoney()));
//            list.add(String.valueOf(player.getExp()));
//
//            String[] vocationDescList = EVocation.getVocationDescList();
//            int length = vocationDescList.length;
//            String vocationStr = "";
//            for (int i = 0; i < length; ++i)
//            {
//                if (i != length - 1)
//                {
//                    vocationStr = vocationStr + vocationDescList[i] + "、";
//                }
//                else
//                {
//                    vocationStr = vocationStr + vocationDescList[i] + "；";
//                }
//            }
//
//            String attribute = null;
//            if (isOnline)
//            {
//                attribute = "玩家目前在线";
//            }
//            else
//            {
//                attribute = "玩家目前不在线";
//            }
//
//            attribute += "\n\n可修改属性：\n" + "级别(1-50)：" + player.getLevel()
//                    + "\n" + "职业：" + player.getVocation().getDesc() + "\n"
//                    + "金钱：" + player.getMoney() + "\n" + "经验(0-"
//                    + player.getUpgradeNeedExp() + ")：" + player.getExp()
//                    + "\n\n" + "不可修改属性：\n" + "阵营：" + player.getClan().getDesc()
//                    + "\n" + "性別：" + player.getSex().getDesc() + "\n";
//
//            if (isOnline)
//            {
//                attribute += "最大生命值："
//                        + player.getActualProperty().getHpMax()
//                        + "\n"
//                        + "当前生命值："
//                        + player.getHp()
//                        + "\n"
//                        + "最大魔法值："
//                        + player.getActualProperty().getMpMax()
//                        + "\n"
//                        + "当前魔法值："
//                        + player.getMp()
//                        + "\n"
//                        + "物理防御："
//                        + player.getActualProperty().getDefense()
//                        + "\n"
//                        + "力量："
//                        + player.getActualProperty().getStrength()
//                        + "\n"
//                        + "敏捷："
//                        + player.getActualProperty().getAgility()
//                        + "\n"
//                        + "耐力："
//                        + player.getActualProperty().getStamina()
//                        + "\n"
//                        + "智力："
//                        + player.getActualProperty().getInte()
//                        + "\n"
//                        + "精神："
//                        + player.getActualProperty().getSpirit()
//                        + "\n"
//                        + "幸运："
//                        + player.getActualProperty().getLucky()
//                        + "\n"
//                        + "物理爆击等级："
//                        + player.getActualProperty().getPhysicsDeathblowLevel()
//                        + "\n"
//                        + "魔法爆击等级："
//                        + player.getActualProperty().getMagicDeathblowLevel()
//                        + "\n"
//                        + "命中等级："
//                        + player.getActualProperty().getHitLevel()
//                        + "\n"
//                        + "物理闪避等级："
//                        + player.getActualProperty().getPhysicsDuckLevel()
//                        + "\n"
//                        + "物理致命一击几率："
//                        + player.getActualProperty().getPhysicsDeathblowOdds()
//                        + "\n"
//                        + "魔法致命一击几率："
//                        + player.getActualProperty().getMagicDeathblowOdds()
//                        + "\n"
//                        + "物理命中几率："
//                        + player.getActualProperty().getPhysicsHitOdds()
//                        + "\n"
//                        + "魔法命中几率："
//                        + player.getActualProperty().getMagicHitOdds()
//                        + "\n"
//                        + "物理闪避等级："
//                        + player.getActualProperty().getPhysicsDuckLevel()
//                        + "\n"
//                        + "最小物理攻击力："
//                        + player.getActualProperty().getMinPhysicsAttack()
//                        + "\n"
//                        + "最大物理攻击力："
//                        + player.getActualProperty().getMaxPhysicsAttack()
//                        + "\n"
//                        + "附加物理攻击伤害值："
//                        + player.getActualProperty()
//                                .getAdditionalPhysicsAttackHarmValue()
//                        + "\n"
//                        + "附加物理攻击伤害比例："
//                        + player.getActualProperty()
//                                .getAdditionalPhysicsAttackHarmScale()
//                        + "\n"
//                        + "附加的受物理攻击伤害值："
//                        + player.getActualProperty()
//                                .getAdditionalHarmValueBePhysicsAttack()
//                        + "\n"
//                        + "附加的受物理攻击伤害比例："
//                        + player.getActualProperty()
//                                .getAdditionalHarmScaleBePhysicsAttack() + "\n";
//            }
//
//            RoleInfo roleInfo = new RoleInfo(player.getUserID(), player
//                    .getName(), list, attribute);
//            Broadcast.getInstance().send(message.getSessionID(),
//                    new SelRoleInfoResponse(sid, roleInfo));
//        }
//        else
//        {
//            Broadcast.getInstance().send(message.getSessionID(),
//                    new SelRoleInfoResponse(sid, "不存在该角色2"));
//        }
//    }
//}
