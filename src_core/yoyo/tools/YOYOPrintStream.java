package yoyo.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import yoyo.service.YOYOSystem;


public class YOYOPrintStream extends PrintStream
{
    private static String logOut = YOYOSystem.HOME + "log" + File.separator + "out.log";

    private static String logErr = YOYOSystem.HOME + "log" + File.separator + "err.log";

    PrintStream ps;
    
    public static void init ()
    {
        try
        {
            PrintStream out = new PrintStream(new FileOutputStream(logOut));
            PrintStream pStream = new YOYOPrintStream(System.out, out);
            System.setOut(pStream);

            PrintStream err = new PrintStream(new FileOutputStream(logErr));
            pStream = new YOYOPrintStream(System.err, err);

            System.setErr(pStream);
        }
        catch (FileNotFoundException e)
        {
        	e.printStackTrace();
        }
    }

    public YOYOPrintStream(PrintStream ps1, PrintStream ps2)
    {
        super(ps1);
        ps = ps2;
    }

    public void write (byte[] bytes , int offset, int length)
    {
        try
        {
            super.write(bytes, offset, length);
            ps.write(bytes, offset, length);
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
    }

    public void flush ()
    {
        super.flush();
        ps.flush();
    }
}
