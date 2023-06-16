package yoyo.service;

public class YOYOSystem
{
    public static String HOME;

    static
    {
        HOME = System.getenv("YOYO_HOME");

        if (HOME == null || HOME.equals(""))
        {
            HOME = "./";
        }
    }
}
