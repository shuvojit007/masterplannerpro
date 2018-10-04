package com.cruxbd.master_planner_pro.view.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cruxbd.master_planner_pro.App;
import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.di.alarm_cancel_activity_feature.component.AlarmCancelActivityComponent;
import com.cruxbd.master_planner_pro.di.alarm_cancel_activity_feature.component.DaggerAlarmCancelActivityComponent;
import com.cruxbd.master_planner_pro.model.realm_model.TodoItem;
import com.cruxbd.master_planner_pro.realm_service.RealmService;
import com.cruxbd.master_planner_pro.utils.alarm_service.AlarmReceiver;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Inject;

import io.realm.Realm;

import static com.cruxbd.master_planner_pro.utils.StaticValues.ALARM;
import static com.cruxbd.master_planner_pro.utils.StaticValues.DESCRIPTION;
import static com.cruxbd.master_planner_pro.utils.StaticValues.NOTIFICATION_ID;
import static com.cruxbd.master_planner_pro.utils.StaticValues.TITLE;
import static com.cruxbd.master_planner_pro.utils.StaticValues.TODO_ID;

public class AlarmCancelActivity extends AppCompatActivity {
    private static final String TAG = "MyAlarmCancelActivity";
    Context cn;
    @Inject
    Realm realm;
    RealmService mRealmService;
    TodoItem item;
    String description;

    TextView tvTitle, tvDescription, tvAlarmClock;
    Button btnSnooze, btnAlarmCancel;

    private AlarmManager alarmManager;
    private PendingIntent alarmPendingIntent;
    private int notification_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_cancel);
        cn = AlarmCancelActivity.this;
        setupDagger2(cn);
        init();

        //===get todo id from Alarm broadcast receiver (AlarmReceiver.class) ===//
        String id = getIntent().getStringExtra(TODO_ID);

//        Log.d(TAG, "onCreate: getIntent todo_id: "+ id);
//        StyleableToast.makeText(this, "todo_id: " + id, Toast.LENGTH_SHORT, R.style.mytoast).show();


        //---get data of TodoItem by Id ---//
        getTodoItemData(id);

        setDataToView();

        //=== Stop Alarm & Clear Notification ====//
        notification_id = getIntent().getIntExtra(NOTIFICATION_ID, 0);

        //--- cancel notification & alarm --//
        btnAlarmCancel.setOnClickListener(v -> {
            clearNotification(notification_id);
            turnOffAlarm();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        //--- snoozing alarm---//
        btnSnooze.setOnClickListener(v -> {

            StyleableToast.makeText(cn, "Snoozing your reminder..", Toast.LENGTH_SHORT, R.style.mytoast).show();
            turnOffAlarm();
            clearNotification(notification_id);
            createSnoozeAlarm();
            finish();
        });

        //--- set clock to textview and update its time ----//
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                try{
                    while (!isInterrupted()){
                        Thread.sleep(1000);
                        runOnUiThread(() -> {
                            TextView tvClock = findViewById(R.id.tvAlarmClockBig);
                            long date = System.currentTimeMillis();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");
                            String timeStr = simpleDateFormat.format(date);
                            tvClock.setText(timeStr);
                        });
                    }
                }catch (InterruptedException e) {
//                    Log.d(TAG, "run: "+ e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        thread.start();

    }

    private void setupDagger2(Context cn) {
        AlarmCancelActivityComponent alarmCancelActivityComponent = DaggerAlarmCancelActivityComponent
                .builder()
                .appComponent(App.get((Activity) cn).getAppComponent())
                .build();
        alarmCancelActivityComponent.injectAlarmCancelActivity((AlarmCancelActivity) cn);
    }

    private void init() {
        tvAlarmClock = findViewById(R.id.tvAlarmClockBig);
        tvTitle = findViewById(R.id.tvTitleAlarm);
        tvDescription = findViewById(R.id.tvDescriptionAlarm);
        btnSnooze = findViewById(R.id.btnSnoozeAlarm);
        btnAlarmCancel = findViewById(R.id.btnAlarmCancel);

    }

    private void getTodoItemData(String id) {
//        Log.d(TAG, "getTodoItemData: id="+id);
        mRealmService = new RealmService(realm);
        item = mRealmService.getTodoItem(id);
        if(item!=null);
//            Log.d(TAG, "getTodoItemData: "+ item.toString());
    }

    private void setDataToView() {


        if (item != null) {
            tvTitle.setText("Task: " + item.getTask());
            description = "Details: \n";
            if (item.getNote() != null)
                description += item.getNote() + "\n";

            if (item.getList() != null) {
                description += "List: " + item.getList() + "\n";
            }

            if (item.getDueDate() != null) {
                description += "Due Date: " + item.getDueDate().toString() + "\n";
            }

            tvDescription.setText(description);
        }
    }

    private void turnOffAlarm() {
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra(ALARM, "off");
        sendBroadcast(intent);
    }

    private void createSnoozeAlarm() {
//        Log.d(TAG, "createAlarm: setting your alarm... ");

        alarmManager = (AlarmManager) getApplicationContext().getSystemService(this.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putInt(NOTIFICATION_ID, notification_id);
        if(item!=null){
            bundle.putString(TODO_ID, item.getId());
            bundle.putString(TITLE, item.getTask());
            bundle.putString(DESCRIPTION, description);
        }
        bundle.putString(ALARM, "on");
        intent.putExtras(bundle);

        alarmPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), item.getAlarm_req_code(), intent, 0);

        // Set the alarm
        Calendar calendar = Calendar.getInstance();

//        calendar.setTimeInMillis(System.currentTimeMillis()+ 600000);
        //Todo:: snooze set after 1 min -> for debugging  <-- this should be removed in production version
        calendar.setTimeInMillis(System.currentTimeMillis()+ 60000);



//        Log.d(TAG, "createAlarm: Alarm Time: "+calendar.get(Calendar.HOUR_OF_DAY) +
//                ":"+calendar.get(Calendar.MINUTE)+
//                ":"+calendar.get(Calendar.SECOND));


        //===for onetime alarm ===//
//        Log.d(TAG, "createAlarm: set alarm at "+ calendar.get(Calendar.HOUR_OF_DAY)+ ":"+ calendar.get(Calendar.MINUTE));

        alarmManager.set(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), alarmPendingIntent);
    }

    private void clearNotification(int notification_id) {

        if (notification_id != 0) {
            NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            assert mgr != null;
            mgr.cancel(notification_id);
        }
    }
}
