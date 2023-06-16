package hero.item.other;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 11-1-14
 * Time: 上午10:15
 * 把 res//data//goods 下的物品和宠物数据导入 运营数据库的 goods 表里
 * 物品指需要花钱购买的物品
 * 只含 物品ID，物品名称，物品类型
 */
public class ImportGoodsToDB {
    private static Logger log = Logger.getLogger(ImportGoodsToDB.class);

    private static final String DIR_PATH = "\\res\\data\\goods\\";
    private static final String PET_PATH = "\\res\\data\\pet\\pet.xml";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/pss"
                + "?connectTimeout=0&autoReconnect=true&characterEncoding=utf8";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "yoyo1705";

    private static List<File> filelist = new ArrayList<File>();

    /**
     * game_id
     */
    private static final int GAME_ID = 1;

    public static void main(String[] args){
        /*try {
            getGoodsList();
        } catch (DocumentException e) {
            e.printStackTrace();
        }*/
        importToDB();
    }

    private static void importToDB(){
        Connection conn = null;
        CallableStatement cs = null;

        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL,USERNAME,PASSWORD);
            conn.setAutoCommit(false);
            cs = conn.prepareCall("call sync_goods_proc(?,?,?,?,?)");

            List<Goodsx> goodslist = getGoodsList();

            for(Goodsx goods : goodslist){
                cs.setInt(1,GAME_ID);
                cs.setInt(2,goods.getGoodsid());
                cs.setString(3,goods.getGoodsname());
                cs.setString(4,goods.getType());
                cs.setString(5,goods.getSmallType());

                cs.addBatch();
            }

            cs.executeBatch();
            conn.commit();

            conn.setAutoCommit(true);

            cs.close();
            conn.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }finally{
            try{
                if(cs != null){
                    cs.close();
                    cs = null;
                }
                if(conn != null){
                    conn.close();
                    conn = null;
                }
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
    }

    private static List<Goodsx> getGoodsList() throws DocumentException {
        List<Goodsx> goodsList = new ArrayList<Goodsx>();

        File dir = new File(System.getProperty("user.dir")+DIR_PATH);
        getFileList(dir);
        getFileList(new File(System.getProperty("user.dir")+PET_PATH));
        for(File f : filelist){
            log.info(f.getName());
            String type = "";
            boolean  ispet = false;
            if(f.getName().equals("suite.xml") || f.getName().equals("clothes.xml")
                    || f.getName().equals("glove.xml") || f.getName().equals("hat.xml")
                    || f.getName().equals("necklace.xml") || f.getName().equals("shoes.xml")){
                type = "防具";
            }
            else if(f.getName().equals("feed.xml")){
                type = "宠物饲料";
            }
            else if(f.getName().equals("mall_goods.xml")){
                type = "商城物品";
            }
            else if(f.getName().equals("material.xml")){
                type = "材料";
            }
            else if(f.getName().equals("medicament.xml")){
                type = "药水";
            }
            else if(f.getName().equals("pet_quip.xml")){
                type = "宠物装备";
            }
            else if(f.getName().equals("special.xml")){
                type = "特殊物品";
            }
            else if(f.getName().equals("weapon.xml")){
                type = "武器";
            }else if(f.getName().equals("pet.xml")){
                type = "宠物";
                ispet = true;
            }
            else{
                continue;
            }
            SAXReader reader = new SAXReader();
            Document document = reader.read(f);
            Element root = document.getRootElement();
            Iterator<Element> rootIt = root.elementIterator();
            while(rootIt.hasNext()){
                Goodsx goods = new Goodsx();
                Element subE = rootIt.next();
                if(null != subE){
                    String id = subE.elementTextTrim("id");
                    String name = subE.elementTextTrim("name");
                    String smallType = subE.elementTextTrim("type");

                    if(ispet){
                        int stage = Integer.parseInt(subE.elementTextTrim("stage"));
                        type = "宠物"+name;
                        if(stage == 0) {
                            name = name+"蛋";
                            smallType = "宠物蛋";
                        }
                        if(stage == 1) {
                            name = "幼年"+name;
                            smallType = "幼年宠物";
                        }
                        if(stage == 2){
                            if(Integer.parseInt(smallType) == 1){
                                name = "坐骑" + name;
                            }else if(Integer.parseInt(smallType) == 2){
                                name = "战斗" + name;
                            }
                            smallType = "成年宠物";
                        }
                    }
                    goods.setGoodsid(Integer.parseInt(id));
                    goods.setGoodsname(name);
                    goods.setType(type);
                    if(null != smallType){
                        goods.setSmallType(smallType);
                    }

                    goodsList.add(goods);
                }
            }
        }

        return goodsList;
    }

    private static void getFileList(File file){

        if(file.isDirectory()){
            File[] fs = file.listFiles();
            for(File f : fs){
                getFileList(f);
            }
        }else{
            if(file.getName().endsWith(".xml")){
                filelist.add(file);
            }
        }

    }


}

class Goodsx{
        public int goodsid;
        public String goodsname;
        public String type;
        public String smallType = "";

        Goodsx(){}


        public int getGoodsid() {
            return goodsid;
        }

        public void setGoodsid(int goodsid) {
            this.goodsid = goodsid;
        }

        public String getGoodsname() {
            return goodsname;
        }

        public void setGoodsname(String goodsname) {
            this.goodsname = goodsname;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSmallType() {
            return smallType;
        }

        public void setSmallType(String smallType) {
            this.smallType = smallType;
        }
    }
