package com.cruxbd.master_planner_pro.view.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cruxbd.master_planner_pro.App;
import com.cruxbd.master_planner_pro.BackupRestore.FormatDateTime;
import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.adapters.MasterPlannerCardAdapter;
import com.cruxbd.master_planner_pro.di.add_item_to_card_activity.component.AddItemToCardActivityComponent;
import com.cruxbd.master_planner_pro.di.add_item_to_card_activity.component.DaggerAddItemToCardActivityComponent;
import com.cruxbd.master_planner_pro.model.realm_model.CardItems;
import com.cruxbd.master_planner_pro.model.realm_model.TodoItem;
import com.cruxbd.master_planner_pro.model.realm_model.TodoReminder;
import com.cruxbd.master_planner_pro.realm_service.RealmService;
import com.cruxbd.master_planner_pro.utils.TimeFormatString;
import com.cruxbd.master_planner_pro.utils.alarm_service.AlarmManagerUtil;
import com.cruxbd.master_planner_pro.utils.alarm_service.AlarmReceiver;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.cruxbd.master_planner_pro.adapters.MasterPlannerProjectsAdapter.PROJECT_KEY;
import static com.cruxbd.master_planner_pro.utils.StaticValues.ALARM;
import static com.cruxbd.master_planner_pro.utils.StaticValues.ALARM_TITLE_PLANNER;
import static com.cruxbd.master_planner_pro.utils.StaticValues.CARD_POSITION;
import static com.cruxbd.master_planner_pro.utils.StaticValues.CARD_TITLE;
import static com.cruxbd.master_planner_pro.utils.StaticValues.DESCRIPTION;
import static com.cruxbd.master_planner_pro.utils.StaticValues.NOTIFICATION_ID;
import static com.cruxbd.master_planner_pro.utils.StaticValues.REPEAT_DAILY;
import static com.cruxbd.master_planner_pro.utils.StaticValues.REPEAT_WEEKLY;
import static com.cruxbd.master_planner_pro.utils.StaticValues.TITLE;
import static com.cruxbd.master_planner_pro.utils.StaticValues.TODO_ID;
import static com.cruxbd.master_planner_pro.view.fragments.MasterPlannerScrollViewFragment.PROJECT_ID;

public class AddItemToCard extends AppCompatActivity {

    private static final String TAG = "MyAddItemToCardActivity";
    Toolbar toolbar;
    EditText etDescription, etBillFor, etAmount;
    TextView tvAccountingTitle, tvReminderTitle, tvReminder, tvRepeat;
    RelativeLayout rlCardItemBilling, rlReminder, rlRepeat;
    LinearLayout llPlannerBilling, llPlannerReminder, llPlannerBottomActionButton;
    ImageView ivBillingViewClear, ivReminderViewClear;
    private Button btnAddBilling, btnAddReminder;
    private View vButtonSeperator;
    private boolean repeatEnable;
    private String repeatType;

    @Inject
    Realm realm;
    RealmService mRealmService;
    Context context;

    private String projectID;
    private CardItems mCardItem;

    private Date remindDate;
    private int year, month, day, hour, minute;
    DatePickerDialog.OnDateSetListener remindDateListener;
    TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private int card_position;

    //--- For Alarm ----//
    private AlarmManager alarmManager;
    private PendingIntent alarmPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_to_card);
        context = AddItemToCard.this;
        setUpDagger2(context);
        init();

       /* //--- for debugging :: starts ---//
        List<TodoReminder> reminders = mRealmService.getAllPlannerReminder();

        Log.d(TAG, "onCreate: ");
        for (TodoReminder reminder : reminders) {
            Log.d(TAG, "onCreate: Planner Reminde Date: " + TimeFormatString
                    .getStringTime(reminder.getRemindDate()));
        }
        //--- debugging :: ends ---//*/


        //===== get CardItem data =======//
        mCardItem = getIntent().getExtras().getParcelable(MasterPlannerCardAdapter.CARD_ITEM_KEY);
        projectID = getIntent().getStringExtra(PROJECT_ID);
        card_position = getIntent().getIntExtra(CARD_POSITION, 0);

        LinearLayout.LayoutParams params_weight_two = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        LinearLayout.LayoutParams params_weight_one = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );


        //===== Set CardItem data into views ======//
        if (mCardItem != null) {
            //--set description --//
            String description = mCardItem.getDescription();
            if (description.equals("Write something on card...")) {
                etDescription.setHint(mCardItem.getDescription());
            } else {
                etDescription.setText(mCardItem.getDescription());
            }

            //==== Account Billing ==== Handle Dynamic View Change//
            if (mCardItem.getBillFor() != null && !TextUtils.isEmpty(mCardItem.getBillFor())) {
                llPlannerBilling.setVisibility(View.VISIBLE);
                etBillFor.setText(mCardItem.getBillFor());
                etAmount.setText(mCardItem.getAmount().toString());

                if (mCardItem.getReminder() != null) {
                    llPlannerBottomActionButton.setVisibility(View.GONE);
                } else {
                    btnAddBilling.setVisibility(View.GONE);
                    vButtonSeperator.setVisibility(View.GONE);
                    btnAddReminder.setLayoutParams(params_weight_two);
                }

            } else {
                llPlannerBilling.setVisibility(View.GONE);
                if (mCardItem.getReminder() != null) {
                    llPlannerReminder.setVisibility(View.VISIBLE);
                    btnAddReminder.setVisibility(View.GONE);
                    vButtonSeperator.setVisibility(View.GONE);
                    btnAddBilling.setLayoutParams(params_weight_two);
                } else {
                    llPlannerBottomActionButton.setVisibility(View.VISIBLE);
                    btnAddBilling.setLayoutParams(params_weight_one);
                    btnAddReminder.setLayoutParams(params_weight_one);
                    btnAddBilling.setVisibility(View.VISIBLE);
                    vButtonSeperator.setVisibility(View.VISIBLE);
                    btnAddReminder.setVisibility(View.VISIBLE);
                }
            }


//            if(mCardItem.getBillFor() != null && mCardItem.getAmount() != null){
//                etAmount.setText(mCardItem.getAmount().toString());
//            }

            //==== Reminder === Handle Dynamic View Change//
            if (mCardItem.getReminder() != null) {

//                rlReminder.setVisibility(View.VISIBLE);
//                rlRepeat.setVisibility(View.VISIBLE);

                llPlannerReminder.setVisibility(View.VISIBLE);
                tvReminder.setText("Remind me at " + TimeFormatString.getStringTime(mCardItem.getReminder().getRemindDate()));
                if (mCardItem.getReminder().getRepeatEnabled()) {
                    tvRepeat.setText(mCardItem.getReminder().getRepeatType());
                }

                if (mCardItem.getBillFor() != null) {
                    llPlannerBottomActionButton.setVisibility(View.GONE);
                } else {
                    btnAddReminder.setVisibility(View.GONE);
                    vButtonSeperator.setVisibility(View.GONE);
                    btnAddBilling.setLayoutParams(params_weight_two);
                }

            } else {
                llPlannerReminder.setVisibility(View.GONE);
                if (mCardItem.getBillFor() != null) {
                    llPlannerBilling.setVisibility(View.VISIBLE);
                    btnAddBilling.setVisibility(View.GONE);
                    vButtonSeperator.setVisibility(View.GONE);
                    btnAddReminder.setLayoutParams(params_weight_two);
                } else {
                    llPlannerBottomActionButton.setVisibility(View.VISIBLE);
                    btnAddBilling.setLayoutParams(params_weight_one);
                    btnAddReminder.setLayoutParams(params_weight_one);
                    btnAddBilling.setVisibility(View.VISIBLE);
                    vButtonSeperator.setVisibility(View.VISIBLE);
                    btnAddReminder.setVisibility(View.VISIBLE);
                }
            }
        }


        //----Visible AddBilling View----//
        btnAddBilling.setOnClickListener(v -> {
            llPlannerBilling.setVisibility(View.VISIBLE);

            if (btnAddReminder.getVisibility() == View.VISIBLE) {
                btnAddBilling.setVisibility(View.GONE);
                vButtonSeperator.setVisibility(View.GONE);
                btnAddReminder.setLayoutParams(params_weight_two);
            } else {
                llPlannerBottomActionButton.setVisibility(View.GONE);
            }

        });

        //----Visible AddReminder View----//
        btnAddReminder.setOnClickListener(v -> {
            llPlannerReminder.setVisibility(View.VISIBLE);
            if (btnAddBilling.getVisibility() == View.VISIBLE) {
                btnAddReminder.setVisibility(View.GONE);
                vButtonSeperator.setVisibility(View.GONE);
                btnAddBilling.setLayoutParams(params_weight_two);
            } else {
                llPlannerBottomActionButton.setVisibility(View.GONE);
            }
        });

        //----Invisible AddBilling View---//
        ivBillingViewClear.setOnClickListener(v ->
        {
            llPlannerBilling.setVisibility(View.GONE);
            if (llPlannerReminder.getVisibility() == View.VISIBLE) {
                llPlannerBottomActionButton.setVisibility(View.VISIBLE);
                btnAddBilling.setVisibility(View.VISIBLE);
                vButtonSeperator.setVisibility(View.GONE);
                btnAddBilling.setLayoutParams(params_weight_two);
            } else {
                llPlannerBottomActionButton.setVisibility(View.VISIBLE);
                btnAddBilling.setLayoutParams(params_weight_one);
                btnAddReminder.setLayoutParams(params_weight_one);
                btnAddBilling.setVisibility(View.VISIBLE);
                vButtonSeperator.setVisibility(View.VISIBLE);
                btnAddReminder.setVisibility(View.VISIBLE);
            }
        });

        //----Invisible AddReminder View---//
        ivReminderViewClear.setOnClickListener(v ->
        {
            llPlannerReminder.setVisibility(View.GONE);
            if (llPlannerBilling.getVisibility() == View.VISIBLE) {
                llPlannerBottomActionButton.setVisibility(View.VISIBLE);
                btnAddReminder.setVisibility(View.VISIBLE);
                vButtonSeperator.setVisibility(View.GONE);
                btnAddBilling.setLayoutParams(params_weight_two);
            } else {
                llPlannerBottomActionButton.setVisibility(View.VISIBLE);
                btnAddBilling.setVisibility(View.VISIBLE);
                vButtonSeperator.setVisibility(View.VISIBLE);
                btnAddReminder.setVisibility(View.VISIBLE);
                btnAddBilling.setLayoutParams(params_weight_one);
                btnAddReminder.setLayoutParams(params_weight_one);
            }

        });

        //----get Remind Date and Time from user---//
        rlReminder.setOnClickListener(v -> {

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

                //======================

//                int hour = hourOfDay;
//                int minutes = minute;
//                String timeSet = "";
//                if (hour > 12) {
//                    hour -= 12;
//                    timeSet = "PM";
//                } else if (hour == 0) {
//                    hour += 12;
//                    timeSet = "AM";
//                } else if (hour == 12) {
//                    timeSet = "PM";
//                } else {
//                    timeSet = "AM";
//                }
//
//                String min = "";
//                if (minutes < 10)
//                    min = "0" + minutes;
//                else
//                    min = String.valueOf(minutes);
//
//                String aTime = new StringBuilder().append(hour).append(':')
//                        .append(min).append(" ").append(timeSet).toString();
//
//                SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
//                String dayOfTheWeek = sdf.format(c.getTime());
//                String total_date = new SimpleDateFormat("MMM dd, yyyy").format(remindDate.getTime());

                tvReminder.setText("Remind me at " + TimeFormatString.getStringTime(remindDate));

//                StyleableToast.makeText(context, "Remind date: " + remindDate.toString(), Toast.LENGTH_SHORT, R.style.mytoast).show();
                // visible clear button for dueDateReminder
//                ivClearReminder.setVisibility(View.VISIBLE);

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

//            StyleableToast.makeText(context, "set reminder", Toast.LENGTH_SHORT, R.style.mytoast).show();

        });

        //----get Repeat For reminder from user----//
        rlRepeat.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context,v);

            popupMenu.getMenuInflater().inflate(R.menu.repeat_alarm_popup_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {

                switch (item.getItemId()){
                    case R.id.repeat_once:
                        tvRepeat.setText(getString(R.string.repeat_once));
                       // StyleableToast.makeText(context, "Once", Toast.LENGTH_SHORT, R.style.mytoast).show();
                        break;
                    case R.id.repeat_daily:
                        tvRepeat.setText(getString(R.string.repeat_daily));
                        repeatEnable = true;
                        repeatType = REPEAT_DAILY;
                      //  StyleableToast.makeText(context, "Daily", Toast.LENGTH_SHORT, R.style.mytoast).show();
                        break;
                    case R.id.repeat_weekly:
                        tvRepeat.setText(getString(R.string.repeat_weekly));
                        repeatEnable = true;
                        repeatType = REPEAT_WEEKLY;
                      //  StyleableToast.makeText(context, "Weekly", Toast.LENGTH_SHORT, R.style.mytoast).show();
                        break;
                }

                return true;
            });
            popupMenu.show();
        });


    }

    private void setUpDagger2(Context context) {
        AddItemToCardActivityComponent addItemToCardActivityComponent =
                DaggerAddItemToCardActivityComponent
                        .builder()
                        .appComponent(App.get((Activity) context).getAppComponent())
                        .build();
        addItemToCardActivityComponent.injectAddTodoItemToCardActivity((AddItemToCard) context);
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar_add_card_item);
        String title = getIntent().getStringExtra(CARD_TITLE);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());


        etDescription = findViewById(R.id.et_card_item_description);
        etBillFor = findViewById(R.id.etBillFor);
        etAmount = findViewById(R.id.etAmount);

        tvAccountingTitle = findViewById(R.id.tvAccountingTitle);
        tvReminderTitle = findViewById(R.id.tvReminderTitle);
        tvReminder = findViewById(R.id.tv_card_item_reminder_MP);
        tvRepeat = findViewById(R.id.tv_card_item_repeat);

        llPlannerBilling = findViewById(R.id.llPlannerBilling);
        llPlannerReminder = findViewById(R.id.llPlannerReminder);
        llPlannerBottomActionButton = findViewById(R.id.llPlannerBottomActionButton);

        btnAddBilling = findViewById(R.id.btnAddBilling);
        btnAddReminder = findViewById(R.id.btnAddReminder);
        vButtonSeperator = findViewById(R.id.viewButtonSeparator);

        ivBillingViewClear = findViewById(R.id.ivPlannerBillingClear);
        ivReminderViewClear = findViewById(R.id.ivPlannerReminderClear);


        rlCardItemBilling = findViewById(R.id.rl_card_item_billing);
        rlReminder = findViewById(R.id.rlReminder);
        rlRepeat = findViewById(R.id.rlRepeat);

        mRealmService = new RealmService(realm);

        repeatEnable = false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_add_item_to_card_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_item_save:
                createOrUpdateItem();
                break;
            case R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createOrUpdateItem() {
        String description = etDescription.getText().toString().trim();

        if (!description.equals(""))
            mCardItem.setDescription(description);

        if (llPlannerBilling.getVisibility() == View.VISIBLE) {
//            StyleableToast.makeText(this, "Accounting visible", Toast.LENGTH_SHORT, R.style.mytoast).show();
            String billFor = etBillFor.getText().toString().trim();
            if (!billFor.equals("")) {
                mCardItem.setBillFor(billFor);
                String amount = etAmount.getText().toString().trim();
                if (!amount.equals("")) {
                    mCardItem.setAmount(Double.parseDouble(amount));
                } else {
                    StyleableToast.makeText(this, "Enter Amount", Toast.LENGTH_SHORT, R.style.mytoast).show();
                }
            } else {
                StyleableToast.makeText(this, "Enter bill for..", Toast.LENGTH_SHORT, R.style.mytoast).show();
            }
        } else {
            mCardItem.setBillFor(null);
            mCardItem.setAmount(0.0);
        }

        if (llPlannerReminder.getVisibility() == View.VISIBLE) {
            if (remindDate != null) {

                TodoReminder reminder = new TodoReminder();
                reminder.setRemindDate(remindDate);

                long current_time = System.currentTimeMillis();

                if (remindDate.getTime() > current_time) {
                    reminder.setAlarm_status(true);
                    reminder.setAlarm_req_code((int) System.currentTimeMillis());
                    reminder.setReminderEnabled(true);

                } else {
                    reminder.setAlarm_status(false);
                    reminder.setReminderEnabled(false);
                    StyleableToast.makeText(context, "Invalid date-time, reminder must set for future.\nReminder no set.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                }

                reminder.setRepeatEnabled(repeatEnable);
                reminder.setRepeatType(repeatType);
                reminder.setRepeatEnabled(false);
                reminder.setRepeatCustom(0);
                mCardItem.setReminder(reminder);
            }
        } else {

            if (mCardItem.getReminder() != null) {
                mCardItem.getReminder().setAlarm_status(false);
                mCardItem.getReminder().setReminderEnabled(false);

            } else {
                TodoReminder reminder = new TodoReminder();
                reminder.setAlarm_status(false);
                reminder.setReminderEnabled(false);
                mCardItem.setReminder(reminder);
            }
        }

        mCardItem.setLocation(mCardItem.getLocation());

        //update card
        mRealmService.updateCardItem(mCardItem);


        //---- update alarm list ---//
        Log.d(TAG, "createOrUpdateItem: AlarmManagerUtil call...");
//        AlarmManagerUtil alarmManagerUtil = new AlarmManagerUtil(getApplicationContext());
//        alarmManagerUtil.initializeAlarm();

        createAlarm(mRealmService.getAllPlannerReminder());


        Intent intent = new Intent(this, MasterPlannerActivity.class);
        intent.putExtra(PROJECT_KEY, mRealmService.getMasterPlannerProject(projectID));
//        Log.d(TAG, "createOrUpdateItem: card_position: " + card_position);

        intent.putExtra(CARD_POSITION, card_position);
        startActivity(intent);

        finish();

    }

    private void createAlarm(RealmResults<TodoReminder> reminders) {
        alarmManager = (AlarmManager) getApplicationContext().getSystemService(this.ALARM_SERVICE);

        for(TodoReminder reminder: reminders){
            Log.d(TAG, "createAlarm: seting your alarm... ");
            Log.d(TAG, "createAlarm: cardItemId: " + mCardItem.getId());

            Intent intent = new Intent(this, AlarmReceiver.class);

            Bundle bundle = new Bundle();
            bundle.putInt(NOTIFICATION_ID, reminder.getAlarm_req_code());
            bundle.putString(TODO_ID, mCardItem.getId());
            bundle.putString(TITLE, ALARM_TITLE_PLANNER);
            bundle.putString(DESCRIPTION, ALARM_TITLE_PLANNER + " Description");
            bundle.putString(ALARM, "on");
            intent.putExtras(bundle);

            alarmPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), reminder.getAlarm_req_code(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Set the alarm
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(reminder.getRemindDate().getTime());
            Log.d(TAG, "createAlarm: Alarm Time: " + calendar.get(Calendar.HOUR_OF_DAY) +
                    ":" + calendar.get(Calendar.MINUTE) +
                    ":" + calendar.get(Calendar.SECOND));


            //====== for repeat alarm ====//
            if(reminder.getRepeatEnabled()){
                switch (reminder.getRepeatType()){
                    case REPEAT_DAILY:
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                1000 * 60 * 60 * 24, alarmPendingIntent);

                        break;
                    case REPEAT_WEEKLY:
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                1000 * 60 * 60 * 24 * 7, alarmPendingIntent);

                        break;

//                case REPEAT_MONTHLY:
//                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                            1000 * 60 * 60 * 24 * 30 , alarmPendingIntent);
//                    break;
//
//                case REPEAT_YEARLY:
//
//                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                            1000 * 60 * 60 * 24 * 365, alarmPendingIntent);
//                    break;

                }
            }else{

                //===for onetime alarm ===//
//                Log.d(TAG, "createAlarm: set one time alarm at " + calendar.getTimeInMillis());

                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(), alarmPendingIntent);
            }
        }

    }


    @Override
    public boolean onNavigateUp() {
        finish();
        return super.onNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mRealmService.CloseRealm(realm);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MasterPlannerActivity.class);
        intent.putExtra(PROJECT_KEY, mRealmService.getMasterPlannerProject(projectID));
        startActivity(intent);
        finish();
    }
}
