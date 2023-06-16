package hero.ui;

import java.io.IOException;

import yoyo.tools.YOYOOutputStream;


public class UI_EvidenveReceive {
	
    /**
     * 获取UI类型
     * 
     * @return
     */
    public static EUIType getType ()
    {
        return EUIType.EVIDENVE_RECEIVE;
    }
    
    public static EUIType getEndType ()
    {
        return EUIType.TIP_UI;
    }
    
    /**
     * 输入框格式化
     * @param inputLenghts
     * @param inputContents
     * @param _title
     * @return
     */
    public static byte[] getBytes(int[] inputLenghts, String[] inputContents, String _title)
    {
    	YOYOOutputStream output = new YOYOOutputStream();
    	
    	try 
    	{
    		output.writeByte(getType().getID());
    		output.writeUTF(_title);
    		output.writeByte(inputLenghts.length);
    		for (int i = 0; i < inputLenghts.length; i++) 
    		{
    			output.writeByte(inputLenghts[i]);
    			output.writeUTF(inputContents[i]);
			}
			output.flush();
		} 
    	catch (IOException e) 
    	{
			e.printStackTrace();
		}
    	return output.getBytes();
    }
    
    /**
     * 
     * @param _endContent
     * @param _award
     * @param _title
     * @return
     */
    public static byte[] getEndBytes(String _endContent, String[] _award, String _title)
    {
    	YOYOOutputStream output = new YOYOOutputStream();
    	
    	try 
    	{
    		output.writeByte(getEndType().getID());
    		output.writeUTF(_title);
    		String utf = "";
    		for (int i = 0; i < _award.length; i++) {
    			utf = _award[i] + "#HH";
			}
    		output.writeUTF(utf);
    		output.writeUTF(_endContent);
			output.flush();
		} 
    	catch (IOException e) 
    	{
			e.printStackTrace();
		}
    	return output.getBytes();
    }

}
