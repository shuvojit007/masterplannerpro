package com.cruxbd.master_planner_pro.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class PrefUtils {


    /**
     * Storing API Key in shared preferences to
     * add it in header part of every retrofit request
     */
    public PrefUtils() {
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("APP_PREF", Context.MODE_PRIVATE);
    }

    public static void storeApiKey(Context context, String apiKey) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("API_KEY", apiKey);
        editor.commit();
    }

    public static String getApiKey(Context context) {
        return getSharedPreferences(context).getString("API_KEY", null);
    }


    public static void storePin(Context context, String pin, DatabaseReference db) {
        Map<String, String> map = new HashMap<>();
        map.put("todopass", pin);
        db.setValue(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SharedPreferences.Editor editor = getSharedPreferences(context).edit();
                editor.putString("PIN_lOCK", pin);
                editor.commit();
                Toast.makeText(context, "Suecessfully Store Password", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context, "Suecessfully Store Password", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static String getPinLock(Context context) {

        return getSharedPreferences(context).getString("PIN_lOCK", "nothing");

    }
}
