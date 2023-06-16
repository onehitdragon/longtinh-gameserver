package yoyo.service;

public enum Priority
{
    REAL_TIME(0),
    DELAY(1);
    private int value;

    private Priority(int v)
    {
        value = v;
    }
    
    public int getValue()
    {
    	return value;
    }
    
    public static Priority getPriority (int v)
    {
        for (Priority p : Priority.values())
        {
            if (p.value == v)
            {
                return p;
            }
        }
        return REAL_TIME;
    }
}
