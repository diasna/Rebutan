package com.melvitech.rebutan.crawler.model;

/**
 * Created by miku on 7/9/14.
 */
public class Keyword {

    public int id;
    public String keywords;
    public int count = 0;

    public Keyword(int id) {
        this.id = id;
    }

    public Keyword(int id, String keywords) {
        this.id = id;
        this.keywords = keywords;
    }

    @Override
    public String toString() {
        return "Keyword{" +
                "id=" + id +
                ", keywords='" + keywords + '\'' +
                ", count=" + count +
                '}';
    }
}