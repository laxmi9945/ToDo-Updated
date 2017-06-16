package com.app.todo.todoMain.ui.alarmManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;

public class AlarmTask implements Runnable{
    // The date selected for the alarm
    private final Calendar date;
    // The android system alarm manager
    private final AlarmManager alarmManager;
    // Your context to retrieve the alarm manager from
    private final Context context;

    private final Bundle bundle;
    public AlarmTask(Context context, Calendar date, Bundle bundle) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.date = date;
        this.bundle=bundle;
    }

    @Override
    public void run() {
        // Request to start are service when the alarm date is upon us
        // We don't start an activity as we just want to pop up a notification into the system bar not a full activity
        Intent intent = new Intent(context, NotifyService.class);
        intent.putExtras(bundle);
        intent.putExtra(NotifyService.INTENT_NOTIFY, true);
        Log.i("data", "run: "+bundle);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        // Sets an alarm - note this alarm will be lost if the phone is turned off and on again
        alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTimeInMillis(), pendingIntent);
    }
}
