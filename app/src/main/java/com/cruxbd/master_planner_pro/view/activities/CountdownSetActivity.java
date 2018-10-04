package com.cruxbd.master_planner_pro.view.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cruxbd.master_planner_pro.App;
import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.di.countdown_set_activity_feature.component.CountdownSetActivityComponent;
import com.cruxbd.master_planner_pro.di.countdown_set_activity_feature.component.DaggerCountdownSetActivityComponent;
import com.cruxbd.master_planner_pro.realm_service.RealmService;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import io.realm.Realm;

import static com.cruxbd.master_planner_pro.utils.StaticValues.CARD_ITEM_ID;
import static com.cruxbd.master_planner_pro.utils.StaticValues.COUNTDOWN_DATE;
import static com.cruxbd.master_planner_pro.utils.StaticValues.COUNTDOWN_FROM;
import static com.cruxbd.master_planner_pro.utils.StaticValues.COUNTDOWN_TITLE;
import static com.cruxbd.master_planner_pro.utils.StaticValues.DATE_STRING;
import static com.cruxbd.master_planner_pro.utils.StaticValues.MASTER_PLANNER_COUNTDOWN;
import static com.cruxbd.master_planner_pro.utils.StaticValues.NORMAL_COUNTDOWN;
import static com.cruxbd.master_planner_pro.utils.StaticValues.TODO_COUNTDOWN;
import static com.cruxbd.master_planner_pro.view.fragments.MasterPlannerScrollViewFragment.PROJECT_ID;

public class CountdownSetActivity extends AppCompatActivity {

    private static final String TAG = "MyCountdownSet";

    private String countdownFrom;

    @Inject
    Realm realm;
    private RealmService mRealmService;
    private Context cn;
    private String mProjectId;
    private String mCardItemId;
    private String mTodoId;

    private EditText etCountdownTitle;
    private TextView tvCountdownExpDate;
    private Button btnEnableCountdown;

    private String date_str;
    private DatePickerDialog.OnDateSetListener expireDateListener;
    private TimePickerDialog.OnTimeSetListener expireTimeListener;
    private Date countdownExpireDate;
    private int year, month, day, hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown_set);
        cn = CountdownSetActivity.this;
        setupDagger2(cn);
        init();


        if (mCardItemId != null) {
            StyleableToast.makeText(cn, "card_item_id: " + mCardItemId, Toast.LENGTH_SHORT, R.style.mytoast).show();
        }

        tvCountdownExpDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);


            expireTimeListener = (TimePicker view, int hourOfDay, int minute) -> {

                //save remindDateTime
                Calendar c = Calendar.getInstance();
                c.setTime(countdownExpireDate);
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.SECOND, 0);

                countdownExpireDate = c.getTime();

                //======================

                int hour = hourOfDay;
                int minutes = minute;
                String timeSet = "";
                if (hour > 12) {
                    hour -= 12;
                    timeSet = "PM";
                } else if (hour == 0) {
                    hour += 12;
                    timeSet = "AM";
                } else if (hour == 12) {
                    timeSet = "PM";
                } else {
                    timeSet = "AM";
                }

                String min = "";
                if (minutes < 10)
                    min = "0" + minutes;
                else
                    min = String.valueOf(minutes);

                String aTime = new StringBuilder().append(hour).append(':')
                        .append(min).append(" ").append(timeSet).toString();

                SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                String dayOfTheWeek = sdf.format(c.getTime());
                String total_date = new SimpleDateFormat("MMM dd, yyyy").format(countdownExpireDate.getTime());
                date_str = aTime+ ", "+ dayOfTheWeek + ", "+ total_date;
                tvCountdownExpDate.setText("Countdown valid till " + aTime + "\n" + dayOfTheWeek + ", " + total_date);

            };

            expireDateListener = (view, year, month, dayOfMonth) -> {
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
        });


        btnEnableCountdown.setOnClickListener(v -> {
            String title = etCountdownTitle.getText().toString().trim();
            //===For testing ====//
            if (!title.equals("") && countdownExpireDate!=null) {

                //---set Master planner project countdown--//
                if (countdownFrom != null) {
                    if (countdownFrom.equals(NORMAL_COUNTDOWN)) {
                        mRealmService.createNormalCountdown(title, countdownExpireDate);
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(COUNTDOWN_TITLE, title);
                        returnIntent.putExtra(COUNTDOWN_DATE, countdownExpireDate.getTime());
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();

                    } else if (countdownFrom.equals(TODO_COUNTDOWN)) {

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(COUNTDOWN_TITLE, title);
                        returnIntent.putExtra(DATE_STRING, date_str);
                        //returnIntent.putExtra(COUNTDOWN_DATE, countdownExpireDate.getTime());
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();

                    } else if (countdownFrom.equals(MASTER_PLANNER_COUNTDOWN)) {
                        mRealmService.createPlannerCountdown(mProjectId, mCardItemId, title, countdownExpireDate);
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(COUNTDOWN_TITLE, title);
                        returnIntent.putExtra(COUNTDOWN_DATE, countdownExpireDate.getTime());
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    } else {
                        StyleableToast.makeText(cn, "Countdown not created. please try again.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                    }
                }else{
//                    Log.d(TAG, "onCreate: COUNTDOWN_FROM IS NULL");
                }


            } else {
                StyleableToast.makeText(this, "Please input both fields.", Toast.LENGTH_SHORT, R.style.mytoast).show();
            }
        });

    }

    private void setupDagger2(Context context) {
        CountdownSetActivityComponent countdownSetActivityComponent =
                DaggerCountdownSetActivityComponent.builder()
                        .appComponent(App.get((Activity) context).getAppComponent())
                        .build();
        countdownSetActivityComponent.injectCountdownSetActivity((CountdownSetActivity) context);
    }

    private void init() {
        //member variable
        mRealmService = new RealmService(realm);

        countdownFrom = getIntent().getStringExtra(COUNTDOWN_FROM);
        mProjectId = getIntent().getStringExtra(PROJECT_ID);
        mCardItemId = getIntent().getStringExtra(CARD_ITEM_ID);

//        Log.d(TAG, "init: countdownFrom: "+ countdownFrom);
//        Log.d(TAG, "init: mProjectId: " + mProjectId);
//        Log.d(TAG, "init: mCardId: " + mCardItemId);
        //views
        etCountdownTitle = findViewById(R.id.etCountdownTitle);
        tvCountdownExpDate = findViewById(R.id.tvCountdownDate);
        btnEnableCountdown = findViewById(R.id.btnEnableCountdown);
    }
}
