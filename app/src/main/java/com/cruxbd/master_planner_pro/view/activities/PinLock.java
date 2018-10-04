package com.cruxbd.master_planner_pro.view.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.model.realm_model.TodoLockModel;
import com.cruxbd.master_planner_pro.utils.GetFirebaseRef;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import io.realm.Realm;

public class PinLock extends AppCompatActivity {
    IndicatorDots mIndicatorDots;
    PinLockView mPinLockView;
    String pass = "";
    private String TAG = "Pin_Lock";
    private Context cn;
    SharedPreferences sp;
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference db = GetFirebaseRef.GetDbIns().getReference("Users").child(mUser.getUid());
    Realm realm;
    TodoLockModel lockModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_lock);
        cn = this;

        realm = Realm.getDefaultInstance();

        lockModel = realm.where(TodoLockModel.class).findFirst();

        if (lockModel != null) {
            pass = lockModel.getPin();
        } else {
            Log.d(TAG, "setTodoLockPassword: ");

            LayoutInflater inflater = getLayoutInflater();
            AlertDialog.Builder builder = new AlertDialog.Builder(cn);
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
                        startActivity(new Intent(cn, LockTodoListActivity.class));
                        finish();

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
//            dialog.setCancelable(false);
            dialog.show();


            StyleableToast.makeText(cn, "You have not set your pin yet.", Toast.LENGTH_SHORT, R.style.mytoast).show();
            pass = "";
        }

        //pass = PrefUtils.getPinLock(this);
        Log.d(TAG, "onCreate: " + pass);

        init();


    }

    private void init() {
        mPinLockView = findViewById(R.id.pin_lock_view);
        mIndicatorDots = findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);


        PinLockListener mPinLockListener = new PinLockListener() {
            @Override
            public void onComplete(String pin) {
                Log.d(TAG, "Pin complete: " + pin);
                if(pass.length()<4){
                    StyleableToast.makeText(cn, "You have not set your pin yet.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                }
                else if (pass.equals(pin)) {
                    startActivity(new Intent(cn, LockTodoListActivity.class));
                    finish();
                } else {
                    Toast.makeText(cn, "Pin not matched", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onEmpty() {
                Log.d(TAG, "Pin empty");
            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {
                Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
            }
        };
        mPinLockView.setPinLockListener(mPinLockListener);
    }
}
