package hero.share;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodj
 * Date: 11-4-21
 * Time: 下午3:03
 * 公告/活动
 */
public class Inotice {
    public int id;
    public String title;
    public String content;
    public int top; //是否置顶 0:置顶 1:普通
    public int color;

    public boolean isTop(){
        return top==0;
    }
}
