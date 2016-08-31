package com.melvitech.rebutan.crawler.impl;

import com.melvitech.rebutan.crawler.BaseCrawler;

import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OLXCrawlerImpl extends BaseCrawler {

    private static final String LINK_XPATH = "/div[@class='ikl-box']/h2/a";
    private static final String DATE_XPATH = "/div[@class='item-time kir']";

    @Override
    protected List<TagNode> getData(Object[] input) throws XPatherException {
        List<TagNode> items = new ArrayList<TagNode>();
        for (Object o : input) {
            TagNode node = (TagNode) o;
            items.add(node);
        }
        return items;
    }

    @Override
    protected String parseLink(TagNode node) throws XPatherException {
        TagNode linkNode = (TagNode) node.evaluateXPath(LINK_XPATH)[0];
        String link = linkNode.getAttributeByName("href");
        return link.substring(0, link.indexOf("#"));
    }

    @Override
    protected String parseName(TagNode node) throws XPatherException {
        TagNode linkNode = (TagNode) node.evaluateXPath(LINK_XPATH)[0];
        return linkNode.getAttributeByName("title");
    }

    @Override
    protected Date parseCreatedDate(TagNode node) throws XPatherException {
        TagNode metaNode = (TagNode) node.evaluateXPath(DATE_XPATH)[0];
        return parseDate(metaNode.getText().toString());
    }

    @Override
    protected String parseUser(TagNode node) throws XPatherException {
        return null;
    }

    @Override
    protected String parseLocation(TagNode node) throws XPatherException {
        return null;
    }

    @Override
    protected BigDecimal parsePrice(TagNode node) throws XPatherException {
        return null;
    }

    public static Date parseDate(String raw) {
        Calendar date = Calendar.getInstance();
        String split[] = raw.split(" ");
        if (raw.contains("jam")) {
            date.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY) - Integer.parseInt(split[0]));
        } else if (raw.contains("menit")) {
            date.set(Calendar.MINUTE, date.get(Calendar.MINUTE) - Integer.parseInt(split[0]));
        } else if (raw.contains("Kemarin")) {
            date.set(Calendar.DATE, date.get(Calendar.DATE) - 1);
        } else if (raw.contains("hari")) {
            date.set(Calendar.DATE, date.get(Calendar.DATE) - Integer.parseInt(split[0]));
        } else if (raw.contains("/")) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            try {
                date.setTime(sdf.parse(raw));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date.getTime();
    }
}
