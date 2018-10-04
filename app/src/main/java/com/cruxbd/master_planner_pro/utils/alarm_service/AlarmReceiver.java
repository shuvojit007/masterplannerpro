package com.cruxbd.master_planner_pro.utils.alarm_service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;
import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.view.activities.AddTodoDetails;
import com.cruxbd.master_planner_pro.view.activities.AlarmCancelActivity;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.cruxbd.master_planner_pro.App.CHANNEL_1_ID;
import static com.cruxbd.master_planner_pro.utils.StaticValues.DESCRIPTION;
import static com.cruxbd.master_planner_pro.utils.StaticValues.NOTIFICATION_ID;
import static com.cruxbd.master_planner_pro.utils.StaticValues.TITLE;
import static com.cruxbd.master_planner_pro.utils.StaticValues.TODO_ID;
import static com.cruxbd.master_planner_pro.view.activities.AddTodoDetails.CHANNEL_ID;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "MyAlarmReceiver";
    private static final int NOTIFY_ID = 1222;

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        Bundle bundle = intent.getExtras();
        try {
            String get_alarm_extra = bundle.getString("alarm");
            Log.e(TAG,"get_alarm_extra = " + get_alarm_extra);

            if(get_alarm_extra.equals("on")){
                assert bundle != null;
                int n_id = bundle.getInt(NOTIFICATION_ID, 0);
                String todo_id = bundle.getString(TODO_ID);
                String title = bundle.getString(TITLE);
                String description = bundle.getString(DESCRIPTION);
                Toast.makeText(context, n_id + title + description, Toast.LENGTH_LONG).show();
                Log.d(TAG, "onReceive: todo_id: "+ todo_id);
                createNotification(context, n_id, todo_id,title, description);

                Intent service_intent = new Intent(context, RingtonePlayingService.class);
                service_intent.putExtra("alarm", get_alarm_extra);
                service_intent.putExtra(TODO_ID, todo_id);
                service_intent.putExtra(NOTIFICATION_ID, n_id);
                context.startService(service_intent);
            }else {
                Log.d(TAG, "onReceive: AlarmReceiver  alarm = "+ get_alarm_extra);
                Intent service_intent = new Intent(context, RingtonePlayingService.class);
                service_intent.putExtra("alarm", get_alarm_extra);
                context.startService(service_intent);
            }

        } catch (Exception e) {
            Log.d(TAG, "onReceive: Exception: "+ e.getMessage());
            e.printStackTrace();
        }


//        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//        vibrator.vibrate(1000);

        wl.release();
    }

    private void createNotification(Context context, int n_id, String todo_id, String title, String description) {
        //Todo::  create the NotificationCompat Builder

        Intent intent = new Intent(context, AlarmCancelActivity.class);
        Log.d(TAG, "createNotification: todo_id: "+ todo_id);
        intent.putExtra(TODO_ID, todo_id);
        intent.putExtra(NOTIFICATION_ID, n_id);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, n_id, intent, PendingIntent.FLAG_CANCEL_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_1_ID)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle(title)
        .setContentText(description)
        .setAutoCancel(true)
        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
        .setSubText("Tap to view")
        .setContentIntent(pendingIntent)
        .setVisibility(Notification.VISIBILITY_PUBLIC)
        .setCategory(NotificationCompat.CATEGORY_ALARM);

        Notification notification = builder.build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

//        NotificationManager mgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//        mgr.notify(NOTIFY_ID, notification);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(n_id, notification);
        Log.d(TAG, "createNotification: notification set: notify_id="+n_id);
    }


}
