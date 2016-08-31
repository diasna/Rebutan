package com.melvitech.rebutan.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.melvitech.rebutan.crawler.ECrawlSource;
import com.melvitech.rebutan.crawler.model.Item;
import com.melvitech.rebutan.crawler.model.Keyword;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by miku on 6/29/14.
 */
public class NotifSQLiteDataSource {

    private SQLiteDatabase database;
    private NotifSQLiteHelper dbHelper;

    private String[] columnsItem = {
            NotifSQLiteHelper.COLUMN_ID_ITEM,
            NotifSQLiteHelper.COLUMN_LINK,
            NotifSQLiteHelper.COLUMN_NAME,
            NotifSQLiteHelper.COLUMN_CREATED,
            NotifSQLiteHelper.COLUMN_SOURCE,
            NotifSQLiteHelper.COLUMN_READ,
            NotifSQLiteHelper.COLUMN_ITEM_KEYWORDS,
            NotifSQLiteHelper.COLUMN_USER,
            NotifSQLiteHelper.COLUMN_LOCATION,
            NotifSQLiteHelper.COLUMN_PRICE

    };

    private String[] columnsKeywords = {
            NotifSQLiteHelper.COLUMN_ID_KEYWORDS,
            NotifSQLiteHelper.COLUMN_KEYWORDS
    };

    public NotifSQLiteDataSource(Context context) {
        dbHelper = new NotifSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean addItem(Item item) {
        try {
            long insertId = database.insertOrThrow(NotifSQLiteHelper.TABLE_ITEM, null, item.toContentValues());
            Log.d("NotifSQLiteDataSource", "Success Persisting ID: " + insertId + ", DATA: " + item.toString());
            return true;
        } catch (SQLiteConstraintException e) {
            Log.e("NotifSQLiteDataSource", item.getName() + " is already exist");
            return false;
        }
    }

    public Item getItemByName(String name) {
        Cursor cursor = database.query(NotifSQLiteHelper.TABLE_ITEM,
                columnsItem,
                NotifSQLiteHelper.COLUMN_NAME + " = ?",
                new String[]{name},
                null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            Item item = cursorToItem(cursor);
            cursor.close();
            return item;
        }
        cursor.close();
        return null;
    }

    public List<Item> getAllItems(int page, int max) {
        String from = String.valueOf(page * max);
        String to = String.valueOf(from + max);
        List<Item> items = new ArrayList<Item>();
        Cursor cursor = database.query(NotifSQLiteHelper.TABLE_ITEM,
                columnsItem, null, null, null, null, NotifSQLiteHelper.COLUMN_CREATED + " DESC", from + "," + max);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Item item = cursorToItem(cursor);
            items.add(item);
            cursor.moveToNext();
        }
        cursor.close();
        return items;
    }

    public List<Item> getAllItemsByKeywords(int page, int max, long keywordsId, CharSequence[] sources) {
        String from = String.valueOf(page * max);
        String to = String.valueOf(from + max);
        List<Item> items = new ArrayList<Item>();
        Cursor cursor = database.query(NotifSQLiteHelper.TABLE_ITEM,
                columnsItem, NotifSQLiteHelper.COLUMN_ITEM_KEYWORDS + "=" + keywordsId + OR(sources), null, null, null, NotifSQLiteHelper.COLUMN_CREATED + " DESC", from + "," + max);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Item item = cursorToItem(cursor);
            items.add(item);
            cursor.moveToNext();
        }
        cursor.close();
        return items;
    }

    private String OR(CharSequence[] sources) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sources.length; i++) {
            sb.append(NotifSQLiteHelper.COLUMN_SOURCE + "=" + sources[i]);
            if (i < sources.length - 1) {
                sb.append(" OR ");
            }
        }
        if (sources.length > 0) {
            return " AND (" + sb.toString() + ")";
        } else {
            return "";
        }
    }

    private Item cursorToItem(Cursor cursor) {
        Item item = new Item();
        item.setId(cursor.getLong(0));
        item.setLink(cursor.getString(1));
        item.setName(cursor.getString(2));
        item.setCreateDate(new Date(cursor.getLong(3)));
        item.setSource(ECrawlSource.valueOf(cursor.getString(4)));
        item.setRead(cursor.getInt(5) > 0);
        item.setKeywordId(cursor.getInt(6));
        item.setUser(cursor.getString(7));
        item.setLocation(cursor.getString(8));
        item.setPrice(new BigDecimal(cursor.getString(9)));
        return item;
    }

    public boolean addKeywords(String keywords) {
        try {
            ContentValues values = new ContentValues();
            values.put(NotifSQLiteHelper.COLUMN_KEYWORDS, keywords);
            long insertId = database.insertOrThrow(NotifSQLiteHelper.TABLE_KEYWORDS, null, values);
            Log.d("NotifSQLiteDataSource", "Success Persisting ID: " + insertId + ", DATA: " + values.toString());
            return true;
        } catch (SQLiteConstraintException e) {
            Log.e("NotifSQLiteDataSource", keywords + " is already exist");
            return false;
        }
    }

    public void setRead(long id) {
        ContentValues values = new ContentValues();
        values.put(NotifSQLiteHelper.COLUMN_READ, 1);
        database.update(NotifSQLiteHelper.TABLE_ITEM,
                values,
                NotifSQLiteHelper.COLUMN_ID_KEYWORDS + "=" + id,
                null);
    }

    public List<Keyword> getAllKeywords(boolean withCount) {
        List<Keyword> keywords = new ArrayList<Keyword>();
        Cursor cursor = database.query(NotifSQLiteHelper.TABLE_KEYWORDS,
                columnsKeywords, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Keyword keyword = new Keyword(cursor.getInt(0), cursor.getString(1));
            if (withCount) {
                Cursor mCount = database.rawQuery("select count(*) from " +
                                NotifSQLiteHelper.TABLE_ITEM + " where " +
                                NotifSQLiteHelper.COLUMN_READ + "=0 and " + NotifSQLiteHelper.COLUMN_ITEM_KEYWORDS + "=" + keyword.id,
                        null
                );
                mCount.moveToFirst();
                int count = mCount.getInt(0);
                keyword.count = count;
                mCount.close();
            }
            keywords.add(keyword);
            cursor.moveToNext();
        }
        cursor.close();
        return keywords;
    }

    public boolean deleteKeywords(int id) {
        database.delete(NotifSQLiteHelper.TABLE_ITEM, NotifSQLiteHelper.COLUMN_ITEM_KEYWORDS + "=" + id, null);
        return database.delete(NotifSQLiteHelper.TABLE_KEYWORDS, NotifSQLiteHelper.COLUMN_ID_KEYWORDS + "=" + id, null) > 0;
    }
}
