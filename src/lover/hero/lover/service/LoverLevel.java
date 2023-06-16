package hero.lover.service;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 11-1-23
 * Time: 下午12:23
 */
public enum LoverLevel {

    ZHI(1,"纸婚"),TIE(2,"铁婚"),TONG(3,"铜"),YIN(4,"银"),JIN(5,"金婚"),ZUANSHI(6,"钻石婚");

    private int level;
    private String name;

    LoverLevel(int level, String name) {
        this.level = level;
        this.name = name;
    }

    public int getLevel(){
        return level;
    }

    public String getName(){
        return name;
    }

}
