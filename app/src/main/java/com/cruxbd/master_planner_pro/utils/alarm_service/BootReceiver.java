package com.cruxbd.master_planner_pro.utils.alarm_service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver{
    private static final String TAG = "MyBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // initialize all alarm...
            AlarmManagerUtil alarmManagerUtil = new AlarmManagerUtil(context);
            alarmManagerUtil.initializeAlarm();
        }
    }
}
