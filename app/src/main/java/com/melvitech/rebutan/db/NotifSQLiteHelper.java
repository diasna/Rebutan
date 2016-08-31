package com.melvitech.rebutan.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by miku on 6/29/14.
 */
public class NotifSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_ITEM = "item";
    public static final String COLUMN_ID_ITEM = "_id";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CREATED = "created";
    public static final String COLUMN_SOURCE = "source";
    public static final String COLUMN_READ = "read";
    public static final String COLUMN_ITEM_KEYWORDS = "keyword_id";
    public static final String COLUMN_USER = "user";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_PRICE = "price";

    public static final String TABLE_KEYWORDS = "search";
    public static final String COLUMN_ID_KEYWORDS = "_id";
    public static final String COLUMN_KEYWORDS = "keywords";

    private static final String DATABASE_NAME = "notif.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_ITEM = "create table "
            + TABLE_ITEM + "(" + COLUMN_ID_ITEM
            + " integer primary key autoincrement, " +
            COLUMN_LINK + " text not null," +
            COLUMN_NAME + " text unique," +
            COLUMN_CREATED + " integer not null," +
            COLUMN_SOURCE + " text not null," +
            COLUMN_READ + " integer," +
            COLUMN_ITEM_KEYWORDS + " integer," +
            COLUMN_USER + " text," +
            COLUMN_LOCATION + " text," +
            COLUMN_PRICE + " text" +
            ");";

    private static final String CREATE_SEARCH = "create table "
            + TABLE_KEYWORDS + "(" +
            COLUMN_ID_KEYWORDS + " integer primary key autoincrement, " +
            COLUMN_KEYWORDS + " text unique"+
            ");";


    public NotifSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_ITEM);
        database.execSQL(CREATE_SEARCH);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(NotifSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
        onCreate(db);
    }

}
