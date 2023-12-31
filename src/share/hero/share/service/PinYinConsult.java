/**
 * Copyright: Copyright (c) 2007
 * <br>
 * Company: Digifun
 * <br>
 * Date: 2007-8-8
 */

package hero.share.service;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Description:汉字转换为拼音的工具<br>
 * 
 * @author Johnny
 * @version 0.1
 */
public class PinYinConsult
{
    private static PinYinConsult instance;

    public static PinYinConsult getInstance ()
    {
        if (instance == null)
        {
            instance = new PinYinConsult();
        }
        return instance;
    }

    private PinYinConsult()
    {
        initSpell();
    }

    private static LinkedHashMap<String, Integer> spellMap = new LinkedHashMap<String, Integer>(
                                                                   400);

    private void initSpell ()
    {
        spellMap.put("a", -20319);
        spellMap.put("ai", -20317);
        spellMap.put("an", -20304);
        spellMap.put("ang", -20295);
        spellMap.put("ao", -20292);
        spellMap.put("ba", -20283);
        spellMap.put("bai", -20265);
        spellMap.put("ban", -20257);
        spellMap.put("bang", -20242);
        spellMap.put("bao", -20230);
        spellMap.put("bei", -20051);
        spellMap.put("ben", -20036);
        spellMap.put("beng", -20032);
        spellMap.put("bi", -20026);
        spellMap.put("bian", -20002);
        spellMap.put("biao", -19990);
        spellMap.put("bie", -19986);
        spellMap.put("bin", -19982);
        spellMap.put("bing", -19976);
        spellMap.put("bo", -19805);
        spellMap.put("bu", -19784);
        spellMap.put("ca", -19775);
        spellMap.put("cai", -19774);
        spellMap.put("can", -19763);
        spellMap.put("cang", -19756);
        spellMap.put("cao", -19751);
        spellMap.put("ce", -19746);
        spellMap.put("ceng", -19741);
        spellMap.put("cha", -19739);
        spellMap.put("chai", -19728);
        spellMap.put("chan", -19725);
        spellMap.put("chang", -19715);
        spellMap.put("chao", -19540);
        spellMap.put("che", -19531);
        spellMap.put("chen", -19525);
        spellMap.put("cheng", -19515);
        spellMap.put("chi", -19500);
        spellMap.put("chong", -19484);
        spellMap.put("chou", -19479);
        spellMap.put("chu", -19467);
        spellMap.put("chuai", -19289);
        spellMap.put("chuan", -19288);
        spellMap.put("chuang", -19281);
        spellMap.put("chui", -19275);
        spellMap.put("chun", -19270);
        spellMap.put("chuo", -19263);
        spellMap.put("ci", -19261);
        spellMap.put("cong", -19249);
        spellMap.put("cou", -19243);
        spellMap.put("cu", -19242);
        spellMap.put("cuan", -19238);
        spellMap.put("cui", -19235);
        spellMap.put("cun", -19227);
        spellMap.put("cuo", -19224);
        spellMap.put("da", -19218);
        spellMap.put("dai", -19212);
        spellMap.put("dan", -19038);
        spellMap.put("dang", -19023);
        spellMap.put("dao", -19018);
        spellMap.put("de", -19006);
        spellMap.put("deng", -19003);
        spellMap.put("di", -18996);
        spellMap.put("dian", -18977);
        spellMap.put("diao", -18961);
        spellMap.put("die", -18952);
        spellMap.put("ding", -18783);
        spellMap.put("diu", -18774);
        spellMap.put("dong", -18773);
        spellMap.put("dou", -18763);
        spellMap.put("du", -18756);
        spellMap.put("duan", -18741);
        spellMap.put("dui", -18735);
        spellMap.put("dun", -18731);
        spellMap.put("duo", -18722);
        spellMap.put("e", -18710);
        spellMap.put("en", -18697);
        spellMap.put("er", -18696);
        spellMap.put("fa", -18526);
        spellMap.put("fan", -18518);
        spellMap.put("fang", -18501);
        spellMap.put("fei", -18490);
        spellMap.put("fen", -18478);
        spellMap.put("feng", -18463);
        spellMap.put("fo", -18448);
        spellMap.put("fou", -18447);
        spellMap.put("fu", -18446);
        spellMap.put("ga", -18239);
        spellMap.put("gai", -18237);
        spellMap.put("gan", -18231);
        spellMap.put("gang", -18220);
        spellMap.put("gao", -18211);
        spellMap.put("ge", -18201);
        spellMap.put("gei", -18184);
        spellMap.put("gen", -18183);
        spellMap.put("geng", -18181);
        spellMap.put("gong", -18012);
        spellMap.put("gou", -17997);
        spellMap.put("gu", -17988);
        spellMap.put("gua", -17970);
        spellMap.put("guai", -17964);
        spellMap.put("guan", -17961);
        spellMap.put("guang", -17950);
        spellMap.put("gui", -17947);
        spellMap.put("gun", -17931);
        spellMap.put("guo", -17928);
        spellMap.put("ha", -17922);
        spellMap.put("hai", -17759);
        spellMap.put("han", -17752);
        spellMap.put("hang", -17733);
        spellMap.put("hao", -17730);
        spellMap.put("he", -17721);
        spellMap.put("hei", -17703);
        spellMap.put("hen", -17701);
        spellMap.put("heng", -17697);
        spellMap.put("hong", -17692);
        spellMap.put("hou", -17683);
        spellMap.put("hu", -17676);
        spellMap.put("hua", -17496);
        spellMap.put("huai", -17487);
        spellMap.put("huan", -17482);
        spellMap.put("huang", -17468);
        spellMap.put("hui", -17454);
        spellMap.put("hun", -17433);
        spellMap.put("huo", -17427);
        spellMap.put("ji", -17417);
        spellMap.put("jia", -17202);
        spellMap.put("jian", -17185);
        spellMap.put("jiang", -16983);
        spellMap.put("jiao", -16970);
        spellMap.put("jie", -16942);
        spellMap.put("jin", -16915);
        spellMap.put("jing", -16733);
        spellMap.put("jiong", -16708);
        spellMap.put("jiu", -16706);
        spellMap.put("ju", -16689);
        spellMap.put("juan", -16664);
        spellMap.put("jue", -16657);
        spellMap.put("jun", -16647);
        spellMap.put("ka", -16474);
        spellMap.put("kai", -16470);
        spellMap.put("kan", -16465);
        spellMap.put("kang", -16459);
        spellMap.put("kao", -16452);
        spellMap.put("ke", -16448);
        spellMap.put("ken", -16433);
        spellMap.put("keng", -16429);
        spellMap.put("kong", -16427);
        spellMap.put("kou", -16423);
        spellMap.put("ku", -16419);
        spellMap.put("kua", -16412);
        spellMap.put("kuai", -16407);
        spellMap.put("kuan", -16403);
        spellMap.put("kuang", -16401);
        spellMap.put("kui", -16393);
        spellMap.put("kun", -16220);
        spellMap.put("kuo", -16216);
        spellMap.put("la", -16212);
        spellMap.put("lai", -16205);
        spellMap.put("lan", -16202);
        spellMap.put("lang", -16187);
        spellMap.put("lao", -16180);
        spellMap.put("le", -16171);
        spellMap.put("lei", -16169);
        spellMap.put("leng", -16158);
        spellMap.put("li", -16155);
        spellMap.put("lia", -15959);
        spellMap.put("lian", -15958);
        spellMap.put("liang", -15944);
        spellMap.put("liao", -15933);
        spellMap.put("lie", -15920);
        spellMap.put("lin", -15915);
        spellMap.put("ling", -15903);
        spellMap.put("liu", -15889);
        spellMap.put("long", -15878);
        spellMap.put("lou", -15707);
        spellMap.put("lu", -15701);
        spellMap.put("lv", -15681);
        spellMap.put("luan", -15667);
        spellMap.put("lue", -15661);
        spellMap.put("lun", -15659);
        spellMap.put("luo", -15652);
        spellMap.put("ma", -15640);
        spellMap.put("mai", -15631);
        spellMap.put("man", -15625);
        spellMap.put("mang", -15454);
        spellMap.put("mao", -15448);
        spellMap.put("me", -15436);
        spellMap.put("mei", -15435);
        spellMap.put("men", -15419);
        spellMap.put("meng", -15416);
        spellMap.put("mi", -15408);
        spellMap.put("mian", -15394);
        spellMap.put("miao", -15385);
        spellMap.put("mie", -15377);
        spellMap.put("min", -15375);
        spellMap.put("ming", -15369);
        spellMap.put("miu", -15363);
        spellMap.put("mo", -15362);
        spellMap.put("mou", -15183);
        spellMap.put("mu", -15180);
        spellMap.put("na", -15165);
        spellMap.put("nai", -15158);
        spellMap.put("nan", -15153);
        spellMap.put("nang", -15150);
        spellMap.put("nao", -15149);
        spellMap.put("ne", -15144);
        spellMap.put("nei", -15143);
        spellMap.put("nen", -15141);
        spellMap.put("neng", -15140);
        spellMap.put("ni", -15139);
        spellMap.put("nian", -15128);
        spellMap.put("niang", -15121);
        spellMap.put("niao", -15119);
        spellMap.put("nie", -15117);
        spellMap.put("nin", -15110);
        spellMap.put("ning", -15109);
        spellMap.put("niu", -14941);
        spellMap.put("nong", -14937);
        spellMap.put("nu", -14933);
        spellMap.put("nv", -14930);
        spellMap.put("nuan", -14929);
        spellMap.put("nue", -14928);
        spellMap.put("nuo", -14926);
        spellMap.put("o", -14922);
        spellMap.put("ou", -14921);
        spellMap.put("pa", -14914);
        spellMap.put("pai", -14908);
        spellMap.put("pan", -14902);
        spellMap.put("pang", -14894);
        spellMap.put("pao", -14889);
        spellMap.put("pei", -14882);
        spellMap.put("pen", -14873);
        spellMap.put("peng", -14871);
        spellMap.put("pi", -14857);
        spellMap.put("pian", -14678);
        spellMap.put("piao", -14674);
        spellMap.put("pie", -14670);
        spellMap.put("pin", -14668);
        spellMap.put("ping", -14663);
        spellMap.put("po", -14654);
        spellMap.put("pu", -14645);
        spellMap.put("qi", -14630);
        spellMap.put("qia", -14594);
        spellMap.put("qian", -14429);
        spellMap.put("qiang", -14407);
        spellMap.put("qiao", -14399);
        spellMap.put("qie", -14384);
        spellMap.put("qin", -14379);
        spellMap.put("qing", -14368);
        spellMap.put("qiong", -14355);
        spellMap.put("qiu", -14353);
        spellMap.put("qu", -14345);
        spellMap.put("quan", -14170);
        spellMap.put("que", -14159);
        spellMap.put("qun", -14151);
        spellMap.put("ran", -14149);
        spellMap.put("rang", -14145);
        spellMap.put("rao", -14140);
        spellMap.put("re", -14137);
        spellMap.put("ren", -14135);
        spellMap.put("reng", -14125);
        spellMap.put("ri", -14123);
        spellMap.put("rong", -14122);
        spellMap.put("rou", -14112);
        spellMap.put("ru", -14109);
        spellMap.put("ruan", -14099);
        spellMap.put("rui", -14097);
        spellMap.put("run", -14094);
        spellMap.put("ruo", -14092);
        spellMap.put("sa", -14090);
        spellMap.put("sai", -14087);
        spellMap.put("san", -14083);
        spellMap.put("sang", -13917);
        spellMap.put("sao", -13914);
        spellMap.put("se", -13910);
        spellMap.put("sen", -13907);
        spellMap.put("seng", -13906);
        spellMap.put("sha", -13905);
        spellMap.put("shai", -13896);
        spellMap.put("shan", -13894);
        spellMap.put("shang", -13878);
        spellMap.put("shao", -13870);
        spellMap.put("she", -13859);
        spellMap.put("shen", -13847);
        spellMap.put("sheng", -13831);
        spellMap.put("shi", -13658);
        spellMap.put("shou", -13611);
        spellMap.put("shu", -13601);
        spellMap.put("shua", -13406);
        spellMap.put("shuai", -13404);
        spellMap.put("shuan", -13400);
        spellMap.put("shuang", -13398);
        spellMap.put("shui", -13395);
        spellMap.put("shun", -13391);
        spellMap.put("shuo", -13387);
        spellMap.put("si", -13383);
        spellMap.put("song", -13367);
        spellMap.put("sou", -13359);
        spellMap.put("su", -13356);
        spellMap.put("suan", -13343);
        spellMap.put("sui", -13340);
        spellMap.put("sun", -13329);
        spellMap.put("suo", -13326);
        spellMap.put("ta", -13318);
        spellMap.put("tai", -13147);
        spellMap.put("tan", -13138);
        spellMap.put("tang", -13120);
        spellMap.put("tao", -13107);
        spellMap.put("te", -13096);
        spellMap.put("teng", -13095);
        spellMap.put("ti", -13091);
        spellMap.put("tian", -13076);
        spellMap.put("tiao", -13068);
        spellMap.put("tie", -13063);
        spellMap.put("ting", -13060);
        spellMap.put("tong", -12888);
        spellMap.put("tou", -12875);
        spellMap.put("tu", -12871);
        spellMap.put("tuan", -12860);
        spellMap.put("tui", -12858);
        spellMap.put("tun", -12852);
        spellMap.put("tuo", -12849);
        spellMap.put("wa", -12838);
        spellMap.put("wai", -12831);
        spellMap.put("wan", -12829);
        spellMap.put("wang", -12812);
        spellMap.put("wei", -12802);
        spellMap.put("wen", -12607);
        spellMap.put("weng", -12597);
        spellMap.put("wo", -12594);
        spellMap.put("wu", -12585);
        spellMap.put("xi", -12556);
        spellMap.put("xia", -12359);
        spellMap.put("xian", -12346);
        spellMap.put("xiang", -12320);
        spellMap.put("xiao", -12300);
        spellMap.put("xie", -12120);
        spellMap.put("xin", -12099);
        spellMap.put("xing", -12089);
        spellMap.put("xiong", -12074);
        spellMap.put("xiu", -12067);
        spellMap.put("xu", -12058);
        spellMap.put("xuan", -12039);
        spellMap.put("xue", -11867);
        spellMap.put("xun", -11861);
        spellMap.put("ya", -11847);
        spellMap.put("yan", -11831);
        spellMap.put("yang", -11798);
        spellMap.put("yao", -11781);
        spellMap.put("ye", -11604);
        spellMap.put("yi", -11589);
        spellMap.put("yin", -11536);
        spellMap.put("ying", -11358);
        spellMap.put("yo", -11340);
        spellMap.put("yong", -11339);
        spellMap.put("you", -11324);
        spellMap.put("yu", -11303);
        spellMap.put("yuan", -11097);
        spellMap.put("yue", -11077);
        spellMap.put("yun", -11067);
        spellMap.put("za", -11055);
        spellMap.put("zai", -11052);
        spellMap.put("zan", -11045);
        spellMap.put("zang", -11041);
        spellMap.put("zao", -11038);
        spellMap.put("ze", -11024);
        spellMap.put("zei", -11020);
        spellMap.put("zen", -11019);
        spellMap.put("zeng", -11018);
        spellMap.put("zha", -11014);
        spellMap.put("zhai", -10838);
        spellMap.put("zhan", -10832);
        spellMap.put("zhang", -10815);
        spellMap.put("zhao", -10800);
        spellMap.put("zhe", -10790);
        spellMap.put("zhen", -10780);
        spellMap.put("zheng", -10764);
        spellMap.put("zhi", -10587);
        spellMap.put("zhong", -10544);
        spellMap.put("zhou", -10533);
        spellMap.put("zhu", -10519);
        spellMap.put("zhua", -10331);
        spellMap.put("zhuai", -10329);
        spellMap.put("zhuan", -10328);
        spellMap.put("zhuang", -10322);
        spellMap.put("zhui", -10315);
        spellMap.put("zhun", -10309);
        spellMap.put("zhuo", -10307);
        spellMap.put("zi", -10296);
        spellMap.put("zong", -10281);
        spellMap.put("zou", -10274);
        spellMap.put("zu", -10270);
        spellMap.put("zuan", -10262);
        spellMap.put("zui", -10260);
        spellMap.put("zun", -10256);
        spellMap.put("zuo", -10254);
    }

    /**
     * 返回字符串的全拼,是汉字转化为全拼,其它字符不进行转换
     * 
     * @param cnStr String 字符串
     * @return String 转换成全拼后的字符串
     */
    public String getFullSpell (String cnStr)
    {
        if (!validate(cnStr))
        {
            return cnStr;
        }
        char[] chars = cnStr.toCharArray();
        StringBuffer retuBuf = new StringBuffer();
        for (int i = 0, Len = chars.length; i < Len; i++)
        {
            int ascii = getCnAscii(chars[i]);// 得到单个中文的Ascii码
            if (ascii == 0)
            { // 取ascii时出错
                retuBuf.append(chars[i]);
            }
            else
            {
                String spell = getSpellByAscii(ascii);
                if (spell == null)
                {
                    retuBuf.append(chars[i]);
                }
                else
                {
                    retuBuf.append(spell);
                } // end of if spell == null
            } // end of if ascii <= -20400
        } // end of for
        return retuBuf.toString();
    }

    /**
     * 根据ASCII码到SpellMap中查找对应的拼音
     * 
     * @param ascii int 字符对应的ASCII
     * @return String 拼音,首先判断ASCII是否>0&<160,如果是返回对应的字符,
     *         否则到SpellMap中查找,如果没有找到拼音,则返回null,如果找到则返回拼音.
     */
    private String getSpellByAscii (int ascii)
    {
        if (ascii > 0 && ascii < 160)
        { // 单字符--英文或半角字符
            return String.valueOf((char) ascii);
        }
        if (ascii < -20319 || ascii > -10247)
        { // 不知道的字符
            return null;
        }
        Set<String> keySet = spellMap.keySet();
        Iterator<String> it = keySet.iterator();
        String spell0 = null;
        String spell = null;

        int asciiRang0 = -20319;
        int asciiRang;
        while (it.hasNext())
        {
            spell = (String) it.next();
            Integer asciiValue = spellMap.get(spell);
            if (asciiValue != null)
            {
                asciiRang = asciiValue.intValue();
                if (ascii >= asciiRang0 && ascii < asciiRang)
                { // 区间找到
                    return (spell0 == null) ? spell : spell0;
                }
                else
                {
                    spell0 = spell;
                    asciiRang0 = asciiRang;
                }
            }
        }
        return null;
    }

    /**
     * 获得单个汉字的Ascii.
     * 
     * @param cn char 汉字字符
     * @return int 错误返回 0,否则返回ascii
     */
    private int getCnAscii (char cn)
    {
        byte[] bytes = (String.valueOf(cn)).getBytes();
        if (bytes == null || bytes.length > 2 || bytes.length <= 0)
        { // 错误
            return 0;
        }
        if (bytes.length == 1)
        { // 英文字符
            return bytes[0];
        }
        if (bytes.length == 2)
        { // 中文字符
            int hightByte = 256 + bytes[0];
            int lowByte = 256 + bytes[1];
            int ascii = (256 * hightByte + lowByte) - 256 * 256;
            return ascii;
        }
        return 0; // 错误
    }

    /**
     * 检验字符传是否正确
     * 
     * @param cnStr
     * @return
     */
    private boolean validate (String cnStr)
    {
        if (cnStr == null || cnStr.trim().equals(""))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
