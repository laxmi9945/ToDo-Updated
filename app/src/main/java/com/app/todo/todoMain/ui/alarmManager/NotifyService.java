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
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.app.todo.R;
import com.app.todo.model.NotesModel;
import com.app.todo.todoMain.ui.activity.ReminderNotifyActivity;

import java.util.ArrayList;
import java.util.List;


public class NotifyService extends Service {
    List<NotesModel> allNotes;
    NotesModel notesModel;
    int requestID=0;
    public class ServiceBinder extends Binder {
        NotifyService getService() {
            return NotifyService.this;
        }
    }

    // Unique id to identify the notification.
    private static final int NOTIFICATION = 123;
    // Name of an intent extra we can use to identify if this service was started to create a notification
    public static final String INTENT_NOTIFY = "com.blundell.tut.service.INTENT_NOTIFY";
    // The system notification manager
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        Log.i("NotifyService", "onCreate()");
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        allNotes = new ArrayList<>();
        notesModel=new NotesModel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        // If this service was started by out AlarmTask intent then we want to show our notification
        if(intent.getBooleanExtra(INTENT_NOTIFY, false))
            showNotification();

        // We don't care if this service is stopped as we have already delivered our notification
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients
    private final IBinder mBinder = new ServiceBinder();

    /**
     * Creates a notification and shows it in the OS drag-down status bar
     */
    private void showNotification() {
        // This is the 'title' of the notification
        CharSequence title = getString(R.string.reminder);
        // This is the icon to use on the notification
        int icon = R.drawable.todo;

        // This is the scrolling text of the notification
        CharSequence text = getString(R.string.reminder_message);
        // What time to show on the notification
        long time = System.currentTimeMillis();

        Notification notification = new Notification(icon, text, time);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//Define sound URI
        Intent notificationIntent=new Intent(this,ReminderNotifyActivity.class);
        notificationIntent.putExtra("title",notesModel.getTitle());
        notificationIntent.putExtra("content",notesModel.getContent());
        notificationIntent.setAction("myString"+requestID);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, requestID, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
       // PendingIntent pendingIntent = PendingIntent.getService(this, requestID, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        //For large icon
        Bitmap bmp= BitmapFactory.decodeResource(getResources(),
                R.drawable.alarm_bell);

        // Set the info for the views that show in the notification panel.
        //notification.setLatestEventInfo(this, title, text, contentIntent);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        notification=builder.setContentIntent(contentIntent)
                .setSmallIcon(icon)
                .setStyle(inboxStyle)
                .setTicker(text)
                .setWhen(time)
                .setAutoCancel(true)
                .setContentTitle("HI................")
                .setContentText("Its ur day......")
                .setLargeIcon(bmp)
                .setSound(soundUri)
                .build();
        /* // Clear the notification when it is pressed
        notification.flags |= Notification.FLAG_AUTO_CANCEL;*/

        // Send the notification to the system.
        notificationManager.notify(NOTIFICATION, notification);

        // Stop the service when we are finished
        stopSelf();
    }
}
