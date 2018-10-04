package com.cruxbd.master_planner_pro.view.activities;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.cruxbd.master_planner_pro.App;

import com.cruxbd.master_planner_pro.BackupRestore.BackupActivity;
import com.cruxbd.master_planner_pro.BackupRestore.RealmBackupRestore;
import com.cruxbd.master_planner_pro.di.main_activity_feature.component.DaggerMainActivityComponent;
import com.cruxbd.master_planner_pro.di.main_activity_feature.component.MainActivityComponent;
import com.cruxbd.master_planner_pro.model.realm_model.TodoItem;

import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.realm_service.RealmService;
import com.cruxbd.master_planner_pro.utils.GetFirebaseRef;
import com.cruxbd.master_planner_pro.utils.ItemDecoration;
import com.cruxbd.master_planner_pro.view.activities.location_based_reminder.LocationReminder;
import com.cruxbd.master_planner_pro.view.activities.nav_rec.rec_adapter.RecAdapter;
import com.cruxbd.master_planner_pro.view.activities.nav_rec.rec_model.childModel;
import com.cruxbd.master_planner_pro.view.activities.nav_rec.rec_model.parentModel;
import com.cruxbd.master_planner_pro.view.fragments.HomeFragment;
import com.cruxbd.master_planner_pro.view.widgets.WidgetProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialOverlayLayout;
import com.leinardi.android.speeddial.SpeedDialView;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;

import static com.cruxbd.master_planner_pro.utils.StaticValues.CREATE_COUNTDOWN_REQUEST;

public class MainActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private static final String TAG = "MyMainActivity";
    public static final String HOME_FRAGMENT_TAG = "HomeFragment";
    private static final int av = 0;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    //    ActionBar actionBar;
    DrawerLayout drawer;
    NavigationView navigationView;
     public static SpeedDialView speedDialView;

    //todo Firebase
    private DatabaseReference mDm;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    private String email;
    private String Name;

    TextView tvUserName;
    TextView tvUserEmail;
    //todo Firebase


    //==== Quick TodoAdd views===//
    private ImageView ivMic, ivQucikTodo;
    private EditText etTask;
    private static CardView card_quick_todo;


    // Drawer item
    TextView nav_MasterPlanner;
    TextView nav_LocationReminder;
    // TextView nav_RemoveAds;
    TextView nav_UpgradeAccount;
    TextView nav_Settings;
    TextView nav_MoreOfOurApps;
    TextView nav_AboutUs;
    TextView nav_drawings;
    LinearLayout nav_countdown;

    LinearLayout nav_feedbackOrHelp, nav_RateMasterPlanner, nav_ShareApp;


    //RecylerView
    List<parentModel> pmodel;
    private RecyclerView mRecyclerView;
    private Context cn;
    RealmBackupRestore realmBackupRestore;
    RealmService realmService;
    @Inject
    Realm realm;

    private RecAdapter recAdapter;
    private boolean hideIcon;

    private DatePickerDialog.OnDateSetListener expireDateListener;
    private TimePickerDialog.OnTimeSetListener expireTimeListener;
    int year, month, day, hour, minute, second;
    Date countdownExpireDate;
    private static final String NOTIFICATION_MSG = "NOTIFICATION MSG";


    public static Intent makeNotificationIntent(Context context, String msg) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(NOTIFICATION_MSG, msg);
        return intent;
    }
    Handler hand;
    int index = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Fabric.with(this, new Crashlytics());
        Fabric.with(this, new Answers());

        cn = this;
        setUpDagger2(cn);

        init();
        GetUserInformation();
        SetUpRecylerView();
        DataUSage();
        // TODO: Move this to where you establish a user session
        logUser();


        Answers.getInstance().logContentView(new ContentViewEvent());
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Home Screen Content ")
                .putContentType("Technical documentation")
                .putContentId("MainActivity"));


        realmBackupRestore = new RealmBackupRestore(this, realm);

        hand =new Handler();
        LoadHomeFragment();

        ivMic.setOnClickListener(v -> {
            promptspeech();

            Answers.getInstance().logContentView(new ContentViewEvent());
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("Home Screen Content ")
                    .putContentType("Technical documentation")
                    .putContentId("Google Mic"));


            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "mic");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "talk to speech");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        });

        ivQucikTodo.setOnClickListener(v -> {
            String task = etTask.getText().toString().trim();

            if (!task.equals("")) {
                String list = "Default";
                createQuickTodo(task, list);
                etTask.setText("");
                StyleableToast.makeText(cn, "Todo added.", Toast.LENGTH_SHORT, R.style.mytoast).show();
            } else {
                StyleableToast.makeText(cn, "Please write something", Toast.LENGTH_SHORT, R.style.mytoast).show();
            }

        });

        //=====Handle Keyboard input done =====//
        etTask.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                String task = etTask.getText().toString().trim();

                if (!task.equals("")) {
                    String list = "Default";
                    createQuickTodo(task, list);
                    etTask.setText("");
                    StyleableToast.makeText(cn, "Todo added.", Toast.LENGTH_SHORT, R.style.mytoast).show();

                } else {
                    StyleableToast.makeText(cn, "Please write something", Toast.LENGTH_SHORT, R.style.mytoast).show();

                }
                handled = true;


            }
            return handled;
        });

        nav_drawings.setOnClickListener(v ->{
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("Home Screen Content ")
                    .putContentType("Technical documentation")
                    .putContentId("Paint List"));
            startActivity(new Intent(cn, PaintList.class));
        });
        nav_MasterPlanner.setOnClickListener(v -> {
            Answers.getInstance().logContentView(new ContentViewEvent());
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("Home Screen Content ")
                    .putContentType("Technical documentation")
                    .putContentId("Master Planner Project Activity"));

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, " Planner Project Activity");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "master Planner");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


            startActivity(new Intent(cn, MasterPlannerProjectsActivity.class));
            //finish();
        });

        nav_LocationReminder.setOnClickListener(v -> {
            Bundle bundle2 = new Bundle();
            bundle2.putString(FirebaseAnalytics.Param.ITEM_ID, "2");
            bundle2.putString(FirebaseAnalytics.Param.ITEM_NAME, "Location Reminder");

            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle2);

            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("Home Screen Content ")
                    .putContentType("Technical documentation")
                    .putContentId("Location Reminder "));
            startActivity(new Intent(MainActivity.this, LocationReminder.class));
        });

        nav_countdown.setOnClickListener(v -> {
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("Home Screen Content ")
                    .putContentType("Technical documentation")
                    .putContentId("View All Countdown"));
            startActivity(new Intent(cn, CountdownViewAllActivity.class));
            finish();
        });

        /*nav_RemoveAds.setOnClickListener(v -> {
            StyleableToast.makeText(this, "Remove Ads", Toast.LENGTH_SHORT, R.style.mytoast).show();
        });*/
        nav_UpgradeAccount.setOnClickListener(v -> {
            if (checkPlayServices()) {
                Answers.getInstance().logContentView(new ContentViewEvent());
                Answers.getInstance().logContentView(new ContentViewEvent()
                        .putContentName("Home Screen Content ")
                        .putContentType("Technical documentation")
                        .putContentId("Backup and Restore Activity "));

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "3");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, " Backup and Restore Activity ");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "BackupRestore");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


                // TODO: Finish backup in next release
                Intent intent = new Intent(getApplicationContext(), BackupActivity.class);
                startActivity(intent);
            }
        });
        nav_Settings.setOnClickListener(v -> {
            Answers.getInstance().logContentView(new ContentViewEvent());
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("Home Screen Content ")
                    .putContentType("Technical documentation")
                    .putContentId("Settings Activity"));


            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "4");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, " Settings Activity ");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "settingsactivity");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
        nav_MoreOfOurApps.setOnClickListener(v -> {
            Answers.getInstance().logContentView(new ContentViewEvent());
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("Home Screen Content ")
                    .putContentType("Technical documentation")
                    .putContentId("More of our Apps"));

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "5");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, " More of our apps ");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "more of our apps");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/apps/developer?id=CRUX"));
            startActivity(intent);
        });
        nav_AboutUs.setOnClickListener(v -> {
            Answers.getInstance().logContentView(new ContentViewEvent());
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("Home Screen Content ")
                    .putContentType("Technical documentation")
                    .putContentId("Help and Feedback"));
            startActivity(new Intent(cn, About_Us.class));
        });

        nav_feedbackOrHelp.setOnClickListener(v -> {
            String body = null;
            try {
                body = cn.getPackageManager().getPackageInfo(cn.getPackageName(), 0).versionName;
                body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                        Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                        "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
            } catch (PackageManager.NameNotFoundException e) {
            }
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{cn.getResources().getString(R.string.crux_email)});
            intent.putExtra(Intent.EXTRA_SUBJECT, cn.getResources().getString(R.string.crux_email_subject_help_feedback));
            intent.putExtra(Intent.EXTRA_TEXT, body);
            startActivity(Intent.createChooser(intent, cn.getString(R.string.choose_email_client)));
        });

        nav_RateMasterPlanner.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getResources().getString(R.string.url_rate_app)));
            startActivity(intent);
        });

        nav_ShareApp.setOnClickListener(v -> {
            String body = cn.getResources().getString(R.string.email_body_share_text) +
                    "\n"
                    + "https://play.google.com/store/apps/details?id=" + cn.getResources().getString(R.string.app_package_name);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Install Master Planner Pro App");
            intent.putExtra(Intent.EXTRA_TEXT, body);
            startActivity(Intent.createChooser(intent, "Share Master Planner Pro using"));
        });

        //---hide keyboard---//
        findViewById(R.id.container_main).setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });
    }


    private void logUser() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Crashlytics.setUserIdentifier(user.getUid());
        Crashlytics.setUserEmail(user.getEmail());
    }


    public void init() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(cn);
        mFirebaseAnalytics.setCurrentScreen(this, "MainActivity", null /* class override */);
        //======Todo --- Quick Todo Add  =====//
        ivMic = findViewById(R.id.mic);
        ivQucikTodo = findViewById(R.id.quickTodoDone);
        etTask = findViewById(R.id.add_todo_et);
        card_quick_todo = findViewById(R.id.card_quick_todo);

        //=========================Todo fab speedDialView ===========================
        //https://github.com/leinardi/FloatingActionButtonSpeedDial
        //======================Todo Mridul Vai owtar design apne ow link giya tik koroin jeno=====
        SpeedDialOverlayLayout overlayLayout = findViewById(R.id.overlay);
        speedDialView = findViewById(R.id.speedDial);
        speedDialView.setOverlayLayout(overlayLayout);
        speedDialView.inflate(R.menu.menu_speed_dial);
        speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                switch (actionItem.getId()) {
                    case R.id.fab_addnote:
                        Bundle bundle0 = new Bundle();
                        bundle0.putString(FirebaseAnalytics.Param.ITEM_ID, "0");
                        bundle0.putString(FirebaseAnalytics.Param.ITEM_NAME, "Add Note");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle0);


                        Answers.getInstance().logContentView(new ContentViewEvent()
                                .putContentName("Home Screen Content ")
                                .putContentType("Technical documentation")
                                .putContentId("Add Todo Details Activity"));
                        Intent intent = new Intent(MainActivity.this, AddTodoDetails.class);
                        startActivity(intent);
                        finish();
                        return false;
                    case R.id.fab_draw:
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Draw");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


                        Answers.getInstance().logContentView(new ContentViewEvent()
                                .putContentName("Home Screen Content ")
                                .putContentType("Technical documentation")
                                .putContentId("Paint List"));
                        startActivity(new Intent(MainActivity.this, PaintList.class));
                        return false;
                    case R.id.fab_location:
                        Bundle bundle2 = new Bundle();
                        bundle2.putString(FirebaseAnalytics.Param.ITEM_ID, "2");
                        bundle2.putString(FirebaseAnalytics.Param.ITEM_NAME, "Location Reminder");

                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle2);

                        Answers.getInstance().logContentView(new ContentViewEvent()
                                .putContentName("Home Screen Content ")
                                .putContentType("Technical documentation")
                                .putContentId("Location Reminder "));
                        startActivity(new Intent(MainActivity.this, LocationReminder.class));
                        return false;
                    case R.id.fab_master_planner:
                        Bundle bundle3 = new Bundle();
                        bundle3.putString(FirebaseAnalytics.Param.ITEM_ID, "3");
                        bundle3.putString(FirebaseAnalytics.Param.ITEM_NAME, "Master Planner");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle3);

                        Answers.getInstance().logContentView(new ContentViewEvent()
                                .putContentName("Home Screen Content ")
                                .putContentType("Technical documentation")
                                .putContentId("Master Planner"));
                        startActivity(new Intent(MainActivity.this, MasterPlannerProjectsActivity.class));
                        return false;
                    case R.id.fab_countdown:
                        Bundle bundle4 = new Bundle();
                        bundle4.putString(FirebaseAnalytics.Param.ITEM_ID, "4");
                        bundle4.putString(FirebaseAnalytics.Param.ITEM_NAME, "CountDown");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle4);

                        setCountdown();
                        Answers.getInstance().logContentView(new ContentViewEvent()
                                .putContentName("Home Screen Content ")
                                .putContentType("Technical documentation")
                                .putContentId("Home Screen CountDown"));

                        return false;
                    default:
                        return false;
                }
            }
        });


        hideIcon = false;
        invalidateOptionsMenu();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //actionBar = getSupportActionBar();
        //BackGroundNotificationBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
//                Log.d(TAG, "onDrawerSlide: triggered");
                InputMethodManager imm = (InputMethodManager) cn.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);

            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);
        mRecyclerView = headerview.findViewById(R.id.nav_rec);
        realmService = new RealmService(realm);
        mRecyclerView = headerview.findViewById(R.id.nav_rec);

        //Drawer item
        nav_MasterPlanner = headerview.findViewById(R.id.nav_master_planner);
        nav_LocationReminder = headerview.findViewById(R.id.nav_location_reminder);
//        nav_RemoveAds = headerview.findViewById(R.id.nav_remove_ads);
        nav_drawings = headerview.findViewById(R.id.nav_drawings);
        nav_countdown = headerview.findViewById(R.id.nav_countdown);
        nav_UpgradeAccount = headerview.findViewById(R.id.nav_upgrade_account);
        nav_Settings = headerview.findViewById(R.id.nav_settings);
        nav_MoreOfOurApps = headerview.findViewById(R.id.nav_more_of_our_apps);
        nav_AboutUs = headerview.findViewById(R.id.nav_about_as);

        nav_feedbackOrHelp = headerview.findViewById(R.id.nav_feedback_or_help);
        nav_RateMasterPlanner = headerview.findViewById(R.id.nav_rate_master_planner);
        nav_ShareApp = headerview.findViewById(R.id.nav_share_app);


        tvUserName = headerview.findViewById(R.id.tvUserName);

        tvUserEmail = headerview.findViewById(R.id.tvUserEmail);

    }

    private void setCountdown() {
        //todo Firebase analytics test
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "countdown");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "CountDown Click Test");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        //todo Firebase analytics test


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
                realmService.createNormalCountdown(title, countdownExpireDate);


                //  updateCountdown(realmService.getCountdownModels());
               LoadHomeFragment();
                dialog.dismiss();

                //---set Master planner project countdown--//

            } else {
                StyleableToast.makeText(this, "Please input both fields.", Toast.LENGTH_SHORT, R.style.mytoast).show();
            }
        });


        dialog.show();
    }

    private void setUpDagger2(Context cn) {
        //================Set Up Dagger 2 ==========================
        MainActivityComponent mainActivityComponent =
                DaggerMainActivityComponent
                        .builder()
                        .appComponent(App.get((Activity) cn).getAppComponent())
                        .build();
        mainActivityComponent.injectMainActivity((MainActivity) cn);
    }

    public void SetUpRecylerView() {
        LinearLayoutManager lin = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(lin);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new ItemDecoration(20, 10, 20, 0));
        pmodel = new ArrayList<>();
        List<childModel> cmodel = new ArrayList<>();
        cmodel.add(new childModel("Add New Task", R.drawable.pencil_rec, 0, 0));
//        cmodel.add(new childModel("Active Reminders", R.drawable.reminder, 0, 0));
        cmodel.add(new childModel("Locked Todo", R.drawable.lock_outline_dark, 0, 0));
        cmodel.add(new childModel("Tags", R.drawable.format_list_bulleted, 0, 0));
        pmodel.add(new parentModel("Tasks", cmodel));
        mRecyclerView.setAdapter(new RecAdapter(pmodel));
    }


    //todo ---------------Lifecycle----------//
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //realmService.CloseRealm(realm);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }


    }

    //todo ---------------Handle Fragment----------//
    public void LoadHomeFragment(){

        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        getSupportActionBar().setTitle("Master Planner");
        Runnable runnable = () -> getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_main,new HomeFragment(),"Home Fragment")
                .commit();

        if(runnable!=null){
            hand.post(runnable);
        }

    }


    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.container_main);
    }
    //todo ---------------Handle Fragment----------//


    //save quick to-do to Realm
    private void createQuickTodo(String task, String list) {
        TodoItem todoItem = new TodoItem();
        todoItem.setUserId("1");
        todoItem.setTask(task);
        todoItem.setNote("");
        //task due date
        todoItem.setDueDate(null);
        //reminder date and time
        todoItem.setRemindDate(null);
        //repeat reminder
        todoItem.setRepeatEnabled(false);
        todoItem.setRepeatType("");

        //tag
        todoItem.setList(list);
        //location
        todoItem.setLocationName("");
        todoItem.setLat(0.0);
        todoItem.setLng(0.0);
        //priority
        todoItem.setPriority(0);
        //lockTodo
        todoItem.setLocked(false);
        //auto assign values
        todoItem.setDone(false);
        todoItem.setDeleted(false);
        todoItem.setHasCountdown(false);
        todoItem.setCreatedAt(Calendar.getInstance().getTime());


        realmService.addTodo(todoItem);

        //--- widget update start -----//
        Intent intent = new Intent(this, WidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), WidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);

        //--- refresh fragment ---//
        LoadHomeFragment();

//        Fragment frg = null;
//        frg = getSupportFragmentManager().findFragmentByTag(HOME_FRAGMENT_TAG);
//        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.detach(frg);
//        ft.attach(frg);
//        ft.commit();
    }


    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000)
                        .show();
            } else {
//                Log.i("Glucosio", "This device is not supported.");
                showErrorDialogPlayServices();
            }
            return false;
        }
        return true;
    }

    private void showErrorDialogPlayServices() {
        StyleableToast.makeText(getApplicationContext(), "Master Planner app needs Play Services to enable Google Drive backup.", Toast.LENGTH_SHORT, R.style.mytoast).show();
    }

    public void promptspeech() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something!");
        startActivityForResult(i, av);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == av) {
            if (resultCode == RESULT_OK) {
                List<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                etTask.setText(result.get(0));
                StyleableToast.makeText(cn, result.get(0), Toast.LENGTH_SHORT, R.style.mytoast).show();
            }
        }
        if (requestCode == CREATE_COUNTDOWN_REQUEST) {
            if (resultCode == RESULT_OK) {


                StyleableToast.makeText(cn, "New countdown added", Toast.LENGTH_SHORT, R.style.mytoast).show();
//                Log.d(TAG, "onActivityResult: countdown_added");
            } else {
//                Log.d(TAG, "onActivityResult: countdown_canceled");
            }
        }
    }


    public static void Quick(int id) {
        if (id == 0) {

            //speedDialView.setVisibility(View.VISIBLE);
            card_quick_todo.setVisibility(View.VISIBLE);
        } else {
          //  speedDialView.setVisibility(View.GONE);
            card_quick_todo.setVisibility(View.GONE);
        }
    }


    public void DataUSage() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(mInfo);

        List<ActivityManager.RunningAppProcessInfo> listOfRunningProcess = activityManager.getRunningAppProcesses();
//        Log.d(TAG, "XXSize: " + listOfRunningProcess.size());

        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : listOfRunningProcess) {

            if (runningAppProcessInfo.uid > 1026) {
//                Log.d(TAG, "ANS " + runningAppProcessInfo.processName +
//                        " Id :" + runningAppProcessInfo.pid +
//                        " UID: " + runningAppProcessInfo.uid);
            } else {
//                Log.d(TAG, "DataUSage: " + runningAppProcessInfo.toString());
            }
        }
    }

    private void GetUserInformation() {
//        Log.d("UserInfo", "Method Called: ");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mDm = GetFirebaseRef.GetDbIns().getReference()
                .child("Users")
                .child(mUser.getUid());

        mDm.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Name = dataSnapshot.child("name").getValue().toString();
                email = dataSnapshot.child("email").getValue().toString();
//                Log.d("UserInfo", "User information " + Name + "  " + email);
                tvUserEmail.setText(email);
                tvUserName.setText(Name);


//                StyleableToast.makeText(cn, Name + "  " + email, Toast.LENGTH_SHORT, R.style.mytoast).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//                Log.d("UserInfo", databaseError.getMessage());
            }
        });
    }

}




