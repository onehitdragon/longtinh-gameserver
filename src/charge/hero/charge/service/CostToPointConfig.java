package hero.charge.service;

import java.io.File;
import java.util.List;

import javolution.util.FastMap;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import yoyo.service.YOYOSystem;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 CostToPointConfig.java
 * @创建者 ChenYaMeng
 * @版本 1.0
 * @时间 2010-6-28 下午03:24:14
 * @描述 ：
 */

public class CostToPointConfig
{
    private static CostToPointConfig instance;

    /**
     * 充值卡数据
     */
    private Card[]                   cards;

    /**
     * 金额兑换游戏点数配置文件
     */
    public static String             COST_TO_POINT_FILE = YOYOSystem.HOME
                                                                + "res/config/charge/cost_to_point.xml";

    @SuppressWarnings("unchecked")
    public CostToPointConfig()
    {
        // 清空充值卡记录
        cards = null;

        SAXReader reader = new SAXReader();
        Document dom;
        try
        {
            dom = reader.read(new File(COST_TO_POINT_FILE));
            Element root = dom.getRootElement();

            Element eCardType = root.element("card_type");
            if (eCardType != null)
            {
                List<Element> list = eCardType.elements("card");
                int length = list.size();
                if (length > 0)
                {
                    cards = new Card[length];
                    int index = 0;
                    for (Element e : list)
                    {
                        cards[index] = new Card();
                        cards[index].id = Integer.parseInt(e
                                .attributeValue("id"));
                        cards[index].name = e.attributeValue("name");
                        cards[index].tip = e.attributeValue("tip");

                        List<Element> ePoints = e.elements("point");
                        int lenPoint = ePoints.size();
                        if (lenPoint > 0)
                        {
                            cards[index].points = new FastMap<Integer, Integer>();
                            for (Element ep : ePoints)
                            {
                                cards[index].points.put(Integer.parseInt(ep
                                        .attributeValue("amount")), Integer
                                        .parseInt(ep.getTextTrim()));
                            }
                        }

                        ++index;
                    }
                }
            }

        }
        catch (DocumentException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static CostToPointConfig getInstance ()
    {
        if (instance == null)
        {
            instance = new CostToPointConfig();
        }
        return instance;
    }

    /**
     * 更具充值金额获取充值信息及充值点数
     * 
     * @param _cardID
     * @param _amount
     * @return [0]描述 [1]可兑换点数
     */
    public String[] getChargeCardPoint (int _cardID, int _amount)
    {
        String[] result = new String[2];

        if (cards != null && cards.length > 0)
        {
            int cardLen = cards.length;
            for (int i = 0; i < cardLen; ++i)
            {
                if (cards[i].id == _cardID)
                {
                    result[0] = cards[i].tip; // 描述

                    if (cards[i].points != null && cards[i].points.size() > 0)
                    {
                        int pointLen = cards[i].points.size();
                        for (int j = 0; j < pointLen; ++j)
                        {
                            if (cards[i].points.containsKey(_amount))
                                ;

                            result[1] = String.valueOf(cards[i].points
                                    .get(_amount));

                            break;
                        }
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * 充值卡转换成游戏点数的对象类
     * 
     * @author Administrator
     */
    public class Card
    {
        public int                       id;    // 充值卡的ID

        public String                    name;

        public String                    tip;   // 充值卡提示信息

        public FastMap<Integer, Integer> points; // 该充值卡可兑换的游戏点数
    }
}
