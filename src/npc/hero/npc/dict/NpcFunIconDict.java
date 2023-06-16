package hero.npc.dict;

import javolution.util.FastMap;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 11-2-13
 * Time: 下午5:52
 */
public class NpcFunIconDict {
    private static Logger log = Logger.getLogger(NpcFunIconDict.class);
    private FastMap<Integer,Short[]> npcFunIconDict;

    private static NpcFunIconDict instance;

    private NpcFunIconDict(){
        npcFunIconDict = new FastMap<Integer,Short[]>();
    }

    public static NpcFunIconDict getInstance(){
        if(instance == null){
            instance = new NpcFunIconDict();
        }
        return instance;
    }

    public Short[] getNpcFunIcon(int funID){
        return npcFunIconDict.get(funID);
    }

    public void load(String filePath){
        File fileDir;
        try{
            fileDir = new File(filePath);
            File[] fileList = fileDir.listFiles();

            for(File file : fileList){
                if(!file.getName().endsWith(".xml")){
                    continue;
                }
                SAXReader reader = new SAXReader();
                Document document = reader.read(file);
                Element rootE = document.getRootElement();
                Iterator<Element> rootIt = rootE.elementIterator();

                while (rootIt.hasNext())
                {
                    Element subE = rootIt.next();
                    int funID = Integer.parseInt(subE.elementTextTrim("typeID"));
                    short iconID = Short.parseShort(subE.elementTextTrim("iconID"));
                    short iconID2 = iconID;
                    String data = subE.elementTextTrim("iconID2");
                    if(data != null){
                    	iconID2 = Short.parseShort(data);
                    }
                    npcFunIconDict.put(funID,new Short[]{iconID,iconID2});
                }
            }
            log.debug("npc fun icon dict size = " + npcFunIconDict.size());
        } catch (DocumentException e) {
            log.error("load npc fun icon error : ",e);
            e.printStackTrace();
        }
    }
}
