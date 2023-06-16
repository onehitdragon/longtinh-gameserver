package hero.item.message;

import hero.item.EquipmentInstance;
import hero.item.dictionary.GoodsContents;
import hero.item.special.Crystal;

import java.io.IOException;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;

/**
 * Created by IntelliJ IDEA.
 * User: jiaodongjie
 * Date: 2010-11-21
 * Time: 14:59:35
 * 返回装备上的强化细节 
 * 装备上的单个孔的强化加成属性值
 * 老报文,暂时不用
 * 下行：0x1d30
 */
public class ResponseSingleHoleEnhanceProperty extends AbsResponseMessage {
	private static Logger log = Logger.getLogger(ResponseSingleHoleEnhanceProperty.class);
    private EquipmentInstance ei;

    public ResponseSingleHoleEnhanceProperty(EquipmentInstance ei){
        this.ei = ei;
    }
    
    @Override
    protected void write() throws IOException {
        if(null != ei) {
        	//edit:	返回装备各细节
        	//date:	2011-01-25
        	//note:	只返回固定的光效ID即可
            byte[][] detail = ei.getGeneralEnhance().detail;
//            int level = ei.getArchetype().getNeedLevel();
//            int stoneID = Crystal.CORRESPONDING_TARGET_GOURD_LIST[0][0];// 默认的宝石的ID
//            for(int[] lev : Crystal.CORRESPONDING_TARGET_GOURD_LIST){
//                if(level >= lev[1] && level<= lev[2]){
//                    stoneID = lev[0];
//                }
//            }
//            Crystal crystal = (Crystal)GoodsContents.getGoods(stoneID);
            yos.writeByte(detail.length);
            for(int i = 0; i < detail.length; i++){
            	log.debug(detail[i][0] + "---" + detail[i][1] +" -- module=" + ei.getGeneralEnhance().getHoleModules(i) * 100);
                yos.writeByte(i);
                yos.writeShort(ei.getGeneralEnhance().getYetSetJewel(i)[0]);
                yos.writeShort(ei.getGeneralEnhance().getYetSetJewel(i)[1]);
                yos.writeByte(detail[i][0]); //0=未打孔  1=已打孔
                yos.writeByte(detail[i][1]); //0=未镶嵌  1、2、3镶嵌等级
                yos.writeInt(ei.getGeneralEnhance().getHoleModules(i) * 100);
            }
        } else {
            yos.writeShort(0);//这里改成 short 和图标一个类型
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
