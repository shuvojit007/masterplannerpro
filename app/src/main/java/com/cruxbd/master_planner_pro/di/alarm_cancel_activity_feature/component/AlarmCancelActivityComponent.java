package com.cruxbd.master_planner_pro.di.alarm_cancel_activity_feature.component;

import com.cruxbd.master_planner_pro.di.AppComponent;
import com.cruxbd.master_planner_pro.di.alarm_cancel_activity_feature.module.AlarmCancelActivityModule;
import com.cruxbd.master_planner_pro.di.alarm_cancel_activity_feature.scope.AlarmCancelActivityScope;
import com.cruxbd.master_planner_pro.view.activities.AlarmCancelActivity;

import dagger.Component;

@Component (modules = AlarmCancelActivityModule.class, dependencies = AppComponent.class)
@AlarmCancelActivityScope
public interface AlarmCancelActivityComponent {
    void injectAlarmCancelActivity(AlarmCancelActivity alarmCancelActivity);
}
