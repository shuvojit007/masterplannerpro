package com.cruxbd.master_planner_pro.view.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cruxbd.master_planner_pro.App;
import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.di.view_todo_activity_feature.component.DaggerViewTodoActivityComponent;
import com.cruxbd.master_planner_pro.di.view_todo_activity_feature.component.ViewTodoActivityComponent;
import com.cruxbd.master_planner_pro.models.TodoItem;
import com.cruxbd.master_planner_pro.realm_service.RealmService;
import com.cruxbd.master_planner_pro.utils.TimeFormatString;

import java.util.Date;
import java.util.Objects;

import javax.inject.Inject;

import io.realm.Realm;

import static com.cruxbd.master_planner_pro.adapters.TodoItemAdapter.EDIT_KEY;
import static com.cruxbd.master_planner_pro.adapters.TodoItemAdapter.ITEM_KEY;
import static com.cruxbd.master_planner_pro.utils.StaticValues.EDIT_TODO;
import static com.cruxbd.master_planner_pro.utils.StaticValues.EDIT_TODO_REQUEST;
import static com.cruxbd.master_planner_pro.utils.StaticValues.INTENT_FROM;
import static com.cruxbd.master_planner_pro.utils.StaticValues.MAIN_ACTIVITY;
import static com.cruxbd.master_planner_pro.utils.StaticValues.VIEW_COUNTDOWN_ACTIVITY;
//import com.cruxbd.todo_scrum.model.realm_model.TodoItem;


public class ViewTodoActivity extends AppCompatActivity {
//    private static final String TAG = "MyViewTodoActivity";
    Toolbar toolbar;
    TextView tvTodo, tvNote, tvDueDate, tvRemainder, tvRepeat, tvTag, tvPriority, tvLocationName, tvCreadteDate, tvCountdown, tvLock;
    ImageView ivPriority, ivNavigation, ivDeleteTodo;
    TextView tvNoteTitle, tvDueDateTitle, tvLocatonTitle, tvCountdownTitle, tvLockTitle;
    CardView cvNote, cvDueDate, cvReminder, cvRepeat, cvLocation, cvCountdown, cvLock;
    TodoItem todoItem;

    Context cn;
    @Inject
    Realm realm;
    RealmService mRealmService;

    @Override
    protected void onResume() {
        super.onResume();
//        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_todo);
//        Log.d(TAG, "onCreate: ");
        cn = ViewTodoActivity.this;

        setupDagger2(cn);

        init();

        todoItem = Objects.requireNonNull(getIntent().getExtras()).getParcelable(ITEM_KEY);

        setDataToViews();

    }

    private void setDataToViews() {
        if (todoItem != null) {


//            Log.d(TAG, "setDataToViews: TodoItem: " + todoItem.toString());
            tvTodo.setText(todoItem.getTask());
            if (todoItem.getDone())
                tvTodo.setPaintFlags(tvTodo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            else
                tvTodo.setPaintFlags(tvTodo.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

            tvCountdownTitle.setVisibility(View.GONE);
            cvCountdown.setVisibility(View.GONE);

            if (todoItem.getHasCountdown()) {
                if (todoItem.getCountdown() != null) {
                    if (todoItem.getCountdown().getCountDownTime() != null) {
                        tvCountdownTitle.setVisibility(View.VISIBLE);
                        cvCountdown.setVisibility(View.VISIBLE);
                        startCountdown(todoItem.getCountdown().getCountDownTime());
                    }
                }
            }

            tvTag.setText(todoItem.getList());

            String note = todoItem.getNote();
            if (note != null) {
                if (!note.equals("")) {
                    tvNoteTitle.setVisibility(View.VISIBLE);
                    cvNote.setVisibility(View.VISIBLE);
                    tvNote.setText(todoItem.getNote());
                } else {
                    tvNoteTitle.setVisibility(View.GONE);
                    cvNote.setVisibility(View.GONE);
                }
            } else {
                tvNoteTitle.setVisibility(View.GONE);
                cvNote.setVisibility(View.GONE);
            }

            if (todoItem.getDueDate() != null) {
                tvDueDate.setText(TimeFormatString.getDateString(todoItem.getDueDate()));
            } else {
                tvDueDate.setText(getString(R.string.due_date_not_set));
            }

            if (todoItem.getRemindDate() != null) {

                tvRemainder.setText(String.format(getString(R.string.remind_me_at_time), TimeFormatString.getStringTime(todoItem.getRemindDate())));

            } else {
                cvReminder.setVisibility(View.GONE);
            }

            if (todoItem.getRepeatEnabled()) {
                tvRepeat.setText(todoItem.getRepeatType());
            } else {
                cvRepeat.setVisibility(View.GONE);
            }

            int priority = todoItem.getPriority();

            switch (priority) {
                case 0:
                    tvPriority.setText(getString(R.string.priority_none));
                    ivPriority.setBackgroundResource(R.drawable.circle);
                    break;
                case 1:
                    tvPriority.setText(getString(R.string.priority_low));
                    ivPriority.setBackgroundResource(R.drawable.circle_low_priority);
                    break;
                case 2:
                    tvPriority.setText(getString(R.string.priority_medium));
                    ivPriority.setBackgroundResource(R.drawable.circle_medium_priority);
                    break;
                case 3:
                    tvPriority.setText(getString(R.string.priority_high));
                    ivPriority.setBackgroundResource(R.drawable.circle_high_priority);
                    break;
            }

            //====SET LOCKED STATUS ====//
            if (todoItem.isLocked()) {
                tvLock.setText(getString(R.string.locked));
            } else {
                tvLock.setText(getString(R.string.unlocked));
            }


            if (!todoItem.getLocationName().equals("")) {
                tvLocationName.setText(todoItem.getLocationName());
            } else {
                tvLocatonTitle.setVisibility(View.GONE);
                cvLocation.setVisibility(View.GONE);
            }

            if (todoItem.getCreatedAt() != null) {
                String expDate = TimeFormatString.getStringTime(todoItem.getCreatedAt());

                tvCreadteDate.setText(String.format(getString(R.string.created_at), expDate.replace("\n", ", ")));
            }

//            Toast.makeText(this, "Received todo todoItem. " + todoItem.getTask(), Toast.LENGTH_SHORT).show();

        }
//        else {
//            Toast.makeText(this, "Did not received todo todoItem.", Toast.LENGTH_SHORT).show();
//        }
    }

    private void setupDagger2(Context context) {
        ViewTodoActivityComponent viewTodoActivityComponent =
                DaggerViewTodoActivityComponent
                        .builder()
                        .appComponent(App.get((Activity) context).getAppComponent())
                        .build();
        viewTodoActivityComponent.injectViewTodoActivity((ViewTodoActivity) context);
        mRealmService = new RealmService(realm);
    }

    private void init() {
        toolbar = findViewById(R.id.view_todo_toolbar);
        toolbar.setTitle("View Todo");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvTodo = findViewById(R.id.tvTodo_vt);
        tvCountdown = findViewById(R.id.tvCountdown_v);
        tvNote = findViewById(R.id.tvNotes_vt);
        tvDueDate = findViewById(R.id.tvDueDate_vt);
        tvRemainder = findViewById(R.id.tvReminder_vt);
        tvRepeat = findViewById(R.id.tvRepeat_vt);
        tvTag = findViewById(R.id.tvTag_vt);
        tvPriority = findViewById(R.id.tvPriority_vt);
        tvLock = findViewById(R.id.tvLock_v);
        tvLocationName = findViewById(R.id.tvLocationName_vt);
        tvCreadteDate = findViewById(R.id.tvCreadteDate_vt);
        ivPriority = findViewById(R.id.ivPriority);
        ivNavigation = findViewById(R.id.ivNavigation);
        ivDeleteTodo = findViewById(R.id.ivDeleteTodo);

        tvCountdownTitle = findViewById(R.id.tvCountdownTitle_v);
        tvNoteTitle = findViewById(R.id.tvNoteTitle);
        tvDueDateTitle = findViewById(R.id.tvDueDateTitle);
        tvLockTitle = findViewById(R.id.tvLockTitle_v);
        tvLocatonTitle = findViewById(R.id.tvLocationTitle);

        cvCountdown = findViewById(R.id.cvCountdown_v);
        cvNote = findViewById(R.id.cvNote);
        cvDueDate = findViewById(R.id.cvDueDAte);
        cvReminder = findViewById(R.id.cvReminder);
        cvRepeat = findViewById(R.id.cvRepeat);
        cvLock = findViewById(R.id.cvLock_v);
        cvLocation = findViewById(R.id.cvLocation);


        ivNavigation.setOnClickListener(v -> {

            //Todo------show navigation on google map--------------//
            Uri gmmIntentUri = Uri.parse(String.format(getString(R.string.google_map_navigation_query), todoItem.getLocationName()) );
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage(getString(R.string.google_map_package));
            startActivity(mapIntent);


        });

        ivDeleteTodo.setOnClickListener(v -> {

            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(cn, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(cn);
            }
            builder.setTitle("Delete To-do")
                    .setMessage("Are you sure you want to delete this To-do?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // continue with delete
                        if (todoItem.getId() != null) {
                            mRealmService.deletTodoListItemById(todoItem.getId());
//                            Toast.makeText(cn, "To-do item deleted.", Toast.LENGTH_SHORT).show();

                            if (getIntent().getStringExtra(INTENT_FROM).equals(VIEW_COUNTDOWN_ACTIVITY)) {
                                startActivity(new Intent(cn, CountdownViewAllActivity.class));
                            }
                            finish();

                        }
//                        else {
//                            Toast.makeText(cn, "To-do item can not be deleted.", Toast.LENGTH_SHORT).show();
//                        }
                    })
                    .setNegativeButton(android.R.string.no, (dialog, which) -> {
                        // do nothing
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

    }

    private void startCountdown(Date countDownTime) {
        long remingTime = countDownTime.getTime() - System.currentTimeMillis();

        new CountDownTimer(remingTime, 1000) {

            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;
                long months = days / 30;
                long years = months / 12;

                String strYears, strMonth, strDays, strHours, strMin, strSec;
                String time;


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

//                time = strYears + "y:" + strMonth + "m:" + strDays + "d\n" +
//                        strHours + "h:" + strMin + "m:" + strSec + "s";


                tvCountdown.setText(time);
            }

            public void onFinish() {
                tvCountdown.setText(getString(R.string.finish));

            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_view_todo_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case android.R.id.home:
                if (getIntent().getStringExtra(INTENT_FROM).equals(MAIN_ACTIVITY)) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(this, LockTodoListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();
                }
                return true;
            case R.id.editTodo:
                Intent intent = new Intent(ViewTodoActivity.this, AddTodoDetails.class);

                intent.putExtra(EDIT_KEY, todoItem);
                intent.putExtra(EDIT_TODO, true);
                startActivityForResult(intent, EDIT_TODO_REQUEST);

//                Toast.makeText(this, "Edit todo remining", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_TODO_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data.getParcelableExtra(ITEM_KEY) != null) {
                    todoItem = data.getParcelableExtra(ITEM_KEY);
                    setDataToViews();
//                    Log.d(TAG, "onActivityResult: Result Ok");
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (getIntent().getStringExtra(INTENT_FROM).equals(MAIN_ACTIVITY)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, LockTodoListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }


//        if (getIntent().getStringExtra(INTENT_FROM).equals(MAIN_ACTIVITY)) {
//
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//            startActivity(intent);
//        } else {
//            Intent intent = new Intent(this, ViewCountdownActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//            startActivity(intent);
//        }
    }
}
