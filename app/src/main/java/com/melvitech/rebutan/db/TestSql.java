package com.melvitech.rebutan.db;

import com.melvitech.rebutan.crawler.ECrawlSource;

/**
 * Created by miku on 7/22/14.
 */
public class TestSql {
    public static void main (String [] args){
        ECrawlSource[] sources = {ECrawlSource.KASKUS, ECrawlSource.BERNIAGA, ECrawlSource.OLX};
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<sources.length;i++){
            sb.append(NotifSQLiteHelper.COLUMN_SOURCE+"="+sources[i].getCode());
            if(i < sources.length -1 ){
                sb.append(" OR ");
            }
        }
        System.out.println(sb.toString());
    }
}
