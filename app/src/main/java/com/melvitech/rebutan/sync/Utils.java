package com.melvitech.rebutan.sync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;

public class Utils {

    public static void setRecurringAlarm(Context context) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 583, getAlarmIntent(context), PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.getBoolean("perform_updates", true)) {
            long interval = Long.valueOf(prefs.getString("updates_interval", "1")) * 3600000;
            Log.d("Utils", "Setting Schedule for Sync, Interval: " + interval);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), interval, pendingIntent);
        } else {
            Log.d("Utils", "Periodic Sync Disabled");
            alarmManager.cancel(pendingIntent);
        }
    }

    public static boolean isAlarmSet(Context context) {
        return (PendingIntent.getBroadcast(context, 583, getAlarmIntent(context), PendingIntent.FLAG_NO_CREATE) != null);
    }

    public static Intent getAlarmIntent(Context context) {
        Intent intent = new Intent(context, NotifBroadcastReceiver.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(NotifBroadcastReceiver.ACTION_ALARM);
        return intent;
    }
}
