package com.cruxbd.master_planner_pro.di.alarm_cancel_activity_feature.module;

import com.cruxbd.master_planner_pro.view.activities.AlarmCancelActivity;

import dagger.Module;

@Module
public class AlarmCancelActivityModule {
    private final AlarmCancelActivity alarmCancelActivity;
    public AlarmCancelActivityModule(AlarmCancelActivity alarmCancelActivity){
        this.alarmCancelActivity = alarmCancelActivity;
    }
}
