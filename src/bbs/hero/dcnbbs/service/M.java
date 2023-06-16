package hero.dcnbbs.service;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

public class M {

	private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	private static Logger log = Logger.getLogger(M.class);
	
	public static String md5(String text, String charSet) {
		MessageDigest msgDigest = null;

		try {
			msgDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(
					"System doesn't support MD5 algorithm.");
		}
		try {
			msgDigest.update(text.getBytes(charSet)); // ע��Ľӿ��ǰ���utf-8������ʽ����
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(
					"System doesn't support your  EncodingException.");
		}
		byte[] bytes = msgDigest.digest();
		String md5Str = new String(encodeHex(bytes));
		return md5Str;
	}

	public static String sha256(String text, String charSet) {
		MessageDigest msgDigest = null;
		try {
			msgDigest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(
					"System doesn't support SHA-256 algorithm.");
		}
		try {
			msgDigest.update(text.getBytes(charSet));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(
					"System doesn't support your  EncodingException.");
		}
		byte[] bytes = msgDigest.digest();
		return Bytes2HexString(bytes);
	}

	public static String Bytes2HexString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}

	public static String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}

	public static char[] encodeHex(byte[] data) {

		int l = data.length;

		char[] out = new char[l << 1];

		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
			out[j++] = DIGITS[0x0F & data[i]];
		}

		return out;
	}

	public static InputStream httpRequest(String urlvalue, String des) {
		try {
			URL url = new URL(urlvalue);
			System.out.println("url:" + urlvalue);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("content-type", "text/html");
			urlConnection.setRequestProperty("Accept-Charset", "utf-8");
			return urlConnection.getInputStream();
		} catch (Exception e) {
			log.error(des + "通信异常",e);
		}
		return null;
	}

	public static InputStream httpPostRequest(String content ,String urlvalue, String des) {
		HttpURLConnection c = null;
		InputStream instr = null;
		System.out.println("url:" + urlvalue);
		try {
			URL url = new URL(urlvalue);
			c = (HttpURLConnection) url.openConnection();
			c.setRequestMethod("POST");
			c.setRequestProperty("Accept-Charset", "utf-8");
			c.setDoOutput(true);
			c.setDoInput(true);
			c.connect();
			PrintWriter out = new PrintWriter(c.getOutputStream());// 发送数据
			System.out.println(content);
			out.print(content);
			out.flush();
			out.close();
			int res = 0;
			
			res = c.getResponseCode();
			if (res == 200) {
				instr = c.getInputStream();// 获取servlet返回值，接收
			} else {
				log.error("论坛post氢气数据异常code=" + res);
			}
		} catch (Exception e) {
			log.error("post请求数据异常", e);
		}
		return instr;
	}

	public static String getEncodeURL(String url, String enc, Class subClass) {
		try {
			return java.net.URLEncoder.encode(url, enc);
		} catch (UnsupportedEncodingException e) {
			log.error(subClass.getName() + "中getEncodeURL(url, enc):"
					+ enc + "为不支持的编码方式。", e);
		}
		return url;
	}
	
	public static String getTimeDes(String time) {
		Date date;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time.trim());
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(date.getTime());
			Calendar calendar2 = Calendar.getInstance();
			if(calendar.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)) {
				return "今天";
			}
			calendar2.add(Calendar.DAY_OF_YEAR, -1);
			if(calendar.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)) {
				return "昨天";
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "很久";
	}

}
