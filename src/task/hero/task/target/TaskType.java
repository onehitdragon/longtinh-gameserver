package hero.task.target;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 2010-11-9
 * Time: 17:09:45
 * 任务类型
 */
public enum TaskType {
    /**
     * 自己
     */
    SINGLE(1,"自己"),
    /**
     * 师徒任务(玩家必须要有师徒关系，但不一定同时在线，也不一定在同一队伍)
     */
    MASTER(2,"师徒"),
    /**
     * 婚姻任务(玩家必须要有夫妻关系，但不一定同时在线，也不一定在同一队伍)
     */
    MARRY(3,"婚姻");

    private int id;
    private String desc;

    TaskType(int id, String desc){
        this.id = id;
        this.desc = desc;
    }

    public static TaskType getTaskTypeByID(int id){
        for(TaskType type : TaskType.values()){
            if(type.id == id){
                return type;
            }
        }
        return null;
    }

    public static TaskType getTaskTypeByType(String desc){
        for(TaskType type : TaskType.values()){
            if(type.desc.equals(desc)){
                return type;
            }
        }
        return null;
    }
}
