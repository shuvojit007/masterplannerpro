package com.cruxbd.master_planner_pro.di.countdown_set_activity_feature.module;

import com.cruxbd.master_planner_pro.view.activities.CountdownSetActivity;

import dagger.Module;

@Module
public class CountdownSetActivityModule {
    private final CountdownSetActivity countdownSetActivity;
    public CountdownSetActivityModule(CountdownSetActivity countdownSetActivity){
        this.countdownSetActivity = countdownSetActivity;
    }
}
