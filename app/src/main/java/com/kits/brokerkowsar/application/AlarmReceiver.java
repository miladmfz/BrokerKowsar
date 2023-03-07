package com.kits.brokerkowsar.application;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.util.TimeZone;

public class AlarmReceiver extends BroadcastReceiver {

    Context mcontext;
    PersianCalendar calendar1 = new PersianCalendar();

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        this.mcontext = context;
        calendar1.setTimeZone(TimeZone.getDefault());
        Intent in = new Intent(context, LocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(in);
        }
        context.startService(in);



        if (calendar1.getTime().getHours() > 7 || calendar1.getTime().getHours() < 20) {
            setAlarm(context);
        }



    }

    public void setAlarm(Context context) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_IMMUTABLE);
            assert am != null;
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() / 1000L + 15L) * 1000L, pi); //Next alarm in 15s

    }


}