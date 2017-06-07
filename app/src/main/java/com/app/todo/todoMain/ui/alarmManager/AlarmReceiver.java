package com.app.todo.todoMain.ui.alarmManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.app.todo.R;

public class AlarmReceiver extends BroadcastReceiver {
    private static final int MY_NOTIFICATION_ID=1;
    NotificationManager notificationManager;
    Notification myNotification;
    NotificationCompat.Builder notificationBuilder;


    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Reminder received!", Toast.LENGTH_LONG).show();
        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, myIntent, 0);
        Bitmap bmp= BitmapFactory.decodeResource(context.getResources(),
                R.drawable.alarm_bell);
        notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setContentTitle("Notes reminder Notification!")
                .setContentText("Please check your Notes")
                .setTicker("Notification!")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.alarm_bell)
                .setLargeIcon(bmp)
                .build();

        myNotification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationManager.notify(1000, myNotification);
        long[] pattern = {500,500,500,500,500,500,500,500,500};
        notificationBuilder.setVibrate(pattern);
        notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(MY_NOTIFICATION_ID, myNotification);
    }

}
