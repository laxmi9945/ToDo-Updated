package com.app.todo.todoMain.ui.alarmManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.app.todo.R;
import com.app.todo.model.NotesModel;
import com.app.todo.todoMain.ui.activity.ReminderNotifyActivity;
import com.app.todo.utils.Constants;

import java.util.ArrayList;
import java.util.List;


public class NotifyService extends Service {
    // Name of an intent extra we can use to identify if this service was started to create a notification
    public static final String INTENT_NOTIFY = "com.blundell.tut.service.INTENT_NOTIFY";
    // Unique id to identify the notification.
    private static final int NOTIFICATION = 123;
    // This is the object that receives interactions from clients
    private final IBinder mBinder = new ServiceBinder();
    List<NotesModel> allNotes;
    NotesModel notesModel;
    int requestID = 0;
    Bundle bundle;
    // The system notification manager
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        Log.i("NotifyService", "onCreate()");
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        allNotes = new ArrayList<>();
        notesModel = new NotesModel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        bundle=intent.getExtras();

        // If this service was started by out AlarmTask intent then we want to show our notification
        if (intent.getBooleanExtra(INTENT_NOTIFY, false))
            showNotification();

        // We don't care if this service is stopped as we have already delivered our notification
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Creates a notification and shows it in the OS drag-down status bar
     */
    private void showNotification() {
        // This is the 'title' of the notification
        //CharSequence title = getString(R.string.reminder);
        CharSequence title = bundle.getString(Constants.titleKey);
        // This is the icon to use on the notification
        int icon = R.drawable.ic_action_reminder_noti;

        // This is the scrolling text of the notification
        //CharSequence text = getString(R.string.reminder_message);
        CharSequence text = bundle.getString(Constants.descriptionKey);
        // What time to show on the notification
        long time = System.currentTimeMillis();
        Notification notification = new Notification(icon, text, time);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//Define sound URI
        Intent notificationIntent = new Intent(this, ReminderNotifyActivity.class);
        //notificationIntent.putExtra(Constants.titleKey,bundle);
        notificationIntent.putExtras(bundle);
        notificationIntent.setAction("myString"+requestID);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, requestID, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        // PendingIntent pendingIntent = PendingIntent.getService(this, requestID, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        //For large icon
        Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.alarm_bell);

        // Set the info for the views that show in the notification panel.
        notification = builder
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(icon)
                .setWhen(time)
                .setTicker("Notification!")
                .setAutoCancel(true)
                .setLargeIcon(bmp)
                .setSound(soundUri)
                .setContentIntent(contentIntent)
                .build();
        // Send the notification to the system.
        notificationManager.notify(NOTIFICATION, notification);
        //default vivrate
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        // Stop the service when we are finished
        stopSelf();
    }

    public class ServiceBinder extends Binder {
        NotifyService getService() {
            return NotifyService.this;
        }
    }
}
