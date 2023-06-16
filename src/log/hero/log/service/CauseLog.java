package hero.log.service;

public enum CauseLog
{
    SALE(0, "出售"), BUY(1, "购买"), EXCHANGE(2, "交易"), DROP(3, "掉落"), AUCTION(4,
            "拍卖"), MAIL(5, "邮箱"), MANUF(6, "制造"), DEL(7, "丢弃"), TASKAWARD(8,
            "任务奖励"), STORAGE(9, "仓库"), STORE(10, "个人商店"), GATHER(11, "采集"), REFINED(
            12, "炼化"), RATTAN(13, "藤条"), TASKTOOL(14, "任务道具"), MALL(15, "商城"), TEACH(
            16, "知识授予"), CANCELTASK(17, "取消任务"), SUBMITTASK(18, "完成任务"), WORLDCHAT(
            19, "世界聊天"), CHANGEVOCATION(20, "转职"), REMOVESEAL(21, "解除封印"), CONVERT(
            22, "兑换"), SHORTCUTKEY(23, "快捷键"), NOVICE(24, "新手奖励"), UNLOAD(25,"卸下"), 
            WEAR(26, "穿戴"),DIVORCE(27,"离婚"), TAKEOFF(28,"摆摊下架"), 
            ENHANCE(29,"镶嵌"), USE(30,"计费道具使用"), GUILDBUILD(31,"创建公会消耗道具"),
            PERFORATE(32,"打孔"),PULLOUT(33,"拔除宝石"), DICEEQUIP(34,"打孔失败装备摧毁"), 
            GMADD(35,"GM添加"), RINSE(36, "洗点"),CLANCHAT(37, "阵营聊天"), TASKPUSH(38, "任务推广"), 
            OPENGIFTBAG(39, "打开礼包"), COUNTDOWNGIFTSEND(39, "倒计时礼包下发"), 
            ENHANCEFEE(40,"强化花费金钱"), ANSWERQUESTION(41, "回答问题活动获得道具"), 
            EVIDENVEGET(42, "凭输入领取奖励");
    
    private int    id;

    private String name;

    CauseLog(int _id, String _name)
    {
        id = _id;
        name = _name;
    }

    public int getId ()
    {
        return id;
    }

    public String getName ()
    {
        return name;
    }
}
