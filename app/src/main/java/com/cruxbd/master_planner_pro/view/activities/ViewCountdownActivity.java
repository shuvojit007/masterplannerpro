package com.cruxbd.master_planner_pro.view.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cruxbd.master_planner_pro.App;
import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.di.view_countdown_activity_feature.component.DaggerViewCountdownActivityComponent;
import com.cruxbd.master_planner_pro.di.view_countdown_activity_feature.component.ViewCountdownActivityComponent;
import com.cruxbd.master_planner_pro.model.realm_model.CountdownModel;
import com.cruxbd.master_planner_pro.model.realm_model.MasterPlannerProject;
import com.cruxbd.master_planner_pro.model.realm_model.TodoItem;
import com.cruxbd.master_planner_pro.realm_service.RealmService;
import com.cruxbd.master_planner_pro.utils.TimeFormatString;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import io.realm.Realm;

import static com.cruxbd.master_planner_pro.adapters.MasterPlannerProjectsAdapter.PROJECT_KEY;
import static com.cruxbd.master_planner_pro.adapters.TodoItemAdapter.ITEM_KEY;
import static com.cruxbd.master_planner_pro.utils.StaticValues.COUNTDOWN_ITEM;
import static com.cruxbd.master_planner_pro.utils.StaticValues.INTENT_FROM;
import static com.cruxbd.master_planner_pro.utils.StaticValues.MAIN_ACTIVITY;
import static com.cruxbd.master_planner_pro.utils.StaticValues.MASTER_PLANNER_COUNTDOWN;
import static com.cruxbd.master_planner_pro.utils.StaticValues.NORMAL_COUNTDOWN;
import static com.cruxbd.master_planner_pro.utils.StaticValues.TODO_COUNTDOWN;
import static com.cruxbd.master_planner_pro.utils.StaticValues.VIEW_COUNTDOWN_ACTIVITY;

public class ViewCountdownActivity extends AppCompatActivity {
    private static final String TAG = "MyViewCountdown";

    private Toolbar toolbar;

    CountdownModel mCountdownModel;
    TextView tvCountdown, tvCountdownStatus, tvCountdownDate, tvCountdownFrom, tvCountdownCreatedAt;
    EditText etCountdownTitle;
    Button btnViewCountdownSource;
    ImageView ivDeleteCountdown, iv_c_exp;
    Context cn;

    CountDownTimer timer;

    @Inject
    Realm realm;
    RealmService mRealmService;

    private DatePickerDialog.OnDateSetListener expireDateListener;
    private TimePickerDialog.OnTimeSetListener expireTimeListener;
    int year, month, day, hour, minute, second;
    Date countdownExpireDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_countdown);

        cn = ViewCountdownActivity.this;
        setupDagger2(cn);
        init();

        if (getIntent().getExtras() != null)
            mCountdownModel = getIntent().getExtras().getParcelable(COUNTDOWN_ITEM);


        if (mCountdownModel != null) {

            //---start countdown---//
            startCountdown(mCountdownModel.getCountDownTime());

            countdownExpireDate = mCountdownModel.getCountDownTime();


            etCountdownTitle.setText(mCountdownModel.getTitle());
            int position = etCountdownTitle.length();
            Editable etext = etCountdownTitle.getText();
            Selection.setSelection(etext, position);


            String expireAt = setExpireDate(mCountdownModel.getCountDownTime());
            tvCountdownDate.setText(expireAt);


            String createAt = setExpireDate(mCountdownModel.getCreatedAt());
            tvCountdownCreatedAt.setText("Created at " + createAt);

            if (mCountdownModel.getCountdownFrom().equals(NORMAL_COUNTDOWN)) {
                tvCountdownFrom.setVisibility(View.GONE);
                btnViewCountdownSource.setVisibility(View.GONE);
            } else if (mCountdownModel.getCountdownFrom().equals(TODO_COUNTDOWN)) {
                toolbar.setTitle("To-do Countdown");
                iv_c_exp.setImageResource(R.drawable.history_todo);
                tvCountdownFrom.setVisibility(View.VISIBLE);
                btnViewCountdownSource.setVisibility(View.VISIBLE);
                tvCountdownFrom.setText("This countdown is from your To-do list");
                btnViewCountdownSource.setText("Open To-do");
            } else if (mCountdownModel.getCountdownFrom().equals(MASTER_PLANNER_COUNTDOWN)) {
                toolbar.setTitle("Planner Countdown");
                iv_c_exp.setImageResource(R.drawable.history_planner);
                tvCountdownFrom.setVisibility(View.VISIBLE);
                btnViewCountdownSource.setVisibility(View.VISIBLE);
                tvCountdownFrom.setText("This countdown is from your Planner project list");
                btnViewCountdownSource.setText("Open Project");
            }


//            Log.d(TAG, "onCreate: mCountdownModel: " + mCountdownModel.toString());

            btnViewCountdownSource.setOnClickListener(v -> {

                if (mCountdownModel.getCountdownFrom().equals(MASTER_PLANNER_COUNTDOWN)) {
//                    Log.d(TAG, "onCreate: open master planner");
                    MasterPlannerProject project = mRealmService.getMasterPlannerProject(mCountdownModel.getProjectId());

                    Intent intent = new Intent(v.getContext(), MasterPlannerActivity.class);
                    intent.putExtra(PROJECT_KEY, project);
                    startActivity(intent);
                    if (timer != null) timer.cancel();
                    finish();
                } else if (mCountdownModel.getCountdownFrom().equals(TODO_COUNTDOWN)) {
//                    Log.d(TAG, "onCreate: open todo..");
                    TodoItem item = mRealmService.getTodoItem(mCountdownModel.getTodoId());

                    com.cruxbd.master_planner_pro.models.TodoItem todoItem = new com.cruxbd.master_planner_pro.models.TodoItem(item.getId(), item.getUserId(), item.getTask(),
                            item.getNote(), item.getDueDate(), item.getRemindDate(), item.getRepeatEnabled(), item.getRepeatType(),
                            item.getRepeatCustom(), item.getAlarm_req_code(), item.getAlarm_status(), item.getList(), item.getPriority(), item.getDeleted(), item.getLocationName(),
                            item.getLat(), item.getLng(), item.getDone(), item.getDeleted(), item.getHasCountdown(), item.getCountdown(),
                            item.getCreatedAt());

//                    Log.d(TAG, "onCreate: Todo item: "+ todoItem.getTask());
                    Intent intent = new Intent(v.getContext(), ViewTodoActivity.class);
                    intent.putExtra(INTENT_FROM, VIEW_COUNTDOWN_ACTIVITY);
                    intent.putExtra(ITEM_KEY, todoItem);
                    startActivity(intent);
                    if (timer != null)
                        timer.cancel();
                    finish();
                }

            });

            ivDeleteCountdown.setOnClickListener(v -> {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(cn, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(cn);
                }
                builder.setTitle("Delete Countdown")
                        .setMessage("Are you sure you want to delete this countdown?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            // continue with delete
                            if (mCountdownModel.getId() != null) {

                                if (mCountdownModel.getCountdownFrom().equals(NORMAL_COUNTDOWN)) {
                                    mRealmService.deleteCountdown(mCountdownModel.getId());

                                } else if (mCountdownModel.getCountdownFrom().equals(MASTER_PLANNER_COUNTDOWN)) {

                                    mRealmService.deleteMasterPlannerCountdown(mCountdownModel.getId(), mCountdownModel.getCardItemId());

                                } else if (mCountdownModel.getCountdownFrom().equals(TODO_COUNTDOWN)) {

                                    mRealmService.deleteTodoCountdown(mCountdownModel.getId(), mCountdownModel.getTodoId());

                                }

                                StyleableToast.makeText(cn, "Countdown item deleted.", Toast.LENGTH_SHORT, R.style.mytoast).show();

                                if (getIntent().getStringExtra(INTENT_FROM).equals(MAIN_ACTIVITY)) {

                                    startActivity(new Intent(cn, MainActivity.class));
                                }
                                timer.cancel();
                                finish();
                            } else {
                                StyleableToast.makeText(cn, "Countdown can not be deleted.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, (dialog, which) -> {
                            // do nothing
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            });


        }
//        else {
//            Log.d(TAG, "onCreate: mCountdownModel is null");
//        }

//        long remingTime = mCountdownModel.getCountDownTime().getTime() - System.currentTimeMillis();

    }


    private void setupDagger2(Context context) {
        ViewCountdownActivityComponent viewCountdownActivityComponent =
                DaggerViewCountdownActivityComponent
                        .builder()
                        .appComponent(App.get((Activity) context).getAppComponent())
                        .build();
        viewCountdownActivityComponent.injectViewCountdownActivity((ViewCountdownActivity) context);
        mRealmService = new RealmService(realm);
    }

    private String setExpireDate(Date date) {

        return TimeFormatString.getStringTime(date).replace("\n", ", ");
    }

    private void startCountdown(Date date) {
        long remingTime = date.getTime() - System.currentTimeMillis();

        timer = new CountDownTimer(remingTime, 1000) {

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


                tvCountdown.setText(time);
            }

            public void onFinish() {
                tvCountdown.setText(R.string.finish);
                tvCountdownStatus.setVisibility(View.GONE);
            }
        }.start();
    }

    private void init() {


        toolbar = findViewById(R.id.tl_view_countdown);
        toolbar.setTitle("Countdown");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tvCountdown = findViewById(R.id.tvCountdown);
        tvCountdownStatus = findViewById(R.id.tvCountdownStatus);
        etCountdownTitle = findViewById(R.id.etCountdownTitle_v);
        tvCountdownDate = findViewById(R.id.tvCountdownDate_v);
        tvCountdownFrom = findViewById(R.id.tvCountdownFrom);
        btnViewCountdownSource = findViewById(R.id.btnCountdownSource);
        tvCountdownCreatedAt = findViewById(R.id.tvCountdownCreatedAt);
        ivDeleteCountdown = findViewById(R.id.ivDeleteCountdown);
        iv_c_exp = findViewById(R.id.iv_c_exp);

        tvCountdownDate.setOnClickListener(v -> setCountdownTime());

    }

    private void setCountdownTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);


        expireTimeListener = (TimePicker view2, int hourOfDay, int minute) -> {

            //save remindDateTime
            Calendar c = Calendar.getInstance();
            c.setTime(countdownExpireDate);
            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
            c.set(Calendar.MINUTE, minute);
            c.set(Calendar.SECOND, 0);

            countdownExpireDate = c.getTime();
//
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
//            String total_date = new SimpleDateFormat("MMM dd, yyyy").format(countdownExpireDate.getTime());
//
            tvCountdownDate.setText("Countdown valid till " + TimeFormatString.getStringTime(countdownExpireDate));

            if (timer != null) timer.cancel();
            startCountdown(countdownExpireDate);


        };

        expireDateListener = (view1, year, month, dayOfMonth) -> {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(year, month, dayOfMonth);
            countdownExpireDate = calendar1.getTime();
            hour = calendar1.get(Calendar.HOUR);
            minute = calendar1.get(Calendar.MINUTE);

            new TimePickerDialog(this,
                    expireTimeListener,
                    hour,
                    minute,
                    false)
                    .show();

        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, expireDateListener, year, month, day);

        datePickerDialog.show();
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
                updateCountdown();
                break;
            case android.R.id.home:
                timer.cancel();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateCountdown() {
        StyleableToast.makeText(cn, "Countdown update", Toast.LENGTH_SHORT, R.style.mytoast).show();

        String title = etCountdownTitle.getText().toString().trim();
        if (!title.equals("") && countdownExpireDate != null) {
            mRealmService.updateCountdown(mCountdownModel.getId(), title, countdownExpireDate);
            startActivity(new Intent(cn, MainActivity.class));
            timer.cancel();
            finish();

        } else {
            StyleableToast.makeText(this, "Please input both fields.", Toast.LENGTH_SHORT, R.style.mytoast).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        timer.cancel();

        if (getIntent().getStringExtra(INTENT_FROM).equals(MAIN_ACTIVITY)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, CountdownViewAllActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
        finish();
    }
}
