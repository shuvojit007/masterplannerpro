package com.cruxbd.master_planner_pro.utils.alarm_service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.cruxbd.master_planner_pro.model.realm_model.CardItems;
import com.cruxbd.master_planner_pro.model.realm_model.CardItemsFields;
import com.cruxbd.master_planner_pro.model.realm_model.TodoItem;
import com.cruxbd.master_planner_pro.model.realm_model.TodoItemFields;
import com.cruxbd.master_planner_pro.model.realm_model.TodoReminder;
import com.cruxbd.master_planner_pro.model.realm_model.TodoReminderFields;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.Sort;

import static com.cruxbd.master_planner_pro.utils.StaticValues.ALARM;
import static com.cruxbd.master_planner_pro.utils.StaticValues.DESCRIPTION;
import static com.cruxbd.master_planner_pro.utils.StaticValues.NOTIFICATION_ID;
import static com.cruxbd.master_planner_pro.utils.StaticValues.REPEAT_DAILY;
import static com.cruxbd.master_planner_pro.utils.StaticValues.REPEAT_WEEKLY;
import static com.cruxbd.master_planner_pro.utils.StaticValues.TITLE;
import static com.cruxbd.master_planner_pro.utils.StaticValues.TODO_ID;
/*
public class AlarmManagerUtil {
    private static final String TAG = "MyAlarmManagerUtil";
    Realm realm;
    AlarmManager alarmManager;
    PendingIntent alarmPendingIntent;
    Context cn;

    public AlarmManagerUtil(Context context) {
        this.cn = context;
    }

    public void initializeAlarm() {

        openRealm();

        //--- get active to-do alarms ---//
        List<TodoItem> todos = getAllTodoAlarm();

        //--- get active planner alarms ---//
        List<TodoReminder> reminders = getAllPlannerAlarm();

        //-- initialize alarm manager ---//
        alarmManager = (AlarmManager) cn.getSystemService(cn.ALARM_SERVICE);

        //--- set all to-do alarm one by one ---//
        for (TodoItem todo : todos) {
            setAlarm(todo.getId(), "To-do Alert",todo.getAlarm_req_code(), todo.getTask(),
                    todo.getRemindDate(), todo.getRepeatEnabled(), todo.getRepeatType());
        }

        //--- set all planner alarm one by one ---//
        for(TodoReminder reminder : reminders){
            setAlarm(reminder.getId(), "Planner Alert" ,reminder.getAlarm_req_code(), "Planner",
                    reminder.getRemindDate(), reminder.getRepeatEnabled(), reminder.getRepeatType());
        }

        closeRealm();
    }

    private void setAlarm(String id, String title, int req_code, String task, Date alarmTime,
                          boolean repeat_enabled, String repeat_type) {
        Log.d(TAG, "createAlarm: seting your alarm... ");
        Log.d(TAG, "createAlarm: _id: " + id);

        Intent intent = new Intent(cn, AlarmReceiver.class);

        Bundle bundle = new Bundle();
        bundle.putInt(NOTIFICATION_ID, req_code);
        bundle.putString(TODO_ID, id);
        bundle.putString(TITLE, title);
        bundle.putString(DESCRIPTION, task);
        bundle.putString(ALARM, "on");
        intent.putExtras(bundle);

        alarmPendingIntent = PendingIntent.getBroadcast(cn, req_code,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the alarm
        Calendar calendar = Calendar.getInstance();
        // calendar.setTime(countdownExpireDate);
        calendar.setTimeInMillis(alarmTime.getTime());
        Log.d(TAG, "createAlarm: Alarm Time: " + calendar.get(Calendar.HOUR_OF_DAY) +
                ":" + calendar.get(Calendar.MINUTE) +
                ":" + calendar.get(Calendar.SECOND));

        //====== for repeat alarm ====//
        if (repeat_enabled) {
            switch (repeat_type) {
                case REPEAT_DAILY:
                    Log.d(TAG, "setAlarm: set daily repeating alarm");
                    //1000 * 60 * 60 * 24
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            1000 * 60 * 60 * 24, alarmPendingIntent);

                    break;
                case REPEAT_WEEKLY:
                    Log.d(TAG, "setAlarm: set weekly  repeating alarm");
                    //1000 * 60 * 60 * 24 * 7
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
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
        } else {
            //===for onetime alarm ===//
            Log.d(TAG, "createAlarm: set one time alarm at " + calendar.getTimeInMillis());

            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(), alarmPendingIntent);
            } else if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmPendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmPendingIntent);
            }

//            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                    alarmPendingIntent);
        }
    }

    private List<TodoReminder> getAllPlannerAlarm() {
        return realm.where(TodoReminder.class)
                .greaterThanOrEqualTo(TodoReminderFields.REMIND_DATE, new Date(System.currentTimeMillis()))
                .equalTo(TodoReminderFields.REMINDER_ENABLED, true)
                .sort(TodoReminderFields.REMIND_DATE, Sort.ASCENDING)
                .findAll();
    }

    private List<TodoItem> getAllTodoAlarm() {
        return realm.where(com.cruxbd.master_planner_pro.model.realm_model.TodoItem.class)
                .greaterThanOrEqualTo(TodoItemFields.REMIND_DATE, new Date(System.currentTimeMillis()))
                .equalTo(TodoItemFields.ALARM_STATUS, true)
                .sort(TodoItemFields.REMIND_DATE, Sort.ASCENDING)
                .findAll();
    }

    private void openRealm() {
        realm = Realm.getDefaultInstance();
    }

    private void closeRealm() {
        realm.close();
    }

    public void cancelTodoAlarm(int req_code) {

        alarmManager = (AlarmManager) cn.getSystemService(cn.ALARM_SERVICE);
        Intent intent = new Intent(cn, AlarmReceiver.class);
        alarmPendingIntent = PendingIntent.getBroadcast(cn, req_code,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(alarmPendingIntent);

//        openRealm();
//        realm.executeTransaction(realm1 -> Objects.requireNonNull(realm1.where(TodoItem.class)
//                .equalTo(TodoItemFields.ALARM_REQ_CODE, req_code).findFirst()).setAlarm_status(false));

    }

    public void cancelAllTodoAlarm(){

        //Todo::**** 1. If the alarm has been set, cancel it.

        //--- get active to-do alarms ---//
        List<TodoItem> todos = getAllTodoAlarm();


        alarmManager = (AlarmManager) cn.getSystemService(cn.ALARM_SERVICE);

        //--- cancel all to-do alarm one by one ---//
        for (TodoItem todo : todos) {
            cancelTodoAlarm(todo.getAlarm_req_code());
        }
    }

    public void cancelAllPlannerAlarm(){
        //--- get active planner alarms ---//
        List<TodoReminder> reminders = getAllPlannerAlarm();

        alarmManager = (AlarmManager) cn.getSystemService(cn.ALARM_SERVICE);

        //--- cancel all planner alarm one by one ---//
        for(TodoReminder reminder : reminders){
            cancelPlannerAlarm(reminder.getAlarm_req_code());
        }
    }

    public void cancelPlannerAlarm(int req_code){
        alarmManager = (AlarmManager) cn.getSystemService(cn.ALARM_SERVICE);
        Intent intent = new Intent(cn, AlarmReceiver.class);
        alarmPendingIntent = PendingIntent.getBroadcast(cn, req_code,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(alarmPendingIntent);

//        openRealm();
//        realm.executeTransaction(realm1 -> Objects.requireNonNull(realm1.where(TodoReminder.class)
//                .equalTo(TodoReminderFields.ID, req_code)
//                .findFirst()).setAlarm_status(false));
    }

    public void enableBootReceiver(){
        ComponentName receiver = new ComponentName(cn, BootReceiver.class);
        PackageManager pm = cn.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void disableBootReceiver(){
        ComponentName receiver = new ComponentName(cn, BootReceiver.class);
        PackageManager pm = cn.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}*/



public class AlarmManagerUtil {
    private static final String TAG = "MyAlarmManagerUtil";
    Realm realm;
    AlarmManager alarmManager;
    PendingIntent alarmPendingIntent;
    Context cn;

    public AlarmManagerUtil(Context context) {
        this.cn = context;
    }

    public void initializeAlarm() {

        openRealm();

        //--- get active to-do alarms ---//
        List<TodoItem> todos = getAllTodoAlarm();

        //--- get active planner alarms ---//
        List<TodoReminder> reminders = getAllPlannerAlarm();

        //-- initialize alarm manager ---//
        alarmManager = (AlarmManager) cn.getSystemService(cn.ALARM_SERVICE);

        //--- set all to-do alarm one by one ---//
        for (TodoItem todo : todos) {
            setAlarm(todo.getId(), "To-do Alert",todo.getAlarm_req_code(), todo.getTask(),
                    todo.getRemindDate(), todo.getRepeatEnabled(), todo.getRepeatType());
        }

        //--- set all planner alarm one by one ---//
        for(TodoReminder reminder : reminders){
            setAlarm(reminder.getId(), "Planner Alert" ,reminder.getAlarm_req_code(), "Planner",
                    reminder.getRemindDate(), reminder.getRepeatEnabled(), reminder.getRepeatType());
        }

        closeRealm();
    }

    private void setAlarm(String id, String title, int req_code, String task, Date alarmTime,
                          boolean repeat_enabled, String repeat_type) {
        Log.d(TAG, "createAlarm: seting your alarm... ");
        Log.d(TAG, "createAlarm: _id: " + id);

        Intent intent = new Intent(cn, AlarmReceiver.class);

        Bundle bundle = new Bundle();
        bundle.putInt(NOTIFICATION_ID, req_code);
        bundle.putString(TODO_ID, id);
        bundle.putString(TITLE, title);
        bundle.putString(DESCRIPTION, task);
        bundle.putString(ALARM, "on");
        intent.putExtras(bundle);

        alarmPendingIntent = PendingIntent.getBroadcast(cn, req_code,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the alarm
        Calendar calendar = Calendar.getInstance();
        // calendar.setTime(countdownExpireDate);
        calendar.setTimeInMillis(alarmTime.getTime());
        Log.d(TAG, "createAlarm: Alarm Time: " + calendar.get(Calendar.HOUR_OF_DAY) +
                ":" + calendar.get(Calendar.MINUTE) +
                ":" + calendar.get(Calendar.SECOND));

        //====== for repeat alarm ====//
        if (repeat_enabled) {
            switch (repeat_type) {
                case REPEAT_DAILY:
                    Log.d(TAG, "setAlarm: set daily repeating alarm");
                    //1000 * 60 * 60 * 24
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            1000 * 60 * 60 * 24, alarmPendingIntent);

                    break;
                case REPEAT_WEEKLY:
                    Log.d(TAG, "setAlarm: set weekly  repeating alarm");
                    //1000 * 60 * 60 * 24 * 7
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
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
        } else {
            //===for onetime alarm ===//
            Log.d(TAG, "createAlarm: set one time alarm at " + calendar.getTimeInMillis());

            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(), alarmPendingIntent);
            } else if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmPendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmPendingIntent);
            }

//            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                    alarmPendingIntent);
        }
    }

    private List<TodoReminder> getAllPlannerAlarm() {
        return realm.where(TodoReminder.class)
                .greaterThanOrEqualTo(TodoReminderFields.REMIND_DATE, new Date(System.currentTimeMillis()))
                .equalTo(TodoReminderFields.REMINDER_ENABLED, true)
                .sort(TodoReminderFields.REMIND_DATE, Sort.ASCENDING)
                .findAll();
    }

    private List<TodoItem> getAllTodoAlarm() {
        return realm.where(com.cruxbd.master_planner_pro.model.realm_model.TodoItem.class)
                .greaterThanOrEqualTo(TodoItemFields.REMIND_DATE, new Date(System.currentTimeMillis()))
                .equalTo(TodoItemFields.ALARM_STATUS, true)
                .sort(TodoItemFields.REMIND_DATE, Sort.ASCENDING)
                .findAll();
    }

    private void openRealm() {
        realm = Realm.getDefaultInstance();
    }

    private void closeRealm() {
        realm.close();
    }


    public void initializeTodoAlarm(){
        openRealm();

        List<TodoItem> todos = getAllTodoAlarm();
        alarmManager = (AlarmManager) cn.getSystemService(cn.ALARM_SERVICE);
        for (TodoItem todo : todos) {
            setAlarm(todo.getId(), "To-do Alert",todo.getAlarm_req_code(), todo.getTask(),
                    todo.getRemindDate(), todo.getRepeatEnabled(), todo.getRepeatType());
        }
        closeRealm();
        Log.d(TAG, "initializeTodoAlarm: ");
    }

    public void initializePlannerAlarm(){
        openRealm();
        List<TodoReminder> reminders = getAllPlannerAlarm();
        alarmManager = (AlarmManager) cn.getSystemService(cn.ALARM_SERVICE);
        for(TodoReminder reminder : reminders){
            setAlarm(reminder.getId(), "Planner Alert" ,reminder.getAlarm_req_code(), "Planner",
                    reminder.getRemindDate(), reminder.getRepeatEnabled(), reminder.getRepeatType());
        }
        closeRealm();
        Log.d(TAG, "initializePlannerAlarm: ");
    }

    public void activateTodoAlarm(com.cruxbd.master_planner_pro.models.TodoItem todo) {
        openRealm();
        Log.d(TAG, "activateTodoAlarm: ");

        alarmManager = (AlarmManager) cn.getSystemService(cn.ALARM_SERVICE);

        setAlarm(todo.getId(), "To-do Alert",todo.getAlarm_req_code(), todo.getTask(),
                todo.getRemindDate(), todo.getRepeatEnabled(), todo.getRepeatType());

        realm.executeTransaction(realm1 -> Objects.requireNonNull(realm1.where(TodoItem.class)
                .equalTo(TodoItemFields.ID, todo.getId()).findFirst()).setAlarm_status(true));

        closeRealm();

    }


    public void cancelTodoAlarm(int req_code) {
        openRealm();
        Log.d(TAG, "cancelTodoAlarm: ");
        alarmManager = (AlarmManager) cn.getSystemService(cn.ALARM_SERVICE);
        Intent intent = new Intent(cn, AlarmReceiver.class);
        alarmPendingIntent = PendingIntent.getBroadcast(cn, req_code,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(alarmPendingIntent);

        realm.executeTransaction(realm1 -> Objects.requireNonNull(realm1.where(TodoItem.class)
                .equalTo(TodoItemFields.ALARM_REQ_CODE, req_code).findFirst()).setAlarm_status(false));

        closeRealm();
    }

    public void cancelAllTodoAlarm(){
        Log.d(TAG, "cancelAllTodoAlarm: ");
        //Todo::**** 1. If the alarm has been set, cancel it.
        openRealm();
        //--- get active to-do alarms ---//
        List<TodoItem> todos = getAllTodoAlarm();


        alarmManager = (AlarmManager) cn.getSystemService(cn.ALARM_SERVICE);

        //--- cancel all to-do alarm one by one ---//
        for (TodoItem todo : todos) {
            cancelTodoAlarm(todo.getAlarm_req_code());
        }

        closeRealm();
    }

    public void cancelAllPlannerAlarm(){
        openRealm();
        //--- get active planner alarms ---//
        List<TodoReminder> reminders = getAllPlannerAlarm();

        alarmManager = (AlarmManager) cn.getSystemService(cn.ALARM_SERVICE);

        //--- cancel all planner alarm one by one ---//
        for(TodoReminder reminder : reminders){
            cancelPlannerAlarm(reminder.getAlarm_req_code());
        }
        closeRealm();
    }


    public void cancelPlannerAlarm(int req_code){
        alarmManager = (AlarmManager) cn.getSystemService(cn.ALARM_SERVICE);
        Intent intent = new Intent(cn, AlarmReceiver.class);

        alarmPendingIntent = PendingIntent.getBroadcast(cn, req_code,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(alarmPendingIntent);

//        realm.executeTransaction(realm1 -> Objects.requireNonNull(realm1.where(TodoReminder.class)
//                .equalTo(TodoReminderFields.ID, req_code)
//                .findFirst()).setAlarm_status(false));
    }

    public void enableBootReceiver(){
        ComponentName receiver = new ComponentName(cn, BootReceiver.class);
        PackageManager pm = cn.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void disableBootReceiver(){
        ComponentName receiver = new ComponentName(cn, BootReceiver.class);
        PackageManager pm = cn.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}

