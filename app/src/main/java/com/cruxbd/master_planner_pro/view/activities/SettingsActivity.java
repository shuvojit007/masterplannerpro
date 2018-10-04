package com.cruxbd.master_planner_pro.view.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.model.realm_model.TodoLockModel;
import com.cruxbd.master_planner_pro.utils.GetFirebaseRef;
import com.cruxbd.master_planner_pro.utils.NetworkUtils;
import com.cruxbd.master_planner_pro.utils.PrefUtils;
import com.cruxbd.master_planner_pro.utils.alarm_service.AlarmManagerUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.Objects;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import io.realm.Realm;

public class SettingsActivity extends AppCompatPreferenceActivity {

    private static final String TAG = "MySettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();


        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }


    public static class MainPreferenceFragment extends PreferenceFragment {

        public static CheckBoxPreference pin;

        Realm realm = Realm.getDefaultInstance();


        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference db = GetFirebaseRef.GetDbIns().getReference("Users").child(mUser.getUid());
        private boolean pin_status = false;

        Boolean todo_reminder_pref_value;
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_headers);


            //todo reminder
//            Preference todo_reminder_pref = findPreference(getString(R.string.key_todo_reminder));
//            todo_reminder_pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//
//                    Toast.makeText(preference.getContext(), "Todo reminder : "+ newValue.toString() , Toast.LENGTH_SHORT).show();
//
//                    return false;
//                }
//            });

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            todo_reminder_pref_value = sharedPrefs.getBoolean(getString(R.string.key_todo_reminder), false);

        //    Toast.makeText(getActivity(), "Todo reminder : " + todo_reminder_pref_value, Toast.LENGTH_SHORT).show();
//            Preference todo_reminder_pref = findPreference(getString(R.string.key_todo_reminder));
//            todo_reminder_pref.setOnPreferenceClickListener(preference -> {
//
//                if(todo_reminder_pref_value){
//                    Toast.makeText(getActivity(), "Todo reminder turned off", Toast.LENGTH_SHORT).show();
//                    todo_reminder_pref_value = false;
//                }else{
//                    Toast.makeText(getActivity(), "Todo reminder turned on", Toast.LENGTH_SHORT).show();
//                    todo_reminder_pref_value = true;
//                }
//
//                return true;
//
//            });

            SwitchPreference switchPreferenceTodo = (SwitchPreference) findPreference(getString(R.string.key_todo_reminder));
            switchPreferenceTodo.setOnPreferenceChangeListener((preference, newValue) -> {

                if(newValue.toString().equals("true")){
                    //turn off all reminder
                    AlarmManagerUtil alarmManagerUtil = new AlarmManagerUtil(getActivity().getApplicationContext());
                    alarmManagerUtil.initializeTodoAlarm();
                    alarmManagerUtil.enableBootReceiver();
                    Toast.makeText(getActivity(), "Todo Reminder enabled", Toast.LENGTH_SHORT).show();

                }else{
                    //turn on all reminder
                    AlarmManagerUtil alarmManagerUtil = new AlarmManagerUtil(getActivity().getApplicationContext());
                    alarmManagerUtil.cancelAllTodoAlarm();
                    alarmManagerUtil.cancelAllPlannerAlarm();
//                    alarmManagerUtil.disableBootReceiver();
                    Toast.makeText(getActivity(), "Todo Reminder disabled", Toast.LENGTH_SHORT).show();
                }

                return true;
            });


            SwitchPreference switchPreferencePlanner = (SwitchPreference) findPreference(getString(R.string.key_planner_reminder));
            switchPreferencePlanner.setOnPreferenceChangeListener((preference, newValue) -> {

                if(newValue.toString().equals("true")){
                    //turn off all reminder
                    AlarmManagerUtil alarmManagerUtil = new AlarmManagerUtil(getActivity().getApplicationContext());
                    alarmManagerUtil.initializePlannerAlarm();
                    alarmManagerUtil.enableBootReceiver();
                    Toast.makeText(getActivity(), "Todo Reminder enabled", Toast.LENGTH_SHORT).show();

                }else{
                    //turn on all reminder
                    AlarmManagerUtil alarmManagerUtil = new AlarmManagerUtil(getActivity().getApplicationContext());
                    alarmManagerUtil.cancelAllPlannerAlarm();
//                    alarmManagerUtil.disableBootReceiver();
                    Toast.makeText(getActivity(), "Todo Reminder disabled", Toast.LENGTH_SHORT).show();
                }

                return true;
            });


            // feedback preference click listener
            Preference myPref = findPreference(getString(R.string.key_submit_feedback));
            myPref.setOnPreferenceClickListener(preference -> {
                sendFeedback(getActivity());
                return true;
            });

            Preference signout = findPreference(getString(R.string.key_sign_out));
            signout.setOnPreferenceClickListener(preference -> {



                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(preference.getContext(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(preference.getContext());
                }
                builder.setTitle("Warning!")
                        .setMessage("Before Logout Please Backup Your All Data in Google Drive. All App Data Will be Deleted after Pressing “OK”")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            // continue with delete

                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            mAuth.signOut();

                            realm.executeTransaction(realm1 -> realm1.deleteAll());
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                            getActivity().finish();


                        })
                        .setNegativeButton(android.R.string.no, (dialog, which) -> {
                            // do nothing
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


                return true;
            });


            //todo update Pass
            Preference updatepass = findPreference(getString(R.string.key_update_password));
            updatepass.setOnPreferenceClickListener(preference -> {
                Log.d(TAG, "onCreate: todo pref: clicked");
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final View dialogView = inflater.inflate(R.layout.update_pass_dialog, null);
                builder.setView(dialogView);
                final AlertDialog dialog = builder.create();
                TextInputLayout mLoginEmail = dialogView.findViewById(R.id.pin_email);
                TextInputLayout mLoginPass = dialogView.findViewById(R.id.pin_pass);

                CircularProgressButton btnlogin = dialogView.findViewById(R.id.pin_login);
                btnlogin.setOnClickListener(v -> {
                    String email = Objects.requireNonNull(mLoginEmail.getEditText()).getText().toString();
                    String pass = Objects.requireNonNull(mLoginPass.getEditText()).getText().toString();

                    if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass)) {
                        btnlogin.startAnimation();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(email, pass);

                        user.reauthenticate(credential)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Bitmap icon = BitmapFactory.decodeResource(dialogView.getResources(),
                                                R.drawable.btn_anim_icon);
                                        btnlogin.doneLoadingAnimation(Color.WHITE, icon);


                                        ChangePass();
                                        dialog.dismiss();

                                    } else {
                                        startActivity(new Intent(dialogView.getContext(), LoginActivity.class));
                                        StyleableToast.makeText(dialogView.getContext(), "Something went wrong" + task.getException().getMessage(), Toast.LENGTH_SHORT, R.style.mytoast).show();
                                        dialog.dismiss();
                                        getActivity().finish();
                                    }
                                });
                    } else {
                        StyleableToast.makeText(dialogView.getContext(), "Please enter valid data", Toast.LENGTH_SHORT, R.style.mytoast).show();
                    }

                });


                dialog.show();
                return true;
            });


            // send recommendation of app preference click listener
            Preference recommendPrefs = findPreference(getString(R.string.key_recommend_app));
            recommendPrefs.setOnPreferenceClickListener(preference -> {
                sendRecommendation(getActivity());
                return true;
            });


            //todo lock pref
            Preference todolocpref = findPreference("key_todo_lock");
            TodoLockModel lockModel = realm.where(TodoLockModel.class).findFirst();
            pin_status = false;
            if (lockModel != null) {
                Log.d(TAG, "onCreate: todo lock pref: user already set pin....,show change pin...");
                //have pin
                todolocpref.setTitle("Change pin");
                todolocpref.setSummary("Change your to-do lock pin.");
                pin_status = true;
            } else {
                Log.d(TAG, "onCreate: todo lock pref: pin not set... show create pin dialog");
                pin_status = false;
            }

            todolocpref.setOnPreferenceClickListener(preference -> {
                Log.d(TAG, "onCreate: todo perf: lock pref clicked");
                if (pin_status) {
                    //have pin
                    changeTodoPin();
                } else {
                    // pin not set, so show pin set dialog
                    setTodoLockPassword();
                }
//                realm.close();
                return true;
            });

            //  key_todo_reset
            Preference resetlocktodo = findPreference("key_todo_reset");
            resetlocktodo.setOnPreferenceClickListener(pref -> {


                if (pin_status) {
                    if (!NetworkUtils.haveNetworkConnection(getActivity())) {
                        StyleableToast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT, R.style.mytoast).show();
                    } else {
                        String msg = "Master Planner Pro" +
                                "Your reset password in " +
                                lockModel.getPin();

                        BackgroundMail.newBuilder(getActivity())
                                .withUsername("app.cruxbd@gmail.com")
                                .withPassword("pythoncru123crux")
                                .withMailto(mUser.getEmail())
                                .withType(BackgroundMail.TYPE_PLAIN)
                                .withSubject("Reset Todo Lock Password for master planner")
                                .withBody(msg)
                                .withOnSuccessCallback(() -> {
                                    StyleableToast.makeText(getActivity(), "Reset Code successfully sent", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                })
                                .withOnFailCallback(() -> {
                                    StyleableToast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                })
                                .send();
                    }

                } else {
                    StyleableToast.makeText(getActivity(), "To reset password you have to set password first", Toast.LENGTH_SHORT, R.style.mytoast).show();
                }
                return true;
            });

        }


        private void changeTodoPin() {
            Log.d(TAG, "changeTodoPin: ");
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final View dialogView = inflater.inflate(R.layout.pin_change_dailog, null);
            builder.setView(dialogView);
            final AlertDialog dialog = builder.create();


            TextInputLayout todoOldPin = dialogView.findViewById(R.id.todo_old_pin);
            TextInputLayout todoNewPin = dialogView.findViewById(R.id.todo_pin);

            dialogView.findViewById(R.id.btntodosave).setOnClickListener(v -> {
                String old_pin = todoOldPin.getEditText().getText().toString();
                String pin = todoNewPin.getEditText().getText().toString();
                Log.d(TAG, "changeTodoPin: " + pin);

                if (pin != null && old_pin != null) {
                    if (pin.length() == 4 && old_pin.length() == 4) {

                        realm.executeTransaction(realm1 -> {

                            TodoLockModel lockModel = realm1.where(TodoLockModel.class).findFirst();

                            if (lockModel != null) {

                                if (old_pin.equals(lockModel.getPin())) {

                                    lockModel.setPin(pin);
                                    realm1.insertOrUpdate(lockModel);
                                    StyleableToast.makeText(v.getContext(), "Pin updated successfully.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                    dialog.dismiss();
                                } else {
                                    StyleableToast.makeText(v.getContext(), "Pin not matched", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                }

                            } else {
                                StyleableToast.makeText(v.getContext(), "No pin set yet. Set a pin first.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                            }
                        });

                    } else {
                        StyleableToast.makeText(v.getContext(), "Pin must be 4 digit", Toast.LENGTH_SHORT, R.style.mytoast).show();
                    }
                }
            });
            dialog.show();

//            dialog.setOnDismissListener(dialog1 -> realm.close());

        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            realm.close();
        }

        private void setTodoLockPassword() {
//            Log.d(TAG, "setTodoLockPassword: ");
////            LayoutInflater inflater = LayoutInflater.from(getActivity());
////            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
////            final View dialogView = inflater.inflate(R.layout.pin_dailog, null);
////            builder.setView(dialogView);
////            final AlertDialog dialog = builder.create();
////            TextInputLayout todopin = dialogView.findViewById(R.id.todo_pin);
////            TextInputLayout todopin_retype = dialogView.findViewById(R.id.todo_pin_retype);
////
////
////            dialogView.findViewById(R.id.btntodosave).setOnClickListener(v -> {
////                String pin = todopin.getEditText().getText().toString();
////                String pin_retype = todopin.getEditText().getText().toString();
////                Log.d(TAG, "setTodoLockPassword: save button clicked");
////                if ((!pin.equals("") || !pin.equals(null)) && ((!pin_retype.equals("") || !pin_retype.equals(null)))) {
////                    Log.d(TAG, "setTodoLockPassword: pin: " + pin);
////                    if(pin.length()<4){
////
////                        StyleableToast.makeText(v.getContext(), "Pin must be 4 digit", Toast.LENGTH_SHORT, R.style.mytoast).show();
////                    }else if(pin.equals(pin_retype)){
////                        realm.executeTransaction(realm1 -> {
////                            TodoLockModel lockModel = new TodoLockModel(pin);
////                            realm1.insert(lockModel);
////                        });
////
////                        findPreference("key_todo_lock").setTitle("Change pin");
////                        findPreference("key_todo_lock").setSummary("Change your to-do lock pin.");
////
////                        dialog.dismiss();
////                    }
////
////                }
////            });
////            dialog.show();

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final View dialogView = inflater.inflate(R.layout.pin_dailog, null);
            builder.setView(dialogView);
            final AlertDialog dialog = builder.create();
            TextInputLayout todopin = dialogView.findViewById(R.id.todo_pin);
            TextInputLayout todopin_retype = dialogView.findViewById(R.id.todo_pin_retype);


            dialogView.findViewById(R.id.btntodosave).setOnClickListener(v -> {
                String pin = todopin.getEditText().getText().toString();
                String pin_retype = todopin_retype.getEditText().getText().toString();
                Log.d(TAG, "setTodoLockPassword: save button clicked");


                if (!TextUtils.isEmpty(pin) && !TextUtils.isEmpty(pin_retype)) {
                    if (pin.length() == 4 && pin.equals(pin_retype)) {
                        //valid pin
                        realm.executeTransaction(realm1 -> {
                            TodoLockModel lockModel = new TodoLockModel(pin);
                            realm1.insert(lockModel);
                        });
                        dialog.dismiss();
                       // startActivity(new Intent(v.getContext(), LockTodoListActivity.class));
                        //getActivity().finish();

                        findPreference("key_todo_lock").setTitle("Change pin");
                        findPreference("key_todo_lock").setSummary("Change your to-do lock pin.");
                        pin_status = true;
                        StyleableToast.makeText(v.getContext(), "pin saved", Toast.LENGTH_SHORT, R.style.mytoast).show();


                    }else if(pin.length() == 4 && !pin.equals(pin_retype)){

                        StyleableToast.makeText(v.getContext(), "Retype pin not matched", Toast.LENGTH_SHORT, R.style.mytoast).show();
                    }
                    else {
                        StyleableToast.makeText(v.getContext(), "The pin must be 4 digits", Toast.LENGTH_SHORT, R.style.mytoast).show();
                    }
                } else {
                    StyleableToast.makeText(v.getContext(), "Please input in both field", Toast.LENGTH_SHORT, R.style.mytoast).show();
                }

//                Log.d(TAG, "setTodoLockPassword: pin: " + pin);

            });
            dialog.show();
        }

        private void ChangePass() {
            Log.d(TAG, "ChangePass: ");
            LayoutInflater inflater2 = LayoutInflater.from(getActivity());
            AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
            final View dialogView2 = inflater2.inflate(R.layout.change_pass, null);
            builder2.setView(dialogView2);
            final AlertDialog dialog2 = builder2.create();
            dialog2.setCancelable(false);
            TextInputLayout newPass = dialogView2.findViewById(R.id.pass_enter);
            TextInputLayout confirmPass = dialogView2.findViewById(R.id.pass_confirm);

            CircularProgressButton chngbtn = dialogView2.findViewById(R.id.pass_change);
            chngbtn.setOnClickListener(v -> {
                String pass = Objects.requireNonNull(newPass.getEditText()).getText().toString();
                String confPass = Objects.requireNonNull(confirmPass.getEditText()).getText().toString();

                if ((!TextUtils.isEmpty(pass)) && (!TextUtils.isEmpty(confPass))) {
                    if (pass.equals(confPass) && pass.length() >= 6) {
                        //pass matched
                        chngbtn.startAnimation();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        user.updatePassword(pass)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Bitmap icon = BitmapFactory.decodeResource(dialogView2.getResources(),
                                                R.drawable.btn_anim_icon);
                                        chngbtn.doneLoadingAnimation(Color.WHITE, icon);
                                        startActivity(new Intent(dialogView2.getContext(), LoginActivity.class));
                                        getActivity().finish();
                                        StyleableToast.makeText(v.getContext(), "Password changed successfully.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                        // dialog2.dismiss();
                                    } else {
                                        StyleableToast.makeText(dialogView2.getContext(), "Something Wrong", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                        startActivity(new Intent(dialogView2.getContext(), LoginActivity.class));
                                        getActivity().finish();
                                        dialog2.dismiss();
                                    }
                                });

                    } else if (pass.equals(confPass) && pass.length() < 6) {

                        StyleableToast.makeText(v.getContext(), "Weak Password!", Toast.LENGTH_SHORT, R.style.mytoast).show();
                    } else if(!pass.equals(confPass)){
                        StyleableToast.makeText(v.getContext(), "The Retype password does not match", Toast.LENGTH_SHORT, R.style.mytoast).show();
                    }

                } else {
                    StyleableToast.makeText(dialogView2.getContext(), "Please enter a valid data", Toast.LENGTH_SHORT, R.style.mytoast).show();
                }
            });
            dialog2.show();
        }
    }


    /**
     * Email client intent to send support mail
     * Appends the necessary device information to email body
     * useful when providing support
     */
    public static void sendFeedback(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getResources().getString(R.string.crux_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.crux_email_subject_help_feedback));
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }

    public static void sendRecommendation(Context context) {
        String body = context.getResources().getString(R.string.email_body_share_text) +
                "\n"
                + "https://play.google.com/store/apps/details?id=" + context.getResources().getString(R.string.app_package_name);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.email_subject_install_master_planner_app));
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }
}