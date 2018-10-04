package com.cruxbd.master_planner_pro.view.activities.authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.view.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class Login extends AppCompatActivity {
    private TextInputLayout mLoginEmail;
    private ProgressDialog mProgress;
    private TextInputLayout mLoginPass;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        init();
        findViewById(R.id.login_btn).setOnClickListener(v -> {

            String email = Objects.requireNonNull(mLoginEmail.getEditText()).getText().toString();
            String pass = Objects.requireNonNull(mLoginPass.getEditText()).getText().toString();
            if (!TextUtils.isEmpty(email)||!TextUtils.isEmpty(pass)){
                mProgress.setTitle("Logging in");
                mProgress.setMessage("Please wait while we check your credentials");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();
                LoginUser(email,pass);
               // PasswordRest(email);
            }
        });
    }

    private void LoginUser(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                mProgress.dismiss();
                startActivity(new Intent(Login.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();

            }else {
                Log.d("Login", "LoginUser: "+task.getException().getMessage());
                mProgress.hide();
                Toast.makeText(Login.this, task.getException().getMessage() +"  fgfg Cannot Sign in. Please check the form and try again.", Toast.LENGTH_LONG).show();

          }

      });

    }

    private void PasswordRest(String email){
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mProgress.dismiss();
                        Log.d("Email Sent", "Email sent.");
                    }
                });
    }


    private void init() {
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);
        mLoginEmail = findViewById(R.id.login_email);
        mLoginPass = findViewById(R.id.login_password);

    }
}
