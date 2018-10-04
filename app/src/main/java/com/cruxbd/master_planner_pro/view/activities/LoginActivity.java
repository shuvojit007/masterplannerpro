package com.cruxbd.master_planner_pro.view.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.adapters.SlideAdapter;
import com.cruxbd.master_planner_pro.utils.GetFirebaseRef;
import com.cruxbd.master_planner_pro.view.activities.authentication.Login;
import com.cruxbd.master_planner_pro.view.activities.authentication.Register;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.Timer;
import java.util.TimerTask;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

import static com.cruxbd.master_planner_pro.utils.StaticValues.SKIP_SLIDER;


public class LoginActivity extends AppCompatActivity {
    private ProgressDialog mProgress;
    private static final String TAG = "TodoLoginActivity";
    public static int APP_REQUEST_CODE = 99;
    private ViewPager viewPager;
    private SlideAdapter slideAdapter;
    private LinearLayout layoutDot;
    private TextView[] dotstv;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private int NUM_PAGES = 5;
    private int currentPage = 0;
    private Timer timer;
    final long DELAY_MS = 500;
    final long PERIOD_MS = 1500;

    private SharedPreferences preferences;
    private LinearLayout rl_intro;
    private RelativeLayout rl_login, rl_register;
    private Context cn;
    private TextInputLayout displaname, ti_login_email, ti_login_password;
    private CircularProgressButton btnlogin;

    TextView loginforgetpassword;
    private Handler handler;
    private Runnable  Update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        cn = this;
        init();

        btnlogin.setOnClickListener(v -> {
            String email = ti_login_email.getEditText().getText().toString().trim();
            String pass = ti_login_password.getEditText().getText().toString();
//            Log.d(TAG, "Email : " + email + " Password" + pass);
            if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass)) {
                btnlogin.startAnimation();
                LoginUser(email, pass);
            }
        });

        loginforgetpassword.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(cn);
            AlertDialog.Builder builder = new AlertDialog.Builder(cn);
            final View dialogView = inflater.inflate(R.layout.forget_pass_dialog, null);
            builder.setView(dialogView);
            final AlertDialog dialog = builder.create();
            TextInputLayout forgetemail = dialogView.findViewById(R.id.forgetpassed);
            dialogView.findViewById(R.id.forgetpassbtn).setOnClickListener(vv -> {
                String email = forgetemail.getEditText().getText().toString();
//                Log.d(TAG, "setTodoLockPassword: save button clicked");
                if (!email.equals("") || !email.equals(null)) {
                    mProgress.setMessage("Please Wait");
                    mProgress.show();
                    PasswordRest(email);
                } else {
                    Toast.makeText(this, "Please add valid data", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.show();
        });

    }

    @Override
    protected void onResume() {

        super.onResume();

        timer = new Timer();
        timer.schedule(new TimerTask() { // task to be scheduled

            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
    }

    private void init() {
        mProgress = new ProgressDialog(cn);
        ti_login_email = findViewById(R.id.ti_login_email);
        ti_login_password = findViewById(R.id.ti_login_password);
        btnlogin = findViewById(R.id.login_btn);
        loginforgetpassword = findViewById(R.id.login_forget_pass);

        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = GetFirebaseRef.GetDbIns().getReference().child("Users");


        displaname = findViewById(R.id.reg_user_name);
        findViewById(R.id.btnLogin).setOnClickListener(this::login);
        findViewById(R.id.btnRegister).setOnClickListener(this::register);

        rl_intro = findViewById(R.id.rl_intro);
        rl_login = findViewById(R.id.rl_login);
        rl_register = findViewById(R.id.rl_register);

        findViewById(R.id.btnLogin).setOnClickListener(this::login);
        findViewById(R.id.btnRegister).setOnClickListener(this::register);
        findViewById(R.id.tvLogin).setOnClickListener(this::login);
        findViewById(R.id.tvSignUp).setOnClickListener(this::register);


        //==========start slider==============//
        viewPager = findViewById(R.id.viewPager);
        slideAdapter = new SlideAdapter(this);
        viewPager.setAdapter(slideAdapter);

        layoutDot = findViewById(R.id.dotLayout);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                setDotStatus(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setDotStatus(0);

        //--- auto scrolling with handler ---//

        handler = new Handler();
        Update = () -> {

            if (currentPage == NUM_PAGES) {
                currentPage = 0;
            }
            viewPager.setCurrentItem(currentPage++, true);
        };

        timer = new Timer();
        timer.schedule(new TimerTask() { // task to be scheduled

            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);

    }

    public void RegAccount(View vv) {
        String name = displaname.getEditText().getText().toString();
        if (!name.equals("")) {
            StyleableToast.makeText(cn, name, Toast.LENGTH_LONG, R.style.mytoast).show();
            if (timer != null) timer.cancel();
            startActivity(new Intent(cn, Register.class).putExtra("name", name));
        } else {
            StyleableToast.makeText(cn, "Invalid Input", Toast.LENGTH_LONG, R.style.mytoast).show();
        }
    }

    public void showIntro(View vv) {
        rl_intro.setVisibility(vv.VISIBLE);
        rl_login.setVisibility(vv.GONE);
        rl_register.setVisibility(vv.GONE);
    }

    private void login(View view) {
        rl_intro.setVisibility(View.GONE);
        rl_register.setVisibility(View.GONE);
        rl_login.setVisibility(View.VISIBLE);

    }

    private void register(View view) {
        rl_intro.setVisibility(View.GONE);
        rl_login.setVisibility(View.GONE);
        rl_register.setVisibility(View.VISIBLE);
        //TODO::for debug purpose

    }


    private void setDotStatus(int page) {
        layoutDot.removeAllViews();
        dotstv = new TextView[NUM_PAGES]; // slide number = 5
        for (int i = 0; i < dotstv.length; i++) {
            dotstv[i] = new TextView(this);
            dotstv[i].setText(Html.fromHtml("&#8226;"));
            dotstv[i].setTextSize(30);
            dotstv[i].setTextColor(getResources().getColor(R.color.slider_dots_inactive));
            layoutDot.addView(dotstv[i]);
        }

        //Set current dot active
        if (dotstv.length > 0) {
            dotstv[page].setTextColor(getResources().getColor(R.color.slider_dots_active));
        }
    }


    private void goToMyLoggedInActivity() {
        if (timer != null)
            timer.cancel();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void LoginUser(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
//                mProgress.dismiss();
                Bitmap icon = BitmapFactory.decodeResource(cn.getResources(),
                        R.drawable.btn_anim_icon);
                btnlogin.doneLoadingAnimation(Color.WHITE, icon);
                timer.cancel();
                startActivity(new Intent(LoginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();

            } else {
                btnlogin.stopAnimation();

//                Log.d("Login", "LoginUser: " + task.getException().getMessage());
                mProgress.hide();
                btnlogin.revertAnimation();
                StyleableToast.makeText(cn, "Cannot Sign in. Please check the form and try again. ", Toast.LENGTH_LONG, R.style.mytoast).show();
            }

        });

    }


    //todo user password reset
    private void PasswordRest(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mProgress.dismiss();
                        StyleableToast.makeText(cn, "Please check your email.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                    }else {
                        mProgress.dismiss();
                        StyleableToast.makeText(cn, "Email address not found.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            if(timer!=null) timer.cancel();
            startActivity(new Intent(cn, MainActivity.class));
            finish();
        }
//        else {
//            //mUserDatabase.child("online").setValue(true);
//        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
