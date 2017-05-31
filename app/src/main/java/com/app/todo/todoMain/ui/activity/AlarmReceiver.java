package com.app.todo.todoMain.ui.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.app.todo.R;


public class AlarmReceiver extends BroadcastReceiver {
    private static final int MY_NOTIFICATION_ID=1;
    NotificationManager notificationManager;
    Notification myNotification;


    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Alarm received!", Toast.LENGTH_LONG).show();

        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, myIntent, 0);
        /*NotificationCompat.Builder builder=new
                NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.alarm_bell);

        builder.setContentTitle("Sample Notification");

        builder.setContentText("Sample Notification for reminder...");

        builder.setSubText("Sample Notification for reminder...");

        Bitmap bmp= BitmapFactory.decodeResource(context.getResources(),
                R.drawable.alarm_bell);

        builder.setLargeIcon(bmp);*/
        Bitmap bmp= BitmapFactory.decodeResource(context.getResources(),
                R.drawable.alarm_bell);
        myNotification = new NotificationCompat.Builder(context)
                .setContentTitle("Notes reminder Notification!")
                .setContentText("Please check your Notes")
                .setTicker("Notification!")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.alarm_bell)
                .setLargeIcon(bmp)
                .build();


        notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(MY_NOTIFICATION_ID, myNotification);
    }

}
