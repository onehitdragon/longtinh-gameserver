package hero.map.message;

import hero.map.Decorater;
import hero.map.Map;
import hero.map.detail.OtherObjectData;
import hero.share.Constant;

import java.io.IOException;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;

/**
 * 装饰层数据
 * @author jiaodongjie
 *
 */
public class ResponseMapDecorateData extends AbsResponseMessage
{
    private static Logger log = Logger.getLogger(ResponseMapDecorateData.class);
	private Map map;
	
	/**
     * 客户端类型（高、中、低端）
     */
    private short clientType;
    
    /**
     * 装饰层数据
     * @param map
     * @param clientType
     */
	public ResponseMapDecorateData(Map map, short clientType)
	{
		super();
		this.map = map;
		this.clientType = clientType;
	}

	@Override
	public int getPriority ()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void write () throws IOException
	{
		log.debug("进入装饰层数据透传");
		yos.writeShort(map.getID());
		log.debug("map.getID() : " + map.getID());
		//edit by zhengl ; date: 2010-11-19 ; note: 装饰层没有旋转的功能
//		output.writeShort(map.resourceTransformData.length);//资源层
//		log.debug("map.resourceTransformData.length : " + map.resourceTransformData.length);
//		output.writeBytes(map.resourceTransformData);//资源层旋转角度
		//end
		
		yos.writeByte(map.decoraterList.length);//装饰层对象数量
		log.debug("map.decoraterList.length :" + map.decoraterList.length);

        int i=0;
		for(OtherObjectData decoraters : map.decoraterList) {//装饰层
			//edit by zhengl ; date: 2010-11-19 ; note: 装饰层不再关心pngid取而代之的是animationid
			//edit by zhengl; date: 2011-02-14; note: 装饰层也需要PNGID.
			yos.writeShort(decoraters.pngId);
			log.debug("pngId: " + decoraters.pngId);
			yos.writeShort(decoraters.animationID);
			log.debug("decoraters.animationID:"+decoraters.animationID);
			yos.writeByte(decoraters.x);
			log.debug("decoraters.x:"+decoraters.x); 
			yos.writeByte(decoraters.y);
			log.debug("decoraters.y:"+decoraters.y);
			yos.writeByte(decoraters.z);
			log.debug("decoraters.z"+decoraters.z);
            log.debug("decoraters["+i+"] = " + decoraters.decorater);
            i++;
            //edit by zhengl; date: 2011-02-22; note: 仅发送默认动画ID
            if(decoraters.decorater != null)
            {
            	yos.writeByte(decoraters.decorater[0].decorateId);
            }
            else
            {
            	log.info("warn:策划未填写动画ID,mapid="+this.map.getID()+";mapname="+this.map.getName());
            	yos.writeByte(0);
            }
//			output.writeByte(decoraters.decorater.length); //装饰物的动作数量
//			for (Decorater d : decoraters.decorater) {
//				output.writeByte(d.decorateId); //动作ID
//			}

            //end
			//del:	zhengl
			//date:	2010-11-03
			//note:	装饰层暂时不考虑其装饰件功能,只考虑增加Z轴.
//			output.writeByte(decoraters.decorater.length);//对象上装饰件数量
//    		for(Decorater decorater : decoraters.decorater){
//    			output.writeShort(decorater.decorateId);
//    			output.writeByte(decorater.x);
//    			output.writeByte(decorater.y);
//    			output.writeByte(decorater.z);
//    		}
			//end
    	}
	}

}
