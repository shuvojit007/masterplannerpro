package com.cruxbd.master_planner_pro.utils.alarm_service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.view.activities.AlarmCancelActivity;

import static com.cruxbd.master_planner_pro.utils.StaticValues.ALARM;
import static com.cruxbd.master_planner_pro.utils.StaticValues.NOTIFICATION_ID;
import static com.cruxbd.master_planner_pro.utils.StaticValues.TODO_ID;

public class RingtonePlayingService extends Service {
    private static final String TAG = "MyRingtoneService";
    private boolean isRunning;
    private Context context;
    MediaPlayer mMediaPlayer;
    Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    private int startId;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "In the RingtonePlayingService");
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        String state = intent.getExtras().getString(ALARM);
        Log.e("what is going on here  ", state);

        assert state != null;
        switch (state) {
            case "off":
                startId = 0;
                break;
            case "on":
                startId = 1;

                break;
            default:
                startId = 0;
                break;
        }


        if (!this.isRunning && startId == 1) {
            Log.e(TAG, "if there was not sound and you want start");
            if(alert!= null)
                mMediaPlayer =  MediaPlayer.create(getApplicationContext(), alert);
            else
                mMediaPlayer = MediaPlayer.create(this, R.raw.alert);
            mMediaPlayer.start();
            mMediaPlayer.setLooping(true);
            this.isRunning = true;
            this.startId = 0;

            Intent intent1 = new Intent(getApplicationContext(), AlarmCancelActivity.class);
            intent1.putExtra(TODO_ID, intent.getStringExtra(TODO_ID));
            intent1.putExtra(NOTIFICATION_ID, intent.getIntExtra(NOTIFICATION_ID, 0));

            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);

        } else if (!this.isRunning && startId == 0) {

            Log.e(TAG, "if there was not sound and you want end");
            this.isRunning = false;
            this.startId = 0;
        } else if (this.isRunning && startId == 1) {

            Log.e(TAG, "if there was not sound and you want start");
            this.isRunning = true;
            this.startId = 0;

        } else {

            Log.e(TAG, "if there was not sound and you want end");

            mMediaPlayer.stop();
            mMediaPlayer.reset();
            this.isRunning = false;
            this.startId = 0;
        }

        Log.e(TAG, "MyActivity In the service");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "on destroy called");
        super.onDestroy();
        this.isRunning = false;
    }
}
