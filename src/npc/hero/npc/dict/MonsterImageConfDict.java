package hero.npc.dict;

/**<p>
 *  Copyright: DGFun CO., (c) 2005
 *  </p>
 *  @author DING Chu
 *  @version 1.0
 *  @date 2008-8-21 上午10:03:08
 *
 *  <pre>
 *      Description:
 *  </pre>
 **/

import hero.npc.service.NotPlayerServiceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

import jxl.Sheet;
import jxl.Workbook;
import org.apache.log4j.Logger;

public class MonsterImageConfDict
{
     private static Logger log = Logger.getLogger(MonsterImageConfDict.class);
    private static final String           CONFIG_FILE_NAME = "MonsterPictConf.xls";

    private static HashMap<Short, Config> configMap        = new HashMap<Short, Config>();

    private static boolean                hasInited;

    public static void init ()
    {
        if (!hasInited)
        {
            load();
            hasInited = true;
        }
    }

    private static void load ()
    {
        Workbook rwb = null;
        try
        {
            File file = new File(
                    NotPlayerServiceImpl.getInstance().getConfig().MonsterImageCfgPath
                            + File.separator + CONFIG_FILE_NAME);
            if (file.exists())
            {
                InputStream is = new FileInputStream(file);
                rwb = Workbook.getWorkbook(is);
                // 获取第一张Sheet表
                Sheet rs = rwb.getSheet(0);
                int packetNums = rs.getRows();

                if (packetNums > 1)
                {
                    int rowIndex = 1;

                    try
                    {
                        short ID = 0, shadowY = 0, height = 0, animation = 0;
                        byte grid = 0, headExcursionX = 0, headExcursionY = 0, centerExcursionX = 0, shadowType = 0, shadowX = 0, shadowSize = 0;

                        for (; rowIndex < packetNums; rowIndex++)
                        {
                            ID = Short.parseShort(rs.getCell(0, rowIndex)
                                    .getContents().trim());
                            grid = Byte.parseByte(rs.getCell(1, rowIndex)
                                    .getContents().trim());
                            height = Short.parseShort(rs.getCell(2, rowIndex)
                                    .getContents().trim());
//                            headExcursionX = Byte.parseByte(rs.getCell(3,
//                                    rowIndex).getContents().trim());
//                            headExcursionY = Byte.parseByte(rs.getCell(4,
//                                    rowIndex).getContents().trim());
//                            centerExcursionX = Byte.parseByte(rs.getCell(5,
//                                    rowIndex).getContents().trim());
//                            shadowX = Byte.parseByte(rs.getCell(6, rowIndex)
//                                    .getContents().trim());
//                            shadowY = Short.parseShort(rs.getCell(7, rowIndex)
//                                    .getContents().trim());
//                            shadowType = Byte.parseByte(rs.getCell(8, rowIndex)
//                                    .getContents().trim());
                            animation = Short.parseShort(rs.getCell(9, rowIndex)
                                    .getContents().trim());
                            shadowSize = Byte.parseByte(rs.getCell(10, rowIndex)
                                    .getContents().trim());

                            configMap.put(ID, new Config(ID, grid,
                                    headExcursionX, headExcursionY,
                                    centerExcursionX, shadowType, shadowX,
                                    shadowY, height, animation, shadowSize));
                        }
                    }
                    catch (Exception e)
                    {
                        log.info("警告:" + CONFIG_FILE_NAME + "->第"
                                + (rowIndex + 1) + "行数据不规范");
                        e.printStackTrace();
                    }

                }
                else
                {
                    log.info("怪物图片配置文件没有内容");
                }

                rwb.close();
                rwb = null;
                is.close();
                is = null;
            }
            else
            {
                log.info("没有找到怪物图片配置文件");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static Config get (short _ID)
    {
        return configMap.get(_ID);
    }

    public static class Config
    {
        private Config(short _ID, byte _grid, byte _headExcursionX,
                byte _headExcursionY, byte _centerExcursionX, byte _shadowType,
                byte _shadowX, short _shadowY, short _monsterHeight, short _animationID, 
                byte _shadowSize)
        {
            ID = _ID;
            grid = _grid;
            headExcursionX = _headExcursionX;
            headExcursionY = _headExcursionY;
            centerExcursionX = _centerExcursionX;
            shadowType = _shadowType;
            shadowX = _shadowX;
            shadowY = _shadowY;
            monsterHeight = _monsterHeight;
            animationID = _animationID;
            shadowSize = _shadowSize;
            
        }

        public short ID;              // 图片编号

        public byte  grid;            // 怪物占用地图格子数

        public byte  headExcursionX;  // 头像区域左边偏移像素，以图片左边为原点

        public byte  headExcursionY;  // 头像区域上边偏移像素，以图片上边为原点

        public byte  centerExcursionX; // 怪物中心距偏移像素，以图片左边为原点

        public byte  shadowType;      // 影子类型

        public byte  shadowX;         // 影子X坐标

        public short shadowY;         // 影子Y坐标
        
        public short monsterHeight;   //怪物高度
        
        public short animationID;     //动画ID
        
        public byte  shadowSize;      //阴影以及选中框大小
    }
}
