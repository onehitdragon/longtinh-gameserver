package hero.share.message;

import java.io.IOException;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 Warning.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2009-7-17 下午04:32:38
 * @描述 ：
 */

public class Warning extends AbsResponseMessage
{
    private static Logger log = Logger.getLogger(Warning.class);
    /**
     * 警告内容
     */
    private String warningContent;
    
    private byte uiType;
    
    /**
     * 需要展示在UI上的倒计时时间(秒)
     */
    private short time;

    /**
     * 是否全屏显示
     * 默认 false
     */
    private boolean fullScreen = false;
    
    private byte event;
    
    private int  signID;

    private short returnCommandCode;
    
    public static final byte UI_STRING_TIP = 0;
    
    public static final byte UI_TOOLTIP_TIP = 1;
    
    public static final byte UI_TOOLTIP_AND_EVENT_TIP = 2;
    
    public static final byte UI_COMPLEX_TIP = 3;

    /**
     * 带确认和返回按钮都返回值的确认框，点"确认"返回1，点"返回"返回0
     */
    public static final byte UI_TOOLTIP_CONFIM_CANCEL_TIP = 4;
    
    /**
     * 弹出商城事件标记
     */
    public static final byte SUBFUNCTION_UI_POPUP_REVIVE_CHARGE = 0;
    
    /**
     * 弹出通用商城事件标记
     */
    public static final byte SUBFUNCTION_UI_POPUP_COMM_CHARGE = 1;
    
    /**
     * 弹出任务推广事件
     * *事件1:请求网游服务器
     * *事件2:对网游服务器发起接受推广请求
     */
    public static final byte SUBFUNCTION_UI_TASK_PUSH_COMM = 2;
    
    /**
     * 弹出常规确认事件
     * *事件:回发请求确认真的购买这个推广道具(服务器即时响应)
     */
    public static final byte SUBFUNCTION_UI_TASK_PUSH_COMM_CONFIRM = 3;
    
    /**
     * 弹特定确认事件
     * *事件1:客户端(1:上行1条短信xx到xxxx; 2:下载某某jar包的y%) 
     * *事件2:回发请求确认真的购买这个推广道具(服务器不会即时响应,因为需要其他扣费点服务器确认)
     */
    public static final byte SUBFUNCTION_UI_TASK_PUSH_COMM_SPECIAL = 4;

    /**
     * 充值使用
     */
    public static final byte SUBFUNCTION_UI_CHARGEUP = 5;

    /**
     * 弹出NPC传送确认弹出框
     */
	public static final byte EVENT_SERVER_TRANSFER_NPC = 6;

	/**
     * 弹出地图传送确认弹出框
     */
	public static final byte EVENT_SERVER_TRANSFER_MAP = 7;

    /**
     * "确定"和"返回"按钮都返回值的确认框
     * "确定"返回 1
     * "返回"返回 0
     */
    public static final byte EVENT_SERVER_NEED_RETURN = 8;


    /**
     * 弹出带"确认"和"返回"按钮的框，并且点出"确认"或"返回"都返回值
     * 点"确认"返回 1
     * 点"返回"返回 0
     * @param _content
     * @param _uiType
     * @param _commandCode 返回时请求的报文号
     */
    public Warning(String _content,byte _uiType, short _commandCode){
        warningContent = _content;
        uiType = _uiType;
        returnCommandCode = _commandCode;
        event = EVENT_SERVER_NEED_RETURN;
    }
    
    /**
     * 构造
     * <p>
     * 需要再次交互的复杂类型的弹出框
     * @param _content
     */
    public Warning(String _content, byte _uiType, byte _event, int _signID, int _time)
    {
        warningContent = _content;
        uiType = _uiType;
        event = _event;
        signID = _signID;
        time = (short)_time;
    }

    /**
     * 构造
     * <p>
     * 带确认事件和返回的弹出框
     * @param _content
     */
    public Warning(String _content, byte _uiType, byte _event)
    {
        warningContent = _content;
        uiType = _uiType;
        event = _event;
    }
    
    /**
     * 构造
     * <p>
     * 带确认和返回的弹出框
     * @param _content
     */
    public Warning(String _content, byte _uiType)
    {
        warningContent = _content;
        uiType = _uiType;
    }
    
    /**
     * 构造
     * <p>
     * 纯文本tip
     * @param _content
     */
    public Warning(String _content)
    {
    	uiType = UI_STRING_TIP; //默认为0
        warningContent = _content;
    }

    /**
     * 如果提示的内容需要全屏显示，则调用此方法，设置 fullScreen = true;
     */
    public void fullScreenShow(){
        fullScreen = true;
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
//        log.debug("warning ui type="+uiType+",content="+warningContent);
    	//add by zhengl; date: 2011-03-10; note: 添加提示类型,可以让客户端弹出各种类型的文本提示框
    	yos.writeByte(uiType);
        yos.writeUTF(warningContent);
        yos.writeByte(fullScreen);
        //add by zhengl; date: 2011-03-21; note: 添加带事件标记的文本提示框.
        if(uiType == UI_TOOLTIP_AND_EVENT_TIP)
        {
        	yos.writeByte(event);
        }
        else if (uiType == UI_COMPLEX_TIP) 
        {
			//复杂类型
        	yos.writeByte(event);
        	yos.writeInt(signID); //客户端再次发起请求附带的标记ID
        	yos.writeShort(time);
		}
        else if(uiType == UI_TOOLTIP_CONFIM_CANCEL_TIP){
            yos.writeByte(EVENT_SERVER_NEED_RETURN);
            yos.writeShort(returnCommandCode);
        }
    }

}
