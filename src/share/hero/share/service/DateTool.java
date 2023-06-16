package hero.share.service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * 日期格式化类
 * 
 * @version $vision: 1.0 $ $Date: 2005/05/20 16:40:28 $
 */
public class DateTool extends java.util.Date {
	private static final long serialVersionUID = 1L;
	private static SimpleDateFormat sFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static SimpleDateFormat sFormat2 = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sFormat3 = new SimpleDateFormat("yyyy/MM/dd");
	private static SimpleDateFormat sFormat4 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	private static SimpleDateFormat sFormat5 = new SimpleDateFormat("HH:mm");
	private static SimpleDateFormat sFormat6 = new SimpleDateFormat("h:mm a");
	private static SimpleDateFormat sFormat7 = new SimpleDateFormat("yyyyMMddHHmmss");
	private static SimpleDateFormat sFormat8 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat sFormat9 = new SimpleDateFormat("ddMMMyy");
	private static SimpleDateFormat sFormat10 = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat sFormat15 = new SimpleDateFormat("yyMM");
	private static SimpleDateFormat sFormat17 = new SimpleDateFormat("MM/dd/yyyy");
	private static SimpleDateFormat sFormat18 = new SimpleDateFormat("dd/MM/yyyy");
	
	/**
	 * 构造函数
	 */
	public DateTool() {
		super(getSystemDate().getTime().getTime());
	}

	/**
	 * 当前时间
	 * 
	 * @return 时间Timestamp
	 */
	public java.sql.Timestamp parseTime() {
		return new java.sql.Timestamp(this.getTime());
	}

	/**
	 * 当前时间
	 * 
	 * @return 时间Date
	 */
	public java.sql.Date parseDate() {
		return new java.sql.Date(this.getTime());
	}

	/**
	 * 取当前系统时间
	 * 
	 * @return 时间Calendar
	 */
	public static Calendar getSystemDate() {
		return Calendar.getInstance();
	}

	/*
	 * 取当前系统时间 @return 时间Calendar 
	 */ 
	/* public static Calendar getSystemDate() { ResultSet rs = null; Connection conn = null; Calendar cal = null; try { DataSource ds = (DataSource) IoC.get("DataSource");
	 * conn = ds.getConnection(); MyQueryRunner runner = new MyQueryRunner(); rs = runner.rsQuery(conn, SYSTEM_DATE_SQL, null); String sDate = ""; String sTime = ""; if (rs.next()) { String ss =
	 * rs.getString(1); sDate = ss.substring(0, 8); sTime = ss.substring(9); } if (!sDate.equals("") && !sTime.equals("")) { String sYear = sDate.substring(0, 4); String sMonth = sDate.substring(4,
	 * 6); String sDay = sDate.substring(6, 8); String sHH = sTime.substring(0, 2); String sMI = sTime.substring(3, 5); String sSS = sTime.substring(6, 8); cal = new
	 * GregorianCalendar(Integer.parseInt(sYear), Integer.parseInt(sMonth) - 1, Integer.parseInt(sDay), Integer.parseInt(sHH), Integer.parseInt(sMI), Integer.parseInt(sSS)); } else { cal =
	 * Calendar.getInstance(); } } catch (SQLException ex) { ex.printStackTrace(); } finally { try { if (rs != null) { rs.close(); rs = null; } if (conn != null) { conn.close(); conn = null; } } catch
	 * (Exception ee) { } } return cal; }
	 */

	/**
	 * 取得指定日期几天后的日期
	 * 
	 * @param date 日期
	 * @param afterDays 天数
	 * @return 日期
	 */
	public static java.util.Date getAfterDay(java.util.Date date, int afterDays) {
		GregorianCalendar cal = new GregorianCalendar();
		if (date == null) {
			cal.setTime(new DateTool());
		} else {
			cal.setTime(date);
		}
		cal.add(java.util.Calendar.DATE, afterDays);
		return cal.getTime();
	}

	/**
	 * 获得几个月后的日期 addby(yangwr) 2005/08/30
	 * 
	 * @param sDate 日期
	 * @param afterMonth 月数
	 * @return 日期"yyyy-MM-dd HH:mm"
	 */
	public static String getAfterMonth(String sDate, int afterMonth) {
		java.util.Date date = null;
		try {
			date = sFormat1.parse(sDate);
			date = getAfterMonth(date, afterMonth);
			return sFormat1.format(date);
		} catch (ParseException e) {

			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获得几个月后的日期
	 * 
	 * @param date 日期
	 * @param afterMonth 月数
	 * @return 日期Date
	 */
	public static java.util.Date getAfterMonth(java.util.Date date, int afterMonth) {
		GregorianCalendar cal = new GregorianCalendar();
		if (date == null) {
			cal.setTime(new DateTool());
		} else {
			cal.setTime(date);
		}
		cal.add(java.util.Calendar.MONTH, afterMonth);
		return cal.getTime();
	}

	/**
	 * 取得指定日期几天后的日期
	 * 
	 * @param sDate 日期
	 * @param afterDays 天数
	 * @return 日期
	 */
	public static String getAfterDay(String sDate, int afterDays) {
		java.util.Date date = null;
		try {
			date = convertLongDate(sDate);
		} catch (Exception e) {
			e.printStackTrace();
		}

		date = getAfterDay(date, afterDays);
		return formatDate(date);
	}

	/**
	 * 日期格式化
	 * 
	 * @param date 日期
	 * @return 日期"yyyy-MM-dd"
	 */
	public static String formatDate(java.util.Date date) {
		if (date == null) {
			return "";
		}
		return sFormat2.format(date);
	}

	/**
	 * 转换类型
	 * 
	 * @param sDate 日期"yyyy-MM-dd"
	 * @return 日期Date
	 */
	public static java.util.Date convertShortDate(String sDate) {
		try {
			return sFormat2.parse(sDate);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 转换类型
	 * 
	 * @param sDate 日期"yyyy-MM-dd HH:mm"
	 * @return 日期Date
	 * @throws Exception
	 */
	public static java.util.Date convertLongDate(String sDate) 
	{
		try {
			return sFormat1.parse(sDate);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	/**
	 * 转换类型
	 * 
	 * @param sDate 日期"yyyy-MM-dd HH:mm"
	 * @return 日期Timestamp
	 */
	public static Timestamp convertTimestamp1(String sDate) {
		long lDate = 0;

		try {
			lDate = convertLongDate(sDate).getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new Timestamp(lDate);

	}

	/**
	 * 转换类型
	 * 
	 * @param sDate 日期"yyyy-MM-dd"
	 * @return 日期Timestamp
	 */
	public static Timestamp convertTimestamp2(String sDate) {

		long lDate = 0;

		try {
			lDate = convertShortDate(sDate).getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new Timestamp(lDate);

	}

	// 2005-05-05 00:00:00
	/**
	 * 转换类型
	 * 
	 * @param sDate 日期
	 * @return 日期Timestamp
	 */
	public static Timestamp convertTimestamp(String sDate) {
		/*
		 * long lDate = 0; if(sDate.length() == 10){ try { lDate = convertDate(sDate).getTime(); } catch (Exception e) { e.printStackTrace(); } } return new Timestamp(lDate);
		 */
		if (sDate.length() == 10) {
			sDate = sDate + " 00:00:00";
		}

		return Timestamp.valueOf(sDate);
	}

	// 2005-05-05 23:59:59.0
	/**
	 * 转换类型
	 * 
	 * @param sDate 日期
	 * @return 日期Timestamp
	 */
	public static Timestamp convertTimestampE(String sDate) {

		if (sDate.length() == 10) {
			sDate = sDate + " 23:59:59.0";
		}

		return Timestamp.valueOf(sDate);
	}
	
	/**
	 * 毫秒数换算的天数,不足1天取0
	 * @param _time 
	 * @return
	 */
	public static int getDayByLong(long _time) {
		int result = 0;
		float second =  _time/1000;
		second = second/60/60/24;
		result = (int)second;
		return result;
	}
	
	/**
	 * 返回2个日期相差的天数
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int getDifference(java.util.Date date1, java.util.Date date2) {
		int day = 0;
		Calendar cld1 = Calendar.getInstance();
		Calendar cld2 = Calendar.getInstance();
		cld1.setTime(date1);
		cld2.setTime(date2);
		day = cld2.get(Calendar.DAY_OF_MONTH) - cld1.get(Calendar.DAY_OF_MONTH);
		return day;
	}

	/**
	 * 取得时间差
	 * 
	 * @param date1 日期1
	 * @param date2 日期2
	 * @return 日期2-日期1的毫秒时间差
	 */
	public static long getDateDifference(java.util.Date date1, java.util.Date date2) {
		Calendar cld1Work = Calendar.getInstance();
		Calendar cld2Work = Calendar.getInstance();
		Calendar cld1 = Calendar.getInstance();
		Calendar cld2 = Calendar.getInstance();
		long lTime1;
		long lTime2;

		cld1Work.setTime(date1);
		cld2Work.setTime(date2);
		cld1.clear();
		cld2.clear();
		cld1.set(cld1Work.get(Calendar.YEAR), cld1Work.get(Calendar.MONTH), cld1Work.get(Calendar.DATE));
		cld2.set(cld2Work.get(Calendar.YEAR), cld2Work.get(Calendar.MONTH), cld2Work.get(Calendar.DATE));
		lTime1 = (cld1.getTime()).getTime();
		lTime2 = (cld2.getTime()).getTime();

		return (lTime2 - lTime1) / (1000 * 60 * 60 * 24);
	}
	public static String getDateDifferenceToStr(String start_date,String end_date){
		String ret = "";
	    DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
	    
	    try{
	        Date first = format.parse(start_date);
	        Date second = format.parse(end_date);
	        ret = String.valueOf(getDateDifference(first,second)+1);
	        
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    
	    return ret;
		
	}
	
	/**
	 * 日期类型转化
	 * 
	 * @param sTsp 日期串
	 * @param iType 0:yyyy年M月d日； <br>
	 *            1:yyyy-M-d; <br>
	 *            2:yyyy/M/d <br>
	 *            3:yyyy年MM月dd日; <br>
	 *            4:yyyy-MM-dd; <br>
	 *            5:yyyy/MM/dd; <br>
	 *            6:yyyy-MM-dd HH:mm; <br>
	 *            7:yyyy/MM/dd HH:mm; <br>
	 *            8:HH:mm <br>
	 *            9:h:mm a <br>
	 *            10:yyyyMMdd <br>
	 *            11:yyyyMMddHHmmSS<br>
	 *            12:yyyy-MM-dd HH:mm:ss <br>
	 *            13:HH:mm:ss <br>
	 *            14:ddMMMyy <br>
	 *            15:yyMM
	 *            17:MM/dd/yyyy
	 *            18:dd/MM/yyyy
	 * @return 日期串
	 */
	public static String formatDate(String sTsp, int iType) {

		if (sTsp == null || "".equals(sTsp)) {
			return "";
		}

		if (sTsp.length() == 10) {
			return formatDate(convertTimestamp(sTsp), iType);
		} else if (sTsp.length() > 10) {
			String[] sDatas = sTsp.split("\\.");
			if (sDatas.length > 2) {
				if (sDatas.length > 1) {
					String[] sDates = sDatas[0].split("-");
					if (sDates[1].length() == 1)
						sDates[1] = "0" + sDates[1];
					if (sDates[2].length() == 1)
						sDates[2] = "0" + sDates[2];

					for (int i = 1; i < 4; i++) {
						sDatas[i] = sDatas[i].trim();
						if (sDatas[i].length() == 1)
							sDatas[i] = "0" + sDatas[i];
					}

					sTsp = sDates[0] + "-" + sDates[1] + "-" + sDates[2] + " " + sDatas[1] + ":" + sDatas[2] + ":" + sDatas[3] + ".000000000";
				}
			}

			return formatDate(Timestamp.valueOf(sTsp), iType);
		} else {
			return "";
		}
	}

	/**
	 * 日期类型转化
	 * 
	 * @param tsp 日期
	 * @param iType 0:yyyy年M月d日； <br>
	 *            1:yyyy-M-d; <br>
	 *            2:yyyy/M/d <br>
	 *            3:yyyy年MM月dd日; <br>
	 *            4:yyyy-MM-dd; <br>
	 *            5:yyyy/MM/dd; <br>
	 *            6:yyyy-MM-dd HH:mm; <br>
	 *            7:yyyy/MM/dd HH:mm; <br>
	 *            8:HH:mm <br>
	 *            9:h:mm a <br>
	 *            10:yyyyMMdd <br>
	 *            11:yyyyMMddHHmmSS<br>
	 *            12:yyyy-MM-dd HH:mm:ss <br>
	 *            13:HH:mm:ss <br>
	 *            14:ddMMMyy <br>
	 *            15:yyMM <br>
	 *            16:上一个月日期yyyyMMdd
	 *            17:MM/dd/yyyy
	 *            18:dd/MM/yyyy
	 * @return 日期串
	 */
	public static String formatDate(Timestamp tsp, int iType) {

		GregorianCalendar cal = new GregorianCalendar();
		// java.util.Date dDate = null;

		if (tsp == null) {
			cal.setTime(new DateTool());
		} else {
			cal.setTime(tsp);
		}

		String sDate = "";
		// 0:yyyy年M月d日

		if (iType == 0) {
			int iYear = cal.get(Calendar.YEAR);
			int iMonth = cal.get(Calendar.MONTH) + 1;
			int iDay = cal.get(Calendar.DAY_OF_MONTH);

			sDate = "" + iYear + "年" + iMonth + "月" + iDay + "日";
		}
		// 1:yyyy-M-d
		if (iType == 1) {
			int iYear = cal.get(Calendar.YEAR);
			int iMonth = cal.get(Calendar.MONTH) + 1;
			int iDay = cal.get(Calendar.DAY_OF_MONTH);

			sDate = "" + iYear + "-" + iMonth + "-" + iDay;
		}
		// 2:yyyy/M/d
		if (iType == 2) {
			int iYear = cal.get(Calendar.YEAR);
			int iMonth = cal.get(Calendar.MONTH) + 1;
			int iDay = cal.get(Calendar.DAY_OF_MONTH);

			sDate = "" + iYear + "/" + iMonth + "/" + iDay;
		}
		// 3:yyyy年MM月dd日;
		if (iType == 3) {
			String strYear = String.valueOf(cal.get(Calendar.YEAR));
			String strMonth = String.valueOf(cal.get(Calendar.MONTH) + 1);
			strMonth = "00" + strMonth;
			strMonth = strMonth.substring(strMonth.length() - 2, strMonth.length());
			String strDay = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
			strDay = "00" + strDay;
			strDay = strDay.substring(strDay.length() - 2, strDay.length());

			sDate = "" + strYear + "年" + strMonth + "月" + strDay + "日";
		}
		// 4:yyyy-MM-dd;
		if (iType == 4) {
			sDate = sFormat2.format(cal.getTime());
		}
		// 5:yyyy/MM/dd;
		if (iType == 5) {
			sDate = sFormat3.format(cal.getTime());
		}
		// 6:yyyy-MM-dd HH:mm;
		if (iType == 6) {
			sDate = sFormat1.format(cal.getTime());
		}
		// 7:yyyy/MM/dd HH:mm;
		if (iType == 7) {
			sDate = sFormat4.format(cal.getTime());
		}
		// 8:HH:mm
		if (iType == 8) {
			sDate = sFormat5.format(cal.getTime());
		}
		// 9:h:mm a
		if (iType == 9) {
			sDate = sFormat6.format(cal.getTime());
		}
		// 10:h:mm a
		if (iType == 10) {
			sDate = sFormat10.format(cal.getTime());
		}
		if (iType == 11) {
			sDate = sFormat7.format(cal.getTime());
		}
		// 12:yyyy-MM-dd HH:mm:ss;
		if (iType == 12) {
			sDate = sFormat8.format(cal.getTime());
		}
		// 13:HH:mm:ss;
		if (iType == 13) {
			sDate = sFormat8.format(cal.getTime());
			sDate = sDate.substring(10);
		}
		// 14:ddMMMyy
		if (iType == 14) {
			sDate = sFormat9.format(cal.getTime());
		}
		// 15:yyMM
		if (iType == 15) {
			sDate = sFormat15.format(cal.getTime());
		}
		if (iType == 16) {
			int iYear = cal.get(Calendar.YEAR);
			int iMonth = cal.get(Calendar.MONTH) + 1;
			int iDay = cal.get(Calendar.DAY_OF_MONTH);
			switch (iMonth) {
			case 1: {
				cal.set(iYear, Calendar.JANUARY, iDay);
				break;
			}
			case 2: {
				cal.set(iYear, Calendar.FEBRUARY, iDay);
				break;
			}
			case 3: {
				cal.set(iYear, Calendar.MARCH, iDay);
				break;
			}
			case 4: {
				cal.set(iYear, Calendar.APRIL, iDay);
				break;
			}
			case 5: {
				cal.set(iYear, Calendar.MAY, iDay);
				break;
			}
			case 6: {
				cal.set(iYear, Calendar.JUNE, iDay);
				break;
			}
			case 7: {
				cal.set(iYear, Calendar.JULY, iDay);
				break;
			}
			case 8: {
				cal.set(iYear, Calendar.AUGUST, iDay);
				break;
			}
			case 9: {
				cal.set(iYear, Calendar.SEPTEMBER, iDay);
				break;
			}
			case 10: {
				cal.set(iYear, Calendar.OCTOBER, iDay);
				break;
			}
			case 11: {
				cal.set(iYear, Calendar.NOVEMBER, iDay);
				break;
			}
			case 12: {
				cal.set(iYear, Calendar.DECEMBER, iDay);
				break;
			}
			}
			cal.add(Calendar.MONTH, -1);
			sDate = sFormat2.format(cal.getTime());

		}
		// 17:MM/dd/yyyy
		if (iType == 17) {
			sDate = sFormat17.format(cal.getTime());
		}
		
		// 18:dd/MM/yyyy
		if (iType == 18) {
			sDate = sFormat18.format(cal.getTime());
		}
		return sDate;
	}

	/**
	 * 取得n分钟前的时间
	 * 
	 * @param lminute
	 * @return 时间Timestamp
	 */
	public static Timestamp gettimebefore(long lminute) {

		Timestamp tsp = new DateTool().parseTime();

		long lngTime = tsp.getTime() - lminute * 60 * 1000;

		return new Timestamp(lngTime);

	}

	/**
	 * 取得n分钟前的时间
	 * 
	 * @param date 日期
	 * @param lminute
	 * @return 时间Timestamp
	 */
	public static Timestamp gettimebefore(java.util.Date date, long lminute) {

		long lngTime = date.getTime() - lminute * 60 * 1000;

		return new Timestamp(lngTime);

	}

	/**
	 * 获取国内机票航程时间 <br>
	 * 如果结束时间小于开始时间，按照第二天算
	 * 
	 * @param start 四位数字字符串
	 * @param end 四位数字字符串
	 * @return 例1小时30分钟
	 */
	public static String getFlyingTime(String start, String end) {
		int iSt = Integer.valueOf(start).intValue();
		int iEn = Integer.valueOf(end).intValue();
		String rtn = null;
		// 是否第二天
		if (iEn <= iSt) {
			iEn += 2400;
		}
		// 小时
		int hour = iEn / 100 - 1 - iSt / 100;
		// 分钟
		int mini = iEn % 100 + 60 - iSt % 100;

		if (mini >= 60) {
			mini -= 60;
			hour += 1;
		}
		if (mini == 0) {
			rtn = hour + "小时";
		} else if (hour == 0) {
			rtn = mini + "分钟";
		} else {
			rtn = hour + "小时" + mini + "分钟";
		}
		return rtn;
	}

	/**
	 * 获取国内机票航程时间(外文版) <br>
	 * 如果结束时间小于开始时间，按照第二天算
	 * 
	 * @param start 四位数字字符串
	 * @param end 四位数字字符串
	 * @return 例1小时30分钟
	 */
	public static String getMultiLangFlyingTime(String start, String end) {
		int iSt = Integer.valueOf(start).intValue();
		int iEn = Integer.valueOf(end).intValue();
		String rtn = null;
		// 是否第二天
		if (iEn <= iSt) {
			iEn += 2400;
		}
		// 小时
		int hour = iEn / 100 - 1 - iSt / 100;
		// 分钟
		int mini = iEn % 100 + 60 - iSt % 100;

		if (mini >= 60) {
			mini -= 60;
			hour += 1;
		}
		if (mini == 0) {
			rtn = hour + "hr ";
		} else if (hour == 0) {
			rtn = mini + "mn";
		} else {
			rtn = hour + "hr " + mini + "mn";
		}

		return rtn;
	}

	/**
	 * 获取国内机票航程时间 <br>
	 * 如果结束时间小于开始时间，按照第二天算
	 * 
	 * @param start 四位数字字符串
	 * @param end 四位数字字符串
	 * @return 飞行时间（分钟）
	 */
	public static int getFlyingMunites(String start, String end) {
		int iSt = Integer.valueOf(start).intValue();
		int iEn = Integer.valueOf(end).intValue();
		int rtn = 0;
		// 是否第二天
		if (iEn <= iSt) {
			iEn += 2400;
		}
		// 小时
		int hour = iEn / 100 - 1 - iSt / 100;
		// 分钟
		int mini = iEn % 100 + 60 - iSt % 100;

		rtn = hour * 60 + mini;

		return rtn;
	}

	/**
	 * 将java.sql.timestamp，<br>
	 * 转换为用于sql的字符串(to_timestamp('2007-04-18 00:00:00.0' , 'yyyy-mm-dd hh24:mi:ssxff'))
	 * 
	 * @param timestamp 待转换的timestamp
	 * @return 例如to_timestamp('2007-04-18 00:00:00.0' , 'yyyy-mm-dd hh24:mi:ssxff')
	 */
	public static String toTimeStamp(Timestamp timestamp) {
		String sql = "to_timestamp('" + timestamp.toString() + "' , 'yyyy-mm-dd hh24:mi:ssxff')";
		return sql;
	}

	/**
	 * 将格式如2007-04-18 00:00:00.0的字符串，<br>
	 * 转换为用于sql的字符串(to_timestamp('2007-04-18 00:00:00.0' , 'yyyy-mm-dd hh24:mi:ssxff'))
	 * 
	 * @param s 待转换的字符串
	 * @return 例如to_timestamp('2007-04-18 00:00:00.0' , 'yyyy-mm-dd hh24:mi:ssxff')
	 */
	public static String toTimeStamp(String s) {
		String sql = "to_timestamp('" + s + "' , 'yyyy-mm-dd hh24:mi:ssxff')";
		return sql;
	}

	/**
	 * 计算 给定日期的当月最后一天并显示
	 * 
	 * @param s 日期
	 * @return 最后一天
	 */
	public static String LastDay(String s) {
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sFormat.parse(s);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GregorianCalendar gc = new GregorianCalendar();
		if (date == null)
			gc.setTime(new DateTool());
		else
			gc.setTime(date);

		gc.add(Calendar.MONTH, 1);
		gc.add(Calendar.DATE, -date.getDate());
		DateFormat df = DateFormat.getDateInstance();
		Date dateTemp = gc.getTime();
		return df.format(dateTemp);
	}
	
	
	/**
	 * 计算 给定日期的当月最后一天并显示
	 * @param s 日期
	 * @return 最后一天
	 */
	public static String getMonthLastDay(String s) {
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sFormat.parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		GregorianCalendar gc = new GregorianCalendar();
		if (date == null)
			gc.setTime(new DateTool());
		else
			gc.setTime(date);
		
		gc.add(Calendar.MONTH, 1);
		gc.add(Calendar.DATE, -date.getDate());
		
		DateFormat df = DateFormat.getDateInstance();
		Date dateTemp = gc.getTime();		
		return sFormat.format(dateTemp);
	}
	
	/**
	 * 日期类型转化
	 * @param birthDay yyyy-mm-dd
	 * @return
	 */
	public static String getAge(String sBirthDay){
		if(sBirthDay==null || sBirthDay.equals("")){
			return "";
		}
		
		int age=0;
		//今天
		Date currentDate = (new DateTool()).parseDate();
				
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		
		Date birthDay = DateTool.convertLongDate(sBirthDay);
        if (cal.before(birthDay)) {
            throw new IllegalArgumentException(
                "The birthDay is before Now.It's unbelievable!");
        }

        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        
        //生日
        cal.setTime(birthDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                //monthNow==monthBirth
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                } else {
                    //do nothing
                }
            } else {
                //monthNow>monthBirth
                age--;
            }
        } else {
            //monthNow<monthBirth
            //donothing
        }
        if(age==0){
        	return "婴儿";
        }
		return String.valueOf(age);
		
	}
	/**
	 * 取当前日期(yyyy-mm-dd)
	 * 
	 * @return 时间Timestamp
	 */
	public static String getTodayDate() {
		return formatDate(new DateTool().parseTime(),4);
	}

	public static void main(String[] args){
		getDayByLong(1*23*60*60*1000);
//		System.out.println(formatDate("2009-05-01 20:30:00","EN",4));
//		System.out.println(formatDate("2009-05-01 20:30:00","DE",4));
//		System.out.println(formatDate("2009-05-01 20:30:00","ES",4));
//		System.out.println(formatDate("2009-05-01 20:30:00","FR",4));
//		System.out.println(formatDate("2009-05-01 20:30:00","IT",4));
//		System.out.println(formatDate("2009-05-01 20:30:00","JP",4));
//		
//		Calendar cal = Calendar.getInstance();
//		cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt("20:30".substring(0,2)));
//		cal.set(Calendar.MINUTE,Integer.parseInt("20:30".substring(3)));
//		 
//		String departDatetime = CmnUtDate.formatDate(new Timestamp(cal.getTime().getTime()),"EN",4);
//		System.out.println(departDatetime);
	}
}

