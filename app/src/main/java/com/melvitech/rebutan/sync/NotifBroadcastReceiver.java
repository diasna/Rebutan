package com.melvitech.rebutan.sync;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by miku on 6/29/14.
 */
public class NotifBroadcastReceiver extends android.content.BroadcastReceiver {

    public static final String ACTION_ALARM = "com.melvitech.rebutan.Alarm";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NotifBroadcastReceiver", "Called context.startService from NotifBroadcastReceiver.onReceive: "+intent.getAction());
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Utils.setRecurringAlarm(context);
        } else if(ACTION_ALARM.equals(intent.getAction())) {
            Intent dailyUpdater = new Intent(context, SchedulerService.class);
            context.startService(dailyUpdater);
        }
    }

}
