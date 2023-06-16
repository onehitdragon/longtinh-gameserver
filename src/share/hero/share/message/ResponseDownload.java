package hero.share.message;

import hero.share.clienthandler.RequestExchange;
import hero.share.service.AllPictureDataDict;

import java.io.IOException;

import org.apache.log4j.Logger;

import yoyo.core.packet.AbsResponseMessage;


public class ResponseDownload extends AbsResponseMessage {
	
	private static Logger log = Logger.getLogger(ResponseDownload.class);
//	private String fileName;
	
	private String fileURL;
	
//	private byte fileType;
	
	private byte[] file;
	
	public ResponseDownload(String _url, byte[] _file) {

		fileURL = _url;
		file = _file;
	}
	

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void write() throws IOException {
		yos.writeUTF(fileURL);
		yos.writeShort(file.length);
		yos.writeBytes(file);
		
	}

}
