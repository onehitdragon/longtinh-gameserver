package hero.dcnbbs.service;

import hero.dcnbbs.Topic;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class DCNService {
	private static Logger log = Logger.getLogger(DCNService.class);

	public static final String ROOT_URL = "http://202.142.19.66:8877/connect/";// "http://app.d.cn/connect/";//http://202.142.19.66:8877/connect/

	public static final String LOGIN_URL = ROOT_URL + "member/login";// login

	// url

	public static final String SYNC_URL = ROOT_URL + "member/autosync";// sync

	// url

	public static final String BBS_TOPIC_LIST_URL = ROOT_URL + "forum/list";// bbs

	// topic
	// list

	public static final String TOPIC_URL = ROOT_URL + "forum/show-";// topic url

	public static final String NEW_TOPIC_URL = ROOT_URL + "forum/new-topic";// new

	// topic

	public static final String REPLY_TOPIC_URL = ROOT_URL + "forum/reply-";// new

	// topic

	public static final String UPLOAD_GAMEIMG_URL = ROOT_URL + "forum/game-img";// new

	// topic

	public static final String NEW_NOTE = ROOT_URL + "notice/new-note";// new

	// note

	public static final String API_KEY = "40"; // 应用ID

	public static final String CHARSET = "utf-8";

	private static final String KEY = "sdf5432c"; // 密钥KEY

	public static final String CHANNEL_ID = "1001";// 渠道ID

	public static final int PAGE_NUM = 10;

	/**
	 * 获取当乐渠道的登录url
	 * 
	 * @author rlj
	 * @param mid
	 * @param username
	 * @param pwd
	 * @return
	 */
	public static String getLoginUrl(String mid, String username, String pwd) {
		StringBuffer sbu = new StringBuffer();
		try {
			long time = System.currentTimeMillis();
			String verString = new StringBuffer().append("api_key=").append(
					API_KEY).append("&call_id=").append(time).append("&mid=")
					.append(mid).append("&username=").append(
							java.net.URLEncoder.encode(username, CHARSET))
					.toString();
			String sig = DCNService.getSign(
					new StringBuffer().append(verString).append("&sha256_pwd=")
							.append(M.sha256(pwd, CHARSET).toUpperCase())
							.append("&secret_key=").append(KEY).toString())
					.toUpperCase();
			String vc = DCNService.getSign(
					new StringBuffer().append(verString).append("&sig=")
							.append(sig).toString()).toUpperCase();
			sbu.append(LOGIN_URL).append("?").append(verString).append("&vc=")
					.append(vc).append("&sig=").append(sig);
			log.info("login_url" + sbu.toString());
		} catch (Exception ex) {
			log.error("生成登录得url", ex);
		}
		return sbu.toString();
	}

	/**
	 * 登录当乐接口
	 * 
	 * @author rlj
	 * @param mid
	 * @param username
	 * @param pwd
	 * @return
	 */
	public static Result login(String mid, String username, String pwd) {
		InputStream reStr = M.httpRequest(getLoginUrl(mid, username, pwd),
				"当乐用户" + mid + username + "登录");
		Result result = new Result();
		result.setResult(false);
		result.setReList("");
		if (reStr != null) {
			try {
				SAXReader reader = new SAXReader();
				Document doc = reader.read(reStr);
				Element root = doc.getRootElement();
				if (root != null) {
					Attribute attribute = root.attribute("status");
					if (attribute != null) {
						String status = attribute.getValue();
						if (status.equals("0")) {
							Element user = root.element("user");
							String mid2 = user.element("mid").getText();
							result.setResult(true);
							result.setReList(mid2);
							result.setDjtk(user.element("djtk").getText());
							log.info("mid:" + mid2);
						} else {
							result.setReList(status);
						}
					}
				}
			} catch (Exception e) {
				log.error("当乐登陆接口", e);
			}
		} else {
			log.error("当乐登录请求返回" + reStr);
		}
		return result;
	}

	/**
	 * 获取当乐的同步url
	 * 
	 * @author rlj
	 * @param mid
	 * @param username
	 * @param pwd
	 * @return
	 */
	public static String getSyncUrl(String unique_id, String playerName,
			String pwd) {
		long time = System.currentTimeMillis();
		StringBuffer ver = new StringBuffer();
		try {
			ver.append("api_key=").append(API_KEY).append("&call_id=").append(time)
					.append("&unique_id=").append(unique_id).append("&show_name=")
					.append(java.net.URLEncoder.encode(playerName, CHARSET)).append("&sha256_pwd=").append(M.sha256(pwd, CHARSET).toUpperCase());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String sig = DCNService.getSign(
				new StringBuffer().append(ver).append(
								"&secret_key=").append(KEY).toString()).toUpperCase();
		StringBuffer sbu = new StringBuffer();
		sbu.append(SYNC_URL).append("?").append(ver.toString()).append("&sig=").append(sig);
		log.info(sbu.toString());
		return sbu.toString();
	}


	/**
	 * 发起同步
	 * 
	 * @author rlj
	 * @param mid
	 * @param username
	 * @param pwd
	 * @return
	 */
	public static Result sys(String unique_id, String playerName, String pwd) {
		InputStream reStr = M.httpRequest(
				getSyncUrl(unique_id, playerName, pwd), "当乐同步用户unique_id"
						+ unique_id);
		Result result = new Result();
		result.setResult(false);
		result.setReList("");
		if (reStr != null) {
			try {
				SAXReader reader = new SAXReader();
				Document doc = reader.read(reStr);
				Element root = doc.getRootElement();
				if (root != null) {
					Attribute attribute = root.attribute("status");
					if (attribute != null) {
						String status = attribute.getValue();
						if (status.equals("0")) {
							Element user = root.element("user");
							result.setResult(true);
							result.setReList(user.element("mid").getText());
							result.setDjtk(user.element("djtk").getText());
						} else {
							result.setReList(status);
						}
					}
				}
			} catch (Exception e) {
				log.error("当乐用户同步接口:", e);
			}
		} else {
			log.info("当乐登录请求返回" + reStr);
		}
		return result;
	}

	public static String getNewNoteUrl() {
		long time = System.currentTimeMillis();
		StringBuffer ver = new StringBuffer();
		ver.append("api_key=").append(API_KEY).append("&call_id=").append(time);

		String sig = DCNService.getSign(
				new StringBuffer().append(ver).append("&secret_key=").append(
						KEY).toString()).toUpperCase();
		StringBuffer sbu = new StringBuffer();
		sbu.append(NEW_NOTE).append("?").append(ver.toString()).append("&sig=")
				.append(sig);
		log.info(sbu.toString());
		return sbu.toString();
	}

	public static Result newNote(String content) {
		InputStream instr = null;
		try {
			instr = M.httpPostRequest("content=" + URLEncoder.encode(content, CHARSET),
					getNewNoteUrl(), "");
		} catch (UnsupportedEncodingException e1) {
			log.error("当乐发公告接口",e1);
		}
		Result result = new Result();
		result.setResult(false);
		result.setReList("");
		if (instr != null) {
			try {
				SAXReader reader = new SAXReader();
				Document doc = reader.read(instr);
				Element root = doc.getRootElement();
				if (root != null) {
					Attribute attribute = root.attribute("status");
					if (attribute != null) {
						String status = attribute.getValue();
						if (status.equals("0")) {
							result.setResult(true);
							result.setReList("");
						} else {
							result.setReList(status);
						}
					}
				}
			} catch (Exception e) {
				log.error("当乐发公告接口",e);
			}
		} else {
			log.error("当乐发公告接口发送公告后无数据返回");
		}
		return result;
	}

	public static String getForumListUrl(String pageno, String pagesize, String djtk) {
		long time = System.currentTimeMillis();
		StringBuffer ver = new StringBuffer();
		ver.append("api_key=").append(API_KEY).append("&call_id=").append(time)
				.append("&page_no=").append(pageno).append("&page_size=")
				.append(pagesize).append("&djtk=").append(djtk);

		String sig = DCNService.getSign(
				new StringBuffer().append(ver).append("&secret_key=").append(
						KEY).toString()).toUpperCase();
		StringBuffer sbu = new StringBuffer();
		sbu.append(BBS_TOPIC_LIST_URL).append("?").append(ver.toString())
				.append("&sig=").append(sig);
		log.info(sbu.toString());
		return sbu.toString();
	}
	
	public static List<Topic> getForumList(int pageno, String djtk) {
		List<Topic> topicList = new ArrayList<Topic>();
		if (pageno <= 0) {
			pageno = 1;
		}
		long time3 = System.currentTimeMillis();
		InputStream reStr = M.httpRequest(getForumListUrl(pageno + "", PAGE_NUM + "", djtk), "获得论坛帖子列表");
		 if (reStr != null) {
			try {
				SAXReader reader = new SAXReader();
				Document doc = reader.read(reStr);
				Element root = doc.getRootElement();
				if (root != null) {
					Attribute attribute = root.attribute("STATUS");
					if (attribute != null) {
						String status = attribute.getValue();
						if (status.equals("0")) {
							Element data = root.element("DATA");
							List list = data.elements();
							if(list.size() > 0) {
								for(Iterator it = list.iterator(); it.hasNext();) {
									Element elem = (Element) it.next();
									Topic topic = new Topic();
									topic.setPostId(elem.element("POSTID").getText());
									topic.setTitle(elem.element("TITLE").getText());
									topic.setCreatedByInfo(elem.element("CREATED_BY_INFO").getText());
									topic.setDateTime(M.getTimeDes(elem.element("EDITDATE").getText()));
									topicList.add(topic);
								}
							}
						} else {
							log.info("获取论坛帖子列表失败 status=" + status);
							return topicList;
						}
					}
				}
			} catch (Exception e) {
				log.error("当乐用户同步接口:", e);
			}
		} else {
			log.error("当乐登录请求返回" + reStr);
		}
		System.out.println("获取论坛帖子列表时长：（ms）" + (System.currentTimeMillis() - time3));
		return topicList;
	}

	public static String getShowForumUrl(String topic_id, String djtk) {
		long time = System.currentTimeMillis();
		StringBuffer ver = new StringBuffer();
		ver.append("api_key=").append(API_KEY).append("&call_id=").append(time)
				.append("&djtk=").append(djtk);
		String sig = DCNService.getSign(
				new StringBuffer().append(ver).append("&secret_key=").append(
						KEY).toString()).toUpperCase();
		StringBuffer sbu = new StringBuffer();
		sbu.append(TOPIC_URL).append(topic_id).append("?").append(
				ver.toString()).append("&sig=").append(sig);
		log.info(sbu.toString());
		return sbu.toString();
	}

	public static Topic getForum(String topic_id, String djtk) {
		Topic topic = null;
		long time3 = System.currentTimeMillis();
		InputStream reStr = M.httpRequest(getShowForumUrl(topic_id, djtk),
				"获得论坛帖子信息");
		if (reStr != null) {
			try {
				long time4 = System.currentTimeMillis();
				SAXReader reader = new SAXReader();
				Document doc = reader.read(reStr);
				Element root = doc.getRootElement();
				if (root != null) {
					Attribute attribute = root.attribute("STATUS");
					if (attribute != null) {
						String status = attribute.getValue();
						if (status.equals("0")) {
							topic = new Topic();
							Element data = root.element("DATA");
							Element dataRow = data.element("ROW");
							topic.setPostId(dataRow.element("POSTID").getText());
							topic.setForumId(dataRow.element("FORUMID").getText());
							topic.setTitle(dataRow.element("TITLE").getText());
							topic.setContent(dataRow.element("MESSAGE").getText());
							String time = dataRow.element("EDITDATE").getText();
							topic.setDateTime(M.getTimeDes(time));
							topic.setCreatedByInfo(dataRow.element("CREATED_BY_INFO").getText());
							
							Element newbanch = root.element("NEWBRANCH");
							Element bdata = newbanch.element("DATA");
							List<Element> list = bdata.elements(); 
							List<Topic> topicList = new ArrayList<Topic>();
							for(Element el : list) {
								Topic topic2 = new Topic();
								topic2.setContent(el.element("MESSAGE").getText());
								topic2.setCreatedByInfo(el.element("CREATED_BY_INFO").getText());
								String time2 = el.element("DATETIME").getText();
								topic2.setDateTime(M.getTimeDes(time2));
								topicList.add(topic2);
							}
							topic.setReplyTopicList(topicList);
							System.out.println("解析xml单个帖时长：（ms）" + (System.currentTimeMillis() - time4));
						} else {
							log.error("获取论坛帖子详细信息失败topicid=" + topic_id + "status=" + status);
							return null;
						}
					}
				}
			} catch (Exception e) {
				log.error("当乐用户同步接口:", e);
			}
		} else {
			log.error("当乐登录请求返回" + reStr);
		}
		System.out.println("解析单个帖时长：（ms）" + (System.currentTimeMillis() - time3));
		return topic;
		
	}

	public static String getNewTopicUrl(String mid, String djtk, String pwd,
			String imgs) {
		long time = System.currentTimeMillis();
		StringBuffer ver = new StringBuffer();
		ver.append("api_key=").append(API_KEY).append("&call_id=").append(time)
				.append("&mid=").append(mid).append("&djtk=").append(djtk);
		String s1 = new StringBuffer().append(ver).append("&sha256_pwd=")
				.append(
						(pwd != null && pwd.length() > 0) ? M.sha256(pwd,
								CHARSET).toUpperCase() : "").append(
						"&secret_key=").append(KEY).append(
						(imgs != null && imgs.length() > 0) ? "&imgs=" + imgs
								: "").toString();
		// api_key=40&call_id=1305256633890&mid=&djtk=jl4c022wq1rmbu54x4164&sha256_pwd=E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855&secret_key=sdf5432c
		String sig = DCNService.getSign(s1).toUpperCase();
		StringBuffer sbu = new StringBuffer();
		sbu.append(NEW_TOPIC_URL).append("?").append(ver.toString()).append(
				"&sig=").append(sig);
		log.info(sbu.toString());
		return sbu.toString();
	}

	public static Result newTopic(String mid, String djtk, String pwd,
			String title, String content, String ip, String showName,
			String imgs, String imgsdes) {
		StringBuffer message = null;
		try {
			message = new StringBuffer().append("title=").append(
					URLEncoder.encode(title, CHARSET)).append("&content=")
					.append(URLEncoder.encode(content, CHARSET)).append("&ip=")
					.append(ip).append("&show_name=").append(
							URLEncoder.encode(showName, CHARSET)).append(
							"&imgs=").append(imgs != null && imgs.length() > 0 ? URLEncoder.encode(imgs, CHARSET) : "")
					.append("&imgs_desc=").append(
							imgsdes != null && imgsdes.length() > 0 ? URLEncoder.encode(imgsdes, CHARSET) : "");
		} catch (UnsupportedEncodingException e) {
			log.error("当乐接口发新帖失败",e);
		}
		InputStream instr = M.httpPostRequest(message.toString(),
				getNewTopicUrl(mid, djtk, pwd, imgs), "");
		Result result = new Result();
		result.setResult(false);
		result.setReList("");
		if (instr != null) {
			try {
				SAXReader reader = new SAXReader();
				Document doc = reader.read(instr);
				Element root = doc.getRootElement();
				if (root != null) {
					Attribute attribute = root.attribute("STATUS");
					if (attribute != null) {
						String status = attribute.getValue();
						if (status.equals("0")) {
							result.setResult(true);
							result.setReList("发帖成功");
						} else {
							result.setReList(status);
						}
					}
				}
			} catch (Exception e) {
				log.error("当乐发帖接口异常",e);
			}
		} else {
			log.error("发帖后无数据返回");
		}
		return result;
	}

	public static String getReplyTopicUrl(String topic_id, String mid,
			String djtk, String pwd) {
		long time = System.currentTimeMillis();
		StringBuffer ver = new StringBuffer();
		ver.append("api_key=").append(API_KEY).append("&call_id=").append(time)
				.append("&mid=").append(mid).append("&djtk=").append(djtk);
		String sig = DCNService.getSign(
				new StringBuffer().append(ver).append("&sha256_pwd=").append(
						(pwd != null && pwd.length() > 0) ? M.sha256(pwd,
								CHARSET).toUpperCase() : "").append(
						"&secret_key=").append(KEY).toString()).toUpperCase();
		StringBuffer sbu = new StringBuffer();
		sbu.append(REPLY_TOPIC_URL).append(topic_id).append("?").append(
				ver.toString()).append("&sig=").append(sig);
		return sbu.toString();
	}

	public static Result replyTopic(String topic_id, String mid, String djtk,
			String pwd, String title, String content, String ip, String showName) {
		StringBuffer message = null;
		try {
			message = new StringBuffer().append("title=")
					.append(java.net.URLEncoder.encode(title, CHARSET)).append("&content=").append(java.net.URLEncoder.encode(content, CHARSET)).append(
							"&ip=").append(ip).append("&show_name=").append(
							java.net.URLEncoder.encode(showName, CHARSET));
		} catch (UnsupportedEncodingException e1) {
			log.error("",e1);
		}
		InputStream instr = M.httpPostRequest(message.toString(),
				getReplyTopicUrl(topic_id, mid, djtk, pwd), "");
		Result result = new Result();
		result.setResult(false);
		result.setReList("");
		if (instr != null) {
			try {
				SAXReader reader = new SAXReader();
				Document doc = reader.read(instr);
				Element root = doc.getRootElement();
				if (root != null) {
					Attribute attribute = root.attribute("STATUS");
					if (attribute != null) {
						String status = attribute.getValue();
						if (status.equals("0")) {
							result.setResult(true);
							result.setReList("回帖成功");
						} else {
							result.setReList(status);
						}
					}
				}
			} catch (Exception e) {
				log.error("当乐回帖接口异常",e);
			}
		} else {
			log.error("当乐接口回帖后无数据返回");
		}
		return result;
	}

	public static String getUploadImgUrl(String mid, String djtk, String pwd) {
		long time = System.currentTimeMillis();
		StringBuffer ver = new StringBuffer();
		ver.append("api_key=").append(API_KEY).append("&call_id=").append(time)
				.append("&mid=").append(mid).append("&djtk=").append(djtk);
		String str = new StringBuffer().append(ver).append("&sha256_pwd=").append(
				(pwd != null && pwd.length() > 0) ? M.sha256(pwd,
						CHARSET).toUpperCase() : "").append(
				"&secret_key=").append(KEY).toString();
		String sig = DCNService.getSign(str).toUpperCase();
		StringBuffer sbu = new StringBuffer();
		
		sbu.append(UPLOAD_GAMEIMG_URL).append("?").append(ver.toString())
				.append("&sig=").append(sig);
		log.info(sbu.toString());
		return sbu.toString();
	}

	public static Result uploadImg(String mid, String djtk, String pwd,
			String name, String bytes, String ip, String type) {
		StringBuffer message = null;
		try {
			message = new StringBuffer().append("name=").append(name)
					.append("&bytes=").append(java.net.URLEncoder.encode(bytes, CHARSET)).append("&ip=").append(ip)
					.append("&type=").append(type);
		} catch (UnsupportedEncodingException e1) {
			log.error("",e1);
		}
		InputStream instr = M.httpPostRequest(message.toString(),
				getUploadImgUrl(mid, djtk, pwd), "");
		Result result = new Result();
		result.setResult(false);
		result.setReList("");
		if (instr != null) {
			try {
				SAXReader reader = new SAXReader();
				Document doc = reader.read(instr);
				Element root = doc.getRootElement();
				if (root != null) {
					Attribute attribute = root.attribute("STATUS");
					if (attribute != null) {
						String status = attribute.getValue();
						if (status.equals("0")) {
							Element eml = root.element("IMG_URL");
							result.setResult(true);
							result.setReList(eml.getText());
						} else {
							result.setReList(status);
						}
					}
				}
			} catch (Exception e) {
				log.error("当乐上传图片接口异常",e);
			}
		} else {
			log.error("上传图片后无数据返回");
		}
		return result;
	}

	public static String getSign(String params) {
		String dString = M.md5(params, CHARSET);
		return dString;
	}
	
	public static void main(String[] arg) throws IOException {
//		Result result = login("32730167","","111111");
//		getForumList(3, "");
//		sys("123455", "sdfasfdas", "111111");
//		 getForum("9723","");
//		 newTopic("" , result.getDjtk(), "", "哈哈1", "我了个去咿呀咿呀咿呀一","111.111.111.111", "够", "", "");
//		 replyTopic("9723", "", result.getDjtk(), "", "", "哈哈哈","111.111.111.111", "ladeng");
//		 newNote("仙境惊喜连连");
//		File file = new File("D:\\My Documents\\382026.jpg");
//		BufferedInputStream in = null;
//		try {
//			in = new BufferedInputStream(new FileInputStream("D:\\My Documents\\a.png"));
//		} catch (FileNotFoundException e1) {
//			e1.printStackTrace();
//		}
//		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);  
//        System.out.println("Available bytes:" + in.available());  
//        byte[] temp = new byte[1024];  
//        int size = 0;
//        while ((size = in.read(temp)) != -1) {
//            out.write(temp, 0, size);
//        }
//        in.close();  
//        byte[] content = out.toByteArray();  
//        System.out.println("Readed bytes count:" + content.length);
//		Result resutl = uploadImg(result.getReList(), result.getDjtk(),"","a", (new sun.misc.BASE64Encoder()).encode(content),"111.111.111.111","png");
//		System.out.println("result:" + resutl.getReList());
	}
}