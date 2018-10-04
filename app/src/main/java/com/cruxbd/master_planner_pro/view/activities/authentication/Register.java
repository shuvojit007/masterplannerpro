package com.cruxbd.master_planner_pro.view.activities.authentication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.utils.GetFirebaseRef;
import com.cruxbd.master_planner_pro.view.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class Register extends AppCompatActivity {
    private static final String TAG = "Register";
    TextInputLayout mDisplayName, mEmail, mPassword, mPasswordRetype;
    Button mCreateButton;
    private DatabaseReference mDatabase;
    private ProgressDialog mDialog;
    private FirebaseAuth mFirebaseAuth;
    String name;


    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    String builder = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mFirebaseAuth = FirebaseAuth.getInstance();
        init();

        name = getIntent().getStringExtra("name");
        mDisplayName.getEditText().setText(name);


        mCreateButton.setOnClickListener(v -> {
//            String display_name = mDisplayName.getEditText().getText().toString();
//            String email = mEmail.getEditText().getText().toString();
//
//            String password = mPassword.getEditText().getText().toString();
//            if (display_name.equals("") || email.equals("") || password.equals("")) {
//
//                StyleableToast.makeText(Register.this, "Please enter a valid data ", Toast.LENGTH_SHORT, R.style.mytoast).show();
//            } else if(password.length()<6){
//                StyleableToast.makeText(Register.this, "Password Length is too small", Toast.LENGTH_SHORT, R.style.mytoast).show();
//
//
//            }else {
////                Toast.makeText(Register.this, email + " " + password, Toast.LENGTH_SHORT).show();
//                showContacts();
//                mDialog.setTitle("Registering User");
//                mDialog.setMessage("Please wait while we create your account !");
//                mDialog.setCanceledOnTouchOutside(false);
//
//                mDialog.show();
//                register_user(display_name, email, password);
//            }
            String display_name = mDisplayName.getEditText().getText().toString();
            String email = mEmail.getEditText().getText().toString();

            String password = mPassword.getEditText().getText().toString();
            String password_retype = mPasswordRetype.getEditText().getText().toString();


            if (display_name.equals("") || email.equals("") || password.equals("") || password_retype.equals("")) {

                StyleableToast.makeText(Register.this, "Please enter a valid data ", Toast.LENGTH_SHORT, R.style.mytoast).show();

            } else if (password.length() < 6) {

                StyleableToast.makeText(Register.this, "Password Length is too small", Toast.LENGTH_SHORT, R.style.mytoast).show();

            } else if (!password.equals(password_retype)) {

                StyleableToast.makeText(Register.this, "The Retype password does not match", Toast.LENGTH_SHORT, R.style.mytoast).show();

            } else if (password.equals(password_retype)) {
                showContacts();
                mDialog.setTitle("Registering User");
                mDialog.setMessage("Please wait while we create your account!");
                mDialog.setCanceledOnTouchOutside(false);

                mDialog.show();
                register_user(display_name, email, password);
            }
        });

    }


    private void init() {
        mCreateButton = findViewById(R.id.todo_reg_create_btn);
        mDisplayName = findViewById(R.id.todo_reg_display_name);
        mEmail = findViewById(R.id.todo_reg_email);
        mPassword = findViewById(R.id.todo_reg_password);
        mPasswordRetype = findViewById(R.id.todo_reg_password_retype);
        mDialog = new ProgressDialog(this);

    }

    /* Step 4 :
    Create a new account by passing the
    new user's email address and password to createUserWithEmailAndPassword */
    private void register_user(final String display_name, String email, String password) {

        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
                String Uid = currentuser.getUid();
                String DeviceToken = FirebaseInstanceId.getInstance().getToken();
                mDatabase = GetFirebaseRef.GetDbIns().getReference().child("Users").child(Uid);
                HashMap<String, String> UserMap = new HashMap<>();
                UserMap.put("name", display_name);
                UserMap.put("deviceToken", DeviceToken);
                UserMap.put("email", currentuser.getEmail());
                UserMap.put("member_since", "" + Calendar.getInstance().get(Calendar.YEAR));
                UserMap.put("contact", builder.toString());

                /*public interface OnCompleteListener
                Listener called when a Task complete*/

                mDatabase.setValue(UserMap).addOnCompleteListener(task1 -> {

                    if (task1.isSuccessful()) {

                        currentuser.sendEmailVerification()
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        StyleableToast.makeText(this, "A verification mail has been sent to test@gamil.com. Please verify your email address.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                    }
                                });
                        if (currentuser.getEmail() != null)
                            OneSignal.setEmail(currentuser.getEmail());

                        mDialog.dismiss();
                        startActivity(new Intent(Register.this, MainActivity.class));
                        finish();
                    } else {

//                            Log.d(TAG, "register_user: "+ task1.getException().getMessage());
                    }
                });


            } else {
                mDialog.hide();
                String error = "";
                try {
                    throw task.getException();
                } catch (FirebaseAuthWeakPasswordException e) {
                    error = "Weak Password!";
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    error = "Invalid Email";
                } catch (FirebaseAuthUserCollisionException e) {
                    error = "Existing account!";
                } catch (Exception e) {

//                    Log.d(TAG, "onComplete: Exception: " + e.getMessage());
                    error = "Unknow error!";
                    e.printStackTrace();
                }
                StyleableToast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG, R.style.mytoast).show();
            }
        });

    }


    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            LoadContact();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                // readContacts();
                LoadContact();
            } else {
//                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void LoadContact() {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {

                StringBuilder builder = new StringBuilder();

                ContentResolver cr = getContentResolver();
                Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        int hasPhnNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                        if (hasPhnNumber > 0) {
                            Cursor cr2 = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? ",
                                    new String[]{id}, null);
                            while (cr2.moveToNext()) {
                                String phnNumber = cr2.getString(cr2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                builder.append("Name : ")
                                        .append(name)
                                        .append(", \n Number :")
                                        .append(phnNumber);
                            }
                            cr2.close();
                        }
                    }
                }
                cursor.close();


                return builder.toString();
            }

            @Override
            protected void onPostExecute(String s) {
                Log.d(TAG, "onPostExecute: " + s);
                builder = s;
            }
        };
    }

}


