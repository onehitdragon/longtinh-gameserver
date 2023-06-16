package hero.share;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * <p>
 * Copyright: digifun co., (c) 2005
 * </p>
 * 
 * @author dingchu
 * @version 2.0
 * @date 2008-3-31
 * 
 * <pre>
 *            Description:
 * </pre>
 */
public class DirtyStringDict
{
    private static DirtyStringDict   instance;

    private static ArrayList<String> dirtyStringList;

    private static final String      RECORD_FILE_PATH = System
                                                              .getProperty("user.dir")
                                                              + File.separator
                                                              + "res"
                                                              + File.separator
                                                              + "data"
                                                              + File.separator
                                                              + "dirty"
                                                              + File.separator
                                                              + "TalkDirtyString.txt";

    static
    {
        BufferedReader br = null;

        try
        {
            if (null == dirtyStringList)
            {
                dirtyStringList = new ArrayList<String>();
                File file = new File(RECORD_FILE_PATH);

                if (file.exists())
                {
                    br = new BufferedReader(new InputStreamReader(
        					new FileInputStream(file.getAbsolutePath()), "GB2312"));
                    String record = br.readLine();

                    while (record != null)
                    {
                        if (!record.trim().equals(""))
                        {
                            dirtyStringList.add(record);
                        }

                        record = br.readLine();
                    }

                    // ln("屏蔽词语加载完毕");
                    // LogWriter.println("屏蔽词语加载完毕");
                }
                else
                {
                    file.createNewFile();
                }
            }
        }
        catch (Exception e)
        {

        }
        finally
        {
            try
            {
                if (null != br)
                {
                    br.close();
                    br = null;
                }
            }
            catch (Exception e)
            {

            }
        }
    }

    private DirtyStringDict()
    {

    }

    public static DirtyStringDict getInstance ()
    {
        if (null == instance)
        {
            instance = new DirtyStringDict();
        }

        return instance;
    }

    public boolean isCleanString (String _content)
    {
        if (_content.indexOf(" ") != -1)
        {
            return false;
        }
        if (_content.indexOf("　") != -1)
        {
            return false;
        }
        if ((_content.indexOf("g") != -1 || (_content.indexOf("G") != -1) || (_content
                .indexOf("Ｇ") != -1))
                && (_content.indexOf("m") != -1
                        || (_content.indexOf("M") != -1) || (_content
                        .indexOf("Ｍ") != -1)))
        {
            return false;
        }
        if (_content.indexOf("客") != -1 && _content.indexOf("服") != -1)
        {
            return false;
        }
        if (_content.indexOf("管") != -1 && _content.indexOf("理") != -1)
        {
            return false;
        }
        if (_content.indexOf("系") != -1 && _content.indexOf("统") != -1)
        {
            return false;
        }

        for (int i = 0; i < dirtyStringList.size(); i++)
        {
            if (_content.indexOf(dirtyStringList.get(i)) != -1)
            {
                return false;
            }
        }

        return true;
    }

    public String clearDirtyChar (String _content)
    {
        StringBuffer sb = new StringBuffer();

        //把聊天内容里添加的物品数量解析出来，物品数量是不能大于10的
        int goodsnum = Integer.parseInt(_content.substring(_content.indexOf("<num>")+5,_content.indexOf("</num>")));
        _content = _content.substring(0,_content.indexOf("<num>"));
        if(goodsnum>0){
            String content_tmp;
            String goodsname_tmp;
            for (int i=0; i<goodsnum; i++){
                content_tmp = _content.substring(0,_content.indexOf("<goodsname_"+i+">"));
                goodsname_tmp = _content.substring(_content.indexOf("<goodsname_"+i+">")+13,_content.indexOf("</goodsname_"+i+">"));

                for (int j=0; j<dirtyStringList.size(); j++){
                    content_tmp = content_tmp.replaceAll(dirtyStringList.get(j),"**");
                }
                sb.append(content_tmp).append(goodsname_tmp);

                _content = _content.substring(_content.indexOf("</goodsname_"+i+">")+14);
            }
            for (int i=0; i<dirtyStringList.size(); i++){
                _content = _content.replaceAll(dirtyStringList.get(i),"**");
            }
            return sb.append(_content).toString();
        }else {
            for (int i=0; i<dirtyStringList.size(); i++){
                _content = _content.replaceAll(dirtyStringList.get(i),"**");
            }
            return _content;
        }


    }
}
