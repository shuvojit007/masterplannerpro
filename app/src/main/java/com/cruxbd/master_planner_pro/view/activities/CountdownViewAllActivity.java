package com.cruxbd.master_planner_pro.view.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cruxbd.master_planner_pro.App;
import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.adapters.CountdownAdapter;
import com.cruxbd.master_planner_pro.di.countdown_view_all_activity.component.CountdownViewAllActivityComponent;
import com.cruxbd.master_planner_pro.di.countdown_view_all_activity.component.DaggerCountdownViewAllActivityComponent;
import com.cruxbd.master_planner_pro.model.realm_model.CountdownModel;
import com.cruxbd.master_planner_pro.realm_service.RealmService;
import com.cruxbd.master_planner_pro.utils.ItemDecoration;
import com.muddzdev.styleabletoastlibrary.StyleableToast;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmList;

public class CountdownViewAllActivity extends AppCompatActivity {

    private static final String TAG = "CViewAllActivity";

    private Context cn;
    private Toolbar mToolbar;
    private RecyclerView mCountdown_rec_view;
    private CountdownAdapter mCountdownAdapter;
    private List<CountdownModel> mCountdownModelList;
    private FloatingActionButton add_countdown_fab;
    private TextView tvMsgEmptyCountdonw;

    //create countdown
    private int year, month, day, hour, minute, second;
    private DatePickerDialog.OnDateSetListener expireDateListener;
    private TimePickerDialog.OnTimeSetListener expireTimeListener;
    private Date countdownExpireDate;

    @Inject
    Realm realm;
    private RealmService mRealmService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown_view_all);

        cn = this;
        setupDagger2(cn);
        init();
        generateRecyclerView();

        mCountdown_rec_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0){
                    add_countdown_fab.setVisibility(View.GONE);
                }else {
                    add_countdown_fab.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void generateRecyclerView() {
        mCountdownModelList = mRealmService.getCountdownModels();
        if(mCountdownModelList.size() == 0){
            tvMsgEmptyCountdonw.setVisibility(View.VISIBLE);
        }else {
            tvMsgEmptyCountdonw.setVisibility(View.GONE);
        }

//        Log.d(TAG, "generateRecyclerView: mCountdownModelList.size() = " + mCountdownModelList.size());
        mCountdownAdapter = new CountdownAdapter(mCountdownModelList, cn);
        //mCountdown_rec_view.setLayoutManager(new LinearLayoutManager(cn));
        mCountdown_rec_view.setAdapter(mCountdownAdapter);
        mCountdown_rec_view.setItemAnimator(new DefaultItemAnimator());
        mCountdown_rec_view.addItemDecoration(new ItemDecoration(5, 5, 10, 5));
    }

    private void setupDagger2(Context context) {
        CountdownViewAllActivityComponent countdownViewAllActivityComponent =
                DaggerCountdownViewAllActivityComponent
                        .builder()
                        .appComponent(App.get((Activity) context).getAppComponent())
                        .build();
        countdownViewAllActivityComponent.injectCountdownViewAllActivity((CountdownViewAllActivity) context);
    }

    private void init() {
        mToolbar = findViewById(R.id.countdown_view_all_toolbar);
        mToolbar.setTitle("Countdown");
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mCountdown_rec_view = findViewById(R.id.rvCountdownViewAll);
        mCountdown_rec_view.setLayoutManager(new LinearLayoutManager(cn));
        mRealmService = new RealmService(realm);
        add_countdown_fab = findViewById(R.id.countdown_fab);
        tvMsgEmptyCountdonw = findViewById(R.id.tvMsgEmptyCountdown);

        add_countdown_fab.setOnClickListener(v -> {
            createCountdown();
        });

    }

    private void createCountdown() {

        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_set_countdown, null);

        EditText etTitle = view.findViewById(R.id.etCountdownTitle_d);
        TextView tvDate = view.findViewById(R.id.tvCountdownDate_d);
        Button btnEnableCountdown = view.findViewById(R.id.ivBtnSaveCountdown);


        Dialog dialog = new Dialog(this);

        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);


        tvDate.setOnClickListener(v -> {
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
                tvDate.setText("Countdown valid till " + aTime + "\n" + dayOfTheWeek + ", " + total_date);

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
        });

        btnEnableCountdown.setOnClickListener(v -> {

            String title = etTitle.getText().toString().trim();
            if (!title.equals("") && countdownExpireDate != null) {
                mRealmService.createNormalCountdown(title, countdownExpireDate);


                mCountdownModelList = new RealmList<>();
                mCountdownAdapter.notifyDataSetChanged();
                generateRecyclerView();


                dialog.dismiss();

                //---set Master planner project countdown--//

            } else {
                StyleableToast.makeText(this, "Please input both fields.", Toast.LENGTH_SHORT, R.style.mytoast).show();
            }
        });


        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
//            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        generateRecyclerView();
    }
}
