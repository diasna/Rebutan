package com.melvitech.rebutan.sync;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.melvitech.rebutan.MainFragmentActivity;
import com.melvitech.rebutan.R;
import com.melvitech.rebutan.crawler.BaseCrawler;
import com.melvitech.rebutan.crawler.ECrawlSource;
import com.melvitech.rebutan.crawler.impl.FJBKaskusCrawlerImpl;
import com.melvitech.rebutan.crawler.impl.OLXCrawlerImpl;
import com.melvitech.rebutan.crawler.model.Item;
import com.melvitech.rebutan.crawler.model.Keyword;
import com.melvitech.rebutan.db.NotifSQLiteDataSource;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by miku on 6/29/14.
 */
public class SchedulerService extends IntentService {

    public SchedulerService() {
        super("SchedulerService");
    }

    List<Item> items = new ArrayList<Item>();
    List<Item> newItem = new ArrayList<Item>();

    NotifSQLiteDataSource sqLiteDataSource;
    boolean main = false;
    public static final String ACTION_DONE_REFRESH = "com.melvitech.rebutan.DoneRefresh";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        main = intent.getBooleanExtra("main", false);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sqLiteDataSource = new NotifSQLiteDataSource(this);
        BaseCrawler crawler;
        try {
            Log.d("SchedulerService", "Start Getting Data From KASKUS");
            crawler = new FJBKaskusCrawlerImpl();
            sqLiteDataSource.open();
            for (Keyword keyword : sqLiteDataSource.getAllKeywords(false)) {
                String keywordStr = keyword.keywords.replaceAll(" ", "+");
                items.addAll(crawler.process("http://www.kaskus.co.id/search/classified?q=" + keywordStr + "&sort=date&order=desc", ECrawlSource.KASKUS, keyword));
            }
            sqLiteDataSource.close();
//            Log.d("SchedulerService", "Start Getting Data From OLX");
//            crawler = new OLXCrawlerImpl();
//            items.addAll(crawler.process("http://www.olx.co.id/halaman/mencari/xperia|z1/search", ECrawlSource.OLX));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            sqLiteDataSource.open();
            for (Item item : items) {
                if (sqLiteDataSource.addItem(item)) {
                    newItem.add(item);
                }
            }
            if (!newItem.isEmpty()) {
                displayNotification(newItem);
            }
            sqLiteDataSource.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Intent i = new Intent();
        i.setAction(ACTION_DONE_REFRESH);
        sendBroadcast(i);
    }

    protected void displayNotification(List<Item> items) {
        Log.i("Start", "notification");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this);

        mBuilder.setContentTitle("New Item");
        mBuilder.setContentText("We found a new item you are looking for");
        mBuilder.setTicker("New Item Alert!");
        mBuilder.setSmallIcon(R.drawable.ic_stat_miku);
        mBuilder.setAutoCancel(true);
        mBuilder.setNumber(items.size());
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();

        inboxStyle.setBigContentTitle("New Item Found:");
        for (int i = 0; i < items.size(); i++) {
            inboxStyle.addLine(items.get(i).getName());
        }
        mBuilder.setStyle(inboxStyle);

        Intent resultIntent = new Intent(this, MainFragmentActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainFragmentActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(584, mBuilder.build());
    }
}
