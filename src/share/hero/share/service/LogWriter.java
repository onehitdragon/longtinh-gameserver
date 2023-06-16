package hero.share.service;

import java.io.File;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;

public class LogWriter extends TimerTask
{
     private static Logger log = Logger.getLogger(LogWriter.class);
    private static LogWriter     instance             = null;

    private static Timer         timer                = null;

    private static Logger        logger               = null;

    private static FileAppender  appender             = null;

    private static PatternLayout layout               = null;

    private static String        logPattern           = "";

    private static String        logCurrentFileName   = "log.txt";

    private static String        logCurrentPath       = "." + File.separator
                                                              + "server"
                                                              + File.separator
                                                              + "currentlog"
                                                              + File.separator;

    private static String        logBackupPath        = "." + File.separator
                                                              + "server"
                                                              + File.separator
                                                              + "backuplog"
                                                              + File.separator;

    private static String        logBackupExtendsname = ".log";

    private static boolean       hasStart             = false;

    public LogWriter()
    {
    }

    public static LogWriter getInstance ()
    {
        if (instance == null)
            instance = new LogWriter();
        return instance;
    }

    public static void init ()
    {
        logPattern = "%d [%t] %-5p %c - %m%n";
        init(logCurrentPath, logCurrentFileName);
    }

    public static void init (String _path, String _fileName)
    {
        init(_path, _fileName, "%d [%t] %-5p %c - %m%n");
    }

    public static void init (String _path, String _fileName,
            String _log4jPattern)
    {
        if (hasStart)
            return;
        hasStart = true;
        if (_log4jPattern == null)
            logPattern = "%m%n";
        else
            logPattern = _log4jPattern;
        logCurrentPath = _path;
        logCurrentFileName = _fileName;
        startLog();
        timer = new Timer();
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.set(11, 24);
        timer
                .scheduleAtFixedRate(getInstance(), calendar.getTime(),
                        0x5265c00L);
    }

    public final Object clone () throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException();
    }

    public final void writeObject (ObjectOutputStream out)
            throws NotSerializableException
    {
        throw new NotSerializableException("This object cannot be serialized");
    }

    public final void readObject (ObjectInputStream in)
            throws NotSerializableException
    {
        throw new NotSerializableException("This object cannot be deserialized");
    }

    private static void startLog ()
    {
        try
        {
            logger = Logger.getLogger("UEN");
            layout = new PatternLayout(logPattern);
            File currentLogFolder = new File(logCurrentPath);
            if (!currentLogFolder.exists())
                currentLogFolder.mkdir();
            try
            {
                appender = new FileAppender(layout, logCurrentPath
                        + logCurrentFileName, true);
            }
            catch (Exception exception)
            {
            }
            // 设置不继承 add by黄树振
            logger.setAdditivity(false);
            logger.addAppender(appender);
            logger.setLevel(Level.ALL);
        }
        catch (Exception ex)
        {
            log.error("Can not find logger!");
        }
    }

    public void run ()
    {
        try
        {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(new Date());
            String backupFileName = formatLogName();
            backupFileName = backupFileName + logBackupExtendsname;
            File backupLogFolder = new File(logBackupPath);
            if (!backupLogFolder.exists())
                backupLogFolder.mkdir();
            File backupLog = new File(logBackupPath + backupFileName);

            synchronized (logger)
            {
                logger.removeAppender(appender);
                appender.close();
                File logfile = new File(logCurrentPath + logCurrentFileName);
                logfile.renameTo(backupLog);

                try
                {
                    appender = new FileAppender(layout, logCurrentPath
                            + logCurrentFileName, false);
                }
                catch (Exception exception)
                {
                }
                logger.addAppender(appender);
                logger.addAppender(new ConsoleAppender(new SimpleLayout()));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public String formatLogName ()
    {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(new Date());
        DateFormat df = DateFormat.getDateTimeInstance();
        String backupFileName = df.format(gc.getTime());

        int index = backupFileName.indexOf(" ");
        String fileName = backupFileName.substring(0, index);
        String temp = "";
        temp = backupFileName.substring(index + 1, backupFileName.indexOf(":"));
        backupFileName = backupFileName
                .substring(backupFileName.indexOf(":") + 1);
        fileName = fileName + "-" + temp;
        
        temp = backupFileName.substring(0, backupFileName.indexOf(":"));
        fileName = fileName + "-" + temp;
       
        backupFileName = backupFileName
                .substring(backupFileName.indexOf(":") + 1);
        temp = backupFileName;
        fileName = fileName + "-" + temp;

        return fileName;
    }

    public static void uninit ()
    {
        logger = null;
    }

    public static void println (int _event)
    {
        info(String.valueOf(_event));
    }

    public static void println (String _event)
    {
        try
        {
            _event.replace('\'', '_');
            _event.replace('"', '_');
            info(_event);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void println (byte _event[])
    {
        info(new String(_event));
    }

    public static void println (char _event[])
    {
        info(new String(_event));
    }

    public static void println (Exception _event)
    {
        info(_event.toString() + _event.getMessage());
    }

    public static void error (Object obj, Throwable e)
    {
        logger.error(obj, e);
    }

    public static void info (String _msg)
    {
        logger.info(_msg);
    }

}
