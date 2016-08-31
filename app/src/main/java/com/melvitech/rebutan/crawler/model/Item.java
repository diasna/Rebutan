package com.melvitech.rebutan.crawler.model;

import android.content.ContentValues;

import com.melvitech.rebutan.crawler.ECrawlSource;
import com.melvitech.rebutan.db.NotifSQLiteHelper;

import java.math.BigDecimal;
import java.util.Date;

public class Item {
    private long id;
    private String link;
    private String name;
    private Date createDate;
    private ECrawlSource source;
    private int count = 0;
    private boolean read = false;
    private int keywordId;
    private String user;
    private String location;
    private BigDecimal price = new BigDecimal("0");

    public Item() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public ECrawlSource getSource() {
        return source;
    }

    public void setSource(ECrawlSource source) {
        this.source = source;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public int getKeywordId() {
        return keywordId;
    }

    public void setKeywordId(int keywordId) {
        this.keywordId = keywordId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", link='" + link + '\'' +
                ", name='" + name + '\'' +
                ", createDate=" + createDate +
                ", source=" + source +
                ", count=" + count +
                ", read=" + read +
                ", keywordId=" + keywordId +
                ", user='" + user + '\'' +
                ", location='" + location + '\'' +
                ", price=" + price +
                '}';
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(NotifSQLiteHelper.COLUMN_LINK, getLink() + "");
        values.put(NotifSQLiteHelper.COLUMN_NAME, getName() + "");
        values.put(NotifSQLiteHelper.COLUMN_CREATED, getCreateDate().getTime() + "");
        values.put(NotifSQLiteHelper.COLUMN_SOURCE, getSource().toString() + "");
        values.put(NotifSQLiteHelper.COLUMN_READ, isRead() ? 1 : 0);
        values.put(NotifSQLiteHelper.COLUMN_ITEM_KEYWORDS, getKeywordId());
        values.put(NotifSQLiteHelper.COLUMN_USER, getUser());
        values.put(NotifSQLiteHelper.COLUMN_LOCATION, getLocation());
        if (getPrice() != null)
            values.put(NotifSQLiteHelper.COLUMN_PRICE, getPrice().toString());
        return values;
    }

}
