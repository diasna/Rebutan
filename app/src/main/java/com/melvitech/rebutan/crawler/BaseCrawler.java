package com.melvitech.rebutan.crawler;

import android.util.Log;

import com.melvitech.rebutan.crawler.model.Item;
import com.melvitech.rebutan.crawler.model.Keyword;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class BaseCrawler {

    private static final String USER_AGENT = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:23.0) Gecko/20100101 Firefox/23.0";

    public List<Item> process(String url, ECrawlSource source, Keyword keyword) throws IOException {
        CleanerProperties props = new CleanerProperties();
        props.setTranslateSpecialEntities(true);
        props.setTransResCharsToNCR(true);
        props.setOmitComments(true);
        HttpURLConnection lConn = (HttpURLConnection) new URL(url).openConnection();
        lConn.setRequestProperty("User-Agent", USER_AGENT);
        lConn.connect();
        TagNode tagNode = new HtmlCleaner(props).clean(
                lConn.getInputStream()
        );
        Log.d("BaseCrawler", "Done get input sream, now parsing..");
        List<Item> items = new ArrayList<Item>();
        try {
            Object[] objects = tagNode.evaluateXPath(source.getBaseData());
            for (TagNode node : getData(objects)) {
                Item item = new Item();
                item.setLink(parseLink(node));
                item.setName(parseName(node));
                item.setSource(source);
                item.setCreateDate(parseCreatedDate(node));
                item.setKeywordId(keyword.id);
                item.setUser(parseUser(node));
                item.setLocation(parseLocation(node));
                item.setPrice(parsePrice(node));
                items.add(item);
            }
        } catch (XPatherException e) {
            e.printStackTrace();
        }
        return items;
    }

    protected abstract List<TagNode> getData(Object[] input) throws XPatherException;

    protected abstract String parseLink(TagNode node) throws XPatherException;

    protected abstract String parseName(TagNode node) throws XPatherException;

    protected abstract Date parseCreatedDate(TagNode node) throws XPatherException;

    protected abstract String parseUser(TagNode node) throws XPatherException;

    protected abstract String parseLocation(TagNode node) throws XPatherException;

    protected abstract BigDecimal parsePrice(TagNode node) throws XPatherException;

}
