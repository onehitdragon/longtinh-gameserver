package hero.share;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-4-2
 * Time: 下午3:46
 * 存放排行菜单标签
 */
public class RankMenuField {
    public byte id;
    public String name;
    /**
     * 当前菜单等级
     */
    public byte menuLevel;
    /**
     * 两个职业查看的菜单里包括的职业,多个职业ID用","分隔
     */
    public String vocation;
    /**
     * 排行标签名称
     */
    public List<String> fieldList;

    /**
     * 子菜单
     */
    public List<RankMenuField> childMenuList;

    public RankMenuField getChildRankMenuFieldByID(byte id){
        for (RankMenuField rmf : childMenuList){
            if(rmf.id == id){
                return rmf;
            }
        }
        return null;
    }
}
