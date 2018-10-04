package com.cruxbd.master_planner_pro.view.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.TextUtils;
import android.transition.Explode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.cruxbd.master_planner_pro.App;
import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.di.add_todo_details_activity_feature.component.AddTodoDetailsActivityComponent;
import com.cruxbd.master_planner_pro.di.add_todo_details_activity_feature.component.DaggerAddTodoDetailsActivityComponent;
import com.cruxbd.master_planner_pro.model.realm_model.CountdownModel;
import com.cruxbd.master_planner_pro.model.realm_model.TodoItem;
import com.cruxbd.master_planner_pro.realm_service.RealmService;
import com.cruxbd.master_planner_pro.utils.TimeFormatString;
import com.cruxbd.master_planner_pro.utils.alarm_service.AlarmReceiver;
import com.cruxbd.master_planner_pro.view.activities.tags_adapter.Tag_Model;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.cruxbd.master_planner_pro.adapters.TodoItemAdapter.EDIT_KEY;
import static com.cruxbd.master_planner_pro.adapters.TodoItemAdapter.ITEM_KEY;
import static com.cruxbd.master_planner_pro.utils.StaticValues.ALARM;
import static com.cruxbd.master_planner_pro.utils.StaticValues.DESCRIPTION;
import static com.cruxbd.master_planner_pro.utils.StaticValues.EDIT_TODO;
import static com.cruxbd.master_planner_pro.utils.StaticValues.NOTIFICATION_ID;
import static com.cruxbd.master_planner_pro.utils.StaticValues.REPEAT_DAILY;
import static com.cruxbd.master_planner_pro.utils.StaticValues.REPEAT_WEEKLY;
import static com.cruxbd.master_planner_pro.utils.StaticValues.TITLE;
import static com.cruxbd.master_planner_pro.utils.StaticValues.TODO_COUNTDOWN;
import static com.cruxbd.master_planner_pro.utils.StaticValues.TODO_ID;


public class AddTodoDetails extends AppCompatActivity {
    public static final String CHANNEL_ID = "todo_scrum_app_notification_channel";
    private static final int REQUEST_CODE = 1234;
    private static final String TAG = "MyAddTodoDetails";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int NOTE_REQUEST = 10;
    private Context cn;

    private EditText todo;
    private TextView date, reminder, repeatReminder, tvPriority, tvPriorityColor, tvLocation,
            tvLock_et, tvCountdown, notes;
    DatePickerDialog.OnDateSetListener remindDateListener;
    TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private SeekBar seekBarPriority;
    private Switch todoLockSwitch;
    private Switch todoCountdownSwitch;
    private Spinner spinnerList;
    private ImageView ivClearDueDate, ivClearReminder, ivClearRepeat, ivClearLocation, mic;
    private Toolbar tl;
    ArrayList<String> listArray;
    ArrayAdapter<String> listAdapter;

    //--- For Alarm ----//
    private AlarmManager alarmManager;
    private PendingIntent alarmPendingIntent;

    //==============for storing data into db ============
    @Inject
    Realm realm;
    RealmService realmService;
    private String taskID;
    private String task;
    private String note;
    private Date dueDate;
    private Date remindDate;
    private int remindHourOfDay;
    private int remindMinute;
    private boolean repeatEnable;
    private String repeatType; //Once,EveryDay,...
    private int alarm_req_code;
    private boolean alarm_status;
    private List<String> repeatWeekDays;
    private int repeatCustom;
    private String list;
    private int priority;
    private boolean lockTodo;
    private String locationName;
    private Double lat;
    private Double lng;
    private com.cruxbd.master_planner_pro.models.TodoItem mTodoItem;

    private int year, month, day, hour, minute;
    private static final int av = 0;
    //----countdown---//
    private String countdownTitle;
    private Date countdownDate;
    private String date_str;
    private TextView tvCountdownExpDate;

    //---Edit todo: Intent from viewTodoActivity ---//
    boolean editTodo = false;

    CountDownTimer timer;
    private TextView tvCountdown_preview;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Apply activity transition
            // inside your activity (if you did not enable transitions in your theme)
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

            // set an exit transition
            getWindow().setExitTransition(new Explode());
        } else {
            // Swap without transition
        }

        setContentView(R.layout.activity_add_todo_details);

        cn = this;

        setUpDagger2(cn);

        init();

        if (getIntent().getExtras() != null){
            mTodoItem = getIntent().getExtras().getParcelable(EDIT_KEY);
            editTodo = getIntent().getBooleanExtra(EDIT_TODO, false);
        }


        if (mTodoItem != null) {
//            Log.d(TAG, "onCreate: TodoItem = " + mTodoItem.toString());
            taskID = mTodoItem.getId();
            todo.setText(mTodoItem.getTask());
            int position = todo.length();
            Editable etext = todo.getText();
            Selection.setSelection(etext, position);


            if (!TextUtils.isEmpty(mTodoItem.getNote()))
                notes.setText(mTodoItem.getNote());

            // ==== SET TASK DUE TIME ====//
            if (mTodoItem.getDueDate() != null) {
                dueDate = mTodoItem.getDueDate();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(mTodoItem.getDueDate());
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                String dayOfTheWeek = sdf.format(calendar.getTime());
                String total_date = new SimpleDateFormat("MMM dd, yyyy").format(calendar.getTime());
                date.setText(dayOfTheWeek + ", " + total_date);
                ivClearDueDate.setVisibility(View.VISIBLE);
            }


            // ====SET REMINDER DATE =====//
            if (mTodoItem.getRemindDate() != null) {
                remindDate = mTodoItem.getRemindDate();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(mTodoItem.getRemindDate());
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                String dayOfTheWeek = sdf.format(calendar.getTime());
                String total_date = new SimpleDateFormat("MMM dd, yyyy").format(calendar.getTime());
                reminder.setText("Remind me at " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + "\n" + dayOfTheWeek + ", " + total_date);
                ivClearReminder.setVisibility(View.VISIBLE);
            }

            //====SET REPEAT REMINDER TYPE===//
            if (mTodoItem.getRepeatEnabled()) {
                repeatType = mTodoItem.getRepeatType();
                repeatCustom = 0;
                repeatEnable = mTodoItem.getRepeatEnabled();
                repeatReminder.setText(mTodoItem.getRepeatType());
                ivClearRepeat.setVisibility(View.VISIBLE);
            }else{
                repeatEnable = false;
                repeatType = "";
                repeatWeekDays = null;
                repeatCustom = 0;
                ivClearRepeat.setVisibility(View.GONE);
            }

            //====SET LIST ==============//
            int spinnerPosition = listAdapter.getPosition(mTodoItem.getList());
            spinnerList.setSelection(spinnerPosition);

            //====SET PRIORITY =====//
            switch (mTodoItem.getPriority()) {
                case 0:
                    priority = 0;
                    tvPriority.setText("None");
                    seekBarPriority.setProgress(0);
                    tvPriorityColor.setBackgroundResource(R.drawable.circle);
                    break;
                case 1:
                    priority = 1;
                    tvPriority.setText("Low");
                    seekBarPriority.setProgress(1);
                    tvPriorityColor.setBackgroundResource(R.drawable.circle_low_priority);
                    break;
                case 2:
                    priority = 2;
                    tvPriority.setText("Medium");
                    seekBarPriority.setProgress(2);
                    tvPriorityColor.setBackgroundResource(R.drawable.circle_medium_priority);
                    break;
                case 3:
                    priority = 3;
                    tvPriority.setText("High");
                    seekBarPriority.setProgress(3);
                    tvPriorityColor.setBackgroundResource(R.drawable.circle_high_priority);
                    break;
            }

            //====SET LOCKED STATUS ====//
            if (mTodoItem.isLocked()) {
                lockTodo = true;
                tvLock_et.setText("Locked");
                todoLockSwitch.setChecked(true);
            } else {
                lockTodo = false;
                tvLock_et.setText("Unlocked");
                todoLockSwitch.setChecked(false);
            }

            //====SET COUNTDOWN DATE ====//
            if(mTodoItem.getHasCountdown()){
                if(mTodoItem.getCountdown()!=null){
                    tvCountdown.setText("Countdown valid till " + TimeFormatString.getStringTime(mTodoItem.getCountdown().getCountDownTime()));
                    todoCountdownSwitch.setChecked(true);
                    startCountdown(mTodoItem.getCountdown().getCountDownTime());
                    tvCountdown_preview.setVisibility(View.VISIBLE);
                }else{
                    tvCountdown_preview.setVisibility(View.GONE);
                }
            }

            //====SET LOCATION NAME ====//
            if (!TextUtils.isEmpty(mTodoItem.getLocationName())) {
                lat = mTodoItem.getLat();
                lng = mTodoItem.getLng();
                locationName = mTodoItem.getLocationName();
                tvLocation.setText(mTodoItem.getLocationName());
            } else {
                tvLocation.setHint(getResources().getString(R.string.add_location));
                locationName = "";
                lat = 0.0;
                lng = 0.0;
            }

        } else {
            taskID = "";
            repeatEnable = false;
            repeatType = "";
            repeatWeekDays = null;
            repeatCustom = 0;
            priority = 0;
            lockTodo = false;
            locationName = "";
            lat = 0.0;
            lng = 0.0;
        }

        //==========Due Date================
        DatePickerDialog.OnDateSetListener dpd = (DatePicker view, int year, int monthOfYear, int dayOfMonth) -> {

//                int s=monthOfYear+1;
//                String a = dayOfMonth+"/"+s+"/"+year;
//                date.setText(""+a);

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            dueDate = calendar.getTime();


            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            String dayOfTheWeek = sdf.format(calendar.getTime());
            String total_date = new SimpleDateFormat("MMM dd, yyyy").format(calendar.getTime());
            date.setText(dayOfTheWeek + ", " + total_date);

            //visible clear button for DueDate
            ivClearDueDate.setVisibility(View.VISIBLE);

        };

        date.setOnClickListener((v) -> {
            Calendar cl = Calendar.getInstance();
            int year = cl.get(Calendar.YEAR);
            int month = cl.get(Calendar.MONTH);
            int day = cl.get(Calendar.DAY_OF_MONTH);
            new DatePickerDialog(this, dpd, year, month, day).show();
        });

        //==========Due Date Reminder================

        reminder.setOnClickListener((v) -> {

            if(mTodoItem!=null){
                if(mTodoItem.isLocked()){
                    StyleableToast.makeText(cn, "Reminder Can not be set on locked todo.\nIn order to set reminder unlock your to-do first.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                    return;
                }
            }

            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);

                mTimeSetListener = (TimePicker view, int hourOfDay, int minute) -> {

                    //save remindDateTime
                    Calendar c = Calendar.getInstance();
                    c.setTime(remindDate);
                    c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    c.set(Calendar.MINUTE, minute);
                    c.set(Calendar.SECOND, 0);

                    remindDate = c.getTime();

                    reminder.setText("Remind me at " + TimeFormatString.getStringTime(remindDate));

                    // visible clear button for dueDateReminder
                    ivClearReminder.setVisibility(View.VISIBLE);
                };

            remindDateListener = (view, year, month, dayOfMonth) -> {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(year, month, dayOfMonth);
                remindDate = calendar1.getTime();
                hour = calendar1.get(Calendar.HOUR);
                minute = calendar1.get(Calendar.MINUTE);

                new TimePickerDialog(this,
                        mTimeSetListener,
                        hour,
                        minute,
                        false)
                        .show();
            };

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, remindDateListener, year, month, day);

            datePickerDialog.show();



        });

        //========== Repeat Alarm ==================//

        repeatReminder.setOnClickListener(v -> {
            final View view = getLayoutInflater().inflate(R.layout.layout_repeat_alarm, null);

            LinearLayout llRepeatDaily = view.findViewById(R.id.llRepeatDaily);
            LinearLayout llRepeatWeekly = view.findViewById(R.id.llRepeatWeekly);
//            LinearLayout llRepeatMonthly = view.findViewById(R.id.llRepeatMonthly);
//            LinearLayout llRepeatYearly = view.findViewById(R.id.llRepeatYearly);


            if(repeatEnable)
            {
                switch (repeatType){
                    case REPEAT_DAILY:
                        llRepeatDaily.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        llRepeatWeekly.setBackgroundColor(Color.WHITE);
                        break;
                    case REPEAT_WEEKLY:
                        llRepeatWeekly.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        llRepeatDaily.setBackgroundColor(Color.WHITE);
                        break;
                }
            }


            Dialog dialog = new Dialog(cn);

            dialog.getWindow();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(true);


            llRepeatDaily.setOnClickListener(v1 -> {
                repeatEnable = true;
                repeatType=REPEAT_DAILY;
                ivClearRepeat.setVisibility(View.VISIBLE);
                dialog.dismiss();
            });

            llRepeatWeekly.setOnClickListener(v1 -> {
                repeatEnable = true;
                repeatType=REPEAT_WEEKLY;
                ivClearRepeat.setVisibility(View.VISIBLE);
                dialog.dismiss();
            });

//            llRepeatMonthly.setOnClickListener(v1 -> {
//                repeatEnable = true;
//                repeatType=REPEAT_MONTHLY;
//                dialog.dismiss();
//            });
//
//            llRepeatYearly.setOnClickListener(v1 -> {
//                repeatEnable = true;
//                repeatType=REPEAT_YEARLY;
//                dialog.dismiss();
//            });


            dialog.setOnDismissListener(dialog1 -> {
                if(repeatEnable) repeatReminder.setText("Repeat "+ repeatType);
            });


            dialog.show();
        });

//        //==============List Spinner=================//
//
//        spinnerList.setAdapter(listAdapter);

        //============== Priority Seek Bar =================//
        seekBarPriority.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                priority = progress;
                switch (progress) {
                    case 0:
                        tvPriority.setText("None");
                        tvPriorityColor.setBackgroundResource(R.drawable.circle);
                        break;
                    case 1:
                        tvPriority.setText("Low");
                        tvPriorityColor.setBackgroundResource(R.drawable.circle_low_priority);
                        break;
                    case 2:
                        tvPriority.setText("Medium");
                        tvPriorityColor.setBackgroundResource(R.drawable.circle_medium_priority);
                        break;
                    case 3:
                        tvPriority.setText("High");
                        tvPriorityColor.setBackgroundResource(R.drawable.circle_high_priority);
                        break;
                }

//                Log.d(TAG, "onProgressChanged: seekbar progress: " + progress);
                //Toast.makeText(AddTodoActivity.this, "Progress: "+ progress, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //============= TodoLockSwitch ====================//
        todoLockSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tvLock_et.setText(getString(R.string.locked));
                lockTodo = true;
                StyleableToast.makeText(this, "To-do is Locked: true", Toast.LENGTH_SHORT, R.style.mytoast).show();
            } else {
                tvLock_et.setText(getString(R.string.lock_your_todo));
                lockTodo = false;
                StyleableToast.makeText(this, "To-do Lock: false", Toast.LENGTH_SHORT, R.style.mytoast).show();
            }
        });

        //========== Todo Countdown ===================//
        todoCountdownSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
               // setTodoCountdown();

                setCountdownDate();



            } else {
                if(timer!=null)
                timer.cancel();
                tvCountdown_preview.setVisibility(View.GONE);
                tvCountdown.setText("Set countdown for this to-do.");
            }
        });

        //============== Location ======================//

        tvLocation.setOnClickListener(v -> {
            if (isServiceOk()) getLocation();
        });

        //================== Action DueDateClear button (Clear DueDate) ===============//
        ivClearDueDate.setOnClickListener(v -> {
            date.setText("");
            date.setHint("Add due date");
            dueDate = null;
            ivClearDueDate.setVisibility(View.GONE);
        });

        //================== Action ReminderClear button (Clear Reminder Time) ===============//
        ivClearReminder.setOnClickListener(v -> {
            reminder.setText("");
            reminder.setHint("Remind me at... ");
            remindDate = null;
            ivClearReminder.setVisibility(View.GONE);
        });

        //================== Action RepeatClear button (Clear Repeat) ===============//
        ivClearRepeat.setOnClickListener(v -> {
            repeatReminder.setText("");
            repeatReminder.setHint("Repeat this reminder...");
            repeatEnable = false;
            repeatType = "";
            repeatWeekDays = null;
            repeatCustom = 0;
            ivClearRepeat.setVisibility(View.GONE);
        });

        //================== Action LocationClear button (Clear Location) ============//
        ivClearLocation.setOnClickListener(v -> {
            tvLocation.setText("");
            tvLocation.setHint("Add location");
            locationName = "";
            lat = 0.0;
            lng = 0.0;
            ivClearLocation.setVisibility(View.GONE);
        });

        notes.setOnClickListener(v ->
        {
            Intent intent1 = new Intent(AddTodoDetails.this, NoteEdittext02.class).putExtra("notes",notes.getText().toString());
            startActivityForResult(intent1, NOTE_REQUEST);
        });


    }

    private void setTodoCountdown() {
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.activity_countdown_set, null);


        EditText etCountdownTitle = view.findViewById(R.id.etCountdownTitle);
        tvCountdownExpDate = view.findViewById(R.id.tvCountdownDate);
        Button btnEnableCountdown = view.findViewById(R.id.btnEnableCountdown);

        //======= create dialogue ========//
        Dialog dialog = new Dialog(this);
        dialog.setTitle("Set countdown");
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);

        // open EditTodoFragment
        tvCountdownExpDate.setOnClickListener(v -> {
            setCountdownDate();


        });

        // button save countdown
        btnEnableCountdown.setOnClickListener(v -> {
            String title = etCountdownTitle.getText().toString().trim();
            //===For testing ====//
            if (!title.equals("") && countdownDate!=null) {




            } else {
                StyleableToast.makeText(this, "Please input both fields.", Toast.LENGTH_SHORT,R.style.mytoast).show();
            }
        });
        dialog.show();
    }

    private void setCountdownDate() {
        //---getting date for countdown---//

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);



        mTimeSetListener = (TimePicker view, int hourOfDay, int minute) -> {

            //save remindDateTime
            Calendar c = Calendar.getInstance();
            c.setTime(countdownDate);
            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
            c.set(Calendar.MINUTE, minute);
            c.set(Calendar.SECOND, 0);

            countdownDate = c.getTime();

//            //======================
//
//            int hour = hourOfDay;
//            int minutes = minute;
//            String timeSet = "";
//            if (hour > 12) {
//                hour -= 12;
//                timeSet = "PM";
//            } else if (hour == 0) {
//                hour += 12;
//                timeSet = "AM";
//            } else if (hour == 12) {
//                timeSet = "PM";
//            } else {
//                timeSet = "AM";
//            }
//
//            String min = "";
//            if (minutes < 10)
//                min = "0" + minutes;
//            else
//                min = String.valueOf(minutes);
//
//            String aTime = new StringBuilder().append(hour).append(':')
//                    .append(min).append(" ").append(timeSet).toString();
//
//            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
//            String dayOfTheWeek = sdf.format(c.getTime());
//            String total_date = new SimpleDateFormat("MMM dd, yyyy").format(countdownDate.getTime());

            tvCountdown.setText("Countdown valid till " + TimeFormatString.getStringTime(countdownDate));

            startCountdown(countdownDate);
            tvCountdown_preview.setVisibility(View.VISIBLE);
        };

        remindDateListener = (view, year, month, dayOfMonth) -> {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(year, month, dayOfMonth);
            countdownDate = calendar1.getTime();
            hour = calendar1.get(Calendar.HOUR);
            minute = calendar1.get(Calendar.MINUTE);

            new TimePickerDialog(this,
                    mTimeSetListener,
                    hour,
                    minute,
                    false)
                    .show();

        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, remindDateListener, year, month, day);

        datePickerDialog.show();

            //----



        }

    private void setUpDagger2(Context cn) {
        //Todo================Set Up Dagger 2 ==========================
        AddTodoDetailsActivityComponent addTodoDetailsActivityComponent =
                DaggerAddTodoDetailsActivityComponent
                        .builder()
                        .appComponent(App.get((Activity) cn).getAppComponent())
                        .build();

        addTodoDetailsActivityComponent.injectAddTodoDetailsActivity((AddTodoDetails) cn);
    }

    public void AddTag(){

        LayoutInflater inflater1 = LayoutInflater.from(cn);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(cn);
        final View dview = inflater1.inflate(R.layout.add_tags_layout,null);
        builder1.setView(dview);
        final AlertDialog dialog1= builder1.create();
        final EditText gettodo = dview.findViewById(R.id.todo_tag_edittex);
        final Button canceltag = dview.findViewById(R.id.todo_tag_cancel);
        final Button addtag = dview.findViewById(R.id.todo_tag_add);

        canceltag.setOnClickListener((vv)-> dialog1.dismiss());


        addtag.setOnClickListener((vv)->{
            RealmService.AddTag(gettodo.getText().toString());

            listArray.clear();
            listArray.add("Default");
            RealmResults<Tag_Model> res = RealmService.ReadTagList();
            for (Tag_Model data:res){
                listArray.add(data.getTagTitle());
            }
            listAdapter.notifyDataSetChanged();

            dialog1.dismiss();

        });
        dialog1.show();

    }


    private void init() {


        tl = findViewById(R.id.add_todo_tl);
        tl.setTitle("Add Todo");
        tl.setTitleTextColor(Color.WHITE);
        setSupportActionBar(tl);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        todo = findViewById(R.id.add_todo_et);
        notes = findViewById(R.id.add_todo_Notes_et);
        date = findViewById(R.id.add_todo_DueDate_et);
        reminder = findViewById(R.id.add_todo_Reminder);
        repeatReminder = findViewById(R.id.tv_card_item_repeat);
        spinnerList = findViewById(R.id.spinnerList_et);
        tvPriority = findViewById(R.id.tvPriority_at);
        tvPriorityColor = findViewById(R.id.tvPriorityColor);
        seekBarPriority = findViewById(R.id.sbPriority_at);
        todoLockSwitch = findViewById(R.id.switchLock_at);
        tvCountdown = findViewById(R.id.tvCountdown_et);
        todoCountdownSwitch = findViewById(R.id.switchCountdown_at);
        tvLock_et = findViewById(R.id.tvLock_et);
        tvLocation = findViewById(R.id.tvLocationName_et);
        ivClearDueDate = findViewById(R.id.ivDueDateClear);
        ivClearReminder = findViewById(R.id.ivReminderClear);
        ivClearRepeat = findViewById(R.id.ivRepeatClear);
        ivClearLocation = findViewById(R.id.ivLocationClear);
        tvCountdown_preview = findViewById(R.id.tvCountdown_addTodo);

        //==== TAG LIST =====//
        RealmResults<Tag_Model> res = RealmService.ReadTagList();
        listArray = new ArrayList<>();
        listArray.add("Default");

        for (Tag_Model data:res){
            listArray.add(data.getTagTitle());
        }

        listAdapter = new ArrayAdapter<>(this, R.layout.layout_spinner_list, R.id.text, listArray);

        spinnerList.setAdapter(listAdapter);

        findViewById(R.id.addtag).setOnClickListener(v->AddTag());

        //Todo===================Speak to speech=======================
        mic = findViewById(R.id.mic);
        // Disable button if no recognition service is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            mic.setEnabled(false);
        }
        /**
         * Handle the action of the button being clicked
         */
        mic.setOnClickListener(v -> promptspeech());
        //Todo===================Speak to speech=======================

//        repeatEnable = false;
//        repeatType = "";
//        repeatWeekDays = null;
//        repeatCustom = 0;
//        priority = 0;
//        lockTodo = false;
//        locationName = "";
//        lat = 0.0;
//        lng = 0.0;

    }

    public boolean isServiceOk() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(AddTodoDetails.this);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {

            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(AddTodoDetails.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            StyleableToast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT, R.style.mytoast).show();
            }
        return false;
    }

    private void getLocation() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(AddTodoDetails.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
//            Log.e(TAG, "onClick: GooglePlayServicesRepairableException: " + e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
//            Log.e(TAG, "onClick: GooglePlayServicesNotAvailableException: " + e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                if (place == null) {
//                    Log.i(TAG, "No place selected");
                    return;
                }
                locationName = place.getName() + ", " + place.getAddress();
                lat = place.getLatLng().latitude;
                lng = place.getLatLng().longitude;

                tvLocation.setText(locationName);

                ivClearLocation.setVisibility(View.VISIBLE);

            } else {
                StyleableToast.makeText(this, "Can not get tvLocation. Please try again.", Toast.LENGTH_SHORT, R.style.mytoast).show();
            }

        }
        if (requestCode == av) {
            if (resultCode == RESULT_OK) {
                List<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                todo.setText(result.get(0));
                StyleableToast.makeText(cn, result.get(0), Toast.LENGTH_SHORT, R.style.mytoast).show();
            }
        }

        if (requestCode == NOTE_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (!data.getStringExtra("data").equals(""))
                    notes.setText(Html.fromHtml(data.getStringExtra("data")));

            } else {
                StyleableToast.makeText(this, "Note can not be taken.", Toast.LENGTH_SHORT, R.style.mytoast).show();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.activity_add_todo_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                createTodo();
                break;
            case android.R.id.home:
                if(timer!=null)
                timer.cancel();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        realmService = new RealmService(realm);
    }

    private void createTodo() {

        task = todo.getText().toString().trim();
        //task*
        //note
        //dueDate
        //countdownExpireDate&Time
        //repeatEnabled
        //repeatType
        //repeatWeekDays
        //repeatCustom
        //list*
        //location
        //lat
        //lon
        //done
        //deleted
        //createdAt*

        if (!task.equals("")) {
            TodoItem todoItem = new TodoItem();
            if (!taskID.equals("")) {
                todoItem.setId(taskID);
                todoItem.setCreatedAt(mTodoItem.getCreatedAt());
            }else{
                todoItem.setCreatedAt(Calendar.getInstance().getTime());
            }

            //Todo replace this with actual userId
            todoItem.setUserId("1");
            todoItem.setTask(task);
            note = notes.getText().toString().trim();
            if (!note.equals(""))
                todoItem.setNote(note);

            //task due date
            todoItem.setDueDate(dueDate);

            //reminder date and time
            todoItem.setRemindDate(remindDate);

            //repeat reminder
            todoItem.setRepeatEnabled(repeatEnable);
            todoItem.setRepeatType(repeatType);


            //mTodoItem.setRepeatWeekDays((RealmList<String>) repeatWeekDays);
            //mTodoItem.setRepeatCustom(repeatCustom);

            //tag
            if(spinnerList.getSelectedItem()!=null)
                todoItem.setList(spinnerList.getSelectedItem().toString());

            //location
            todoItem.setLocationName(locationName);
            todoItem.setLat(lat);
            todoItem.setLng(lng);

            //priority
            todoItem.setPriority(priority);

            //lockTodo
            todoItem.setLocked(lockTodo);

            //auto assign values
            todoItem.setDone(false);
            todoItem.setDeleted(false);
            if(!editTodo)
            todoItem.setCreatedAt(Calendar.getInstance().getTime());

//            Toast.makeText(cn, mTodoItem.toString(), Toast.LENGTH_SHORT).show();

            //---save data to realm---//
            CountdownModel countdownModel = new CountdownModel();
            countdownModel.setTodoId(todoItem.getId());
            countdownModel.setCountdownStatus(false);
            countdownModel.setCountdownFrom(TODO_COUNTDOWN);
            todoItem.setHasCountdown(false);

            if (countdownDate != null && todoCountdownSwitch.isChecked()) {
                todoItem.setHasCountdown(true);
                countdownModel.setTitle(todoItem.getTask());
                countdownModel.setCountDownTime(countdownDate);
            }

            todoItem.setCountdown(countdownModel);

            //---if remind date is set than create a alarm---//
            if (remindDate != null){

                long current_time = System.currentTimeMillis();

                if(remindDate.getTime() > current_time){

                   // createAlarm(todoItem.getId());

                    todoItem.setAlarm_status(true);
                    todoItem.setAlarm_req_code((int) System.currentTimeMillis());
                }else {
                    StyleableToast.makeText(cn, "Invalid date-time, reminder must set for future.\nReminder no set.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                    todoItem.setAlarm_status(false);
                }

            }else {
                todoItem.setAlarm_status(false);
            }



            realmService.addTodo(todoItem);

            createAlarm(realmService.getAllTodoAlarm());


            StyleableToast.makeText(cn, "To-do saved.", Toast.LENGTH_SHORT, R.style.mytoast).show();
            //---displayTodo is for testing purpose only... should be removed---//
           // displayTodo();

           // startActivity(new Intent(AddTodoDetails.this, MainActivity.class));
            if(editTodo){
                com.cruxbd.master_planner_pro.models.TodoItem item = new com.cruxbd.master_planner_pro.models.TodoItem(todoItem.getId(),
                        todoItem.getUserId(), todoItem.getTask(), todoItem.getNote(), todoItem.getDueDate(), todoItem.getRemindDate(),
                        todoItem.getRepeatEnabled(), todoItem.getRepeatType(), todoItem.getRepeatCustom(), todoItem.getAlarm_req_code(),
                        todoItem.getAlarm_status(), todoItem.getList(), todoItem.getPriority(), todoItem.getDeleted(), todoItem.getLocationName(),
                        todoItem.getLat(), todoItem.getLng(), todoItem.getDone(), todoItem.getDeleted(), todoItem.getHasCountdown(), todoItem.getCountdown(),
                        todoItem.getCreatedAt());

                Intent returnIntent = new Intent();
                returnIntent.putExtra(ITEM_KEY, item);
                setResult(Activity.RESULT_OK,returnIntent);
            }

            startActivity(new Intent(cn, MainActivity.class));
            finish();
        } else {
            StyleableToast.makeText(this, "Please add todo first.", Toast.LENGTH_SHORT, R.style.mytoast).show();
        }
    }

    private void createAlarm(RealmResults<TodoItem> todos) {

        alarmManager = (AlarmManager) getApplicationContext().getSystemService(this.ALARM_SERVICE);

        for(TodoItem todo: todos){
//            Log.d(TAG, "createAlarm: seting your alarm... ");
//            Log.d(TAG, "createAlarm: todo_id: " + todo.getId());

            Intent intent = new Intent(this, AlarmReceiver.class);

            Bundle bundle = new Bundle();
            bundle.putInt(NOTIFICATION_ID, todo.getAlarm_req_code());
            bundle.putString(TODO_ID, todo.getId());
            bundle.putString(TITLE, "Todo Alert");
            bundle.putString(DESCRIPTION, todo.getTask());
            bundle.putString(ALARM, "on");
            intent.putExtras(bundle);

            alarmPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), todo.getAlarm_req_code(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Set the alarm
            Calendar calendar = Calendar.getInstance();
            // calendar.setTime(countdownExpireDate);
            calendar.setTimeInMillis(todo.getRemindDate().getTime());
//            Log.d(TAG, "createAlarm: Alarm Time: " + calendar.get(Calendar.HOUR_OF_DAY) +
//                    ":" + calendar.get(Calendar.MINUTE) +
//                    ":" + calendar.get(Calendar.SECOND));


            //====== for repeat alarm ====//

            if(todo.getRepeatEnabled()){
                switch (todo.getRepeatType()){
                    case REPEAT_DAILY:
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                1000 * 60 * 60 * 24, alarmPendingIntent);



                        break;
                    case REPEAT_WEEKLY:
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                1000 * 60 * 60 * 24 * 7, alarmPendingIntent);



                        break;

               /* case REPEAT_MONTHLY:
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            1000 * 60 * 60 * 24 * 30 , alarmPendingIntent);
                    break;

                case REPEAT_YEARLY:

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            1000 * 60 * 60 * 24 * 365, alarmPendingIntent);
                    break;*/

                }
            }else{

                //===for onetime alarm ===//
//                Log.d(TAG, "createAlarm: set one time alarm at " + calendar.getTimeInMillis());

                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(), alarmPendingIntent);
            }


//        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
//                calendar.getTimeInMillis(),
//                alarmPendingIntent);


        }


    }

    // for displaying realm data

//    private void displayTodo() {
//
//
//        List<TodoItem> todoItems = realmService.readTodos();
//        StringBuilder todos = new StringBuilder();
//        for (TodoItem todoItem : todoItems) {
//            todos.append(todoItem.toString()).append("\n");
//        }
//
//       // StyleableToast.makeText(cn, todos.toString(), Toast.LENGTH_SHORT, R.style.mytoast).show();
//    }

    public void promptspeech() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something!");
        startActivityForResult(i, av);

    }

    private void startCountdown(Date date) {
        long remingTime = date.getTime() - System.currentTimeMillis();

        timer =  new CountDownTimer(remingTime, 1000) {

            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;
                long months = days / 30;
                long years = months / 12;

                String strYears, strMonth, strDays, strHours, strMin, strSec;
                String time = "";


                int sec = (int) seconds % 60;
                int min = (int) minutes % 60;
                int hour = (int) hours % 24;


                int day = (int) days % 30;
                int month = (int) months % 12;


                int year = (int) years % 12;

                if (sec < 10) strSec = "0" + sec;
                else strSec = "" + sec;

                if (min < 10) strMin = "0" + min;
                else strMin = "" + min;

                if (hour < 10) strHours = "0" + hour;
                else strHours = "" + hour;

                if (day < 10) strDays = "0" + day;
                else strDays = "" + day;

                if (month < 10) strMonth = "0" + month;
                else strMonth = "" + month;

                if (year < 10) strYears = "0" + year;
                else strYears = "" + year;


                if (year != 0) {
                    time = strYears + "y " + strMonth + "m " + strDays + "d " + strHours + "h " + strMin + "m " + strSec + "s";
                } else if (month != 0) {
                    time = strMonth + "m " + strDays + "d " + strHours + "h " + strMin + "m " + strSec + "s";
                } else if (day != 0) {
                    time = strDays + "d " + strHours + "h " + strMin + "m " + strSec + "s";
                } else {
                    time = strHours + "h " + strMin + "m " + strSec + "s";
                }

//                time = strYears + "y:" + strMonth+ "m:" + strDays + "d\n" +
//                        strHours + "h:" + strMin + "m:" + strSec + "s";


                tvCountdown_preview.setText(time);
            }

            public void onFinish() {
                tvCountdown_preview.setText(R.string.finish);

            }
        }.start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {

     /*   // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Apply activity transition
            Intent intent = new Intent(cn, MainActivity.class);
            startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            // Swap without transition
            startActivity(new Intent(cn, MainActivity.class));
        }*/

        if(timer!=null)
        timer.cancel();
        startActivity(new Intent(cn, MainActivity.class));
        super.onBackPressed();
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer!=null)
        timer.cancel();
        //realmService.CloseRealm(realm);
    }
}
