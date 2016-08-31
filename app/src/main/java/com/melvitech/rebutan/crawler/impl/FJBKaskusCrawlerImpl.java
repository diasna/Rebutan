package com.melvitech.rebutan.crawler.impl;

import android.util.Log;

import com.melvitech.rebutan.crawler.BaseCrawler;

import org.apache.commons.lang3.StringEscapeUtils;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FJBKaskusCrawlerImpl extends BaseCrawler {

    private static final String LINK_XPATH = "/td[@class='col-xs-7']/div/div[@class='post-title']/a";
    private static final String META_XPATH = "/td[@class='col-xs-3']/div[@class='author']";
    private static final String LOCATION_XPATH = "/td[@class='col-xs-7']/div/div[@class='location']";
    private static final String PRICE_XPATH = "/td[@class='col-xs-7']/div/span";
    private static final String TYPE_XPATH = "/td[@class='item-type']/div/span";

    @Override
    protected List<TagNode> getData(Object[] input) throws XPatherException {
        List<TagNode> items = new ArrayList<TagNode>();
        for (Object o : input) {
            TagNode node = (TagNode) o;
            String status = ((TagNode) node.evaluateXPath(TYPE_XPATH)[0]).getAttributeByName("class");
            if ("jual".equals(status)) {
                items.add(node);
            }
        }
        return items;
    }

    @Override
    protected String parseLink(TagNode node) {
        try {
            TagNode linkNode = (TagNode) node.evaluateXPath(LINK_XPATH)[0];
            String link = linkNode.getAttributeByName("href");
            return link.substring(0, link.indexOf("#"));
        } catch (XPatherException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected String parseName(TagNode node) {
        try {
            TagNode linkNode = (TagNode) node.evaluateXPath(LINK_XPATH)[0];
            return StringEscapeUtils.unescapeHtml4(linkNode.getText().toString().trim());
        } catch (XPatherException e) {
            e.printStackTrace();
        }
        return "";
    }


    @Override
    protected Date parseCreatedDate(TagNode node) {
        try {
            TagNode metaNode = (TagNode) node.evaluateXPath(META_XPATH)[0];
            return parseDate(metaNode.getText().toString());
        } catch (XPatherException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected String parseUser(TagNode node) throws XPatherException {
        TagNode userNode = (TagNode) node.evaluateXPath(META_XPATH)[0];
        String meta[] = userNode.getText().toString().split(" ");
        return StringEscapeUtils.unescapeHtml4(meta[2]);
    }

    @Override
    protected String parseLocation(TagNode node) throws XPatherException {
        TagNode locationNode = (TagNode) node.evaluateXPath(LOCATION_XPATH)[0];
        return locationNode.getText().toString().trim();
    }

    @Override
    protected BigDecimal parsePrice(TagNode node) throws XPatherException {
        TagNode priceNode = (TagNode) node.evaluateXPath(PRICE_XPATH)[0];
        String price[] = priceNode.getText().toString().trim().split(" ");
        return new BigDecimal(price[1].replace(".", ""));
    }

    public static Date parseDate(String raw) {
        String meta[] = raw.split(" ");
        Calendar date = Calendar.getInstance();
        if (meta[3].equals("Today")) {
            String time[] = meta[4].split(":");
            date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
            date.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        } else if (meta[3].equals("Yesterday")) {
            String time[] = meta[4].split(":");
            date.set(Calendar.DATE, date.get(Calendar.DATE) - 1);
            date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
            date.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        } else {
            String dateRaw = meta[3] + " " + meta[4];
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            try {
                date.setTime(sdf.parse(dateRaw));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date.getTime();
    }
}
